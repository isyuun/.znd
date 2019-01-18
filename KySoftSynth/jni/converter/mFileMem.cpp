/* mFileMem.cpp - */

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
#include "mFileMem.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MFileMem::MFileMem - 
*
* RETURNS:
* void
*/
MFileMem::MFileMem (void)
{
	pData = NULL;
	bAllocated = false;
}

/*******************************************************************************
*
* MFileMem::~MFileMem - 
*
* RETURNS:
* void
*/
MFileMem::~MFileMem ()
{
	fileClose ();
}

/*******************************************************************************
*
* MFileMem::memoryGet - 
*
* RETURNS:
* error code
*/
int MFileMem::memoryGet
	(
	DWORD fileSize
	)
{
	memoryFree ();

	___try {
		pData = new char[fileSize];
	}
	___catch {
		pData = NULL;
		return (kErrMemAlloc);
	}

	bAllocated = true;

	return (OK);
}

/*******************************************************************************
*
* MFileMem::memoryFree - 
*
* RETURNS:
* error code
*/
int MFileMem::memoryFree (void)
{
	if (bAllocated && pData){
		delete [] pData;
		pData = NULL;
	}
	bAllocated = false;

	return (OK);
}

/*******************************************************************************
*
* MFileMem::fileOpen - 
*
* RETURNS:
* error code
*/
int MFileMem::fileOpen
	(
	LPCTSTR file
	)
{
	int err = OK;

	fileClose ();
	
	if (err == OK) {
		err = MFile::fileOpen (file);
	}

	if (err == OK) {
		err = memoryGet (fileSizeGet ());
	}

	if (err == OK) {
		err = MFile::read (address (), fileSizeGet ());
	}

	if (err == OK) {
		readseek (0L, kStart);
	}

	MFile::fileClose ();

	return (err);
}

/*******************************************************************************
*
* MFileMem::fileClose - 
*
* RETURNS:
* void
*/
void MFileMem::fileClose (void)
{
	memoryFree ();
}

/*******************************************************************************
*
* MFileMem::memoryOpen - 
*
* RETURNS:
* error code
*/
int MFileMem::memoryOpen
	(
	char *data,
	DWORD dataSize
	)
{
	int err = OK;

	fileClose ();
	memoryFree ();
	
	if (data) {
		pData = data;
		fileSizeSet (dataSize);
		readseek (0L, kStart);
	}
	else {
		err = kErrParamWrong;
	}

	return (err);
}

#pragma mark -
/*******************************************************************************
*
* MFileMem::read - 
*
* RETURNS:
* error code
*/
int MFileMem::read 
	(
	void *data, 
	DWORD size
	)
{
	if (!pData) {
		return (kErrFileNotReady);
	}
	
	if (curPosition < address ()) {
#ifdef BSL_DEBUG
		mError (_TEXT("FILE: invalid address"));
#endif // BSL_DEBUG
		return kErrFileRead;
	}
	else if (0UL < fileSizeGet ()) {
		if ((address () + fileSizeGet ()) <= curPosition) {
#ifdef BSL_DEBUG
			mError (_TEXT("FILE: invalid address"));
#endif // BSL_DEBUG
			return kErrFileRead;
		}
	}

	::memcpy (data, curPosition, size);
	curPosition += size;
	return (OK);
}

/*******************************************************************************
*
* MFileMem::readseek - 
*
* RETURNS:
* error code
*/
int MFileMem::readseek
	(
	long offset, 
	int mode
	)
{
	if (!pData) {
		return (kErrFileNotReady);
	}

	switch (mode) {
	case kStart:
		curPosition = address () + offset;
		break;
	case kCurrent:
		curPosition += offset;
		break;
	case kEnd:
		curPosition = address () + fileSizeGet () + offset;
		break;
	default:
#ifdef BSL_DEBUG
		mError (_TEXT("FILE: unknown seek mode"));
#endif // BSL_DEBUG
		return kErrParamWrong;
	}	

	if (curPosition < address ()) {
#ifdef BSL_DEBUG
		mError (_TEXT("FILE: invalid address"));
#endif // BSL_DEBUG
		return kErrFileSeek;
	}
	else if (0UL < fileSizeGet ()) {
		if ((address () + fileSizeGet ()) < curPosition) {
#ifdef BSL_DEBUG
			mError (_TEXT("FILE: invalid address"));
#endif // BSL_DEBUG
			return kErrFileSeek;
		}
	}
	
	return (OK);
}

/*******************************************************************************
*
* MFileMem::readposition - 
*
* RETURNS:
* DWORD
*/
uintptr_t MFileMem::readposition (void)
{
	return ((uintptr_t) curPosition - (uintptr_t) address ());
}

