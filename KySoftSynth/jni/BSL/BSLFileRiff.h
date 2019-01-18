/*
 *  BSLFileRiff.h - RIFF file base parser
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileRiff.h
	@brief
	RIFFファイル入力制御オブジェクト
*/

#ifndef __INCBSLFileRiffh
#define __INCBSLFileRiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSLFile.h"

/* defines */

#define RIFF_MARKER (BSL_FILE_MARKER ('R', 'I', 'F', 'F'))
#define LIST_MARKER (BSL_FILE_MARKER ('L', 'I', 'S', 'T'))

/* typedefs */

/*!
	RIFFファイル入力処理を行う基本制御オブジェクト
	- 使用手順
		-# bslFileRiff()により制御オブジェクトを構築
		-# 下記のメソッドの継承を設定する
			- BSLFileRiff::getListChunk()
			- BSLFileRiff::getChunk()
			- BSLFileRiff::checkTypeHeader()
			- BSLFileRiff::checkRiffHeader()
		-# BSLFile制御オブジェクトを構築
		-# BSLFileRiff::parse ()によりRIFF構造のパースを行う
		-# 任意処理
		-# bslFileExit()によりBSLFile制御オブジェクトを破棄
*/
typedef struct BSLFileRiff
{
	/*! RIFFのparse処理を行う */
	BSLErr (*parse) (struct BSLFileRiff *obj, BSLFile *file, void *user);
	/*! LIST Chunkの取得処理を行う */
	BSLErr (*getListChunk) (struct BSLFileRiff *obj, DWORD parentHeader, DWORD header, unsigned long offset, unsigned long size, int level, void *user);
	/*! Chunkの取得処理を行う */
	BSLErr (*getChunk) (struct BSLFileRiff *obj, DWORD parentHeader, DWORD header, unsigned long offset, unsigned long size, int level, void *user);
	/*! RIFFヘッダの確認処理を行う */
	BSLErr (*checkRiffHeader) (struct  BSLFileRiff *obj, DWORD header, void *user);
	/*! Typeヘッダの確認処理を行う */
	BSLErr (*checkTypeHeader) (struct  BSLFileRiff *obj, DWORD header, void *user);
	/*! LIST Chunkのparse処理を行う */
	BSLErr (*parseListChunk) (struct BSLFileRiff *obj, DWORD header, unsigned long offset, unsigned long size, int level, void *user);

	BSLFile *file;
} BSLFileRiff;

/* globals */

/* locals */

/* function declarations */

BSLErr bslFileRiff (BSLFileRiff *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileRiffh */
