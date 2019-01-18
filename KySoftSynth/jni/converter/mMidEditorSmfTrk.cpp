/* mMidEditorSmfTrk.cpp - standard MIDI file editor */

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
#include "mDebug.h"
#include "mError.h"
#include "mMidDef.h"

#include "mMidEditorSmf.h"
#include "mMidEditorSmfTrk.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */


/*******************************************************************************
*
* MMidSmfEditorTrk:: MMidSmfEditorTrk - 
*
* RETURNS:
* void
*/
MMidEditorSmfTrk::MMidEditorSmfTrk
	(
	MMidReaderBase *parent
	) : MMidReaderSmfTrk (parent)
{
	vEvent.clear ();
}

/*******************************************************************************
*
* MMidSmfEditorTrk::~MMidSmfEditorTrk - 
*
* RETURNS:
* void
*/
MMidEditorSmfTrk::~MMidEditorSmfTrk ()
{
	eventDeleteAll ();
}

#pragma mark -
/*******************************************************************************
*
* MMidSmfEditorTrk::initialize - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::initialize (void)
{
	eventDeleteAll ();
	return MMidReaderSmfTrk::initialize ();
}

/*******************************************************************************
*
* MMidEditorSmfTrk::rewind - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::rewind (void)
{
	vEventIterator = vEvent.begin ();
	if (vEvent.size () > 0) {
		dwReadtick = (*vEventIterator)->dwTick;
	}
	else {
		dwReadtick = (DWORD) kMidSmfReadtickMax;
	}
	
	return MMidReaderBaseTrk::rewind ();
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmfTrk::eventRead - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventRead
	(
	MMidEvent **event
	)
{
	*event = *vEventIterator;
	return (OK);
}

/*******************************************************************************
*
* MMidEditorSmfTrk::eventInc - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventInc
	(
	MMidEvent *event
	)
{
	(void) event;

	vEventIterator++;
	dwReadtick = (*vEventIterator)->dwTick;
	return (OK);
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmfTrk::eventNumGet - 
*
* RETURNS:
* DWORD
*/
DWORD MMidEditorSmfTrk::eventNumGet (void)
{
	return (DWORD)  vEvent.size ();
}

/*******************************************************************************
*
* MMidEditorSmfTrk::eventGet - 
*
* RETURNS:
* MMidEvent *
*/
MMidEvent *MMidEditorSmfTrk::eventGet
	(
	DWORD index
	)
{
	if (index < vEvent.size ()) {
		return vEvent[index];
	}

	return (NULL);
}

/*******************************************************************************
*
* MMidEditorSmfTrk::eventAdd - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventAdd 
	(
	DWORD tick, 
	int size,
	MMidEvent **event
	)
{
	MMidEvent *newEvent;
	
	___try {
		newEvent = new MMidEvent;
	}
	___catch {
#ifdef BSL_DEBUG
		mError (_TEXT("MID : memory empty"));
#endif // BSL_DEBUG
		return (kErrMemAlloc);
	}

	newEvent->dwTick = tick; 
	newEvent->iSize = size;
	if (size > 0) {
		___try {
			newEvent->pData = new BYTE [size];
		}
		___catch {
#ifdef BSL_DEBUG
			mError (_TEXT("MID : memory empty"));
#endif // BSL_DEBUG
			delete newEvent;
			return (kErrMemAlloc);
		}
	}
	else newEvent->pData = NULL;

	if (vEvent.size () == 0 || vEvent.back ()->dwTick <= tick) {
		___try {
			vEvent.push_back (newEvent);
		}
		___catch {
#ifdef BSL_DEBUG
			mError (_TEXT("MID : memory empty"));
#endif // BSL_DEBUG
			if (newEvent->pData) {
				delete [] newEvent->pData;
				newEvent->pData = NULL;
			}
			delete newEvent;
			return kErrMemAlloc;
		}
		*event = vEvent.back ();
		return (OK);
	}
	else {
		for (MMidReaderBaseTrkEventType::iterator i = vEvent.begin (); i != vEvent.end (); i++) {
			if (tick < (*i)->dwTick) {
				___try {
					vEvent.insert (i, newEvent);
				}
				___catch {
	#ifdef BSL_DEBUG
					mError (_TEXT("MID : memory empty"));
	#endif // BSL_DEBUG
					if (newEvent->pData) {
						delete [] newEvent->pData;
						newEvent->pData = NULL;
					}
					delete newEvent;
					return kErrMemAlloc;
				}
				*event = newEvent;
				return (OK);
			}
		}
	}

#ifdef BSL_DEBUG
	mError (_TEXT("MID : event error"));
#endif // BSL_DEBUG
	return (kErrUndefined);
}

/*******************************************************************************
*
* MMidEditorSmfTrk::eventDelete - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventDelete
	(
	DWORD index
	)
{
	if (index < vEvent.size ()) {
		___try {
			delete vEvent[index];
			vEvent.erase (vEvent.begin () + index);
		}
		___catch {
			return (kErrMemFree);
		}
		
		return (OK);
	}

	return (kErrParamWrong);
}

/*******************************************************************************
*
* MMidEditorSmfTrk::eventDeleteAll - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventDeleteAll (void)
{
	// delete all events
	___try {
		for (MMidReaderBaseTrkEventType::iterator i = vEvent.begin (); i != vEvent.end (); i++) {
			if ((*i)->pData) {
				delete [] (*i)->pData;
				(*i)->pData = NULL;
			}
		}
		vEvent.clear ();
	}
	___catch {
		return (kErrMemFree);
	}

	return (OK);
}

int MMidEditorSmfTrk::eventDeleteInRange (DWORD fromTick, DWORD toTick)
{
  ___try {
    for (MMidReaderBaseTrkEventType::iterator i = vEvent.begin (); i != vEvent.end (); ) {
      if (fromTick <= (*i)->dwTick && (*i)->dwTick < toTick) {
        if ((*i)->pData) {
          delete [] (*i)->pData;
          (*i)->pData = NULL;
        }
        i = vEvent.erase(i);
        continue;
      }
      i++;
    }
  }
  ___catch {
    return (kErrMemFree);
  }
  
  return (OK);
}

#pragma mark -
/*******************************************************************************
*
* MMidSmfEditorTrk::eventLoadAll - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventLoadAll (void)
{
	rewind ();

	while (1) {
		MMidEvent *event;
		int err = OK;
		if ((err = MMidReaderSmfTrk::eventRead (&event)) != OK) {
			return (err);
		} 

		MMidEvent *newEvent;
		if ((err = eventAdd (readtickGet (), event->iSize, &newEvent)) != OK) {
			return (err);
		}
		newEvent->byPort = event->byPort;
		newEvent->byStatus = event->byStatus;
		newEvent->iOrgTrk = orgTrkNumberGet();
#ifdef M_MID_INCLUDE_OPTSW
		newEvent->byOptSw = event->byOptSw;
#endif // M_MID_INCLUDE_OPTSW
		if (newEvent->pData) {
			::memcpy (newEvent->pData, event->pData, newEvent->iSize);
		}

		if (event->byStatus == kMidSmfMetaTypeEnd) {
			break;
		}

		MMidReaderSmfTrk::eventInc (event);
	}

	rewind ();

#ifdef BSL_DEBUG
	// mPrintf (_TEXT("MID : load %u events"), vEvent.size ());
#endif // BSL_DEBUG

	return (OK);
}

/*******************************************************************************
*
* MMidSmfEditorTrk::eventLoadAll - 
*
* RETURNS:
* error code
*/
int MMidEditorSmfTrk::eventLoadAll
	(
	MMidReaderSmfTrk *reader
	)
{
	reader->rewind ();
	rewind ();

	while (1) {
		MMidEvent *event;
		int err;
		if ((err = reader->eventRead (&event)) != OK) {
			return (err);
		} 

		// printf (" --- %08lX - %02X\n", dwReadtick, event.byStatus);
		MMidEvent *newEvent;
		if ((err = eventAdd (reader->readtickGet(), event->iSize, &newEvent)) != OK) {
			return (err);
		}
		newEvent->byPort = event->byPort;
		newEvent->byStatus = event->byStatus;
		newEvent->iOrgTrk = orgTrkNumberGet();;
#ifdef M_MID_INCLUDE_OPTSW
		newEvent->byOptSw = event->byOptSw;
#endif // M_MID_INCLUDE_OPTSW
		if (newEvent->pData) {
			::memcpy (newEvent->pData, event->pData, newEvent->iSize);
		}

		if (event->byStatus == kMidSmfMetaTypeEnd) {
			break;
		}

		reader->eventInc (event);
	}

	reader->rewind ();
	rewind ();

#ifdef BSL_DEBUG
	// mPrintf (_TEXT("MID : load %u events"), vEvent.size ());
#endif // BSL_DEBUG

	return (OK);
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmfTrk::varLenWrite - 
*
* RETURNS:
* void
*/
int MMidEditorSmfTrk::varLenWrite
	(
	DWORD value, // encoded data
	MFileRef fileRef
	)
{
	DWORD buffer = value & 0x7F;

	while ((value >>= 7) > 0) {
		buffer <<= 8;
		buffer |=	0x80;
		buffer +=	(value & 0x7F);
	}
	while (1) {
		BYTE data = (BYTE) (buffer & 0x000000FFUL);
		int err;
		if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, &data, 1)) != OK) {
			return (err);
		}
		if (buffer & 0x80) {
			buffer >>= 8;
		}
		else {
			break;
		}
	}

	return (OK);
}

/*******************************************************************************
*
* MMidEditorSmfTrk::save - 
*
* RETURNS:
* OK of ERR if failed
*/
int MMidEditorSmfTrk::save
	(
	MFileRef fileRef, 
	BOOL useRunningStatus, 
	BOOL noteOffIs9nH
	)
{
	int err;
	DWORD lastTick = 0UL;
	BYTE lastStatus = kMidStatusNull;

	// end of track
	if (vEvent.size () <= 0 || vEvent.back ()->byStatus != kMidSmfMetaTypeEnd) {
		if (vEvent.size () > 0) {
			lastTick = vEvent.back ()->dwTick;
		}
		MMidEvent *newEvent;
		if ((err = eventAdd (lastTick, 0, &newEvent)) != OK) {
			return (err);
		}
		newEvent->byStatus = kMidSmfMetaTypeEnd;
	}

	// header
	DWORD header = MTrk_MARKER;
	if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, &header, 4)) != OK) {
		return (err);
	}

	// size
	DWORD posSize = ((MMidEditorSmf *) pParent)->writeposition ();
	header = 0UL; // dummy
#ifndef M_BIGENDIAN
	pParent->swap (&header, 4);
#endif // M_BIGENDIAN
	if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, &header, 4)) != OK) {
		return (err);
	}

	lastTick = 0UL;
	lastStatus = kMidStatusNull;

	for (MMidReaderBaseTrkEventType::iterator i = vEvent.begin (); i != vEvent.end (); i++) {
		// delta
		DWORD dwDelta = (*i)->dwTick - lastTick;
		if ((err = varLenWrite (dwDelta, fileRef)) != OK) { // delta time
			return (err);
		}
		lastTick = (*i)->dwTick;

		BYTE status = (*i)->byStatus;
		// meta event
		if (status < 0x80) {
			if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, kMidSmfStatusMeta)) != OK) { // status
				return (err);
			}
			if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, status)) != OK) { //type
				return (err);
			}
			if ((err = varLenWrite ((DWORD) (*i)->iSize, fileRef)) != OK) { // size
				return (err);
			}
			if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, (*i)->pData, (*i)->iSize)) != OK) {
				return (err);
			}
		}
		// channel message
		else if (status < 0xF0) {
			if ((status & 0xF0) == kMidStatusNoteOff && noteOffIs9nH) {
				status &= 0x0F;
				status += kMidStatusNoteOn;
				// status
				if (useRunningStatus) {
					if (lastStatus != status) {
						if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, status)) != OK) {
							return (err);
						}
					}
				}
				else {
					if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, status)) != OK) {
						return (err);
					}
				}
				// data
				if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, (*i)->pData[0])) != OK) { // note number
					return (err);
				}
				if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, 0x00)) != OK) { // velocity
					return (err);
				}
			}
			else {
				// status
				if (useRunningStatus) {
					if (lastStatus != status) {
						if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, status)) != OK) {
							return (err);
						}
					}
				}
				else {
					if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, status)) != OK) {
						return (err);
					}
				}
				// data
				if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, (*i)->pData, (*i)->iSize)) != OK) {
					return (err);
				}
			}
		}
		// system exclisive
		else if (status == kMidStatusSyx) {
			if ((err = ((MMidEditorSmf *) pParent)->writeByte (fileRef, kMidStatusSyx)) != OK) { // status
				return (err);
			}
			if ((err = varLenWrite ((DWORD) (*i)->iSize, fileRef)) != OK) { // size
				return (err);
			}
			if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, (*i)->pData, (*i)->iSize)) != OK) {
				return (err);
			}
		}

		lastStatus = status;
	}

	// fix size
	DWORD posLast = ((MMidEditorSmf *) pParent)->writeposition ();
	if ((err = ((MMidEditorSmf *) pParent)->writeseek (fileRef, posSize, MFile::kStart)) != OK) {
		return (err);
	}

	header = posLast - posSize - 4;
#ifndef M_BIGENDIAN
	pParent->swap (&header, 4);
#endif // M_BIGENDIAN
	if ((err = ((MMidEditorSmf *) pParent)->write (fileRef, &header, 4)) != OK) {
		return (err);
	}

	if ((err = ((MMidEditorSmf *) pParent)->writeseek (fileRef, posLast, MFile::kStart)) != OK) {
		return (err);
	}
	
	return (OK);
}

