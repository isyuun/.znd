/*
 *  BSLMidParserTrack.c
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserTrackSmf.c
	@brief
	MIDI File Track制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLMem.h"
#include "BSLMid.h"
#include "BSLMidParserTrack.h"
#include "BSLMidParser.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */

/*---------------------------------------------------------------------------*/
BSLErr _bslMidParserTrackInitialize
	(
	BSLMidParserTrack *obj
	)
{
	obj->dwTick = BSL_MID_READTICK_MAX;

	obj->bIsFinished = FALSE;
	obj->ulSize = 0UL;

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static DWORD _getTick
	(
	BSLMidParserTrack *obj
	)
{
	return obj->dwTick;
}

/*---------------------------------------------------------------------------*/
BSLErr _bslMidParserTrackRewind
	(
	BSLMidParserTrack *obj
	)
{
	obj->bIsFinished = FALSE;

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static void _close
	(
	BSLMidParserTrack *obj
	)
{
	obj->bIsFinished = TRUE;
}

/*---------------------------------------------------------------------------*/
static BOOL _isFinished
	(
	BSLMidParserTrack *obj
	)
{
	return obj->bIsFinished;
}

/*---------------------------------------------------------------------------*/
static BSLFile *_getFile
	(
	BSLMidParserTrack *obj
	)
{
	return obj->pFile;
}

/*---------------------------------------------------------------------------*/
static void _setPort
	(
	BSLMidParserTrack *obj,
	BSL_MID_PORT port
	)
{
	obj->nPort = port;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_PORT _getPort
	(
	BSLMidParserTrack *obj
	)
{
	return obj->nPort;
}

#ifdef BSL_MID_INCLUDE_TRACK_NAME
/*---------------------------------------------------------------------------*/
static LPCTSTR _getName
	(
	BSLMidParserTrack *obj
	)
{
	return (LPCTSTR) obj->pName;
}
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

#pragma mark -
/*---------------------------------------------------------------------------*/
void bslMidParserTrack
	(
	BSLMidParserTrack *obj
	)
{
	memset (obj, 0, sizeof (BSLMidParserTrack));

	obj->initialize = _bslMidParserTrackInitialize;
/*
	obj->parse = 

	obj->readEvent = 
	obj->incEvent = 	
*/
	obj->getTick = _getTick;

	obj->rewind = _bslMidParserTrackRewind;
	obj->close = _close;
	obj->isFinished = _isFinished;

	obj->getFile = _getFile;
	obj->setPort = _setPort;
	obj->getPort = _getPort;
#ifdef BSL_MID_INCLUDE_TRACK_NAME
	obj->getName = _getName;
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

	obj->pInternalData = NULL;
}

/*---------------------------------------------------------------------------*/
void bslMidParserTrackExit
	(
	BSLMidParserTrack *obj
	)
{
#ifdef BSL_MID_INCLUDE_TRACK_NAME
	if (obj->pName) {
		bslMemFree (obj->pName);
		obj->pName = NULL;
	}
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

	if (obj->finalize) {
		obj->finalize (obj);
	}

	if (obj->pInternalData) {
		bslMemFree (obj->pInternalData);
		obj->pInternalData = NULL;
	}
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	Track内で使用されているMIDIチャンネルを取得する
	@param obj
	-1 : 検出不可、あるいは複数チャンネル使用、0 ~ 15 : 使用されているチャンネル
*/
int bslMidParserTrackGetUsedChannel
	(
	BSLMidParserTrack *obj
	)
{
	BSLErr err = BSL_OK;
	int channel = -1;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (0x80 <= event.byStatus && event.byStatus < 0xF0) {
				int c = (int) (event.byStatus & 0x0F);

				if (channel < 0) {
					/* Channel未設定 */
					channel = c;
				}
				else {
					/* Channel設定あり */
					if (channel != c) {
						/* 複数Channel使用 */
						channel = -1;
						break;
					}
				}
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}
	}

	return channel;
}

