/*
 *  BSLWavBuff.c
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavBuff.c
	@brief
	Wave Buffer制御オブジェクト
*/

/* includes */

#include "BSL.h"

#if !BSL_MAC

#include "BSLMem.h"
#include "BSLWav.h"
#include "BSLWavBuff.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* function declarations */


/*---------------------------------------------------------------------------*/
/*!
	@brief
	バッファの更新を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param buffers
	バッファ数
	@param bufferSize
	バッファ長
	@retval
	BSLErr
*/
static BSLErr _resize
	(
	BSLWavBuff *obj,
	int buffers,
	long bufferSize
	)
{
	BOOL state;

	if (buffers == obj->nBuffers && bufferSize == obj->lBufferSize) {
		return BSL_OK;
	}

	state = obj->bAllocated;
	if (state) {
		obj->freeBuffer (obj);
	}

	if (buffers <= 0) {
		obj->nBuffers = 1;
	}
	else if (buffers > BSL_WAV_BUFFERS_MAX) {
		obj->nBuffers = BSL_WAV_BUFFERS_MAX;
	}
	else {
		obj->nBuffers = buffers;
	}
	obj->lBufferSize = bufferSize;

	if (state) {
		return (obj->allocateBuffer (obj));
	}

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	バッファの取得を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BSLErr
*/
static BSLErr _allocateBuffer
	(
	BSLWavBuff *obj
	)
{
	long i;

	if (obj->bAllocated) {
		return BSL_OK;
	}

	obj->freeBuffer (obj);

#ifdef BSL_DEBUG
	/* bslTrace (_TEXT("WAV : allocate buffer (%d * %ld[byte])"), obj->nBuffers, obj->lBufferSize); */
#endif /* BSL_DEBUG */

#if BSL_WINDOWS
	obj->hHeap = HeapCreate (0, 0, 0);
	if (!obj->hHeap) {
		return (BSL_ERR_MEM_HEAP_CREATE);
	}

	for (i = 0; i < obj->nBuffers; ++i) {
		obj->pHdr[i] = (LPWAVEHDR) HeapAlloc (obj->hHeap, HEAP_ZERO_MEMORY, sizeof (WAVEHDR));
		if (!obj->pHdr[i]) {
			obj->pHdr[i] = NULL;
			return BSL_ERR_MEM_ALLOC;
		}
		obj->pBuff[i] = (LPSTR) HeapAlloc (obj->hHeap, HEAP_ZERO_MEMORY, obj->lBufferSize);
		if (!obj->pBuff[i]) {
			obj->pBuff[i] = NULL;
			return BSL_ERR_MEM_ALLOC;
		}
	}
#elif BSL_LINUX || BSL_ANDROID
	for (i = 0; i < obj->nBuffers; i++) {
		obj->pHdr[i] = (BSL_WAV_BUFFER *) bslMemAlloc (sizeof (BSL_WAV_BUFFER));
		if (!obj->pHdr[i]) {
			return BSL_ERR_MEM_ALLOC;
		}
		memset (obj->pHdr[i], 0, sizeof (BSL_WAV_BUFFER));

		#ifdef BSL_WAV_IO_FLOAT
		size_t sampleSize = sizeof(BSLWavIOData);
		#else /* BSL_WAV_IO_FLOAT */
		size_t sampleSize = sizeof(BSLWavIOData16);
		#endif /* BSL_WAV_IO_FLOAT */
		obj->pHdr[i]->pData = (short *) bslMemAlloc (sampleSize * obj->lBufferSize);
		if (!obj->pHdr[i]->pData) {
			return BSL_ERR_MEM_ALLOC;
		}
	}
#endif

	obj->bAllocated = TRUE;
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	バッファの開放を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BSLErr
*/
static BSLErr _freeBuffer
	(
	BSLWavBuff *obj
	)
{
	long i;

	for (i = 0; i < BSL_WAV_BUFFERS_MAX; ++i) {
#if BSL_WINDOWS
		if (obj->pHdr[i]) {
			HeapFree (obj->hHeap, 0, obj->pHdr[i]);
			obj->pHdr[i] = NULL;
		}
		if (obj->pBuff[i]) {
			HeapFree (obj->hHeap, 0, obj->pBuff[i]);
			obj->pBuff[i] = NULL;
		}
#elif BSL_LINUX || BSL_ANDROID
		if (obj->pHdr[i]) {
			if (obj->pHdr[i]->pData) {
				bslMemFree (obj->pHdr[i]->pData);
				obj->pHdr[i]->pData = NULL;
			}
			bslMemFree (obj->pHdr[i]);
			obj->pHdr[i] = NULL;
		}
#endif
	}

#if BSL_WINDOWS
	if (obj->hHeap) {
		if (!HeapDestroy (obj->hHeap)) {
			return (BSL_ERR_MEM_HEAP_DESTROY);
		}
		obj->hHeap = 0;
	}
#endif /* BSL_WINDOWS */

	obj->bAllocated = FALSE;
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	バッファサイズの取得を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	バッファサイズ[Byte]
*/
static long _getBufferSize
	(
	BSLWavBuff *obj
	)
{
	return obj->lBufferSize;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	バッファ数の取得を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	バッファ数
*/
static int _getBuffers
	(
	BSLWavBuff *obj
	)
{
	return obj->nBuffers;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
void bslWavBuff
	(
	BSLWavBuff *obj
	)
{
	int i;

	memset (obj, 0, sizeof (BSLWavBuff));

	obj->nBuffers = BSL_WAV_BUFFERS;
	obj->lBufferSize = BSL_WAV_BLOCK_SIZE * (long) BSL_WAV_CHANNELS * (long) BSL_WAV_BITS_PER_SAMPLE / 8L;

	obj->bAllocated = FALSE;
#if BSL_WINDOWS
	obj->hHeap = NULL;
#endif /* BSL_WINDOWS */

	for (i = 0; i < BSL_WAV_BUFFERS_MAX; ++i) {
		obj->pHdr[i] = NULL;
#if BSL_WINDOWS
		obj->pBuff[i] = NULL;
#endif /* BSL_WINDOWS */
	}

	obj->resize = _resize;
	obj->allocateBuffer = _allocateBuffer;
	obj->freeBuffer = _freeBuffer;
	obj->getBufferSize = _getBufferSize;
	obj->getBuffers = _getBuffers;
}

/*---------------------------------------------------------------------------*/
void bslWavBuffExit
	(
	BSLWavBuff *obj
	)
{
	if (obj) {
		obj->freeBuffer (obj);
	}
}

#endif /* !BSL_MAC */