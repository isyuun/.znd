/* mMidReaderBase.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmMidReaderBaseh
#define __INCmMidReaderBaseh

/* includes */

#include "mFileMem.h"
#include "mMidDef.h"

/* defines */

enum {
	// event processing status
	kMidEventProcOk = 0,
	kMidEventProcError,
	kMidEventProcBufferFull,
	kMidEventProcFinished,
	kMidEventProcLoopEnd,
	kMidEventProcUndefined,

	// meta event types
	kMidSmfMetaTypeSeqNum = 0x00, // sequence number
	kMidSmfMetaTypeText = 0x01, // text
	kMidSmfMetaTypeCopyright = 0x02, // copyright
	kMidSmfMetaTypeName = 0x03, // sequence/track name
	kMidSmfMetaTypeInst = 0x04, // instrument name
	kMidSmfMetaTypeLyric = 0x05, // lyric
	kMidSmfMetaTypeMarker = 0x06, // marker
	kMidSmfMetaTypeCue = 0x07, // cue point
	kMidSmfMetaTypeChPrefix = 0x20, // channel prefix
	kMidSmfMetaTypeEnd = 0x2F, // end of track chunk
	kMidSmfMetaTypeTempo = 0x51, // set tempo
	kMidSmfMetaTypeSmpte = 0x54, // smpte offset
	kMidSmfMetaTypeTimeSig = 0x58, // time signature
	kMidSmfMetaTypeKey = 0x59, // key
	kMidSmfMetaTypeSeqSpecific = 0x7F, // sequencer specific

	kMidSmfStatusMeta = 0xFF, // meta event status
	kMidSmfReadtickMax = 0xFFFFFFFFUL,
	kMidSmfDivisionSmpte = 0x8000 // SMPTE formated division 
};

/* typedefs */

/* class */

class MMidReaderBaseTrk;

class MMidReaderBase : public MFileMem
{
public:

	MMidReaderBase ();
	virtual ~MMidReaderBase ();

	virtual int ready
		(
		int formatTypes = 0, // number of supported formats
		int *formatTypeList = NULL, // supported formats
		int divisionType = 0, // number of supported divisions
		int *divisionTypeList = NULL,	// supported divisions
		void *user = NULL // other parameter in ascii
		) = 0;

	virtual int rewind (DWORD startReadtick = 0UL);
	virtual int eventSort (DWORD startReadtick = 0UL);

	virtual int formatGet (void);
	void formatSet (int value);
	
	virtual int tracksGet (void);
	void tracksSet (int value);

	virtual int divisionGet (void);
	void divisionSet (int value, int clocksPerBeat = kMidMclkTpqn);

	virtual MMidReaderBaseTrk *trackGet (int index) { return NULL; }
	virtual void trackNameGet (int trk, LPTSTR str, int length);
	virtual int playTracksGet (void);
	virtual int remainTracksGet (void);
	BYTE portGet (int trk);
	BYTE channelGet (int trk);
#ifdef M_MID_INCLUDE_OPTSW
	BYTE optionSwitchGet (int trk);
#endif // M_MID_INCLUDE_OPTSW
	int orgTrkNumberGet (int trk);

#ifdef BSL_DEBUG
	virtual void statusShow (void) = 0; // show status
	virtual void nameShow (void); // show track names
#endif // BSL_DEBUG

#ifdef M_MID_INCLUDE_COPYRIGHT
	enum {
		kCopyrightLength = 128
	};
	void copyrightGet (LPTSTR str, int length);
	void copyrightSet (LPTSTR str);
#endif // M_MID_INCLUDE_COPYRIGHT

	void wrapperSet (MMidReaderBase *wrapper);

	virtual void error (int num, void *param1 = NULL, void *param2 = NULL, void *param3 = NULL) {}

protected:

	virtual int eventGet (MMidEvent *event);
	virtual int eventChase (MMidEvent *event);

	int iPlayedTracks; // number of played tracks 
	int iRemainedTracks; // number of remained trks 

	DWORD dwStartReadtick; // tick for play start

private:

	BOOL trackClose (int index);
	MMidReaderBase *pWrapper;
#ifdef M_MID_INCLUDE_COPYRIGHT
	TCHAR cCopy[kCopyrightLength]; // copyright notice
#endif // M_MID_INCLUDE_COPYRIGHT

	int iFormat;// smf format
	int iTracks; // number of smf track
	int iDivision; // smf division [TPQN]

#ifdef M_MID_INCLUDE_MIDICLOCK
	DWORD dwMidiclockReadtick; // current tick for midiclk
	DWORD dwTicksPerMidiclock;
	DWORD dwTicksMidiClockOddPerBeat;
	int iMidiclocksPerBeat;
	int iMidiClockCounter;
#endif // M_MID_INCLUDE_MIDICLOCK

	enum {
		kMidMclkTpqn = 24 // midi clock [TPQN] 
	};
};

#endif /* __INCmMidReaderBaseh */

