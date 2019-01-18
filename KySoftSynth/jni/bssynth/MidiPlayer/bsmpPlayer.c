/*
 *  bsmpPlayer.c
 *
 *  Copyright (c) 2002 - 2016 bismark. All rights reserved.
 *
 */

/*!
	@file bsmpPlayer.c
	@brief
	BSMPPlayer制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLMath.h"
#include "BSLFileMem.h"
#include "BSLFileSave.h"
#include "BSLFileSaveDummy.h"
#include "BSLMem.h"
#include "BSLMidParserAuto.h"
#if BSL_WINDOWS
	#include "BSLWavOutMmeThread.h"
#elif BSL_MAC || BSL_IOS
	#include "BSLWavOutAudioQueue.h"
#elif BSL_SIGMA
	#include "BSLWavOutSigmaSmp8670Thread.h"
#elif BSL_LINUX
	#include "BSLWavOutAlsaThread.h"
	#include "BSLWavOutOssThread.h"
#elif BSL_ANDROID
	#include "BSLWavOutOpenSLES.h"
#else
	#error
#endif
#ifdef BSMP_EXPORT_WAVE
#include "BSLWavWriter.h"
#include "BSLWavWriterRiff.h"
#include "BSLWavWriterAiff.h"
#endif /* BSMP_EXPORT_WAVE */
#include "bsmpPlayer.h"

#if (defined BSSYNTH_88)
#include "bsmp_sega.h"
#endif
#ifdef BSSYNTH_KY
#include "bsmp_ky.h"
#endif /* BSSYNTH_KY */


#if (1 < BSSYNTH_MID_PORTS)
#include "portSelection.h"
#endif

#include "mid88.h"

/* defines */

#if BSL_WINDOWS
	#define DRIVER_NAME_MME _TEXT("Windows MME")
/*	#define _ASIO_ */
	#ifdef _ASIO_
		#include "BSLWavOutAsio.h"
		#define DRIVER_NAME_ASIO _TEXT("ASIO")
	#endif /* _ASIO_ */
#elif BSL_MAC || BSL_IOS
	#define DRIVER_NAME_AUDIO_QUEUE _TEXT("AudioQueue")
#elif BSL_SIGMA
#define DRIVER_NAME_SIGMA_SMP8670 _TEXT("SMP8670")
#elif BSL_LINUX
	#define DRIVER_NAME_ALSA _TEXT("ALSA")
	#define DRIVER_NAME_OSS _TEXT("OSS")
#elif BSL_ANDROID
	#define DRIVER_NAME_OPENSLES _TEXT("OpenSLES")
#endif

enum {
	DRIVER_DUMMY = -1,
#if BSL_WINDOWS
	DRIVER_MME,
	#ifdef _ASIO_
	DRIVER_ASIO,
	#endif /* _ASIO_ */
#elif BSL_MAC || BSL_IOS
	DRIVER_AUDIO_QUEUE,
#elif BSL_SIGMA
	DRIVER_SMP8670,
#elif BSL_LINUX
	DRIVER_ALSA,
	DRIVER_OSS,
#elif BSL_ANDROID
	DRIVER_OPENSLES,
#endif
	NUM_DRIVERS,
};

/* typedefs */

/* globals */

/* locals */

/* forward declarations */

static void _setPlayerParameters (BSMPPlayer *obj, DWORD tick);
static void _initCallback (BSMPPlayer *obj);

static void _setTempo (BSMPPlayer *obj, DWORD tempo
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
		, BOOL immediate,
		DWORD tick
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
	);

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
static BOOL _KY_isSubstitutedForRhythmChange (BYTE port, BYTE channel);
#endif


/*---------------------------------------------------------------------------*/
static BYTE __calcGuide
	(
	int guide, 
	BYTE value
	)
{
	switch (guide) {
	case 2:
		value = 127;
		break;
	case 1:
		{
			value = (BYTE) sqrtf_((float) (value * value) * 2.f);
			if (value > 127) {
				value = 127;
			}
/*
			int tmp = (int) value + ((127 - (int) value) * 4 / 10);
			value = (BYTE) tmp;*/
		}
		break;
	case -1:
		{
			value = (BYTE) sqrtf_((float) (value * value) * 0.5f);

/*			int tmp = (int) value - ((127 - (int) value) * 4 / 10);
			if (tmp < 0) {
				value = 0;
			}
			else {
				value = (BYTE) tmp;
			}*/
		}
		break;
	case -2:
		value = 0;
		break;
	default:
	case 0:
		break;
	}

	return value;
}

/*---------------------------------------------------------------------------*/
static BYTE __volResetCallback
	(
	BYTE module, 
	BYTE part, 
	BYTE value, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	if (!player) return value;

	if (player->iGuideMainCh == (int) ((module << 4) + part)) {
		int guide = player->iGuide;
		int lastguide = player->iLastGuide;

		if (guide != lastguide) {
			/* from GUI */
		}
		else {
			/* from SMF */
			player->setGuideMainValue (player, value);
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp: cc#7 = %u [main]\n"), value);
#endif /* BSL_DEBUG */
		}

		/* convert */
		{
#ifdef BSL_DEBUG
			BYTE org = value;
#endif /* BSL_DEBUG */
			value = __calcGuide (guide, value);
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp: cc#7 %u -> %u [main]\n"), org, value);
#endif /* BSL_DEBUG */
		}
	}
	else if (player->iGuideSubCh == (int) ((module << 4) + part)) {
		int guide = player->iGuide;
		int lastguide = player->iLastGuide;

		if (guide != lastguide) {
			/* from GUI */
		}
		else {
			/* from SMF */
			player->setGuideSubValue (player, value);
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp: cc#7 = %u [sub]\n"), value);
#endif /* BSL_DEBUG */
		}

		/* convert */
		{
#ifdef BSL_DEBUG
			BYTE org = value;
#endif /* BSL_DEBUG */
			value = __calcGuide (guide, value);
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp: cc#7 %u -> %u [sub]\n"), org, value);
#endif /* BSL_DEBUG */
		}
	}

	return value;
}

/*---------------------------------------------------------------------------*/
#if (defined BSSYNTH_88)
static void __setNoTranspose
	(
	BSMPPlayer *obj,
	int key
	)
{
	int port;
	int ch;

	for (port = 0; port < BSSYNTH_MID_PORTS; port++) {
		for (ch = 0; ch < BSL_MID_CHANNELS; ch++) {
			if (obj->portSelector.bNoTranspose[port][ch]) {
				BYTE value;
				BYTE sysex[] = {0x41, 0x10, 0x42, 0x12, 0x40, 0x10, 0x16, 0x00, 0x00, 0xF7};
				if (key >= 0) {
					value = (0x40 + (BYTE) key) & 0x7F;
				}
				else {
					value = (0x40 - (BYTE) -key) & 0x7F;
				}

				if (ch < 9) {
					sysex[5] = 0x10 + ch + 1;
				}
				else if (ch == 9) {
					sysex[5] = 0x10;
				}
				else {
					sysex[5] = 0x10 + ch;
				}
				sysex[7] = value;
				sysex[8] = (128 - ((int) sysex[4] + (int) sysex[5] + (int) sysex[6] + (int) sysex[7]) % 128);

				obj->bsseFunc->setSystemExclusiveMessage (obj->bsseHandle, port, 0xF0, sysex, 10);
			}
		}
	}
}
#endif

static BYTE __masterKeyResetCallback
	(
	BYTE module, 
	BYTE part, 
	BYTE value, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	if (!user) return value;

	{
		int key = player->iKey;
		int lastkey = player->iLastKey;

		if (key != lastkey) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp : master key reset callback (from API) %u\n"), value);
#endif /* BSL_DEBUG */

#if (defined BSSYNTH_88)
		__setNoTranspose (player, -1 * key);
#endif
			return value;
		}

		if (key >= 0) {
			value = (value + (BYTE) key) & 0x7F;
		}
		else {
			value = (value - (BYTE) -key) & 0x7F;
		}
#ifdef BSL_DEBUG
		bslTrace (_TEXT("bsmp : master key reset callback (from MIDI) %u\n"), value);
#endif /* BSL_DEBUG */

#if (defined BSSYNTH_88)
		__setNoTranspose (player, -1 * key);
#endif

		return value;
	}
}

/*---------------------------------------------------------------------------*/
static short __masterTuneResetCallback
	(
	BYTE module, 
	BYTE part, 
	short value, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	if (!user) return value;

	{
		int tune = player->nTune;
		int lasttune = player->nLastTune;

		if (tune != lasttune) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp : master tune reset callback (from API) %u\n"), value);
#endif /* BSL_DEBUG */
			return value;
		}

		value = value + tune;
#ifdef BSL_DEBUG
		bslTrace (_TEXT("bsmp : master tune reset callback (from MIDI) %u\n"), value);
#endif /* BSL_DEBUG */
		return value;
	}
}


/*---------------------------------------------------------------------------*/
#ifndef BSL_WAV_OUT_VOLUME
static BYTE __masterVolResetCallback
	(
	BYTE module, 
	BYTE part, 
	BYTE value, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	if (!user) return value;

	{
		int vol = player->iVolume;
		int lastvol = player->iLastVolume;

		if (vol != lastvol) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp : master vol reset callback (from GUI) %u\n"), value);
#endif /* BSL_DEBUG */
			return value;
		}

		player->byOrgVolume = value;
#ifdef BSSYNTH_KT
		value = (BYTE) ((int) value * vol / (BSMP_KT_VOLUME_MAX));
#else /* BSSYNTH_KT */
		value = (BYTE) ((int) value * vol / BSMP_VOLUME_MAX);
#endif /* BSSYNTH_KT */
#ifdef BSL_DEBUG
			bslTrace (_TEXT("bsmp : master vol reset callback (from MIDI) %u\n"), value);
#endif /* BSL_DEBUG */
		return value;
	}
}
#endif /* BSL_WAV_OUT_VOLUME */

#pragma mark -
#pragma mark BSLWavOut callbacks

/*---------------------------------------------------------------------------*/
static void __stopped
	(
	BSLWavOut *obj, 
	BSLErr error
	)
{
	BSMPPlayer *player = (BSMPPlayer *) obj->getUser (obj);
	player->stopped (player, error);
}

/*---------------------------------------------------------------------------*/
static BSL_WAV_RENDER_STATE __render
	(
	BSLWavRender *obj, 
	BSLWavIOData *data, 
	long sampleFrames
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	, long *readFrames
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	)
{
	BSMPPlayer *player = (BSMPPlayer *) obj->getUser (obj);
	return player->output (player, data, sampleFrames
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
		, readFrames
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
		);
}

