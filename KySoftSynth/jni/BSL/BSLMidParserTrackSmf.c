/*
 *  BSLMidParserTrackSmf.c
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserTrackSmf.c
	@brief
	MIDI File Track制御オブジェクト(SMF)
*/

/* includes */

#include "BSL.h"
#include "BSLMid.h"
#include "BSLMidParser.h"
#include "BSLMidParserTrackSmf.h"
#ifdef BSL_MID_INCLUDE_TRACK_NAME
#include "BSLMem.h"
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */

/*---------------------------------------------------------------------------*/
static BSLErr _readVarLen
	(
	BSLMidParserTrack *obj,
	DWORD *value
	)
{
	DWORD dwValue = 0UL;
	DWORD i = 0UL;

	for (i = 1UL; i <= 4UL; i++) {
		BYTE data;
		BSLFile *file = obj->getFile (obj);
		BSLErr err = file->read (file, &data, 1);
		if (err != BSL_OK) {
			return err;
		}
		dwValue <<= 7;
		if (data & 0x80) {
			dwValue += (DWORD) (data & 0x7F);
		}
		else {
			dwValue += (DWORD) data;
			*value = dwValue;
			return BSL_OK;
		}
	}

	return BSL_ERR_FILE_READ;
}

/*---------------------------------------------------------------------------*/
static BSLErr _readStatus
	(
	BSLMidParserTrack *obj, 
	BYTE *status
	)
{
	BYTE _status;
	BSLFile *file = obj->getFile (obj);
	BSLErr err = BSL_OK;

	if ((err = file->read (file, &_status, 1)) != BSL_OK) {
		return err;
	}

	if (_status < 0x80) {
		if (BSL_MID_STATUS_NOTE_OFF <= obj->byRunning && obj->byRunning < BSL_MID_STATUS_SYX) {
			_status = obj->byRunning;
			if ((err = file->seek (file, -1L, BSL_FILE_SEEK_CURRENT)) != BSL_OK) {
				return err;
			}
		}
		else {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: can't read smf status byte\n"));
#endif /* BSL_DEBUG */
			return BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}
	*status = _status;

	return BSL_OK;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSLErr _initialize
	(
	BSLMidParserTrack *obj
	)
{
	obj->ulPosition = 0UL;
	obj->ulStart = 0UL;

	obj->byRunning = BSL_MID_STATUS_NULL;

	return _bslMidParserTrackInitialize (obj);
}

/*---------------------------------------------------------------------------*/
static BSLErr _parse
	(
	BSLMidParserTrack *obj, 
	void *_smf,
	unsigned long offset,
	unsigned long size
	)
{
	BYTE end[3];
	BSLErr err = BSL_OK;

	BSLMidParser *smf = (BSLMidParser *) _smf;
	BSLFile *file = obj->pFile = smf->getFile (smf);


	if (err == BSL_OK) {
		obj->ulStart = offset;
		obj->ulSize = size;
	}

	/* check to end of track */
	if (err == BSL_OK) {
		err = file->seek (file, (long) size - 3L, BSL_FILE_SEEK_CURRENT);
	}

	if (err == BSL_OK) {
		err = file->read (file, &end, 3);
	}

	if (err == BSL_OK) {
		if (end[0] != BSL_MID_STATUS_META || end[1] != BSL_MID_META_TYPE_END || end[2] != 0) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: illegal end of track\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}

#ifdef BSL_MID_INCLUDE_TRACK_NAME
	/* seach track name */
	if (err == BSL_OK) {
		/* clear */
		if (obj->pName) {
			bslMemFree (obj->pName);
			obj->pName = NULL;
		}
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			/* 0Tickになければ探さない */
			if (0UL < obj->getTick (obj)) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == BSL_MID_META_TYPE_TEXT || event.byStatus == BSL_MID_META_TYPE_NAME) {
				if (event.nSize) {
#ifdef BSL_UNICODE
	#if BSL_WINDOWS
					int length;
					char *name = (char *) bslMemAlloc (event.nSize + 1);
					if (!name) {
						err = BSL_ERR_MEM_ALLOC;
						break;
					}
					memset (name, 0, event.nSize + 1);
					memcpy (name, (char *) event.pData, event.nSize);
					length = MultiByteToWideChar (CP_ACP, 0, name, (int) strlen (name), NULL, 0);
					if (length <= 0) {
						bslMemFree (name);
						break;
					}
					obj->pName = bslMemAlloc (sizeof (WCHAR) * (length + 1));
					if (!obj->pName) {
						err = BSL_ERR_MEM_ALLOC;
						break;
					}
					memset (obj->pName, 0, sizeof (WCHAR) * (length + 1));
					MultiByteToWideChar (CP_ACP, 0, name, (int) strlen (name), obj->pName, length + 1);
					bslMemFree (name);
	#endif /* BSL_WINDOWS */
#else /* BSL_UNICODE */
					obj->pName = (LPTSTR) bslMemAlloc (event.nSize + 1);
					if (!obj->pName) {
						err = BSL_ERR_MEM_ALLOC;
						break;
					}
					 _tcsncpy_(obj->pName, (TCHAR *) event.pData, event.nSize);
#endif /* BSL_UNICODE */
					 break;
				}
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}

	}
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _readEvent
	(
	BSLMidParserTrack *obj, 
	BSLMidEvent *event
	)
{
	BYTE status;
	DWORD length;
	BSLFile *file = obj->getFile (obj);
	BSLErr err;

	if ((err = file->seek (file, (long) obj->ulPosition, BSL_FILE_SEEK_START)) != BSL_OK) {
		return err;
	}

	if ((err = _readStatus (obj, &status)) != BSL_OK) {
		return err;
	}
	event->dwTick = obj->dwTick;
	event->byPort = obj->nPort;
	event->byStatus = status;

	if (status == BSL_MID_STATUS_META) {
		/* type */
		if ((err = file->read (file, &event->byStatus, 1)) != BSL_OK) {
			return err;
		}
		if (0x80 <= event->byStatus) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: illegal meta event type\n"));
#endif /* BSL_DEBUG */
			return BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
		/* size */
		if ((err = _readVarLen (obj, &length)) != BSL_OK) {
			return err;
		}
		event->nSize = (int) length;
	}
	else if (status == BSL_MID_STATUS_SYX || status == BSL_MID_STATUS_SYX_END) {
		/* size */
		if ((err = _readVarLen (obj, &length)) != BSL_OK) {
			return err;
		}
		event->nSize = (int) length;
	}
	else if (BSL_MID_STATUS_NOTE_OFF <= status && status < BSL_MID_STATUS_SYX) {
		if ((status & 0xE0) == 0xC0) {
			event->nSize = 1;
		}
		else {
			event->nSize = 2;
		}
	}
	else {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-MID: smf unknown status byte\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_FILE_UNKNOWN_STRUCTURE;
	}

	event->pData = (BYTE *) file->getAddress (file) + file->getPos (file);
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static BSLErr _incEvent
	(
	BSLMidParserTrack *obj, 
	BSLMidEvent *event
	)
{
	DWORD length;
	BSLFile *file = obj->getFile (obj);
	BSLErr err;

	if ((err = file->seek (file, (long) event->nSize, BSL_FILE_SEEK_CURRENT)) != BSL_OK) {
		return err;
	}

	/* set running status */
	if (BSL_MID_STATUS_NOTE_OFF <= event->byStatus && event->byStatus < BSL_MID_STATUS_SYX) {
		obj->byRunning = event->byStatus;
	}
	else if (event->byStatus < 0x80) { /* meta event */
	}
	else {
		obj->byRunning = BSL_MID_STATUS_NULL;
	}

	/* increment event */
	if ((err = _readVarLen (obj, &length)) != BSL_OK) {
		return err;
	}
	obj->dwTick += length;
	obj->ulPosition = file->getPos (file);
	
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static BSLErr _rewind
	(
	BSLMidParserTrack *obj
	)
{
	BSLFile *file = obj->getFile (obj);
	BSLErr err;

	if ((err = file->seek (file, obj->ulPosition = obj->ulStart, BSL_FILE_SEEK_START)) != BSL_OK) {
		return err;
	}

	if ((err = _readVarLen (obj, &obj->dwTick)) != BSL_OK) {
		return err;
	}

	obj->ulPosition = file->getPos (file);
	obj->byRunning = BSL_MID_STATUS_NULL;

	return _bslMidParserTrackRewind (obj);
}

/*---------------------------------------------------------------------------*/
void bslMidParserTrackSmf
	(
	BSLMidParserTrack *obj
	)
{
	bslMidParserTrack (obj);

	obj->initialize = _initialize;
	obj->parse = _parse;

	obj->rewind = _rewind;
	obj->readEvent = _readEvent;
	obj->incEvent = _incEvent;
}

