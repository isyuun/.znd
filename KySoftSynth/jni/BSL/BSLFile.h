/*
 *  BSLFile.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFile.h
	@brief
	ファイル入力制御オブジェクト
*/

#ifndef __INCBSLFileh
#define __INCBSLFileh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLText.h"
#ifndef BSL_FILE_NO_SYSTEM
	#if !BSL_WINDOWS
		#include <stdio.h>
	#endif /* BSL_WINDOWS */
#endif /* BSL_FILE_NO_SYSTEM */

/* defines */

/*! 4バイトヘッダ用マクロ */
#ifdef BSL_BIGENDIAN
	#define BSL_FILE_MARKER(a,b,c,d) (((a)<<24)|((b)<<16)|((c)<<8)|(d))
#else /* BSL_BIGENDIAN */
	#define BSL_FILE_MARKER(a,b,c,d) ((a)|((b)<<8)|((c)<<16)|((d)<<24))
#endif /* BSL_BIGENDIAN */

/*! ファイルシーク処理用 */
typedef enum {
	/*! 先頭 */
	BSL_FILE_SEEK_START = 0,
	BSL_FILE_SEEK_CURRENT,
	BSL_FILE_SEEK_END
} BSL_FILE_SEEK;

/* typedefs */

/*!
	ファイル入力処理を行う基本制御オブジェクト。
	各処理対象毎に以下の継承オブジェクトが存在する。
	- BSLFile.c / BSLFile.h : ストリームI/O型
	- BSLFileMem.c / BSLFileMem.h : メモリI/O型
	- BSLFileCache.c / BSLFileCache.h : ストリームI/O + リードキャッシュ型

	- 使用手順
		-# bslFile*()により制御オブジェクトを構築
		-# BSLFile::open()により指定ファイルを、またはBSLFile::openMem()により指定アドレスをオープン
			- 各メソッドの継承処理はbslFile*Open ()にて行う
		-# BSLFile::read(), BSLFile::seek()により任意入力処理を行う
		-# BSLFile::close()により指定ファイルをクローズ
		-# bslFileExit()により制御オブジェクトを破棄
*/
typedef struct BSLFile
{
#ifndef BSL_FILE_NO_SYSTEM
	/*!
		ファイルパスをオープンする。
		BSL_FILE_NO_SYSTEMが未定義の場合のみ有効。
	*/
	BSLErr (*open) (struct BSLFile *obj, LPCTSTR filename);
#endif /* BSL_FILE_NO_SYSTEM */
	/*! メモリマップされたファイルイメージをオープンする */
	BSLErr (*openMem) (struct BSLFile *obj, char *data, unsigned long size);
	/*! データの読み出しを行う */
	BSLErr (*read) (struct BSLFile *obj, void *data, unsigned long size);
	/*! 指定された位置にファイル ポインタを移動する */
	BSLErr (*seek) (struct BSLFile *obj, long offset, BSL_FILE_SEEK mode);
	/*! ファイルポインタの現在位置を取得する */
	unsigned long (*getPos) (struct BSLFile *obj);
	/*! ファイルサイズ[Byte]を取得する */
	unsigned long (*getLength) (struct BSLFile *obj);
	/*! ファイルを閉じる */
	BSLErr (*close) (struct BSLFile *obj);

	/*! ファイルがオープンされているか*/
	BOOL (*isOpened) (struct BSLFile *obj);

	/*!
		メモリIOアドレスの取得を行う。メモリI/O型のみ有効。
	*/
	char *(*getAddress) (struct BSLFile *obj);

	/*!
		終了処理を行うメソッド。
		継承オブジェクト独自のメモリ破棄等が必要な場合にはここで行う。
		必要ない場合は本メソッドポインタはNULLのままで可。
		bslFileExit()より呼び出される。
	*/
	void (*finalize) (struct BSLFile *obj);

	unsigned long ulFileSize;
	BOOL bIsOpened;
	union {
#ifndef BSL_FILE_NO_SYSTEM
		struct {
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
			BOOL bAllocated;
			char *pPosition;
		} mem;
	} member;

	/*!
		内部データ用ポインタとして継承オブジェクトにて使用する。
		メモリ取得は継承オブジェクトにて行うこと。
		メモリ開放はbslFileExit()にて行われる。
	*/
	void *pInternalData;
} BSLFile;

/* globals */

/* locals */

/* function declarations */

void bslFile (BSLFile *obj);
void bslFileExit (BSLFile *obj);

void bslFileSwap (void *data, int size);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileh */
