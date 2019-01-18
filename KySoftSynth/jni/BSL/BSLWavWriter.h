/*
 *  BSLWavWriter.h
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriter.h
	@brief
	Wave File�o�͐���I�u�W�F�N�g
*/

#ifndef __INCBSLWavWriterh
#define __INCBSLWavWriterh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLFileSave.h"
#include "BSLWav.h"
#include "BSLWavRender.h"
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	#include "BSLMFiUtil.h"
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

/* defines */

/* typedefs */

/*! Update�֐��^ */
typedef int (*BSL_WAV_UPDATER) (int percent, void *param);

/*! Stream�o�͊֐��^ */
typedef BSLErr (*BSL_WAV_WRITER_STREAM) (BSLFileSave *file, DWORD *size, void *user);

/*!
	Wave File�o�͊�{����I�u�W�F�N�g�B
	�eFile�`�����ƂɈȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLWavWriterAiff.c / BSLWavWriterAiff.h : AIFF
	- BSLWavWriterRiff.c / BSLWavWriterRiff.h : RIFF
	- BSLWavWriterG726.c / BSLWavWriterG726.h : G726 ADPCM
	- BSLWavWriterVox.c / BSLWavWriterVox.h : Dialogic Vox ADPCM
	- BSLWavWriterOki.c / BSLWavWriterOki.h : Oki LSI Audio
	- BSLWavWriterRaw.c / BSLWavWriterRaw.h : Raw Audio

	- �g�p�菇
		- Rendering�o��
			-# BSLWavRender����I�u�W�F�N�g���\�z
			-# bslWavWriter* ()�ɂ�萧��I�u�W�F�N�g���\�z
			-# BSLWavWriter::save()�ɂ��o�͏������s��
			-# bslWavWriterExit()�ɂ�萧��I�u�W�F�N�g��j��
			-# BSLWavRender����I�u�W�F�N�g��j��
		- Stream�o��
			-# bslWavWriter* ()�ɂ�萧��I�u�W�F�N�g���\�z
			-# BSLWavWriter::saveStream()�ɂ��o�͏������s��
			-# bslWavWriterExit()�ɂ�萧��I�u�W�F�N�g��j��
*/
typedef struct BSLWavWriter
{
	/*! Wave Rendering��������I�u�W�F�N�g�iBSLWavRender�j���g����Wave�t�@�C���o�͏������s�� */
	BSLErr (*save) (struct BSLWavWriter *obj, BSLFileSave *file, BSLWavRender *render, BSL_WAV_UPDATER updater,  void *updateParam);

	/*! Stream�o�͊֐����g����Wave�t�@�C���o�͏������s�� */
	BSLErr (*saveStream) (struct BSLWavWriter *obj, BSLFileSave *file, BSL_WAV_WRITER_STREAM stream, void *streamParam, BSL_WAV_UPDATER updater,  void *updateParam);

	/*! �t�@�C���^�C�v��ݒ肷�郁�\�b�h */
	BSLErr  (*setType) (struct BSLWavWriter *obj, BSL_WAV_FILE_TYPE type);
	/*! �t�@�C���^�C�v���擾���郁�\�b�h */
	BSL_WAV_FILE_TYPE (*getType) (struct BSLWavWriter *obj);

	/*! �R�[�f�b�N�^�C�v��ݒ肷�郁�\�b�h */
	BSLErr (*setCodec) (struct BSLWavWriter *obj, BSL_WAV_CODEC codec);
	/*! �R�[�f�b�N�^�C�v���擾���郁�\�b�h */
	BSL_WAV_CODEC (*getCodec) (struct BSLWavWriter *obj);

	/*! ���t���[����[sample]��ݒ肷�郁�\�b�h */
	BSLErr (*setFrames) (struct BSLWavWriter *obj, DWORD frames);
	/*! ���t���[����[sample]���擾���郁�\�b�h */
	DWORD (*getFrames) (struct BSLWavWriter *obj);

	/*!
		�T���v�����O���g��[Hz]��ݒ肷�郁�\�b�h�B
		���Ή��̃T���v�����O���g�������݂���ꍇ�ɂ́A
		�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setSampleRate) (struct BSLWavWriter *obj, int sampleRate);
	/*! �T���v�����O���g��[Hz]���擾���郁�\�b�h */
	int (*getSampleRate) (struct BSLWavWriter *obj);

	/*!
		�X�g���[���̃r�b�g���x[bit]��ݒ肷�郁�\�b�h�B
		���Ή��̃r�b�g���x�����݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setBitsPerSample) (struct BSLWavWriter *obj, int bitsPerSample);

	/*!
		�X�g���[���̃r�b�g���x[bit]���擾���郁�\�b�h�B
	*/
	int (*getBitsPerSample) (struct BSLWavWriter *obj);

	/*!
		�����_�����O�������̃r�b�g���x[bit]��ݒ肷�郁�\�b�h�B
		�����Őݒ肳���r�b�g���x�l��BSLWavWriter::save()���s���ɍs����
		�����_�����O�����ɂ����ē��͂����f�[�^�̃r�b�g���x���w���B
		BSL_WAV_CODEC_PCM�ȊO�̃G���R�[�_��p���ăt�@�C���o�͂���ꍇ�ɂ́A
		���̃G���R�[�_�ɓ��͉\�ȃr�b�g���x���w�����̂ł����āA
		�o�͂����t�@�C���̃r�b�g���x�ł͂Ȃ����Ƃɒ��ӂ��邱�ƁB
	*/
	BSLErr (*setBitsPerSampleRendering) (struct BSLWavWriter *obj, int bitsPerSample);

	/*!
		�����_�����O�������̃r�b�g���x[bit]���擾���郁�\�b�h�B
		�����Ŏ擾�����r�b�g���x�l��BSLWavWriter::save()���s���ɍs����
		�����_�����O�����ɂ����ē��͂����f�[�^�̃r�b�g���x���w���B
		BSL_WAV_CODEC_PCM�ȊO�̃G���R�[�_��p���ăt�@�C���o�͂���ꍇ�ɂ́A
		���̃G���R�[�_�ɓ��͉\�ȃr�b�g���x���w�����̂ł����āA
		�o�͂����t�@�C���̃r�b�g���x�ł͂Ȃ����Ƃɒ��ӂ��邱�ƁB
	*/
	int (*getBitsPerSampleRendering) (struct BSLWavWriter *obj);

	/*!
		�r�b�g���[�g[bps]��ݒ肷�郁�\�b�h�B
		���Ή��̃r�b�g���[�g�����݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setBitRate) (struct BSLWavWriter *obj, DWORD bitRate);

	/*!
		�r�b�g���[�g[bps]���擾���郁�\�b�h�B
	*/
	DWORD (*getBitRate) (struct BSLWavWriter *obj);

	/*!
		�`�����l������ݒ肷�郁�\�b�h�B
		���Ή��̃`�����l���������݂���ꍇ�ɂ́A
		�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setChannels) (struct BSLWavWriter *obj, int channels);
	/*! �`�����l�������擾���郁�\�b�h */
	int (*getChannels) (struct BSLWavWriter *obj);

	/*!
		�X�g���[���̃o�C�g��/�t���[�����擾���郁�\�b�h�B
		�����_�����O�������̒l�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBytesPerFrame) (struct BSLWavWriter *obj);

	/*!
		�����_�����O�������̃o�C�g��/�t���[�����擾���郁�\�b�h�B
		�X�g���[���̒l�Ƃ͈قȂ�ꍇ�����邽�ߒ��ӂ��邱�ƁB
	*/
	int (*getBytesPerFrameRendering) (struct BSLWavWriter *obj);

	/*!
		Unsigned������ݒ肷�郁�\�b�h�B
		���Ή��̑��������݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setUnsigned) (struct BSLWavWriter *obj, BOOL flag);
	/*! Unsigned�������擾���郁�\�b�h */
	BOOL (*getUnsigned) (struct BSLWavWriter *obj);

	/*!
		Big Endian������ݒ肷�郁�\�b�h�B
		���Ή��̑��������݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setBigEndian) (struct BSLWavWriter *obj, BOOL flag);
	/*! Big Endian�������擾���郁�\�b�h */
	BOOL (*getBigEndian) (struct BSLWavWriter *obj);

	/*!
		Float������ݒ肷�郁�\�b�h�B
		���Ή��̑��������݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
	*/
	BSLErr (*setFloat) (struct BSLWavWriter *obj, BOOL flag);
	/*! Float�������擾���郁�\�b�h */
	BOOL (*getFloat) (struct BSLWavWriter *obj);

    /*!
     �����_�����O��������Float������ݒ肷�郁�\�b�h�B
     ���Ή��̑��������݂���ꍇ�ɂ́A�p���������\�b�h���Ŕ��ʂ��s�����ƁB
     */
    BSLErr (*setFloatRendering) (struct BSLWavWriter *obj, BOOL flag);
    /*! �����_�����O��������Float�������擾���郁�\�b�h */
    BOOL (*getFloatRendering) (struct BSLWavWriter *obj);
    
#ifdef BSL_WAV_FILE_TITLE_SIZE
	/*!
		�^�C�g���������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setTitle) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		�^�C�g����������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getTitle) (struct BSLWavWriter *obj, LPTSTR str, int length);
	
	DWORD (*getTitleSize) (struct BSLWavWriter *obj);

	/*!
		�^�C�g����������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	LPCTSTR (*getTitleAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
	/*!
		�o�[�W�����������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setVersion) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		�o�[�W������������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getVersion) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getVersionSize) (struct BSLWavWriter *obj);

	/*!
		�o�[�W������������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	LPCTSTR (*getVersionAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
	/*!
		���t��񕶎����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setDate) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		���t��񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getDate) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getDateSize) (struct BSLWavWriter *obj);

	/*!
		���t��񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	LPCTSTR (*getDateAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
	/*!
		�R�s�[���C�g�������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setCopyright) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		�R�s�[���C�g��������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getCopyright) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getCopyrightSize) (struct BSLWavWriter *obj);

	/*!
		�R�s�[���C�g��������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	LPCTSTR (*getCopyrightAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
	/*!
		�f�[�^�Ǘ��y�ѕی��񕶎����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setProtect) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		�f�[�^�Ǘ��y�ѕی��񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getProtect) (struct BSLWavWriter *obj, LPTSTR str, int length);

	LPCTSTR (*getProtectAddr) (struct BSLWavWriter *obj);

	DWORD (*getProtectSize) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
	/*!
		���쌠��񕶎����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setAuthor) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		���쌠��񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getAuthor) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getAuthorSize) (struct BSLWavWriter *obj);

	LPCTSTR (*getAuthorAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
	/*!
		�T�|�[�g��񕶎����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setSupport) (struct BSLWavWriter *obj, LPCTSTR str);

	/*!
		�T�|�[�g��񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*getSupport) (struct BSLWavWriter *obj, LPTSTR str, int length);

	DWORD (*getSupportSize) (struct BSLWavWriter *obj);

	LPCTSTR (*getSupportAddr) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_FILE_SUPT_SIZE */

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_SMAF)
	/*!
		�R�s�[�t���O��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setCopyEnable) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		�R�s�[�t���O���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BOOL (*getCopyEnable) (struct BSLWavWriter *obj);

	/*!
		�ۑ��t���O��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setSaveEnable) (struct BSLWavWriter *obj, BOOL flag);

	/*!
		�ۑ��t���O���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BOOL (*getSaveEnable) (struct BSLWavWriter *obj);

	/*!
		LED�t���O��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setLEDUsing) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		LED�t���O���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BOOL (*getLEDUsing) (struct BSLWavWriter *obj);

	/*!
		VIB�t���O��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setVIBUsing) (struct BSLWavWriter *obj, BOOL flag);
	/*!
		VIB�t���O���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BOOL (*getVIBUsing) (struct BSLWavWriter *obj);
#endif

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
	/*!
		�Ĕz�z�t�����ʎq��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setMFiSorc) (struct BSLWavWriter *obj, BYTE sorc);
	/*!
		�Ĕz�z�t�����ʎq���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BYTE (*getMFiSorc) (struct BSLWavWriter *obj);

	/*!
		�m�[�g���b�Z�[�W���������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setMFiNoteMessLength) (struct BSLWavWriter *obj, WORD length);

	/*!
		�m�[�g���b�Z�[�W����������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	WORD (*getMFiNoteMessLength) (struct BSLWavWriter *obj);

	/*!
		�g���X�e�[�^�XA���b�Z�[�W����񕶎����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setMFiExstStatusALength) (struct BSLWavWriter *obj, WORD length);

	/*!
		�g���X�e�[�^�XA���b�Z�[�W����񕶎�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	WORD (*getMFiExstStatusALength) (struct BSLWavWriter *obj);
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*!
		�Đ����Ԃ�ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setPlayTime) (struct BSLWavWriter *obj, DWORD time);
	/*!
		�Đ����Ԃ��擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	DWORD (*getPlayTime) (struct BSLWavWriter *obj);

	/*!
		�Đ��㖳�����Ԃ�ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setBlankTime) (struct BSLWavWriter *obj, DWORD time);
	/*!
		�Đ��㖳�����Ԃ�ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	DWORD (*getBlankTime) (struct BSLWavWriter *obj);
	
	/*!
		�I�[�f�B�I�{�����[����ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setAudioVolume) (struct BSLWavWriter *obj, DWORD volume);
	/*!
		�I�[�f�B�I�{�����[�����擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	DWORD (*getAudioVolume) (struct BSLWavWriter *obj);

	/*!
		3D�̗L��/������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*set3DEfx) (struct BSLWavWriter *obj, BOOL efx);
	/*!
		3D�̗L��/�������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	BOOL (*get3DEfx) (struct BSLWavWriter *obj);
	
	/*!
		���[�J�[ID��ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setMakerID) (struct BSLWavWriter *obj, int nMakerID);
	/*!
		���[�J�[ID���擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	int (*getMakerID) (struct BSLWavWriter *obj);
	
	/*!
		MFi�o�[�W������ݒ肷�郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	void (*setMFiVersion) (struct BSLWavWriter *obj, int nMFiVersion);
	/*!
		MFi�o�[�W�������擾���郁�\�b�h�B
		�Ή��t�H�[�}�b�g�̂ݍ�p����B
	*/
	int (*getMFiVersion) (struct BSLWavWriter *obj);
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
	int (*getLoopNum) (struct BSLWavWriter *obj);
	BSLErr (*addLoop) (struct BSLWavWriter *obj, BSL_WAV_LOOP *loop);
	BSLErr (*deleteLoop) (struct BSLWavWriter *obj, int index);
	void (*getLoop) (struct BSLWavWriter *obj, int index, BSL_WAV_LOOP *loop);
#endif /* BSL_WAV_LOOPS */

	/*!
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslWavWriterExit()���Ăяo�����B
	*/
	void (*finalize) (struct BSLWavWriter *obj);

	/*!
		Wave Rendering��������I�u�W�F�N�g�iBSLWavRender�j���g�����t�@�C���o�͊֐����s�������֐��B
		�p���I�u�W�F�N�g�ŕK�����\�b�h���p�����邱�ƁB
		�{���\�b�h��save���\�b�h����Ă΂����̂ŁA�O�����Ăяo�����Ƃ͍s��Ȃ��B
	*/
	BSL_FILE_SAVE _save;

	/*!
		Stream�o�͊֐����g�����t�@�C���o�͊֐����s�������֐��B
		�p���I�u�W�F�N�g�ŕK�����\�b�h���p�����邱�ƁB
		�{���\�b�h��save���\�b�h����Ă΂����̂ŁA�O�����Ăяo�����Ƃ͍s��Ȃ��B
	*/
	BSL_FILE_SAVE _saveStream;

	/*! Stream�o�͏����p�p�����[�^ */
	void *pUpdateParam;

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
	/*! �����_�����O�������̃r�b�g���x[bit] */
	int nBitsPerSampleRendering;
	/*! �r�b�g���[�g[bps] */
	DWORD dwBitRate;
	/*! �`�����l���� */
	int nChannels;
	/*! Unsigned�t���O */
	BOOL bUnsigned;
	/*! Big Endian�t���O */
	BOOL bBigEndian;
	/*! Float�t���O (�o�̓t�@�C��) */
	BOOL bFloat;
    /*! Float�t���O (�����_�����O����) */
    BOOL bFloatRendering;

#ifdef BSL_WAV_FILE_TITLE_SIZE
	/*! �^�C�g�������� */
	TCHAR cTitle[BSL_WAV_FILE_TITLE_SIZE];
#endif /* BSL_WAV_FILE_TITLE_SIZE */

#ifdef BSL_WAV_FILE_VERSION_SIZE
	/*! �o�[�W������񕶎��� */
	TCHAR cVersion[BSL_WAV_FILE_VERSION_SIZE];
#endif /* BSL_WAV_FILE_VERSION_SIZE */

#ifdef BSL_WAV_FILE_DATE_SIZE
	/*! ���t��񕶎��� */
	TCHAR cDate[BSL_WAV_FILE_DATE_SIZE];
#endif /* BSL_WAV_FILE_DATE_SIZE */

#ifdef BSL_WAV_FILE_COPYRIGHT_SIZE
	/*! �R�s�[���C�g������ */
	TCHAR cCopyright[BSL_WAV_FILE_COPYRIGHT_SIZE];
#endif /* BSL_WAV_FILE_COPYRIGHT_SIZE */

#ifdef BSL_WAV_FILE_PROT_SIZE
	/*! �f�[�^�Ǘ��y�ѕی��񕶎��� */
	TCHAR cProtect[BSL_WAV_FILE_PROT_SIZE];
#endif /* BSL_WAV_FILE_PROT_SIZE */

#ifdef BSL_WAV_FILE_AUTH_SIZE
	/*! ���쌠�Ǘ���񕶎��� */
	TCHAR cAuthor[BSL_WAV_FILE_AUTH_SIZE];
#endif /* BSL_WAV_FILE_AUTH_SIZE */

#ifdef BSL_WAV_FILE_SUPT_SIZE
	/*! �T�|�[�g��񕶎��� */
	TCHAR cSupport[BSL_WAV_FILE_SUPT_SIZE];
#endif /* BSL_WAV_FILE_SUPT_SIZE */

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

#if (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi) || (defined BSL_WAV_INCLUDE_FILE_TYPE_MFi5)
	/*! �Ĕz�z�t�����ʎq */
	BYTE  byMFiSorc;
	/*! �m�[�g���b�Z�[�W����񕶎��� */
	WORD wMFiNoteMessLength;
	/*! �g���X�e�[�^�XA���b�Z�[�W����񕶎��� */
	WORD wMFiExstStatusALength;
#endif

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! ���t���� */
	DWORD dwPlayTime;
	/*! ���t�I����̖������� */
	DWORD dwBlankTime;
	/*! �I�[�f�B�I�{�����[�� */
	DWORD dwAudioVolume;
	/*! 3D���ʂ̗L�� */
	BOOL  b3DEfx;
	/*! �o�[�W�������*/
	int   nMFiVersion;

	/*! �[���̃��[�J�[ID*/
	int	  nMakerID;
	/*! MFi�C�x���g���� */
	BSLMFiUtil MFiUtil;
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_LOOPS
	int nLoops;
	BSL_WAV_LOOP loop[BSL_WAV_LOOPS];
#endif /* BSL_WAV_LOOPS */

	/*! �A�b�v�f�[�g�����֐� */
	BSL_WAV_UPDATER pUpdater;
	/*! �A�b�v�f�[�g�����p�p�����[�^ */
	void *pStreamParam;

	/*!
		�X�g���[���f�[�^�m�ۗp�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g�ɂčs�����ƁB
		�������J����bslWavParserExit()�ɂčs����B
	*/
	void *pInternalCache;
	/*! �X�g���[���f�[�^�m�ۗp�|�C���^�̃T�C�Y[Byte] */
	DWORD dwInternalCacheSize;

	/*!
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g�ɂčs�����ƁB
		�������J����bslWavWriterExit()�ɂčs����B
	*/
	void *pInternalData;
} BSLWavWriter;

/* globals */

/* locals */

/* function declarations */

void bslWavWriter (BSLWavWriter *obj);
void bslWavWriterExit (BSLWavWriter *obj);

BSLErr _bslWavWriterSetType (BSLWavWriter *obj, BSL_WAV_FILE_TYPE type);
BSLErr _bslWavWriterSetCodec (BSLWavWriter *obj, BSL_WAV_CODEC codec);
BSLErr _bslWavWriterSetFrames (BSLWavWriter *obj, DWORD frames);
BSLErr _bslWavWriterSetSampleRate (BSLWavWriter *obj, int sampleRate);
BSLErr _bslWavWriterSetBitsPerSample (BSLWavWriter *obj, int bitsPerSample);
BSLErr _bslWavWriterSetBitsPerSampleRendering (BSLWavWriter *obj, int bitsPerSampleRendering);
BSLErr _bslWavWriterSetBitRate (BSLWavWriter *obj, DWORD bitRate);
DWORD _bslWavWriterGetBitRateLinear (BSLWavWriter *obj);
BSLErr _bslWavWriterSetChannels (BSLWavWriter *obj, int channels);
BSLErr _bslWavWriterSetUnsigned (BSLWavWriter *obj, BOOL isUnsigned);
BSLErr _bslWavWriterSetBigEndian (BSLWavWriter *obj, BOOL isBigEndian);
BSLErr _bslWavWriterSetFloat (BSLWavWriter *obj, BOOL flag);
BSLErr _bslWavWriterBounce (BSLFileSave *file, BSLWavWriter *obj, BSLWavRender *render, DWORD *renderedFrames);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavWriterh */
