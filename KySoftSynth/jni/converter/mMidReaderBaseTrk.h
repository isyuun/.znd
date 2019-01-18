/* mMidReaderBaseTrk.h - */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidReaderBaseTrkh
#define __INCmMidReaderBaseTrkh

/* include */

/* defines */

#define M_MID_TRACKNAME_DEF _TEXT("untitled")

/* typedefs */

/* class */

class MMidReaderBase;

class MMidReaderBaseTrk
{
public:

	MMidReaderBaseTrk (MMidReaderBase *parent);
	virtual ~MMidReaderBaseTrk ();

	MMidReaderBase *parent (void);
  void parentSet (MMidReaderBase *parent);

	virtual int initialize (void);
	virtual int ready (int trk, DWORD chunkSize);

	virtual int rewind (void);
	virtual void close (void);
	virtual BOOL isFinished (void);

	virtual int eventRead (MMidEvent **event) = 0;
	virtual int eventInc (MMidEvent *event) = 0;
	DWORD readtickGet (void);

#ifdef M_MID_INCLUDE_LOOPPLAY
	virtual void loopStartSet (void) = 0;
	virtual int loopEndSet (void) = 0;
#endif // M_MID_INCLUDE_LOOPPLAY

	BYTE portGet (void);
	void portSet (BYTE port = kMidPortNull);
	BYTE channelGet (void);
	void channelSet (BYTE ch = kMidChNull);
#ifdef M_MID_INCLUDE_OPTSW
	BYTE optionSwitchGet (void);
	void optionSwitchSet (BYTE optSw = kMidOptionSwNull);
#endif // M_MID_INCLUDE_OPTSW
	void orgTrkNumberSet (int trk);
	int orgTrkNumberGet (void);

	void nameSet (LPCTSTR str);
	void nameGet (LPTSTR str, int length);
  LPCTSTR nameGet (void);
#ifdef BSL_DEBUG
	virtual void nameShow (void) = 0;
	virtual void statusShow (void) = 0;
#endif // BSL_DEBUG

	enum {
		kMidPortNull = 0xFF,
		kMidChNull = 0xFF,
#ifdef M_MID_INCLUDE_OPTSW
		kMidOptionSwNull = ' ', // track/ch option sw def
#endif // M_MID_INCLUDE_OPTSW
		kNameLength = 128,
	};

protected:

	DWORD dwReadtick; // sum of ticks
	BOOL bIsFinished; // read finish flag
	int iOrgTrk; // original track number

	MMidReaderBase *pParent;
	DWORD dwChunkSize; // chunk size
	TCHAR cName[kNameLength]; // track name
 
private:

	BYTE byPort; // output midi port
	BYTE byCh; // output midi channel 
#ifdef M_MID_INCLUDE_OPTSW
	BYTE byOptSw; // track option switch
#endif // M_MID_INCLUDE_OPTSW
};

#endif /* __INCmMidReaderBaseTrkh */

