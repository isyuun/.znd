/*
 *  BSLMidParser.h
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParser.h
	@brief
	MIDI File入力制御オブジェクト
*/

#ifndef __INCBSLMidParaserh
#define __INCBSLMidParaserh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLMid.h"
#include "BSLMidParserTrack.h"

/* defines */

typedef enum {
	/* file types */
	BSL_MID_FILE_TYPE_NONE = -1,

#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMF
	BSL_MID_FILE_TYPE_SMF,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMF */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	BSL_MID_FILE_TYPE_MCOMP,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMAF
	#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA2
	BSL_MID_FILE_TYPE_SMAF_MA2,
	#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA2 */
	#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA3
	BSL_MID_FILE_TYPE_SMAF_MA3,
	#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA3 */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMAF */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFi
	BSL_MID_FILE_TYPE_MFi,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFMP
	BSL_MID_FILE_TYPE_MFMP,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFMP */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_CMX
	BSL_MID_FILE_TYPE_CMX,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_CMX */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCDF
	BSL_MID_FILE_TYPE_MCDF,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCDF */
} BSL_MID_FILE_TYPE;

typedef enum
{
#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMF
	BSL_MID_FILE_FORMAT_SMF_0 = 0x0000,
	BSL_MID_FILE_FORMAT_SMF_1,
	BSL_MID_FILE_FORMAT_SMF_2,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMF */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	BSL_MID_FILE_FORMAT_MCOMP_SMF0 = 0x0010,
	BSL_MID_FILE_FORMAT_MCOMP_SMF1,
	BSL_MID_FILE_FORMAT_MCOMP_SMF2,
	BSL_MID_FILE_FORMAT_MCOMP_MFMP,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMAF
	BSL_MID_FILE_FORMAT_SMAF_MA2 = 0x0020,
	BSL_MID_FILE_FORMAT_SMAF_MA3,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMAF */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFi
	BSL_MID_FILE_FORMAT_MFi_1 = 0x0030,
	BSL_MID_FILE_FORMAT_MFi_2,
	BSL_MID_FILE_FORMAT_MFi_3,
	BSL_MID_FILE_FORMAT_MFi_4,
	BSL_MID_FILE_FORMAT_MFi_5,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFMP
	BSL_MID_FILE_FORMAT_MFMP = 0x0040,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFMP */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_CMX
	BSL_MID_FILE_FORMAT_CMX = 0x0050,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_CMX */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCDF
	BSL_MID_FILE_FORMAT_MCDF = 0x0060,
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCDF */
} BSL_MID_FILE_FORMAT;

typedef enum
{
	BSL_MID_EVENT_PROC_OK = 0,
	BSL_MID_EVENT_PROC_ERROR,
	BSL_MID_EVENT_PROC_BUFFER_FULL,
	BSL_MID_EVENT_PROC_FINISHED,
	BSL_MID_EVENT_PROC_LOOP_END,
	BSL_MID_EVENT_PROC_UNDEFINED,
} BSL_MID_EVENT_PROC;

enum {
	BSL_MID_STATUS_META = 0xFF, /* meta event status */

	/* meta event types */
	BSL_MID_META_TYPE_SEQ_NUM = 0x00, /* sequence number */
	BSL_MID_META_TYPE_TEXT = 0x01, /* text */
	BSL_MID_META_TYPE_COPYRIGHT = 0x02, /* copyright */
	BSL_MID_META_TYPE_NAME = 0x03, /* sequence/track name */
	BSL_MID_META_TYPE_INSTRUMENT_NAME = 0x04, /* instrument name */
	BSL_MID_META_TYPE_LYRIC = 0x05, /* lyric */
	BSL_MID_META_TYPE_MARKER = 0x06, /* marker */
	BSL_MID_META_TYPE_CUE = 0x07, /* cue point */
	BSL_MID_META_TYPE_CHANNEL_PREFIX = 0x20, /* channel prefix */
	BSL_MID_META_TYPE_PORT = 0x21,
	BSL_MID_META_TYPE_END = 0x2F, /* end of track chunk */
	BSL_MID_META_TYPE_SET_TEMPO = 0x51, /* set tempo */
	BSL_MID_META_TYPE_SMPTE_OFFSET = 0x54, /* smpte offset */
	BSL_MID_META_TYPE_TIME_SIGNATURE = 0x58, /* time signature */
	BSL_MID_META_TYPE_KEY = 0x59, /* key */
	BSL_MID_META_TYPE_SEQUENCER_SPECIFIC = 0x7F, /* sequencer specific */

	BSL_MID_READTICK_MAX = 0xFFFFFFFFUL,
};

/* typedefs */

/*!
	MIDI File入力基本制御オブジェクト。
	各File形式ごとに以下の継承オブジェクトが存在する。
	- BSLMidParserSmf.c / BSLMidParserSmf.h : SMF
	- BSLMidParserMcomp.c / BSLMidParserMcomp.h : M-compression
	...
	
	- 使用手順
		-# BSLFile制御オブジェクトによりMIDI FileをOpen
		-# bslMidParser* ()により制御オブジェクトを構築
		-# 下記のメソッドの継承を設定する
		  - BSLMidParser::chaseEvent() : MIDIイベント取得関数(Event Chasing)
		  - BSLMidParser::getEvent() : MIDIイベント取得関数
		-# BSLMidParser::parse()によりパース処理を行う（入力するファイル制御オブジェクトはBSLFileMemのみとする）
		-# BSLMidParser::rewind()によりファイル先頭へ移動
		-# BSLMidParser::sortEvent()によりイベント取得処理を行う（継承したメソッドBSLMidParser::chaseEvent() / BSLMidParser::getEvent()が呼ばれる）
		-# bslMidParserExit()により制御オブジェクトを破棄
		-# BSLFile制御オブジェクトを破棄
*/
typedef  struct BSLMidParser
{
	/*!
		パース処理を行う。
		入力するファイル制御オブジェクトはBSLFileMemのみとする。
	*/
	BSLErr (*parse) (struct BSLMidParser *obj, BSLFile *file);

	/*! 演奏開始位置へ移動 */
	BSLErr (*rewind) (struct BSLMidParser *obj, DWORD startReadtick);

	/*! MIDIイベントソート処理 */
	BSL_MID_EVENT_PROC (*sortEvent) (struct BSLMidParser *obj, DWORD startReadtick, void *user);
	/*!
		MIDIイベント取得（Event Chasing)
		Event Chasing処理実装時にはアプリケーション側でメソッドを継承すること
	*/
	BSL_MID_EVENT_PROC (*chaseEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user);
	/*!
		MIDIイベント取得
		必ずアプリケーション側でメソッドを継承すること
	*/
	BSL_MID_EVENT_PROC (*getEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user);

	/*! ファイルタイプを取得するメソッド */
	BSL_MID_FILE_TYPE (*getType) (struct BSLMidParser *obj);
	/*! ファイルフォーマットを取得するメソッド */
	BSL_MID_FILE_FORMAT (*getFormat) (struct BSLMidParser *obj);
	/*! 分解能[TPQN]を取得するメソッド */
	int (*getDivision) (struct BSLMidParser *obj);
	/*! 分解能[TPQN]を設定するメソッド */
	void (*setDivision) (struct BSLMidParser *obj, int division, int clocksPerBeat);
	/*! トラック数を取得するメソッド */
	int (*getTracks) (struct BSLMidParser *obj);

	/*! BSLMidParserTrack制御オブジェクトを取得するメソッド */
	BSLMidParserTrack *(*getTrack) (struct BSLMidParser *obj, int index);
	/*! 内部使用（BSLMidParser::sortEvent()）のみ */
	BOOL (*closeTrack) (struct BSLMidParser *obj, int index);

	/*! BSLFile制御オブジェクトを取得するメソッド */
	BSLFile *(*getFile) (struct BSLMidParser *obj);

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	/*! デコード結果を取得するメソッド */
	char *(*getDecoded) (struct BSLMidParser *obj);
	/*! デコード後のファイルサイズを取得するメソッド */
	DWORD (*getDecodedSize) (struct BSLMidParser *obj);
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslMidParserExit()より呼び出される。
	*/
	void (*finalize) (struct BSLMidParser *obj);


	BSLFile *pFile;

	BSL_MID_FILE_TYPE nType;
	BSL_MID_FILE_FORMAT nFormat;
	int nDivision;
	int nTracks;

	BSLMidParserTrack tTrack[BSL_MID_TRACKS];

	int nPlayedTracks; /* number of played tracks */
	int nRemainedTracks; /* number of remained trks */

	DWORD dwStartReadtick; /* tick for play start */

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	DWORD dwMidiclockReadtick; /* current tick for midiclk */
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	/*! デコード結果 */
	char *pDecoded;
	/*! デコード結果のサイズ */
	DWORD dwDecodedSize;
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトのCrlMidParser::parse()内にて行うこと。
		メモリ開放はbslMidParserExit()にて行われる。
	*/
	void *pInternalData; /* used for each upper object */
	
	/*! コールバック時のユーザー変数 */
	void *pCallbackUser;
} BSLMidParser;

/* globals */

/* locals */

/* function declarations */

void bslMidParser (BSLMidParser *obj);
void bslMidParserExit (BSLMidParser *obj);

BSLErr _bslMidParserRewind (BSLMidParser *obj, DWORD startReadtick);

BSLErr bslMidParserGetInfo (BSLMidParser *obj, DWORD *totaltick, DWORD *totaltime);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMidParaserh */
