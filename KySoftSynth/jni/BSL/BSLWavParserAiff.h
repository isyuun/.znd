/*
 *  BSLWavParserAiff.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavParserAiff.h
	@brief
	Wave File入力制御オブジェクト (AIFF)
*/

#ifndef __INCBSLWavParserAiffh
#define __INCBSLWavParserAiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_AIFF
#include "BSLWav.h"
#include "BSLWavParser.h"

/* defines */

#define FORM_MARKER (BSL_FILE_MARKER ('F', 'O', 'R', 'M')) 
#define AIFF_MARKER (BSL_FILE_MARKER ('A', 'I', 'F', 'F')) 
#define AIFC_MARKER (BSL_FILE_MARKER ('A', 'I', 'F', 'C')) 
#define COMM_MARKER (BSL_FILE_MARKER ('C', 'O', 'M', 'M')) 
#define SSND_MARKER (BSL_FILE_MARKER ('S', 'S', 'N', 'D')) 
#define MARK_MARKER (BSL_FILE_MARKER ('M', 'A', 'R', 'K')) 
#define INST_MARKER (BSL_FILE_MARKER ('I', 'N', 'S', 'T')) 
#define APPL_MARKER (BSL_FILE_MARKER ('A', 'P', 'P', 'L')) 

#define c____MARKER (BSL_FILE_MARKER ('(', 'c', ')', ' ')) 
#define NAME_MARKER (BSL_FILE_MARKER ('N', 'A', 'M', 'E')) 
#define AUTH_MARKER (BSL_FILE_MARKER ('A', 'U', 'T', 'H')) 
#define ANNO_MARKER (BSL_FILE_MARKER ('A', 'N', 'N', 'O')) 
#define COMT_MARKER (BSL_FILE_MARKER ('C', 'O', 'M', 'T')) 
#define FVER_MARKER (BSL_FILE_MARKER ('F', 'V', 'E', 'R')) 
#define SFX__MARKER (BSL_FILE_MARKER ('S', 'F', 'X', '!')) 

#define PEAK_MARKER (BSL_FILE_MARKER ('P', 'E', 'A', 'K')) 

/* Supported AIFC encodings.*/
#define NONE_MARKER (BSL_FILE_MARKER ('N', 'O', 'N', 'E')) 
#define sowt_MARKER (BSL_FILE_MARKER ('s', 'o', 'w', 't')) 
#define twos_MARKER (BSL_FILE_MARKER ('t', 'w', 'o', 's')) 
#define fl32_MARKER (BSL_FILE_MARKER ('f', 'l', '3', '2')) 
#define FL32_MARKER (BSL_FILE_MARKER ('F', 'L', '3', '2')) 

/* Unsupported AIFC encodings.*/
#define fl64_MARKER (BSL_FILE_MARKER ('f', 'l', '6', '4')) 
#define FL64_MARKER (BSL_FILE_MARKER ('F', 'L', '6', '4')) 
#define MAC3_MARKER (BSL_FILE_MARKER ('M', 'A', 'C', '3')) 
#define MAC6_MARKER (BSL_FILE_MARKER ('M', 'A', 'C', '6')) 
#define ima4_MARKER (BSL_FILE_MARKER ('i', 'm', 'a', '4')) 
#define ulaw_MARKER (BSL_FILE_MARKER ('a', 'l', 'a', 'w')) 
#define alaw_MARKER (BSL_FILE_MARKER ('u', 'l', 'a', 'w')) 
#define ADP4_MARKER (BSL_FILE_MARKER ('A', 'D', 'P', '4')) 

enum {
	BSL_WAV_AIFF_NO_LOOPING = 0,
	BSL_WAV_AIFF_FORWARD_LOOPING,
	BSL_WAV_AIFF_FORWARD_BACKWORD_LOOPING
};

/* typedefs */

/* globals */

/* locals */

/* function declarations */

void bslWavParserAiff (BSLWavParser *obj);

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_AIFF */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavParserAiffh */
