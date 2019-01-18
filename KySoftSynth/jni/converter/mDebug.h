/* mDebug.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmDebugh
#define __INCmDebugh

/* includes */

#include "mText.h"

/* defines */

/* typedefs */

/* class */

/* function declarations */

#ifdef BSL_DEBUG
void mPrintf (LPCTSTR format, ...);
void mError  (LPCTSTR format, ...);
#endif // BSL_DEBUG

void mLog (LPCTSTR filename, LPCTSTR format, ...);

#endif /* __INCmDebugh */

