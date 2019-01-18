/*
 *  BSLWavOut.h
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavOut.h
	@brief
	Wave出力制御オブジェクト
*/

#ifndef __INCBSLWavOuth
#define __INCBSLWavOuth

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLWav.h"
#include "BSLWavRender.h"
#if !BSL_MAC && !BSL_IOS
	#include "BSLWavBuff.h"
#endif

#if BSL_MAC || BSL_IOS
/*	#include <CoreServices/CoreServices.h> */
	#include <AudioToolbox/AudioQueue.h>
	#include <AudioToolbox/AudioFile.h>
#elif BSL_LINUX
	#include <fcntl.h>
	#include <unistd.h>
	#include <sys/ioctl.h>
	#include <pthread.h>
#endif

/* defines */

#ifndef BSL_WAV_OUT_DEVICE_NAME_LENGTH
	#define BSL_WAV_OUT_DEVICE_NAME_LENGTH 256
#endif /* BSL_WAV_OUT_DEVICE_NAME_LENGTH */

#ifdef BSL_WAV_OUT_VOLUME
enum {
#if BSL_MAC
	BSL_WAV_OUT_VOLUME_MAX = 256
#elif BSL_WINDOWS
	BSL_WAV_OUT_VOLUME_MAX = 65535
#elif BSL_LINUX
	BSL_WAV_OUT_VOLUME_MAX = 255
#else
	BSL_WAV_OUT_VOLUME_MAX = 1 /* dummy */
#endif
	};
#endif /* BSL_WAV_OUT_VOLUME */

/* typedefs */

/*!
	Wave出力処理基本制御オブジェクト。
	各プラットフォーム用に以下の継承オブジェクトが存在する。
	- BSLWavOutMme.c / h : Win MME
	- BSLWavOutMmeThread.c / h : Win MME（スレッド使用）
	- BSLWavOutDX.c / h : Win DirectSound（スレッド使用）
	- BSLWavOutDsp.c / h : Linux DSP
	- BSLWavOutDspThread.c / h : Linux DSP（スレッド使用）
	- BSLWavOutAsio.cpp / h : Win ASIO
	- BSLWavOutAudioQueue.c / h : Mac OS X / iPhone Audio Queue

	スレッド使用オブジェクトではBSLConf.hにてBSL_WAV_OUT_INCLUDE_THREADを定義すること。
 
	基本的な使用手順は以下の通り。詳細は別途シーケンス図を参照すること（現状はdoc/figure.docにて提供される）。
	-# レンダリング処理用コールバック（BSL_WAV_RENDER）を用意する
	-# bslWavOut* ()により制御オブジェクトを構築
	-# BSLWavOut::initialize ()によりオブジェクトを初期化
	-# BSLWavOut::setSampleRate ()によりサンプリング周波数を設定（BSLConf.hに定義した初期設定値（BSL_WAV_SAMPLE_RATE）以外での設定が必要な場合のみ）
	-# BSLWavOut::setBlockSize ()により最大ブロックサイズを設定（BSLConf.hに定義した初期設定値（BSL_WAV_BLOCK_SIZE）以外での設定が必要な場合のみ）
	-# BSLWavOut::setBitsPerSample ()によりビット精度を設定（BSLConf.hに定義した初期設定値（BSL_WAV_BITS_PER_SAMPLE）以外での設定が必要な場合のみ）
	-# BSLWavOut::setChannels ()によりチャンネル数を設定（BSLConf.hに定義した初期設定値（BSL_WAV_CHANNELS）以外での設定が必要な場合のみ）
	-# 以下繰り返し
		- BSLWavOut::open ()
		- BSLWavOut::start ()
		- BSLWavOut::isPlayDone ()によって停止完了までWait
		- BSLWavOut::stopped ()
		- BSLWavOut::close ()
	-# bslWavOutExit ()により制御オブジェクトを破棄
*/
typedef struct BSLWavOut
{
	/*! 初期化を行うメソッド。構築後必ず最初に実行する。 */
	BSLErr (*initialize) (struct BSLWavOut *obj);

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslWavOutExit()より呼び出される。
	*/
	void (*finalize) (struct BSLWavOut *obj);

	/*! ドライバ総数の取得 */
	int (*getDrivers) (struct BSLWavOut *obj);
	/*! ドライバ名の取得 */
	void (*getDriverName) (struct BSLWavOut *obj, int index, LPTSTR name, int length);

	/*! サンプリング周波数[Hz]を設定するメソッド */
	BSLErr (*setSampleRate) (struct BSLWavOut *obj, int sampleRate);
	/*! サンプリング周波数[Hz]を取得するメソッド */
	int (*getSampleRate) (struct BSLWavOut *obj);

	/*! 最大ブロックサイズ[sample]を設定するメソッド */
	BSLErr (*setBlockSize) (struct BSLWavOut *obj, long blockSize);
	/*! 最大ブロックサイズ[sample]を取得するメソッド */
	long (*getBlockSize) (struct BSLWavOut *obj);

	/*! ビット精度[bit]を設定するメソッド */
	BSLErr (*setBitsPerSample) (struct BSLWavOut *obj, int bitsPerSample);
	/*! ビット精度[bit]を取得するメソッド */
	int (*getBitsPerSample) (struct BSLWavOut *obj);

	/*! チャンネル数を設定するメソッド */
	BSLErr (*setChannels) (struct BSLWavOut *obj, int channels);
	/*! チャンネル数を取得するメソッド */
	int (*getChannels) (struct BSLWavOut *obj);

	/*! バイト数/フレームを取得するメソッド */
	int (*getBytesPerFrame) (struct BSLWavOut *obj);

	BSLErr (*setBuffers) (struct BSLWavOut *obj, int bufferes);
	int (*getBuffers) (struct BSLWavOut *obj);

	/*! デバイスのオープンを行うメソッド */
	BSLErr (*open) (struct BSLWavOut *obj, LPCTSTR name, void *param1, DWORD param2);
#if BSL_WINDOWS
	/*! MMEデバイスのオープンを行うメソッド */
	BSLErr (*openMme) (struct BSLWavOut *obj, UINT uDeviceID, DWORD_PTR dwCallback, DWORD_PTR dwCallbackInstance, DWORD fdwOpen);
#endif /* BSL_WINDOWS */
	/*! デバイスのクローズを行うメソッド */
	BSLErr (*close) (struct BSLWavOut *obj);

#ifdef BSL_WAV_OUT_VOLUME
	BSLErr (*setVolume) (struct BSLWavOut *obj, DWORD value);
	DWORD (*getVolume) (struct BSLWavOut *obj);
#endif /* BSL_WAV_OUT_VOLUME */

	/*!
		再生開始を行うメソッド。<br>
		BSL_WAV_OUT_PAUSE定義時において一時停止の場合には一時停止解除を行う。
	*/
	BSLErr (*start) (struct BSLWavOut *obj);
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		再生一時停止を行うメソッド。
		BSL_WAV_OUT_PAUSE定義時のみ有効。
	*/
	BSLErr (*pause) (struct BSLWavOut *obj);	
#endif /* BSL_WAV_OUT_PAUSE */
	/*! 再生停止を行うメソッド。本メソッド実行後もBSLWavOut::stoppedまで再生は終了しないことに注意。 */
	BSLErr (*stop) (struct BSLWavOut *obj);
	/*! 再生停止完了処理を行うメソッド */
	void (*stopped) (struct BSLWavOut *obj, BSLErr error);
	/*! デバイスのリセット処理を行うメソッド */
	BSLErr (*reset) (struct BSLWavOut *obj);

	/*! デバイスがオープン済かを取得するメソッド */
	BOOL (*isOpened) (struct BSLWavOut *obj);
	/*! デバイスが再生中かを取得するメソッド */
	BOOL (*isPlaying) (struct BSLWavOut *obj);
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		デバイスが一時停止中かを取得するメソッド。
		BSL_WAV_OUT_PAUSE定義時のみ有効。
	*/
	BOOL (*isPaused) (struct BSLWavOut *obj);
#endif /* BSL_WAV_OUT_PAUSE */
	/*! 再生が完了したかをを取得メソッド。BSLWavOut::stoppedの実行条件となる。 */
	BOOL (*isPlayDone) (struct BSLWavOut *obj);

	/*! 出力バッファ生成を行うメソッド。BSLWavOut::tRenderを内部で駆動する。*/
#if BSL_MAC || BSL_IOS
	BSLErr (*buffering) (struct BSLWavOut *obj, BSLWavIOData *data, long sampleFrames);
#elif BSL_WINDOWS || BSL_ANDROID
	BSLErr (*buffering) (struct BSLWavOut *obj, void *data, long sampleFrames);
#else
	BSLErr (*buffering) (struct BSLWavOut *obj);
#endif

	/*! ドライバの独自設定画面を起動するメソッド（現状ASIOのみ）*/
	void (*showControlPanel) (struct BSLWavOut *obj);

	/*! ユーザーデータを設定するメソッド */
	void *(*getUser) (struct BSLWavOut *obj);
	/*! ユーザーデータを取得するメソッド */
	void (*setUser) (struct BSLWavOut *obj, void *user);

	/*! レンダリング制御オブジェクト */ 
	BSLWavRender tRender;
#if BSL_MAC || BSL_IOS
	int nBuffers;
#else
	/*! バッファ制御オブジェクト */ 
	BSLWavBuff tBuff;
#endif /* BSL_MAC */

	/*! デバイス識別子 */
#if BSL_WINDOWS
	HWAVEOUT hDevice;
#elif BSL_LINUX
	 int hDevice;
#endif
	/*! サンプリング周波数[Hz] */
	 int nSampleRate;
	/*! ビット精度[bit] */
	int nBitsPerSample;
	/*! チャンネル数 */
	int nChannels;

#ifdef BSL_WAV_OUT_VOLUME
	DWORD dwVolume;
#endif /* BSL_WAV_OUT_VOLUME */

	/*! 再生中フラグ */
	BOOL bPlaying;
	/*! 停止中フラグ */
	BOOL bStop;
	/*! 停止エラー */
	BOOL bStopError;
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		一時停止フラグ。
		BSL_WAV_OUT_PAUSE定義時のみ有効。
	*/
	BOOL bPausing;
	/*!
		一時停止完了フラグ。
		BSL_WAV_OUT_PAUSE定義時のみ有効。
	*/
	BOOL bPaused;
#endif /* BSL_WAV_OUT_PAUSE */
	/*! バッファインデックス */
	int nBufferIndex;
	/*! バッファブロック数 */
	int nBlockedBuffers;

#ifdef BSL_WAV_OUT_FADEOUT
	/*! フェードアウト処理をリセットするメソッド（内部使用専用） */
	void (*resetFadeout) (struct BSLWavOut *obj);
	/*! フェードアウト処理を開始するメソッド（内部使用専用） */
	void (*startFadeout) (struct BSLWavOut *obj);
	/*! フェードアウト処理を行うメソッド（内部使用専用） */
	void (*processFadeout) (struct BSLWavOut *obj, BSLWavIOData *buffer, long sampleFrames);
	/*! フェードアウト処理完了を取得するメソッド（内部使用専用） */
	BOOL (*isFadeoutDone) (struct BSLWavOut *obj);
	/*! フェードアウト係数 */
#ifdef BSL_WAV_IO_FLOAT
    BSLWavIOData aFadeout;
#else /* BSL_WAV_IO_FLOAT */
    long lFadeout;
#endif /* BSL_WAV_IO_FLOAT */
	/*! フェードアウト単位 */
#ifdef BSL_WAV_IO_FLOAT
	BSLWavIOData aFadeUnit;
#else /* BSL_WAV_IO_FLOAT */
    long lFadeUnit;
#endif /* BSL_WAV_IO_FLOAT */

	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	/*! フェードアウト後無音再生区間[sample] */
	long lFadeOutZeroSampleDef;
	/*! フェードアウト後無音再生区間用カウンタ[sample] */
	long lFadeOutZeroSample;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

#ifdef BSL_WAV_OUT_INCLUDE_THREAD
	/* <- only for thread */
	/*!
		オブジェクト破棄時に必要となるスレッド終了処理を行う。
		スレッド対応の制御オブジェクトのみ有効。
	*/
	BSLErr (*threadexit) (struct BSLWavOut *obj);
	/*!
		再生スレッドの停止処理を開始させる。
		スレッド対応の制御オブジェクトのみ有効。
	*/
	BSLErr (*threadstop) (struct BSLWavOut *obj);

	#if BSL_WINDOWS
	HANDLE pThread;
	#elif BSL_LINUX
	pthread_t pThread;
	BOOL bThread;
	#endif /* BSL_WINDOWS */

	/*! スレッド終了待ちフラグ */
	BOOL bThreadExit;
	/*! スレッドへスレッド停止（またはそのためのフェード開始）を伝えるためのフラグ */
	BOOL bThreadStop;
	/*! スレッドの終了待ちフラグ ? */
	BOOL bThreadWaitStopped;

	#ifdef BSL_WAV_DEBUG
	BOOL bThreadTestRestart;
	#endif /* BSL_WAV_DEBUG */
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトのBSLWavEnc::initialize()内にて行うこと。
		メモリ開放はbslWavOutExit()にて行われる。
	*/
	void *pInternalData; /* used for each upper object */
} BSLWavOut;

/* globals */

/* loclas */

/* function declarations */

void bslWavOut (BSLWavOut *obj, BSL_WAV_RENDER render, void *user);
void bslWavOutExit (BSLWavOut *obj);

BSLErr _bslWavOutInitialize (BSLWavOut *obj);
BSLErr _bslWavOutSetSampleRate (BSLWavOut *obj, int sampleRate);
BSLErr _bslWavOutSetBlockSize (BSLWavOut *obj, long blockSize);
BSLErr _bslWavOutSetBitsPerSample (BSLWavOut *obj, int bitsPerSample);
BSLErr _bslWavOutSetChannels (BSLWavOut *obj, int channels);
BSLErr _bslWavOutSetBuffers (BSLWavOut *obj, int buffers);

#ifdef BSL_WAV_OUT_PAUSE
BSLErr _bslWavOutPause (BSLWavOut *obj);
#endif /* BSL_WAV_OUT_PAUSE */
BSLErr _bslWavOutStop (BSLWavOut *obj);
void _bslWavOutStopped (BSLWavOut *obj, BSLErr error);
BSLErr _bslWavOutReset (BSLWavOut *obj);

#ifdef BSL_WAV_OUT_INCLUDE_THREAD

/*
	stop() = _bslWavOutStop_thread()
		bThreadStop = TRUE
		…
		threadstop ()
			bThreadStop = FALSE
			bThreadWaitStopped = TRUE
			_bslWavOutStop ()
				startFadeOut()
				…
				bStop = TRUE
				…
				if (isPlayDone ()) {
					stopped ()
				}
*/

BSLErr _bslWavOutStop_thread (BSLWavOut *obj);
BSLErr _bslWavOutThreadStop (BSLWavOut *obj);
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavOuth */
