/* mMidReaderBaseTrkEvent.h - */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidReaderBaseTrkEventh
#define __INCmMidReaderBaseTrkEventh

/* include */

#include <vector>
#include "mMidReaderBaseTrk.h"

/* defines */

/* typedefs */

#ifndef MMidReaderBaseTrkEventType
typedef std::vector<MMidEvent *> MMidReaderBaseTrkEventType; 
#endif

/* class */

class MMidReaderBaseTrkEvent : public MMidReaderBaseTrk
{
public:

	MMidReaderBaseTrkEvent (MMidReaderBase *parent);
	virtual ~MMidReaderBaseTrkEvent ();

	virtual int initialize (void);
	virtual int rewind (void);

	virtual int eventRead (MMidEvent **event);
	virtual int eventInc (MMidEvent *event);

	DWORD eventNumGet (void);
	MMidEvent *eventGet (DWORD index);
	int eventAdd (DWORD tick, int size, MMidEvent **event);
	int eventDelete (DWORD index);
	int eventDeleteAll (void);

protected:

private:

	MMidReaderBaseTrkEventType vEvent;
	MMidReaderBaseTrkEventType::iterator vEventIterator;
};

#endif /* __INCmMidBaseTrkEventh */

	