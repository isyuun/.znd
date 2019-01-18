/*
 *  BSLWavWriterAiff.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriterAiff.h
	@brief
	Wave File出力制御オブジェクト (AIFF)
*/

#ifndef __INCBSLWavWriterAiffh
#define __INCBSLWavWriterAiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_AIFF
#include "BSLWav.h"
#include "BSLWavWriter.h"
#include "BSLWavParserAiff.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* function declarations */

void bslWavWriterAiff (BSLWavWriter *obj);

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_AIFF */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavWriterAiffh */
