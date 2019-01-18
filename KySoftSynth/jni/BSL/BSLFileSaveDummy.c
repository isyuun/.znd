/*
 *  BSLFileSaveMem_Dummy.c
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileSaveDummy.c
	@brief
	ファイル出力制御オブジェクト(Dummy)
*/

/* includes */

#include "BSL.h"
#include "BSLDebug.h"
#include "BSLError.h"
#include "BSLFile.h"
#include "BSLFileSave.h"
#include "BSLFileSaveDummy.h"

/* defines */

/* typedefs */

/* globals */

/* locals */


/*---------------------------------------------------------------------------*/
static BSLErr _write
	(
	BSLFileSave *obj, 
	void *data, 
	unsigned long size
	)
{
	if(size + obj->member.dummy.ulActivePos > obj->member.dummy.ulPassedPos) {
		obj->member.dummy.ulPassedPos
			+= size - (obj->member.dummy.ulPassedPos - obj->member.dummy.ulActivePos);
	}

	obj->member.dummy.ulActivePos += size;

	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
static BSLErr _read
	(
	BSLFileSave *obj, 
	void *data, 
	unsigned long size
	)
{
	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
static BSLErr _seek
	(
	BSLFileSave *obj, 
	long offset, 
	BSL_FILE_SEEK mode
	)
{
	if (!obj->member.dummy.ulPassedPos) {
		return (BSL_ERR_FILE_NOT_READY);
	}

	switch (mode) {
	case BSL_FILE_SEEK_START:
		if( (DWORD)offset > obj->member.dummy.ulPassedPos || offset < 0 )
			return BSL_ERR_PARAM_WRONG;
		obj->member.dummy.ulActivePos = offset;
		break;
	case BSL_FILE_SEEK_CURRENT:
		if ((offset + obj->member.dummy.ulActivePos) > obj->member.dummy.ulPassedPos
											|| (offset + (long) obj->member.dummy.ulActivePos) < 0 )
			return BSL_ERR_PARAM_WRONG;
		obj->member.dummy.ulActivePos += offset;
		break;
	case BSL_FILE_SEEK_END:
		if ((offset + obj->member.dummy.ulPassedPos) > obj->member.dummy.ulPassedPos
											|| (offset + (long) obj->member.dummy.ulPassedPos) < 0 )
			return BSL_ERR_PARAM_WRONG;
		obj->member.dummy.ulActivePos = obj->member.dummy.ulPassedPos + offset;
		break;
	default:

#ifdef BSL_DEBUG
		bslTrace (_TEXT("File: unknown seek mode\n"));
#endif /* BSL_DEBUG */
		return (BSL_ERR_PARAM_WRONG);
	}	

	if (obj->member.dummy.ulPassedPos < obj->member.dummy.ulActivePos) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("File: invalid address\n"));
#endif /* BSL_DEBUG */
		return (BSL_ERR_FILE_SEEK);
	}
#if 0
	else if (obj->member.dummy.dwPassedPos < 0UL) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("File: invalid address\n"));
#endif /* BSL_DEBUG */
		return (BSL_ERR_FILE_SEEK);
	}
#endif
	
	return (BSL_OK);
}

/*---------------------------------------------------------------------------*/
static unsigned long _getPos
	(
	BSLFileSave *obj
	)
{
	return obj->member.dummy.ulActivePos;
}

/*---------------------------------------------------------------------------*/
static unsigned long _getLength
	(
	BSLFileSave *obj
	)
{
	return obj->member.dummy.ulPassedPos;
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
	obj->ulFileSize = 0UL;

	obj->member.dummy.ulPassedPos = 0UL;
	obj->member.dummy.ulActivePos = 0UL;

	return save (obj, user, userParam);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	メモリ上でのファイルイメージ出力処理を行うファイル出力基本制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
*/
void bslFileSaveDummy
	(
	BSLFileSave *obj
	)
{
	bslFileSave (obj);

	obj->save = _save;
	obj->write = _write;
	obj->read = _read;
	obj->seek = _seek;
	obj->getPos = _getPos;
	obj->getLength = _getLength;
	obj->nType = BSL_FILE_SAVE_TYPE_DUMMY;
}

