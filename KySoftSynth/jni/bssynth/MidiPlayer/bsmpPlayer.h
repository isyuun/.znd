/*
 *  bsmpPlayer.h
 *
 *  Copyright (c) 2002 - 2016 bismark. All rights reserved.
 *
 */

/*!
	@file bsmpPlayer.h
	@brief
	BSMPPlayer制御オブジェクト
*/

#ifndef __INCbsmpPlayerh
#define __INCbsmpPlayerh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLMidParser.h"
#include "BSLWavOut.h"
#include "SynthEngine/bsse.h"
#include "bsmp.h"

#ifdef BSMP_INCLUDE_WAVE
#include "crmpWaveManager.h"
#endif /* BSMP_INCLUDE_WAVE */

#include "tempoList.h"
#if (defined BSSYNTH_88)
#include "portSelection.h"
#endif

/* defines */

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	enum {
		kCallbackQueues  = 8192,
		kCallbackDelayMs = BSL_WAV_BLOCK_SIZE * BSL_WAV_BUFFERS * 1000 / BSL_WAV_SAMPLE_RATE
	};
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

/* typedefs */

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
typedef struct {
	BSMP_CALLBACK_TYPE nType;
	DWORD dwData;
	DWORD dwSample;
} BSMP_CALLBACK_QUEUE;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

typedef struct BSMPPlayer
{
	/* bsmp */
	void *bsmpHandle;
	BSMP_ERR (*initialize) (struct BSMPPlayer *obj, const unsigned char *key, void *target);

	/* wave output */
	BSLWavOut m_wavOut;
	BSLWavRender m_wavRender;
	int (*getNumDrivers) (struct BSMPPlayer *obj);
	int (*getNumDevices) (struct BSMPPlayer *obj, LPCTSTR driver);
	LPCTSTR (*getDriverName) (struct BSMPPlayer *obj, int index);
	LPCTSTR (*getDeviceName) (struct BSMPPlayer *obj, LPCTSTR driver, int index);
	BSLErr (*open) (struct BSMPPlayer *obj, LPCTSTR driver, LPCTSTR device);
	BSLErr (*close) (struct BSMPPlayer *obj);
	BSLErr (*start) (struct BSMPPlayer *obj);
	BSLErr (*stop) (struct BSMPPlayer *obj);
	void (*stopped) (struct BSMPPlayer *obj, BSLErr err);
	BSLErr (*seek) (struct BSMPPlayer *obj, DWORD tick);
    BSLErr (*seekWithTime) (struct BSMPPlayer *obj, float time);
	BOOL bSeek;
	DWORD dwTickToSeek;
	BSLErr (*setSampleRate) (struct BSMPPlayer *obj, int sampleRate);
	BSLErr (*setBlockSize) (struct BSMPPlayer *obj, long blockSize);
	BSL_WAV_RENDER_STATE (*output) (struct BSMPPlayer *obj, BSLWavIOData *data, long sampleFrames
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
		, long *readFrames = NULL
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
		);
#ifndef BSL_WAV_OUT_VOLUME
	void (*setVolume) (struct BSMPPlayer *obj, int value);
	int iVolume;
	int iLastVolume;
	BYTE byOrgVolume;
#endif /* BSL_WAV_OUT_VOLUME */

	/* bsse */
	BSSE_HANDLE bsseHandle;
	BSSE_FUNC *bsseFunc;
	TCHAR cDefaultLibraryPath[_MAX_PATH];
	char *pDefaultLibraryAddress;
	unsigned long ulDefaultLibrarySize;
	BSLErr (*setPoly) (struct BSMPPlayer *obj, int poly);
	int (*getPoly) (struct BSMPPlayer *obj);
#ifdef BSL_MID_INCLUDE_EFFECT1
	void (*setEffect1Switch) (struct BSMPPlayer *obj, int state);
	int (*getEffect1Switch) (struct BSMPPlayer *obj);
	int (*effect1Available) (struct BSMPPlayer *obj);
	int bEffect1;
	int bEffect1Last;
#endif /* BSL_MID_INCLUDE_EFFECT1 */
#ifdef BSL_MID_INCLUDE_EFFECT3
	void (*setEffect3Switch) (struct BSMPPlayer *obj, int state);
	int (*getEffect3Switch) (struct BSMPPlayer *obj);
	int (*effect3Available) (struct BSMPPlayer *obj);
	int bEffect3;
	int bEffect3Last;
#endif /* BSL_MID_INCLUDE_EFFECT3 */
#ifdef BSL_MID_INCLUDE_EFFECT4
	void (*setEffect4Switch) (struct BSMPPlayer *obj, int state);
	int (*getEffect4Switch) (struct BSMPPlayer *obj);
	int (*effect4Available) (struct BSMPPlayer *obj);
	int bEffect4;
	int bEffect4Last;
#endif /* BSL_MID_INCLUDE_EFFECT4 */
	BSMP_ERR (*bsseCtrl) (struct BSMPPlayer *obj, BSMP_CTRL ctrl, void *data, int size);

	/* callback */
	void (*setCallback) (struct BSMPPlayer *obj, BSMP_CALLBACK callback, void *user);
	void (*executeCallback) (struct BSMPPlayer *obj, BSMP_CALLBACK_TYPE type, DWORD data
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
		, DWORD sample
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
	);
	BSMP_CALLBACK pCallback;
	BSMP_CALLBACK pCallbackBackup;
	void *pCallbackUser;
#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	void (*setCallbackDelay) (struct BSMPPlayer *obj, long delayMs);
	long (*getCallbackDelay) (struct BSMPPlayer *obj);
	void (*executeCallbackDelayed) (struct BSMPPlayer *obj, DWORD tick);
	BSMP_CALLBACK_QUEUE tCallbackQueue[kCallbackQueues];
	DWORD dwTotalSamples;
	DWORD dwCallbackDelaySample;
	long lCallbackDelayMs;
	int iCallbackQueueGet;
	int iCallbackQueuePut;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

	/* process */
	BSL_MID_EVENT_PROC (*chaseEvent) (struct BSMPPlayer *obj, BSLMidEvent *event);  
	BSL_MID_EVENT_PROC (*getEvent) (struct BSMPPlayer *obj, BSLMidEvent *event);  
#ifdef BSMP_INCLUDE_WAVE
	BSLWavIOData32 *pOutput;
#else /* BSMP_INCLUDE_WAVE */
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID) && !(defined BSL_EFT_NO_FLOAT) && (defined BSL_WAV_IO_FLOAT)
    BSLWavIOData *pOutput;
    #else
    BSLWavIOData16 *pOutput;
    #endif
#endif /* BSMP_INCLUDE_WAVE */
	long lSampleFrames;
	long lRemainedSamples;
	long lBufferedSamples;

	/* file */
	BSLFile m_file;
	BSLMidParser m_midParser;
	DWORD dwTotaltick;
	DWORD dwTotaltime;
	struct BSSYNTH_TEMPO *pTempoList;
	BSLErr (*set) (struct BSMPPlayer *obj, LPCTSTR file);
	BSLErr (*setMemory) (struct BSMPPlayer *obj, char *data, unsigned long size);
	DWORD (*getTotalTick) (struct BSMPPlayer *obj);
	DWORD (*getTotalTime) (struct BSMPPlayer *obj);

	/* player */
	void (*setSpeed) (struct BSMPPlayer *obj, int value);
	void (*setKey) (struct BSMPPlayer *obj, int value);
	void (*setTune) (struct BSMPPlayer *obj, int value);
   	void (*setGuide) (struct BSMPPlayer *obj, int value);
	void (*setGuideMainCh) (struct BSMPPlayer *obj, int value);
	void (*setGuideMainValue) (struct BSMPPlayer *obj, BYTE value);
	void (*setGuideSubCh) (struct BSMPPlayer *obj, int value);
	void (*setGuideSubValue) (struct BSMPPlayer *obj, BYTE value);
	int iSpeed;
#ifdef BSMP_INCLUDE_WAVE
	int iLastSpeed;
#endif /* BSMP_INCLUDE_WAVE */
	int iKey;
	int iLastKey;
	int nTune;
	int nLastTune;
	int iGuide;
	int iLastGuide;
	int iGuideMainCh;
	int iGuideSubCh;
	BYTE byGuideMainValue;
	BYTE byGuideSubValue;
	DWORD dwTempo;
	DWORD dwWritetick;
	long lEndMargin;
	float fTickGenMother;
	float fTickGenChild;
	float fTickGenRemainder;

#ifdef BSMP_EXPORT_WAVE
	BSLErr (*bounce) (struct BSMPPlayer *obj, LPCTSTR file, BSMP_WAVE_FILE type, int (*updater) (int percent, void *updateParam), void *updateParam);
#endif /* BSMP_EXPORT_WAVE */

#if (defined BSSYNTH_88)
  BSSYNTH_PORT_SELECTOR portSelector;
	float fSegaAbsoluteTimeForFirstEvent;
#endif
  
#ifdef BSSYNTH_KY
  void (*KY_setRhythmChangeType) (struct BSMPPlayer *obj, int type);
  int (*KY_getRhythmChangeType) (struct BSMPPlayer *obj);
  int KY_nRhythmChangeType;
  int KY_nRhythmChangeLastType;
#endif /* BSSYNTH_KY */

#ifdef BSSYNTH_WTBL_ON_DEMAND
	BSL_MID_EVENT_PROC (*getEventOnDemand) (struct BSMPPlayer *obj, BSLMidEvent *event);  
#endif /* BSSYNTH_WTBL_ON_DEMAND */

#ifdef BSMP_INCLUDE_WAVE
	CRMPWaveManager tWaveManager;
#endif /* BSMP_INCLUDE_WAVE */
} BSMPPlayer;

/* function decralations */

void bsmpPlayer (BSMPPlayer *obj, LPCTSTR libraryPath, char *libraryAddress, unsigned long librarySize);
void bsmpPlayerExit (BSMPPlayer *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCbsmpPlayerh */
