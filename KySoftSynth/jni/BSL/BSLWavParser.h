/*
 *  BSLWavParser.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavParser.h
	@brief
	Wave File���͐���I�u�W�F�N�g
*/

#ifndef __INCBSLWavParserh
#define __INCBSLWavParserh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLFile.h"
#include "BSLWav.h"

/* defines */

/* typedefs */

/*!
	Wave File���͊�{����I�u�W�F�N�g�B
	�eFile�`�����ƂɈȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLWavParserAiff.c / BSLWavParserAiff.h : AIFF
	- BSLWavParserRiff.c / BSLWavParserRiff.h : RIFF
	- BSLWavParserG726.c / BSLWavParserG726.h : G726 ADPCM
	- BSLWavParserVox.c / BSLWavParserVox.h : Dialogic Vox ADPCM
	- BSLWavParserOki.c / BSLWavParserOki.h : Oki LSI Audio
	- BSLWavParserRaw.c / BSLWavParserRaw.h : Raw Audio
	- BSLWavParserAuto.c / BSLWavParserAuto.h : �t�H�[�}�b�g�������ʌ^
	
	- �g�p�菇
		-# BSLFile����I�u�W�F�N�g�ɂ��Wave File��Open
		-# bslWavParser* ()�ɂ�萧��I�u�W�F�N�g���\�z
		-# BSLWavParser::parse()�ɂ��p�[�X�������s��
		-# BSLWavParser::read() ...�ɂ��Wave Data�擾�������s��
		-# BSLWavParser::seek() ....�ɂ��Wave Data�擾�ʒu��ݒ肷��
		-# bslWavParserExit()�ɂ�萧��I�u�W�F�N�g��j��
		-# BSLFile����I�u�W�F�N�g��j��
*/
typedef struct BSLWavParser
{
	/*! �p�[�X�������s�����\�b�h */
	BSLErr (*parse) (struct BSLWavParser *obj, BSLFile *file);

	/*!
		Wave�f�[�^�ǂݏo�����s�����\�b�h�B
		�����_�����O�������ꂽ�f�[�^�i���k�X�g���[�����̏ꍇ�̓f�R�[�h���ꂽ�f�[�^�j���擾���邽�߂Ɏg�p����B
		���̃��\�b�h�̌Ăяo���󋵂ɂ����BSLWavParser::getPlayedFrames()�ɂ���ē�����ǂݏo���ʒu�������I�Ɉړ�����B
		BSLWavParser::seek()�ABSLWavParser::read()�Ƃ͔r���I�ɌĂяo�����ƁB
	*/
	BSLErr (*read) (struct BSLWavParser *obj, void *data, long sampleFrames, long *readFrames);
	/*!
	�ǂݏo���ʒu[frame]�̐ݒ���s�����\�b�h�B
	�t�@�C���^�C�v�iBSL_WAV_FILE_TYPE�j�ɂ���Ă̓V�[�N�������Ή��̏ꍇ�����邽�߁A
	�{���\�b�h�̖߂�l��BSL_OK�ȊO�̏ꍇ�ɂ�BSLWavParser::getPlayedFrames�ɂ���čēx���݂̓ǂݏo���ʒu���m�F����K�v�����邱�Ƃɒ��ӁB
	*/
	BSLErr (*seek) (struct BSLWavParser *obj, DWORD frame);
	/*! �ǂݏo���ʒu[frame]���擾���郁�\�b�h */
	DWORD (*getPlayedFrames) (struct BSLWavParser *obj);

	/*!
		Wave�f�[�^�ǂݏo�����s�����\�b�h�B
		�X�g���[���f�[�^���́i���k�X�g���[�����̏ꍇ�̓f�R�[�h�O�̃f�[�^�j���擾���邽�߂Ɏg�p����B
		���̃��\�b�h�̌Ăяo���󋵂�BSLWavParser::getPlayedFrames()�ɂ���ē�����ǂݏo���ʒu�ɂ͊֗^���Ȃ��B
		offset�Asize�͂Ƃ���Byte�����w���B
		BSLWavParser::read()�ABSLWavParser::seek()�Ƃ͔r���I�ɌĂяo�����ƁB
	*/
	BSLErr (*readStream) (struct BSLWavParser *obj, void *data, DWORD offset, DWORD size);

	/* Wave�f�[�^�i�X�g���[�����́j�̃T�C�Y[Byte]���擾���郁�\�b�h */
	DWORD (*getStreamSize) (struct BSLWavParser *obj);

	/*! �t�@�C���^�C�v��ݒ肷�郁�\�b�h */
	BSLErr  (*setType) (struct BSLWavParser *obj, BSL_WAV_FILE_TYPE type);
	/*! �t�@�C���^�C�v���擾���郁�\�b�h */
	BSL_WAV_FILE_TYPE (*getType) (struct BSLWavParser *obj);

	/*! �R�[�f�b�N�^�C�v��ݒ肷�郁�\�b�h */
	BSLErr (*setCodec) (struct BSLWavParser *obj, BSL_WAV_CODEC codec);
	/*! �R�[�f�b�N�^�C�v���擾���郁�\�b�h */
	BSL_WAV_CODEC (*getCodec) (struct BSLWavParser *obj);

	/*! ���t���[����[sample]��ݒ肷�郁�\�b�h */
	BSLErr (*setFrames) (struct BSLWavParser *obj, DWORD frames);
	/*! ���t���[����[sample]���擾���郁�\�b�h */
	DWORD (*getFrames) (struct BSLWavParser *obj);

	/*! �T���v�����O���g��[Hz]��ݒ肷�郁�\�b�h */
	BSLErr (*setSampleRate) (struct BSLWavParser *obj, int sampleRate);
	/*! �T���v�����O���g��[Hz]���擾���郁�\�b�h */
	int (*getSampleRate) (struct BSLWavParser *obj);

	/*!
		�X�g���[���̃r�b�g���x[bit]��ݒ肷�郁�\�b�h�B
		�����_�����O�������̃r�b�g���x�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
		Raw�t�@�C�����������Ĉ�ʂɂ̓I�u�W�F�N�g���g�Őݒ肳�����̂ŁA�O�����ݒ�͍s��Ȃ��B
	*/
	BSLErr (*setBitsPerSample) (struct BSLWavParser *obj, int bitsPerSample);
	/*!
		�X�g���[���̃r�b�g���x[bit]���擾���郁�\�b�h�B
		�����_�����O�������̃r�b�g���x�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBitsPerSample) (struct BSLWavParser *obj);

	/*!
		�����_�����O�������̃r�b�g���x[bit]��ݒ肷�郁�\�b�h�B
		BSL_WAV_CODEC_PCM�ȊO�̃f�R�[�_���g�p����t�@�C���̏ꍇ�A
		�X�g���[���������̃r�b�g���x�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
		��ʂɂ̓I�u�W�F�N�g���g�Őݒ肳�����̂ŁA�O�����ݒ�͍s��Ȃ��B
	*/
	BSLErr (*setBitsPerSampleRendering) (struct BSLWavParser *obj, int bitsPerSample);
	/*!
		�����_�����O�������̃r�b�g���x[bit]���擾���郁�\�b�h�B
		BSL_WAV_CODEC_PCM�ȊO�̃f�R�[�_���g�p����t�@�C���̏ꍇ�A
		�X�g���[���������̃r�b�g���x�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBitsPerSampleRendering) (struct BSLWavParser *obj);

	/*!
		�r�b�g���[�g[bps]��ݒ肷�郁�\�b�h�B
		���Ή��̃r�b�g���[�g�����݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setBitRate) (struct BSLWavParser *obj, DWORD bitRate);

	/*!
		�r�b�g���[�g[bps]���擾���郁�\�b�h�B
	*/
	DWORD (*getBitRate) (struct BSLWavParser *obj);

	/*! �`�����l������ݒ肷�郁�\�b�h */
	BSLErr (*setChannels) (struct BSLWavParser *obj, int channels);
	/*! �`�����l�������擾���郁�\�b�h */
	int (*getChannels) (struct BSLWavParser *obj);

	/*!
		�X�g���[���̃o�C�g��/�t���[�����擾���郁�\�b�h�B
		�����_�����O�������̒l�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBytesPerFrame) (struct BSLWavParser *obj);

	/*!
		�����_�����O�������̃o�C�g��/�t���[�����擾���郁�\�b�h�B
		�X�g���[���̒l�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBytesPerFrameRendering) (struct BSLWavParser *obj);

	/*! Unsigned������ݒ肷�郁�\�b�h */
	BSLErr (*setUnsigned) (struct BSLWavParser *obj, BOOL flag);
	/*! Unsigned�������擾���郁�\�b�h */
	BOOL (*getUnsigned) (struct BSLWavParser *obj);

	/*! Big Endian������ݒ肷�郁�\�b�h */
	BSLErr (*setBigEndian) (struct BSLWavParser *obj, BOOL flag);
	/*! Big Endian�������擾���郁�\�b�h */
	BOOL (*getBigEndian) (struct BSLWavParser *obj);

	/*! Float������ݒ肷�郁�\�b�h */
	BSLErr (*setFloat) (struct BSLWavParser *obj, BOOL flag);
	/*! Float�������擾���郁�\�b�h */
	BOOL (*getFloat) (struct BSLWavParser *obj);

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! �^�C�g���������ݒ肷�郁�\�b�h */
	void (*setTitle) (struct BSLWavParser *obj, LPCTSTR str);
	/*! �^�C�g����������擾���郁�\�b�h */
	void (*getTitle) (struct BSLWavParser *obj, LPTSTR str, int length);
	/*! �^�C�g����������擾���郁�\�b�h */
	LPTSTR (*getTitleAddr) (struct BSLWavParser *obj);

	/*! �R�s�[���C�g�������ݒ肷�郁�\�b�h */
	void (*setCopyright) (struct BSLWavParser *obj, LPCTSTR str);
	/*! �R�s�[���C�g��������擾���郁�\�b�h */
	void (*getCopyright) (struct BSLWavParser *obj, LPTSTR str, int length);
	/*! �R�s�[���C�g��������擾���郁�\�b�h */
	LPTSTR (*getCopyrightAddr) (struct BSLWavParser *obj);
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*! �R�s�[�t���O��ݒ肷�郁�\�b�h */
	void (*setCopyEnable) (struct BSLWavParser *obj, BOOL flag);
	/*! �R�s�[�t���O���擾���郁�\�b�h */
	BOOL (*getCopyEnable) (struct BSLWavParser *obj);

	/*! �ۑ��t���O��ݒ肷�郁�\�b�h */
	void (*setSaveEnable) (struct BSLWavParser *obj, BOOL flag);
	/*! �ۑ��t���O���擾���郁�\�b�h */
	BOOL (*getSaveEnable) (struct BSLWavParser *obj);

	/*! LED�t���O��ݒ肷�郁�\�b�h */
	void (*setLEDUsing) (struct BSLWavParser *obj, BOOL flag);
	/*! LED�t���O���擾���郁�\�b�h */
	BOOL (*getLEDUsing) (struct BSLWavParser *obj);

	/*! VIB�t���O��ݒ肷�郁�\�b�h */
	void (*setVIBUsing) (struct BSLWavParser *obj, BOOL flag);
	/*! VIB�t���O���擾���郁�\�b�h */
	BOOL (*getVIBUsing) (struct BSLWavParser *obj);
#endif

#ifdef BSL_WAV_LOOPS
	int (*getLoopNum) (struct BSLWavParser *obj);
	BSLErr (*addLoop) (struct BSLWavParser *obj, BSL_WAV_LOOP *loop);
	void (*getLoop) (struct BSLWavParser *obj, int index, BSL_WAV_LOOP *loop);
#endif /* BSL_WAV_LOOPS */

	/*!
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslWavParserExit()���Ăяo�����B
	*/
	void (*finalize) (struct BSLWavParser *obj);

	/*! �t�@�C���^�C�v */
	BSL_WAV_FILE_TYPE nType;
	/*! �R�[�f�b�N�^�C�v */
	BSL_WAV_CODEC nCodec;
	/*! ���t���[����[sample] */
	DWORD dwFrames;
	/*! �T���v�����O���g��[Hz] */
	int nSampleRate;
	/*! �r�b�g���x[bit] */
	int nBitsPerSample;
	/*! �r�b�g���[�g[bps] */
	DWORD dwBitRate;
	/*! �`�����l���� */
	int nChannels;
	/*! Unsigned�t���O */
	BOOL bUnsigned;
	/*! Big Endian�t���O */
	BOOL bBigEndian;
	/*! Float�t���O */
	BOOL bFloat;

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*! �R�s�[�t���O */
	BOOL bCopyEnable;
	/*! �ۑ��t���O */
	BOOL bSaveEnable;
	/*! LED�t���O */
	BOOL bLEDUsing;
	/*! VIB�t���O */
	BOOL bVIBUsing;
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! �^�C�g�������� */
	TCHAR cTitle[BSL_WAV_FILE_TITLE_SIZE];
	/*! �R�s�[���C�g������ */
	TCHAR cCopyright[BSL_WAV_FILE_COPYRIGHT_SIZE];
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

	/*! �t�@�C������I�u�W�F�N�g */
	BSLFile *pFile;
	/*! �t�H�[�}�b�g�����擾�ł������������t���O */
	BOOL bHasFormat;
	/*! �f�[�^�����擾�ł������������t���O */
	BOOL bHasData;
	/*! �f�[�^�J�n�A�h���X */
	unsigned long ulStartPosition;
	/*! �ǂݏo���ʒu�A�h���X */
	DWORD dwPlayedFrames;

	/*!
		�X�g���[���f�[�^�m�ۗp�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g�ɂčs�����ƁB
		�������J����bslWavParserExit()�ɂčs����B
	*/
	char *pInternalCache;
	/*! �X�g���[���f�[�^�m�ۗp�|�C���^�̃T�C�Y[Byte] */
	DWORD dwInternalCacheSize;
	/*! �X�g���[���f�[�^�ǂݏo���ʒu */
	DWORD dwInternalCachePosition;
	/*! ���g�p�X�g���[���f�[�^�T�C�Y[Byte] */
	DWORD dwInternalCacheSizeLoaded;

	/*!
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g��CrlWavParser::parse()���ɂčs�����ƁB
		�������J����bslWavParserExit()�ɂčs����B
	*/
	void *pInternalData; /* used for each upper object */

#ifdef BSL_WAV_LOOPS
	int nLoops;
	BSL_WAV_LOOP loop[BSL_WAV_LOOPS];
#endif /* BSL_WAV_LOOPS */

} BSLWavParser;

/* globals */

/* locals */

/* function declarations */

void bslWavParser (BSLWavParser *obj);
void bslWavParserExit (BSLWavParser *obj);

BSLErr _bslWavParserSetFrames (BSLWavParser *obj, DWORD frames);
BSLErr _bslWavParserSetSampleRate (BSLWavParser *obj, int sampleRate);
BSLErr _bslWavParserSetBitsPerSample (BSLWavParser *obj, int bitsPerSample);
BSLErr _bslWavParserSetBitRate (BSLWavParser *obj, DWORD bitRate);
DWORD _bslWavParserGetBitRateLinear (BSLWavParser *obj);
BSLErr _bslWavParserSetChannels (BSLWavParser *obj, int channels);
BSLErr _bslWavParserSetUnsigned (BSLWavParser *obj, BOOL isUnsigned);
BSLErr _bslWavParserSetBigEndian (BSLWavParser *obj, BOOL isBigEndian);
BSLErr _bslWavParserSeek (BSLWavParser *obj, DWORD frame);
BSLErr _bslWavParserRead (BSLWavParser *obj, void *data, long sampleFrames, long *readFrames);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavParserh */
