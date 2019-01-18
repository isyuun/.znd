/*
 *  BSLDebug.h
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLDebug.h
	@brief
	デバッグ用ユーティリティ
*/

#ifndef __INCBSLDebugh
#define __INCBSLDebugh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

/* defines */

/* typedefs */

/* globals */

/* loclas */

/* function declarations */

#ifdef BSL_DEBUG
void bslTrace (LPCTSTR format, ...);
#endif /* BSL_DEBUG */

#ifdef BSL_DEBUG_LOG
void bslLog (LPCTSTR filename, LPCTSTR format, ...);
#endif /* BSL_DEBUG_LOG */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLDebugh */

