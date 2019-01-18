/*
 *  BSLWavWriterRiff.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriterRiff.h
	@brief
	Wave File出力制御オブジェクト (RIFF)
*/

#ifndef __INCBSLWavWriterRiffh
#define __INCBSLWavWriterRiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_RIFF
#include "BSLWav.h"
#include "BSLWavWriter.h"
#include "BSLWavParserRiff.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* function declarations */

void bslWavWriterRiff (BSLWavWriter *obj);

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_RIFF */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavWriterRiffh */
