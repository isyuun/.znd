/* mFile.cpp - */

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
#include "mFile.h"
#include "mError.h"
#include "mDebug.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MFile::MFile - 
*
* RETURNS:
* void
*/
MFile::MFile (void)
{
	bOpened = false;
	dwFileSize = 0UL;
	dwFileSizeMax = 0UL;
	readyUnSet ();
}

/*******************************************************************************
*
* MFile::~MFile - 
*
* RETURNS:
* void
*/
MFile::~MFile ()
{
	fileClose ();
}

#pragma mark -
/*******************************************************************************
*
* MFile::fileOpen - 
*
* RETURNS:
* error code
*/
int MFile::fileOpen
	(
	LPCTSTR file
	)
{
	fileClose ();
	
	int err = ERR;

#if M_WINDOWS

	DWORD fileSize;

	tFileRef = ::CreateFile (file, GENERIC_READ, FILE_SHARE_READ, NULL,
							OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
	if (tFileRef != INVALID_HANDLE_VALUE) {
		fileSize = ::GetFileSize (tFileRef, NULL); // get filesize
		// filesize check
		if (dwFileSizeMax > 0L && fileSize > (dwFileSizeMax * 1024UL)) {
			err = kErrFileSizeOver;
		}
		err = OK;
		dwFileSize = fileSize;
	}
	else {
		err = kErrFileOpen;
	}

#else

	tFileRef = NULL;
	long fileSize;

	// open file
	if ((tFileRef = ::fopen (file, "rb")) != NULL) {
		// get filesize
		::fseek (tFileRef, 0L, SEEK_END);
		fileSize = ::ftell (tFileRef);
		::fseek (tFileRef, 0L, SEEK_SET);
		if (fileSize <= 0L) {
			err = kErrFileSize;
		}
		else {
			// filesize check
			if (dwFileSizeMax > 0UL && (DWORD) fileSize > (dwFileSizeMax * 1024UL)) {
				err = kErrFileSizeOver;
			}
			err = OK;
			dwFileSize = (DWORD) fileSize;
		}
	}
	else {
		err = kErrFileOpen;
	}

#endif

	if (err == OK) {
		bOpened = true;
		readseek (0L, kStart);
	}
	return (err);
}

/*******************************************************************************
*
* MFile::fileClose - 
*
* RETURNS:
* void
*/
void MFile::fileClose (void)
{
	if (bOpened) {
#if M_WINDOWS
		::CloseHandle (tFileRef);
#else
		::fclose (tFileRef);
#endif
	}
	bOpened = false;
}

#pragma mark -
/*******************************************************************************
*
* MFile::read - 
*
* RETURNS:
* error code
*/
int MFile::read 
	(
	void *data, 
	DWORD size
	)
{
	if (!bOpened) {
#ifdef BSL_DEBUG
		mError (_TEXT("FILE: file not opened"));
#endif // BSL_DEBUG
		return (kErrFileRead);
	}

#if M_WINDOWS
	DWORD readSize;
	if (!::ReadFile (tFileRef, data, size, &readSize, NULL)) {
	#ifdef BSL_DEBUG
		mError (_TEXT("FILE: read error"));
	#endif // BSL_DEBUG
		return (kErrFileRead);
	}
	if (readSize != size) {
	#ifdef BSL_DEBUG
		mPrintf (_TEXT("FILE: EOF"));
	#endif // BSL_DEBUG
		return (kErrFileReadEOF);
	}
#else
	if ((DWORD) ::fread (data, 1, size, tFileRef) != size) {
		if (feof (tFileRef)) {
	#ifdef BSL_DEBUG
			mPrintf (_TEXT("FILE: EOF"));
	#endif // BSL_DEBUG
			return (kErrFileReadEOF);
		}
		else {
	#ifdef BSL_DEBUG
			mError (_TEXT("FILE: read error"));
	#endif // BSL_DEBUG
			return (kErrFileRead);
		}
	}
#endif

	curPosition += size;
	return (OK);
}

/*******************************************************************************
*
* MFile::readseek - 
*
* RETURNS:
* error code
*/
int MFile::readseek
	(
	long offset, 
	int mode
	)
{
	if (!bOpened) {
		return (kErrFileNotReady);
	}

#if M_WINDOWS
	DWORD method = FILE_BEGIN;
#else
	int method = SEEK_SET;
#endif

	switch (mode) {
	case kStart:
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
	case kCurrent:
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
	case kEnd:
#if M_WINDOWS
		method = FILE_END;
#else
		method = SEEK_END;
#endif
		if (offset > 0) {
			offset = 0L;
		}
		curPosition = dwFileSize - (DWORD) (-offset);
		break;
	}

#if M_WINDOWS
	::SetFilePointer (tFileRef, offset, NULL, method);
#else
	::fseek (tFileRef, offset, method);
#endif

	return (OK);
}

/*******************************************************************************
*
* MFile::readposition - 
*
* RETURNS:
* DWORD
*/
uintptr_t MFile::readposition (void)
{
	if (bOpened) {
		return curPosition;
	}
	else {
		return 0UL;
	}
}

#pragma mark -
/*******************************************************************************
*
* MFile::swap - 
*
* RETURNS:
* error code
*/
void MFile::swap
	(
	void *data,
	int size
	)
{
	BYTE *p = (BYTE *) data;
	BYTE temp;

	switch (size) {
	case 2:
		temp = *p;
		*p = *(p + 1);
		*(p + 1) = temp;
		break;
	case 3:
		temp = *p;
		*p = *(p + 2);
		*(p + 2) = temp;
		break;
	case 4:
		temp = *p;
		*p = *(p + 3);
		*(p + 3) = temp;
		temp = *(p + 1);
		*(p + 1) = *(p + 2);
		*(p + 2) = temp;
		break;
	}
}
