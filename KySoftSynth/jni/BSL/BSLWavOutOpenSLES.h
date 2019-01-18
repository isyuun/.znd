/*
 *  BSLWavOutOpenSLES.h
 *
 *  Copyright (c) 2012 bismark. All rights reserved.
 *
 */

/*!
 @file BSLWavOutOpenSLES.h
 @brief
 Wave出力制御オブジェクト (AudioQueue)
 */

#ifndef __INCBSLWavOutOpenSLESh
#define __INCBSLWavOutOpenSLESh

#ifdef __cplusplus
extern "C" {
#endif
	
/* includes */
	
#include "BSL.h"
	
#if BSL_ANDROID

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
	
#include "BSLWav.h"
#include "BSLWavOut.h"
	
/* defines */
	
/* typedefs */

typedef struct BSLWavOutOpenSLES_Internal {
	// engine interfaces
	SLObjectItf engineObject;
	SLEngineItf engineEngine;

	// output mix interfaces
	SLObjectItf outputMixObject;

	// buffer queue player interfaces
	SLObjectItf bqPlayerObject;
	SLPlayItf bqPlayerPlay;
	SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
} BSLWavOutOpenSLES_Internal;
	
/* globals */
	
/* loclas */
	
/* function declarations */
	
void bslWavOutOpenSLES (BSLWavOut *obj, BSL_WAV_RENDER render, void *user);
void bslWavOutOpenSLESStopped (BSLWavOut *obj, BSLErr error);
	
#endif /* BSL_ANDROID */
	
#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavOutOpenSLESh */
