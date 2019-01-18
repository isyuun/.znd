/* mMidDef.h - */

/* Copyright 1996-2xxx hk */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmMidDefh
#define __INCmMidDefh

/* includes */

#include "mMid.h"

/* defines */

enum {
//	kMidModules = 1, // modules
//	kMidParts = PARTS, // parts/module
//	kMidGsDrumMaps = 2, // gs drum maps/module
	kMidDataSize = 255, // maximum data buffer size
//	kMidPorts = 1, // number of midi ports
//	kMidChannels = 16, // number of midi channels
  kMidSmfTracks = 256
};

/* typedefs */

struct MMidEvent // midi event
{
	DWORD dwTick; // tick to be send
//	BYTE byOptSw; // option switch
	BYTE byPort;
	BYTE byStatus;
	int iSize;
	BYTE *pData;
	int iOrgTrk;
//	int iNum;
//	struct MMidEvent *pNext;
};

/* globals */

/* locals */

/* function declarations */

#endif /* __INCmMidDefh */
