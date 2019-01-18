/* mMidReaderSmf.cpp - smf class */

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
* MMidReaderSmf::MMidReaderSmf - 
*
* RETURNS:
* void
*/
MMidReaderSmf::MMidReaderSmf ()
{
#ifdef M_MID_INCLUDE_LOOPPLAY
	dwReadtickAtLoopStart = 0UL;
	dwReadtickAtLoopEnd = 0UL;
	dwLoops = 0UL;
	dwLoopLengthUnit = 0UL;
	dwLoopLength = 0UL;
#endif // M_MID_INCLUDE_LOOPPLAY

  iPlayedTracks = 0;
  dwStartReadtick = 0UL;
  
#ifdef M_MID_INCLUDE_INFINITE_TRACK
	vTrk.clear ();
#else // M_MID_INCLUDE_INFINITE_TRACK
	for (int i = 0; i < kMidSmfTracks; i++) {
		pTrk[i] = NULL;
	}
#endif // M_MID_INCLUDE_INFINITE_TRACK
}

/*******************************************************************************
*
* MMidReaderSmf::~MMidReaderSmf - 
*
* RETURNS:
* void
*/
MMidReaderSmf::~MMidReaderSmf ()
{
#ifdef M_MID_INCLUDE_INFINITE_TRACK
	trackDeleteAll ();
#else // M_MID_INCLUDE_INFINITE_TRACK
	for (int i = 0; i < kMidSmfTracks; i++) {
		if (pTrk[i]) {
			delete pTrk[i];
		}
		pTrk[i] = NULL;
	}
#endif // M_MID_INCLUDE_INFINITE_TRACK

	fileClose ();
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmf::ready - 
*
* RETURNS:
* error code
*/
int MMidReaderSmf::ready
	(
	int formatTypes,
	int *formatTypeList,
	int divisionTypes,
	int *divisionTypeList,
	void *user // extra parameter string
	)
{
	int err = OK;
	int i;

#ifdef BSL_DEBUG
/*
	mPrintf (_TEXT(""));
	mPrintf (_TEXT("MID : ---------------------------------------"));
	mPrintf (_TEXT("MID : standard midi file loaded"));
*/
#endif // BSL_DEBUG

	// clear smf parameters
	iPlayedTracks = 0;
	dwStartReadtick = 0UL;
#ifdef M_MID_INCLUDE_LOOPPLAY
	dwReadtickAtLoopStart = 0UL;
	dwReadtickAtLoopEnd = 0UL;
	dwLoops = 0UL;
	dwLoopLengthUnit = 0UL;
	dwLoopLength = 0UL;
#endif // M_MID_INCLUDE_LOOPPLAY

#ifdef M_MID_INCLUDE_INFINITE_TRACK
	trackDeleteAll ();
#else // M_MID_INCLUDE_INFINITE_TRACK
	for (i = 0; i < kMidSmfTracks; i++) {
		if (!pTrk[i]) {
			___try {
				pTrk[i] = new MMidReaderSmfTrk (this);
			}
			___catch {
				pTrk[i] = NULL;
				return (kErrSmfMemoryEmpty);
			}
		}
		pTrk[i]->initialize ();
	}	
#endif // M_MID_INCLUDE_INFINITE_TRACK

	// read smf header chunk
	DWORD header[2];
	if (err == OK) {
		err = readseek (0L, kStart);
	}
	
	// read chunk header
	if (err == OK) {
		err = read (&header, 8);
	}

#ifndef M_BIGENDIAN
	if (err == OK) {
		swap (&header[1], 4);
	}
#endif // M_BIGENDIAN

	// check 'MThd'
	if (err == OK) {
		if (header[0] != MThd_MARKER) {
			error (kErrSmfHeaderChunk);
			err = kErrSmfHeaderChunk;
		}
	}

	// check chunksize
	if (err == OK) {
		if (header[1] < 6UL) {
			error (kErrSmfHeaderChunkSize);
			err = kErrSmfHeaderChunkSize;
		}
	}

	// read format
	WORD format;
	if (err == OK) {
		err = read (&format, 2);
	}

	// read number of tracks
	WORD tracks;
	if (err == OK) {
		err = read (&tracks, 2);
	}

	// read division
	WORD division;
	if (err == OK) {
		err = read (&division, 2);
	}

#ifndef M_BIGENDIAN
	if (err == OK) {
		swap (&format, 2);
		swap (&tracks, 2);
		swap (&division, 2);
	}
#endif // M_BIGENDIAN

	// don't support SMPTE format
	if (err == OK) {
		if (division & kMidSmfDivisionSmpte) {
			error (kErrSmfDivisionFormat);
			err = kErrSmfDivisionFormat;
		}
	}

	if (err == OK) {
		formatSet ((int) format);
		tracksSet ((int) tracks);
		divisionSet ((int) division);
		// set read address to top address of 1st track chunk
		err = readseek ((long) (header[1] - 6UL), kCurrent);
	}

	// check format
	if (err == OK) {
		if (formatTypes > 0) {
			for (i = 0; i < formatTypes; i++) {
				if (formatGet () == formatTypeList[i]) {
					break;
				}
			}
			if (i >= formatTypes) {
				error (kErrSmfFormat);
				err = kErrSmfFormat;
			}
		}
	}

	// check tracks
	if (err == OK) {
		if (formatGet () == kFormat0 && tracksGet () > 1) {
			error (kErrSmfTracks0);
			err = kErrSmfTracks0;
		}
	}
	
	if (err == OK) {
		if (tracksGet () <= 0) {
			error (kErrSmfTracks);
			err = kErrSmfTracks;
		}
	}

	// check division
	if (err == OK) {
		if (divisionGet () <= 0) {
			error (kErrSmfDivision);
			err = kErrSmfDivision;
		}
	}
	
	if (err == OK) {
		if (divisionTypes > 0) {
			int i;
			for (i = 0; i < divisionTypes; i++) {
				if (divisionGet () == divisionTypeList[i]) {
					break;
				}
			}
			if (i >= divisionTypes) {
				error (kErrSmfDivision);
				err = kErrSmfDivision;
			}
		}
	}

	// read track chunks
	for (i = 0; i < tracksGet () && err == OK; i++) {
		MMidReaderSmfTrk *trk = NULL;
#ifdef M_MID_INCLUDE_INFINITE_TRACK		
		___try {
			trk = new MMidReaderSmfTrk (this);
		}
		___catch {
			err = kErrClassConstruct;
			goto parse_trk;
		}
		trk->initialize ();
#else // M_MID_INCLUDE_INFINITE_TRACK
		trk = pTrk[iPlayedTracks];
#endif // M_MID_INCLUDE_INFINITE_TRACK

		// read chunk header
		if ((err = read (&header, 8)) != OK) {
			goto parse_trk;
		}
#ifndef M_BIGENDIAN
		swap (&header[1], 4);
#endif // M_BIGENDIAN

		if (header[0] != MTrk_MARKER) {
			error (kErrSmfTrackChunk, &i);
			err = kErrSmfTrackChunk;
			goto parse_trk;
		}

		// ready track chunk
		if ((err = trk->ready (i, header[1])) != OK) {
#ifdef M_MID_INCLUDE_INFINITE_TRACK		
			delete trk;
			trk = NULL;
#endif // M_MID_INCLUDE_INFINITE_TRACK
			goto parse_trk;
		}

		// track select
		BOOL useTrk;
		if ((err = trackSelect (trk, user, &useTrk)) != OK) {
#ifdef M_MID_INCLUDE_INFINITE_TRACK		
			delete trk;
			trk = NULL;
#endif // M_MID_INCLUDE_INFINITE_TRACK
			goto parse_trk;
		}

		if (useTrk) {
			// set this track chunk will be played by sequencer
#ifdef M_MID_INCLUDE_INFINITE_TRACK
			___try {
				vTrk.push_back (trk);
			}
			___catch {
				err = kErrMemAlloc;
				goto parse_trk;
			}
#endif // M_MID_INCLUDE_INFINITE_TRACK

			iPlayedTracks++;

#ifndef M_MID_INCLUDE_INFINITE_TRACK
			if (iPlayedTracks > kMidSmfTracks) {
#ifdef BSL_DEBUG
				mPrintf (_TEXT("MID : too many smf tracks to play"));
#endif // BSL_DEBUG
				error (kErrSmfTracks);
				err = kErrSmfTracks;
				goto parse_trk;
			}
#endif // M_MID_INCLUDE_INFINITE_TRACK
		}
#ifdef M_MID_INCLUDE_INFINITE_TRACK
		else {
			// remove this track chunk (will not be used)
			delete trk;
			trk = NULL;
		}
#endif // M_MID_INCLUDE_INFINITE_TRACK
	}
	parse_trk: ;

	// total tracks
#ifdef BSL_DEBUG
/*
	if (err == OK) {
		mPrintf (_TEXT("MID : %d tracks (%d tracks will be played)"), tracksGet (), iPlayedTracks);
		mPrintf (_TEXT(""));
	}
*/
#endif // BSL_DEBUG

	if (err == OK) {
		err = rewind ();
	}

	if (err == OK) {
		readySet ();
	}

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmf::rewind
*
* RETURNS: 
* int
*/
int MMidReaderSmf::rewind
	(
	DWORD startReadtick
	)
{
	int err = OK;

	if (err == OK) {
		err = MMidReaderBase::rewind (startReadtick);
	}

	if (err == OK) {
#ifdef M_MID_INCLUDE_INFINITE_TRACK
		for (unsigned int i = 0; i < vTrk.size (); i++) {
			if ((err = vTrk[i]->rewind ()) != OK) {
				break;
			}
		}
#else // M_MID_INCLUDE_INFINITE_TRACK
		for (int i = 0; i < iPlayedTracks; i++) {
      if (pTrk[i]) {
        if ((err = pTrk[i]->rewind ()) != OK) {
          break;
        }
      }
		}
#endif // M_MID_INCLUDE_INFINITE_TRACK
	}

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderSmf::trackGet - 
*
* RETURNS:
* MMidReaderBaseTrk *
*/
MMidReaderBaseTrk *MMidReaderSmf::trackGet
	(
	int index
	)
{
#ifdef M_MID_INCLUDE_INFINITE_TRACK
	if (0 <= index && index < vTrk.size ()) {
		return vTrk[index];
	}
#else // M_MID_INCLUDE_INFINITE_TRACK
	if (0 <= index && index < kMidSmfTracks) {
		return pTrk[index];
	}
#endif // M_MID_INCLUDE_INFINITE_TRACK
	else {
		return NULL;
	}
}

/*******************************************************************************
*
* MMidReaderSmf::trackSelect - 
*
* RETURNS:
* error code
*/
int MMidReaderSmf::trackSelect
	(
	MMidReaderSmfTrk *trk, 
	void *user,
	BOOL *useTrk
	)
{
	(void) trk;
	(void) user;
		
	*useTrk = true;

	return (OK);
}

#ifdef M_MID_INCLUDE_INFINITE_TRACK
/*******************************************************************************
*
* MMidReaderSmf::trackDeleteAll - 
*
* RETURNS:
* void
*/
void MMidReaderSmf::trackDeleteAll (void)
{
	for (unsigned int i = 0; i < vTrk.size (); i++) {
		delete vTrk[i];
	}
	vTrk.clear ();
}
#endif // M_MID_INCLUDE_INFINITE_TRACK

#pragma mark -
#ifdef M_MID_INCLUDE_LOOPPLAY
/*******************************************************************************
*
* MMidReaderSmf::loopStartSet - 
*
* RETURNS:
* error code
*/
int MMidReaderSmf::loopStartSet
	(
	DWORD readtick
	)
{
#ifdef BSL_DEBUG
	mPrintf (_TEXT("MID : #%lu loop start = %08lXH[tick]"), dwLoops, readtick);
#endif // m_TEST
	
	if (dwLoops == 0UL) {
		dwReadtickAtLoopStart = readtick;
#ifdef BSL_DEBUG
		mPrintf (_TEXT("MID : remained tracks = %d"), iRemainedTracks);
#endif // BSL_DEBUG
		iRemainedTracksAtLoopStart = iRemainedTracks;
		for (int i = 0; i < playTracksGet (); i++) {
			MMidReaderSmfTrk *trk = trackGet (i);
			if (trk) {
				trk->loopStartSet ();
			}
		}
	} 

	return (kMidEventProcOk);
}

/*******************************************************************************
*
* MMidReaderSmf::loopEndSet -
*
* RETURNS:
* error code
*/
int MMidReaderSmf::loopEndSet
	(
	DWORD readtick
	)
{
#ifdef BSL_DEBUG
	mPrintf (_TEXT("MID : #%lu loop end = %08lXH[tick]"), dwLoops, readtick);
#endif // BSL_DEBUG
	
	if (dwLoops == 0UL) {
		dwReadtickAtLoopEnd = readtick;
		dwLoopLengthUnit = dwReadtickAtLoopEnd - dwReadtickAtLoopStart;
		// mPrintf (_TEXT("MID : loop unit = %08lXH[tick]", dwLoopLengthUnit);
	}

	dwLoops++;
	dwLoopLength = dwLoops * dwLoopLengthUnit;
	// mPrintf (_TEXT("MID : loop length = %08lXH[tick]", dwLoopLength);
	
	// mPrintf (_TEXT("MID : remained tracks = %d->%d", iRemainedTracks, iRemainedTracksAtLoopStart);
	iRemainedTracks = iRemainedTracksAtLoopStart;
	for (int i = 0; i < playTracksGet (); i++) {
		MMidReaderSmfTrk *trk = trackGet (i);
		if (trk) {
			trk->loopEndSet ();
		}
	}

	return (kMidEventProcLoopEnd);
}
#endif // M_MID_INCLUDE_LOOPPLAY

#pragma mark -
#ifdef BSL_DEBUG
/*******************************************************************************
*
* MMidReaderSmf::statusShow - show smf track chunk status
*
* RETURNS:
* void
*/
void MMidReaderSmf::statusShow (void)
{
	mPrintf (_TEXT("MID : trk readtime  running port ch  optSW  chunksize"));
	mPrintf (_TEXT("MID : -----------------------------------------------"));

	for (int i = 0; i < playTracksGet (); i++) {
		MMidReaderBaseTrk *trk = trackGet (i);
		if (trk) {
			trk->statusShow ();
		}
	}
}
#endif // BSL_DEBUG

#pragma mark -
void MMidReaderSmf::error
	(
	int num,
	void *param1,
	void *param2,
	void *param3
	)
{
#ifdef BSL_DEBUG
	(void) param3;

	switch (num) {
	case kErrSmfHeaderChunk:
		mError (_TEXT("MID : can't read header chunk"));
		break;
	case kErrSmfHeaderChunkSize:
		mError (_TEXT("MID : size of header chunk is illegal"));
		break;
	case kErrSmfFormat:
		mError (_TEXT("MID : unsupported smf format (%d)"), formatGet ());
		break;
	case kErrSmfTracks0:
		mError (_TEXT("MID : smf format0, but number of tracks isn't 0 (%d)"), tracksGet ());
		break;
	case kErrSmfTracks:
		mError (_TEXT("MID : number of tracks is (%d)"), tracksGet ());
		break;
	case kErrSmfDivisionFormat:
		mError (_TEXT("MID : division is based on SMTPE format"));
		break;
	case kErrSmfDivision:
		mError (_TEXT("MID : unsupported division (%d)"), divisionGet ());
		break;

	// track
	case kErrSmfTrackChunk:
		mError (_TEXT("MID : can't read track chunk (%d[track])"), *(int *) param1);
		break;
	case kErrSmfVarLenRead:
		mError (_TEXT("MID : can't read deltatime (%d[track])"), *(int *) param1);
		break;
	case kErrSmfMetaType:
		mError (_TEXT("MID : can't read meta event type (%d[track])"), *(int *) param1);
		break;
	case kErrSmfMetaSize:
		mError (_TEXT("MID : illegal size of meta event (%d[track])"), *(int *) param1);
		break;
	case kErrSmfSysexSize:
		mError (_TEXT("MID : illegal size of sysex (%d[track])"), *(int *) param1);
		break;
	case kErrSmfSysexData:
		mError (_TEXT("MID : illegal data in sysex (%d[track])"), *(int *) param1);
		break;
	case kErrSmfSysexSize2:
		mError (_TEXT("MID : illegal size of sysex(F7) (%d[track])"), *(int *) param1);
		break;
	case kErrSmfSysexData2:
		mError (_TEXT("MID : illegal data in sysex(F7) (%d[track])"), *(int *) param1);
		break;
	case kErrSmfChannelMsgData:
		mError (_TEXT("MID : illegal data in channel msg (%d[track])"), *(int *) param1);
		break;
	case kErrSmfNoStatus:
		mError (_TEXT("MID : no status byte (%d[track], %02X)"), *(int *) param1, *(BYTE *) param2);
		break;
	case kErrSmfUndefinedStatus:
		mError (_TEXT("MID : undefined status byte (%d[track], %02X)"), *(int *) param1, *(BYTE *) param2);
		break;
	case kErrSmfNoEOT:
		mError (_TEXT("MID : can't read end of track (%d[track])"), *(int *) param1);
		break;

	// not assigned
#ifdef M_MID_SMF_TRACKNAME_REQUIRED
	case kErrSmfNoTrackName:
		mError (_TEXT("MID : can't find track name (%d[track])"), *(int *) param1);
		break;
	case kErrSmfNon1stTrackName:
		mError (_TEXT("MID : multiple track name found (%d[track])"), *(int *) param1);
		break;
#endif // M_MID_SMF_TRACKNAME_REQUIRED
	}
#else // BSL_DEBUG
	(void) num;
	(void) param1;
	(void) param2;
	(void) param3;
#endif // BSL_DEBUG
}
