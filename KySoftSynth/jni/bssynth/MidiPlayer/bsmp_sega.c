/*
 *  bsmp_sega.c
 *  bssynth
 *
 *  Created by hideo on 10/12/03.
 *  Copyright 2010 bismark. All rights reserved.
 *
 */

#include "bsmp_sega.h"


#pragma mark -

typedef struct
{
	DWORD dwTempo;
	DWORD dwTick;
	DWORD dwPlaytickTP;
	float fPlayTime;
	float fPlayTimeTP;
	float fTickTime;
} BSMP_SEGA_LOCAL;


static BSL_MID_EVENT_PROC _getEvent
	(
	BSLMidParser *obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	BSMP_SEGA_LOCAL *info = (BSMP_SEGA_LOCAL *) user;

	info->dwTick = event->dwTick;
	info->fPlayTime = info->fPlayTimeTP + (info->fTickTime * (info->dwTick - info->dwPlaytickTP));

	switch (event->byStatus) {
	case BSL_MID_META_TYPE_SET_TEMPO:
		{
			BYTE *data = event->pData;
			DWORD tempo;
			tempo = 0x10000UL * (DWORD) data[0];
			tempo += 0x100UL * (DWORD) data[1];
			tempo += (DWORD) data[2];

			info->fPlayTimeTP += (info->fTickTime * (event->dwTick - info->dwPlaytickTP));
			info->dwPlaytickTP = event->dwTick;
			info->dwTempo = tempo;
			info->fTickTime = (float) info->dwTempo / ((float) obj->getDivision (obj) * 1000000.f); /* [s] */
		}
		break;
	default:
		if (0x80 <= event->byStatus && event->byStatus < 0xA0) {
			info->fPlayTimeTP += (info->fTickTime * (event->dwTick - info->dwPlaytickTP));
			info->dwPlaytickTP = event->dwTick;
			info->fTickTime = (float) info->dwTempo / ((float) obj->getDivision (obj) * 1000000.f); /* [s] */
			return BSL_MID_EVENT_PROC_FINISHED;
		}
		break;
	}

	return BSL_MID_EVENT_PROC_OK;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	MIDIファイルの情報を取得する
	@param obj
	制御オブジェクトのアドレス
	@param totaltick
	総Tick数取得バッファ
	@param totaltime
	総演奏時間[s]取得バッファ
*/
float bsmpSegaGetAbsoluteTimeForFirstNoteEvent
	(
	BSLMidParser *obj
	)
{
	BSMP_SEGA_LOCAL info;
	BSL_MID_EVENT_PROC (*ce) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->chaseEvent;
	BSL_MID_EVENT_PROC (*ge) (struct BSLMidParser *obj, BSLMidEvent *event, void *user) = obj->getEvent;

	memset (&info, 0, sizeof (BSMP_SEGA_LOCAL));
	info.dwTempo = 500000UL;
	info.dwTick = 0UL;
	info.dwPlaytickTP = 0UL;
	info.fPlayTime = 0.f;
	info.fPlayTimeTP = 0.f;
	info.fTickTime = (float) info.dwTempo / ((float) obj->getDivision (obj) * 1000000.f); /* [s] */

	obj->chaseEvent = NULL;
	obj->getEvent = _getEvent;

	obj->rewind (obj, 0UL);

	if (obj->sortEvent (obj, 0UL, (void *) &info) != BSL_MID_EVENT_PROC_FINISHED) {
	}

	obj->chaseEvent = ce;
	obj->getEvent = ge;

	return info.fPlayTime;
}
