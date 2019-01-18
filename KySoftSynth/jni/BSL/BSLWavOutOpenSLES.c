/*
 *  BSLWavOutOpenSLES.c
 *
 *  Copyright (c) 2012 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavOutOpenSLES.c
	@brief
	Wave出力制御オブジェクト (OpenSLES/Android NDK)
	@description
	テスト中
*/

/* includes */

#include "BSL.h"

#if BSL_ANDROID

#include "BSLMem.h"
#include "BSLWav.h"
#include "BSLWavOutOpenSLES.h"

#include <SLES/OpenSLES_Android.h>

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
// this callback handler is called every time a buffer finishes playing
static void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
	BSLWavOut *obj = (BSLWavOut *) context;	
	BSLWavOutOpenSLES_Internal *in = (BSLWavOutOpenSLES_Internal *) obj->pInternalData;

#ifdef BSL_WAV_IO_FLOAT
	BSLWavIOData *buffer = obj->tBuff.pHdr[obj->nBufferIndex]->pData;
#else /* BSL_WAV_IO_FLOAT */
	BSLWavIOData16 *buffer = obj->tBuff.pHdr[obj->nBufferIndex]->pData;
#endif /* BSL_WAV_IO_FLOAT */
	long blockSize = obj->getBlockSize (obj);
	
	SLresult result;

	obj->buffering (obj, buffer, blockSize);

	if (obj->isPlayDone (obj)) {
		result = (*(in->bqPlayerPlay))->SetPlayState (in->bqPlayerPlay, SL_PLAYSTATE_STOPPED);
		obj->stopped (obj, BSL_OK);
	}
	else {
		result = (*(in->bqPlayerBufferQueue))->Enqueue (in->bqPlayerBufferQueue, buffer, blockSize * obj->getBytesPerFrame (obj));

		obj->nBufferIndex++;
		if (obj->nBufferIndex >= obj->getBuffers (obj)) {
			obj->nBufferIndex = 0;
		}
	}
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	初期化を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BSLErr
 */
static BSLErr _initialize
	(
	BSLWavOut *obj
	)
{
	BSLErr err = BSL_OK;
	
	if (err == BSL_OK) {
		if (obj->pInternalData == NULL) {
			obj->pInternalData = (BSLWavOutOpenSLES_Internal *) bslMemAlloc (sizeof (BSLWavOutOpenSLES_Internal));
			if (obj->pInternalData == NULL) {
				err = BSL_ERR_MEM_ALLOC;
			}
			else memset (obj->pInternalData, 0, sizeof (BSLWavOutOpenSLES_Internal));
		}
	}
	
	if (err == BSL_OK) {
		err = _bslWavOutInitialize (obj);
	}

	if (err == BSL_OK) {
		err = obj->setBuffers (obj, obj->getBuffers (obj));
	}

	if (err == BSL_OK) {
		err = obj->tBuff.allocateBuffer (&obj->tBuff);
	}
	
	return err;
}
	
/*---------------------------------------------------------------------------*/
static void _finalize
	(
	BSLWavOut *obj
	)
{
}

/*---------------------------------------------------------------------------*/
static int _getDrivers
(
 BSLWavOut *obj
 )
{
	return 1;
}

/*---------------------------------------------------------------------------*/
static void _getDriverName
(
 BSLWavOut *obj, 
 int index, 
 LPTSTR name, 
 int length
 )
{
	int drivers = obj->getDrivers (obj);
	
	if (!name) return;
	
	if (0 <= index && index < drivers) {
		_tcsncpy_(name, _TEXT("OpenSLES Default"), length);
	}
	else {
		name[0] = 0;
	}
}


#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	デバイスのOpenを行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param name
	デバイス名
	@param param1
	パラメータ1
	@param param2
	パラメータ2
	@retval
	BSLErr
 */
static BSLErr _open
	(
	BSLWavOut *obj, 
	LPCTSTR name,
	void *param1,
	DWORD param2
	)
{
	BSLErr err = BSL_OK;
    SLresult result;

	BSLWavOutOpenSLES_Internal *in = (BSLWavOutOpenSLES_Internal *) obj->pInternalData;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output (OpenSLES) open - (%d[Hz], %d[bit], %d[channel], %ld[blocksize], %d[buffers])\n"),
			  obj->getSampleRate (obj), obj->getBitsPerSample (obj), obj->getChannels (obj), obj->getBlockSize (obj), obj->getBuffers (obj));
#endif /* BSL_DEBUG */

	obj->close (obj);
	obj->nBlockedBuffers = 0L;
	
	if (err == BSL_OK) {
		if (in == NULL) err = BSL_ERR_UNDEFINED;
	}	

	if (err == BSL_OK) {
		// create engine
		result = slCreateEngine (&(in->engineObject), 0, NULL, 0, NULL, NULL);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: slCreateEngine error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// realize the engine
		result = (*(in->engineObject))->Realize (in->engineObject, SL_BOOLEAN_FALSE);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: engineObject Realize error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// get the engine interface, which is needed in order to create other objects
		result = (*(in->engineObject))->GetInterface (in->engineObject, SL_IID_ENGINE, &in->engineEngine);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: GetInterface error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// create output mix, with environmental reverb specified as a non-required interface
		result = (*(in->engineEngine))->CreateOutputMix (in->engineEngine, &in->outputMixObject, 0, NULL, NULL);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: CreateOutputMix error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// realize the output mix
		result = (*(in->outputMixObject))->Realize (in->outputMixObject, SL_BOOLEAN_FALSE);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: outputMixObject Realize error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}



	if (err == BSL_OK) {
		// configure audio source
		SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, obj->getBuffers (obj)};
#ifdef BSL_WAV_IO_FLOAT
        SLAndroidDataFormat_PCM_EX format_pcm = {
			SL_ANDROID_DATAFORMAT_PCM_EX, // formatType
			obj->getChannels (obj), // numChannels
			(SLuint32) obj->getSampleRate (obj) * 1000, // samplesPerSec
			32, // bitsPerSample
			32, // containerSize
			SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, // channelMask
			SL_BYTEORDER_LITTLEENDIAN, // endianness
			SL_ANDROID_PCM_REPRESENTATION_FLOAT
        };
#else /* BSL_WAV_IO_FLOAT */
        SLDataFormat_PCM format_pcm = {
			SL_DATAFORMAT_PCM, // formatType
			obj->getChannels (obj), // numChannels
			(SLuint32) obj->getSampleRate (obj) * 1000, // samplesPerSec
			SL_PCMSAMPLEFORMAT_FIXED_16, // bitsPerSample
			SL_PCMSAMPLEFORMAT_FIXED_16, // containerSize
			SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, // channelMask
			SL_BYTEORDER_LITTLEENDIAN // endianness
		};
#endif /* BSL_WAV_IO_FLOAT */
		SLDataSource audioSrc = {&loc_bufq, &format_pcm};

		// configure audio sink
		SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, in->outputMixObject};
		SLDataSink audioSnk = {&loc_outmix, NULL};

		// create audio player
		const SLInterfaceID ids[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
		const SLboolean req[1] = {SL_BOOLEAN_TRUE};
		result = (*(in->engineEngine))->CreateAudioPlayer (in->engineEngine, &in->bqPlayerObject, &audioSrc, &audioSnk, 1, ids, req);

		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: CreateAudioPlayer error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// realize the player
		result = (*(in->bqPlayerObject))->Realize (in->bqPlayerObject, SL_BOOLEAN_FALSE);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: bqPlayerObject Realize error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// get the play interface
		result = (*(in->bqPlayerObject))->GetInterface (in->bqPlayerObject, SL_IID_PLAY, &in->bqPlayerPlay);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: bqPlayerObject Realize error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// get the buffer queue interface
		result = (*(in->bqPlayerObject))->GetInterface (in->bqPlayerObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &in->bqPlayerBufferQueue);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: bqPlayerObject GetInterface error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}

	if (err == BSL_OK) {
		// register callback on the buffer queue
		result = (*(in->bqPlayerBufferQueue))->RegisterCallback (in->bqPlayerBufferQueue, bqPlayerCallback, obj);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: bqPlayerObject RegisterCallback error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}	

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	デバイスのクローズを行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BSLErr
 */
static BSLErr _close
	(
	BSLWavOut *obj
	)
{
	BSLWavOutOpenSLES_Internal *in = (BSLWavOutOpenSLES_Internal *) obj->pInternalData;

	if (obj->isOpened (obj)) {
		// destroy buffer queue audio player object, and invalidate all associated interfaces
		if (in->bqPlayerObject != NULL) {
			(*in->bqPlayerObject)->Destroy (in->bqPlayerObject);
			in->bqPlayerObject = NULL;
			in->bqPlayerPlay = NULL;
			in->bqPlayerBufferQueue = NULL;
		}

		// destroy output mix object, and invalidate all associated interfaces
		if (in->outputMixObject != NULL) {
			(*in->outputMixObject)->Destroy (in->outputMixObject);
			in->outputMixObject = NULL;
		}

		// destroy engine object, and invalidate all associated interfaces
		if (in->engineObject != NULL) {
			(*in->engineObject)->Destroy (in->engineObject);
			in->engineObject = NULL;
			in->engineEngine = NULL;
		}
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : wave output (OpenSLES) close [%d]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
	}
	
	obj->bPlaying = FALSE;
	obj->bStop = FALSE;
	obj->nBufferIndex = 0;
	obj->nBlockedBuffers = 0;

	if (in) in->engineObject = NULL;
	
#ifdef BSL_WAV_OUT_FADEOUT
	obj->resetFadeout (obj);
#endif /* BSL_WAV_OUT_FADEOUT */
	
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	デバイスがオープンされているかを判別する非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BOOL
 */
static BOOL _isOpened
	(
	BSLWavOut *obj
	)
{
	BSLWavOutOpenSLES_Internal *in = (BSLWavOutOpenSLES_Internal *) obj->pInternalData;
	if (in) {
		return in->engineObject != NULL ? TRUE : FALSE;
	}
	else return FALSE;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	デバイスの再生開始を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	BSLErr
 */
static BSLErr _start
	(
	BSLWavOut *obj
	)
{
	BSLWavOutOpenSLES_Internal *in = (BSLWavOutOpenSLES_Internal *) obj->pInternalData;
	BSLErr err = BSL_OK;
	SLresult result;
	
	if (!obj->isOpened (obj)) {
		return BSL_ERR_UNDEFINED;
	}
	if (obj->isPlaying (obj)) {
		return BSL_ERR_UNDEFINED;
	}
	
	if (in == NULL) {
		return BSL_ERR_UNDEFINED;
	}

	obj->bStop = FALSE;
	obj->bStopError = FALSE;
	obj->nBlockedBuffers = 0L;

#ifdef BSL_WAV_OUT_FADEOUT
	obj->resetFadeout (obj);
#endif /* BSL_WAV_OUT_FADEOUT */

	/* clear */
	if (err == BSL_OK) {
		result = (*(in->bqPlayerBufferQueue))->Clear (in->bqPlayerBufferQueue);
 	}

	/* filling initial buffer */
	if (err == BSL_OK) {
		int buffers = obj->getBuffers (obj);
		int i;
		
		if (1 < buffers) buffers--;
		
		for (i = 0; i < buffers; i++) {
			long bufferBytes = obj->getBlockSize (obj) * obj->getBytesPerFrame (obj);
#ifdef BSL_WAV_IO_FLOAT
			BSLWavIOData *buffer = obj->tBuff.pHdr[i]->pData;
#else /* BSL_WAV_IO_FLOAT */
			BSLWavIOData16 *buffer = obj->tBuff.pHdr[i]->pData;
#endif /* BSL_WAV_IO_FLOAT */
			memset (buffer, 0, bufferBytes);
			result = (*(in->bqPlayerBufferQueue))->Enqueue (in->bqPlayerBufferQueue, buffer, bufferBytes);
			obj->nBlockedBuffers++;
		}
	}

	if (err == BSL_OK) {
		// set the player's state to playing
		result = (*(in->bqPlayerPlay))->SetPlayState (in->bqPlayerPlay, SL_PLAYSTATE_PLAYING);
		if (SL_RESULT_SUCCESS != result) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-WAV: bqPlayerObject SetPlayState error (%d)\n"), result);
#endif /* BSL_DEBUG */
			err = BSL_ERR_WAV_OUT_OPEN;
		}
	}
	
	if (err == BSL_OK) {
#ifdef BSL_DEBUG
	#ifdef BSL_WAV_OUT_PAUSE
		if (obj->isPaused (obj)) {
			bslTrace (_TEXT("WAV : wave output (OpenSLES) resume [%ld]\n"), obj->nBlockedBuffers);
		}
		else 
	#endif /* BSL_WAV_OUT_PAUSE */
		bslTrace (_TEXT("WAV : wave output (OpenSLES) start [%ld]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
		obj->bPlaying = TRUE;
#ifdef BSL_WAV_OUT_PAUSE
		obj->bPausing = FALSE;
		obj->bPaused = FALSE;
#endif /* BSL_WAV_OUT_PAUSE */
		return BSL_OK;
	}
	else {
		obj->bPlaying = FALSE;
		return err;
	}
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	再生停止を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
*/
/*
static BSLErr _stop
	(
	BSLWavOut *obj
	)
{
	BSLErr err = _bslWavOutStop (obj);

	return err;
}
*/

#ifdef BSL_WAV_OUT_PAUSE
/*---------------------------------------------------------------------------*/
/*!
	@brief
	再生一時停止を行う内部関数
	@param obj
	制御オブジェクトのアドレス
*/
/*
static BSLErr _pause
	(
	BSLWavOut *obj
	)
{
	BSLErr err = _bslWavOutPause (obj);

	return err;
}
*/
#endif /* BSL_WAV_OUT_PAUSE */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	デバイスに設定されたバッファへデータを出力する非公開関数。
	本関数よりレンダリング処理を呼び出す。
	@param obj
	制御オブジェクトのアドレス
	@param data
	バッファ
	@param sampleFrames
	バッファフレーム数
	@retval
	BSLErr
 */
static BSLErr _buffering
	(
	BSLWavOut *obj, 
	void *data,
	long sampleFrames
	)
{
	if (!obj->isOpened (obj) || !obj->isPlaying (obj)) {
		return BSL_OK;
	}
	
	if (!data) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : wave output (OpenSLES) invalid buffer\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_UNDEFINED;
	}

	if (obj->nBlockedBuffers <= 0) {
		return BSL_ERR_UNDEFINED; /* すでに準備されたバッファは存在しない */
	}
	obj->nBlockedBuffers--;
	
	if (obj->bStop) {
		memset (data, 0, obj->getBytesPerFrame (obj) * sampleFrames);
	}
	else {
		/* fill buffer */
		BSLWavIOData *buffer = (BSLWavIOData *) data;
		
		BSL_WAV_RENDER_STATE status = obj->tRender.render (&obj->tRender, buffer, sampleFrames
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
														   , NULL
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
														   );
		
		switch (status) {
		case BSL_WAV_RENDER_STATE_FULL:
		case BSL_WAV_RENDER_STATE_COMPLETED:
#ifdef BSL_WAV_OUT_FADEOUT
			obj->processFadeout (obj, buffer, sampleFrames);
#endif /* BSL_WAV_OUT_FADEOUT */
				
			obj->nBlockedBuffers++;
			if (status == BSL_WAV_RENDER_STATE_COMPLETED) {
				obj->bStop = TRUE;
#ifdef BSL_DEBUG
				bslTrace (_TEXT("WAV : wave output (OpenSLES) completed [%d]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
			}
			break;
		case BSL_WAV_RENDER_STATE_ERROR:
		default:
			obj->bStopError = TRUE;
			obj->bStop = TRUE;
			break;
		}
	}
	
	return BSL_OK;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
void bslWavOutOpenSLESStopped
	(
	BSLWavOut *obj, 
	BSLErr error
	)
{
	_bslWavOutStopped (obj, error);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	OpenSLES用BSLWavOutの継承制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
	@param render
	rendering関数
	@param user
	ユーザパラメータ
 */
void bslWavOutOpenSLES
	(
	BSLWavOut *obj, 
	BSL_WAV_RENDER render, 
	void *user
	)
{
#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV: bslWavOutOpenSLES\n"));
#endif /* BSL_DEBUG */

	bslWavOut (obj, render, user);
	
	obj->initialize = _initialize;
	obj->finalize = _finalize;

	obj->getDrivers = _getDrivers;
	obj->getDriverName = _getDriverName;
	
	obj->open = _open;
	obj->close = _close;
	obj->isOpened = _isOpened;
	
	obj->start = _start;
/*	obj->stop = _stop; */
	obj->stopped = bslWavOutOpenSLESStopped;
#ifdef BSL_WAV_OUT_PAUSE
/*	obj->pause = _pause; */
#endif /* BSL_WAV_OUT_PAUSE */
	obj->buffering = _buffering;
}

#endif /* BSL_ANDROID */
