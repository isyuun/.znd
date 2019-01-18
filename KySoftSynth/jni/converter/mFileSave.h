/* mFileSave.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmFileSaveh
#define __INCmFileSaveh

/* includes */

#include "mFile.h"

/* defines */

/* typedefs */

/* class */

class MFileSave
{
public:

	MFileSave ();
	virtual ~MFileSave ();

	virtual int fileSave (LPCTSTR file, BOOL invisible = false, void *parameter = NULL);

	virtual DWORD writeposition (void);
	int write (MFileRef fileRef, void *data, DWORD size);
	int writeByte (MFileRef fileRef, BYTE data);
	int writeseek (MFileRef fileRef, long offset, int mode);

protected:

	virtual int save_ (MFileRef fileRef, void *parameter = NULL) { (void) fileRef; (void) parameter; return OK; } 

private:

	DWORD curPosition;
};

#endif /* __INCmFileSaveh */

