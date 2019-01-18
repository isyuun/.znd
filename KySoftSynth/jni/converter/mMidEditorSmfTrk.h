/* midEditorSmfTrk.h - smf defines */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidEditorSmfTrkh
#define __INCmMidEditorSmfTrkh

/* include */

#include <vector>
#include <string>

#include "mFileMem.h"
#include "mMidReaderSmfTrk.h"

/* defines */

/* typedefs */

#ifndef MMidReaderBaseTrkEventType
typedef std::vector<MMidEvent *> MMidReaderBaseTrkEventType; 
#endif

/* class */

class MMidReaderSmf;

class MMidEditorSmfTrk : public MMidReaderSmfTrk 
{
public:

	MMidEditorSmfTrk (MMidReaderBase *parent);
	virtual ~MMidEditorSmfTrk ();

	virtual int initialize (void);

	virtual int rewind (void);
	int save (MFileRef fileRef, BOOL useRunningStatus, BOOL noteOffIs9nH);

	virtual int eventRead (MMidEvent **event);
	virtual int eventInc (MMidEvent *event);
	
	DWORD eventNumGet (void);
	MMidEvent *eventGet (DWORD index);
	int eventAdd (DWORD tick, int size, MMidEvent **event);
	int eventDelete (DWORD index);
	int eventDeleteAll (void);
  int eventDeleteInRange (DWORD fromTick, DWORD toTick);

	virtual int eventLoadAll (void); // from overrided MidSmfTrkReader class
	virtual int eventLoadAll (MMidReaderSmfTrk *reader); // from other MidSmfTrkReader class
  
protected:

private:

	int varLenWrite (DWORD value, MFileRef fileRef);

	MMidReaderBaseTrkEventType vEvent;
	MMidReaderBaseTrkEventType::iterator vEventIterator;
};

#endif /* __INCmMidEditorSmfTrkh */
