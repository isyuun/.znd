/* mMidReaderSmfTrk.cpp - smf track class */

/* */

/* 
modification history
--------------------
*/

/*
DESCRIPTION
*/

/* includes */

#include "mDefine.h"
#include "mError.h"
#include "mDebug.h"
#include "mMidDef.h"
#include "mMid.h"
#include "mMidReaderSmfTrk.h"
#include "mMidReaderSmf.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MMidReaderSmfTrk::MMidReaderSmfTrk - 
*
* RETURNS:
* void
*/
MMidReaderSmfTrk::MMidReaderSmfTrk
	(
	MMidReaderBase *parent
	) : MMidReaderBaseTrk (parent)
{
}

/*******************************************************************************
*
* MMidReaderSmfTrk::~MMidReaderSmfTrk - 
*
* RETURNS:
* void
*/
MMidReaderSmfTrk::~MMidReaderSmfTrk ()
{
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmfTrk::initialize - 
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::initialize (void)
{
	dwPosition = 0UL;
	dwPositionStart = 0UL;

	byRunning = kMidStatusNull;

#ifdef M_MID_INCLUDE_LOOPPLAY
	dwPositionAtLoopStart = NULL;
	dwReadtickAtLoopStart = (DWORD) kMidSmfReadtickMax;
	byRunningAtLoopStart = kMidStatusNull;
#endif // M_MID_INCLUDE_LOOPPLAY

	return MMidReaderBaseTrk::initialize ();
}

/*******************************************************************************
*
* MidSmfReader::ready - 
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::ready
	(
	int trk, // original track number
	DWORD chunkSize
	)
{
	int err = OK;

	if (err == OK) {
		err = MMidReaderBaseTrk::ready (trk, chunkSize);
	}

	// set track parameters
	if (err == OK) {
		dwPosition = dwPositionStart = pParent->readposition ();
		if (varLenRead (&dwReadtick) == 0UL) {
			pParent->error (kErrSmfVarLenRead, &trk);
			err = kErrSmfVarLenRead;
		}
	}

	if (err == OK) {
#ifdef M_MID_INCLUDE_LOOPPLAY
		loopStartSet ();
#endif // M_MID_INCLUDE_LOOPPLAY
		byRunning = kMidStatusNull;
		bIsFinished = false;
	}

	// check end of track chunk, and get trackname
	if (err == OK) {
		err = property ();
	}

	// move pointer to top address of next track chunk
	if (err == OK) {
		err = pParent->readseek (dwPositionStart + chunkSize, MFile::kStart);
	}

	return (err);
}

/*******************************************************************************
*
* MMidReaderSmfTrk::rewind - 
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::rewind (void)
{
	int err = OK;

	if (err == OK) {
		err = pParent->readseek (dwPosition = dwPositionStart, MFile::kStart);
	}

	DWORD count = 0UL;
	if (err == OK) {
		if ((count = varLenRead (&dwReadtick)) == 0UL) {
			pParent->error (kErrSmfVarLenRead, &iOrgTrk);
			err = kErrSmfVarLenRead;
		}
	}

	if (err == OK) {
		dwPosition += count;
		byRunning = kMidStatusNull;
		err =  MMidReaderBaseTrk::rewind ();
	}

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmfTrk::eventRead - 
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::eventRead
	(
	MMidEvent **event
	)
{
	MMidEvent *e = &tEvent;
	DWORD length;
	BYTE status;
	int err;

	if ((err = pParent->readseek ((long) dwPosition, MFile::kStart)) != OK) {
		return (err);
	}

	if ((err = statusRead (&status, byRunning)) != OK) {
		return (err);
	}
	e->dwTick = dwReadtick;
	e->byPort = portGet ();
	e->byStatus = status;
	e->iOrgTrk = orgTrkNumberGet ();
#ifdef M_MID_INCLUDE_OPTSW
	e->byOptSw = optionSwitchGet ();
#endif // M_MID_INCLUDE_OPTSW
	
	if (status == kMidSmfStatusMeta) {
		// type
		if ((err = pParent->read (&e->byStatus, 1)) != OK) {
			return (err);
		}
		if (e->byStatus >= 0x80) {
			pParent->error (kErrSmfMetaType, &iOrgTrk);
			return (kErrSmfMetaType);
		}
		if (varLenRead (&length) == 0UL) {
			pParent->error (kErrSmfVarLenRead, &iOrgTrk);
			return (kErrSmfVarLenRead);
		}
		// size
		if (length >= kMidDataSize) {
			pParent->error (kErrSmfMetaSize, &iOrgTrk);
			return (kErrSmfMetaSize);
		}
		e->iSize = (int) length;
	}
	else if (status == kMidStatusSyx) {
		if (varLenRead (&length) == 0UL) {
			pParent->error (kErrSmfVarLenRead, &iOrgTrk);
			return (kErrSmfVarLenRead);
		}
		// size
		if (length >= kMidDataSize) {
			pParent->error (kErrSmfSysexSize, &iOrgTrk);
			return (kErrSmfSysexSize);
		}
		e->iSize = (int) length;
#ifdef M_MID_SMF_STRICT_ERROR_CHECK
		BYTE *x = (BYTE *) pParent->address () + pParent->readposition (); // MFileMem ONLY
		// check sysex data
		for (int i = 0; i < (e->iSize - 1); i++) {
			if (*x++ >= 0x80) {
				pParent->error (kErrSmfSysexData, &iOrgTrk);
				return (kErrSmfSysexData);
			}
		}
		// check end of sysex
		if (*x != kMidStatusSyxEnd) {
			pParent->error (kErrSmfSysexData, &iOrgTrk);
			return (kErrSmfSysexData);
		}
#endif // M_MID_SMF_STRICT_ERROR_CHECK
	}
	else if (status == kMidStatusSyxEnd) {
		if (varLenRead (&length) == 0UL) {
			pParent->error (kErrSmfVarLenRead, &iOrgTrk);
			return (kErrSmfVarLenRead);
		}
		// size
		if (length >= kMidDataSize) {
			pParent->error (kErrSmfSysexSize2, &iOrgTrk);
			return (kErrSmfSysexSize2);
		}
		e->iSize = (int) length;
#ifdef M_MID_SMF_STRICT_ERROR_CHECK
		// check sysex data
		BYTE *x = (BYTE *) pParent->address () + pParent->readposition (); // MFileMem ONLY
		for (int i = 0; i < (e->iSize - 1); i++) {
			if (*x++ >= 0x80) {
				pParent->error (kErrSmfSysexData2, &iOrgTrk);
				return (kErrSmfSysexData2);
			}
		}
#endif // M_MID_SMF_STRICT_ERROR_CHECK
	}
	else if (kMidStatusNoteOff <= status && status < kMidStatusSyx) {
		if ((status & 0xE0) == kMidStatusProgram) {
			e->iSize = 1;
		}
		else {
			e->iSize = 2;
		}
#ifdef M_MID_SMF_STRICT_ERROR_CHECK
		// check data
		BYTE *x = (BYTE *) pParent->address () + pParent->readposition (); // MFileMem ONLY
		for (int i = 0; i < e->iSize; i++) {
			if (*x++ >= 0x80) {
				pParent->error (kErrSmfChannelMsgData, &iOrgTrk);
				return (kErrSmfChannelMsgData);
			}
		}
#endif // M_MID_SMF_STRICT_ERROR_CHECK
	}
	else {
		pParent->error (kErrSmfUndefinedStatus, &iOrgTrk, &status);
		return (kErrSmfUndefinedStatus);
	}
	
	e->pData = (BYTE *) pParent->address () + pParent->readposition ();  // MFileMem ONLY

	*event = e;
	return (OK);
}

/*******************************************************************************
*
* MMidReaderSmfTrk::eventInc - 
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::eventInc
	(
	MMidEvent *event
	)
{
	int err;

	if ((err = pParent->readseek ((long) event->iSize, MFile::kCurrent)) != OK) {
		return (err);
	}

	// set running status
	if (kMidStatusNoteOff <= event->byStatus && event->byStatus < kMidStatusSyx) {
		byRunning = event->byStatus;
	}
	else if (event->byStatus < 0x80) { // meta event
	}
	else {
		byRunning = kMidStatusNull;
	}

	// increment event
	DWORD length;
	if (varLenRead (&length) == 0UL) {
		pParent->error (kErrSmfVarLenRead, &iOrgTrk);
		return (kErrSmfVarLenRead);
	}
	dwReadtick += length;
	dwPosition = pParent->readposition ();
	
	return (OK);
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmfTrk::property -
*
* RETURNS: 
* error code
*/
int MMidReaderSmfTrk::property (void)
{
	MMidReaderSmfTrk trk (pParent);
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
	BOOL first = true; // flag for trackname is first event or not
#endif // M_MID_SMF_TRACKNAME_REQUIRED
	BOOL found = false;
	int err;

	// clear trackname
	_tcscpy (cName, M_MID_TRACKNAME_DEF);

	// check end of track
	if ((err = pParent->readseek ((long) (dwPositionStart + dwChunkSize - 3UL), MFile::kStart)) != OK) {
		return (err);
	}

	BYTE end[3];
	if ((err = pParent->read (&end, 3)) != OK) {
		return (err);
	}

	if (end[0] != kMidSmfStatusMeta || end[1] != kMidSmfMetaTypeEnd || end[2] != 0) {
			pParent->error (kErrSmfNoEOT, &iOrgTrk);
		return (kErrSmfNoEOT);
	}

	trk.dwPositionStart = dwPositionStart;
	if ((err = trk.rewind ()) != OK) {
		return (err);
	}

	while (1) {
		MMidEvent *event;
		if ((err = trk.eventRead (&event)) != OK) {
			return (err);
		}
		switch (event->byStatus) {
		case kMidSmfMetaTypeText:
		case kMidSmfMetaTypeName:
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
			if (0UL < event->dwTick) {
				pParent->error (kErrSmfNoTrackName, &iOrgTrk);
				return (kErrSmfNoTrackName);
			}
#endif // M_MID_SMF_TRACKNAME_REQUIRED
			if (!found) {
				int size = event->iSize;
				if (size >= kNameLength) {
					size = kNameLength - 1;
				}
#ifdef M_UNICODE
	#if M_WINDOWS
				char name[kNameLength];
				::memcpy (name, event->pData, size); // MFileMem ONLY
				name[size] = 0;
				::MultiByteToWideChar (CP_ACP, 0, name, -1, cName, kNameLength - 1);
				cName[kNameLength - 1] = _TEXT('\0');
	#endif // M_WINDOWS
#else // M_UNICODE
				::memcpy (cName, event->pData, size); // MFileMem ONLY
				cName[size] = _TEXT('\0');
#endif // M_UNICODE
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
				if (!first) {
					pParent->error (kErrSmfNon1stTrackName, &iOrgTrk);
					return (kErrSmfNon1stTrackName);
				}
#endif // M_MID_SMF_TRACKNAME_REQUIRED
				found = true;
				// return (OK);
			}
			break;
#ifdef M_MID_INCLUDE_COPYRIGHT
		case kMidSmfMetaTypeCopyright:
			{
				int size = event->iSize;
				if (size >= MMidReaderBase::kCopyrightLength) {
					size = MMidReaderBase::kCopyrightLength - 1;
				}
				char tmp[MMidReaderBase::kCopyrightLength];
				::memcpy (tmp, event->pData, size); // MFileMem ONLY
				tmp[size] = '\0';
#ifdef M_UNICODE
	#if M_WINDOWS
				TCHAR tmp2[MMidReaderBase::kCopyrightLength];
				::MultiByteToWideChar (CP_ACP, 0, tmp, -1, tmp2, MMidReaderBase::kCopyrightLength - 1);
				tmp2[MMidReaderBase::kCopyrightLength - 1] = _TEXT('\0');
				pParent->copyrightSet (tmp2);
	#endif // M_WINDOWS
#else // M_UNICODE
				pParent->copyrightSet (tmp);
#endif // M_UNICODE
			}
			break;
#endif // M_MID_INCLUDE_COPYRIGHT
		case kMidSmfMetaTypeEnd:
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
			// trackname found ?
			if (!found) {
				pParent->error (kErrSmfNoTrackName, &iOrgTrk);
				return (kErrSmfNoTrackName);
			}
#endif // M_MID_SMF_TRACKNAME_REQUIRED
#ifdef M_MID_SMF_STRICT_ERROR_CHECK
			// incorrect eot ?
			if (
				event.pData != ((BYTE *) pParent->address () + dwPositionStart + dwChunkSize) // MFileMem ONLY is it ok ?
				|| event.iSize != 0
				) {
				pParent->error (kErrSmfNoEOT, &iOrgTrk);
				return (kErrSmfNoEOT);
			}
#endif // 
			return (OK);
		case kMidStatusSyx:
		case kMidStatusSyxEnd:
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
			first = false;
#endif // M_MID_SMF_TRACKNAME_REQUIRED
			break;
		default:
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
			if (kMidStatusNoteOff <= event->byStatus && event->byStatus < kMidStatusSyx) {
				first = false;
			}
#endif // M_MID_SMF_TRACKNAME_REQUIRED
			break;
		}

		if ((err = trk.eventInc (event)) != OK) {
			return (err);
		}
	}

	return (OK);
}

/*******************************************************************************
*
* MMidReaderSmfTrk::noteOnsCount - count note on events in track chunk
*
* RETURNS:
* error code
*/
int MMidReaderSmfTrk::noteOnsCount
	(
	WORD *pNoteOns // number of vocal track note on events
	)
{
	MMidReaderSmfTrk trk (pParent);
	int err;

	trk.dwPosition = dwPosition;
	if ((err = trk.rewind ()) != OK) {
		return (err);
	}

	while (1) {
		MMidEvent *event;
		if ((err = trk.eventRead (&event)) != OK) {
			return (err);
		}
		switch (event->byStatus) {
		case kMidSmfMetaTypeEnd:
			return (OK);
		default:
			if (kMidStatusNoteOff <= event->byStatus  && event->byStatus < kMidStatusSyx) {
				switch (event->byStatus & 0xF0) {
				case kMidStatusNoteOn:
					if (event->pData[1] != 0x00) {
						(*pNoteOns)++;
					}
					break;
				}
			}
			break;
		}

		if ((err = trk.eventInc (event)) != OK) {
			return (err);
		}
	}

	return (OK);
}

#pragma mark -
#ifdef M_MID_INCLUDE_LOOPPLAY
/*******************************************************************************
*
* MMidReaderSmfTrk::loopStartSet - 
*
* RETURNS:
* void
*/
void MMidReaderSmfTrk::loopStartSet (void)
{
	dwReadtickAtLoopStart = dwReadtick;
	dwPositionAtLoopStart = pParent->readposition ();
	byRunningAtLoopStart = byRunning;
	bIsFinishedAtLoopStart = bIsFinished;
}

/*******************************************************************************
*
* MMidReaderSmfTrk::loopEndSet -
*
* RETURNS:
* OK, or ERR if failed
*/
int MMidReaderSmfTrk::loopEndSet (void)
{
	dwReadtick = dwReadtickAtLoopStart;
	byRunning = byRunningAtLoopStart;
	bIsFinished = bIsFinishedAtLoopStart;

	return pParent->readseek ((long) dwPositionAtLoopStart, MFile::kStart);
}
#endif // M_MID_INCLUDE_LOOPPLAY

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmfTrk::varLenRead - read variable length coded data from smf
*
* RETURNS: 
* DWORD
*/
DWORD MMidReaderSmfTrk::varLenRead
	(
	DWORD *value
	)
{
	DWORD dwValue = 0UL;
	DWORD i = 0;

	for (i = 1UL; i <= 4UL; i++) {
		BYTE data;
		if (pParent->read (&data, 1) != OK) {
			return (0UL);
		}
		dwValue <<= 7;
		if (data & 0x80) {
			dwValue += (DWORD) (data & 0x7F);
		}
		else {
			dwValue += (DWORD) data;
			*value = dwValue;
			return (i);
		}
	}

	return (0UL);
}

/*******************************************************************************
*
* MMidReaderSmfTrk::statusRead -
*
* RETURNS: 
* event sort status
*/
int MMidReaderSmfTrk::statusRead
	(
	BYTE *status, // status byte
	BYTE running  // current running status byte of the trackchunk
	)
{
	int err = OK;
	BYTE _status;
	
	if (err == OK) {
		err = pParent->read (&_status, 1);
	}

	if (err == OK) {
		if (_status < kMidStatusNoteOff) {
			if (kMidStatusNoteOff <= running  && running < kMidStatusSyx) {
				_status = running;
				err = pParent->readseek (-1L, MFile::kCurrent);
			}
			else {
#ifdef BSL_DEBUG
				mPrintf (_TEXT("MID : can't read status byte [%02XH] ?"), _status);
#endif // BSL_DEBUG
				pParent->error (kErrSmfNoStatus, &iOrgTrk, &_status);
				err = kErrSmfNoStatus;
			}
		}
	}

	if (err == OK) {
		*status = _status;
	}

	return (err);
}

#ifdef BSL_DEBUG
/*******************************************************************************
*
* MMidReaderSmfTrk::nameShow -
*
* RETURNS:
* void
*/
void MMidReaderSmfTrk::nameShow (void)
{
	if (orgTrkNumberGet () == 0) {
		mPrintf (_TEXT("MID : title: %s"), cName);
		mPrintf (_TEXT("MID : trk[%02d]: %6lu[b] Conductor"), orgTrkNumberGet (), dwChunkSize);
	}
	else {
		mPrintf (_TEXT("MID : trk[%02d]: %6lu[b] %s"), orgTrkNumberGet (), dwChunkSize, cName);
	}
}

/*******************************************************************************
*
* MMidReaderSmfTrk::statusShow -
*
* RETURNS:
* void
*/
void MMidReaderSmfTrk::statusShow (void)
{
	if (orgTrkNumberGet () == 0) {
		mPrintf (_TEXT("MID : %02XH %08lXH   %02XH        %02XH        %08lXH"), 
			orgTrkNumberGet (), 
			dwReadtick, 
			byRunning, 
			channelGet (), 
			dwChunkSize);
	}
	else {
		mPrintf (_TEXT("MID : %02XH %08lXH   %02XH    %c   %02XH   %c    %08lXH"), 
			orgTrkNumberGet (), 
			dwReadtick, 
			byRunning, 
			(TCHAR) (portGet () + _TEXT('A')), 
			channelGet (), 
#ifdef M_MID_INCLUDE_OPTSW
			(TCHAR) optionSwitchGet (), 
#else // M_MID_INCLUDE_OPTSW
			_TEXT('-'),
#endif // M_MID_INCLUDE_OPTSW
			dwChunkSize);
	}
}
#endif // BSL_DEBUG
