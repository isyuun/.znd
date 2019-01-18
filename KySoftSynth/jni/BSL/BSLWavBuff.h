/*
 *  BSLWavBuff.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavBuff.h
	@brief
	Wave Buffer制御オブジェクト
*/

#ifndef __INCBSLWavBuffh
#define __INCBSLWavBuffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLWav.h"

#if BSL_WINDOWS
	#include <mmsystem.h>
#endif

#if !BSL_MAC

/* defines */

/* typedefs */

#if BSL_LINUX || BSL_ANDROID
typedef struct
{
	#ifdef BSL_WAV_IO_FLOAT
	BSLWavIOData *pData;
	#else /* BSL_WAV_IO_FLOAT */
	BSLWavIOData16 *pData;
	#endif /* BSL_WAV_IO_FLOAT */
} BSL_WAV_BUFFER;
#endif

typedef struct  BSLWavBuff
{
	BSLErr (*resize) (struct BSLWavBuff *obj, int num, long bufferSize);
	BSLErr (*allocateBuffer) (struct BSLWavBuff *obj);
	BSLErr (*freeBuffer) (struct BSLWavBuff *obj);

	long (*getBufferSize) (struct BSLWavBuff *obj);
	int (*getBuffers) (struct BSLWavBuff *obj);

#if BSL_WINDOWS
	LPWAVEHDR pHdr[BSL_WAV_BUFFERS_MAX];
#elif BSL_LINUX || BSL_ANDROID
	BSL_WAV_BUFFER *pHdr[BSL_WAV_BUFFERS_MAX];
#endif

#if BSL_WINDOWS
	HANDLE hHeap;
	LPSTR pBuff[BSL_WAV_BUFFERS_MAX];
#endif /* BSL_WINDOWS */

	int nBuffers;
	long lBufferSize;
	BOOL bAllocated;
} BSLWavBuff;

/* globals */

/* locals */

/* function declarations */

void bslWavBuff (BSLWavBuff *obj);
void bslWavBuffExit (BSLWavBuff *obj);

#endif /* !BSL_MAC */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavBuffh */
