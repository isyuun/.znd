/*
 *  BSLWavOut.h
 *
 *  Copyright (c) 2004-2008 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavOut.h
	@brief
	Wave�o�͐���I�u�W�F�N�g
*/

#ifndef __INCBSLWavOuth
#define __INCBSLWavOuth

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLWav.h"
#include "BSLWavRender.h"
#if !BSL_MAC && !BSL_IOS
	#include "BSLWavBuff.h"
#endif

#if BSL_MAC || BSL_IOS
/*	#include <CoreServices/CoreServices.h> */
	#include <AudioToolbox/AudioQueue.h>
	#include <AudioToolbox/AudioFile.h>
#elif BSL_LINUX
	#include <fcntl.h>
	#include <unistd.h>
	#include <sys/ioctl.h>
	#include <pthread.h>
#endif

/* defines */

#ifndef BSL_WAV_OUT_DEVICE_NAME_LENGTH
	#define BSL_WAV_OUT_DEVICE_NAME_LENGTH 256
#endif /* BSL_WAV_OUT_DEVICE_NAME_LENGTH */

#ifdef BSL_WAV_OUT_VOLUME
enum {
#if BSL_MAC
	BSL_WAV_OUT_VOLUME_MAX = 256
#elif BSL_WINDOWS
	BSL_WAV_OUT_VOLUME_MAX = 65535
#elif BSL_LINUX
	BSL_WAV_OUT_VOLUME_MAX = 255
#else
	BSL_WAV_OUT_VOLUME_MAX = 1 /* dummy */
#endif
	};
#endif /* BSL_WAV_OUT_VOLUME */

/* typedefs */

/*!
	Wave�o�͏�����{����I�u�W�F�N�g�B
	�e�v���b�g�t�H�[���p�Ɉȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLWavOutMme.c / h : Win MME
	- BSLWavOutMmeThread.c / h : Win MME�i�X���b�h�g�p�j
	- BSLWavOutDX.c / h : Win DirectSound�i�X���b�h�g�p�j
	- BSLWavOutDsp.c / h : Linux DSP
	- BSLWavOutDspThread.c / h : Linux DSP�i�X���b�h�g�p�j
	- BSLWavOutAsio.cpp / h : Win ASIO
	- BSLWavOutAudioQueue.c / h : Mac OS X / iPhone Audio Queue

	�X���b�h�g�p�I�u�W�F�N�g�ł�BSLConf.h�ɂ�BSL_WAV_OUT_INCLUDE_THREAD���`���邱�ƁB
 
	��{�I�Ȏg�p�菇�͈ȉ��̒ʂ�B�ڍׂ͕ʓr�V�[�P���X�}���Q�Ƃ��邱�Ɓi�����doc/figure.doc�ɂĒ񋟂����j�B
	-# �����_�����O�����p�R�[���o�b�N�iBSL_WAV_RENDER�j��p�ӂ���
	-# bslWavOut* ()�ɂ�萧��I�u�W�F�N�g���\�z
	-# BSLWavOut::initialize ()�ɂ��I�u�W�F�N�g��������
	-# BSLWavOut::setSampleRate ()�ɂ��T���v�����O���g����ݒ�iBSLConf.h�ɒ�`���������ݒ�l�iBSL_WAV_SAMPLE_RATE�j�ȊO�ł̐ݒ肪�K�v�ȏꍇ�̂݁j
	-# BSLWavOut::setBlockSize ()�ɂ��ő�u���b�N�T�C�Y��ݒ�iBSLConf.h�ɒ�`���������ݒ�l�iBSL_WAV_BLOCK_SIZE�j�ȊO�ł̐ݒ肪�K�v�ȏꍇ�̂݁j
	-# BSLWavOut::setBitsPerSample ()�ɂ��r�b�g���x��ݒ�iBSLConf.h�ɒ�`���������ݒ�l�iBSL_WAV_BITS_PER_SAMPLE�j�ȊO�ł̐ݒ肪�K�v�ȏꍇ�̂݁j
	-# BSLWavOut::setChannels ()�ɂ��`�����l������ݒ�iBSLConf.h�ɒ�`���������ݒ�l�iBSL_WAV_CHANNELS�j�ȊO�ł̐ݒ肪�K�v�ȏꍇ�̂݁j
	-# �ȉ��J��Ԃ�
		- BSLWavOut::open ()
		- BSLWavOut::start ()
		- BSLWavOut::isPlayDone ()�ɂ���Ē�~�����܂�Wait
		- BSLWavOut::stopped ()
		- BSLWavOut::close ()
	-# bslWavOutExit ()�ɂ�萧��I�u�W�F�N�g��j��
*/
typedef struct BSLWavOut
{
	/*! ���������s�����\�b�h�B�\�z��K���ŏ��Ɏ��s����B */
	BSLErr (*initialize) (struct BSLWavOut *obj);

	/*!
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslWavOutExit()���Ăяo�����B
	*/
	void (*finalize) (struct BSLWavOut *obj);

	/*! �h���C�o�����̎擾 */
	int (*getDrivers) (struct BSLWavOut *obj);
	/*! �h���C�o���̎擾 */
	void (*getDriverName) (struct BSLWavOut *obj, int index, LPTSTR name, int length);

	/*! �T���v�����O���g��[Hz]��ݒ肷�郁�\�b�h */
	BSLErr (*setSampleRate) (struct BSLWavOut *obj, int sampleRate);
	/*! �T���v�����O���g��[Hz]���擾���郁�\�b�h */
	int (*getSampleRate) (struct BSLWavOut *obj);

	/*! �ő�u���b�N�T�C�Y[sample]��ݒ肷�郁�\�b�h */
	BSLErr (*setBlockSize) (struct BSLWavOut *obj, long blockSize);
	/*! �ő�u���b�N�T�C�Y[sample]���擾���郁�\�b�h */
	long (*getBlockSize) (struct BSLWavOut *obj);

	/*! �r�b�g���x[bit]��ݒ肷�郁�\�b�h */
	BSLErr (*setBitsPerSample) (struct BSLWavOut *obj, int bitsPerSample);
	/*! �r�b�g���x[bit]���擾���郁�\�b�h */
	int (*getBitsPerSample) (struct BSLWavOut *obj);

	/*! �`�����l������ݒ肷�郁�\�b�h */
	BSLErr (*setChannels) (struct BSLWavOut *obj, int channels);
	/*! �`�����l�������擾���郁�\�b�h */
	int (*getChannels) (struct BSLWavOut *obj);

	/*! �o�C�g��/�t���[�����擾���郁�\�b�h */
	int (*getBytesPerFrame) (struct BSLWavOut *obj);

	BSLErr (*setBuffers) (struct BSLWavOut *obj, int bufferes);
	int (*getBuffers) (struct BSLWavOut *obj);

	/*! �f�o�C�X�̃I�[�v�����s�����\�b�h */
	BSLErr (*open) (struct BSLWavOut *obj, LPCTSTR name, void *param1, DWORD param2);
#if BSL_WINDOWS
	/*! MME�f�o�C�X�̃I�[�v�����s�����\�b�h */
	BSLErr (*openMme) (struct BSLWavOut *obj, UINT uDeviceID, DWORD_PTR dwCallback, DWORD_PTR dwCallbackInstance, DWORD fdwOpen);
#endif /* BSL_WINDOWS */
	/*! �f�o�C�X�̃N���[�Y���s�����\�b�h */
	BSLErr (*close) (struct BSLWavOut *obj);

#ifdef BSL_WAV_OUT_VOLUME
	BSLErr (*setVolume) (struct BSLWavOut *obj, DWORD value);
	DWORD (*getVolume) (struct BSLWavOut *obj);
#endif /* BSL_WAV_OUT_VOLUME */

	/*!
		�Đ��J�n���s�����\�b�h�B<br>
		BSL_WAV_OUT_PAUSE��`���ɂ����Ĉꎞ��~�̏ꍇ�ɂ͈ꎞ��~�������s���B
	*/
	BSLErr (*start) (struct BSLWavOut *obj);
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		�Đ��ꎞ��~���s�����\�b�h�B
		BSL_WAV_OUT_PAUSE��`���̂ݗL���B
	*/
	BSLErr (*pause) (struct BSLWavOut *obj);	
#endif /* BSL_WAV_OUT_PAUSE */
	/*! �Đ���~���s�����\�b�h�B�{���\�b�h���s���BSLWavOut::stopped�܂ōĐ��͏I�����Ȃ����Ƃɒ��ӁB */
	BSLErr (*stop) (struct BSLWavOut *obj);
	/*! �Đ���~�����������s�����\�b�h */
	void (*stopped) (struct BSLWavOut *obj, BSLErr error);
	/*! �f�o�C�X�̃��Z�b�g�������s�����\�b�h */
	BSLErr (*reset) (struct BSLWavOut *obj);

	/*! �f�o�C�X���I�[�v���ς����擾���郁�\�b�h */
	BOOL (*isOpened) (struct BSLWavOut *obj);
	/*! �f�o�C�X���Đ��������擾���郁�\�b�h */
	BOOL (*isPlaying) (struct BSLWavOut *obj);
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		�f�o�C�X���ꎞ��~�������擾���郁�\�b�h�B
		BSL_WAV_OUT_PAUSE��`���̂ݗL���B
	*/
	BOOL (*isPaused) (struct BSLWavOut *obj);
#endif /* BSL_WAV_OUT_PAUSE */
	/*! �Đ������������������擾���\�b�h�BBSLWavOut::stopped�̎��s�����ƂȂ�B */
	BOOL (*isPlayDone) (struct BSLWavOut *obj);

	/*! �o�̓o�b�t�@�������s�����\�b�h�BBSLWavOut::tRender������ŋ쓮����B*/
#if BSL_MAC || BSL_IOS
	BSLErr (*buffering) (struct BSLWavOut *obj, BSLWavIOData *data, long sampleFrames);
#elif BSL_WINDOWS || BSL_ANDROID
	BSLErr (*buffering) (struct BSLWavOut *obj, void *data, long sampleFrames);
#else
	BSLErr (*buffering) (struct BSLWavOut *obj);
#endif

	/*! �h���C�o�̓Ǝ��ݒ��ʂ��N�����郁�\�b�h�i����ASIO�̂݁j*/
	void (*showControlPanel) (struct BSLWavOut *obj);

	/*! ���[�U�[�f�[�^��ݒ肷�郁�\�b�h */
	void *(*getUser) (struct BSLWavOut *obj);
	/*! ���[�U�[�f�[�^���擾���郁�\�b�h */
	void (*setUser) (struct BSLWavOut *obj, void *user);

	/*! �����_�����O����I�u�W�F�N�g */ 
	BSLWavRender tRender;
#if BSL_MAC || BSL_IOS
	int nBuffers;
#else
	/*! �o�b�t�@����I�u�W�F�N�g */ 
	BSLWavBuff tBuff;
#endif /* BSL_MAC */

	/*! �f�o�C�X���ʎq */
#if BSL_WINDOWS
	HWAVEOUT hDevice;
#elif BSL_LINUX
	 int hDevice;
#endif
	/*! �T���v�����O���g��[Hz] */
	 int nSampleRate;
	/*! �r�b�g���x[bit] */
	int nBitsPerSample;
	/*! �`�����l���� */
	int nChannels;

#ifdef BSL_WAV_OUT_VOLUME
	DWORD dwVolume;
#endif /* BSL_WAV_OUT_VOLUME */

	/*! �Đ����t���O */
	BOOL bPlaying;
	/*! ��~���t���O */
	BOOL bStop;
	/*! ��~�G���[ */
	BOOL bStopError;
#ifdef BSL_WAV_OUT_PAUSE
	/*!
		�ꎞ��~�t���O�B
		BSL_WAV_OUT_PAUSE��`���̂ݗL���B
	*/
	BOOL bPausing;
	/*!
		�ꎞ��~�����t���O�B
		BSL_WAV_OUT_PAUSE��`���̂ݗL���B
	*/
	BOOL bPaused;
#endif /* BSL_WAV_OUT_PAUSE */
	/*! �o�b�t�@�C���f�b�N�X */
	int nBufferIndex;
	/*! �o�b�t�@�u���b�N�� */
	int nBlockedBuffers;

#ifdef BSL_WAV_OUT_FADEOUT
	/*! �t�F�[�h�A�E�g���������Z�b�g���郁�\�b�h�i�����g�p��p�j */
	void (*resetFadeout) (struct BSLWavOut *obj);
	/*! �t�F�[�h�A�E�g�������J�n���郁�\�b�h�i�����g�p��p�j */
	void (*startFadeout) (struct BSLWavOut *obj);
	/*! �t�F�[�h�A�E�g�������s�����\�b�h�i�����g�p��p�j */
	void (*processFadeout) (struct BSLWavOut *obj, BSLWavIOData *buffer, long sampleFrames);
	/*! �t�F�[�h�A�E�g�����������擾���郁�\�b�h�i�����g�p��p�j */
	BOOL (*isFadeoutDone) (struct BSLWavOut *obj);
	/*! �t�F�[�h�A�E�g�W�� */
#ifdef BSL_WAV_IO_FLOAT
    BSLWavIOData aFadeout;
#else /* BSL_WAV_IO_FLOAT */
    long lFadeout;
#endif /* BSL_WAV_IO_FLOAT */
	/*! �t�F�[�h�A�E�g�P�� */
#ifdef BSL_WAV_IO_FLOAT
	BSLWavIOData aFadeUnit;
#else /* BSL_WAV_IO_FLOAT */
    long lFadeUnit;
#endif /* BSL_WAV_IO_FLOAT */

	#ifdef BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP
	/*! �t�F�[�h�A�E�g�㖳���Đ����[sample] */
	long lFadeOutZeroSampleDef;
	/*! �t�F�[�h�A�E�g�㖳���Đ���ԗp�J�E���^[sample] */
	long lFadeOutZeroSample;
	#endif /* BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP */
#endif /* BSL_WAV_OUT_FADEOUT */

#ifdef BSL_WAV_OUT_INCLUDE_THREAD
	/* <- only for thread */
	/*!
		�I�u�W�F�N�g�j�����ɕK�v�ƂȂ�X���b�h�I���������s���B
		�X���b�h�Ή��̐���I�u�W�F�N�g�̂ݗL���B
	*/
	BSLErr (*threadexit) (struct BSLWavOut *obj);
	/*!
		�Đ��X���b�h�̒�~�������J�n������B
		�X���b�h�Ή��̐���I�u�W�F�N�g�̂ݗL���B
	*/
	BSLErr (*threadstop) (struct BSLWavOut *obj);

	#if BSL_WINDOWS
	HANDLE pThread;
	#elif BSL_LINUX
	pthread_t pThread;
	BOOL bThread;
	#endif /* BSL_WINDOWS */

	/*! �X���b�h�I���҂��t���O */
	BOOL bThreadExit;
	/*! �X���b�h�փX���b�h��~�i�܂��͂��̂��߂̃t�F�[�h�J�n�j��`���邽�߂̃t���O */
	BOOL bThreadStop;
	/*! �X���b�h�̏I���҂��t���O ? */
	BOOL bThreadWaitStopped;

	#ifdef BSL_WAV_DEBUG
	BOOL bThreadTestRestart;
	#endif /* BSL_WAV_DEBUG */
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

	/*!
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g��BSLWavEnc::initialize()���ɂčs�����ƁB
		�������J����bslWavOutExit()�ɂčs����B
	*/
	void *pInternalData; /* used for each upper object */
} BSLWavOut;

/* globals */

/* loclas */

/* function declarations */

void bslWavOut (BSLWavOut *obj, BSL_WAV_RENDER render, void *user);
void bslWavOutExit (BSLWavOut *obj);

BSLErr _bslWavOutInitialize (BSLWavOut *obj);
BSLErr _bslWavOutSetSampleRate (BSLWavOut *obj, int sampleRate);
BSLErr _bslWavOutSetBlockSize (BSLWavOut *obj, long blockSize);
BSLErr _bslWavOutSetBitsPerSample (BSLWavOut *obj, int bitsPerSample);
BSLErr _bslWavOutSetChannels (BSLWavOut *obj, int channels);
BSLErr _bslWavOutSetBuffers (BSLWavOut *obj, int buffers);

#ifdef BSL_WAV_OUT_PAUSE
BSLErr _bslWavOutPause (BSLWavOut *obj);
#endif /* BSL_WAV_OUT_PAUSE */
BSLErr _bslWavOutStop (BSLWavOut *obj);
void _bslWavOutStopped (BSLWavOut *obj, BSLErr error);
BSLErr _bslWavOutReset (BSLWavOut *obj);

#ifdef BSL_WAV_OUT_INCLUDE_THREAD

/*
	stop() = _bslWavOutStop_thread()
		bThreadStop = TRUE
		�c
		threadstop ()
			bThreadStop = FALSE
			bThreadWaitStopped = TRUE
			_bslWavOutStop ()
				startFadeOut()
				�c
				bStop = TRUE
				�c
				if (isPlayDone ()) {
					stopped ()
				}
*/

BSLErr _bslWavOutStop_thread (BSLWavOut *obj);
BSLErr _bslWavOutThreadStop (BSLWavOut *obj);
#endif /* BSL_WAV_OUT_INCLUDE_THREAD */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavOuth */
