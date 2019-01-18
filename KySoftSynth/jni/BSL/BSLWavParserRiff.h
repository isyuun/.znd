/*
 *  BSLWavParserRiff.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavParserRiff.h
	@brief
	Wave File入力制御オブジェクト (RIFF)
*/

#ifndef __INCBSLWavParserRiffh
#define __INCBSLWavParserRiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_RIFF
#include "BSLWav.h"
#include "BSLWavParser.h"
#include "BSLFileRiff.h"

/* defines */

#define WAVE_MARKER (BSL_FILE_MARKER ('W', 'A', 'V', 'E')) 
#ifndef fmt__MARKER
	#define fmt__MARKER (BSL_FILE_MARKER ('f', 'm', 't', ' ')) 
#endif
#ifndef data_MARKER
	#define data_MARKER (BSL_FILE_MARKER ('d', 'a', 't', 'a')) 
#endif
#define smpl_MARKER (BSL_FILE_MARKER ('s', 'm', 'p', 'l')) 

enum {
	BSL_WAV_FILE_RIFF_FORMAT_UNKNOWN = 0x0000,
	BSL_WAV_FILE_RIFF_FORMAT_PCM = 0x0001,
	BSL_WAV_FILE_RIFF_FORMAT_MSADPCM = 0x0002,
	BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ALAW = 0x0006,
	BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ULAW = 0x0007,
	BSL_WAV_FILE_RIFF_FORMAT_IMA_ADPCM = 0x0011,
	BSL_WAV_FILE_RIFF_FORMAT_ITUG723_ADPCM = 0x0016,
	BSL_WAV_FILE_RIFF_FORMAT_GSM610 = 0x0031,
	BSL_WAV_FILE_RIFF_FORMAT_ITUG721Adpcm = 0x0040,
	BSL_WAV_FILE_RIFF_FORMAT_MPEG = 0x0050,
	BSL_WAV_FILE_RIFF_FORMAT_EXPRRIMENTAL = 0xFFFF
};

/* typedefs */

typedef struct {
	DWORD dwCuePointId;
	DWORD dwType;
	DWORD dwStart;
	DWORD dwEnd;
	DWORD dwFraction;
	DWORD dwPlayCount; 
} BSL_WAV_RIFF_SMPL_LOOP; /* 24[byte] */

typedef struct {
	DWORD dwManufacturer;
	DWORD dwProduct;
	DWORD dwSamplePeriod;
	DWORD dwMidiUnityNote;
	DWORD dwMidiPitchFraction;
	DWORD dwSmpteFormat;
	DWORD dwSmpteOffset;
	DWORD dwNumSampleLoops;
	DWORD dwSamplerData;
} BSL_WAV_RIFF_SMPL; /* 36[byte] */

/* globals */

/* locals */

/* function declarations */

void bslWavParserRiff (BSLWavParser *obj);

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_RIFF */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavParserRiffh */
