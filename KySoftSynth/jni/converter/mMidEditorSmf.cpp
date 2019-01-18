/* mMidEditorSmf.cpp - standard MIDI file editor */

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
#include "mFileMem.h"
#include "mMidDef.h"

#include "mMidEditorSmfTrk.h"
#include "mMidEditorSmf.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */


/*******************************************************************************
*
* MMidEditorSmf::MMidEditorSmf - 
*
* RETURNS:
* void
*/
MMidEditorSmf::MMidEditorSmf ()
{
	vTrk.clear ();

	divisionSet (480);

	useRunningStatus ();
	noteOffIs9nH ();
}

/*******************************************************************************
*
* MMidEditorSmf::~MMidEditorSmf - 
*
* RETURNS:
* void
*/
MMidEditorSmf::~MMidEditorSmf ()
{
	trackDeleteAll ();
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmf::fileNew - 
*
* RETURNS:
* void
*/
void MMidEditorSmf::fileNew (void)
{
	trackDeleteAll ();
	readySet ();
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmf::ready - 
*
* RETURNS:
* error code
*/
int MMidEditorSmf::ready
	(
	int formatTypes,
	int *formatTypeList,
	int divisionTypes,
	int *divisionTypeList,
	void *user // extra parameter string
	)
{
	int err = OK;

	if (err == OK) {
		err = trackDeleteAll ();
	}

	if (err == OK) {
		err = MMidReaderSmf::ready (formatTypes, formatTypeList, divisionTypes, divisionTypeList, user);
	}

	if (err == OK) {
		for (int i = 0; i < playTracksGet (); i++) {
			if ((err = trackAdd (NULL)) != OK) {
				break;
			}
			MMidReaderSmfTrk *src = (MMidReaderSmfTrk *) MMidReaderSmf::trackGet (i);
			MMidEditorSmfTrk *dst = vTrk.back ();
      dst->orgTrkNumberSet(src->orgTrkNumberGet());
      TCHAR name[256];
      src->nameGet(name, 255);
      dst->nameSet(name);
      if ((err = dst->eventLoadAll (src)) != OK) {
				break;
			}
		}
	}

	if (err == OK) {
		err = memoryFree ();
	}

	return (err);
}

int MMidEditorSmf::rewind
(
	DWORD startReadtick
	)
{
  for (MMidEditorSmfTrkType::iterator i = vTrk.begin (); i != vTrk.end (); i++) {
    (*i)->rewind();
  }
  return MMidReaderSmf::rewind (startReadtick);
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmf::trackAdd - 
*
* RETURNS:
* error code
*/
int MMidEditorSmf::trackAdd
	(
	MMidEditorSmfTrk *trk
	)
{
	int err = OK;

	if (err == OK) {
		if (!trk) {
			___try {
				trk = new MMidEditorSmfTrk (this);
			}
			___catch {
#ifdef BSL_DEBUG
				mError (_TEXT("MID : construct MMidEditorSmfTrk"));
#endif // BSL_DEBUG
				err = kErrClassConstruct;		
			}
		}
	}

	if (err == OK) {
    trk->parentSet (this);
		___try {
			vTrk.push_back (trk);
		}
		___catch {
#ifdef BSL_DEBUG
			mError (_TEXT("MID : add MMidEditorSmfTrk"));
#endif // BSL_DEBUG
			err = kErrMemAlloc;		
		}
	}

	if (err == OK) {
		vTrk.back ()->initialize ();
		if (vTrk.size () <= 1) {
			formatSet (kFormat0);
		}
		else {
			formatSet (kFormat1);
		}
	}

	return (err);
}

/*******************************************************************************
*
* MMidEditorSmf::trackAdd - 
*
* RETURNS:
* MMidEditorSmfTrk *
*/
MMidEditorSmfTrk *MMidEditorSmf::trackAdd (void)
{
	if (trackAdd (NULL) == OK) {
		return vTrk.back ();
	}

	return (NULL);
}

/*******************************************************************************
*
* MMidEditorSmf::trackDelete - 
*
* RETURNS:
* error code
*/
int MMidEditorSmf::trackDelete
	(
	int trk
	)
{
	if (0 <= trk && (unsigned int) trk < vTrk.size ()) {
		___try {
			delete vTrk[trk];
			vTrk.erase (vTrk.begin () + trk);
		}
		___catch {
			return kErrMemFree;
		}
		return (OK);
	}

	return kErrParamWrong;
}

/*******************************************************************************
*
* MMidEditorSmf::trackDeleteAll - 
*
* RETURNS:
* error code
*/
int MMidEditorSmf::trackDeleteAll (void)
{
	// delete all
	___try {
		for (MMidEditorSmfTrkType::iterator i = vTrk.begin (); i != vTrk.end (); i++) {
			delete (*i);
		}
		vTrk.clear ();
	}
	___catch {
		return kErrMemFree;
	}

	return (OK);
}

/*******************************************************************************
*
* MMidEditorSmf::trackGet - 
*
* RETURNS:
* MMidReaderBaseTrk *
*/
MMidReaderBaseTrk *MMidEditorSmf::trackGet
	(
	int trk
	)
{
	if (0 <= trk && (unsigned int) trk < vTrk.size ()) {
		return (MMidReaderSmfTrk *) vTrk[trk];
	}

	return NULL;
}

int MMidEditorSmf::numberOfTracks (void)
{
  return (int) vTrk.size ();
}

#pragma mark -
/*******************************************************************************
*
* MMidEditorSmf::save - 
*
* RETURNS:
* error code
*/
int MMidEditorSmf::save_
	(
	MFileRef fileRef, 
	void *parameter
	)
{
	(void) parameter;

	int err = OK;

	// header
	if (err == OK) err = writeByte (fileRef, 'M');
	if (err == OK) err = writeByte (fileRef, 'T');
	if (err == OK) err = writeByte (fileRef, 'h');
	if (err == OK) err = writeByte (fileRef, 'd');
	// chunksize
	if (err == OK) err = writeByte (fileRef, 0x00);
	if (err == OK) err = writeByte (fileRef, 0x00);
	if (err == OK) err = writeByte (fileRef, 0x00);
	if (err == OK) err = writeByte (fileRef, 0x06);
	// format
	if (err == OK) err = writeByte (fileRef, formatGet () >> 8);
	if (err == OK) err = writeByte (fileRef, formatGet () & 0xFF);
	// tracks
	if (err == OK) err = writeByte (fileRef, vTrk.size () >> 8);
	if (err == OK) err = writeByte (fileRef, vTrk.size () & 0xFF);
	// division
	if (err == OK) err = writeByte (fileRef, divisionGet () >> 8);
	if (err == OK) err = writeByte (fileRef, divisionGet () & 0xFF);

	if (err == OK) {
		for (unsigned int i = 0; i < vTrk.size (); i++) {
			err = vTrk[i]->save (fileRef, bUseRunningStatus, bNoteOffIs9nH) ;
			if (err != OK) break;
		}
	}

	return (err);
}

/*******************************************************************************
*
* MMidEditorSmf::useRunningStatus - 
*
* RETURNS:
* void
*/
void MMidEditorSmf::useRunningStatus
	(
	BOOL status
	)
{
	bUseRunningStatus = status;
}

/*******************************************************************************
*
* MMidEditorSmf::noteOffIs9nH - 
*
* RETURNS:
* void
*/
void MMidEditorSmf::noteOffIs9nH
	(
	BOOL status
	)
{
	bNoteOffIs9nH = status;
}
