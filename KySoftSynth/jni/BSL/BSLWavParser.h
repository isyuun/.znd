/*
 *  BSLWavParser.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavParser.h
	@brief
	Wave File入力制御オブジェクト
*/

#ifndef __INCBSLWavParserh
#define __INCBSLWavParserh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLFile.h"
#include "BSLWav.h"

/* defines */

/* typedefs */

/*!
	Wave File入力基本制御オブジェクト。
	各File形式ごとに以下の継承オブジェクトが存在する。
	- BSLWavParserAiff.c / BSLWavParserAiff.h : AIFF
	- BSLWavParserRiff.c / BSLWavParserRiff.h : RIFF
	- BSLWavParserG726.c / BSLWavParserG726.h : G726 ADPCM
	- BSLWavParserVox.c / BSLWavParserVox.h : Dialogic Vox ADPCM
	- BSLWavParserOki.c / BSLWavParserOki.h : Oki LSI Audio
	- BSLWavParserRaw.c / BSLWavParserRaw.h : Raw Audio
	- BSLWavParserAuto.c / BSLWavParserAuto.h : フォーマット自動判別型
	
	- 使用手順
		-# BSLFile制御オブジェクトによりWave FileをOpen
		-# bslWavParser* ()により制御オブジェクトを構築
		-# BSLWavParser::parse()によりパース処理を行う
		-# BSLWavParser::read() ...によりWave Data取得処理を行う
		-# BSLWavParser::seek() ....によりWave Data取得位置を設定する
		-# bslWavParserExit()により制御オブジェクトを破棄
		-# BSLFile制御オブジェクトを破棄
*/
typedef struct BSLWavParser
{
	/*! パース処理を行うメソッド */
	BSLErr (*parse) (struct BSLWavParser *obj, BSLFile *file);

	/*!
		Waveデータ読み出しを行うメソッド。
		レンダリング処理されたデータ（圧縮ストリーム等の場合はデコードされたデータ）を取得するために使用する。
		このメソッドの呼び出し状況によってBSLWavParser::getPlayedFrames()によって得られる読み出し位置が自動的に移動する。
		BSLWavParser::seek()、BSLWavParser::read()とは排他的に呼び出すこと。
	*/
	BSLErr (*read) (struct BSLWavParser *obj, void *data, long sampleFrames, long *readFrames);
	/*!
	読み出し位置[frame]の設定を行うメソッド。
	ファイルタイプ（BSL_WAV_FILE_TYPE）によってはシーク処理未対応の場合があるため、
	本メソッドの戻り値がBSL_OK以外の場合にはBSLWavParser::getPlayedFramesによって再度現在の読み出し位置を確認する必要があることに注意。
	*/
	BSLErr (*seek) (struct BSLWavParser *obj, DWORD frame);
	/*! 読み出し位置[frame]を取得するメソッド */
	DWORD (*getPlayedFrames) (struct BSLWavParser *obj);

	/*!
		Waveデータ読み出しを行うメソッド。
		ストリームデータ自体（圧縮ストリーム等の場合はデコード前のデータ）を取得するために使用する。
		このメソッドの呼び出し状況はBSLWavParser::getPlayedFrames()によって得られる読み出し位置には関与しない。
		offset、sizeはともにByte数を指す。
		BSLWavParser::read()、BSLWavParser::seek()とは排他的に呼び出すこと。
	*/
	BSLErr (*readStream) (struct BSLWavParser *obj, void *data, DWORD offset, DWORD size);

	/* Waveデータ（ストリーム自体）のサイズ[Byte]を取得するメソッド */
	DWORD (*getStreamSize) (struct BSLWavParser *obj);

	/*! ファイルタイプを設定するメソッド */
	BSLErr  (*setType) (struct BSLWavParser *obj, BSL_WAV_FILE_TYPE type);
	/*! ファイルタイプを取得するメソッド */
	BSL_WAV_FILE_TYPE (*getType) (struct BSLWavParser *obj);

	/*! コーデックタイプを設定するメソッド */
	BSLErr (*setCodec) (struct BSLWavParser *obj, BSL_WAV_CODEC codec);
	/*! コーデックタイプを取得するメソッド */
	BSL_WAV_CODEC (*getCodec) (struct BSLWavParser *obj);

	/*! 総フレーム数[sample]を設定するメソッド */
	BSLErr (*setFrames) (struct BSLWavParser *obj, DWORD frames);
	/*! 総フレーム数[sample]を取得するメソッド */
	DWORD (*getFrames) (struct BSLWavParser *obj);

	/*! サンプリング周波数[Hz]を設定するメソッド */
	BSLErr (*setSampleRate) (struct BSLWavParser *obj, int sampleRate);
	/*! サンプリング周波数[Hz]を取得するメソッド */
	int (*getSampleRate) (struct BSLWavParser *obj);

	/*!
		ストリームのビット精度[bit]を設定するメソッド。
		レンダリング処理時のビット精度とは異なる場合があるため注意すること。
		Rawファイル等を除いて一般にはオブジェクト自身で設定されるもので、外部より設定は行わない。
	*/
	BSLErr (*setBitsPerSample) (struct BSLWavParser *obj, int bitsPerSample);
	/*!
		ストリームのビット精度[bit]を取得するメソッド。
		レンダリング処理時のビット精度とは異なる場合があるため注意すること。
	*/
	int (*getBitsPerSample) (struct BSLWavParser *obj);

	/*!
		レンダリング処理時のビット精度[bit]を設定するメソッド。
		BSL_WAV_CODEC_PCM以外のデコーダを使用するファイルの場合、
		ストリーム処理時のビット精度とは異なる場合があるため注意すること。
		一般にはオブジェクト自身で設定されるもので、外部より設定は行わない。
	*/
	BSLErr (*setBitsPerSampleRendering) (struct BSLWavParser *obj, int bitsPerSample);
	/*!
		レンダリング処理時のビット精度[bit]を取得するメソッド。
		BSL_WAV_CODEC_PCM以外のデコーダを使用するファイルの場合、
		ストリーム処理時のビット精度とは異なる場合があるため注意すること。
	*/
	int (*getBitsPerSampleRendering) (struct BSLWavParser *obj);

	/*!
		ビットレート[bps]を設定するメソッド。
		未対応のビットレートが存在する場合には、継承したメソッド内で判別を行うこと。
	*/
	BSLErr (*setBitRate) (struct BSLWavParser *obj, DWORD bitRate);

	/*!
		ビットレート[bps]を取得するメソッド。
	*/
	DWORD (*getBitRate) (struct BSLWavParser *obj);

	/*! チャンネル数を設定するメソッド */
	BSLErr (*setChannels) (struct BSLWavParser *obj, int channels);
	/*! チャンネル数を取得するメソッド */
	int (*getChannels) (struct BSLWavParser *obj);

	/*!
		ストリームのバイト数/フレームを取得するメソッド。
		レンダリング処理時の値とは異なる場合があるため注意すること。
	*/
	int (*getBytesPerFrame) (struct BSLWavParser *obj);

	/*!
		レンダリング処理時のバイト数/フレームを取得するメソッド。
		ストリームの値とは異なる場合があるため注意すること。
	*/
	int (*getBytesPerFrameRendering) (struct BSLWavParser *obj);

	/*! Unsigned属性を設定するメソッド */
	BSLErr (*setUnsigned) (struct BSLWavParser *obj, BOOL flag);
	/*! Unsigned属性を取得するメソッド */
	BOOL (*getUnsigned) (struct BSLWavParser *obj);

	/*! Big Endian属性を設定するメソッド */
	BSLErr (*setBigEndian) (struct BSLWavParser *obj, BOOL flag);
	/*! Big Endian属性を取得するメソッド */
	BOOL (*getBigEndian) (struct BSLWavParser *obj);

	/*! Float属性を設定するメソッド */
	BSLErr (*setFloat) (struct BSLWavParser *obj, BOOL flag);
	/*! Float属性を取得するメソッド */
	BOOL (*getFloat) (struct BSLWavParser *obj);

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! タイトル文字列を設定するメソッド */
	void (*setTitle) (struct BSLWavParser *obj, LPCTSTR str);
	/*! タイトル文字列を取得するメソッド */
	void (*getTitle) (struct BSLWavParser *obj, LPTSTR str, int length);
	/*! タイトル文字列を取得するメソッド */
	LPTSTR (*getTitleAddr) (struct BSLWavParser *obj);

	/*! コピーライト文字列を設定するメソッド */
	void (*setCopyright) (struct BSLWavParser *obj, LPCTSTR str);
	/*! コピーライト文字列を取得するメソッド */
	void (*getCopyright) (struct BSLWavParser *obj, LPTSTR str, int length);
	/*! コピーライト文字列を取得するメソッド */
	LPTSTR (*getCopyrightAddr) (struct BSLWavParser *obj);
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*! コピーフラグを設定するメソッド */
	void (*setCopyEnable) (struct BSLWavParser *obj, BOOL flag);
	/*! コピーフラグを取得するメソッド */
	BOOL (*getCopyEnable) (struct BSLWavParser *obj);

	/*! 保存フラグを設定するメソッド */
	void (*setSaveEnable) (struct BSLWavParser *obj, BOOL flag);
	/*! 保存フラグを取得するメソッド */
	BOOL (*getSaveEnable) (struct BSLWavParser *obj);

	/*! LEDフラグを設定するメソッド */
	void (*setLEDUsing) (struct BSLWavParser *obj, BOOL flag);
	/*! LEDフラグを取得するメソッド */
	BOOL (*getLEDUsing) (struct BSLWavParser *obj);

	/*! VIBフラグを設定するメソッド */
	void (*setVIBUsing) (struct BSLWavParser *obj, BOOL flag);
	/*! VIBフラグを取得するメソッド */
	BOOL (*getVIBUsing) (struct BSLWavParser *obj);
#endif

#ifdef BSL_WAV_LOOPS
	int (*getLoopNum) (struct BSLWavParser *obj);
	BSLErr (*addLoop) (struct BSLWavParser *obj, BSL_WAV_LOOP *loop);
	void (*getLoop) (struct BSLWavParser *obj, int index, BSL_WAV_LOOP *loop);
#endif /* BSL_WAV_LOOPS */

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslWavParserExit()より呼び出される。
	*/
	void (*finalize) (struct BSLWavParser *obj);

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
	/*! ビットレート[bps] */
	DWORD dwBitRate;
	/*! チャンネル数 */
	int nChannels;
	/*! Unsignedフラグ */
	BOOL bUnsigned;
	/*! Big Endianフラグ */
	BOOL bBigEndian;
	/*! Floatフラグ */
	BOOL bFloat;

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

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! タイトル文字列 */
	TCHAR cTitle[BSL_WAV_FILE_TITLE_SIZE];
	/*! コピーライト文字列 */
	TCHAR cCopyright[BSL_WAV_FILE_COPYRIGHT_SIZE];
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

	/*! ファイル制御オブジェクト */
	BSLFile *pFile;
	/*! フォーマット情報を取得できたかを示すフラグ */
	BOOL bHasFormat;
	/*! データ情報を取得できたかを示すフラグ */
	BOOL bHasData;
	/*! データ開始アドレス */
	unsigned long ulStartPosition;
	/*! 読み出し位置アドレス */
	DWORD dwPlayedFrames;

	/*!
		ストリームデータ確保用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトにて行うこと。
		メモリ開放はbslWavParserExit()にて行われる。
	*/
	char *pInternalCache;
	/*! ストリームデータ確保用ポインタのサイズ[Byte] */
	DWORD dwInternalCacheSize;
	/*! ストリームデータ読み出し位置 */
	DWORD dwInternalCachePosition;
	/*! 未使用ストリームデータサイズ[Byte] */
	DWORD dwInternalCacheSizeLoaded;

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトのCrlWavParser::parse()内にて行うこと。
		メモリ開放はbslWavParserExit()にて行われる。
	*/
	void *pInternalData; /* used for each upper object */

#ifdef BSL_WAV_LOOPS
	int nLoops;
	BSL_WAV_LOOP loop[BSL_WAV_LOOPS];
#endif /* BSL_WAV_LOOPS */

} BSLWavParser;

/* globals */

/* locals */

/* function declarations */

void bslWavParser (BSLWavParser *obj);
void bslWavParserExit (BSLWavParser *obj);

BSLErr _bslWavParserSetFrames (BSLWavParser *obj, DWORD frames);
BSLErr _bslWavParserSetSampleRate (BSLWavParser *obj, int sampleRate);
BSLErr _bslWavParserSetBitsPerSample (BSLWavParser *obj, int bitsPerSample);
BSLErr _bslWavParserSetBitRate (BSLWavParser *obj, DWORD bitRate);
DWORD _bslWavParserGetBitRateLinear (BSLWavParser *obj);
BSLErr _bslWavParserSetChannels (BSLWavParser *obj, int channels);
BSLErr _bslWavParserSetUnsigned (BSLWavParser *obj, BOOL isUnsigned);
BSLErr _bslWavParserSetBigEndian (BSLWavParser *obj, BOOL isBigEndian);
BSLErr _bslWavParserSeek (BSLWavParser *obj, DWORD frame);
BSLErr _bslWavParserRead (BSLWavParser *obj, void *data, long sampleFrames, long *readFrames);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavParserh */
