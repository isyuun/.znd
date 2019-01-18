/*
 *  BSLConf.h for Midi Player
 *
 *  Copyright (c) 2004-2013 bismark. All rights reserved.
 *
 */

#ifndef __INCBSLConfh
#define __INCBSLConfh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "target.h" /* defines which os/platform will be used */
#include "version.h" /* defines bssynth version */

/* defines */

/* -- bssynth -- */

#define BSMP_INCLUDE_CALLBACK_QUEUE

#if BSL_WINDOWSCE && (defined (_MIPS_) || defined (_SH3_) || defined (_SH4_) || defined (_X86_) /* || defined (_ARM_) || defined (ARMV4I) */)
#else
	#ifndef BSSYNTH_KT
		#define BSMP_EXPORT_WAVE
	#endif
#endif

/* -- wave -- */

#if !BSL_MAC && !BSL_IOS
	#define BSL_WAV_OUT_INCLUDE_THREAD
#endif
#define BSL_WAV_OUT_FADEOUT /* support fadeout on stop/pause */
#if (BSL_WINDOWS && !BSL_WINDOWSCE && (defined BSMD_EXPORTS)) || BSL_MAC || BSL_IOS || BSL_LINUX
	#define BSL_WAV_OUT_FADEOUT_ZERO_OVERLAP /* ASIO@Win, AudioQueue@Mac/iPhone */
#endif
#if BSL_WINDOWSCE
	#define BSL_WAV_OUT_VOLUME /* support wave driver volume */
#endif /* BSL_WINDOWSCE */

#ifdef BSMP_EXPORT_WAVE
	#define BSL_WAV_INCLUDE_FILE_TYPE_RIFF /* support Microsoft's RIFF audio file (wav) */
	#define BSL_WAV_INCLUDE_FILE_TYPE_AIFF /* support Apple's AIFF audio file (aiff/aif) */
#endif /* BSMP_EXPORT_WAVE */
#ifdef BSSYNTH_KT
	#define BSL_WAV_INCLUDE_FILE_TYPE_MP3 
	#define BSL_WAV_INCLUDE_CODEC_MP3_MAD
#endif // BSSYNTH_KT

#ifdef BSSYNTH_FLOAT
    #if (BSL_MAC || BSL_IOS || BSL_ANDROID)
        #define BSL_WAV_IO_FLOAT
    #endif
#endif
    
#ifdef BSSYNTH_AUDIO_INPUT
	#define BSL_WAV_OUT_AUDIO_UNIT_THROUGH
#endif /* BSSYNTH_AUDIO_INPUT */

enum {
#if (BSL_WINDOWSCE) && (!defined BSSYNTH_KT)
	BSL_WAV_SAMPLE_RATE = 22050, /* default sample rate */
	BSL_WAV_BLOCK_SIZE = 256,
#else
	BSL_WAV_SAMPLE_RATE = 44100, /* default sample rate */
	BSL_WAV_BLOCK_SIZE = 512,
#endif

#ifdef BSL_WAV_IO_FLOAT
	BSL_WAV_BITS_PER_SAMPLE = 32, /* i/o bits (float) */
#else /* BSL_WAV_IO_FLOAT */
	BSL_WAV_BITS_PER_SAMPLE = 16, /* i/o bits (short) */
#endif /* BSL_WAV_IO_FLOAT */

	BSL_WAV_CHANNELS = 2, /* i/o channel (stereo) */

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	BSL_WAV_BUFFERS = 43,
#else /* BSMP_INCLUDE_CALLBACK_QUEUE */
	BSL_WAV_BUFFERS = 16,
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */
#if BSL_ANDROID
    BSL_WAV_BUFFERS_MAX = 512,
#else
    BSL_WAV_BUFFERS_MAX = BSL_WAV_BUFFERS,
#endif
};

	#define BSL_WAV_CHANNELS_MAX 2

#ifdef BSSYNTH_KT
	#define BSMP_INCLUDE_WAVE
	#define BSL_EFT_PARAMETERS 10
	#define BSL_EFT_INCLUDE_WINDOW 
	#define BSL_EFT_WINDOW_SIZE 2048
	#define BSL_EFT_WINDOW_SIZE_MAX (BSL_EFT_WINDOW_SIZE * 2)
//	#define BSL_EFT_INCLUDE_FADER
	#define BSL_EFT_NO_FLOAT
	#define BSL_EFT_SAMPLE_RATE BSL_WAV_SAMPLE_RATE
	#define BSL_EFT_BLOCK_SIZE BSL_WAV_BLOCK_SIZE
#endif /* BSSYNTH_KT */

/* -- midi -- */

#define BSL_MID_INCLUDE_EFFECT1 /* support effect1 (reverb) */
#define BSL_MID_INCLUDE_EFFECT3 /* support effect3 (chorus) */
#if (defined BSSYNTH_88) || (defined BSSYNTH_BS)
	#define BSL_MID_INCLUDE_EFFECT4 /* support effect4 (delay) */
#endif

#define BSL_MID_INCLUDE_FILE_TYPE_SMF /* support smf */
#if BSL_WINDOWS
	/* #define BSL_MID_INCLUDE_FILE_TYPE_MCOMP * support smf encoded by M-compression */
#endif
/*
#define BSL_MID_INCLUDE_FILE_TYPE_SMAF / * support smaf  * /
#define BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA2
#define BSL_MID_INCLUDE_FILE_TYPE_SMAF_MA3
#define BSL_MID_INCLUDE_FILE_TYPE_MFi / * support mfi * /
#define BSL_MID_INCLUDE_FILE_TYPE_MFMP / * support mfmp * /
#define BSL_MID_INCLUDE_FILE_TYPE_MFMP / * support cmx * /
#define BSL_MID_INCLUDE_FILE_TYPE_MCDF / * support mcdf * /
*/

#if (defined BSL_DEBUG)
	#define BSL_MID_INCLUDE_TRACK_NAME
#endif
#define BSL_MID_INCLUDE_MIDICLOCK /* support midi clock generation for external synchronizing */

#if (defined BSSYNTH_88)
	#define BSSYNTH_MID_PORTS 2 /* modules */
#else
	#define BSSYNTH_MID_PORTS 1 /* modules */
#endif
#define BSL_MID_CHANNELS 16

#if (defined BSSYNTH_88)
#define BSL_MID_TRACKS 128
#else
#define BSL_MID_TRACKS 64
#endif

/* -- effect -- */

#ifndef BSSYNTH_FLOAT
    #define BSL_EFT_NO_FLOAT /* use integer internal process <- float process */
#endif
    
/* typedefs */

/* globals */

/* loclas */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLConfh */
