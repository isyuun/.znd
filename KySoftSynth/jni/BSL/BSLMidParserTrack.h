/*
 *  BSLMidParserTrack.h
 *
 *  Copyright (c) 2002-2010 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserTrack.h
	@brief
	MIDI File Track����I�u�W�F�N�g
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
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslMidParserExit()���Ăяo�����B
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
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g��CrlMidParser::parse()���ɂčs�����ƁB
		�������J����bslMidParserExit()�ɂčs����B
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
