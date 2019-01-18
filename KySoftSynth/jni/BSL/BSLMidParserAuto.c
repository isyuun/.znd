/*
 *  BSLMidParserAuto.c
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserAuto.c
	@brief
	MIDI File入力制御オブジェクト (Auto Detect)
*/

/* includes */

#include "BSL.h"
#include "BSLMidParser.h"
#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMF
	#include "BSLMidParserSmf.h"
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMF */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	#include "BSLMidParserMcomp.h"
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMAF
	/* todo */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMAF */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFi
	/* todo */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFi */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MFMP
	/* todo */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MFMP */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_CMX
	/* todo */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_CMX */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCDF
	/* todo */
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCDF */
#include "BSLMidParserAuto.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
static BSLErr _parse
	(
	BSLMidParser *obj, 
	BSLFile *file
	)
{
	BSLErr err = BSL_OK;
	DWORD header[3];

	void *pCallbackUser = obj->pCallbackUser;
	BSL_MID_EVENT_PROC (*_chaseEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->chaseEvent;
	BSL_MID_EVENT_PROC (*_getEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->getEvent;

	if ((err = file->seek (file, 0, BSL_FILE_SEEK_START)) != BSL_OK) {
		return err;
	}

	if (obj->getType (obj) == BSL_MID_FILE_TYPE_NONE) {
		/* タイプ未設定の場合には自動判別する */

		/* ヘッダ解析 */
		if ((err = file->read (file, header, 12UL)) != BSL_OK) {
			return err;
		}

		if (0) {}
#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMF
		else if (header[0] == MThd_MARKER) {
			bslMidParserSmf (obj);
			obj->pCallbackUser = pCallbackUser;
			obj->chaseEvent = _chaseEvent;
			obj->getEvent = _getEvent;
			if ((err = obj->parse (obj, file)) != BSL_OK) {
				return err;
			}
		}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMF */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
		else if (header[0] == mcmp_MARKER || header[0] == MCTB_MARKER) {
			bslMidParserMcomp (obj);
			obj->pCallbackUser = pCallbackUser;
			obj->chaseEvent = _chaseEvent;
			obj->getEvent = _getEvent;
			if ((err = obj->parse (obj, file)) != BSL_OK) {
				return err;
			}
		}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */
		else {
			return BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}

	/* タイプ設定済の場合には自動判別しない */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_SMF
	else if (obj->getType (obj) == BSL_MID_FILE_TYPE_SMF) {
		bslMidParserSmf (obj);
		obj->pCallbackUser = pCallbackUser;
		obj->chaseEvent = _chaseEvent;
		obj->getEvent = _getEvent;
		if ((err = obj->parse (obj, file)) != BSL_OK) {
			return err;
		}
	}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_SMF */
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	else if (obj->getType (obj) == BSL_MID_FILE_TYPE_MCOMP) {
		bslMidParserMcomp (obj);
		obj->pCallbackUser = pCallbackUser;
		obj->chaseEvent = _chaseEvent;
		obj->getEvent = _getEvent;
		if ((err = obj->parse (obj, file)) != BSL_OK) {
			return err;
		}
	}
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */
	else {
		return BSL_ERR_FILE_UNKNOWN_STRUCTURE; /* not supported yet */
	}

	return err;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	フォーマット自動判別機能付きBSLMidParser継承制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
*/
void bslMidParserAuto
	(
	BSLMidParser *obj
	)
{
	bslMidParser (obj);
	obj->parse = _parse;
}

