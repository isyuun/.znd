/* mDefine.h - */

/* Copyright 1997-2xxx hk */

/* 
modification history
--------------------
*/


#ifndef __INCmDefineh
#define __INCmDefineh

#ifdef _MSC_VER
	#define M_MAC 0
	#define M_WINDOWS 1
#else
	#define M_MAC 1
	#define M_WINDOWS 0
#endif

#define M_WINDOWSCE 0

/* includes */

#if M_WINDOWS
	#include <windows.h>
#endif // M_WINDOWS

#include "BSL.h"

/* typedefs */

/* defines */

#if M_MAC
	#ifdef __BIG_ENDIAN__
//		#define M_BIGENDIAN
	#endif // __BIG_ENDIAN__
#endif

#if M_WINDOWS
	#pragma warning(disable:4068)
#endif // M_WINDOWS

#if M_WINDOWSCE
	#define ___try   __try
	#define ___catch __except(1)
#else // M_WINDOWSCE
	#define ___try try
	#define ___catch catch(...)
#endif // M_WINDOWSCE

#ifndef OK
	#define OK 0
#endif
#ifndef ERR
	#define ERR (-1)
#endif
#ifndef TRUE
	#define TRUE 1
#endif
#ifndef FALSE
	#define FALSE 0
#endif

#endif /* __INCmDefineh */
