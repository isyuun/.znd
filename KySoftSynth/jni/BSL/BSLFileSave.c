/*
 *  BSLFileSave.c
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileSave.c
	@brief
	ファイル出力制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLDebug.h"
#include "BSLError.h"
#include "BSLFile.h"
#include "BSLFileSave.h"

#if BSL_WINDOWS
	#include <windows.h>
#else /* BSL_WINDOWS */
	#include <stdio.h>
#endif /* BSL_WINDOWS */

/* defines */

/* typedefs */

/* globals */

/* locals */


#ifndef BSL_FILE_NO_SYSTEM
/*---------------------------------------------------------------------------*/
static BSLErr _write
	(
	BSLFileSave *obj, 
	void *data, 
	unsigned long size
	)
{
#if BSL_WINDOWS
	DWORD writeSize;
	WriteFile (obj->member.disk.fp, data, size, &writeSize, NULL);
	if (writeSize != size) {
		return (BSL_ERR_FILE_WRITE);
	}
#else /* BSL_WINDOWS */
	if (fwrite (data, 1, size, obj->member.disk.fp) != size) {
		return (BSL_ERR_FILE_WRITE);
	}
#endif /* BSL_WINDOWS */

	obj->member.disk.ulPosition += size;

	if (obj->ulFileSize < obj->member.disk.ulPosition) {
		obj->ulFileSize = obj->member.disk.ulPosition;
	}

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static BSLErr _read
	(
	BSLFileSave *obj, 
	void *data, 
	unsigned long size
	)
{
#if BSL_WINDOWS
	DWORD readSize;
	ReadFile (obj->member.disk.fp, data, size, &readSize, NULL);
	if (readSize != size) {
		return (BSL_ERR_FILE_WRITE);
	}
#else /* BSL_WINDOWS */
	if (fread (data, 1, size, obj->member.disk.fp) != size) {
		return (BSL_ERR_FILE_WRITE);
	}
#endif /* BSL_WINDOWS */

	obj->member.disk.ulPosition += size;
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static BSLErr _seek
	(
	BSLFileSave *obj, 
	long offset, 
	BSL_FILE_SEEK mode
	)
{
#if BSL_WINDOWS
	DWORD method = FILE_BEGIN;
#else /* BSL_WINDOWS */
	int method = SEEK_SET;
#endif /* BSL_WINDOWS */

	switch (mode) {
	case BSL_FILE_SEEK_START:
#if BSL_WINDOWS
		method = FILE_BEGIN;
#else
		method = SEEK_SET;
#endif
		if (offset < 0L) {
			offset = 0L;
		}
		obj->member.disk.ulPosition = (unsigned long) offset;
		break;
	case BSL_FILE_SEEK_CURRENT:
#if BSL_WINDOWS
		method = FILE_CURRENT;
#else
		method = SEEK_CUR;
#endif
		if (offset >= 0L) {
			obj->member.disk.ulPosition += (unsigned long) offset;
		}
		else {
			obj->member.disk.ulPosition -= (unsigned long) (-offset);
		}
		break;
	case BSL_FILE_SEEK_END:
		return (BSL_ERR_PARAM_WRONG);
	}

#if BSL_WINDOWS
	SetFilePointer (obj->member.disk.fp, offset, NULL, method);
#else
	fseek (obj->member.disk.fp, offset, method);
#endif

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static unsigned long _getPos
	(
	BSLFileSave *obj
	)
{
	return obj->member.disk.ulPosition;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	指定のパスに出力対象を設定する非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param path
	出力パス
	@retval
	BSLErr
*/
static BSLErr _setDestination
	(
	BSLFileSave *obj, 
	LPCTSTR path
	)
{
	BSLErr err = BSL_OK;
	_tcsncpy_(obj->member.disk.cPath, path, _MAX_PATH - 1);
	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	指定されたファイルへの出力処理を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param save
	書き出し処理用callback
	@param user
	ユーザデータ
	@param userParam
	ユーザパラメータ
	@retval
	BSLErr
*/
static BSLErr _save
	(
	BSLFileSave *obj, 
	BSL_FILE_SAVE save, 
	void *user, 
	void *userParam
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
#if BSL_WINDOWS
		DWORD dwFlagsAndAttributes = FILE_ATTRIBUTE_NORMAL;
		obj->member.disk.fp = CreateFile (obj->member.disk.cPath, GENERIC_WRITE | GENERIC_READ, FILE_SHARE_WRITE | FILE_SHARE_READ, NULL,
							CREATE_ALWAYS, dwFlagsAndAttributes, NULL);
		if (obj->member.disk.fp == INVALID_HANDLE_VALUE) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-FILE: can't create file [%s]\n"), obj->member.disk.cPath);
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_CREATE;
		}
#else /* BSL_WINDOWS */
		if ((obj->member.disk.fp = fopen (obj->member.disk.cPath, "w+b")) == NULL) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-FILE: can't create file [%s]\n"), obj->member.disk.cPath);
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_CREATE;
		}
#endif /* BSL_WINDOWS */
	}

	if (err == BSL_OK) {
		obj->member.disk.ulPosition = 0UL;
		obj->ulFileSize = 0UL;
		err = save (obj, user, userParam);
	}

#if BSL_WINDOWS
	CloseHandle (obj->member.disk.fp);
#else /* BSL_WINDOWS */
	fclose (obj->member.disk.fp);
#endif /* BSL_WINDOWS */

	return err;
}
#endif /* BSL_FILE_NO_SYSTEM */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ファイルサイズを取得する非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	ファイルサイズ[Byte]
*/
static unsigned long _getLength
	(
	BSLFileSave *obj
	)
{
	return obj->ulFileSize;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ファイルタイプを取得する非公開関数
	@param obj
	制御オブジェクトのアドレス
	@retval
	ファイルタイプ[Byte]
*/
static BSL_FILE_SAVE_TYPE _getType
	(
	BSLFileSave *obj
	)
{
	return obj->nType;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	ファイル出力基本制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
*/
void bslFileSave
	(
	BSLFileSave *obj
	)
{
	memset (obj, 0, sizeof (BSLFileSave));

#ifndef BSL_FILE_NO_SYSTEM
	obj->setDestination = _setDestination;
	obj->save = _save;
	obj->write = _write;
	obj->read = _read;
	obj->seek = _seek;
	obj->getPos = _getPos;
	obj->getType = _getType;
	obj->nType = BSL_FILE_SAVE_TYPE_FILE;
#endif /* BSL_FILE_NO_SYSTEM */
	obj->getLength = _getLength;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ファイル出力基本制御オブジェクトを破棄する
	@param obj
	制御オブジェクトのアドレス
*/
void bslFileSaveExit
	(
	BSLFileSave *obj
	)
{
}
