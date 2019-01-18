/* mMidReaderSmfTrk.h - smf defines */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidReaderSmfTrkh
#define __INCmMidReaderSmfTrkh

/* include */

#include "mMidReaderBaseTrk.h"

/* defines */

/* typedefs */

/* class */

class MMidReaderSmfTrk : public MMidReaderBaseTrk
{
public:

	MMidReaderSmfTrk (MMidReaderBase *parent);
	virtual ~MMidReaderSmfTrk ();

	virtual int initialize (void);
	virtual int ready (int trk, DWORD chunkSize);

	virtual int rewind (void);

	virtual int eventRead (MMidEvent **event);
	virtual int eventInc (MMidEvent *event);

#ifdef M_MID_INCLUDE_LOOPPLAY
	virtual void loopStartSet (void);
	virtual int loopEndSet (void);
#endif // M_MID_INCLUDE_LOOPPLAY

	int noteOnsCount (WORD *pNoteOns);

	// void nameGet (LPTSTR str, int length); // show track names
#ifdef BSL_DEBUG
	void nameShow (void); // show track names
	void statusShow (void);
#endif // BSL_DEBUG

protected:

	int statusRead (BYTE *status, BYTE running);
 
private:

	int property (void);
	DWORD varLenRead (DWORD *value);

	uintptr_t dwPosition;
	uintptr_t dwPositionStart;
	BYTE byRunning;

	MMidEvent tEvent;

#ifdef M_MID_INCLUDE_LOOPPLAY
	DWORD dwPositionAtLoopStart;
	DWORD dwReadtickAtLoopStart; // tick at loop start
	BYTE byRunningAtLoopStart; // running status at lp st 
	BOOL bIsFinishedAtLoopStart; // finish flag at loop st 
#endif // M_MID_INCLUDE_LOOPPLAY
};

#endif /* __INCmMidReaderSmfTrkh */

