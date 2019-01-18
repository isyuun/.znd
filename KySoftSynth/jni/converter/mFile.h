/* mFile.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmFileh
#define __INCmFileh

/* includes */

#include "mDefine.h"
#include "mText.h"
#if M_WINDOWS
	#include <windows.h>
#else // M_WINDOWS
	#include <stdio.h>
#endif // M_WINDOWS

/* defines */

#ifdef M_BIGENDIAN
#define MAKE_MARKER(a,b,c,d) (((a)<<24)|((b)<<16)|((c)<<8)|(d))
#else //
#define MAKE_MARKER(a,b,c,d) ((a)|((b)<<8)|((c)<<16)|((d)<<24))
#endif

/* typedefs */

#if M_WINDOWS
typedef HANDLE MFileRef;
#else
typedef FILE * MFileRef;
#endif

/* class */

class MFile {

public:

	MFile ();
	virtual ~MFile ();

	virtual int fileOpen (LPCTSTR file);
	virtual void fileClose (void);

	virtual int fileQuery (LPCTSTR file) { return (OK); }

	BOOL isReady (void) { return bReady; }

	void maxFileSizeSet (DWORD max) { dwFileSizeMax = max; }	
	void fileSizeSet (DWORD value) { dwFileSize = value; }
	virtual DWORD fileSizeGet (void) { return dwFileSize; }

	virtual int read (void *data, DWORD size);
	virtual int readseek (long offset, int mode);
	virtual uintptr_t readposition (void);

	void swap (void *data, int size);
	
	enum {
		kStart = 0,
		kCurrent,
		kEnd
	};

protected:

	void readySet (void) { bReady = true; }
	void readyUnSet (void) { bReady = false; }

	MFileRef tFileRef;
	BOOL bOpened;

private:

	DWORD dwFileSizeMax;
	DWORD dwFileSize;

	DWORD curPosition;

	BOOL bReady;
};

/* function declarations */

#endif /* __INCmFileh */

