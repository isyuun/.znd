/* mMidReaderSmf.h - smf class */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidReaderSmfh
#define __INCmMidReaderSmfh

/* include */

#ifdef M_MID_INCLUDE_INFINITE_TRACK
#include <vector>
#endif // M_MID_INCLUDE_INFINITE_TRACK

#include "mMidReaderBase.h"
#include "mMidReaderSmfTrk.h"

/* defines */

#define MThd_MARKER MAKE_MARKER('M','T','h','d')
#define MTrk_MARKER MAKE_MARKER('M','T','r','k')

/* typedefs */

#ifdef M_MID_INCLUDE_INFINITE_TRACK
typedef std::vector<MMidReaderSmfTrk *> MMidReaderSmfTrkType; 
#endif // M_MID_INCLUDE_INFINITE_TRACK

/* class */

class MMidReaderSmf : public MMidReaderBase
{
public:

	MMidReaderSmf ();
	virtual ~MMidReaderSmf ();

	virtual int ready
		(
		int formatTypes = 0, // number of supported formats
		int *formatTypeList = NULL, // supported formats
		int divisionType = 0, // number of supported divisions
		int *divisionTypeList = NULL,	// supported divisions
		void *user = NULL // other parameter in ascii
		);

	virtual int rewind (DWORD startReadtick = 0UL);

	virtual MMidReaderBaseTrk *trackGet (int index);

#ifdef M_MID_INCLUDE_LOOPPLAY
	int loopStartSet (DWORD dwReadtick); // set loop start position
	int loopEndSet (DWORD dwReadtick); // set loop end position
#endif // M_MID_INCLUDE_LOOPPLAY

#ifdef BSL_DEBUG
	virtual void statusShow (void); // show status
#endif // BSL_DEBUG

	virtual void error (int num, void *param1 = NULL, void *param2 = NULL, void *param3 = NULL);

protected:

	virtual int trackSelect (MMidReaderSmfTrk *trk, void *user, BOOL *useTrk);

	enum {
		kFormat0 = 0, 
		kFormat1,
		kFormat2,
	};

private:

#ifdef M_MID_INCLUDE_LOOPPLAY
	DWORD dwReadtickAtLoopStart; // tick at loop start
	DWORD dwReadtickAtLoopEnd; // tick at loop end
	int iRemainedTracksAtLoopStart; // remained trks at loop st
	DWORD dwLoops; // number of loops
	DWORD dwLoopLengthUnit; // loop duration
	DWORD dwLoopLength; // total loop duration
#endif // M_MID_INCLUDE_LOOPPLAY

#ifdef M_MID_INCLUDE_INFINITE_TRACK
	void trackDeleteAll (void);
	MMidReaderSmfTrkType vTrk;
#else // M_MID_INCLUDE_INFINITE_TRACK
	MMidReaderSmfTrk *pTrk[kMidSmfTracks]; // track parameters address
#endif // M_MID_INCLUDE_INFINITE_TRACK
};

#endif /* __INCmMidReaderSmfh */

