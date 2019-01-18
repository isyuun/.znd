/*
 *  portSelection.h
 *  bssynth
 *
 *  Created by hideo on 11/09/10.
 *  Copyright 2011 bismark. All rights reserved.
 *
 */

#include "BSLMidParser.h"

#ifndef __INCportSelectionh
#define __INCportSelectionh

typedef struct
{
	int nPortSelectionMethod;
	BOOL bNoTranspose[BSSYNTH_MID_PORTS][BSL_MID_CHANNELS];
	BYTE byUgaTrackType[BSSYNTH_MID_PORTS][BSL_MID_CHANNELS];
} BSSYNTH_PORT_SELECTOR;

BSL_MID_PORT portSelectionGetTrackPortUsingMetaEventPort (BSLMidParserTrack *obj, int index);
BSL_MID_PORT portSelectionGetTrackPortDK (BSLMidParserTrack *obj, int index);
BSL_MID_PORT portSelectionGetTrackPortUga (BSSYNTH_PORT_SELECTOR *selector, BSLMidParserTrack *obj, int index, int *guideMainCh, int *guideSubCh);
BSL_MID_PORT portSelectionGetTrackPortSongoku (BSSYNTH_PORT_SELECTOR *selector, BSLMidParserTrack *obj, int index);
BSL_MID_PORT portSelectionGetTrackPortSega (BSSYNTH_PORT_SELECTOR *selector, BSLMidParserTrack *obj, int index);

#endif /* __INCportSelectionh */