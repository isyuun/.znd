/*
 *  BSLMidParserSmf.h
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserSmf.h
	@brief
	MIDI File入力制御オブジェクト(SMF)
*/

#ifndef __INCBSLMidParaserSmfh
#define __INCBSLMidParaserSmfh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLMid.h"
#include "BSLMidParser.h"
#include "BSLMidParserTrackSmf.h"

/* defines */

#define MThd_MARKER (BSL_FILE_MARKER ('M', 'T', 'h', 'd'))

/* typedefs */

/* globals */

/* locals */

/* function declarations */

void bslMidParserSmf (BSLMidParser *obj);

BSLErr _bslMidParserSmfParse (BSLMidParser *obj, BSLFile *file);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMidParaserSmfh */
