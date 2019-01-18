/*
 *  BSLMidParser.h
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParser.h
	@brief
	MIDI File���͐���I�u�W�F�N�g
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
	MIDI File���͊�{����I�u�W�F�N�g�B
	�eFile�`�����ƂɈȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLMidParserSmf.c / BSLMidParserSmf.h : SMF
	- BSLMidParserMcomp.c / BSLMidParserMcomp.h : M-compression
	...
	
	- �g�p�菇
		-# BSLFile����I�u�W�F�N�g�ɂ��MIDI File��Open
		-# bslMidParser* ()�ɂ�萧��I�u�W�F�N�g���\�z
		-# ���L�̃��\�b�h�̌p����ݒ肷��
		  - BSLMidParser::chaseEvent() : MIDI�C�x���g�擾�֐�(Event Chasing)
		  - BSLMidParser::getEvent() : MIDI�C�x���g�擾�֐�
		-# BSLMidParser::parse()�ɂ��p�[�X�������s���i���͂���t�@�C������I�u�W�F�N�g��BSLFileMem�݂̂Ƃ���j
		-# BSLMidParser::rewind()�ɂ��t�@�C���擪�ֈړ�
		-# BSLMidParser::sortEvent()�ɂ��C�x���g�擾�������s���i�p���������\�b�hBSLMidParser::chaseEvent() / BSLMidParser::getEvent()���Ă΂��j
		-# bslMidParserExit()�ɂ�萧��I�u�W�F�N�g��j��
		-# BSLFile����I�u�W�F�N�g��j��
*/
typedef  struct BSLMidParser
{
	/*!
		�p�[�X�������s���B
		���͂���t�@�C������I�u�W�F�N�g��BSLFileMem�݂̂Ƃ���B
	*/
	BSLErr (*parse) (struct BSLMidParser *obj, BSLFile *file);

	/*! ���t�J�n�ʒu�ֈړ� */
	BSLErr (*rewind) (struct BSLMidParser *obj, DWORD startReadtick);

	/*! MIDI�C�x���g�\�[�g���� */
	BSL_MID_EVENT_PROC (*sortEvent) (struct BSLMidParser *obj, DWORD startReadtick, void *user);
	/*!
		MIDI�C�x���g�擾�iEvent Chasing)
		Event Chasing�����������ɂ̓A�v���P�[�V�������Ń��\�b�h���p�����邱��
	*/
	BSL_MID_EVENT_PROC (*chaseEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user);
	/*!
		MIDI�C�x���g�擾
		�K���A�v���P�[�V�������Ń��\�b�h���p�����邱��
	*/
	BSL_MID_EVENT_PROC (*getEvent) (struct BSLMidParser *obj, BSLMidEvent *event, void *user);

	/*! �t�@�C���^�C�v���擾���郁�\�b�h */
	BSL_MID_FILE_TYPE (*getType) (struct BSLMidParser *obj);
	/*! �t�@�C���t�H�[�}�b�g���擾���郁�\�b�h */
	BSL_MID_FILE_FORMAT (*getFormat) (struct BSLMidParser *obj);
	/*! ����\[TPQN]���擾���郁�\�b�h */
	int (*getDivision) (struct BSLMidParser *obj);
	/*! ����\[TPQN]��ݒ肷�郁�\�b�h */
	void (*setDivision) (struct BSLMidParser *obj, int division, int clocksPerBeat);
	/*! �g���b�N�����擾���郁�\�b�h */
	int (*getTracks) (struct BSLMidParser *obj);

	/*! BSLMidParserTrack����I�u�W�F�N�g���擾���郁�\�b�h */
	BSLMidParserTrack *(*getTrack) (struct BSLMidParser *obj, int index);
	/*! �����g�p�iBSLMidParser::sortEvent()�j�̂� */
	BOOL (*closeTrack) (struct BSLMidParser *obj, int index);

	/*! BSLFile����I�u�W�F�N�g���擾���郁�\�b�h */
	BSLFile *(*getFile) (struct BSLMidParser *obj);

#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
	/*! �f�R�[�h���ʂ��擾���郁�\�b�h */
	char *(*getDecoded) (struct BSLMidParser *obj);
	/*! �f�R�[�h��̃t�@�C���T�C�Y���擾���郁�\�b�h */
	DWORD (*getDecodedSize) (struct BSLMidParser *obj);
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

	/*!
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslMidParserExit()���Ăяo�����B
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
	/*! �f�R�[�h���� */
	char *pDecoded;
	/*! �f�R�[�h���ʂ̃T�C�Y */
	DWORD dwDecodedSize;
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */

	/*!
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g��CrlMidParser::parse()���ɂčs�����ƁB
		�������J����bslMidParserExit()�ɂčs����B
	*/
	void *pInternalData; /* used for each upper object */
	
	/*! �R�[���o�b�N���̃��[�U�[�ϐ� */
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
