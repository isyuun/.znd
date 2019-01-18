/*
 *  BSLError.h
 *
 *  Copyright (c) 2004-2009 bismark. All rights reserved.
 *
 */

/*!
	@file BSLError.h
	@brief
	�G���[�Ǘ� (�G���[�R�[�h��`�̂�)
*/

#ifndef __INCBSLErrorh
#define __INCBSLErrorh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

/* defines */

/*! �G���[�R�[�h */
typedef enum {
	/*! ����I�� */
	BSL_OK = 0,

	/*! ���[�U�[�L�����Z�� */
	BSL_ERR_CANCELED,
	/*! ����`�G���[ */
	BSL_ERR_UNDEFINED,
	/*! �v���e�N�V�����G���[ */
	BSL_ERR_PROTECTED,
	/*! ���Ή����� */
	BSL_ERR_NOT_SUPPORTED,
	/*! �O�����W���[�� */
	BSL_ERR_MODULE,


	/*! �p�����[�^�F�p�����[�^�ُ� */
	BSL_ERR_PARAM_WRONG,
	/*! �p�����[�^�F�p�����[�^�͈͊O */
	BSL_ERR_PARAM_RANGE,


	/*! �t�@�C���F�I�[�v�� */
	BSL_ERR_FILE_OPEN,
	/*! �t�@�C���F�V�K�쐬 */
	BSL_ERR_FILE_CREATE,
	/*! �t�@�C���F�T�C�Y�擾 */
	BSL_ERR_FILE_SIZE,
	/*! �t�@�C���F���I�[�v�� */
	BSL_ERR_FILE_NOT_READY,
	/*! �t�@�C���F���[�h */
	BSL_ERR_FILE_READ,
	/*! �t�@�C���FEOF */
	BSL_ERR_FILE_READ_EOF,
	/*! �t�@�C���F���C�g */
	BSL_ERR_FILE_WRITE,
	/*! �t�@�C���F�V�[�N */
	BSL_ERR_FILE_SEEK,
	/*! �t�@�C���F����`�t�H�[�}�b�g */
	BSL_ERR_FILE_UNKNOWN_STRUCTURE,
	/*! �t�@�C���FRIFF�w�b�_�֘A */
	BSL_ERR_FILE_RIFF_HEADER,
	/*! �t�@�C���F���Ή��t�H�[�}�b�g */
	BSL_ERR_FILE_UNSUPPORTED_FORMAT,
	

	/*! �������F�������擾 */
	BSL_ERR_MEM_ALLOC,
	/*! �������F�������j�� */
	BSL_ERR_MEM_FREE,
	/*! �������F�q�[�v�\�z */
	BSL_ERR_MEM_HEAP_CREATE,
	/*! �������F�q�[�v�j�� */
	BSL_ERR_MEM_HEAP_DESTROY,
	

	/*! Mid�F���̓h���C�o�I�[�v�� */
	BSL_ERR_MID_IN_OPEN,
	/*! Mid�F���̓h���C�o�N���[�Y */
	BSL_ERR_MID_IN_CLOSE,
	/*! Mid�F���͊J�n */
	BSL_ERR_MID_IN_START,
	/*! Mid�F���͒�~ */
	BSL_ERR_MID_IN_STOP,


	/*! Mid�F�o�̓h���C�o�I�[�v�� */
	BSL_ERR_MID_OUT_OPEN,
	/*! Mid�F�o�̓h���C�o�N���[�Y */
	BSL_ERR_MID_OUT_CLOSE,
	/*! Mid�F�o�͊J�n */
	BSL_ERR_MID_OUT_START,
	/*! Mid�F�o�͒�~ */
	BSL_ERR_MID_OUT_STOP,


	/*! Wav�F���̓h���C�o�I�[�v�� */
	BSL_ERR_WAV_IN_OPEN,
	/*! Wav�F���̓h���C�o�N���[�Y */
	BSL_ERR_WAV_IN_CLOSE,
	/*! Wav�F���͊J�n */
	BSL_ERR_WAV_IN_START,
	/*! Wav�F���͒�~ */
	BSL_ERR_WAV_IN_STOP,
	/*! Wav�F���͈ꎞ��~ */
	BSL_ERR_WAV_IN_PAUSE,
	/*! Wav�F���͍ĊJ�n */
	BSL_ERR_WAV_IN_RESTART,
	/*! Wav�F���̓h���C�o���Z�b�g */
	BSL_ERR_WAV_IN_RESET,
#if BSL_WINDOWS
	/*! Wav�F���̓o�b�t�@ */
	BSL_ERR_WAV_IN_PREPARE_HEADER,
	/*! Wav�F���̓o�b�t�@ */
	BSL_ERR_WAV_IN_UNPREPARE_HEADER,
#endif /* BSL_WINDOWS */
	/*! Wav�F���̓o�b�t�@�ǉ� */
	BSL_ERR_WAV_IN_ADD_BUFFER,
	/*! Wav�F���̓X���b�h�E�Z�}�t�H�� */
	BSL_ERR_WAV_IN_PROCESS,
	/*! Wav�F�o�̓h���C�o�I�[�v�� */
	BSL_ERR_WAV_OUT_OPEN,
	/*! Wav�F�o�̓h���C�o�N���[�Y */
	BSL_ERR_WAV_OUT_CLOSE,
	/*! Wav�F�o�͊J�n */
	BSL_ERR_WAV_OUT_START,
	/*! Wav�F�o�͒�~ */
	BSL_ERR_WAV_OUT_STOP,
	/*! Wav�F�o�͈ꎞ��~ */
	BSL_ERR_WAV_OUT_PAUSE,
	/*! Wav�F�o�͍ĊJ�n */
	BSL_ERR_WAV_OUT_RESTART,
	/*! Wav�F�o�̓h���C�o���Z�b�g */
	BSL_ERR_WAV_OUT_RESET,
#if BSL_WINDOWS
	/*! Wav�F�o�̓o�b�t�@ */
	BSL_ERR_WAV_OUT_PREPARE_HEADER,
	/*! Wav�F�o�̓o�b�t�@ */
	BSL_ERR_WAV_OUT_UNPREPARE_HEADER,
#endif /* BSL_WINDOWS */
	/*! Wav�F�o�̓o�b�t�@���C�g */
	BSL_ERR_WAV_OUT_WRITE,
	/*! Wav�F�o�̓X���b�h�E�Z�}�t�H�� */
	BSL_ERR_WAV_OUT_PROCESS,
	/*! Wav�F�o�̓f�[�^�ُ� */
	BSL_ERR_WAV_OUT_DATA,
	/*! Wav�F�T���v�����O���g���ݒ� */
	BSL_ERR_WAV_SET_SAMPLE_RATE,
	/*! Wav�F�r�b�g���x�ݒ� */
	BSL_ERR_WAV_SET_BITS_PER_SAMPLE,
	/*! Wav�F�r�b�g���[�g�ݒ� */
	BSL_ERR_WAV_SET_BIT_RATE,
	/*! Wav�F�u���b�N�T�C�Y�ݒ� */
	BSL_ERR_WAV_SET_BLOCK_SIZE,
	/*! Wav�F�`�����l�����ݒ� */
	BSL_ERR_WAV_SET_CHANNELS,
	/*! Wav�F�G���f�B�A���ݒ� */
	BSL_ERR_WAV_SET_BIGENDIAN,
	/*! Wav�FUnsigned�ݒ� */
	BSL_ERR_WAV_SET_UNSIGNED,
	/*! Wav�F�o�b�t�@���ݒ� */
	BSL_ERR_WAV_SET_BUFFERS,
	/*! Wav�F�R�[�f�b�N�ݒ� */
	BSL_ERR_WAV_SET_CODEC,
	/*! Wav�F�f�R�[�h���� */
	BSL_ERR_WAV_DEC,
	/*! Wav�F�f�R�[�h����-�㑱�̃f�[�^���K�v- */
	BSL_ERR_WAV_DEC_REQUEST_FOLLOWING,
	/*! Wav�F�f�R�[�h����-�ŏI�f�[�^�s��- */
	BSL_ERR_WAV_DEC_LAST_DATA,
	/*! Wav�F�f�R�[�h����-�I�[�o�[�t���[- */
	BSL_ERR_WAV_DEC_OVERFLOW,
	/*! Wav�F�G���R�[�h���� */
	BSL_ERR_WAV_ENC,

	/*! Wav�f�[�^�FWAVE�f�[�^�̍Đ����Ԃ��K��ɖ����Ȃ� */
	BSL_ERR_WAVDATA_SHORT_PLAYTIME,

	/*! CDRIP : */
	BSL_ERR_CDRIP_OPEN,
	/*! CDRIP : */
	BSL_ERR_CDRIP_CLOSE,
	/*! CDRIP : */
	BSL_ERR_CDRIP_INVALID_HANDLE,
	/*! CDRIP : */
	BSL_ERR_CDRIP_MEDIA_CHANGED,
	/*! CDRIP : */
	BSL_ERR_CDRIP_GETTOC,
	/*! CDRIP : */
	BSL_ERR_CDRIP_READCD,

	BSL_ERR_HEX_WRONG_ENDRECORD,
	BSL_ERR_HEX_INVALID_CHECKSUM,
	BSL_ERR_HEX_INVALID_ENDRECORD,
	BSL_ERR_HEX_NO_ENDRECORD,
	BSL_ERR_HEX_NO_VALID_DATARECORD,
	BSL_ERR_HEX_INVALID_FORMAT,
	BSL_ERR_HEX_ADDRESS_OVERLAP,

	BSL_ERR_MAX
} BSLErr;

/* typedefs */

/* globals */

/* loclas */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLErrorh */

