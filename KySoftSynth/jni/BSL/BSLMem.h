/*
 *  BSLMem.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMem.h
	@brief
	ÉÅÉÇÉää«óù
*/

#ifndef __INCBSLMemh
#define __INCBSLMemh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

#include <stdlib.h>

/* defines */

/* typedefs */

/* globals */

/* locals */

/* function declarations */

void *bslMemAlloc (size_t size);
void bslMemFree (void *memblock);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMemh */
