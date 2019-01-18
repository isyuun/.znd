/* mDebug.cpp - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

/* includes */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include "mDefine.h"
#include "mDebug.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

#ifdef BSL_DEBUG
/*******************************************************************************
*
* mPrintf - 
*
* RETURNS:
* void
*/
void mPrintf
	(
	LPCTSTR format, ...
	)
{
	va_list argptr;
	TCHAR buf[256];
	
	va_start (argptr, format);
#if M_WINDOWS
	_vstprintf (buf, format, argptr);
#else // M_WINDOWS
	vsprintf (buf, format, argptr);
#endif // M_WINDOWS
	va_end (argptr);

#if M_WINDOWS
	::OutputDebugString (buf);
	::OutputDebugString (_TEXT("\n"));
#else
	_tprintf (_TEXT("%s\n"), buf);
#endif
}

/*******************************************************************************
*
* mError - 
*
* RETURNS:
* void
*/
void mError
	(
	LPCTSTR format, ...
	)
{
	va_list argptr;
	TCHAR buf[256];
	
	va_start (argptr, format);
#if M_WINDOWS
	_vstprintf (buf, format, argptr);
#else // M_WINDOWS
	vsprintf (buf, format, argptr);
#endif // M_WINDOWS
	va_end (argptr);

#if M_WINDOWS
	::OutputDebugString (_TEXT("- ERROR -\n"));
	::OutputDebugString (buf);
	::OutputDebugString (_TEXT("\n"));
#else
	_tprintf ("- ERROR -\n");
	_tprintf (_TEXT("%s\n"), buf);
#endif
}
#endif // BSL_DEBUG

/*******************************************************************************
*
* mLog - 
*
* RETURNS:
* void
*/
void mLog
	(
	LPCTSTR filename,
	LPCTSTR format, ...
	)
{
	va_list argptr;
	TCHAR buf[1024];
	
	va_start (argptr, format);
#if M_WINDOWS
	_vstprintf (buf, format, argptr);
#else // M_WINDOWS
	vsprintf (buf, format, argptr);
#endif // M_WINDOWS
	va_end (argptr);

	FILE *fp = _tfopen (filename, _TEXT("a+t"));
	if (fp) {
		_ftprintf (fp, _TEXT("%s\n"), buf);
		fclose (fp);
	}
}
