/*
 *  BSLMidParserTrack.h
 *
 *  Copyright (c) 2002-2010 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserTrack.h
	@brief
	MIDI File Track制御オブジェクト
*/

#ifndef __INCBSLMidParaserTrackh
#define __INCBSLMidParaserTrackh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLFile.h"
#include "BSLMid.h"

/* defines */

/* typedefs */

typedef struct BSLMidParserTrack
{
	BSLErr (*initialize) (struct BSLMidParserTrack *obj);
	BSLErr (*parse) (struct BSLMidParserTrack *obj, void *parent, unsigned long offset, unsigned long size);

	BSLErr (*readEvent) (struct BSLMidParserTrack *obj, BSLMidEvent *event);
	BSLErr (*incEvent) (struct BSLMidParserTrack *obj, BSLMidEvent *event);
	DWORD (*getTick) (struct BSLMidParserTrack *obj);

	BSLErr (*rewind) (struct BSLMidParserTrack *obj);
	void (*close) (struct BSLMidParserTrack *obj);
	BOOL (*isFinished) (struct BSLMidParserTrack *obj);

	BSLFile *(*getFile) (struct BSLMidParserTrack *obj);
	void (*setPort) (struct BSLMidParserTrack *obj, BSL_MID_PORT port);
	BSL_MID_PORT (*getPort) (struct BSLMidParserTrack *obj);
#ifdef BSL_MID_INCLUDE_TRACK_NAME
	LPCTSTR (*getName) (struct BSLMidParserTrack *obj);
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslMidParserExit()より呼び出される。
	*/
	void (*finalize) (struct BSLMidParserTrack *obj);


	BSLFile *pFile;
	BSL_MID_PORT nPort;
#ifdef BSL_MID_INCLUDE_TRACK_NAME
	LPTSTR pName;
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

	BOOL bIsFinished;

	unsigned long ulStart;
	unsigned long ulPosition;
	unsigned long ulSize;
	DWORD dwTick;
	BYTE byRunning;

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトのCrlMidParser::parse()内にて行うこと。
		メモリ開放はbslMidParserExit()にて行われる。
	*/
	void *pInternalData; /* used for each upper object */
} BSLMidParserTrack;

/* globals */

/* locals */

/* function declarations */

void bslMidParserTrack (BSLMidParserTrack *obj);
void bslMidParserTrackExit (BSLMidParserTrack *obj);

BSLErr _bslMidParserTrackInitialize (BSLMidParserTrack *obj);
BSLErr _bslMidParserTrackRewind (BSLMidParserTrack *obj);

int bslMidParserTrackGetUsedChannel (BSLMidParserTrack *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMidParaserTrackSmfh */
