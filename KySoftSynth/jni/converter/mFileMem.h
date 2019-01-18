/* mFileMem.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmFileMemh
#define __INCmFileMemh

/* includes */

#include "mFile.h"

/* defines */

/* typedefs */

/* class */

class MFileMem : public MFile
{
public:

	MFileMem ();
	virtual ~MFileMem ();

	virtual int fileOpen (LPCTSTR file);
	virtual void fileClose (void);

	virtual int memoryOpen (char *data, DWORD dataSize);
	virtual int memoryQuery (char *, DWORD) { return (OK); }
	virtual char *address (void) { return pData; }

	virtual int read (void *data, DWORD size);
	virtual int readseek (long offset, int mode);
	virtual uintptr_t readposition (void);

protected:

	virtual int memoryGet (DWORD fileSize);
	virtual int memoryFree (void);

private:

	char *pData;
	BOOL bAllocated;
	char *curPosition;
};

#endif /* __INCmFileMemh */
