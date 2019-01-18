/*
 *  BSLText.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLText.h
	@brief
	テキスト処理
	@note
	Windowsにおける国際化対応コード記述を他プラットフォームで
	流用できるように必要な定義を行う。
*/

#ifndef __INCBSLTexth
#define __INCBSLTexth

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include <stdio.h>
#include <string.h>
#if BSL_WINDOWS
	#include <stdlib.h>
	#include <tchar.h>
#endif /* BSL_WINDOWS */

/* defines */

#if BSL_WINDOWS
	#if (defined UNICODE) || (defined _UNICODE)
		#define BSL_UNICODE
	#endif
	#if BSL_WINDOWSCE
		#define _TEXT(string) TEXT(string)
		#define _MAX_DRIVE 3 /* max. length of drive component */
		#define _MAX_DIR 256 /* max. length of path component */
		#define _MAX_FNAME 256 /* max. length of file name component */
		#define _MAX_EXT 256 /* max. length of extension component */
	#endif /* BSL_WINDOWSCE */
#else /* BSL_WINDOWS */
	#define _TEXT(string) string
	#ifndef _MAX_PATH
		#define _MAX_PATH 1024 /* enough ?*/
	#endif /* _MAX_PATH */
	#define _MAX_DRIVE  3
	#define _MAX_DIR 256
	#define _MAX_FNAME 256
	#define _MAX_EXT 256
#endif /* BSL_WINDOWS */

/* typedefs */

#if !BSL_WINDOWS
	#ifndef __TCHAR_DEFINED
		typedef char *LPSTR;
		typedef char TCHAR;
		typedef TCHAR * LPTSTR;
		typedef const TCHAR *LPCTSTR;
		#define __TCHAR_DEFINED
	#endif
#endif /* BSL_WINDOWS */

/* globals */

/* loclas */

/* function declarations */

TCHAR *_tcsncpy_(TCHAR *strDest, const TCHAR *strSource, size_t count);

#if !BSL_WINDOWS
#define _tprintf printf
#define _stprintf sprintf
#define _ftprintf fprintf
#define _tcscpy strcpy
#define _tcsncpy strncpy
#define _tcscat strcat
#define _tcscmp strcmp
#define _tcsncmp strncmp
#define _tcsstr strstr
#define _tcslen strlen
#define _tfopen fopen
#endif /* BSL_WINDOWS */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLTexth */
