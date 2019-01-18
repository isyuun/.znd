/*
 *  BSLWavWriter.c
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriter.c
	@brief
	Wave File出力制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLDebug.h"
#include "BSLError.h"
#include "BSLMem.h"
#include "BSLWav.h"
#include "BSLWavWriter.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
static BSL_WAV_FILE_TYPE _getType
	(
	BSLWavWriter *obj
	)
{
	return (obj->nType);
}

/*---------------------------------------------------------------------------*/
static BSL_WAV_CODEC _getCodec
	(
	BSLWavWriter *obj
	)
{
	return (obj->nCodec);
}

/*---------------------------------------------------------------------------*/
static DWORD _getFrames
	(
	BSLWavWriter *obj
	)
{
	return (obj->dwFrames);
}

/*---------------------------------------------------------------------------*/
static int _getSampleRate
	(
	BSLWavWriter *obj
	)
{
	return obj->nSampleRate;
}

/*---------------------------------------------------------------------------*/
static int _getBitsPerSample
	(
	BSLWavWriter *obj
	)
{
	return (obj->nBitsPerSample);
}

/*---------------------------------------------------------------------------*/
static int _getBitsPerSampleRendering
	(
	BSLWavWriter *obj
	)
{
	return (obj->nBitsPerSampleRendering);
}

/*---------------------------------------------------------------------------*/
static DWORD _getBitRate
	(
	BSLWavWriter *obj
	)
{
	return (obj->dwBitRate);
}

/*---------------------------------------------------------------------------*/
static BSLErr _setBitsPerSampleRendering
	(
	BSLWavWriter *obj,
	int bitsPerSampleRendering
	)
{
	if (obj->getBitsPerSample (obj) != bitsPerSampleRendering) {
		return BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
	}

	return _bslWavWriterSetBitsPerSampleRendering (obj, bitsPerSampleRendering);
}

/*---------------------------------------------------------------------------*/
static int _getChannels
	(
	BSLWavWriter *obj
	)
{
	return (obj->nChannels);
}

/*---------------------------------------------------------------------------*/
static int _getBytesPerFrame
	(
	BSLWavWriter *obj
	)
{
	return (obj->getChannels (obj) * obj->getBitsPerSample (obj) / 8);
}

/*---------------------------------------------------------------------------*/
static int _getBytesPerFrameRendering
	(
	BSLWavWriter *obj
	)
{
	return (obj->getChannels (obj) * obj->getBitsPerSampleRendering (obj) / 8);
}

/*---------------------------------------------------------------------------*/
static BOOL _getUnsigned
	(
	BSLWavWriter *obj
	)
{
	return (obj->bUnsigned);
}

/*---------------------------------------------------------------------------*/
static BOOL _getBigEndian
	(
	BSLWavWriter *obj
	)
{
	return (obj->bBigEndian);
}

/*---------------------------------------------------------------------------*/
static BOOL _getFloat
	(
	BSLWavWriter *obj
	)
{
	return (obj->bFloat);
}

/*---------------------------------------------------------------------------*/
static BOOL _getFloatRendering
(
	BSLWavWriter *obj
	)
{
    return (obj->bFloatRendering);
}

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
/*---------------------------------------------------------------------------*/
static void _setCopyEnable
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bCopyEnable = flag;
}

/*---------------------------------------------------------------------------*/
static BOOL _getCopyEnable
	(
	BSLWavWriter *obj
	)
{
	return (obj->bCopyEnable);
}

/*---------------------------------------------------------------------------*/
static void _setSaveEnable
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bSaveEnable = flag;
}

/*---------------------------------------------------------------------------*/
static BOOL _getSaveEnable
	(
	BSLWavWriter *obj
	)
{
	return (obj->bSaveEnable);
}
/*---------------------------------------------------------------------------*/
static void _setLEDUsing
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bLEDUsing = flag;
}

/*---------------------------------------------------------------------------*/
static BOOL _getLEDUsing
	(
	BSLWavWriter *obj
	)
{
	return (obj->bLEDUsing);
}

/*---------------------------------------------------------------------------*/
static void _setVIBUsing
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bVIBUsing = flag;
}

/*---------------------------------------------------------------------------*/
static BOOL _getVIBUsing
	(
	BSLWavWriter *obj
	)
{
	return (obj->bVIBUsing);
}
#endif

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wave Rendering処理制御オブジェクト（BSLWavRender）を使ったWaveファイル出力処理を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param file
	ファイル出力制御オブジェクトのアドレス
	@param render
	レンダリング処理用制御オブジェクトのアドレス
	@param updater
	経過表示用callback
	@param updateParam
	callback用データ
	@retval BSLErr
*/
static BSLErr _save
	(
	BSLWavWriter *obj, 
	BSLFileSave *file, 
	BSLWavRender *render,
	BSL_WAV_UPDATER updater, 
	void *updateParam
	)
{
	BSLErr err = BSL_OK;

	obj->pUpdater = updater;
	obj->pUpdateParam = updateParam;

	if (err == BSL_OK) {
		err = file->save (file, obj->_save, obj, render);
	}

	return (err);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Stream出力関数を使ったWaveファイル出力処理を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param file
	ファイル出力制御オブジェクトのアドレス
	@param stream
	Stream出力関数
	@param streamParam
	Stream出力関数用データ
	@param updater
	経過表示用callback
	@param updateParam
	callback用データ
	@retval BSLErr
*/
static BSLErr _saveStream
	(
	BSLWavWriter *obj, 
	BSLFileSave *file, 
	BSL_WAV_WRITER_STREAM stream,
	void *streamParam,
	BSL_WAV_UPDATER updater, 
	void *updateParam
	)
{
	BSLErr err = BSL_OK;

	obj->pUpdater = updater;
	obj->pUpdateParam = updateParam;

	obj->pStreamParam = streamParam;

	if (err == BSL_OK) {
		err = file->save (file, obj->_saveStream, obj, stream);
	}

	return (err);
}

/*---------------------------------------------------------------------------*/
static BSLErr __saveStream
	(
	BSLFileSave *file, 
	void *user, 
	void *userParam
	)
{
	BSLErr err = BSL_OK;
	BSLWavWriter *obj = (BSLWavWriter *) user;
	BSL_WAV_WRITER_STREAM stream = (BSL_WAV_WRITER_STREAM) userParam;
	DWORD streamSize = 0UL;

	if (err == BSL_OK) {
		err = stream (file, &streamSize, obj->pStreamParam);
	}

	return err;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	ファイルタイプの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param type
	ファイルタイプ
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetType
	(
	BSLWavWriter *obj,
	BSL_WAV_FILE_TYPE type
	)
{
	obj->nType = type;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	コーデックタイプの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param codec
	コーデックタイプ
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetCodec
	(
	BSLWavWriter *obj,
	BSL_WAV_CODEC codec
	)
{
	obj->nCodec = codec;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	総フレーム数の設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param frames
	総フレーム数[sample]
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetFrames
	(
	BSLWavWriter *obj,
	DWORD frames
	)
{
	obj->dwFrames = frames;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	サンプリング周波数の設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param sampleRate
	サンプリング周波数[Hz]
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetSampleRate
	(
	BSLWavWriter *obj,
	int sampleRate
	)
{
	obj->nSampleRate = sampleRate;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ビット精度の設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param bitsPerSample
	ビット精度[bit]
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetBitsPerSample
	(
	BSLWavWriter *obj,
	int bitsPerSample
	)
{
	obj->nBitsPerSample = bitsPerSample;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	レンダリング処理時の精度の設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param bitsPerSampleRendering
	レンダリング処理時のビット精度[bit]
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetBitsPerSampleRendering
	(
	BSLWavWriter *obj,
	int bitsPerSampleRendering
	)
{
	obj->nBitsPerSampleRendering = bitsPerSampleRendering;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ビットレートの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param bitRate
	ビットレート[bps]
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetBitRate
	(
	BSLWavWriter *obj,
	DWORD bitRate
	)
{
	obj->dwBitRate = bitRate;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	リニアコーデック時のビットレートの取得を行う内部公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	ビットレート[bps]
*/
DWORD _bslWavWriterGetBitRateLinear
	(
	BSLWavWriter *obj
	)
{
	obj->dwBitRate = obj->getBitsPerSample (obj) * obj->getSampleRate (obj) * obj->getChannels (obj);
	return obj->dwBitRate;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	チャンネル数の設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param channels
	チャンネル数
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetChannels
	(
	BSLWavWriter *obj,
	int channels
	)
{
	if (0 < channels && channels <= BSL_WAV_CHANNELS_MAX) {
		obj->nChannels = channels;
		return BSL_OK;
	}
	else {
		return BSL_ERR_WAV_SET_CHANNELS;
	}
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Unsignedの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetUnsigned
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bUnsigned = flag;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Big Endianの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetBigEndian
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bBigEndian = flag;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Floatの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
*/
BSLErr _bslWavWriterSetFloat
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	obj->bFloat = flag;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	レンダリング処理時のFloatの設定を行う内部関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
 */
BSLErr _bslWavWriterSetFloatRendering
(
	BSLWavWriter *obj,
	BOOL flag
	)
{
    obj->bFloatRendering = flag;
    
    return (BSL_OK);
}

#ifdef BSL_WAV_FILE_TITLE_SIZE
/*---------------------------------------------------------------------------*/
static void _setTitle
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cTitle, str, BSL_WAV_FILE_TITLE_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getTitle
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cTitle, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getTitleSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cTitle);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getTitleAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cTitle;
}
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
/*---------------------------------------------------------------------------*/
static void _setVersion
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cVersion, str, BSL_WAV_FILE_TITLE_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getVersion
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cVersion, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getVersionSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cVersion);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getVersionAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cVersion;
}
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
/*---------------------------------------------------------------------------*/
static void _setDate
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cDate, str, BSL_WAV_FILE_DATE_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getDate
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cDate, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getDateSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cDate);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getDateAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cDate;
}
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
/*---------------------------------------------------------------------------*/
static void _setCopyright
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cCopyright, str, BSL_WAV_FILE_COPYRIGHT_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getCopyright
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cCopyright, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getCopyrightSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cCopyright);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getCopyrightAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cCopyright;
}
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
/*---------------------------------------------------------------------------*/
static void _setProtect
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cProtect, str, BSL_WAV_FILE_PROT_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getProtect
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cProtect, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getProtectSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD) _tcslen (obj->cProtect);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getProtectAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cProtect;
}
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
/*---------------------------------------------------------------------------*/
static void _setAuthor
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cAuthor, str, BSL_WAV_FILE_AUTH_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getAuthor
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cAuthor, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getAuthorSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cAuthor);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getAuthorAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cAuthor;
}
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
/*---------------------------------------------------------------------------*/
static void _setSupport
	(
	BSLWavWriter *obj,
	LPCTSTR str
	)
{
	_tcsncpy_(obj->cSupport, str, BSL_WAV_FILE_SUPT_SIZE - 1);
}

/*---------------------------------------------------------------------------*/
static void _getSupport
	(
	BSLWavWriter *obj,
	LPTSTR str,
	int length
	)
{
	_tcsncpy_(str, obj->cSupport, length);
}

/*---------------------------------------------------------------------------*/
static DWORD _getSupportSize
	(
	BSLWavWriter *obj
	)
{
	return (DWORD)_tcslen(obj->cSupport);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getSupportAddr
	(
	BSLWavWriter *obj
	)
{
	return (LPCTSTR) obj->cSupport;
}
#endif /* BSL_WAV_FILE_SUPT_SIZE */

#pragma mark -
#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
/*---------------------------------------------------------------------------*/
static void _setMFiSorc
	(
	BSLWavWriter *obj,
	BYTE sorc
	)
{
	obj->byMFiSorc = (BYTE) sorc;
}

/*---------------------------------------------------------------------------*/
static BYTE _getMFiSorc
	(
	BSLWavWriter *obj
	)
{
	return obj->byMFiSorc;
}

/*---------------------------------------------------------------------------*/
static void _setMFiNoteMessLength
	(
	BSLWavWriter *obj,
	WORD length
	)
{
	obj->wMFiNoteMessLength = length;
}

/*---------------------------------------------------------------------------*/
static WORD _getMFiNoteMessLength
	(
	BSLWavWriter *obj
	)
{
	return obj->wMFiNoteMessLength;
}

/*---------------------------------------------------------------------------*/
static void _setMFiExstStatusALength
	(
	BSLWavWriter *obj,
	WORD length
	)
{
	obj->wMFiExstStatusALength = length;
}

/*---------------------------------------------------------------------------*/
static WORD _getMFiExstStatusALength
	(
	BSLWavWriter *obj
	)
{
	return obj->wMFiExstStatusALength;
}
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
/*---------------------------------------------------------------------------*/
static void _setPlayTime
	(
	BSLWavWriter *obj,
	DWORD time
	)
{
	obj->dwPlayTime = time;
}

/*---------------------------------------------------------------------------*/
static DWORD _getPlayTime
	(
	BSLWavWriter *obj
	)
{
	return obj->dwPlayTime;
}

/*---------------------------------------------------------------------------*/
static void _setBlankTime
	(
	BSLWavWriter *obj,
	DWORD time
	)
{
	obj->dwBlankTime = time;
}

/*---------------------------------------------------------------------------*/
static DWORD _getBlankTime
	(
	BSLWavWriter *obj
	)
{
	return obj->dwBlankTime;
}

/*---------------------------------------------------------------------------*/
static void _setAudioVolume
	(
	BSLWavWriter *obj,
	DWORD volume
	)
{
	obj->dwAudioVolume = volume;
}

/*---------------------------------------------------------------------------*/
static DWORD _getAudioVolume
	(
	BSLWavWriter *obj
	)
{
	return obj->dwAudioVolume;
}

/*---------------------------------------------------------------------------*/
static void _set3DEfx
	(
	BSLWavWriter *obj,
	BOOL efx
	)
{
	obj->b3DEfx= efx;
}

/*---------------------------------------------------------------------------*/
static BOOL _get3DEfx
	(
	BSLWavWriter *obj
	)
{
	return obj->b3DEfx;
}

/*---------------------------------------------------------------------------*/
static void _setMakerID
	(
	BSLWavWriter *obj,
	int nMakerID
	)
{
	obj->nMakerID= nMakerID;
}

/*---------------------------------------------------------------------------*/
static int _getMakerID
	(
	BSLWavWriter *obj
	)
{
	return obj->nMakerID;
}

/*---------------------------------------------------------------------------*/
static void _setMFiVersion
	(
	BSLWavWriter *obj,
	int nMFiVersion
	)
{
	obj->nMFiVersion= nMFiVersion;
}

/*---------------------------------------------------------------------------*/
static int _getMFiVersion
	(
	BSLWavWriter *obj
	)
{
	return obj->nMFiVersion;
}
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
/*---------------------------------------------------------------------------*/
static int _getLoopNum (struct BSLWavWriter *obj)
{
	return obj->nLoops;
}

/*---------------------------------------------------------------------------*/
static BSLErr _addLoop (struct BSLWavWriter *obj, BSL_WAV_LOOP *loop)
{
	int numLoops = obj->getLoopNum (obj);

	if (BSL_WAV_LOOPS <= numLoops) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV: can't add loop\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_UNDEFINED;
	}

	if (!loop) {
		return BSL_ERR_PARAM_WRONG;
	}

	memcpy (&obj->loop[numLoops], loop, sizeof (BSL_WAV_LOOP));
	obj->nLoops = numLoops + 1;

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static BSLErr _deleteLoop (struct BSLWavWriter *obj, int index)
{
	int numLoops = obj->getLoopNum (obj);
	int i;

	if (numLoops <= index) {
		return BSL_ERR_PARAM_WRONG;
	}

	for (i = index; i < (numLoops - 1); i++) {
		memcpy (&obj->loop[i], &obj->loop[i + 1], sizeof (BSL_WAV_LOOP));
	}

	return BSL_OK;
}

static void _getLoop (BSLWavWriter *obj, int index, BSL_WAV_LOOP *loop)
{
	int numLoops = obj->getLoopNum (obj);

	if (!loop) {
		return;
	}
	memset (loop, 0, sizeof (BSL_WAV_LOOP));

	if (numLoops <= index) {
		return;
	}

	memcpy (loop, &obj->loop[index], sizeof (BSL_WAV_LOOP));
}
#endif /* BSL_WAV_LOOPS */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	出力処理を行う内部関数
	@param file
	ファイル出力制御オブジェクトのアドレス
	@param obj
	制御オブジェクトのアドレス
	@param render
	レンダリング処理制御オブジェクトのアドレス
	@param renderedFrames
	処理フレーム数
	@retval
	BSLErr
*/
BSLErr _bslWavWriterBounce
	(
	BSLFileSave *file,
	BSLWavWriter *obj,
	BSLWavRender *render,
	DWORD *renderedFrames
	)
{
	long blockSize;
	DWORD frames = 0UL;
	BOOL more = TRUE;
	BSLErr err = BSL_OK;

	if (!render) {
		return (BSL_ERR_UNDEFINED);
	}

	blockSize = render->getBlockSize (render);

	while (more) {
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
		long readFrames = 0L;
		BSL_WAV_RENDER_STATE state = render->render (render, render->getBuffer (render), blockSize, &readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
		long readFrames = blockSize;
		BSL_WAV_RENDER_STATE state = render->render (render, render->getBuffer (render), blockSize);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
		frames += readFrames;
		switch (state) {
		case BSL_WAV_RENDER_STATE_FULL:
		case BSL_WAV_RENDER_STATE_COMPLETED:
			if (!obj->getFloat (obj)) {
                if (obj->getFloatRendering (obj)) {
                    switch (obj->getBitsPerSample (obj)) {
                        case 32:
                            {
                                long i = readFrames * obj->getChannels (obj);
                                float *in = (float *) render->getBuffer (render);
                                BSLWavIOData32 *out = (BSLWavIOData32 *) in;
                                while (--i >= 0L) {
                                    *out++ = (BSLWavIOData32) (*in++ * (float) 0x7FFFFFFF);
                                }
                            }
                            /* swap */
#ifdef BSL_BIGENDIAN
                            if (!obj->getBigEndian (obj))
#else /* BSL_BIGENDIAN */
                            if (obj->getBigEndian (obj))
#endif /* BSL_BIGENDIAN */
                            {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData32 *data = (BSLWavIOData32 *) render->getBuffer (render);
                                while (--i >= 0L) {
                                    bslFileSwap (data++, 4);
                                }
                            }
                            break;
                        case 8:
#ifdef BSL_DEBUG
                            bslTrace("ERROR: Not supported");
#endif
                            return BSL_ERR_NOT_SUPPORTED;
                        default:
                            {
                                long i = readFrames * obj->getChannels (obj);
                                float *in = (float *) render->getBuffer (render);
                                BSLWavIOData16 *out = (BSLWavIOData16 *) in;
                                while (--i >= 0L) {
                                    *out++ = (BSLWavIOData16) (*in++ * 32767.f);
                                }
                            }
                            /* swap */
#ifdef BSL_BIGENDIAN
                            if (!obj->getBigEndian (obj))
#else /* BSL_BIGENDIAN */
                            if (obj->getBigEndian (obj))
#endif /* BSL_BIGENDIAN */
                            {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData16 *data = (BSLWavIOData16 *) render->getBuffer (render);
                                while (--i >= 0L) {
                                    bslFileSwap (data++, 2);
                                }
                            }
                            break;
                    }
                }
                else {
                    switch (obj->getBitsPerSample (obj)) {
                        case 32:
                            /* swap */
#ifdef BSL_BIGENDIAN
                            if (!obj->getBigEndian (obj))
#else /* BSL_BIGENDIAN */
                            if (obj->getBigEndian (obj))
#endif /* BSL_BIGENDIAN */
                            {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData32 *data = (BSLWavIOData32 *) render->getBuffer (render);
                                while (--i >= 0L) {
                                    bslFileSwap (data++, 4);
                                }
                            }
                            /* signed / unsigned */
                            if (obj->getUnsigned (obj)) {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData32 *in = (BSLWavIOData32 *) render->getBuffer (render);
                                DWORD *out = (DWORD *) in;
                                while (--i >= 0L) {
                                    long long tmp = (long long) *in++;
                                    tmp += 0x80000000;
                                    *out++ = (DWORD) tmp;
                                }
                            }
                            break;
                        case 16:
                            /* swap */
#ifdef BSL_BIGENDIAN
                            if (!obj->getBigEndian (obj))
#else /* BSL_BIGENDIAN */
                            if (obj->getBigEndian (obj))
#endif /* BSL_BIGENDIAN */
                            {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData16 *data = (BSLWavIOData16 *) render->getBuffer (render);
                                while (--i >= 0L) {
                                    bslFileSwap (data++, 2);
                                }
                            }
                            /* signed / unsigned */
                            if (obj->getUnsigned (obj)) {
                                long i = readFrames * obj->getChannels (obj);
                                BSLWavIOData16 *in = (BSLWavIOData16 *) render->getBuffer (render);
                                WORD *out = (WORD *) in;
                                while (--i >= 0L) {
                                    long tmp = (long) *in++;
                                    tmp += 32768L;
                                    *out++ = (WORD) tmp;
                                }
                            }
                            break;
                        case 8:
                            /* signed / unsigned */
#ifdef BSL_WAV_IO_UNSIGNED_8BIT
                            if (!obj->getUnsigned (obj)) {
                                /* unsigned8 -> signed8 */
                                long i = readFrames * obj->getChannels (obj);
                                BYTE *in = (BYTE *) render->getBuffer (render);
                                char *out = (char *) in;
                                while (--i >= 0L) {
                                    short tmp = (short) *in++;
                                    *out++ = (char) (tmp - 128);
                                }
                            }
#else /* BSL_WAV_IO_UNSIGNED_8BIT */
                            if (obj->getUnsigned (obj)) {
                                /* signed8 -> unsigned8 */
                                long i = readFrames * obj->getChannels (obj);
                                char *in = (char *) render->getBuffer (render);
                                BYTE *out = (BYTE *) in;
                                while (--i >= 0L) {
                                    short tmp = (short) *in++;
                                    *out++ = (BYTE) (tmp + 128);
                                }
                            }
#endif /* BSL_WAV_IO_UNSIGNED_8BIT */
                            break;
                    }
                }
			}

			/* write to file */
			if (file) {
				if (file->nType != BSL_FILE_SAVE_TYPE_DUMMY) {
					long size = readFrames * obj->getBytesPerFrameRendering (obj);
					if ((err = file->write (file, render->getBuffer (render), (DWORD) size)) != BSL_OK) {
						break;
					}
				}
			}

			if (state == BSL_WAV_RENDER_STATE_COMPLETED) {
				more = FALSE;
			}
			break;
		case BSL_WAV_RENDER_STATE_ERROR:
		default:
			more = FALSE;
			err = BSL_ERR_UNDEFINED;
			break;
		}

		/* updater */
		if (obj->pUpdater) {
			int percent;
			DWORD fullframes = obj->getFrames (obj);
			if (0UL < fullframes) {
				percent = (int) (frames * 100 / fullframes);
			}
			else {
				percent = (int) (frames * 100 / (5 * 60 * obj->getSampleRate (obj)));
			}
			if (100 < percent) {
				percent = 100;
			}
			if (obj->pUpdater (percent, obj->pUpdateParam) != 0) {
				more = FALSE;
				return (BSL_ERR_CANCELED);
			}
		}
	}

	if (renderedFrames) {
		*renderedFrames = frames;
	}
	
	return (err);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	BSLWavWriter基本制御オブジェクトの構築を行う
	@param obj
	制御オブジェクトのアドレス
*/
void bslWavWriter
	(
	BSLWavWriter *obj
	)
{
	memset (obj, 0, sizeof (BSLWavWriter));

	obj->save = _save;
/*	obj->_save = ; */

	obj->saveStream = _saveStream;
	obj->_saveStream = __saveStream;

	obj->nType = BSL_WAV_FILE_TYPE_NONE;
	obj->nCodec = BSL_WAV_CODEC_PCM;
	obj->dwFrames = 0UL;
	obj->nSampleRate = BSL_WAV_SAMPLE_RATE;
	obj->nBitsPerSample = BSL_WAV_BITS_PER_SAMPLE;
	obj->nBitsPerSampleRendering = BSL_WAV_BITS_PER_SAMPLE;
	obj->nChannels = BSL_WAV_CHANNELS;
	obj->bUnsigned = FALSE;

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	obj->dwPlayTime = (DWORD)-1;
	obj->dwBlankTime = (DWORD)-1;
	obj->dwAudioVolume = BSL_WAV_DEFAULT_VOLUME;
	obj->b3DEfx = FALSE;
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_BIGENDIAN
	obj->bBigEndian = TRUE;
#else /* BSL_BIGENDIAN */
	obj->bBigEndian = FALSE;
#endif /* BSL_BIGENDIAN */

	obj->setType = _bslWavWriterSetType;
	obj->getType = _getType;

	obj->setCodec = _bslWavWriterSetCodec;
	obj->getCodec = _getCodec;

	obj->setFrames = _bslWavWriterSetFrames;
	obj->getFrames = _getFrames;

	obj->setSampleRate = _bslWavWriterSetSampleRate;
	obj->getSampleRate = _getSampleRate;

	obj->setBitsPerSample = _bslWavWriterSetBitsPerSample;
	obj->setBitsPerSampleRendering = _setBitsPerSampleRendering;
	obj->getBitsPerSample = _getBitsPerSample;
	obj->getBitsPerSampleRendering = _getBitsPerSampleRendering;

	obj->setBitRate = _bslWavWriterSetBitRate;
	obj->getBitRate = _getBitRate;

	obj->setChannels = _bslWavWriterSetChannels;
	obj->getChannels = _getChannels;

	obj->getBytesPerFrame = _getBytesPerFrame;
	obj->getBytesPerFrameRendering = _getBytesPerFrameRendering;

	obj->setUnsigned = _bslWavWriterSetUnsigned;
	obj->getUnsigned = _getUnsigned;

	obj->setBigEndian = _bslWavWriterSetBigEndian;
	obj->getBigEndian = _getBigEndian;

	obj->setFloat = _bslWavWriterSetFloat;
	obj->getFloat = _getFloat;

    obj->setFloatRendering = _bslWavWriterSetFloatRendering;
    obj->getFloatRendering = _getFloatRendering;
    
#ifdef BSL_WAV_FILE_TITLE_SIZE
	obj->setTitle = _setTitle;
	obj->getTitle = _getTitle;
	obj->getTitleSize = _getTitleSize;
	obj->getTitleAddr = _getTitleAddr;
	obj->setTitle (obj, _TEXT("untitled"));
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
	obj->setVersion = _setVersion;
	obj->getVersion  = _getVersion;
	obj->getVersionSize = _getVersionSize;
	obj->getVersionAddr = _getVersionAddr;
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
	obj->setDate = _setDate;
	obj->getDate = _getDate;
	obj->getDateSize = _getDateSize;
	obj->getDateAddr = _getDateAddr;
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
	obj->setCopyright = _setCopyright;
	obj->getCopyright = _getCopyright;
	obj->getCopyrightSize = _getCopyrightSize;
	obj->getCopyrightAddr = _getCopyrightAddr;
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
	obj->setProtect = _setProtect;
	obj->getProtect = _getProtect;
	obj->getProtectSize = _getProtectSize;
	obj->getProtectAddr = _getProtectAddr;
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
	obj->setAuthor = _setAuthor;
	obj->getAuthor = _getAuthor;
	obj->getAuthorSize = _getAuthorSize;
	obj->getAuthorAddr = _getAuthorAddr;
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
	obj->setSupport = _setSupport;
	obj->getSupport = _getSupport;
	obj->getSupportSize = _getSupportSize;
	obj->getSupportAddr = _getSupportAddr;
#endif /* BSL_WAV_FILE_SUPT_SIZE */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	obj->setCopyEnable = _setCopyEnable;
	obj->getCopyEnable = _getCopyEnable;

	obj->setSaveEnable = _setSaveEnable;
	obj->getSaveEnable = _getSaveEnable;

	obj->setLEDUsing = _setLEDUsing;
	obj->getLEDUsing = _getLEDUsing;
	obj->setVIBUsing = _setVIBUsing;
	obj->getVIBUsing = _getVIBUsing;
#endif

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
	obj->setMFiSorc = _setMFiSorc;
	obj->getMFiSorc = _getMFiSorc;
	obj->byMFiSorc = 0x01; /* ネットワークからのダウンロード, 再配布不可 */

	obj->setMFiNoteMessLength = _setMFiNoteMessLength;
	obj->getMFiNoteMessLength = _getMFiNoteMessLength;
	obj->wMFiNoteMessLength = 1;

	obj->setMFiExstStatusALength = _setMFiExstStatusALength;
	obj->getMFiExstStatusALength = _getMFiExstStatusALength;
	obj->wMFiExstStatusALength = 1;
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	obj->setPlayTime = _setPlayTime;
	obj->getPlayTime = _getPlayTime;
	obj->setBlankTime = _setBlankTime;
	obj->getBlankTime = _getBlankTime;

	obj->setAudioVolume = _setAudioVolume;
	obj->getAudioVolume = _getAudioVolume;
	obj->set3DEfx = _set3DEfx;
	obj->get3DEfx = _get3DEfx;

	obj->setMakerID = _setMakerID;
	obj->getMakerID = _getMakerID;
	obj->setMFiVersion = _setMFiVersion;
	obj->getMFiVersion = _getMFiVersion;

	bslMFiUtil(&obj->MFiUtil);
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
	obj->nLoops = 0;
	obj->getLoopNum = _getLoopNum;
	obj->addLoop = _addLoop;
	obj->deleteLoop = _deleteLoop;
	obj->getLoop = _getLoop;
#endif /* BSL_WAV_LOOPS */

	obj->pInternalCache = NULL;
	obj->dwInternalCacheSize = 0UL;
	obj->pInternalData = NULL;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	BSLWavWriter基本制御オブジェクトの破棄を行う
	@param obj
	制御オブジェクトのアドレス
*/
void bslWavWriterExit
	(
	BSLWavWriter *obj
	)
{
	if (obj->finalize) {
		obj->finalize (obj);
	}

	if (obj->pInternalCache) {
		bslMemFree (obj->pInternalCache);
		obj->pInternalCache = NULL;
	}

	if (obj->pInternalData) {
		bslMemFree (obj->pInternalData);
		obj->pInternalData = NULL;
	}
}



