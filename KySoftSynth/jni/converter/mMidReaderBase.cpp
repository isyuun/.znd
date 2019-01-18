/* mMidReaderBase.h - */

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
#include "mMidReaderBase.h"
#include "mMidReaderBaseTrk.h"

/* defines */

#define COPYRIGHT_DEF _TEXT("")

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MMidReaderBase::MMidReaderBase - 
*
* RETURNS:
* void
*/
MMidReaderBase::MMidReaderBase (void)
{
	pWrapper = NULL;
#ifdef M_MID_INCLUDE_COPYRIGHT
	_tcscpy (cCopy, COPYRIGHT_DEF);
#endif // M_MID_INCLUDE_COPYRIGHT
}

/*******************************************************************************
*
* MMidReaderBase::~MMidReaderBase - 
*
* RETURNS:
* void
*/
MMidReaderBase::~MMidReaderBase ()
{
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBase::rewind - 
*
* RETURNS:
* error code
*/
int MMidReaderBase::rewind
	(
	DWORD startReadtick
	)
{
	int err = OK;

	dwStartReadtick = startReadtick;
#ifdef M_MID_INCLUDE_MIDICLOCK
	dwMidiclockReadtick = 0UL;
	iMidiClockCounter = 0;
#endif // M_MID_INCLUDE_MIDICLOCK

	iRemainedTracks = iPlayedTracks;

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBase::formatGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::formatGet (void)
{
	return iFormat;
}

/*******************************************************************************
*
* MMidReaderBase::formatSet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::formatSet
	(
	int value
	)
{
	iFormat = value;
}

/*******************************************************************************
*
* MMidReaderBase::tracksGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::tracksGet (void)
{
	return iTracks;
}

/*******************************************************************************
*
* MMidReaderBase::tracksSet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::tracksSet
	(
	int value
	)
{
	iTracks = value;
}

/*******************************************************************************
*
* MMidReaderBase::divisionGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::divisionGet (void)
{
	return iDivision;
}

/*******************************************************************************
*
* MMidReaderBase::divisionSet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::divisionSet
	(
	int value,
	int clocksPerBeat
	)
{
	iDivision = value;
#ifdef M_MID_INCLUDE_MIDICLOCK
	iMidiclocksPerBeat = clocksPerBeat;
	dwTicksPerMidiclock = (DWORD) (iDivision / clocksPerBeat);
	dwTicksMidiClockOddPerBeat = (DWORD) (iDivision % clocksPerBeat);
#endif // M_MID_INCLUDE_MIDICLOCK
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBase::trackNameGet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::trackNameGet
	(
	int index, 
	LPTSTR str, 
	int length
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		trk->nameGet (str, length);
	}
}

/*******************************************************************************
*
* MMidReaderBase::playTracksGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::playTracksGet (void)
{
	return iPlayedTracks;
}

/*******************************************************************************
*
* MMidReaderBase::remainTracksGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::remainTracksGet (void)
{
	return iRemainedTracks;
}

/*******************************************************************************
*
* MMidReaderBase::portGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBase::portGet
	(
	int index
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		return (trk->portGet ());
	}
	else {
		return MMidReaderBaseTrk::kMidPortNull;
	}
}

/*******************************************************************************
*
* MMidReaderBase::channelGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBase::channelGet
	(
	int index
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		return (trk->channelGet ());
	}
	else {
		return MMidReaderBaseTrk::kMidChNull;
	}
}

#ifdef M_MID_INCLUDE_OPTSW
/*******************************************************************************
*
* MMidReaderBase::optionSwitchGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBase::optionSwitchGet
	(
	int index
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		return (trk->optionSwitchGet ());
	}
	else {
		return MMidReaderBaseTrk::kMidOptionSwNull;
	}
}
#endif // M_MID_INCLUDE_OPTSW

/*******************************************************************************
*
* MMidReaderBase::orgTrkNumberGet - 
*
* RETURNS:
* int
*/
int MMidReaderBase::orgTrkNumberGet
	(
	int index
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		return (trk->orgTrkNumberGet ());
	}
	else {
		return -1;
	}
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBase::eventSort - sort smf events
*
* RETURNS: 
* event sort status (kMidEventProc.xxx)
*/
int MMidReaderBase::eventSort
	(
	DWORD startReadtick
	)
{
	DWORD readtick;
	MMidEvent *event;
	int i;
	int err;

#ifdef M_MID_INCLUDE_MIDICLOCK
	MMidEvent midiClkEvent;
	midiClkEvent.dwTick = 0UL;
#ifdef M_MID_INCLUDE_OPTSW
	midiClkEvent.byOptSw = MMidReaderBaseTrk::kMidOptionSwNull;
#endif // M_MID_INCLUDE_OPTSW
	midiClkEvent.byPort = MMidReaderBaseTrk::kMidPortNull;
	midiClkEvent.byStatus = kMidStatusMclk;
	midiClkEvent.iSize = 0;
	midiClkEvent.pData = NULL;
#endif // M_MID_INCLUDE_MIDICLOCK

	// change start position
	if (dwStartReadtick != startReadtick) {

		iRemainedTracks = iPlayedTracks;
		if ((err = rewind (startReadtick)) != OK) {
			return (kMidEventProcError);
		}
		dwStartReadtick = startReadtick;
#ifdef M_MID_INCLUDE_MIDICLOCK
		dwMidiclockReadtick = 0UL;
		iMidiClockCounter = 0;
#endif // M_MID_INCLUDE_MIDICLOCK

		// event chasing (c) motu
		while (1) {
			// search next readtick
			readtick = (DWORD) kMidSmfReadtickMax;
			for (i = 0; i < iPlayedTracks; i++) {
				MMidReaderBaseTrk *trk = trackGet (i);
				if (trk->readtickGet () < readtick && !trk->isFinished ()) {
					readtick = trk->readtickGet ();
				}
			}
			if (readtick >= dwStartReadtick) {
				break;
			}

#ifdef M_MID_INCLUDE_MIDICLOCK
			// generate midi clock
			while (dwMidiclockReadtick <= readtick) {
				midiClkEvent.dwTick = dwMidiclockReadtick;
				err = eventChase (&midiClkEvent);
				switch (err) {
				case kMidEventProcOk:
					dwMidiclockReadtick += ((DWORD) iDivision / (DWORD) kMidMclkTpqn);
						break;
				default:
					return (err);
				}
			}
#endif // M_MID_INCLUDE_MIDICLOCK

			// search midi events on avobe readtime
			for (i = 0; i < iPlayedTracks; i++) {
				MMidReaderBaseTrk *trk = trackGet (i);
				while (!trk->isFinished () && trk->readtickGet () == readtick) {
					if ((err = trk->eventRead (&event)) != OK) {
						return (kMidEventProcError);
					}

					// channel check
					BYTE byStatus = event->byStatus;
					BYTE byChannel = trk->channelGet ();
					if ((byStatus >= 0x80 && byStatus < 0xF0) 
						&& byChannel != MMidReaderBaseTrk::kMidChNull 
						&& (byStatus & 0x0F) != byChannel) {
						// wrong midi channel
						if ((err = trk->eventInc (event)) != OK) {
							return (kMidEventProcError);
						}
					}
					else {
						// end of track
						if (event->byStatus == kMidSmfMetaTypeEnd) {
							if (trackClose (i)) {
								return (kMidEventProcFinished);
							}
							break;
						}
						// callback
						err = eventChase (event);
						switch (err) {
						case kMidEventProcOk:
							if ((err = trk->eventInc (event)) != OK) {
								return (kMidEventProcError);
							}
							break;
						default:
							return (err);
						}
					}
				}
			}
		}
	}

	// mPrintf (_TEXT("event chase completed..."));

	while (1) {
		//*************
		// event get
		//*************
		// search next readtick
		readtick = (DWORD) kMidSmfReadtickMax;
		for (i = 0; i < iPlayedTracks; i++) {
			MMidReaderBaseTrk *trk = trackGet (i);
			if (trk->readtickGet () < readtick && !trk->isFinished ()) {
				readtick = trk->readtickGet ();
			}
		}
#ifdef M_MID_INCLUDE_LOOPPLAY
		if (readtick > ((DWORD) kMidSmfReadtickMax - dwLoopLength)) {
#ifdef BSL_DEBUG
			mError (_TEXT("MID : too long song (^^;"));
#endif // BSL_DEBUG
			return (kMidEventProcFinished);
		}
#endif // M_MID_INCLUDE_LOOPPLAY

#ifdef BSL_DEBUG
		// mPrintf (_TEXT("readtick = %08lX"), readtick);
#endif // BSL_DEBUG

#ifdef M_MID_INCLUDE_MIDICLOCK
		// generate midi clock
		while (dwMidiclockReadtick <= readtick) {
			midiClkEvent.dwTick = dwMidiclockReadtick;
			err = eventGet (&midiClkEvent);
			switch (err) {
			case kMidEventProcOk:
				dwMidiclockReadtick += ((DWORD) iDivision / (DWORD) kMidMclkTpqn);
				iMidiClockCounter++;
				if (iMidiclocksPerBeat <= iMidiClockCounter) {
					iMidiClockCounter = 0;
					dwMidiclockReadtick += dwTicksMidiClockOddPerBeat;
				}
				break;
			default:
				return (err);
			}
		}
#endif // M_MID_INCLUDE_MIDICLOCK

		// search and save midi events on readtime
		for (i = 0; i < iPlayedTracks; i++) {
			MMidReaderBaseTrk *trk = trackGet (i);
			while (!trk->isFinished () && trk->readtickGet () == readtick) {
				if ((err = trk->eventRead (&event)) != OK) {
					return (kMidEventProcError);
				}

				// channel check
				BYTE byStatus = event->byStatus;
				BYTE byChannel = trk->channelGet ();
				if ((byStatus >= 0x80 && byStatus < 0xF0) 
					&& byChannel != MMidReaderBaseTrk::kMidChNull 
					&& (byStatus & 0x0F) != byChannel) {
					// wrong midi channel
					if ((err = trk->eventInc (event)) != OK) {
						return (kMidEventProcError);
					}
				}
				else {
					// end of track
					if (event->byStatus == kMidSmfMetaTypeEnd) {
						if (trackClose (i)) {
							return (kMidEventProcFinished);
						}
						break;
					}
					err = eventGet (event);
					switch (err) {
					case kMidEventProcOk:
						if ((err = trk->eventInc (event)) != OK) {
							return (kMidEventProcError);
						}
						break;
					case kMidEventProcLoopEnd:
					default:
						return (err);
					}
				}
			} // while (readtick == )
		} // for (iTrk)
	} // while (1)
	
	return (kMidEventProcError);
}

/*******************************************************************************
*
* MMidReaderBase::eventGet - 
*
* RETURNS:
* status
*/
int MMidReaderBase::eventGet
	(
	MMidEvent *event
	)
{
	if (pWrapper) {
		return pWrapper->eventGet (event);
	}
	
	return (kMidEventProcOk);
}

/*******************************************************************************
*
* MMidReaderBase::eventChase - 
*
* RETURNS:
* status
*/
int MMidReaderBase::eventChase
	(
	MMidEvent *event
	)
{
	if (pWrapper) {
//		return pWrapper->eventChase (event);
	}
	
	return (kMidEventProcOk);
}

/*******************************************************************************
*
* MMidReaderBase::trackClose - 
*
* RETURNS:
* BOOL
*/
BOOL MMidReaderBase::trackClose
	(
	int index
	)
{
	MMidReaderBaseTrk *trk = trackGet (index);
	if (trk) {
		trk->close ();
		if (iRemainedTracks > 0) {
			iRemainedTracks--;
		}
		if (iRemainedTracks == 0) {
			return (true);
		}
	}

	return (false);
}


#ifdef M_MID_INCLUDE_COPYRIGHT
/*******************************************************************************
*
* MMidReaderBase::copyrightGet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::copyrightGet
	(
	LPTSTR str, 
	int length
	)
{
	if (str) {
		_tcsncpy_(str, cCopy, length);
	}
#ifdef BSL_DEBUG
	else {
		mPrintf (_TEXT("%s"), cCopy);
	}
#endif // BSL_DEBUG
}

/*******************************************************************************
*
* MMidReaderBase::copyrightSet - 
*
* RETURNS:
* void
*/
void MMidReaderBase::copyrightSet
	(
	LPTSTR str
	)
{
	_tcsncpy_(cCopy, str, kCopyrightLength - 1);
}
#endif // M_MID_INCLUDE_COPYRIGHT

#pragma mark -
void MMidReaderBase::wrapperSet
	(
	MMidReaderBase *wrapper
	)
{
	pWrapper = wrapper;
}

#pragma mark -
#ifdef BSL_DEBUG
/*******************************************************************************
*
* MMidReaderBase::nameShow - show smf track chunk name
*
* RETURNS: 
* void
*/
void MMidReaderBase::nameShow (void)
{
	mPrintf (_TEXT("MID : track name"));
	mPrintf (_TEXT("MID : ---------------"));
	
	for (int i = 0; i < playTracksGet (); i++) {
		MMidReaderBaseTrk *trk = trackGet (i);
		if (trk) {
			trk->nameShow ();
		}
	}

	mPrintf (_TEXT(""));
}
#endif // BSL_DEBUG