#pragma mark -
#pragma mark BSLMidParser callbacks

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC __chaseEvent
	(
	BSLMidParser *obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	return player->chaseEvent (player, event);
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC __getEvent
	(
	BSLMidParser *obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	return player->getEvent (player, event);
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC __getEventTempoList
	(
	BSLMidParser *_obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	BSMPPlayer *obj = (BSMPPlayer *) user;

	BYTE status = event->byStatus;

	switch (status) {
	case BSL_MID_META_TYPE_SET_TEMPO:
		{
            BYTE *data = event->pData;
            tempoList_add(&obj->pTempoList, ((DWORD) data[0] << 16) + ((DWORD) data[1] <<  8) + (DWORD) data[2], event->dwTick);
		}
		break;
	}

	return BSL_MID_EVENT_PROC_OK;
}

#ifdef BSSYNTH_WTBL_ON_DEMAND
/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC __getEventOnDemand
	(
	BSLMidParser *obj, 
	BSLMidEvent *event, 
	void *user
	)
{
	BSMPPlayer *player = (BSMPPlayer *) user;
	return player->getEventOnDemand (player, event);
}
#endif /* BSSYNTH_WTBL_ON_DEMAND */

#pragma mark -
/*---------------------------------------------------------------------------*/
#if (1 < BSSYNTH_MID_PORTS)

static void _setTrack
	(
	BSMPPlayer *obj
	)
{
	/* SMFトラック内容解析 */
	BSLMidParser *smf = &obj->m_midParser;
	BSL_MID_PORT port;
	int ch;
	int i;

#if (defined BSSYNTH_88)
	{
		int port_;
		for (port_ = 0; port_ < (int) BSSYNTH_MID_PORTS; port_++) {
			for (ch = 0; ch < BSL_MID_CHANNELS; ch++) {
				obj->portSelector.bNoTranspose[port_][ch] = FALSE;
			}
		}
	}
#endif

	if (smf) {
		switch (obj->portSelector.nPortSelectionMethod) {
		case BSMP_PORT_SELECTION_METHOD_V:
			/* SONGOKU / Victor JVC */
			for (i = 0; i < BSL_MID_TRACKS; i++) {
				port = BSL_MID_PORT_A;
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						port = portSelectionGetTrackPortSongoku (&obj->portSelector, track, i);
						track->setPort (track, port);
	#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
	#endif /* BSL_DEBUG */
					}
				}
			}
			break;
		case BSMP_PORT_SELECTION_METHOD_U:
			/* UGA / XING */
			obj->iGuideMainCh = -1;
			obj->iGuideSubCh = -1;

	#if (defined BSSYNTH_88)
			{
				int port_;
				for (port_ = 0; port_ < (int) BSSYNTH_MID_PORTS; port_++) {
					for (ch = 0; ch < BSL_MID_CHANNELS; ch++) {
						obj->portSelector.byUgaTrackType[port_][ch] = 0x50;
					}
				}
			}
	#endif

			for (i = 0; i < BSL_MID_TRACKS; i++) {
				port = BSL_MID_PORT_A;
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						port = portSelectionGetTrackPortUga (&obj->portSelector, track, i, &obj->iGuideMainCh, &obj->iGuideSubCh);
						track->setPort (track, port);
	#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
	#endif /* BSL_DEBUG */
					}
				}
			}
			break;
		case BSMP_PORT_SELECTION_METHOD_S:
			/* SEGAKARA / SEGA */
			obj->iGuideMainCh = 0; /* A01 */
			obj->iGuideSubCh = 30; /* B15*/

			for (i = 0; i < BSL_MID_TRACKS; i++) {
				port = BSL_MID_PORT_A;
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						port = portSelectionGetTrackPortSega (&obj->portSelector, track, i);
						track->setPort (track, port);
	#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
	#endif /* BSL_DEBUG */
					}
				}
			}
			break;
		case BSMP_PORT_SELECTION_METHOD_D:
			/* DAM / DK */
			for (i = 0; i < BSL_MID_TRACKS; i++) {
				port = BSL_MID_PORT_A;
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						port = portSelectionGetTrackPortDK (track, i);
						track->setPort (track, port);
	#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
	#endif /* BSL_DEBUG */
					}
				}
			}
			break;
		case BSMP_PORT_SELECTION_METHOD_K:
			/* KOREA+SintoSG */
			for (i = 0; i < BSL_MID_TRACKS; i++) {
				port = BSL_MID_PORT_A;
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						port = portSelectionGetTrackPortUsingMetaEventPort (track, i);
						track->setPort (track, port);
	#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
	#endif /* BSL_DEBUG */
					}
				}
			}
			break;
		case BSMP_PORT_SELECTION_METHOD_N:
		default:
			port = BSL_MID_PORT_A;
			ch = 0;

			for (i = 0; i < BSL_MID_TRACKS; i++) {
				if (i < smf->getTracks (smf)) {
					BSLMidParserTrack *track = smf->getTrack (smf, i);
					if (track) {
						int c = bslMidParserTrackGetUsedChannel (track);
						if (c < 0) {
							/* 検出エラー */
							port = BSL_MID_PORT_A;
						}
						else {
							if (ch <= c) {
							}
							else {
								if (port < (BSSYNTH_MID_PORTS - 1)) {
									port = (BSL_MID_PORT) ((int) port + 1);
								}
							}
							ch = c;
						}
						track->setPort (track, port);
		#ifdef BSL_DEBUG
						if (track->getName (track)) {
							bslTrace (_TEXT("bsmp: trk%02d - %s -> port#%d\n"), i, track->getName (track), port);
						}
						else {
							bslTrace (_TEXT("bsmp: trk%02d - <untitled> -> port#%d\n"), i, port);
						}
		#endif /* BSL_DEBUG */
					}
				}
				else {
					port = BSL_MID_PORT_A;
				}
			}
			break;
		}
	}
}
#endif

static void _callback_dummy (BSMP_HANDLE handle, BSMP_CALLBACK_TYPE type, void *data, void *user)
{
}


#pragma mark -
/*---------------------------------------------------------------------------*/
static BSMP_ERR _initialize
	(
	BSMPPlayer *obj,
	const unsigned char *key,
	void *target
	)
{
	BSMP_ERR err = BSMP_OK;
	int state = FALSE;

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	int q;
	obj->iCallbackQueueGet = 0;
	obj->iCallbackQueuePut = 0;  
	for (q = 0; q < kCallbackQueues; q++) {
		obj->tCallbackQueue[q].nType = BSMP_CALLBACK_TYPE_NULL; /* clear */
		obj->tCallbackQueue[q].dwData = 0UL;
		obj->tCallbackQueue[q].dwSample = 0xFFFFFFFFUL;
	}
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

	/* initialize synthesizer */
	if (err == BSMP_OK) {
		obj->bsseFunc = bsseLoad ();
		if (!obj->bsseFunc) {
			err = BSMP_ERR_MODULE;
		}
	}

	if (err == BSMP_OK) {
		err = (BSMP_ERR) obj->bsseFunc->initialize (&obj->bsseHandle, key, target);
	}

	if (err == BSMP_OK) {
		BSSE_ERR _err_;
		if (_tcslen (obj->cDefaultLibraryPath) > 0) {
			_err_ = obj->bsseFunc->loadLibrary (obj->bsseHandle, 0, obj->cDefaultLibraryPath);
			if (_err_ != BSSE_OK) {
				err = (BSMP_ERR) _err_;
			}
		}
		else if (obj->pDefaultLibraryAddress) {
			_err_ = obj->bsseFunc->loadLibraryMemory (obj->bsseHandle, 0, obj->pDefaultLibraryAddress, obj->ulDefaultLibrarySize);
			if (_err_ != BSSE_OK) {
				err = (BSMP_ERR) _err_;
			}
		}
#if !BSL_IOS /* does not load the default library */
		else {
			_err_ = obj->bsseFunc->loadLibrary (obj->bsseHandle, 0, NULL);
			if (_err_ != BSSE_OK) {
				err = (BSMP_ERR) _err_;
			}
		}
#endif /* BSL_IOS */
	}

	if (err == BSMP_OK) {
		obj->bsseFunc->setCallbackVolume (obj->bsseHandle, __volResetCallback, (void *) obj);
#ifndef BSL_WAV_OUT_VOLUME
		obj->bsseFunc->setCallbackMasterVolume (obj->bsseHandle, __masterVolResetCallback, (void *) obj);
#endif /* BSL_WAV_OUT_VOLUME */
		obj->bsseFunc->setCallbackMasterKeySet (obj->bsseHandle, __masterKeyResetCallback, (void *) obj);
		obj->bsseFunc->setCallbackMasterTuneSet (obj->bsseHandle, __masterTuneResetCallback, (void *) obj);
	
#ifdef BSL_MID_INCLUDE_EFFECT1
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_REVERB, &state, sizeof (int));
		obj->bEffect1 = obj->bEffect1Last = state;
#endif /* BSL_MID_INCLUDE_EFFECT1 */
#ifdef BSL_MID_INCLUDE_EFFECT3
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_CHORUS, &state, sizeof (int));
		obj->bEffect3 = obj->bEffect3Last = state;
#endif /* BSL_MID_INCLUDE_EFFECT3 */
#ifdef BSL_MID_INCLUDE_EFFECT4
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_DELAY, &state, sizeof (int));
		obj->bEffect4 = obj->bEffect4Last = state;
#endif /* BSL_MID_INCLUDE_EFFECT4 */
	}

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSMP_OK) {
		err = (BSMP_ERR) obj->tWaveManager.initialize (&obj->tWaveManager);
	}
#endif /* BSMP_INCLUDE_WAVE */

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _setSampleRate
	(
	BSMPPlayer *obj,
	int sampleRate
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		if (obj->m_wavOut.getSampleRate (&obj->m_wavOut) != sampleRate) {
			err = obj->m_wavOut.setSampleRate (&obj->m_wavOut, sampleRate);
		}
	}

	if (err == BSL_OK) {
		if (obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_SAMPLE_RATE, &sampleRate, sizeof (int)) != BSSE_OK) {
			err = BSL_ERR_UNDEFINED;
		}
	}

	/* reset blocksize to keep latency */
	if (err == BSL_OK) {
		err = obj->setBlockSize (obj, (long) BSL_WAV_BLOCK_SIZE * (long) sampleRate / (long) BSL_WAV_SAMPLE_RATE);
	}

	if (err == BSL_OK) {
		obj->fTickGenChild = (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut) * (float) obj->dwTempo * (float) (100 - obj->iSpeed);
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
		obj->setCallbackDelay (obj, obj->lCallbackDelayMs);
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
	}

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSL_OK) {
		err = obj->tWaveManager.setSampleRate (&obj->tWaveManager, sampleRate);
	}
#endif /* BSMP_INCLUDE_WAVE */

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _setBlockSize
	(
	BSMPPlayer *obj,
	long blockSize
	)
{
	BSLErr err = BSL_OK;

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSL_OK) {
		if (obj->pOutput == NULL || obj->m_wavOut.getBlockSize (&obj->m_wavOut) != blockSize) {
			if (obj->pOutput) {
				bslMemFree (obj->pOutput);
				obj->pOutput = NULL;
			}
			obj->pOutput = (BSLWavIOData32 *) bslMemAlloc (sizeof (BSLWavIOData32) * blockSize * BSL_WAV_CHANNELS);
			if (obj->pOutput == NULL) {
				err = BSL_ERR_MEM_ALLOC;
			}
		}
	}
#endif /* BSMP_INCLUDE_WAVE */

	if (err == BSL_OK) {
		if (obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_BLOCK_SIZE, &blockSize, sizeof (long)) != BSSE_OK) {
			err = BSL_ERR_UNDEFINED;
		}
	}

	if (err == BSL_OK) {
		if (obj->m_wavOut.getBlockSize (&obj->m_wavOut) != blockSize) {
			err = obj->m_wavOut.setBlockSize (&obj->m_wavOut, blockSize);
		}
	}

	if (err == BSL_OK) {
		err = obj->m_wavRender.setBlockSize (&obj->m_wavRender, blockSize);
	}

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSMP_OK) {
		err = obj->tWaveManager.setBlockSize (&obj->tWaveManager, blockSize);
	}
#endif /* BSMP_INCLUDE_WAVE */

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _setPoly
	(
	BSMPPlayer *obj,
	int poly
	)
{
	if (obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_POLY, &poly, sizeof (int)) != BSSE_OK) {
		return BSL_ERR_MODULE;
	}

	return BSL_OK;
}

/*---------------------------------------------------------------------------*/
static int _getPoly
	(
	BSMPPlayer *obj
	)
{
	int poly;

	obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_POLY, &poly, sizeof (int));
	return poly;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static int _getNumDrivers
	(
	BSMPPlayer *obj
	)
{
	return NUM_DRIVERS;
}

/*---------------------------------------------------------------------------*/
static int _getNumDevices
	(
	BSMPPlayer *obj,
	LPCTSTR driver
	)
{
	BSLWavOut wavOut;
	int numDrivers = 0;

#if BSL_WINDOWS
	#ifdef _ASIO_
	if (driver && !_tcscmp (driver, DRIVER_NAME_ASIO)) {
		bslWavOutAsio (&wavOut, NULL, NULL);
	}
	else 
	#endif /* _ASIO_ */
	{
		bslWavOutMmeThread (&wavOut, NULL, NULL);
	}
#elif BSL_MAC || BSL_IOS
	bslWavOutAudioQueue (&wavOut, NULL, NULL);
#elif BSL_SIGMA
	bslWavOutSigmaSmp8670Thread (&wavOut, NULL, NULL);
#elif BSL_LINUX
	if (driver && !_tcscmp (driver, DRIVER_NAME_OSS)) {
		bslWavOutOssThread (&wavOut, NULL, NULL);
	}
	else bslWavOutAlsaThread (&wavOut, NULL, NULL);
#elif BSL_ANDROID
	bslWavOutOpenSLES (&wavOut, NULL, NULL);
#endif

	if (wavOut.initialize (&wavOut) == BSL_OK) {
		numDrivers = wavOut.getDrivers (&wavOut);
	}
	bslWavOutExit (&wavOut);

	return numDrivers;
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getDriverName
	(
	BSMPPlayer *obj,
	int index
	)
{
#if BSL_WINDOWS
	switch (index) {
	#ifdef _ASIO_
	case DRIVER_ASIO:
		return (LPCTSTR) DRIVER_NAME_ASIO;
	#endif /* _ASIO_ */
	case DRIVER_MME:
	default:
		return (LPCTSTR) DRIVER_NAME_MME;
	}
#elif BSL_MAC || BSL_IOS
	return (LPCTSTR) DRIVER_NAME_AUDIO_QUEUE;
#elif BSL_SIGMA
	return (LPCTSTR) DRIVER_NAME_SIGMA_SMP8670;
#elif BSL_LINUX
	switch (index) {
	case DRIVER_OSS:
		return (LPCTSTR) DRIVER_NAME_OSS;
	case DRIVER_ALSA:
	default:
		return (LPCTSTR) DRIVER_NAME_ALSA;
	}
#elif BSL_ANDROID
	return (LPCTSTR) DRIVER_NAME_OPENSLES;
#endif
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getDeviceName
	(
	BSMPPlayer *obj,
	LPCTSTR driver, 
	int index
	)
{
	BSLWavOut wavOut;
	static TCHAR name[64];

#if BSL_WINDOWS
	#ifdef _ASIO_
	if (driver && !_tcscmp (driver, DRIVER_NAME_ASIO)) {
		bslWavOutAsio (&wavOut, NULL, NULL);
	}
	else 
	#endif /* _ASIO_ */
	{
		bslWavOutMmeThread (&wavOut, NULL, NULL);
	}
#elif BSL_MAC || BSL_IOS
	bslWavOutAudioQueue (&wavOut, NULL, NULL);
#elif BSL_SIGMA
	bslWavOutSigmaSmp8670Thread (&wavOut, NULL, NULL);
#elif BSL_LINUX
	if (driver && !_tcscmp (driver, DRIVER_NAME_OSS)) {
		bslWavOutOssThread (&wavOut, NULL, NULL);
	}
	else bslWavOutAlsaThread (&wavOut, NULL, NULL);
#elif BSL_ANDROID
	bslWavOutOpenSLES (&wavOut, NULL, NULL);
#endif

	if (wavOut.initialize (&wavOut) == BSL_OK) {
		wavOut.getDriverName (&wavOut, index, name, 64 - 1);
	}
	bslWavOutExit (&wavOut);

	return (LPCTSTR) name;
}

/*---------------------------------------------------------------------------*/
static BSLErr _open
	(
	BSMPPlayer *obj,
	LPCTSTR driver, 
	LPCTSTR device
	)
{
	BSLErr err = BSL_OK;
	int sampleRate = obj->m_wavOut.getSampleRate (&obj->m_wavOut);
	long blockSize = obj->m_wavOut.getBlockSize (&obj->m_wavOut);
	int buffers = obj->m_wavOut.getBuffers (&obj->m_wavOut);

	bslWavOutExit (&obj->m_wavOut);

#if BSL_WINDOWS
	#ifdef _ASIO_
	if (driver && !_tcscmp (driver, DRIVER_NAME_ASIO)) {
		bslWavOutAsio (&obj->m_wavOut, __render, obj);
	}
	else 
	#endif /* _ASIO_ */
	{
		bslWavOutMmeThread (&obj->m_wavOut, __render, obj);
	}
#elif BSL_MAC || BSL_IOS
	bslWavOutAudioQueue (&obj->m_wavOut, __render, obj);
#elif BSL_SIGMA
	bslWavOutSigmaSmp8670Thread (&obj->m_wavOut, __render, obj);
#elif BSL_LINUX
	if (driver && !_tcscmp (driver, DRIVER_NAME_OSS)) {
		bslWavOutOssThread (&obj->m_wavOut, __render, obj);
	}
	else bslWavOutAlsaThread (&obj->m_wavOut, __render, obj);
#elif BSL_ANDROID
    // override default settings with device preferred sampleRate and blockSize
	buffers = kCallbackDelayMs * sampleRate / (1000 * blockSize);
	if (buffers < 1) buffers = 1;
	else if (BSL_WAV_BUFFERS_MAX <= buffers) buffers = BSL_WAV_BUFFERS_MAX;

	bslWavOutOpenSLES (&obj->m_wavOut, __render, obj);
#endif

	if (err == BSL_OK) {
		obj->m_wavOut.stopped = __stopped;
		err = obj->m_wavOut.initialize (&obj->m_wavOut);
	}

	if (err == BSL_OK) {
		err = obj->setSampleRate (obj, sampleRate);
	}

	if (err == BSL_OK) {
		err = obj->setBlockSize (obj, blockSize);
	}

	if (err == BSL_OK) {
		err = obj->m_wavOut.setBuffers (&obj->m_wavOut, buffers);
	}

	if (err == BSL_OK) {
		err = obj->m_wavOut.open (&obj->m_wavOut, device, NULL, 0UL);
	}

	/* 
		Windows / ASIO
		Linux / ALSA
		The blocksize of above drivers should be fixed after opening, so re-set fixed blocksize again.
	*/
#if BSL_WINDOWS
	#ifdef _ASIO_
	if (err == BSL_OK) {
		if (driver && !_tcscmp (driver, DRIVER_NAME_ASIO)) {
	#ifdef BSMP_INCLUDE_WAVE
			if (obj->pOutput) {
				bslMemFree (obj->pOutput);
				obj->pOutput = NULL;
			}
	#endif /* BSMP_INCLUDE_WAVE */
			err = obj->setBlockSize (obj, obj->m_wavOut.getBlockSize (&obj->m_wavOut));
		}
	}
	#endif /* _ASIO_ */
#elif BSL_LINUX && !BSL_SIGMA
	if (err == BSL_OK) {
		if (!driver || _tcscmp (driver, DRIVER_NAME_ALSA)) {
	#ifdef BSMP_INCLUDE_WAVE
			if (obj->pOutput) {
				bslMemFree (obj->pOutput);
				obj->pOutput = NULL;
			}
	#endif /* BSMP_INCLUDE_WAVE */
			err = obj->setBlockSize (obj, obj->m_wavOut.getBlockSize (&obj->m_wavOut));
		}
	}
#endif

	if (err == BSL_OK) {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_OPEN, 0UL
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _close
	(
	BSMPPlayer *obj
	)
{
	BSLErr err = obj->m_wavOut.close (&obj->m_wavOut);

	if (err == BSL_OK) {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CLOSE, 0UL
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}

	return err;
}


/*---------------------------------------------------------------------------*/
static BSLErr _start
	(
	BSMPPlayer *obj
	)
{
	BSLErr err = BSL_OK;

	obj->pCallback = obj->pCallbackBackup;

	/* check file */
	if (err == BSL_OK) {
		if (!obj->m_file.isOpened (&obj->m_file)) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-bsmp: MIDI sequence not loaded.\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_UNDEFINED;
		}
	}

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	if (err == BSL_OK) {
		_initCallback (obj);
	}
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

	obj->iLastVolume = -1; /* reset volume */

#ifdef BSL_MID_INCLUDE_EFFECT1
	if (err == BSL_OK) {
		int state;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_REVERB, &state, sizeof (int));
		if (state != 0) {
			state = 0;
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_REVERB, &state, sizeof (int));
			state = 1;
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_REVERB, &state, sizeof (int));
		}
	}
#endif /* BSL_MID_INCLUDE_EFFECT1 */

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSL_OK) {
		int division = obj->m_midParser.getDivision (&obj->m_midParser);
        float _time = tempoList_tick2Time(obj->pTempoList, division, obj->dwWritetick);
		obj->tWaveManager.seek (&obj->tWaveManager, (long) (_time * (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut)));
	}
#endif /* BSMP_INCLUDE_WAVE */

	/* callback */
	if (err == BSL_OK) {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_START, 0UL
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);

		err = obj->m_wavOut.start (&obj->m_wavOut);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _stop
	(
	BSMPPlayer *obj
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		err = obj->m_wavOut.stop (&obj->m_wavOut);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static void _stopped
	(
	BSMPPlayer *obj,
	BSLErr error
	)
{

#if BSL_WINDOWS
	bslWavOutMmeStopped (&obj->m_wavOut, error);
#elif BSL_MAC || BSL_IOS
	bslWavOutAudioQueueStopped (&obj->m_wavOut, error);
#elif BSL_ANDROID
	bslWavOutOpenSLESStopped (&obj->m_wavOut, error);
#else
	_bslWavOutStopped (&obj->m_wavOut, error);
#endif

	/* callback */
	if (error != BSL_OK) {
		/* stopped by wav out thread process error */
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_STOP, (DWORD) BSMP_ERR_AUDIO_DRIVER
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}
	else if (obj->m_wavOut.bStopError) {
		/* stopped by internal data error in output () function */
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_STOP, (DWORD) BSMP_ERR_DATA
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}
	else {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_STOP, 0UL
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}
}

/*---------------------------------------------------------------------------*/
static BSLErr _seek
	(
	BSMPPlayer *obj,
	DWORD tick
	)
{
	BSLErr err = BSL_OK;

	if (obj->m_wavOut.isPlaying (&obj->m_wavOut)) {
		obj->dwTickToSeek = tick;
		obj->bSeek = TRUE;
		return err;
	}

	if (err == BSL_OK) {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_SEEK, 0
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, obj->dwTotalSamples
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
	}

	if (err == BSL_OK) {
		_setTempo (obj, DEFAULT_TEMPO
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 
			TRUE,
			0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			);
		_setPlayerParameters (obj, 0UL);
		obj->iLastKey = BSMP_KEY_MIN - 1; /* force reset */
	}

	/* initialize synthesizer */
	if (err == BSL_OK) {
		if (obj->bsseFunc->reset (obj->bsseHandle) != BSSE_OK) {
			err = BSL_ERR_MODULE;
 #ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-bsmp: software synthesizer initialize\n"));
 #endif /* BSL_DEBUG */
		}
	}

	if (err == BSL_OK) {
		_setPlayerParameters (obj, tick);
	}

	if (err == BSL_OK) {
		obj->m_midParser.rewind (&obj->m_midParser, tick);
	}

#ifdef BSMP_INCLUDE_WAVE
	if (err == BSL_OK) {
		int division = obj->m_midParser.getDivision (&obj->m_midParser);
        float _time = tempoList_tick2Time(obj->pTempoList, division, tick);
		obj->tWaveManager.seek (&obj->tWaveManager, (long) (_time * (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut)));
	}
#endif /* BSMP_INCLUDE_WAVE */

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _seekWithTime
    (
    BSMPPlayer *obj,
    float time
    )
{
    int division = obj->m_midParser.getDivision (&obj->m_midParser);
    DWORD tick = tempoList_time2Tick(obj->pTempoList, division, time);
    return obj->seek (obj, tick);
}

#ifdef BSMP_EXPORT_WAVE
/*---------------------------------------------------------------------------*/
static BSLErr _bounce
	(
	BSMPPlayer *obj,
	LPCTSTR file, 
	BSMP_WAVE_FILE type,
	int (*updater) (int percent, void *updateParam), 
	void *updateParam
	)
{
	BSLErr err = BSL_OK;
	BSLWavIOData *pBounceBuffer = NULL;
	BSLWavWriter writer;
	BSLFileSave save;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("\n"));
	bslTrace (_TEXT("bsmp: bounce...\n"));
#endif /* BSL_DEBUG */

	obj->pCallback = _callback_dummy;

	/* check file */
	if (err == BSL_OK) {
		if (!obj->m_file.isOpened (&obj->m_file)) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-bsmp: MIDI sequence not loaded.\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_UNDEFINED;
		}
	}

	if (err == BSL_OK) {
		_seek (obj, 0UL);
	}

#ifdef BSL_MID_INCLUDE_EFFECT1
	if (err == BSL_OK) {
		int state;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_REVERB, &state, sizeof (int));
		if (state != 0) {
			state = 0;
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_REVERB, &state, sizeof (int));
			state = 1;
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_REVERB, &state, sizeof (int));
		}
	}
#endif /* BSL_MID_INCLUDE_EFFECT1 */

	if (err == BSL_OK) {
		obj->m_midParser.rewind (&obj->m_midParser, 0UL);
	}

	if (err == BSL_OK) {
		/* set default block size */
		err = obj->setSampleRate (obj, obj->m_wavOut.getSampleRate (&obj->m_wavOut));
	}

	if (err == BSL_OK) {
		pBounceBuffer = (BSLWavIOData *) bslMemAlloc (obj->m_wavOut.getBlockSize (&obj->m_wavOut) * obj->m_wavOut.getBytesPerFrame (&obj->m_wavOut));
		if (!pBounceBuffer) {
			err = BSL_ERR_MEM_ALLOC;
		}
	}

	if (err == BSL_OK) {
		switch (type) {
		case BSMP_WAVE_FILE_AIFF:
			bslWavWriterAiff (&writer);
			break;
		case BSMP_WAVE_FILE_RIFF:
			bslWavWriterRiff (&writer);
			break;
		default:
			err = BSL_ERR_PARAM_WRONG;
			break;
		}
	}

	if (err == BSL_OK) {
		obj->m_wavRender.setBuffer (&obj->m_wavRender, pBounceBuffer);
		writer.setSampleRate (&writer, obj->m_wavOut.getSampleRate (&obj->m_wavOut));
		writer.setBitsPerSample (&writer, 16 /* obj->m_wavOut.getBitsPerSample (&obj->m_wavOut) */);
		writer.setChannels (&writer, obj->m_wavOut.getChannels (&obj->m_wavOut));
		writer.setFrames (&writer, obj->dwTotaltime * obj->m_wavOut.getSampleRate (&obj->m_wavOut));
#if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
        writer.setFloatRendering (&writer, TRUE);
#endif

		if (file) {
			bslFileSave (&save);
			save.setDestination (&save, file);
		}
		else {
			bslFileSaveDummy (&save);
		}
		err = writer.save (&writer, &save, &obj->m_wavRender, updater, updateParam);
	}

	if (pBounceBuffer) {
		bslMemFree (pBounceBuffer);
		pBounceBuffer = NULL;
	}

	bslFileSaveExit (&save);
	bslWavWriterExit (&writer);

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: bounce completed\n"));
#endif /* BSL_DEBUG */

	obj->pCallback = obj->pCallbackBackup;

	return err;
}
#endif /* BSMP_EXPORT_WAVE */

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSL_WAV_RENDER_STATE _output
	(
	BSMPPlayer *obj,
	BSLWavIOData *data,
	long sampleFrames
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	, long *readFrames
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
	)
{
	BSL_WAV_RENDER_STATE more = BSL_WAV_RENDER_STATE_FULL;

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
	BOOL preventResetOnDoingInternalSeek = FALSE;
	
	if (obj->KY_nRhythmChangeLastType != obj->KY_nRhythmChangeType) {
		BYTE port, ch;
		for (port = 0; port < BSSYNTH_MID_PORTS; port++) {
			for (ch = 0; ch < 16; ch++) {
				if (_KY_isSubstitutedForRhythmChange(port, ch)) {
					obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, BSL_MID_STATUS_CONTROL + ch, BSL_MID_CNUM_ALL_NOTE_OFF, 0x00);
				}
			}
		}
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_MASTER_KEY, &obj->iKey, sizeof (int));
		obj->KY_nRhythmChangeLastType = obj->KY_nRhythmChangeType;

		// invoke internal seek to same position to update MIDI settings by using event _chaseEvent()
		preventResetOnDoingInternalSeek = TRUE;
		_seek (obj, obj->dwWritetick);
	}
#endif
	
	if (obj->bSeek) {
		obj->bSeek = 0;

		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_SEEK, 0
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, obj->dwTotalSamples
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);

		/* initialize synthesizer */
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
		if (!preventResetOnDoingInternalSeek)
#endif
		if (obj->bsseFunc->reset (obj->bsseHandle) != BSSE_OK) {
 #ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-bsmp: software synthesizer initialize\n"));
 #endif /* BSL_DEBUG */
		}

		_setPlayerParameters (obj, obj->dwTickToSeek);
		obj->m_midParser.rewind (&obj->m_midParser, obj->dwTickToSeek);

#ifdef BSMP_INCLUDE_WAVE
		{
			int division = obj->m_midParser.getDivision (&obj->m_midParser);
            float _time = tempoList_tick2Time(obj->pTempoList, division, obj->dwTickToSeek);
			obj->tWaveManager.seek (&obj->tWaveManager, (long) (_time * (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut)));
		}
#endif /* BSMP_INCLUDE_WAVE */
	}

	if (obj->iLastKey != obj->iKey) {
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_MASTER_KEY, &obj->iKey, sizeof (int));
#ifdef BSMP_INCLUDE_WAVE
		obj->tWaveManager.setKey (&obj->tWaveManager, obj->iKey);
#endif /* BSMP_INCLUDE_WAVE */
		obj->iLastKey = obj->iKey;
	}

	if (obj->nLastTune != obj->nTune) {
		short value = obj->nTune;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_MASTER_TUNE, &value, sizeof (short));
		obj->nLastTune = obj->nTune;
	}

#ifdef BSMP_INCLUDE_WAVE
	if (obj->iLastSpeed != obj->iSpeed) {
		obj->fTickGenChild = (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut) * (float) obj->dwTempo * (float) (100 - obj->iSpeed);
		obj->tWaveManager.setSpeed (&obj->tWaveManager, obj->iSpeed);
		obj->iLastSpeed = obj->iSpeed;
	}
#endif /* BSMP_INCLUDE_WAVE */

#ifndef BSL_WAV_OUT_VOLUME
	if (obj->iLastVolume != obj->iVolume) {
		int vol;
	#ifdef BSSYNTH_KT
		vol = (int) obj->byOrgVolume * obj->iVolume / (BSMP_KT_VOLUME_MAX);
	#else /* BSSYNTH_KT */
		vol = (int) obj->byOrgVolume * obj->iVolume / BSMP_VOLUME_MAX;
	#endif /* BSSYNTH_KT */
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_MASTER_VOLUME, &vol, sizeof (int));
		obj->iLastVolume = obj->iVolume;
		
	#ifdef BSMP_INCLUDE_WAVE
		obj->tWaveManager.setVolume (&obj->tWaveManager, obj->iVolume);
	#endif /* BSMP_INCLUDE_WAVE */
	}
#endif /* BSL_WAV_OUT_VOLUME */

	if (obj->iLastGuide != obj->iGuide) {
		if (obj->iGuideMainCh >= 0) {
			obj->bsseFunc->setChannelMessage (obj->bsseHandle, (BYTE) (obj->iGuideMainCh / 16), (BYTE) ((obj->iGuideMainCh % 16) + BSL_MID_STATUS_CONTROL), (BYTE) BSL_MID_CNUM_VOLUME, obj->byGuideMainValue);
		}
		if (obj->iGuideSubCh >= 0) {
			obj->bsseFunc->setChannelMessage (obj->bsseHandle, (BYTE) (obj->iGuideSubCh / 16), (BYTE) ((obj->iGuideSubCh  % 16) + BSL_MID_STATUS_CONTROL), (BYTE) BSL_MID_CNUM_VOLUME, obj->byGuideSubValue);
		}
		obj->iLastGuide = obj->iGuide;
	}

#ifdef BSL_MID_INCLUDE_EFFECT1
	if (obj->bEffect1Last != obj->bEffect1) {
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_REVERB, &obj->bEffect1, sizeof (int));
		obj->bEffect1Last = obj->bEffect1;
	}
#endif /* BSL_MID_INCLUDE_EFFECT1 */

#ifdef BSL_MID_INCLUDE_EFFECT3
	if (obj->bEffect3Last != obj->bEffect3) {
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_CHORUS, &obj->bEffect3, sizeof (int));
		obj->bEffect3Last = obj->bEffect3;
	}
#endif /* BSL_MID_INCLUDE_EFFECT3 */

#ifdef BSL_MID_INCLUDE_EFFECT4
	if (obj->bEffect4Last != obj->bEffect4) {
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_SET_DELAY, &obj->bEffect4, sizeof (int));
		obj->bEffect4Last = obj->bEffect4;
	}
#endif /* BSL_MID_INCLUDE_EFFECT4 */

	obj->lSampleFrames = sampleFrames;
	
#ifndef BSMP_INCLUDE_WAVE
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
    obj->pOutput = data;
    #else
    obj->pOutput = (BSLWavIOData16 *) data;
    #endif
#endif /* BSMP_INCLUDE_WAVE */

	if (obj->lEndMargin >= 0L) {
#ifdef BSMP_INCLUDE_WAVE
		obj->bsseFunc->processReplacing32 (obj->bsseHandle, obj->pOutput, sampleFrames);
		obj->tWaveManager.process (&obj->tWaveManager, obj->pOutput, sampleFrames);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
        obj->bsseFunc->processReplacingFloat (obj->bsseHandle, obj->pOutput, sampleFrames);
    #else
        obj->bsseFunc->processReplacing (obj->bsseHandle, obj->pOutput, sampleFrames);
    #endif
#endif /* BSMP_INCLUDE_WAVE */
		obj->lEndMargin -= sampleFrames;
		if (obj->lEndMargin <= 0L) {
			more = BSL_WAV_RENDER_STATE_COMPLETED;
		}
	}
	else {
		while (1) {
			BSL_MID_EVENT_PROC err = obj->m_midParser.sortEvent (&obj->m_midParser, 0UL, obj);
			switch (err) {
			case BSL_MID_EVENT_PROC_OK:
				break;
			case BSL_MID_EVENT_PROC_BUFFER_FULL:
				goto OUTPUT_END;
			case BSL_MID_EVENT_PROC_ERROR:
				more = BSL_WAV_RENDER_STATE_ERROR;
				goto OUTPUT_END;
			case BSL_MID_EVENT_PROC_FINISHED:
				{
					obj->lEndMargin = (long) obj->m_wavOut.getSampleRate (&obj->m_wavOut);
					{
						long remain = sampleFrames - obj->lBufferedSamples;
						if (remain > 0L) {
#ifdef BSMP_INCLUDE_WAVE
							BSLWavIOData32 *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
                            BSLWavIOData *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
    #else
                            BSLWavIOData16 *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
    #endif
#endif /* BSMP_INCLUDE_WAVE */
#ifdef BSMP_INCLUDE_WAVE
							obj->bsseFunc->processReplacing32 (obj->bsseHandle, buffer, remain);
							obj->tWaveManager.process (&obj->tWaveManager, buffer, remain);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
                            obj->bsseFunc->processReplacingFloat (obj->bsseHandle, buffer, remain);
    #else
                            obj->bsseFunc->processReplacing (obj->bsseHandle, buffer, remain);
    #endif
#endif /* BSMP_INCLUDE_WAVE */
						}
					}
				}
				goto OUTPUT_END;
			default:
				more = BSL_WAV_RENDER_STATE_ERROR;
				goto OUTPUT_END;
			}
		}
	}

OUTPUT_END: ;

#ifdef BSMP_INCLUDE_WAVE
	{
		long i = sampleFrames * BSL_WAV_CHANNELS;
		BSLWavIOData32 *in = obj->pOutput;
		BSLWavIOData16 *out = (BSLWavIOData16 *) data;
		while (--i >= 0L) {
			// convert 32bit -> 16bit
			*in >>= 13;
			if ((DWORD) (*in + 32768) <= 65535) {
				*out++ = (BSLWavIOData16) *in;
			}
			else {
				if (32767 < *in) {
					*out++ = 32767;
				}
				else if (*in < -32768) {
					*out++ = -32768;
				}
			}
			in++;
		}
	}
#endif /* BSMP_INCLUDE_WAVE */

#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
	if (readFrames) {
		*readFrames = sampleFrames;
	}
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */

	return more;
}

/*---------------------------------------------------------------------------*/
static void _setPlayerParameters
	(
	BSMPPlayer *obj,
	DWORD tick
	)
{
	obj->dwWritetick = tick;
	obj->fTickGenMother = 1000000.f * (float) obj->m_midParser.getDivision (&obj->m_midParser) * 100.f;
	obj->fTickGenRemainder = 0;
	obj->lRemainedSamples = 0L;
	obj->lBufferedSamples = 0L;

	obj->lEndMargin = -1L;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC _generateAudio
	(
	BSMPPlayer *obj,
	DWORD readtick
	)
{
	long samples;

	if (readtick <= obj->dwWritetick) {
		samples = obj->lRemainedSamples;
	}
	else {
		float child = obj->fTickGenChild * (float) (readtick - obj->dwWritetick) + obj->fTickGenRemainder;
		obj->fTickGenRemainder = (float) fmod (child, obj->fTickGenMother);
		samples = (long) (child / obj->fTickGenMother);

		obj->dwWritetick = readtick;
	}

	{
		long requestedSamples = obj->lSampleFrames - obj->lBufferedSamples;
#ifdef BSMP_INCLUDE_WAVE
		BSLWavIOData32 *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
        BSLWavIOData *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
    #else
        BSLWavIOData16 *buffer = obj->pOutput + (obj->lBufferedSamples * BSL_WAV_CHANNELS);
    #endif
#endif /* BSMP_INCLUDE_WAVE */

		if (samples >= requestedSamples) {
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			obj->executeCallbackDelayed (obj, obj->dwTotalSamples);
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
#ifdef BSMP_INCLUDE_WAVE
			obj->bsseFunc->processReplacing32 (obj->bsseHandle, buffer, requestedSamples);
			obj->tWaveManager.process (&obj->tWaveManager, buffer, requestedSamples);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
            obj->bsseFunc->processReplacingFloat (obj->bsseHandle, buffer, requestedSamples);
    #else
            obj->bsseFunc->processReplacing (obj->bsseHandle, buffer, requestedSamples);
    #endif
#endif /* BSMP_INCLUDE_WAVE */
			obj->lRemainedSamples = samples - requestedSamples;
			obj->lBufferedSamples = 0L;
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			obj->dwTotalSamples += (DWORD) requestedSamples;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			return BSL_MID_EVENT_PROC_BUFFER_FULL;
		}
		else if (samples > 0L) {
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			obj->executeCallbackDelayed (obj, obj->dwTotalSamples);
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
#ifdef BSMP_INCLUDE_WAVE
			obj->bsseFunc->processReplacing32 (obj->bsseHandle, buffer, samples);
			obj->tWaveManager.process (&obj->tWaveManager, buffer, samples);
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
            obj->bsseFunc->processReplacingFloat (obj->bsseHandle, buffer, samples);
    #else
            obj->bsseFunc->processReplacing (obj->bsseHandle, buffer, samples);
    #endif
#endif /* BSMP_INCLUDE_WAVE */
			obj->lRemainedSamples = 0L;
			obj->lBufferedSamples += samples;
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			obj->dwTotalSamples += (DWORD) samples;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			return BSL_MID_EVENT_PROC_OK;
		}
		else {
			return BSL_MID_EVENT_PROC_OK;
		}
	}
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC _chaseEvent
	(
	BSMPPlayer *obj,
	BSLMidEvent*event
	)
{
	BYTE status = event->byStatus;
	BYTE *data = event->pData;
	BYTE port = event->byPort;
	DWORD msg;
	BSL_MID_EVENT_PROC err = BSL_MID_EVENT_PROC_OK;

#if (1 < BSSYNTH_MID_PORTS)
	#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
	switch (obj->KY_nRhythmChangeType) {
		case BSMP_KY_RHYTHM_TYPE_OFF:
			if (BSSYNTH_MID_PORTS <= port) {
				return err;
			}
			break;
		default:
			if (BSSYNTH_MID_PORTS <=port && port != (BSMP_KY_RHYTHM_PORT + obj->KY_nRhythmChangeType - 1)) {
				return BSL_MID_EVENT_PROC_OK;
			}
			break;
	}
	#else
	if (BSSYNTH_MID_PORTS <= port) {
		return err;
	}
	#endif
#endif

	switch (status) {

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	case BSL_MID_STATUS_MIDI_CLOCK:
		obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_CLOCK, NULL, obj->pCallbackUser);
		break;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	case BSL_MID_META_TYPE_SET_TEMPO:
		msg = ((DWORD) data[0] << 16) + ((DWORD) data[1] <<  8) + (DWORD) data[2];
		_setTempo (obj, msg
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, 
			TRUE,
			0UL
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			);
#ifdef BSL_DEBUG
		bslTrace (_TEXT("bsmp: tempo change = %lu[BPM](%lu[usec/beat])\n"), 60UL*1000UL*1000UL/msg, msg);
#endif /* BSL_DEBUG */
		break;

	case BSL_MID_META_TYPE_TIME_SIGNATURE:
		msg = ((DWORD) data[0] << 24) + ((DWORD) data[1] <<  16) + ((DWORD) data[2] << 8) + ((DWORD) data[3]);
		obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_TIME_SIGNATURE, &msg, obj->pCallbackUser);
		break;

	case BSL_MID_STATUS_SYX:
		obj->bsseFunc->setSystemExclusiveMessage (obj->bsseHandle, port, BSL_MID_STATUS_SYX, event->pData, event->nSize);
		obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_SYSTEM_EXCLUSIVE_MESSAGE, NULL, obj->pCallbackUser);
		break;

	default:
		if (status >= BSL_MID_STATUS_POLY_PRESS && status < BSL_MID_STATUS_SYX) { /* 0xA0 - 0xEF */

#ifdef BSSYNTH_KY
			if (port < BSSYNTH_MID_PORTS && (status & 0x0F) == 0x00) {
				// channel1 should be not used
				break;
			}
#endif /* BSSYNTH_KY */
			
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
			switch (obj->KY_nRhythmChangeType) {
				case BSMP_KY_RHYTHM_TYPE_OFF:
					// In normal mode
					obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
					break;
				default:
					// In rhythm change mode
					if (_KY_isSubstitutedForRhythmChange (port, status & 0x0F)) {
						// disable part which will be substituted by rhythm pattern data
					}
					else {
						if (port < BSSYNTH_MID_PORTS) {
							obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
						}
						else {
							port = BSL_MID_PORT_A; // TODO: is it ok?
							obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
						}
					}
			}
#else
			obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
#endif

#if (defined BSSYNTH_SHE)
			switch (status & 0xF0) {
			case 0xC0:
				msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
				obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, &msg, obj->pCallbackUser);
				break;
			}
#else
			msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
			obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, &msg, obj->pCallbackUser);
#endif
		}
		break;
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSL_MID_EVENT_PROC _getEvent
	(
	BSMPPlayer *obj,
	BSLMidEvent*event
	)
{
	BYTE status = event->byStatus;
	BYTE *data = event->pData;
	BYTE port = event->byPort;
	DWORD msg;

#if (1 < BSSYNTH_MID_PORTS)
	#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
	switch (obj->KY_nRhythmChangeType) {
		case BSMP_KY_RHYTHM_TYPE_OFF:
			if (BSSYNTH_MID_PORTS <= port) {
				return BSL_MID_EVENT_PROC_OK;
			}
			break;
		default:
			if (BSSYNTH_MID_PORTS <=port && port != (BSMP_KY_RHYTHM_PORT + obj->KY_nRhythmChangeType - 1)) {
				return BSL_MID_EVENT_PROC_OK;
			}
			break;
	}
	#else
	if (BSSYNTH_MID_PORTS <= port) {
		return BSL_MID_EVENT_PROC_OK;
	}
	#endif
#endif

	switch (status) {

#ifdef BSL_MID_INCLUDE_MIDICLOCK
	case BSL_MID_STATUS_MIDI_CLOCK:
		if (_generateAudio (obj, event->dwTick) != BSL_MID_EVENT_PROC_OK) {
			return BSL_MID_EVENT_PROC_BUFFER_FULL;
		}
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CLOCK, 0
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, obj->dwTotalSamples
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
		break;
#endif /* BSL_MID_INCLUDE_MIDICLOCK */

	case BSL_MID_META_TYPE_SET_TEMPO:
		if (_generateAudio (obj, event->dwTick) != BSL_MID_EVENT_PROC_OK) {
			return BSL_MID_EVENT_PROC_BUFFER_FULL;
		}

		msg = ((DWORD) data[0] << 16) + ((DWORD) data[1] <<  8) + (DWORD) data[2];
		_setTempo (obj, msg
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			,
			FALSE,
			obj->dwTotalSamples
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			);
#ifdef BSL_DEBUG
		bslTrace (_TEXT("bsmp: tempo change = %lu[BPM](%lu[usec/beat])\n"), 60UL*1000UL*1000UL/msg, msg);
#endif /* BSL_DEBUG */
		break;

	case BSL_MID_META_TYPE_TIME_SIGNATURE:
		msg = ((DWORD) data[0] << 24) + ((DWORD) data[1] <<  16) + ((DWORD) data[2] << 8) + ((DWORD) data[3]);
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_TIME_SIGNATURE, msg
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, obj->dwTotalSamples
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
		break;

#ifdef BSSYNTH_KT
	case BSL_MID_META_TYPE_LYRIC:
		if (9 < event->nSize) {
			if (_tcsncmp ((char *) data, _TEXT("{#CHORUS="), 9) == 0) {
				int number = atoi ((const char *) &data[9]);
				if (1 <= number) {
					obj->executeCallback (obj, BSMP_CALLBACK_TYPE_KT_CHORUS, (DWORD) number
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
						, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
					);
				}
			}
		}
		break;
#endif /* BSSYNTH_KT */

	case BSL_MID_STATUS_SYX:
		if (_generateAudio (obj, event->dwTick) != BSL_MID_EVENT_PROC_OK) {
			return BSL_MID_EVENT_PROC_BUFFER_FULL;
		}
		obj->bsseFunc->setSystemExclusiveMessage (obj->bsseHandle, port, BSL_MID_STATUS_SYX, event->pData, event->nSize);

#if (defined BSSYNTH_SHE) || (defined BSSYNTH_SEGA)
#else
		/* send all messages */
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_SYSTEM_EXCLUSIVE_MESSAGE, 0UL
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
			, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
		);
#endif
		break;

#if (defined BSSYNTH_88)
	case BSL_MID_META_TYPE_MARKER:
		if (obj->portSelector.nPortSelectionMethod == BSMP_PORT_SELECTION_METHOD_U) {
			if (4 <= event->nSize) {
				if (data[0] == 0x45 && data[1] == 0x2D && data[2] == 0x30 && data[3] == 0x36) { /* E-06 */
					printf ("bssynthPlayer: Type U 'E-06'\n"); // FIXME
					obj->stop (obj);
				}
			}
		}
		break;
#endif

	default:
		if (status >= BSL_MID_STATUS_NOTE_OFF && status < BSL_MID_STATUS_SYX) {

#ifdef BSSYNTH_KY
			if (port < BSSYNTH_MID_PORTS && (status & 0x0F) == 0x00) {
				// channel1 should be not used
				break;
			}
#endif /* BSSYNTH_KY */
			
			if (_generateAudio (obj, event->dwTick) != BSL_MID_EVENT_PROC_OK) {
				return BSL_MID_EVENT_PROC_BUFFER_FULL;
			}

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
			switch (obj->KY_nRhythmChangeType) {
				case BSMP_KY_RHYTHM_TYPE_OFF:
					// In normal mode
					obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
					break;
				default:
					// In rhythm change mode
					if (_KY_isSubstitutedForRhythmChange (port, status & 0x0F)) {
						// disable part which will be substituted by rhythm pattern data
					}
					else {
						if (port < BSSYNTH_MID_PORTS) {
							obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
						}
						else {
							port = BSL_MID_PORT_A; // TODO: is it ok?
							obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
						}
					}
			}
#else
			obj->bsseFunc->setChannelMessage (obj->bsseHandle, port, status, data[0], data[1]);
#endif

#if (defined BSSYNTH_SHE)
			/* send only guide melody note on/off */
			switch (status & 0xF0) {
			case 0x80:
			case 0x90:
				{
					int ch = (port << 4) + (status & 0x0F);
					if (ch == obj->iGuideMainCh) {
						msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
						obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, msg
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
							, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
						);
					}
				}
				break;
			case 0xC0:
				msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
				obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, msg
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
					, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
				);
				break;
			}
#elif (defined BSSYNTH_SEGA)
			/* send only guide melody note on/off  (A01 and B15) */
			if ((port == 0 && (status & 0xEF) == 0x80) || (port == 1 && (status & 0xEF) == 0x8E)) {
				msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
				obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, msg
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
					, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
				);
			}
#else
			/* send all messages */
			msg = ((DWORD) port << 24) + ((DWORD) status << 16) + ((DWORD) data[0] << 8) + (DWORD) data[1];
			obj->executeCallback (obj, BSMP_CALLBACK_TYPE_CHANNEL_MESSAGE, msg
	#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
				, obj->dwTotalSamples
	#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
			);
#endif
		}
		break;
	}

	return BSL_MID_EVENT_PROC_OK;
}

#ifdef BSSYNTH_WTBL_ON_DEMAND
static BSL_MID_EVENT_PROC _getEventOnDemand
	(
	BSMPPlayer *obj,
	BSLMidEvent*event
	)
{
	BYTE port = event->byPort;
	BYTE status = event->byStatus;
	BYTE *data = event->pData;
	int size = event->nSize;
	BSL_MID_EVENT_PROC err = BSL_MID_EVENT_PROC_OK;

	static BYTE bankMsb[BSSYNTH_MID_PORTS][16];
	static BYTE bankLsb[BSSYNTH_MID_PORTS][16];
	static BOOL isDrum[BSSYNTH_MID_PORTS][16];
	
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
	static BYTE ky_bankMsb[BSMP_KY_RHYTHM_TYPES][16];
	static BYTE ky_bankLsb[BSMP_KY_RHYTHM_TYPES][16];
	static BOOL ky_isDrum[BSMP_KY_RHYTHM_TYPES][16];

	if (BSSYNTH_MID_PORTS <= port && (port < BSMP_KY_RHYTHM_PORT || (BSMP_KY_RHYTHM_PORT + BSMP_KY_RHYTHM_TYPES) <= port)) {
		return err;
	}
#else
	if (BSSYNTH_MID_PORTS <= port) return err;
#endif

	switch (status) {
	case BSL_MID_STATUS_SYX:
		switch (data[0]) {
		case BSL_MID_SYX_ID_UNV_NON_REAL_TIME:
			if (size != 5) break;
			if (data[1] == BSL_MID_SYX_ID_UNV_DEV_BROAD) {
				switch (data[2]) {
				case BSL_MID_SYC_IDS1_GM:
					switch (data[3]) {
					case BSL_MID_SYC_IDS2_GM_ON:
					case BSL_MID_SYC_IDS2_GM_OFF:
					case BSL_MID_SYC_IDS2_GM2_ON:
						/* gm system on */
						{
							int m, p;
							for (m = 0; m < BSSYNTH_MID_PORTS; m++) {
								for (p = 0; p < 16; p++) {
									bankMsb[m][p] = 0;
	#ifdef BSSYNTH_88
									bankLsb[m][p] = 2;
	#else /* BSSYNTH_88 */
									bankLsb[m][p] = 0;
	#endif /* BSSYNTH_88 */
									isDrum[m][p] = (p == 9) ? TRUE : FALSE;
								}
							}
	#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
							for (m = 0; m < BSMP_KY_RHYTHM_TYPES; m++) {
								for (p = 0; p < 16; p++) {
									ky_bankMsb[m][p] = 0;
									ky_bankLsb[m][p] = 2;
									ky_isDrum[m][p] = (p == 9 || p == 10) ? TRUE : FALSE;
								}
							}
	#endif
						}
						break;
					}
					break;
				}
			}
			break;
		case BSL_MID_SYX_ID_ROLAND:
			{
				DWORD adrs;

				if (size < 7) break;

				if (data[1] !=  kMid88DevIdDefault /* device ID */
					|| data[2] != kMid88SyxIdGs /* roland model ID */
					|| data[3] != kMid88SyxIdDt1 /* data set1 */
					) {
					break;
				}

				adrs = (((DWORD) data[4]) << 16) + (((DWORD) data[5]) << 8) + ((DWORD) data[6]);
				switch (adrs) {
				case kMid88AddrSysModeSet:
				case kMid88AddrModeSet:
					if (size == 10) {
						/* gs reset / system mode set */
						{
							int m, p;
							for (m = 0; m < BSSYNTH_MID_PORTS; m++) {
								for (p = 0; p < 16; p++) {
									bankMsb[m][p] = 0;
	#ifdef BSSYNTH_88
									bankLsb[m][p] = 2;
	#else /* BSSYNTH_88 */
									bankLsb[m][p] = 0;
	#endif /* BSSYNTH_88 */
									isDrum[m][p] = (p == 9) ? TRUE : FALSE;
								}
							}
	#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
							for (m = 0; m < BSMP_KY_RHYTHM_TYPES; m++) {
								for (p = 0; p < 16; p++) {
									ky_bankMsb[m][p] = 0;
									ky_bankLsb[m][p] = 2;
									ky_isDrum[m][p] = (p == 9 || p == 10) ? TRUE : FALSE;
								}
							}
	#endif
						}
					}
					break;
				default:
					{				
						int reverse = 0;
						int module = 0;
						int part;

						/* block no. -> part no.  */
						static const int iBlock2Part[16] = {9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15};
	
						/* patch parameter */
						if ((adrs & 0x00E00000UL) == 0x00400000UL) {
							/* 004xxxxxH -> for own part  */
							/* 005xxxxxH -> for another part  */
							if ((adrs & 0x00F00000UL) == 0x00500000UL) {
								reverse = 1;
								adrs &= 0x00EFFFFF; /* 005xxxxx -> 004xxxxx */
							}
							/* else {
								reverse = 0;
								} */
							module = port^reverse;
							/* patch part parameters */
							if (0x00401000UL <= adrs && adrs < 0x00410000UL) {
								/* 00401000 - 0040FFFF */
								part = (int) ((adrs >> 8) & 0x0000000FUL);
								part = iBlock2Part[part];
								adrs &= 0x00EFF0FFUL; /* mask part */

								switch (adrs) {
								case kMid88AddrUseForRhyPart:
									if (data[7] == 0) {
										isDrum[module][part] = FALSE;
									}
									else {
										isDrum[module][part] = TRUE;
									}
								}
							}
						}
					}
				}
			}
			break;
		}
		break;
	default:
		if (status >= BSL_MID_STATUS_NOTE_OFF && status < BSL_MID_STATUS_SYX) {
			BYTE status0 = status & 0xF0;
			BYTE ch = status & 0x0F;
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
			BOOL isRC = FALSE;
#endif
#ifdef BSSYNTH_KY
			if (port < BSSYNTH_MID_PORTS) {
				if (ch == 0) {
					// channel1 should be not used
					break;
				}
			}
	#ifdef BSSYNTH_KY_RHYTHM_CHANGE
			else if (BSSYNTH_MID_PORTS <= port) {
				port -= BSMP_KY_RHYTHM_PORT;
				isRC = TRUE;
			}
	#endif /* BSSYNTH_KY_RHYTHM_CHANGE */
#endif /* BSSYNTH_KY */
			switch (status0) {
			case 0xC0:
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
				if (isRC) {
					DWORD locale = data[0];
					locale |= ((DWORD) ky_bankMsb[port][ch] << 16);
					locale |= ((DWORD) ky_bankLsb[port][ch] << 8);
					if (ky_isDrum[port][ch]) locale |= 0x80000000;
					obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_LOAD_INSTRUMENT, &locale, sizeof (locale));
				}
				else
#endif
				{
					DWORD locale = data[0];
					locale |= ((DWORD) bankMsb[port][ch] << 16);
					locale |= ((DWORD) bankLsb[port][ch] << 8);
					if (isDrum[port][ch]) locale |= 0x80000000;
					obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_LOAD_INSTRUMENT, &locale, sizeof (locale));
				}
				break;
			case 0xB0:
				switch (data[0]) {
				case BSL_MID_CNUM_BANK_SEL_M:
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
					if (isRC) {
						ky_bankMsb[port][ch] = data[1];
					}
					else
#endif
					bankMsb[port][ch] = data[1];
					break;
				case BSL_MID_CNUM_BANK_SEL_L:
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
					if (isRC) {
						ky_bankLsb[port][ch] = data[1];
					}
					else
#endif
					bankLsb[port][ch] = data[1];
					break;
				}
				break;
			}
		}
		break;
	}

	return err;
}
#endif /* BSSYNTH_WTBL_ON_DEMAND */

#pragma mark -
#pragma mark file
/*---------------------------------------------------------------------------*/
static BSLErr __set
	(
	BSMPPlayer *obj
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		err = _seek (obj, 0UL);
	}

	/* get smf file structure */
	if (err == BSL_OK) {
		bslMidParserExit (&obj->m_midParser);

		bslMidParserAuto (&obj->m_midParser);
		obj->m_midParser.pCallbackUser = obj;
		obj->m_midParser.chaseEvent = __chaseEvent;
		obj->m_midParser.getEvent = __getEvent;

		err = obj->m_midParser.parse (&obj->m_midParser, &obj->m_file);
	}

	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		err = 
#endif /* BSL_DEBUG */
		bslMidParserGetInfo (&obj->m_midParser, &obj->dwTotaltick, &obj->dwTotaltime);
	}

	if (err == BSL_OK) {
		obj->fTickGenMother = 1000000.f * (float) obj->m_midParser.getDivision (&obj->m_midParser) * 100.f;
	}

	// テンポリスト生成
	if (err == BSL_OK) {
        tempoList_remove(&obj->pTempoList);

		obj->m_midParser.chaseEvent = NULL;
		obj->m_midParser.getEvent = __getEventTempoList;
		obj->m_midParser.rewind (&obj->m_midParser, 0UL);
		obj->m_midParser.sortEvent (&obj->m_midParser, 0UL, obj);

		obj->m_midParser.chaseEvent = __chaseEvent;
		obj->m_midParser.getEvent = __getEvent;
		obj->m_midParser.rewind (&obj->m_midParser, 0UL);

        tempoList_show(obj->pTempoList);
	}

