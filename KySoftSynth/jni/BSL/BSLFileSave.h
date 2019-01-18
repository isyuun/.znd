/*
 *  BSLFileSave.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileSave.h
	@brief
	ファイル出力制御オブジェクト
*/

#ifndef __INCBSLFileSaveh
#define __INCBSLFileSaveh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSLFile.h"

/* defines */

/*! ファイルタイプ */
typedef enum  {
	/*! ファイル */
	BSL_FILE_SAVE_TYPE_FILE,
	/*! メモリ */
	BSL_FILE_SAVE_TYPE_MEM,
	/*! ダミー */
	BSL_FILE_SAVE_TYPE_DUMMY
} BSL_FILE_SAVE_TYPE;

/* typedefs */

/*!
	ファイル出力処理を行う基本制御オブジェクト
	各処理対象毎に以下の継承オブジェクトが存在する。
	- BSLFileSave.c / BSLFileSave.h : ストリームI/O型
	- BSLFileSaveMem.c / BSLFileMemSave.h : メモリI/O型
	- 使用手順
		-# bslFileSave()により制御オブジェクトを構築
		-# コールバック関数 (BSL_FILE_SAVE)を用意する
		-# BSLFileSave::save()を実行
		-# bslFileSaveExit()により制御オブジェクトを破棄
*/
typedef struct BSLFileSave
{
#ifndef BSL_FILE_NO_SYSTEM
	/*! 指定のパスに出力対象を設定する*/
	BSLErr (*setDestination) (struct BSLFileSave *obj, LPCTSTR filename);
#endif /* BSL_FILE_NO_SYSTEM */

	/*! 指定のメモリに出力対象を設定する */
	BSLErr (*setDestinationMem) (struct BSLFileSave *obj, char *data, DWORD size);

	/*! ファイルを保存する */
	BSLErr (*save) (struct BSLFileSave *obj, BSLErr (*save) (struct BSLFileSave *obj, void *user, void *userParam), void *user, void *userParam);

	/*! データの書き出しを行う */
	BSLErr (*write) (struct BSLFileSave *obj, void *data, unsigned long size);
	/*! データの読み出しを行う */
	BSLErr (*read) (struct BSLFileSave *obj, void *data, unsigned long size);
	/*! 指定された位置にファイル ポインタを移動する */
	BSLErr (*seek) (struct BSLFileSave *obj, long offset, BSL_FILE_SEEK mode);
	/*! ファイルポインタの現在位置を取得する */
	unsigned long (*getPos) (struct BSLFileSave *obj);
	/*! 出力サイズ[byte]を取得する */
	unsigned long (*getLength) (struct BSLFileSave *obj);
	/*! ファイルタイプを取得する */
	BSL_FILE_SAVE_TYPE (*getType) (struct BSLFileSave *obj);


	union {
#ifndef BSL_FILE_NO_SYSTEM
		struct {
			TCHAR cPath[_MAX_PATH];
	#if BSL_WINDOWS
			HANDLE fp;
	#else /* BSL_WINDOWS */
			FILE * fp;
	#endif /* BSL_WINDOWS */
			BOOL bOpened;
			unsigned long ulPosition;
		} disk;
#endif /* BSL_FILE_NO_SYSTEM */
		struct {
			char *pData;
			char *pPosition;
			unsigned long ulDataSize;
		} mem;
		struct {
			unsigned long ulPassedPos;
			unsigned long ulActivePos;
		} dummy;
	} member;

	unsigned long ulFileSize;
	BSL_FILE_SAVE_TYPE nType;

} BSLFileSave;

/*! ファイル書き出し処理を行うコールバック関数型 */
typedef BSLErr (*BSL_FILE_SAVE) (BSLFileSave *obj, void *user, void *userParam);

/* globals */

/* locals */

/* function declarations */

void bslFileSave (BSLFileSave *obj);
void bslFileSaveExit (BSLFileSave *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileSaveh */
