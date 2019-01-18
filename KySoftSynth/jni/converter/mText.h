/* mText.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmTexth
#define __INCmTexth

/* includes */

#include <stdio.h>
#include <string.h>
#if M_WINDOWS
#include <stdlib.h>
#include <tchar.h>
#endif // M_WINDOWS

/* defines */

#if M_WINDOWS
#if M_WINDOWSCE
#define _TEXT(string) TEXT(string)
#define _MAX_DRIVE 3 // max. length of drive component
#define _MAX_DIR 256 // max. length of path component
#define _MAX_FNAME 256 // max. length of file name component
#define _MAX_EXT 256 // max. length of extension component
#endif // M_WINDOWSCE
#else // M_WINDOWS
#define _TEXT(string) string
#ifndef _MAX_PATH
#define _MAX_PATH 260 // max. length of full pathname
#endif // _MAX_PATH
#define _MAX_DRIVE  3 // max. length of drive component
#define _MAX_DIR 256 // max. length of path component
#define _MAX_FNAME 256 // max. length of file name component
#define _MAX_EXT 256 // max. length of extension component
#endif // M_WINDOWS

/* typedefs */

#if !M_WINDOWS
typedef char *LPSTR;
typedef char TCHAR;
typedef TCHAR * LPTSTR;
typedef const TCHAR *LPCTSTR;
#endif // M_WINDOWS

/* function decralations */

TCHAR *_tcsncpy_(TCHAR *strDest, const TCHAR *strSource, size_t count);

#if !M_WINDOWS
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
#endif // M_WINDOWS

#endif /* __INCmTexth */

