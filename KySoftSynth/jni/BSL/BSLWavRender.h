/*
 *  BSLWavRender.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavRender.h
	@brief
	Wave Rendering制御オブジェクト
*/

#ifndef __INCBSLWavRenderh
#define __INCBSLWavRenderh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLWav.h"

/* defines */

/*! レンダリング処理ステータス */
typedef enum  {
	/*! 未完了 (カレントバッファフル等による一時終了) */
	BSL_WAV_RENDER_STATE_FULL,
	/*! 終了 */
	BSL_WAV_RENDER_STATE_COMPLETED,
	/*! エラー停止 */
	BSL_WAV_RENDER_STATE_ERROR,
} BSL_WAV_RENDER_STATE;

/* typedefs */

/*!
	Wave Rendering処理基本制御オブジェクト
*/
typedef struct BSLWavRender
{
	/*! ユーザーデータを設定するメソッド */
	void *(*getUser) (struct BSLWavRender *obj);
	/*! ユーザーデータを取得するメソッド */
	void (*setUser) (struct BSLWavRender *obj, void *user);

	/*! レンダリング処理を行うメソッド */
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	BSL_WAV_RENDER_STATE (*render) (struct BSLWavRender *obj, BSLWavIOData *data, long sampleFrames, long *readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	BSL_WAV_RENDER_STATE (*render) (struct BSLWavRender *obj, BSLWavIOData *data, long sampleFrames);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */

	/*! 最大ブロックサイズ[sample]を設定するメソッド */
	BSLErr (*setBlockSize) (struct BSLWavRender *obj, long blockSize);
	/*! 最大ブロックサイズ[sample]を取得するメソッド */
	long (*getBlockSize) (struct BSLWavRender *obj);

	/*! レンダリングバッファを設定するメソッド */
	void (*setBuffer) (struct BSLWavRender *obj, BSLWavIOData *data);
	/*! レンダリングバッファを取得するメソッド */
	BSLWavIOData *(*getBuffer) (struct BSLWavRender *obj);

	/*! 最大ブロックサイズ[sample] */
	long lBlockSize;
	/*! レンダリングバッファ */
	BSLWavIOData *pBuffer;
	/*! ユーザデータ */
	void *pUser;
} BSLWavRender;

#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	typedef BSL_WAV_RENDER_STATE (*BSL_WAV_RENDER) (BSLWavRender *obj, BSLWavIOData *data, long sampleFrames, long *readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	typedef BSL_WAV_RENDER_STATE (*BSL_WAV_RENDER) (BSLWavRender *obj, BSLWavIOData *data, long sampleFrames);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */

/* globals */

/* locals */

/* function declarations */

void bslWavRender (BSLWavRender *obj, BSL_WAV_RENDER render, void *user);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavRenderh */
