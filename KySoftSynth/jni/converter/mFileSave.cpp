/* mFileSave.cpp - */

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
#include "mFile.h"
#include "mFileSave.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MFileSave::MFileSave - 
*
* RETURNS:
* void
*/
MFileSave::MFileSave ()
{
}

/*******************************************************************************
*
* MFileSave::~MFileSave - 
*
* RETURNS:
* void
*/
MFileSave::~MFileSave ()
{
}

#pragma mark -
/*******************************************************************************
*
* MFileSave::fileSave - 
*
* RETURNS:
* error code
*/
int MFileSave::fileSave
	(
	LPCTSTR file, 
	BOOL invisible,
	void *parameter
	)
{
	int err = OK;

	MFileRef fileRef;
	curPosition = 0UL;

#if M_WINDOWS

	DWORD dwFlagsAndAttributes = FILE_ATTRIBUTE_NORMAL;
	if (invisible) {
		dwFlagsAndAttributes = FILE_ATTRIBUTE_HIDDEN;
	}

	fileRef = ::CreateFile (file, GENERIC_WRITE, FILE_SHARE_WRITE, NULL,
							CREATE_ALWAYS, dwFlagsAndAttributes, NULL);
	if (fileRef != INVALID_HANDLE_VALUE) {
		err = save_ (fileRef, parameter);
		::CloseHandle (fileRef);
	}
	else {
		err = kErrFileCreate;
	}

#else

	(void) invisible;

	if ((fileRef = ::fopen (file, "wb")) != NULL) {
		err = save_ (fileRef, parameter);
		::fclose (fileRef);
	}
	else {
		err = kErrFileCreate;
	}

#endif

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MFileSave::writeposition - 
*
* RETURNS:
* DWORD
*/
DWORD MFileSave::writeposition (void)
{
	return curPosition;
}

/*******************************************************************************
*
* MFileSave::write - 
*
* RETURNS:
* error code
*/
int MFileSave::write
	(
	MFileRef fileRef, 
	void *data, 
	DWORD size
	)
{
#if M_WINDOWS
	DWORD writeSize;
	::WriteFile (fileRef, data, size, &writeSize, NULL);
	if (writeSize != size) {
		return (kErrFileWrite);
	}
#else
	if (fwrite (data, 1, size, fileRef) != size) {
		return (kErrFileWrite);
	}
#endif

	curPosition += size;
	return (OK);
}

/*******************************************************************************
*
* MFileSave::writeByte - 
*
* RETURNS:
* error code
*/
int MFileSave::writeByte
	(
	MFileRef fileRef,
	BYTE data
	)
{
	return write (fileRef, &data, 1);
}

/*******************************************************************************
*
* MFileSave::writeseek - 
*
* RETURNS:
* error code
*/
int MFileSave::writeseek
	(
	MFileRef fileRef,
	long offset, 
	int mode
	)
{
#if M_WINDOWS
	DWORD method = FILE_BEGIN;
#else
	int method = SEEK_SET;
#endif

	switch (mode) {
	case MFile::kStart:
#if M_WINDOWS
		method = FILE_BEGIN;
#else
		method = SEEK_SET;
#endif
		if (offset < 0L) {
			offset = 0L;
		}
		curPosition = (DWORD) offset;
		break;
	case MFile::kCurrent:
#if M_WINDOWS
		method = FILE_CURRENT;
#else
		method = SEEK_CUR;
#endif
		if (offset >= 0L) {
			curPosition += (DWORD) offset;
		}
		else {
			curPosition -= (DWORD) (-offset);
		}
		break;
	case MFile::kEnd:
#if 1
		return kErrParamWrong;
#else
	#if M_WINDOWS
		method = FILE_END;
	#else
		method = SEEK_END;
	#endif
		if (offset > 0L) {
			offset = 0L;
		}
		curPosition = dwFileSize - (DWORD) (-offset);
#endif
		break;
	}

#if M_WINDOWS
	::SetFilePointer (fileRef, offset, NULL, method);
#else
	::fseek (fileRef, offset, method);
#endif

	return (OK);
}

