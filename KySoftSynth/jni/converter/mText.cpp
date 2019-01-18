/* mText.cpp - */

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
#include "mText.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*******************************************************************************
*
* _tcsncpy_ -
*
* RETURNS:
* TCHAR *
*/
TCHAR *_tcsncpy_
	(
	TCHAR *strDest, 
	const TCHAR *strSource, 
	size_t count
	)
{
	_tcsncpy (strDest, strSource, count);
	strDest[count] = _TEXT('\0');
	return strDest;
}