#if (1 < BSSYNTH_MID_PORTS)
	/* 各SMFトラックに対応させるMIDIポートの自動判別 */
	if (err == BSL_OK) {
		_setTrack (obj);
	}
#endif

#if (defined BSSYNTH_88)
	if (err == BSL_OK) {
		if (obj->portSelector.nPortSelectionMethod == BSMP_PORT_SELECTION_METHOD_S) {
			obj->fSegaAbsoluteTimeForFirstEvent = bsmpSegaGetAbsoluteTimeForFirstNoteEvent (&obj->m_midParser);
		}
	}
#endif

#ifdef BSSYNTH_WTBL_ON_DEMAND
	/* SMF内で使用されている音色の検索および読込み */
	if (err == BSL_OK) {
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_UNLOAD_ALL_INSTRUMENT, NULL, 0);

		{
			/* load default preset */
			DWORD locale = 0x00; /* program change */
			locale |= (0x00 << 16); /* bank msb */
			locale |= (0x02 << 8); /* bank lsb */
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_LOAD_INSTRUMENT, &locale, sizeof (locale)); /* Piano */
			locale |= 0x80000000;
			obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_LOAD_INSTRUMENT, &locale, sizeof (locale)); /* Standard Set */
		}
		
		obj->m_midParser.chaseEvent = NULL;
		obj->m_midParser.getEvent = __getEventOnDemand;
		obj->m_midParser.rewind (&obj->m_midParser, 0UL);
		obj->m_midParser.sortEvent (&obj->m_midParser, 0UL, obj);

		obj->m_midParser.chaseEvent = __chaseEvent;
		obj->m_midParser.getEvent = __getEvent;
		obj->m_midParser.rewind (&obj->m_midParser, 0UL);
	}
#endif /* BSSYNTH_WTBL_ON_DEMAND */

#ifdef BSMP_INCLUDE_WAVE
	obj->tWaveManager.removeWaveAll (&obj->tWaveManager);
#endif /* BSMP_INCLUDE_WAVE */

	if (err == BSL_OK) {
		err = _seek (obj, 0UL);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _set
	(
	BSMPPlayer *obj,
	LPCTSTR file
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		err = obj->m_file.open (&obj->m_file, file);
	}

	if (err == BSL_OK) {
		err = __set (obj);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _setMemory
	(
	BSMPPlayer *obj,
	char *data,
	unsigned long size
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		err = obj->m_file.openMem (&obj->m_file, data, size);
	}

	if (err == BSL_OK) {
		err = __set (obj);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static DWORD _getTotalTick
	(
	BSMPPlayer *obj
	)
{
	return obj->dwTotaltick;
}

/*---------------------------------------------------------------------------*/
static DWORD _getTotalTime
	(
	BSMPPlayer *obj
	)
{
	return obj->dwTotaltime;
}

#pragma mark -
#pragma mark callback
/*---------------------------------------------------------------------------*/
static void _initCallback
	(
	BSMPPlayer *obj
	)
{
	int q;
	obj->dwTotalSamples = 0UL;
	obj->iCallbackQueueGet = 0;
	obj->iCallbackQueuePut = 0;  
	for (q = 0; q < kCallbackQueues; q++) {
		obj->tCallbackQueue[q].nType = BSMP_CALLBACK_TYPE_NULL; /* clear */
		obj->tCallbackQueue[q].dwData = 0UL;
		obj->tCallbackQueue[q].dwSample = 0xFFFFFFFFUL;
	}
}

/*---------------------------------------------------------------------------*/
static void _setCallback
	(
	BSMPPlayer *obj,
	BSMP_CALLBACK callback,
	void *user
	) 
{
	if (callback == NULL) callback = _callback_dummy;
	obj->pCallback = obj->pCallbackBackup = callback; 
	obj->pCallbackUser = user;
}

/*---------------------------------------------------------------------------*/
static void _executeCallback
	(
	BSMPPlayer *obj,
	BSMP_CALLBACK_TYPE type, 
	DWORD data
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	, DWORD sample
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
	)
{
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	switch (type) {
	case BSMP_CALLBACK_TYPE_OPEN:
	case BSMP_CALLBACK_TYPE_START:
	case BSMP_CALLBACK_TYPE_SEEK:
		obj->iCallbackQueueGet = 0;
		obj->iCallbackQueuePut = 0;  
		obj->pCallback (obj->bsmpHandle, type, (void *) &data, obj->pCallbackUser);
		break;
	case BSMP_CALLBACK_TYPE_CLOSE:
	case BSMP_CALLBACK_TYPE_STOP:
		while (1) {
			BSMP_CALLBACK_QUEUE *queue = &obj->tCallbackQueue[obj->iCallbackQueuePut];
			if (queue->nType <= BSMP_CALLBACK_TYPE_NULL) {
				break;
			}
			obj->pCallback (obj->bsmpHandle, queue->nType, (void *) &queue->dwData, obj->pCallbackUser);
			queue->nType = BSMP_CALLBACK_TYPE_NULL; /* clear */
			if (++obj->iCallbackQueuePut >= kCallbackQueues) {
				obj->iCallbackQueuePut = 0;
			}
		}
		obj->pCallback (obj->bsmpHandle, type, (void *) &data, obj->pCallbackUser);
		break;
	default:
		{
			BSMP_CALLBACK_QUEUE *queue = &obj->tCallbackQueue[obj->iCallbackQueueGet];
			queue->nType = type;
			queue->dwSample = sample + obj->dwCallbackDelaySample;
			queue->dwData = data;
			if (++obj->iCallbackQueueGet >= kCallbackQueues) {
				obj->iCallbackQueueGet = 0;
			}
		}
		break;
	}
#else /* BSMP_INCLUDE_CALLBACK_QUEUE */
	obj->pCallback (obj->bsmpHandle, type, (void *) &data, obj->pCallbackUser);
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
}

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
/*---------------------------------------------------------------------------*/
static void _setCallbackDelay
	(
	BSMPPlayer *obj,
	long delayMs
	)
{
	if (delayMs < ((long) kCallbackDelayMs * -1L)) {
		delayMs = (long) kCallbackDelayMs * -1L;
	}
	else if (delayMs > (long) kCallbackDelayMs) {
		delayMs = (long) kCallbackDelayMs;
	}

	{
		long ms = delayMs + (long) kCallbackDelayMs;
		ms -= 40L;
		obj->lCallbackDelayMs = delayMs;

		obj->dwCallbackDelaySample = (DWORD) ms * obj->m_wavOut.getSampleRate (&obj->m_wavOut) / 1000UL;
	}
}

/*---------------------------------------------------------------------------*/
static long _getCallbackDelay
	(
	BSMPPlayer *obj
	)
{
	return obj->lCallbackDelayMs;
}

/*---------------------------------------------------------------------------*/
static void _executeCallbackDelayed
	(
	BSMPPlayer *obj,
	DWORD sample
	)
{
	while (1) {
		BSMP_CALLBACK_QUEUE *queue = &obj->tCallbackQueue[obj->iCallbackQueuePut];
		if (queue->nType <= BSMP_CALLBACK_TYPE_NULL) {
			break;
		}
		if (queue->dwSample <= sample) {
			obj->pCallback (obj->bsmpHandle, queue->nType, (void *) &queue->dwData, obj->pCallbackUser);
			queue->nType = BSMP_CALLBACK_TYPE_NULL; /* clear */
			if (++obj->iCallbackQueuePut >= kCallbackQueues) {
				obj->iCallbackQueuePut = 0;
			}
		}
		else {
			return;
		}
	}
}
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

#pragma mark -
#ifndef BSL_WAV_OUT_VOLUME
/*---------------------------------------------------------------------------*/
static void _setVolume
	(
	BSMPPlayer *obj,
	int value
	)
{
	obj->iVolume = value;
	if (!obj->m_wavOut.isPlaying (&obj->m_wavOut)) obj->iLastVolume = value;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: volume = %+d\n"), obj->iVolume);
#endif /* BSL_DEBUG */
}
#endif /* BSL_WAV_OUT_VOLUME */

#pragma mark -
/*---------------------------------------------------------------------------*/
static void _setSpeed
	(
	BSMPPlayer *obj,
	int value
	)
{
	if (value >= 100) value = 99;
	obj->iSpeed = value;
#ifndef BSMP_INCLUDE_WAVE
	obj->fTickGenChild  = (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut) * (float) obj->dwTempo * (float) (100 - obj->iSpeed);
#endif /* BSMP_INCLUDE_WAVE */

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: speed = %+d\n"), obj->iSpeed);
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static void _setKey
	(
	BSMPPlayer *obj,
	int value
	)
{
	obj->iKey = value;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: key = %+d\n"), obj->iKey);
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static void _setTune
	(
	BSMPPlayer *obj,
	int value
	)
{
	obj->nTune = value;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: tune = %+d\n"), obj->nTune);
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static void _setGuide
	(
	BSMPPlayer *obj,
	int value
	)
{
	if (value > 2) {
		value = 2;
	}
	else if (value < -2) {
		value = -2;
	}
	obj->iGuide = value;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: guide = %+d\n"), obj->iGuide);
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static void _setGuideMainCh
	(
	BSMPPlayer *obj,
	int value
	)
{
	obj->iGuideMainCh = value;
	obj->byGuideMainValue = 100;
}

/*---------------------------------------------------------------------------*/
static void _setGuideMainValue
	(
	BSMPPlayer *obj,
	BYTE value
	)
{
	obj->byGuideMainValue = value;
}

/*---------------------------------------------------------------------------*/
static void _setGuideSubCh
	(
	BSMPPlayer *obj,
	int value
	)
{
	obj->iGuideSubCh = value;
	obj->byGuideSubValue = 100;
}

/*---------------------------------------------------------------------------*/
static void _setGuideSubValue
	(
	BSMPPlayer *obj,
	BYTE value
	)
{
	obj->byGuideSubValue = value;
}

/*---------------------------------------------------------------------------*/
static void _setTempo
	(
	BSMPPlayer *obj,
	DWORD tempo
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	,
	BOOL immediate,
	DWORD sample
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
	)
{
	obj->dwTempo = tempo;
	obj->fTickGenChild = (float) obj->m_wavOut.getSampleRate (&obj->m_wavOut) * (float) obj->dwTempo * (float) (100 - obj->iSpeed);

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	if (immediate) {
		obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_TEMPO, &obj->dwTempo, obj->pCallbackUser);
	}
	else {
		obj->executeCallback (obj, BSMP_CALLBACK_TYPE_TEMPO, obj->dwTempo, sample);
	}
#else /* BSMP_INCLUDE_CALLBACK_QUEUE */
	obj->pCallback (obj->bsmpHandle, BSMP_CALLBACK_TYPE_TEMPO, &obj->dwTempo, obj->pCallbackUser);
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
}

#pragma mark -
#ifdef BSL_MID_INCLUDE_EFFECT1
/*---------------------------------------------------------------------------*/
static void _setEffect1Switch
	(
	BSMPPlayer *obj,
	int state
	)
{
	obj->bEffect1 = state;
#ifdef BSL_DEBUG
	if (state) {
		bslTrace (_TEXT("bsmp: effect1 on\n"));
	}
	else {
		bslTrace (_TEXT("bsmp: effect1 off\n"));
	}
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static int _getEffect1Switch
	(
	BSMPPlayer *obj
	)
{
	return obj->bEffect1;
}

/*---------------------------------------------------------------------------*/
static int _effect1Available
	(
	BSMPPlayer *obj
	)
{
	if (obj->bsseHandle) {
		int state;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_REVERB_AVAILABLE, &state, sizeof (int));
		return state;
	}

	return FALSE;
}
#endif /* BSL_MID_INCLUDE_EFFECT1 */

#ifdef BSL_MID_INCLUDE_EFFECT3
/*---------------------------------------------------------------------------*/
static void _setEffect3Switch
	(
	BSMPPlayer *obj,
	int state
	)
{
	obj->bEffect3 = state;
#ifdef BSL_DEBUG
	if (state) {
		bslTrace (_TEXT("bsmp: effect3 on\n"));
	}
	else {
		bslTrace (_TEXT("bsmp: effect3 off\n"));
	}
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static int _getEffect3Switch
	(
	BSMPPlayer *obj
	)
{
	return obj->bEffect3;
}

/*---------------------------------------------------------------------------*/
static int _effect3Available
	(
	BSMPPlayer *obj
	)
{
	if (obj->bsseHandle) {
		int state;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_CHORUS_AVAILABLE, &state, sizeof (int));
		return state;
	}

	return FALSE;
}
#endif /* BSL_MID_INCLUDE_EFFECT3 */

#ifdef BSL_MID_INCLUDE_EFFECT4
/*---------------------------------------------------------------------------*/
static void _setEffect4Switch
	(
	BSMPPlayer *obj,
	int state
	)
{
	obj->bEffect4 = state;
#ifdef BSL_DEBUG
	if (state) {
		bslTrace (_TEXT("bsmp: effect4 on\n"));
	}
	else {
		bslTrace (_TEXT("bsmp: effect4 off\n"));
	}
#endif /* BSL_DEBUG */
}

/*---------------------------------------------------------------------------*/
static int _getEffect4Switch
	(
	BSMPPlayer *obj
	)
{
	return obj->bEffect4;
}

/*---------------------------------------------------------------------------*/
static int _effect4Available
	(
	BSMPPlayer *obj
	)
{
	if (obj->bsseHandle) {
		int state;
		obj->bsseFunc->ctrl (obj->bsseHandle, BSSE_CTRL_GET_DELAY_AVAILABLE, &state, sizeof (int));
		return state;
	}

	return FALSE;
}
#endif /* BSL_MID_INCLUDE_EFFECT4 */

/*---------------------------------------------------------------------------*/
static BSMP_ERR _bsseCtrl
	(
	BSMPPlayer *obj,
	BSMP_CTRL ctrl,
	void *data,
	int size
	)
{
	return (BSMP_ERR) obj->bsseFunc->ctrl (obj->bsseHandle, (BSSE_CTRL) ctrl, data, size);
}

#pragma mark - KY

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
static void _KY_setRhythmChangeType
	(
	BSMPPlayer *obj,
	int type
	)
{
	if (type < BSMP_KY_RHYTHM_TYPE_OFF || (BSMP_KY_RHYTHM_TYPE_OFF + BSMP_KY_RHYTHM_TYPES) < type) {
		return;
	}

	obj->KY_nRhythmChangeType = type;
#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: KY rhyrhm change type = %d\n"), type);
#endif /* BSL_DEBUG */
}

static int _KY_getRhythmChangeType
	(
	BSMPPlayer *obj
	)
{
	return obj->KY_nRhythmChangeType;
}

static BOOL _KY_isSubstitutedForRhythmChange
	(
	BYTE port,
	BYTE channel
	)
{
	switch (port) {
		case BSL_MID_PORT_A:
		case BSL_MID_PORT_B:
			switch (channel + 1) {
				case 2:
				case 10:
				case 11:
					return TRUE;
			}
			break;
	}
	
	return FALSE;
}

#endif

#pragma mark -
/*---------------------------------------------------------------------------*/
void bsmpPlayer
	(
	BSMPPlayer *obj,
	LPCTSTR libraryPath,
	char *libraryAddress,
	unsigned long librarySize
	)
{
	memset (obj, 0, sizeof (BSMPPlayer));

	/* bsmp */
	obj->bsmpHandle = NULL;
	obj->initialize = _initialize;

	/* wave output */
	bslWavRender (&obj->m_wavRender, __render, obj);
#if BSL_WINDOWS
	bslWavOutMmeThread (&obj->m_wavOut, __render, obj);
#elif BSL_MAC || BSL_IOS
	bslWavOutAudioQueue (&obj->m_wavOut, __render, obj);
#elif BSL_SIGMA
	bslWavOutSigmaSmp8670Thread (&obj->m_wavOut, __render, obj);
#elif BSL_LINUX
	bslWavOutAlsaThread (&obj->m_wavOut, __render, obj);
#elif BSL_ANDROID
	bslWavOutOpenSLES (&obj->m_wavOut, __render, obj);
#endif
	obj->getNumDrivers = _getNumDrivers;
	obj->getNumDevices = _getNumDevices;
	obj->getDriverName = _getDriverName;
	obj->getDeviceName = _getDeviceName;
	obj->open = _open;
	obj->close = _close;
	obj->start = _start;
	obj->stop = _stop;
	obj->stopped = _stopped;
	obj->seek = _seek;
    obj->seekWithTime = _seekWithTime;
	obj->setSampleRate = _setSampleRate;
	obj->setBlockSize = _setBlockSize;
	obj->output = _output;
#ifndef BSL_WAV_OUT_VOLUME
	obj->setVolume = _setVolume;
	#ifdef BSSYNTH_KT
	obj->iVolume = obj->iLastVolume = BSMP_KT_VOLUME_DEF;
	#else /* BSSYNTH_KT */
	obj->iVolume = obj->iLastVolume = BSMP_VOLUME_DEF;
	#endif /* BSSYNTH_KT */
    obj->byOrgVolume = kMid88SdefMstVol;
#endif /* BSL_WAV_OUT_VOLUME */

	/* bsse */
	obj->bsseHandle = NULL;
	if (libraryPath) {
		_tcsncpy_(obj->cDefaultLibraryPath, libraryPath, _MAX_PATH - 1);
	}
	else {
#if (_MSC_VER >= 1400) && (!defined _WIN32_WCE)
		_tcscpy_s (obj->cDefaultLibraryPath, _MAX_PATH, _TEXT(""));
#else
		_tcscpy (obj->cDefaultLibraryPath, _TEXT(""));
#endif
	}
	obj->pDefaultLibraryAddress = libraryAddress;
	obj->ulDefaultLibrarySize = librarySize;
	obj->setPoly = _setPoly;
	obj->getPoly = _getPoly;
#ifdef BSL_MID_INCLUDE_EFFECT1
	obj->setEffect1Switch = _setEffect1Switch;
	obj->getEffect1Switch = _getEffect1Switch;
	obj->effect1Available = _effect1Available;
#endif /* BSL_MID_INCLUDE_EFFECT1 */
#ifdef BSL_MID_INCLUDE_EFFECT3
	obj->setEffect3Switch = _setEffect3Switch;
	obj->getEffect3Switch = _getEffect3Switch;
	obj->effect3Available = _effect3Available;
#endif /* BSL_MID_INCLUDE_EFFECT3 */
#ifdef BSL_MID_INCLUDE_EFFECT4
	obj->setEffect4Switch = _setEffect4Switch;
	obj->getEffect4Switch = _getEffect4Switch;
	obj->effect4Available = _effect4Available;
#endif /* BSL_MID_INCLUDE_EFFECT4 */
	obj->bsseCtrl = _bsseCtrl;

	/* callback */
	obj->pCallback = obj->pCallbackBackup = _callback_dummy;
	obj->pCallbackUser = NULL;
	obj->setCallback = _setCallback;
	obj->executeCallback = _executeCallback;
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	obj->setCallbackDelay = _setCallbackDelay;
	obj->getCallbackDelay = _getCallbackDelay;
	obj->executeCallbackDelayed = _executeCallbackDelayed;
	obj->lCallbackDelayMs = 0L;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

	/* process */
	obj->chaseEvent = _chaseEvent;
	obj->getEvent = _getEvent;
	obj->pOutput = NULL;
#ifdef BSSYNTH_WTBL_ON_DEMAND
	obj->getEventOnDemand = _getEventOnDemand;
#endif /* BSSYNTH_WTBL_ON_DEMAND */

	/* player */
	bslFileMem (&obj->m_file);
	bslMidParser (&obj->m_midParser);
	obj->m_midParser.pCallbackUser = obj;
	obj->m_midParser.chaseEvent = __chaseEvent;
	obj->m_midParser.getEvent = __getEvent;
	obj->set = _set;
	obj->setMemory = _setMemory;
	obj->getTotalTick = _getTotalTick;
	obj->getTotalTime = _getTotalTime;
	obj->setSpeed = _setSpeed;
	obj->setKey = _setKey;
	obj->setTune = _setTune;
	obj->setGuide = _setGuide;
	obj->setGuideMainCh = _setGuideMainCh;
	obj->setGuideMainValue = _setGuideMainValue;
	obj->setGuideSubCh = _setGuideSubCh;
	obj->setGuideSubValue = _setGuideSubValue;
	obj->iSpeed = BSMP_SPEED_DEF;
	obj->iKey = obj->iLastKey = BSMP_KEY_DEF;
	obj->nTune = obj->nLastTune = BSMP_TUNE_DEF;
	obj->iGuide = obj->iLastGuide = BSMP_GUIDE_DEF;
	obj->iGuideMainCh = -1; /* ch4 */
	obj->iGuideSubCh = -1; /* off */

#ifdef BSSYNTH_88
	#if (defined BSSYNTH_SEGA)
	obj->portSelector.nPortSelectionMethod = BSMP_PORT_SELECTION_METHOD_S;
	obj->iGuideMainCh = 0; /* A01 */
	obj->iGuideSubCh = 30; /* B15*/
	#elif (defined BSSYNTH_KY)
	obj->iGuideMainCh = 3; /* A04 */
	obj->iGuideSubCh = 19; /* B04*/
	obj->portSelector.nPortSelectionMethod = BSMP_PORT_SELECTION_METHOD_K;
	#else
	obj->portSelector.nPortSelectionMethod = BSMP_PORT_SELECTION_METHOD_N;
	#endif
#endif /* BSSYNTH_88 */

#ifdef BSMP_EXPORT_WAVE
	obj->bounce = _bounce;
#endif /* BSMP_EXPORT_WAVE */

#ifdef BSMP_INCLUDE_WAVE
	crmpWaveManager (&obj->tWaveManager);
#endif /* BSMP_INCLUDE_WAVE */

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
  obj->KY_setRhythmChangeType = _KY_setRhythmChangeType;
  obj->KY_getRhythmChangeType = _KY_getRhythmChangeType;
  obj->KY_nRhythmChangeType = BSMP_KY_RHYTHM_TYPE_OFF;
	obj->KY_nRhythmChangeLastType = BSMP_KY_RHYTHM_TYPE_OFF;
#endif
}

/*---------------------------------------------------------------------------*/
void bsmpPlayerExit
	(
	BSMPPlayer *obj
	)
{
    tempoList_remove(&obj->pTempoList);

	obj->bsseFunc->setCallbackVolume (obj->bsseHandle, NULL, NULL);
#ifndef BSL_WAV_OUT_VOLUME
	obj->bsseFunc->setCallbackMasterVolume (obj->bsseHandle, NULL, NULL);
#endif /* BSL_WAV_OUT_VOLUME */
	obj->bsseFunc->setCallbackMasterKeySet (obj->bsseHandle, NULL, NULL);

	bslWavOutExit (&obj->m_wavOut);

	bslFileExit (&obj->m_file);
	bslMidParserExit (&obj->m_midParser);

	obj->bsseFunc->exit (obj->bsseHandle);

#ifdef BSMP_INCLUDE_WAVE
	if (obj->pOutput) {
		bslMemFree (obj->pOutput);
		obj->pOutput = NULL;
	}

	crmpWaveManagerExit (&obj->tWaveManager);
#endif /* BSMP_INCLUDE_WAVE */
}
