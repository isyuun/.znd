/*
 *  BSLMidParser.c
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParser.c
	@brief
	MIDI File入力制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLFile.h"
#include "BSLMem.h"
#include "BSLMid.h"
#include "BSLMidParser.h"

/* defines */

#define _MIDI_CLOCK_TPQN 24 /* midi clock [TPQN]  */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
BSLErr _bslMidParserRewind
	(
	BSLMidParser *obj,
	DWORD startReadtick
	)
{
	DWORD readtick;
	BSLMidEvent event;
	int i;
	BSL_MID_EVENT_PROC proc;
	BSLErr err = BSL_OK;

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	BSLMidEvent midiClkEvent;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	obj->dwStartReadtick = startReadtick;

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	obj->dwMidiclockReadtick = 0UL;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	obj->nRemainedTracks = obj->nPlayedTracks;

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	midiClkEvent.dwTick= 0UL;
#ifdef BSL_MID_INCLUDE_TRACK_OPTION
	midiClkEvent.nOptSw = BSL_MID_TRACK_OPTION_NORMAL;
#endif /* BSL_MID_INCLUDE_TRACK_OPTION */
	midiClkEvent.byPort = BSL_MID_PORT_A;
	midiClkEvent.byStatus = BSL_MID_STATUS_MIDI_CLOCK;
	midiClkEvent.nSize = 0;
	midiClkEvent.pData = NULL;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	/* change start position */
//	if (obj->dwStartReadtick != startReadtick) 
	{
		obj->nRemainedTracks = obj->nPlayedTracks;

		/* event chasing (c) motu */
		while (1) {
			/* search next readtick */
			readtick = (DWORD) BSL_MID_READTICK_MAX;
			for (i = 0; i < obj->nPlayedTracks; i++) {
				BSLMidParserTrack *trk = obj->getTrack (obj, i);
				if (trk->getTick (trk) < readtick && !trk->isFinished (trk)) {
					readtick = trk->getTick (trk);
				}
			}
			if (readtick >= obj->dwStartReadtick) {
				break;
			}

#ifdef BSL_MID_INCLUDE_MIDICLOCK
			/* generate midi clock */
			while (obj->dwMidiclockReadtick <= readtick) {
				midiClkEvent.dwTick = obj->dwMidiclockReadtick;
				proc = obj->chaseEvent (obj, &midiClkEvent, obj->pCallbackUser);
				switch (proc) {
				case BSL_MID_EVENT_PROC_OK:
					obj->dwMidiclockReadtick += ((DWORD) obj->nDivision / (DWORD) _MIDI_CLOCK_TPQN);
					break;
				default:
					return BSL_ERR_UNDEFINED;
				}
			}
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

			/* search midi events on avobe readtime */
			for (i = 0; i < obj->nPlayedTracks; i++) {
				BSLMidParserTrack *trk = obj->getTrack (obj, i);
				while (!trk->isFinished (trk) && trk->getTick (trk) == readtick) {
					if ((err = trk->readEvent (trk, &event)) != BSL_OK) {
						return err;
					}

					{
						/* end of track */
						if (event.byStatus == BSL_MID_META_TYPE_END) {
							if (obj->closeTrack (obj, i)) {
								return BSL_ERR_UNDEFINED;
							}
							break;
						}
						/* callback */
						proc = obj->chaseEvent (obj, &event, obj->pCallbackUser);
						switch (proc) {
						case BSL_MID_EVENT_PROC_OK:
							if ((err = trk->incEvent (trk, &event)) != BSL_OK) {
								return err;
							}
							break;
						default:
							return BSL_ERR_UNDEFINED;
						}
					}
				}
			}
		}
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLFile *_getFile
	(
	BSLMidParser *obj
	)
{
	return obj->pFile;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_FILE_TYPE _getType
	(
	BSLMidParser *obj
	)
{
	return obj->nType;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_FILE_FORMAT _getFormat
	(
	BSLMidParser *obj
	)
{
	return obj->nFormat;
}

/*---------------------------------------------------------------------------*/
static void _setDivision
	(
	BSLMidParser *obj,
	int division,
	int clocksPerBeat
	)
{
	obj->nDivision = division;
}

/*---------------------------------------------------------------------------*/
static int _getDivision
	(
	BSLMidParser *obj
	)
{
	return obj->nDivision;
}

/*---------------------------------------------------------------------------*/
static int _getTracks
	(
	BSLMidParser *obj
	)
{
	return obj->nTracks;
}

/*---------------------------------------------------------------------------*/
static BSLMidParserTrack *_getTrack
	(
	BSLMidParser *obj,
	int index
	)
{
	if (0 <= index && index < BSL_MID_TRACKS) {
		return &obj->tTrack[index];
	}
	else {
		return NULL;
	}
}

/*---------------------------------------------------------------------------*/
static BOOL _closeTrack
	(
	BSLMidParser *obj,
	int index
	)
{
	BSLMidParserTrack *trk = obj->getTrack (obj, index);
	if (trk) {
		trk->close (trk);
		if (obj->nRemainedTracks > 0) {
			obj->nRemainedTracks--;
		}
		if (obj->nRemainedTracks == 0) {
			return TRUE;
		}
	}

	return FALSE;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC _sortEvent
	(
	BSLMidParser *obj,
	DWORD startReadtick,
	void *user
	)
{
	DWORD readtick;
	BSLMidEvent event;
	int i;
	BSLErr err;
	BSL_MID_EVENT_PROC proc;

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	BSLMidEvent midiClkEvent;
	midiClkEvent.dwTick= 0UL;
#ifdef BSL_MID_INCLUDE_TRACK_OPTION
	midiClkEvent.nOptSw = BSL_MID_TRACK_OPTION_NORMAL;
#endif /* BSL_MID_INCLUDE_TRACK_OPTION */
	midiClkEvent.byPort = BSL_MID_PORT_A;
	midiClkEvent.byStatus = BSL_MID_STATUS_MIDI_CLOCK;
	midiClkEvent.nSize = 0;
	midiClkEvent.pData = NULL;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	while (1) {
		/*************
		 event get
		*************/
		/* search next readtick */
		readtick = (DWORD) BSL_MID_READTICK_MAX;
		for (i = 0; i < obj->nPlayedTracks; i++) {
			BSLMidParserTrack *trk = obj->getTrack (obj, i);
			if (trk->getTick (trk) < readtick && !trk->isFinished (trk)) {
				readtick = trk->getTick (trk);
			}
		}

#ifdef BSL_DEBUG
/*		bslTrace (_TEXT("readtick = %08lX\n"), readtick); */
#endif /* BSL_DEBUG */

#ifdef BSL_MID_INCLUDE_MIDICLOCK
		/* generate midi clock */
		while (obj->dwMidiclockReadtick <= readtick) {
			midiClkEvent.dwTick = obj->dwMidiclockReadtick;
			proc = obj->getEvent (obj, &midiClkEvent, user);
			switch (proc) {
			case BSL_MID_EVENT_PROC_OK:
				obj->dwMidiclockReadtick += ((DWORD) obj->nDivision / (DWORD) _MIDI_CLOCK_TPQN);
				break;
			default:
				return proc;
			}
		}
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

		/* search and save midi events on readtime */
		for (i = 0; i < obj->nPlayedTracks; i++) {
			BSLMidParserTrack *trk = obj->getTrack (obj, i);
			while (!trk->isFinished (trk) && trk->getTick (trk) == readtick) {
				if ((err = trk->readEvent (trk, &event)) != BSL_OK) {
					return BSL_MID_EVENT_PROC_ERROR;
				}

				{
					/* end of track */
					if (event.byStatus == BSL_MID_META_TYPE_END) {
						if (obj->closeTrack (obj, i)) {
							return BSL_MID_EVENT_PROC_FINISHED;
						}
						break;
					}
					proc = obj->getEvent (obj, &event, user);
					switch (proc) {
					case BSL_MID_EVENT_PROC_OK:
						if ((err = trk->incEvent (trk, &event)) != BSL_OK) {
							return BSL_MID_EVENT_PROC_ERROR;
						}
						break;
					default:
						return proc;
					}
				}
			}
		}
	}
	
	return BSL_MID_EVENT_PROC_ERROR;
}

#pragma mark -
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
/*---------------------------------------------------------------------------*/
static char *_getDecoded (BSLMidParser *obj)
{
	return obj->pDecoded;
}

/*---------------------------------------------------------------------------*/
static DWORD _getDecodedSize (BSLMidParser *obj)
{
	return obj->dwDecodedSize;
}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	BSLMidParser基本制御オブジェクトの構築を行う
	@param obj
	制御オブジェクトのアドレス
*/
void bslMidParser
	(
	BSLMidParser *obj
	)
{
	memset (obj, 0, sizeof (BSLMidParser));

	obj->nType = BSL_MID_FILE_TYPE_NONE;

	obj->rewind = _bslMidParserRewind;

	obj->sortEvent = _sortEvent;

	obj->getType = _getType;
	obj->getFormat = _getFormat;
	obj->getDivision = _getDivision;
	obj->setDivision = _setDivision;
	obj->getTracks = _getTracks;

	obj->getTrack = _getTrack;
	obj->closeTrack = _closeTrack;

	obj->getFile = _getFile;

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	obj->getDecoded = _getDecoded;
	obj->getDecodedSize = _getDecodedSize;
	obj->pDecoded = NULL;
	obj->dwDecodedSize = 0UL;
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */
	obj->pInternalData = NULL;

/*
	継承オブジェクトで行うこと
	for (i = 0; i < BSL_MID_TRACKS; i++) {
		bslMidParserTrack (&obj->track[i]);
	}
*/
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	BSLMidParser基本制御オブジェクトの破棄を行う
	@param obj
	制御オブジェクトのアドレス
*/
void bslMidParserExit
	(
	BSLMidParser *obj
	)
{
	int i;

	for (i = 0; i < BSL_MID_TRACKS; i++) {
		bslMidParserTrackExit (&obj->tTrack[i]);
	}

	if (obj->finalize) {
		obj->finalize (obj);
	}

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	if (obj->pDecoded) {
		bslMemFree (obj->pDecoded);
		obj->pDecoded = NULL;
		obj->dwDecodedSize = 0UL;
	}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

	if (obj->pInternalData) {
		bslMemFree (obj->pInternalData);
		obj->pInternalData = NULL;
	}
}

#pragma mark -

typedef struct
{
	DWORD dwTempo;
	DWORD dwTick;
	DWORD dwPlaytickTP;
	float fPlayTime;
	float fPlayTimeTP;
	float fTickTime;
} _INFO;

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC _getEvent
	(
	BSLMidParser *obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	_INFO *info = (_INFO *) user;

	info->dwTick = event->dwTick;
	info->fPlayTime = info->fPlayTimeTP + (info->fTickTime * (info->dwTick - info->dwPlaytickTP));

	switch (event->byStatus) {
	case BSL_MID_META_TYPE_SET_TEMPO:
		{
			BYTE *data = event->pData;
			DWORD tempo;
			tempo = 0x10000UL * (DWORD) data[0];
			tempo += 0x100UL * (DWORD) data[1];
			tempo += (DWORD) data[2];

			info->fPlayTimeTP += (info->fTickTime * (event->dwTick - info->dwPlaytickTP));
			info->dwPlaytickTP = event->dwTick;
			info->dwTempo = tempo;
			info->fTickTime = (float) info->dwTempo / ((float) obj->getDivision (obj) * 1000000.f); /* [s] */
		}
		break;
	case BSL_MID_META_TYPE_TIME_SIGNATURE:
		break;
	}

	return BSL_MID_EVENT_PROC_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	MIDIファイルの情報を取得する
	@param obj
	制御オブジェクトのアドレス
	@param totaltick
	総Tick数取得バッファ
	@param totaltime
	総演奏時間[s]取得バッファ
*/
BSLErr bslMidParserGetInfo
	(
	BSLMidParser *obj, 
	DWORD *totaltick, 
	DWORD *totaltime
	)
{
	_INFO info;
	BSL_MID_EVENT_PROC (*ce) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->chaseEvent;
	BSL_MID_EVENT_PROC (*ge) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->getEvent;
	BSLErr err = BSL_OK;

	memset (&info, 0, sizeof (_INFO));
	info.dwTempo = 500000UL;
	info.dwTick = 0UL;
	info.dwPlaytickTP = 0UL;
	info.fPlayTime = 0.f;
	info.fPlayTimeTP = 0.f;
	info.fTickTime = (float) info.dwTempo / ((float) obj->getDivision (obj) * 1000000.f); /* [s] */

	obj->chaseEvent = NULL;
	obj->getEvent = _getEvent;

	obj->rewind (obj, 0UL);

	if (obj->sortEvent (obj, 0UL, (void *) &info) != BSL_MID_EVENT_PROC_FINISHED) {
		memset (&info, 0, sizeof (_INFO));
		err = BSL_ERR_UNDEFINED;
	}

	if (totaltick) {
		*totaltick = info.dwTick;
	}

	if (totaltime) {
		*totaltime = (DWORD) info.fPlayTime;
	}

	obj->chaseEvent = ce;
	obj->getEvent = ge;

	return err;
}

