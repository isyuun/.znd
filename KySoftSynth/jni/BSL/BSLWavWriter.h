/*
 *  BSLWavWriter.h
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriter.h
	@brief
	Wave File出力制御オブジェクト
*/

#ifndef __INCBSLWavWriterh
#define __INCBSLWavWriterh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLFileSave.h"
#include "BSLWav.h"
#include "BSLWavRender.h"
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	#include "BSLMFiUtil.h"
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

/* defines */

/* typedefs */

/*! Update関数型 */
typedef int (*BSL_WAV_UPDATER) (int percent, void *param);

/*! Stream出力関数型 */
typedef BSLErr (*BSL_WAV_WRITER_STREAM) (BSLFileSave *file, DWORD *size, void *user);

/*!
	Wave File出力基本制御オブジェクト。
	各File形式ごとに以下の継承オブジェクトが存在する。
	- BSLWavWriterAiff.c / BSLWavWriterAiff.h : AIFF
	- BSLWavWriterRiff.c / BSLWavWriterRiff.h : RIFF
	- BSLWavWriterG726.c / BSLWavWriterG726.h : G726 ADPCM
	- BSLWavWriterVox.c / BSLWavWriterVox.h : Dialogic Vox ADPCM
	- BSLWavWriterOki.c / BSLWavWriterOki.h : Oki LSI Audio
	- BSLWavWriterRaw.c / BSLWavWriterRaw.h : Raw Audio

	- 使用手順
		- Rendering出力
			-# BSLWavRender制御オブジェクトを構築
			-# bslWavWriter* ()により制御オブジェクトを構築
			-# BSLWavWriter::save()により出力処理を行う
			-# bslWavWriterExit()により制御オブジェクトを破棄
			-# BSLWavRender制御オブジェクトを破棄
		- Stream出力
			-# bslWavWriter* ()により制御オブジェクトを構築
			-# BSLWavWriter::saveStream()により出力処理を行う
			-# bslWavWriterExit()により制御オブジェクトを破棄
*/
typedef struct BSLWavWriter
{
	/*! Wave Rendering処理制御オブジェクト（BSLWavRender）を使ったWaveファイル出力処理を行う */
	BSLErr (*save) (struct BSLWavWriter *obj, BSLFileSave *file, BSLWavRender *render, BSL_WAV_UPDATER updater,  void *updateParam);

	/*! Stream出力関数を使ったWaveファイル出力処理を行う */
	BSLErr (*saveStream) (struct BSLWavWriter *obj, BSLFileSave *file, BSL_WAV_WRITER_STREAM stream, void *streamParam, BSL_WAV_UPDATER updater,  void *updateParam);

	/*! ファイルタイプを設定するメソッド */
	BSLErr  (*setType) (struct BSLWavWriter *obj, BSL_WAV_FILE_TYPE type);
	/*! ファイルタイプを取得するメソッド */
	BSL_WAV_FILE_TYPE (*getType) (struct BSLWavWriter *obj);

	/*! コーデックタイプを設定するメソッド */
	BSLErr (*setCodec) (struct BSLWavWriter *obj, BSL_WAV_CODEC codec);
	/*! コーデックタイプを取得するメソッド */
	BSL_WAV_CODEC (*getCodec) (struct BSLWavWriter *obj);

	/*! 総フレーム数[sample]を設定するメソッド */
	BSLErr (*setFrames) (struct BSLWavWriter *obj, DWORD frames);
	/*! 総フレーム数[sample]を取得するメソッド */
	DWORD (*getFrames) (struct BSLWavWriter *obj);

	/*!
		サンプリング周波数[Hz]を設定するメソッド。
		未対応のサンプリング周波数が存在する場合には、
		継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setSampleRate) (struct BSLWavWriter *obj, int sampleRate);
	/*! サンプリング周波数[Hz]を取得するメソッド */
	int (*getSampleRate) (struct BSLWavWriter *obj);

	/*!
		ストリームのビット精度[bit]を設定するメソッド。
		未対応のビット精度が存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setBitsPerSample) (struct BSLWavWriter *obj, int bitsPerSample);

	/*!
		ストリームのビット精度[bit]を取得するメソッド。
	*/
	int (*getBitsPerSample) (struct BSLWavWriter *obj);

	/*!
		レンダリング処理時のビット精度[bit]を設定するメソッド。
		ここで設定されるビット精度値はBSLWavWriter::save()実行時に行われる
		レンダリング処理において入力されるデータのビット精度を指す。
		BSL_WAV_CODEC_PCM以外のエンコーダを用いてファイル出力する場合には、
		そのエンコーダに入力可能なビット精度を指すものであって、
		出力されるファイルのビット精度ではないことに注意すること。
	*/
	BSLErr (*setBitsPerSampleRendering) (struct BSLWavWriter *obj, int bitsPerSample);

	/*!
		レンダリング処理時のビット精度[bit]を取得するメソッド。
		ここで取得されるビット精度値はBSLWavWriter::save()実行時に行われる
		レンダリング処理において入力されるデータのビット精度を指す。
		BSL_WAV_CODEC_PCM以外のエンコーダを用いてファイル出力する場合には、
		そのエンコーダに入力可能なビット精度を指すものであって、
		出力されるファイルのビット精度ではないことに注意すること。
	*/
	int (*getBitsPerSampleRendering) (struct BSLWavWriter *obj);

	/*!
		ビットレート[bps]を設定するメソッド。
		未対応のビットレートが存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setBitRate) (struct BSLWavWriter *obj, DWORD bitRate);

	/*!
		ビットレート[bps]を取得するメソッド。
	*/
	DWORD (*getBitRate) (struct BSLWavWriter *obj);

	/*!
		チャンネル数を設定するメソッド。
		未対応のチャンネル数が存在する場合には、
		継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setChannels) (struct BSLWavWriter *obj, int channels);
	/*! チャンネル数を取得するメソッド */
	int (*getChannels) (struct BSLWavWriter *obj);

	/*!
		ストリームのバイト数/フレームを取得するメソッド。
		レンダリング処理時の値とは異なる場合があるため注意すること。
	*/
	int (*getBytesPerFrame) (struct BSLWavWriter *obj);

	/*!
		レンダリング処理時のバイト数/フレームを取得するメソッド。
		ストリームの値とは異なる場合があるため注意すること。
	*/
	int (*getBytesPerFrameRendering) (struct BSLWavWriter *obj);

	/*!
		Unsigned属性を設定するメソッド。
		未対応の属性が存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setUnsigned) (struct BSLWavWriter *obj, BOOL flag);
	/*! Unsigned属性を取得するメソッド */
	BOOL (*getUnsigned) (struct BSLWavWriter *obj);

	/*!
		Big Endian属性を設定するメソッド。
		未対応の属性が存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setBigEndian) (struct BSLWavWriter *obj, BOOL flag);
	/*! Big Endian属性を取得するメソッド */
	BOOL (*getBigEndian) (struct BSLWavWriter *obj);

	/*!
		Float属性を設定するメソッド。
		未対応の属性が存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setFloat) (struct BSLWavWriter *obj, BOOL flag);
	/*! Float属性を取得するメソッド */
	BOOL (*getFloat) (struct BSLWavWriter *obj);

    /*!
     レンダリング処理時のFloat属性を設定するメソッド。
     未対応の属性が存在する場合には、継承したメソッド内で判別を行うこと。
     */
    BSLErr (*setFloatRendering) (struct BSLWavWriter *obj, BOOL flag);
    /*! レンダリング処理時のFloat属性を取得するメソッド */
    BOOL (*getFloatRendering) (struct BSLWavWriter *obj);
    
#ifdef BSL_WAV_FILE_TITLE_SIZE
	/*!
		タイトル文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setTitle) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		タイトル文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getTitle) (struct BSLWavWriter *obj, LPTSTR str, int length);
	
	DWORD (*getTitleSize) (struct BSLWavWriter *obj);

	/*!
		タイトル文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	LPCTSTR (*getTitleAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
	/*!
		バージョン文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setVersion) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		バージョン文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getVersion) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getVersionSize) (struct BSLWavWriter *obj);

	/*!
		バージョン文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	LPCTSTR (*getVersionAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
	/*!
		日付情報文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setDate) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		日付情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getDate) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getDateSize) (struct BSLWavWriter *obj);

	/*!
		日付情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	LPCTSTR (*getDateAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
	/*!
		コピーライト文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setCopyright) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		コピーライト文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getCopyright) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getCopyrightSize) (struct BSLWavWriter *obj);

	/*!
		コピーライト文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	LPCTSTR (*getCopyrightAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
	/*!
		データ管理及び保護情報文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setProtect) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		データ管理及び保護情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getProtect) (struct BSLWavWriter *obj, LPTSTR str, int length);

	LPCTSTR (*getProtectAddr) (struct BSLWavWriter *obj);

	DWORD (*getProtectSize) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
	/*!
		著作権情報文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setAuthor) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		著作権情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getAuthor) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getAuthorSize) (struct BSLWavWriter *obj);

	LPCTSTR (*getAuthorAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
	/*!
		サポート情報文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setSupport) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		サポート情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*getSupport) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getSupportSize) (struct BSLWavWriter *obj);

	LPCTSTR (*getSupportAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_SUPT_SIZE */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*!
		コピーフラグを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setCopyEnable) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		コピーフラグを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BOOL (*getCopyEnable) (struct BSLWavWriter *obj);

	/*!
		保存フラグを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setSaveEnable) (struct BSLWavWriter *obj, BOOL flag);

	/*!
		保存フラグを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BOOL (*getSaveEnable) (struct BSLWavWriter *obj);

	/*!
		LEDフラグを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setLEDUsing) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		LEDフラグを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BOOL (*getLEDUsing) (struct BSLWavWriter *obj);

	/*!
		VIBフラグを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setVIBUsing) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		VIBフラグを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BOOL (*getVIBUsing) (struct BSLWavWriter *obj);
#endif

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
	/*!
		再配布付加識別子を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setMFiSorc) (struct BSLWavWriter *obj, BYTE sorc);
	/*!
		再配布付加識別子を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BYTE (*getMFiSorc) (struct BSLWavWriter *obj);

	/*!
		ノートメッセージ長文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setMFiNoteMessLength) (struct BSLWavWriter *obj, WORD length);

	/*!
		ノートメッセージ長文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	WORD (*getMFiNoteMessLength) (struct BSLWavWriter *obj);

	/*!
		拡張ステータスAメッセージ長情報文字列を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setMFiExstStatusALength) (struct BSLWavWriter *obj, WORD length);

	/*!
		拡張ステータスAメッセージ長情報文字列を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	WORD (*getMFiExstStatusALength) (struct BSLWavWriter *obj);
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*!
		再生時間を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setPlayTime) (struct BSLWavWriter *obj, DWORD time);
	/*!
		再生時間を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	DWORD (*getPlayTime) (struct BSLWavWriter *obj);

	/*!
		再生後無音時間を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setBlankTime) (struct BSLWavWriter *obj, DWORD time);
	/*!
		再生後無音時間を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	DWORD (*getBlankTime) (struct BSLWavWriter *obj);
	
	/*!
		オーディオボリュームを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setAudioVolume) (struct BSLWavWriter *obj, DWORD volume);
	/*!
		オーディオボリュームを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	DWORD (*getAudioVolume) (struct BSLWavWriter *obj);

	/*!
		3Dの有効/無効を設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*set3DEfx) (struct BSLWavWriter *obj, BOOL efx);
	/*!
		3Dの有効/無効を取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	BOOL (*get3DEfx) (struct BSLWavWriter *obj);
	
	/*!
		メーカーIDを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setMakerID) (struct BSLWavWriter *obj, int nMakerID);
	/*!
		メーカーIDを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	int (*getMakerID) (struct BSLWavWriter *obj);
	
	/*!
		MFiバージョンを設定するメソッド。
		対応フォーマットのみ作用する。
	*/
	void (*setMFiVersion) (struct BSLWavWriter *obj, int nMFiVersion);
	/*!
		MFiバージョンを取得するメソッド。
		対応フォーマットのみ作用する。
	*/
	int (*getMFiVersion) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
	int (*getLoopNum) (struct BSLWavWriter *obj);
	BSLErr (*addLoop) (struct BSLWavWriter *obj, BSL_WAV_LOOP *loop);
	BSLErr (*deleteLoop) (struct BSLWavWriter *obj, int index);
	void (*getLoop) (struct BSLWavWriter *obj, int index, BSL_WAV_LOOP *loop);
#endif /* BSL_WAV_LOOPS */

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslWavWriterExit()より呼び出される。
	*/
	void (*finalize) (struct BSLWavWriter *obj);

	/*!
		Wave Rendering処理制御オブジェクト（BSLWavRender）を使ったファイル出力関数を行う内部関数。
		継承オブジェクトで必ずメソッドを継承すること。
		本メソッドはsaveメソッドから呼ばれるもので、外部より呼び出すことは行わない。
	*/
	BSL_FILE_SAVE _save;

	/*!
		Stream出力関数を使ったファイル出力関数を行う内部関数。
		継承オブジェクトで必ずメソッドを継承すること。
		本メソッドはsaveメソッドから呼ばれるもので、外部より呼び出すことは行わない。
	*/
	BSL_FILE_SAVE _saveStream;

	/*! Stream出力処理用パラメータ */
	void *pUpdateParam;

	/*! ファイルタイプ */
	BSL_WAV_FILE_TYPE nType;
	/*! コーデックタイプ */
	BSL_WAV_CODEC nCodec;
	/*! 総フレーム数[sample] */
	DWORD dwFrames;
	/*! サンプリング周波数[Hz] */
	int nSampleRate;
	/*! ビット精度[bit] */
	int nBitsPerSample;
	/*! レンダリング処理時のビット精度[bit] */
	int nBitsPerSampleRendering;
	/*! ビットレート[bps] */
	DWORD dwBitRate;
	/*! チャンネル数 */
	int nChannels;
	/*! Unsignedフラグ */
	BOOL bUnsigned;
	/*! Big Endianフラグ */
	BOOL bBigEndian;
	/*! Floatフラグ (出力ファイル) */
	BOOL bFloat;
    /*! Floatフラグ (レンダリング処理) */
    BOOL bFloatRendering;

