/* mMidReaderBaseTrk.cpp - midi track class with stl vector event */

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
#include "mMidReaderBase.h"
#include "mMidReaderBaseTrkEvent.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MMidReaderBaseTrkEvent::MMidReaderBaseTrkEvent - 
*
* RETURNS:
* void
*/
MMidReaderBaseTrkEvent::MMidReaderBaseTrkEvent
	(
	MMidReaderBase *parent
	) : MMidReaderBaseTrk (parent)
{
	vEvent.clear ();
}

/*******************************************************************************
*
* MMidReaderBaseTrkEvent::~MMidReaderBaseTrkEvent - 
*
* RETURNS:
* void
*/
MMidReaderBaseTrkEvent::~MMidReaderBaseTrkEvent ()
{
	eventDeleteAll ();
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBaseTrkEvent::initialize - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::initialize (void)
{
	eventDeleteAll ();
	return MMidReaderBaseTrk::initialize ();
}

/*******************************************************************************
*
* MMidReaderBaseTrkEvent::rewind - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::rewind (void)
{
	vEventIterator =vEvent.begin ();
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
* MMidReaderBaseTrkEvent::eventRead - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::eventRead
	(
	MMidEvent **event
	)
{
	*event = *vEventIterator;
	return (OK);
}

/*******************************************************************************
*
* MMidReaderBaseTrkEvent::eventInc - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::eventInc
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
* MMidReaderBaseTrkEvent::eventNumGet - 
*
* RETURNS:
* DWORD
*/
DWORD MMidReaderBaseTrkEvent::eventNumGet (void)
{
	return (DWORD)  vEvent.size ();
}

/*******************************************************************************
*
* MMidReaderBaseTrkEvent::eventGet - 
*
* RETURNS:
* MMidEvent *
*/
MMidEvent *MMidReaderBaseTrkEvent::eventGet
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
* MMidReaderBaseTrkEvent::eventAdd - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::eventAdd 
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
* MMidReaderBaseTrkEvent::eventDelete - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::eventDelete
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
* MMidReaderBaseTrkEvent::eventDeleteAll - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrkEvent::eventDeleteAll (void)
{
	// delete all events
	___try {
		for (MMidReaderBaseTrkEventType::iterator i = vEvent.begin (); i != vEvent.end (); i++) {
			if ((*i)->pData) {
				delete [] (*i)->pData;
				(*i)->pData = NULL;
			}
			delete *i;
		}
		vEvent.clear ();
	}
	___catch {
		return (kErrMemFree);
	}

	return (OK);
}
