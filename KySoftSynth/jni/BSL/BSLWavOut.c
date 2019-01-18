/*
 *  BSLWavOut.c
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavOut.c
	@brief
	Wave�o�͐���I�u�W�F�N�g
*/

/* includes */

#include "BSL.h"
#include "BSLMem.h"
#include "BSLWav.h"
#include "BSLWavOut.h"

/* defines */

#ifdef BSL_WAV_OUT_FADEOUT
#define kFadeoutBitShift 14
#endif /* BSL_WAV_OUT_FADEOUT */

#ifndef BSL_WAV_OUT_FADEOUT_LENGTH
/*!
	�t�F�[�h�A�E�g����[ms]�BBSLConf.h�ŏ㏑���\�B
	�������A���󂱂̐ݒ�l�͖����m�ł��邱�Ƃɒ��ӁB
	200[ms]��蒷���l���Z�b�g���Ă���������Ȃ��B
*/
#define BSL_WAV_OUT_FADEOUT_LENGTH 200
#endif /* BSL_WAV_OUT_FADEOUT_LENGTH */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�̓T���v�����O���g�����擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�T���v�����O���g��[Hz]
*/
static int _getSampleRate
	(
	BSLWavOut *obj
	)
{
	return obj->nSampleRate;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�͂̍ő又���t���[�������擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�ő又���t���[����[sample]
*/
static long _getBlockSize
	(
	BSLWavOut *obj
	)
{
	return (obj->tRender.getBlockSize (&obj->tRender));
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�̓r�b�g���x���擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�r�b�g���x[bit]
*/
static int _getBitsPerSample
	(
	BSLWavOut *obj
	)
{
	return (obj->nBitsPerSample);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Frame�������Wav�o�̓o�C�g�����擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�o�C�g��[byte]
*/
static int _getBytesPerFrame
	(
	BSLWavOut *obj
	)
{
	return (obj->getChannels (obj) * obj->getBitsPerSample (obj) / 8);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�̓`�����l�������擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�`�����l����
*/
static int _getChannels
	(
	BSLWavOut *obj
	)
{
	return (obj->nChannels);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�̓o�b�t�@�����擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	�o�b�t�@��
*/
static int _getBuffers
	(
	BSLWavOut *obj
	)
{
#if BSL_MAC || BSL_IOS
	return obj->nBuffers;
#else
	return (obj->tBuff.getBuffers (&obj->tBuff));
#endif
}

#ifdef BSL_WAV_OUT_VOLUME
/*---------------------------------------------------------------------------*/
static BSLErr _setVolume
	(
	BSLWavOut *obj,
	DWORD value
	)
{
#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output volume = %lu\n"), value);
#endif /* BSL_DEBUG */
	obj->dwVolume = value;

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static DWORD _getVolume
	(
	BSLWavOut *obj
	)
{
	return obj->dwVolume;
}
#endif /* BSL_WAV_OUT_VOLUME */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�͍Đ����ł��邩���擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	BOOL
*/
static BOOL _isPlaying
	(
	BSLWavOut *obj
	)
{
	return obj->bPlaying;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�͍Đ����I�����������擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	BOOL
*/
static BOOL _isPlayDone
	(
	BSLWavOut *obj
	)
{
	if (obj->bStop) {
		return (0 < obj->nBlockedBuffers) ? FALSE : TRUE;
	}
	else {
		return FALSE;
	}
}

#ifdef BSL_WAV_OUT_PAUSE
/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wav�o�͍Đ����ꎞ��~�������擾�������J�֐��B
	BSL_WAV_OUT_PAUSE��`���̂ݗL���B
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	BOOL
*/
static BOOL _isPaused
	(
	BSLWavOut *obj
	)
{
	return obj->bPaused;
}
#endif /* BSL_WAV_OUT_PAUSE */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	���[�U�f�[�^��ݒ肷�����J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param user
	���[�U�f�[�^�̃A�h���X
*/
static void _setUser (BSLWavOut *obj, void *user)
{
	obj->tRender.pUser = user;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	���[�U�f�[�^���擾�������J�֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	���[�U�f�[�^�̃A�h���X
*/
static void *_getUser (BSLWavOut *obj)
{
	return (obj->tRender.pUser);
}

#pragma mark -
#ifdef BSL_WAV_OUT_FADEOUT
/*---------------------------------------------------------------------------*/
/*!
	@brief
	�t�F�[�h�A�E�g���������Z�b�g�������J�֐��B
	BSL_WAV_OUT_FADEOUT��`���̂ݗL���B
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
static void _resetFadeout
	(
	BSLWavOut *obj
	)
{
#ifdef BSL_WAV_IO_FLOAT
    obj->aFadeout = -1.f;
#else /* BSL_WAV_IO_FLOAT */
	obj->lFadeout = -1L;
#endif /* BSL_WAV_IO_FLOAT */
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�t�F�[�h�A�E�g�����̊J�n���s������J�֐��B
	BSL_WAV_OUT_FADEOUT��`���̂ݗL���B
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
static void _startFadeout
	(
	BSLWavOut *obj
	)
{
#ifdef BSL_WAV_IO_FLOAT
    obj->aFadeout = 1.f;
#else /* BSL_WAV_IO_FLOAT */
	obj->lFadeout = 1L << kFadeoutBitShift;
#endif /* BSL_WAV_IO_FLOAT */
#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output fadeout started\n"));
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�t�F�[�h�A�E�g�����̍s������J�֐��B
	BSL_WAV_OUT_FADEOUT��`���̂ݗL���B
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param buffer
	�f�[�^�A�h���X
	@param sampleFrames
	�����t���[����[sample]
*/
static void _processFadeout
	(
	BSLWavOut *obj,
	BSLWavIOData *buffer, 
	long sampleFrames
	)
{
#ifdef BSL_WAV_IO_FLOAT
    
    BSLWavIOData fadeout = obj->aFadeout;
    if (0.f <= fadeout) {
        BSLWavIOData fadeunit = obj->aFadeUnit;

        int ch = obj->getChannels (obj);
        
        BSLWavIOData *d = (BSLWavIOData *) buffer;
        while (--sampleFrames >= 0L) {
            int c;
            for (c = 0; c < ch; c++) {
                *d = *d * fadeout;
                d++;
            }
            if (0.f < fadeout) {
                fadeout -= fadeunit;
                if (fadeout <= 0.f) {
#ifdef BSL_DEBUG
                    bslTrace (_TEXT("WAV : wave output fadeout completed\n"));
#endif /* BSL_DEBUG */
#ifndef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
                    obj->bStop = TRUE;
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
                    fadeout = 0.f;
                }
            }
            else {
#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
                if (obj->lFadeOutZeroSampleDef < obj->lFadeOutZeroSample++) {
#ifdef BSL_DEBUG
                    if (!obj->bStop) bslTrace (_TEXT("WAV : wave output fadeout zero overwrap completed\n"));
#endif /* BSL_DEBUG */
                    obj->bStop = TRUE;
                }
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
            }
        }
        
        obj->aFadeout = fadeout;
    }
    
#else /* BSL_WAV_IO_FLOAT */
    
    long fadeout = obj->lFadeout;
	if (0L <= fadeout) {
        long fadeunit = obj->lFadeUnit;

        int ch = obj->getChannels (obj);
		switch (obj->getBitsPerSample (obj)) {
		case 16:
			{
				BSLWavIOData16 *d = (BSLWavIOData16 *) buffer;
				while (--sampleFrames >= 0L) {
					int c;
					for (c = 0; c < ch; c++) {
						*d = (BSLWavIOData16) (((long) *d * fadeout) >> kFadeoutBitShift);
						d++;
					}
					if (0L < fadeout) {
						fadeout -= fadeunit;
						if (fadeout <= 0L) {
#ifdef BSL_DEBUG
							bslTrace (_TEXT("WAV : wave output fadeout completed\n"));
#endif /* BSL_DEBUG */
#ifndef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
							obj->bStop = TRUE;
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
							fadeout = 0L;
						}
					}
					else {
#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
						if (obj->lFadeOutZeroSampleDef < obj->lFadeOutZeroSample++) {
	#ifdef BSL_DEBUG
							if (!obj->bStop) bslTrace (_TEXT("WAV : wave output fadeout zero overwrap completed\n"));
	#endif /* BSL_DEBUG */
							obj->bStop = TRUE;
						}
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
					}
				}
			}
			break;
		case 8:
			{
				BSLWavIOData08 *d = (BSLWavIOData08 *) buffer;
				while (--sampleFrames >= 0L) {
					int c;
					for (c = 0; c < ch; c++) {
						*d = (BSLWavIOData08) 
#ifdef BSL_WAV_IO_UNSIGNED_8BIT
							((((((long) *d - 0x80) << 8) * fadeout) >> kFadeoutBitShift) >> 8) + 0x80; 
#else /* BSL_WAV_IO_UNSIGNED_8BIT */
							(((((long) *d << 8) * fadeout) >> kFadeoutBitShift) >> 8); 
#endif /* BSL_WAV_IO_UNSIGNED_8BIT */
						d++;
					}
					if (0L < fadeout) {
						fadeout -= fadeunit;
						if (fadeout <= 0L) {
#ifndef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
							obj->bStop = TRUE;
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
							fadeout = 0L;
						}
					}
					else {
#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
						if (obj->lFadeOutZeroSampleDef < obj->lFadeOutZeroSample++) {
	#ifdef BSL_DEBUG
							if (!obj->bStop) bslTrace (_TEXT("WAV : wave output fadeout zero overwrap completed\n"));
	#endif /* BSL_DEBUG */
							obj->bStop = TRUE;
						}
#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
					}
				}
			}
			break;
		}

		obj->lFadeout = fadeout;
	}
    
#endif /* BSL_WAV_IO_FLOAT */
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�t�F�[�h�A�E�g�����������ς����擾�������J�֐��B
	BSL_WAV_OUT_FADEOUT��`���̂ݗL���B
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@retval
	BOOL
*/
static BOOL _isFadeoutDone
	(
	BSLWavOut *obj
	)
{
#ifdef BSL_WAV_IO_FLOAT
    if (obj->aFadeout <= 0.f) {
        return (TRUE);
    }
#else /* BSL_WAV_IO_FLOAT */
    if (obj->lFadeout <= 0L) {
        return (TRUE);
    }
#endif /* BSL_WAV_IO_FLOAT */
	return (FALSE);
}
#endif /* BSL_WAV_OUT_FADEOUT */

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wave�o�͊�{����I�u�W�F�N�g�̏��������s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
BSLErr _bslWavOutInitialize
	(
	BSLWavOut *obj
	)
{
	BSLErr err = BSL_OK;

	if (obj->isPlaying (obj)) {
		return BSL_ERR_UNDEFINED;
	}

#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output initialize\n"));
#endif /* BSL_DEBUG */

	obj->close (obj);

	if (err == BSL_OK) {
		err = obj->setSampleRate (obj, obj->getSampleRate (obj));
	}
	
	if (err == BSL_OK) {
		err = obj->setBlockSize (obj, obj->getBlockSize (obj));
	}

	if (err == BSL_OK) {
		err = obj->setChannels (obj, obj->getChannels (obj));
	}

	if (err == BSL_OK) {
		err = obj->setBitsPerSample (obj, obj->getBitsPerSample (obj));
	}

	obj->nBufferIndex = 0;
	obj->nBlockedBuffers = 0;

#ifdef BSL_WAV_OUT_FADEOUT
	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	obj->lFadeOutZeroSample = 0L;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�T���v�����O���g���̐ݒ���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param sampleRate
	�T���v�����O���g��[Hz]
*/
BSLErr _bslWavOutSetSampleRate
	(
	BSLWavOut *obj, 
	int sampleRate
	)
{
	if (obj->isOpened (obj)) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : can't set wave output sample rate\n"));
#endif /* BSL_DEBUG */
		return (BSL_ERR_WAV_SET_SAMPLE_RATE);
	}

	obj->nSampleRate = sampleRate;

#ifdef BSL_WAV_OUT_FADEOUT
	if (sampleRate) {
#ifdef BSL_WAV_IO_FLOAT
        obj->aFadeUnit = 1000.f / ((float) sampleRate * (float) BSL_WAV_OUT_FADEOUT_LENGTH);
#else /* BSL_WAV_IO_FLOAT */
		obj->lFadeUnit = (long) ((1000.f / ((float) sampleRate * (float) BSL_WAV_OUT_FADEOUT_LENGTH)) * (float) (1 << kFadeoutBitShift) + 0.5f);
		if (obj->lFadeUnit < 1L) {
			obj->lFadeUnit = 1L;
		}
#endif /* BSL_WAV_IO_FLOAT */
	}
	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	obj->lFadeOutZeroSampleDef = sampleRate / 10; /* 0.1[s] */
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output sample rate = %d[Hz]\n"), sampleRate);
#endif /* BSL_DEBUG */

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�ő�u���b�N�T�C�Y�̐ݒ���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param blockSize
	�ő�u���b�N�T�C�Y[sample]
*/
BSLErr _bslWavOutSetBlockSize
	(
	BSLWavOut *obj, 
	long blockSize
	)
{
	BSLErr err = BSL_OK;

	if (obj->isOpened (obj)) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : can't set wave output block size\n"));
#endif /* BSL_DEBUG */
		err = BSL_ERR_WAV_SET_BLOCK_SIZE;
	}

	if (err == BSL_OK) {
		if (blockSize <= 0L) err = BSL_ERR_WAV_SET_BLOCK_SIZE;
	}

	if (err == BSL_OK) {
		err = obj->tRender.setBlockSize (&obj->tRender, blockSize);
	}

#if !BSL_MAC && !BSL_IOS
	if (err == BSL_OK) {
		long size = obj->getBlockSize (obj) * (long) obj->getChannels (obj) * (long) obj->getBitsPerSample (obj) / 8L;
		err = obj->tBuff.resize (&obj->tBuff, obj->tBuff.getBuffers (&obj->tBuff), size);
	}
#endif

#ifdef BSL_DEBUG
	if (err == BSL_OK) {
		bslTrace (_TEXT("WAV : wave output block size = %ld[sample]\n"), blockSize);
	}
#endif /* BSL_DEBUG */

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�r�b�g���x�̐ݒ���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param bitsPerSample
	�r�b�g���x[bit]
*/
BSLErr _bslWavOutSetBitsPerSample
	(
	BSLWavOut *obj,
	int bitsPerSample
	)
{
	BSLErr err = BSL_OK;

	if (obj->isOpened (obj)) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : can't set wave output bits per sample\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_UNDEFINED;
	}

	obj->nBitsPerSample = bitsPerSample;

#if !BSL_MAC && !BSL_IOS
	if (err == BSL_OK) {
		long size = obj->getBlockSize (obj) * (long) obj->getChannels (obj) * (long) obj->getBitsPerSample (obj) / 8L;
		err = obj->tBuff.resize (&obj->tBuff, obj->tBuff.getBuffers (&obj->tBuff), size);
	}
#endif

#ifdef BSL_DEBUG
	if (err == BSL_OK) {
		bslTrace (_TEXT("WAV : wave output bits per sample = %d\n"), bitsPerSample);
	}
#endif /* BSL_DEBUG */

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�`�����l�����̐ݒ���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param channels
	�`�����l����
*/
BSLErr _bslWavOutSetChannels
	(
	BSLWavOut *obj,
	int channels
	)
{
	BSLErr err = BSL_OK;

	if (obj->isOpened (obj)) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : can't set wave output channels\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_UNDEFINED;
	}

	obj->nChannels = channels;

#if !BSL_MAC && !BSL_IOS
	if (err == BSL_OK) {
		long size = obj->getBlockSize (obj) * (long) obj->getChannels (obj) * (long) obj->getBitsPerSample (obj) / 8L;
		err = obj->tBuff.resize (&obj->tBuff, obj->tBuff.getBuffers (&obj->tBuff), size);
	}
#endif

#ifdef BSL_DEBUG
	if (err == BSL_OK) {
		bslTrace (_TEXT("WAV : wave output channels = %d\n"), channels);
	}
#endif /* BSL_DEBUG */

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�o�b�t�@���̐ݒ���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param buffers
	�o�b�t�@��
*/
BSLErr _bslWavOutSetBuffers
	(
	BSLWavOut *obj, 
	int buffers
	)
{
	BSLErr err = BSL_OK;

	if (obj->isOpened (obj)) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("ERROR-WAV : can't set wave output buffers\n"));
#endif /* BSL_DEBUG */
		return BSL_ERR_UNDEFINED;
	}

	if (err == BSL_OK) {
#if BSL_MAC || BSL_IOS
		if (buffers <= 0) {
			obj->nBuffers = 1;
		}
		else if (buffers > BSL_WAV_BUFFERS_MAX) {
			obj->nBuffers = BSL_WAV_BUFFERS_MAX;
		}
		else {
			obj->nBuffers = buffers;
		}
#else
		long size = obj->getBlockSize (obj) * (long) obj->getChannels (obj) * (long) obj->getBitsPerSample (obj) / 8L;
		err = obj->tBuff.resize (&obj->tBuff, buffers, size);
#endif
	}

#ifdef BSL_DEBUG
	if (err == BSL_OK) {
		bslTrace (_TEXT("WAV : wave output buffers = %d\n"), buffers);
	}
#endif /* BSL_DEBUG */

	return err;
}

#ifdef BSL_WAV_OUT_PAUSE
/*---------------------------------------------------------------------------*/
/*!
	@brief
	�Đ��ꎞ��~���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
BSLErr _bslWavOutPause
	(
	BSLWavOut *obj
	)
{
	if (!obj->isOpened (obj)) {
		return BSL_ERR_UNDEFINED;
	}

	if (!obj->isPlaying (obj)) {
		return BSL_ERR_UNDEFINED;
	}

	obj->bPausing = TRUE;

#ifdef BSL_WAV_OUT_FADEOUT
	obj->startFadeout (obj);
#else /* BSL_WAV_OUT_FADEOUT */
	obj->bStop = TRUE;
#endif /* BSL_WAV_OUT_FADEOUT */

#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output pause\n"));
#endif /* BSL_DEBUG */

	return BSL_OK;
}
#endif /* BSL_WAV_OUT_PAUSE */

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�Đ���~���s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
BSLErr _bslWavOutStop
	(
	BSLWavOut *obj
	)
{
#ifdef BSL_WAV_OUT_PAUSE
	if (obj->isPaused (obj)) {
		obj->bPaused = FALSE;
		obj->stopped (obj, BSL_OK);
		return BSL_OK;
	}
#endif /* BSL_WAV_OUT_PAUSE */

	if (!obj->isOpened (obj)) {
		return BSL_ERR_UNDEFINED;
	}

	if (!obj->isPlaying (obj)) {
		return BSL_ERR_UNDEFINED;
	}

#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output stop\n"));
#endif /* BSL_DEBUG */

#ifdef BSL_WAV_OUT_FADEOUT
	obj->startFadeout (obj);
#else /* BSL_WAV_OUT_FADEOUT */
	obj->bStop = TRUE;
#endif /* BSL_WAV_OUT_FADEOUT */

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�Đ���~�����������s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param error
	��~�G���[���e
*/
void _bslWavOutStopped
	(
	BSLWavOut *obj,
	BSLErr error
	)
{
	if (!obj->isOpened (obj)) {
		return;
	}

#ifdef BSL_WAV_OUT_PAUSE
	if (obj->bPausing) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : wave output was paused [%d]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
		obj->bPausing = FALSE;
		obj->bPaused = TRUE;
	}
	else {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : wave output was stopped [%d]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
	}
#else /* BSL_WAV_OUT_PAUSE */
#ifdef BSL_DEBUG
	bslTrace (_TEXT("WAV : wave output stopped [%d]\n"), obj->nBlockedBuffers);
#endif /* BSL_DEBUG */
#endif /* BSL_WAV_OUT_PAUSE */

	obj->nBufferIndex = 0;
	obj->nBlockedBuffers = 0;
	obj->bPlaying = FALSE;
	obj->bStop = FALSE;

#ifdef BSL_WAV_OUT_FADEOUT
	obj->resetFadeout (obj);
	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	obj->lFadeOutZeroSample = 0;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	�f�o�C�X���Z�b�g�������s�������֐�
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
BSLErr _bslWavOutReset
	(
	BSLWavOut *obj
	)
{
	BSLErr err = BSL_OK;

	if (!obj->isOpened (obj)) {
		return BSL_ERR_UNDEFINED;
	}
	if (!obj->isPlaying (obj)) {
		return BSL_ERR_UNDEFINED;
	}

	obj->bStop = FALSE;
	obj->nBufferIndex = 0;
	obj->nBlockedBuffers = 0;

#ifdef BSL_WAV_OUT_FADEOUT
	obj->resetFadeout (obj);
	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	obj->lFadeOutZeroSample = 0L;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : wave output reset\n"));
#endif /* BSL_DEBUG */
		obj->bPlaying = TRUE;
		return BSL_OK;
	}
	else {
		obj->bPlaying = FALSE;
		return err;
	}
}

#pragma mark -
#ifdef BSL_WAV_OUT_INCLUDE_THREAD
#ifdef BSL_WAV_OUT_PAUSE
/*---------------------------------------------------------------------------*/
static BSLErr _pause_thread
	(
	BSLWavOut *obj
	)
{
	obj->bPausing = TRUE;
	obj->bThreadStop = TRUE;
	
	return BSL_OK;
}
#endif /* BSL_WAV_OUT_PAUSE */

/*---------------------------------------------------------------------------*/
BSLErr _bslWavOutStop_thread
	(
	BSLWavOut *obj
	)
{
#ifdef BSL_WAV_OUT_PAUSE
	if (obj->isPaused (obj)) {
		obj->bPaused = FALSE;
		obj->stopped (obj, BSL_OK);
		return BSL_OK;
	}
#endif /* BSL_WAV_OUT_PAUSE */

	obj->bThreadStop = TRUE;
	
	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
BSLErr _bslWavOutThreadStop
	(
	BSLWavOut *obj
	)
{
	BSLErr err = BSL_OK;

	obj->bThreadStop = FALSE;
	obj->bThreadWaitStopped = TRUE;

	if (err == BSL_OK) {
		err = _bslWavOutStop (obj);
	}

	return err;
}

#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wave�o�͊�{����I�u�W�F�N�g�̍\�z���s��
	@param obj
	����I�u�W�F�N�g�̃A�h���X
	@param render
	rendering�֐�
	@param user
	���[�U�p�����[�^
*/
void bslWavOut
	(
	BSLWavOut *obj, 
	BSL_WAV_RENDER render, 
	void *user
	)
{
	memset (obj, 0, sizeof (BSLWavOut));
	bslWavRender (&obj->tRender, render, user);
#if !BSL_MAC && !BSL_IOS
	bslWavBuff (&obj->tBuff);
#endif

	obj->nSampleRate = BSL_WAV_SAMPLE_RATE;
	obj->nBitsPerSample = BSL_WAV_BITS_PER_SAMPLE;
	obj->nChannels = BSL_WAV_CHANNELS;
#if BSL_MAC || BSL_IOS
	obj->nBuffers = BSL_WAV_BUFFERS;
#endif

	obj->bPlaying = FALSE;
	obj->bStop = FALSE;
	obj->bStopError = FALSE;
#ifdef BSL_WAV_OUT_PAUSE
	obj->bPausing = FALSE;
	obj->bPaused = FALSE;
#endif /* BSL_WAV_OUT_PAUSE */
	obj->nBufferIndex = 0;
	obj->nBlockedBuffers = 0;

	obj->initialize = _bslWavOutInitialize;
	obj->finalize = NULL;

	obj->setSampleRate = _bslWavOutSetSampleRate;
	obj->getSampleRate = _getSampleRate;
	obj->setBlockSize = _bslWavOutSetBlockSize;
	obj->getBlockSize = _getBlockSize;
	obj->setBitsPerSample = _bslWavOutSetBitsPerSample;
	obj->getBitsPerSample = _getBitsPerSample;
	obj->setChannels = _bslWavOutSetChannels;
	obj->getChannels = _getChannels;
	obj->getBytesPerFrame = _getBytesPerFrame;
	obj->setBuffers = _bslWavOutSetBuffers;
	obj->getBuffers = _getBuffers;

#ifdef BSL_WAV_OUT_VOLUME
	obj->setVolume = _setVolume;
	obj->getVolume = _getVolume;
#endif /* BSL_WAV_OUT_VOLUME */

/*	obj->open = ;  should be set in upper object */
/*	obj->close = ;  should be set in upper object */
/*	obj->start = ;  should be set in upper object */
#ifdef BSL_WAV_OUT_PAUSE
	#ifdef BSL_WAV_OUT_INCLUDE_THREAD
	obj->pause = _pause_thread;
	#else /* BSL_WAV_OUT_INCLUDE_THREAD */
	obj->pause = _bslWavOutPause;
	#endif /* BSL_WAV_OUT_INCLUDE_THREAD */
#endif /* BSL_WAV_OUT_PAUSE */
	obj->stop = _bslWavOutStop;
	obj->stopped = _bslWavOutStopped;
/*	obj->reset = ;  should be set in upper object */

/*	obj->buffering = ;  should be set in upper object */

/*	obj->isOpened = ;  should be set in upper object */
	obj->isPlaying = _isPlaying;
#ifdef BSL_WAV_OUT_PAUSE
	obj->isPaused = _isPaused;
#endif /* BSL_WAV_OUT_PAUSE */
	obj->isPlayDone = _isPlayDone;

#ifdef BSL_WAV_OUT_FADEOUT
	obj->resetFadeout = _resetFadeout;
	obj->startFadeout = _startFadeout;
	obj->processFadeout = _processFadeout;
	obj->isFadeoutDone = _isFadeoutDone;
	obj->resetFadeout (obj);

	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	obj->lFadeOutZeroSampleDef = 0L;
	obj->lFadeOutZeroSample = 0L;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

	obj->setUser = _setUser;
	obj->getUser = _getUser;

#ifdef BSL_WAV_OUT_INCLUDE_THREAD
/*	obj->threadexit  = ;  should be set in upper object */
/*obj->threadstop = ;  should be set in upper object */

	#if BSL_WINDOWS
	obj->pThread = NULL;
	#elif BSL_LINUX
	obj->pThread = 0;
	obj->bThread = FALSE;
	#endif /* BSL_WINDOWS */

	obj->bThreadExit = FALSE;
	obj->bThreadStop = FALSE;
	obj->bThreadWaitStopped = FALSE;
	#ifdef BSL_WAV_DEBUG
	obj->bThreadTestRestart = FALSE;
	#endif /* BSL_WAV_DEBUG */
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

	obj->pInternalData = NULL;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Wave�o�͊�{����I�u�W�F�N�g�̔j�����s��
	@param obj
	����I�u�W�F�N�g�̃A�h���X
*/
void bslWavOutExit
	(
	BSLWavOut *obj
	)
{
	if (obj) {
		if (obj->isPlaying (obj)) {
			obj->stop (obj);
		}

#ifdef  BSL_WAV_OUT_INCLUDE_THREAD
		if (obj->threadexit) {
			obj->threadexit (obj);
		}
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

		obj->close (obj);
#if !BSL_MAC && !BSL_IOS
		bslWavBuffExit (&obj->tBuff);
#endif

		if (obj->finalize) {
			obj->finalize (obj);
		}

		if (obj->pInternalData) {
			bslMemFree (obj->pInternalData);
			obj->pInternalData = NULL;
		}
	}
}