#ifdef BSL_WAV_FILE_TITLE_SIZE
	/*! タイトル文字列 */
	TCHAR cTitle[BSL_WAV_FILE_TITLE_SIZE];
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
	/*! バージョン情報文字列 */
	TCHAR cVersion[BSL_WAV_FILE_VERSION_SIZE];
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
	/*! 日付情報文字列 */
	TCHAR cDate[BSL_WAV_FILE_DATE_SIZE];
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
	/*! コピーライト文字列 */
	TCHAR cCopyright[BSL_WAV_FILE_COPYRIGHT_SIZE];
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
	/*! データ管理及び保護情報文字列 */
	TCHAR cProtect[BSL_WAV_FILE_PROT_SIZE];
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
	/*! 著作権管理情報文字列 */
	TCHAR cAuthor[BSL_WAV_FILE_AUTH_SIZE];
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
	/*! サポート情報文字列 */
	TCHAR cSupport[BSL_WAV_FILE_SUPT_SIZE];
#endif /* BSL_WAV_FILE_SUPT_SIZE */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*! コピーフラグ */
	BOOL bCopyEnable;
	/*! 保存フラグ */
	BOOL bSaveEnable;
	/*! LEDフラグ */
	BOOL bLEDUsing;
	/*! VIBフラグ */
	BOOL bVIBUsing;
#endif

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
	/*! 再配布付加識別子 */
	BYTE  byMFiSorc;
	/*! ノートメッセージ長情報文字列 */
	WORD wMFiNoteMessLength;
	/*! 拡張ステータスAメッセージ長情報文字列 */
	WORD wMFiExstStatusALength;
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! 演奏時間 */
	DWORD dwPlayTime;
	/*! 演奏終了後の無音時間 */
	DWORD dwBlankTime;
	/*! オーディオボリューム */
	DWORD dwAudioVolume;
	/*! 3D効果の有無 */
	BOOL  b3DEfx;
	/*! バージョン情報*/
	int   nMFiVersion;

	/*! 端末のメーカーID*/
	int	  nMakerID;
	/*! MFiイベント操作 */
	BSLMFiUtil MFiUtil;
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
	int nLoops;
	BSL_WAV_LOOP loop[BSL_WAV_LOOPS];
#endif /* BSL_WAV_LOOPS */

	/*! アップデート処理関数 */
	BSL_WAV_UPDATER pUpdater;
	/*! アップデート処理用パラメータ */
	void *pStreamParam;

	/*!
		ストリームデータ確保用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトにて行うこと。
		メモリ開放はbslWavParserExit()にて行われる。
	*/
	void *pInternalCache;
	/*! ストリームデータ確保用ポインタのサイズ[Byte] */
	DWORD dwInternalCacheSize;

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトにて行うこと。
		メモリ開放はbslWavWriterExit()にて行われる。
	*/
	void *pInternalData;
} BSLWavWriter;

/* globals */

/* locals */

/* function declarations */

void bslWavWriter (BSLWavWriter *obj);
void bslWavWriterExit (BSLWavWriter *obj);

BSLErr _bslWavWriterSetType (BSLWavWriter *obj, BSL_WAV_FILE_TYPE type);
BSLErr _bslWavWriterSetCodec (BSLWavWriter *obj, BSL_WAV_CODEC codec);
BSLErr _bslWavWriterSetFrames (BSLWavWriter *obj, DWORD frames);
BSLErr _bslWavWriterSetSampleRate (BSLWavWriter *obj, int sampleRate);
BSLErr _bslWavWriterSetBitsPerSample (BSLWavWriter *obj, int bitsPerSample);
BSLErr _bslWavWriterSetBitsPerSampleRendering (BSLWavWriter *obj, int bitsPerSampleRendering);
BSLErr _bslWavWriterSetBitRate (BSLWavWriter *obj, DWORD bitRate);
DWORD _bslWavWriterGetBitRateLinear (BSLWavWriter *obj);
BSLErr _bslWavWriterSetChannels (BSLWavWriter *obj, int channels);
BSLErr _bslWavWriterSetUnsigned (BSLWavWriter *obj, BOOL isUnsigned);
BSLErr _bslWavWriterSetBigEndian (BSLWavWriter *obj, BOOL isBigEndian);
BSLErr _bslWavWriterSetFloat (BSLWavWriter *obj, BOOL flag);
BSLErr _bslWavWriterBounce (BSLFileSave *file, BSLWavWriter *obj, BSLWavRender *render, DWORD *renderedFrames);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavWriterh */
