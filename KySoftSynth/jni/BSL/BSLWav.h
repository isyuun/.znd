/*
 *  BSLWav.h
 *
 *  Copyright (c) 2004-2009 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWav.h
	@brief
	Wave基本定義
*/

#ifndef __INCBSLWavh
#define __INCBSLWavh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

/* defines */

#if BSL_WINDOWS
	#define BSL_WAV_IO_UNSIGNED_8BIT
#endif /* BSL_WINDOWS */

#define BSL_WAV_LEFT 0
#define BSL_WAV_RIGHT 1
#define BSL_WAV_MONO 1
#define BSL_WAV_STEREO 2

#ifndef BSL_WAV_CHANNELS_MAX
	#define BSL_WAV_CHANNELS_MAX BSL_WAV_STEREO
#endif /* BSL_WAV_CHANNELS_MAX */

#ifndef BSL_WAV_BITS_PER_SAMPLE_MAX
	#define BSL_WAV_BITS_PER_SAMPLE_MAX 16
#endif /* BSL_WAV_BITS_PER_SAMPLE_MAX */
#define BSL_WAV_BYTE_PER_SAMPLE_MAX (BSL_WAV_BITS_PER_SAMPLE_MAX / 8)

/*! ファイルタイプ一覧 */
typedef enum {
	BSL_WAV_FILE_TYPE_NONE = -1,
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_RIFF
	/*! RIFF */
	BSL_WAV_FILE_TYPE_RIFF,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_RIFF */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_AIFF
	/*! AIFF */
	BSL_WAV_FILE_TYPE_AIFF,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_AIFF */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_RAW
	/*! Raw File */
	BSL_WAV_FILE_TYPE_RAW,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_RAW */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_OV
	/*! Ogg Vorvis */
	BSL_WAV_FILE_TYPE_OV,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_OV */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MP3
	/*! MP3 */
	BSL_WAV_FILE_TYPE_MP3,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MP3 */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_G726
		/*! G721 = G726 (32kbps/4bit) ADPCM */
		BSL_WAV_FILE_TYPE_G726,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_G726 */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_VOX
	/*! Vox / Dialogic ADPCM */
	BSL_WAV_FILE_TYPE_VOX,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_VOX */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_SMAF
	#ifdef BSL_WAV_INCLUDE_SMAF_MA2
	/*! SMAF-MA2 (ADPCM) */
	BSL_WAV_FILE_TYPE_SMAF_MA2,
	#endif /* BSL_WAV_INCLUDE_SMAF_MA2 */
	#ifdef BSL_WAV_INCLUDE_SMAF_MA3
	/*! SMAF-MA3 (ADPCM) */
	BSL_WAV_FILE_TYPE_SMAF_MA3,
	#endif /* BSL_WAV_INCLUDE_SMAF_MA3 */
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_SMAF */

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi5
	BSL_WAV_FILE_TYPE_MFi5,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi5 */

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFi
	/*! MFi (ADPCM) */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_N505i
	BSL_WAV_FILE_TYPE_MFI_N505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_N505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO505i
	BSL_WAV_FILE_TYPE_MFI_SO505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH505i
	BSL_WAV_FILE_TYPE_MFI_SH505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_F505i
	BSL_WAV_FILE_TYPE_MFI_F505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_F505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_P900i
	BSL_WAV_FILE_TYPE_MFI_P900i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_P900i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_P505i
	BSL_WAV_FILE_TYPE_MFI_P505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_P505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_D505i
	BSL_WAV_FILE_TYPE_MFI_D505i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_D505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH505iS
	BSL_WAV_FILE_TYPE_MFI_SH505iS,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH505i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO505iS
	BSL_WAV_FILE_TYPE_MFI_SO505iS,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO505iS */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_N600i
	BSL_WAV_FILE_TYPE_MFI_N600i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_N600i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_L600i
	BSL_WAV_FILE_TYPE_MFI_L600i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_L600i */

	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_N901iC
	BSL_WAV_FILE_TYPE_MFI_N901iC,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_N901iC */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_F901iC
	BSL_WAV_FILE_TYPE_MFI_F901iC,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_F901iC */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_D901i
	BSL_WAV_FILE_TYPE_MFI_D901i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_D901i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_P901i
	BSL_WAV_FILE_TYPE_MFI_P901i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_P901i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SA700iS
	BSL_WAV_FILE_TYPE_MFI_SA700iS,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SA700iS */	
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH901iC
	BSL_WAV_FILE_TYPE_MFI_SH901iC,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SH901i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO902i
	BSL_WAV_FILE_TYPE_MFI_SO902i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SO902i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_SA702i
	BSL_WAV_FILE_TYPE_MFI_SA702i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_SA702i */
	#ifdef BSL_WAV_INCLUDE_FILE_TYPE_MFI_MT700i
	BSL_WAV_FILE_TYPE_MFI_MT700i,
	#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFI_MT700i */
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_MFi */

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_OKI
	/*! OKI (.adp)*/
	BSL_WAV_FILE_TYPE_OKI,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_OKI */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_OKI_PCM
	/*! OKI (.pcm) */
	BSL_WAV_FILE_TYPE_OKI_PCM,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_OKI_PCM */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_OKI_NON_LINEAR_PCM
	/*! OKI Non-Linear PCM */
	BSL_WAV_FILE_TYPE_OKI_NON_LINEAR_PCM,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_OKI_NON_LINEAR_PCM */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_OKI_HQ_ADPCM
	/*! OKI HQ ADPCM*/
	BSL_WAV_FILE_TYPE_OKI_HQ_ADPCM,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_OKI_HQ_ADPCM */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_YAMAHA
	/*! YAMAHA */
	BSL_WAV_FILE_TYPE_YAMAHA,
	BSL_WAV_FILE_TYPE_YAMAHA_Emu,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_YAMAHA */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_ROHM
	/*! ROHM */
	BSL_WAV_FILE_TYPE_ROHM,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_ROHM */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_FUETREK
	/*! FUETREK */
	BSL_WAV_FILE_TYPE_FUETREK,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_FUETREK */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_IMA
	/*! IMA */
	BSL_WAV_FILE_TYPE_IMA,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_IMA */
#ifdef BSL_WAV_INCLUDE_FILE_TYPE_QUALCOMM
	/*! QUALCOMM */
	BSL_WAV_FILE_TYPE_QUALCOMM,
#endif /* BSL_WAV_INCLUDE_FILE_TYPE_QUALCOMM */
} BSL_WAV_FILE_TYPE;

/*! コーデックタイプ一覧 */
typedef enum
{
	/*! リニア */
	BSL_WAV_CODEC_PCM,
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
	/*! A-Law */
	BSL_WAV_CODEC_A_LAW,
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
	/*! u-Law */
	BSL_WAV_CODEC_U_LAW,
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
	/*! MP3 */
#ifdef BSL_WAV_INCLUDE_CODEC_MP3_LAME
	BSL_WAV_CODEC_MP3_LAME,
#endif /* BSL_WAV_INCLUDE_CODEC_MP3_LAME */
#ifdef  BSL_WAV_INCLUDE_CODEC_MP3_MAD
	BSL_WAV_CODEC_MP3_MAD,
#endif /* BSL_WAV_INCLUDE_CODEC_MP3_MAD */
#ifdef  BSL_WAV_INCLUDE_CODEC_MP3_MPG123
	BSL_WAV_CODEC_MP3_MPG123,
#endif /* BSL_WAV_INCLUDE_CODEC_MP3_MPG123 */
#ifdef  BSL_WAV_INCLUDE_CODEC_OV
	/*! Ogg Vorbis */
	BSL_WAV_CODEC_OV,
#endif /* BSL_WAV_INCLUDE_CODEC_OV */
#ifdef  BSL_WAV_INCLUDE_CODEC_YAMAHA
	/*! YAMAHA ADPCM */
	BSL_WAV_CODEC_YAMAHA,
#endif /* BSL_WAV_INCLUDE_CODEC_YAMAHA */
#ifdef  BSL_WAV_INCLUDE_CODEC_YAMAHA_EMU
	/*! YAMAHA ADPCM */
	BSL_WAV_CODEC_YAMAHA_EMU,
#endif /* BSL_WAV_INCLUDE_CODEC_YAMAHA_EMU */
#ifdef  BSL_WAV_INCLUDE_CODEC_ROHM
	/*! ROHM ADPCM */
	BSL_WAV_CODEC_ROHM,
#endif /* BSL_WAV_INCLUDE_CODEC_ROHM */
#ifdef  BSL_WAV_INCLUDE_CODEC_FUETREK
	/*! FUETREK ADPCM */
	BSL_WAV_CODEC_FUETREK,
#endif /* BSL_WAV_INCLUDE_CODEC_FUETREK */
#ifdef  BSL_WAV_INCLUDE_CODEC_IMA
	/*! IMA ADPCM */
	BSL_WAV_CODEC_IMA,
#endif /* BSL_WAV_INCLUDE_CODEC_IMA */
#ifdef  BSL_WAV_INCLUDE_CODEC_QUALCOMM
	/*! QUALCOMM ADPCM */
	BSL_WAV_CODEC_QUALCOMM,
#endif /* BSL_WAV_INCLUDE_CODEC_QUALCOMM */
#ifdef BSL_WAV_INCLUDE_CODEC_G726
	/*! G721 = G726 (32kbps/4bit) ADPCM */
	BSL_WAV_CODEC_G726,
#endif /* BSL_WAV_INCLUDE_CODEC_G726 */
#ifdef BSL_WAV_INCLUDE_CODEC_VOX 
	/*! VOX ADPCM */
	BSL_WAV_CODEC_VOX,
#endif /* BSL_WAV_INCLUDE_CODEC_VOX */
#ifdef BSL_WAV_INCLUDE_CODEC_OKI
	/*! OKI ADPCM */
	BSL_WAV_CODEC_OKI,
	BSL_WAV_CODEC_OKI2,
#endif /* BSL_WAV_INCLUDE_CODEC_OKI */
#ifdef BSL_WAV_INCLUDE_CODEC_OKI_NON_LINEAR_PCM
	/*! OKI Non-Linear PCM */
	BSL_WAV_CODEC_OKI_NON_LINEAR_PCM,
#endif /* BSL_WAV_INCLUDE_CODEC_OKI */
#ifdef BSL_WAV_INCLUDE_CODEC_OKI_HQ_ADPCM
	/*! OKI HQ ADPCM */
	BSL_WAV_CODEC_OKI_HQ_ADPCM,
#endif /* BSL_WAV_INCLUDE_CODEC_OKI_HQ_ADPCM */
} BSL_WAV_CODEC;

enum {
	BSL_WAV_LOOP_TYPE_FORWARD = 0, /* normal */
	BSL_WAV_LOOP_TYPE_ALTERNATING, /* forward/backward, also known as Ping Pong */
	BSL_WAV_LOOP_TYPE_BACKWARD, /* reverse */
	BSL_WAV_LOOP_TYPE_SAMPLER_SPECIFIC = 32, /* 32 - 0xFFFFFFFF, defined by manufacturer */
};

/* typedefs */

#ifdef BSL_WAV_IO_FLOAT
	typedef float BSLWavIOData;
#else /* BSL_WAV_IO_FLOAT */
	typedef void  BSLWavIOData;
#endif /* BSL_WAV_IO_FLOAT */
#if __LP64__
    typedef int BSLWavIOData32;
#else
    typedef long BSLWavIOData32;
#endif
typedef short BSLWavIOData16;
typedef char BSLWavIOData08S;
typedef BYTE BSLWavIOData08U;
#ifdef BSL_WAV_IO_UNSIGNED_8BIT
	typedef BSLWavIOData08U  BSLWavIOData08;
#else /* BSL_WAV_IO_UNSIGNED_8BIT */
	typedef BSLWavIOData08S  BSLWavIOData08;
#endif /* BSL_WAV_IO_UNSIGNED_8BIT */

#ifndef BSL_WAV_FILE_TITLE_SIZE
	/*! File Tytle文字数 */
	#define BSL_WAV_FILE_TITLE_SIZE 128
#endif

#ifndef BSL_WAV_FILE_COPYRIGHT_SIZE
	/*! File Copyright文字数 */
	#define BSL_WAV_FILE_COPYRIGHT_SIZE 128
#endif

#ifdef BSL_WAV_LOOPS
typedef struct
{
	DWORD dwUnityNote;
	DWORD dwType;
	DWORD dwStart;
	DWORD dwEnd;
	DWORD dwFineTune;
} BSL_WAV_LOOP;
#endif /* BSL_WAV_LOOPS */

/* globals */

/* locals */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLWavh */
