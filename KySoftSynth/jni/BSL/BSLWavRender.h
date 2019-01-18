/*
 *  BSLWavRender.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavRender.h
	@brief
	Wave Rendering����I�u�W�F�N�g
*/

#ifndef __INCBSLWavRenderh
#define __INCBSLWavRenderh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLWav.h"

/* defines */

/*! �����_�����O�����X�e�[�^�X */
typedef enum  {
	/*! ������ (�J�����g�o�b�t�@�t�����ɂ��ꎞ�I��) */
	BSL_WAV_RENDER_STATE_FULL,
	/*! �I�� */
	BSL_WAV_RENDER_STATE_COMPLETED,
	/*! �G���[��~ */
	BSL_WAV_RENDER_STATE_ERROR,
} BSL_WAV_RENDER_STATE;

/* typedefs */

/*!
	Wave Rendering������{����I�u�W�F�N�g
*/
typedef struct BSLWavRender
{
	/*! ���[�U�[�f�[�^��ݒ肷�郁�\�b�h */
	void *(*getUser) (struct BSLWavRender *obj);
	/*! ���[�U�[�f�[�^���擾���郁�\�b�h */
	void (*setUser) (struct BSLWavRender *obj, void *user);

	/*! �����_�����O�������s�����\�b�h */
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	BSL_WAV_RENDER_STATE (*render) (struct BSLWavRender *obj, BSLWavIOData *data, long sampleFrames, long *readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	BSL_WAV_RENDER_STATE (*render) (struct BSLWavRender *obj, BSLWavIOData *data, long sampleFrames);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */

	/*! �ő�u���b�N�T�C�Y[sample]��ݒ肷�郁�\�b�h */
	BSLErr (*setBlockSize) (struct BSLWavRender *obj, long blockSize);
	/*! �ő�u���b�N�T�C�Y[sample]���擾���郁�\�b�h */
	long (*getBlockSize) (struct BSLWavRender *obj);

	/*! �����_�����O�o�b�t�@��ݒ肷�郁�\�b�h */
	void (*setBuffer) (struct BSLWavRender *obj, BSLWavIOData *data);
	/*! �����_�����O�o�b�t�@���擾���郁�\�b�h */
	BSLWavIOData *(*getBuffer) (struct BSLWavRender *obj);

	/*! �ő�u���b�N�T�C�Y[sample] */
	long lBlockSize;
	/*! �����_�����O�o�b�t�@ */
	BSLWavIOData *pBuffer;
	/*! ���[�U�f�[�^ */
	void *pUser;
} BSLWavRender;

#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	typedef BSL_WAV_RENDER_STATE (*BSL_WAV_RENDER) (BSLWavRender *obj, BSLWavIOData *data, long sampleFrames, long *readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	typedef BSL_WAV_RENDER_STATE (*BSL_WAV_RENDER) (BSLWavRender *obj, BSLWavIOData *data, long sampleFrames);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */

/* globals */

/* locals */

/* function declarations */

void bslWavRender (BSLWavRender *obj, BSL_WAV_RENDER render, void *user);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavRenderh */
