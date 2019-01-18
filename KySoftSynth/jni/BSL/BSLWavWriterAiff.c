/*
 *  BSLWavWriterAiff.c
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriterAiff.c
	@brief
	Wave File出力制御オブジェクト (AIFF)
*/

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_AIFF

#include "BSLFileSave.h"
#include "BSLWav.h"
#include "BSLWavWriterAiff.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
static void _storeFloat
	(
	BYTE * buffer, 
	DWORD value
	)
{
	DWORD count, mask = 0x40000000;

	memset (buffer, 0, 10);

	if (value <= 1) {
		buffer[0] = 0x3F;
		buffer[1] = 0xFF;
		buffer[2] = 0x80;
		return;
	}

	buffer[0] = 0x40;
	
	if (value >= mask) {
		buffer[1] = 0x1D;
		return;
	}
	
	for (count = 0; count <= 32; count ++) {
		if (value & mask) break;
		mask >>= 1;
	}
		
	value <<= count + 1UL;
	buffer[1] = (BYTE) (29UL - count);
	buffer[2] = (BYTE) ((value >> 24) & 0x000000FF);
	buffer[3] = (BYTE) ((value >> 16) & 0x000000FF);
	buffer[4] = (BYTE) ((value >> 8) & 0x000000FF);
	buffer[5] = (BYTE) ( value & 0x000000FF);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	コーデックタイプの設定を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param codec
	コーデックタイプ
	@retval
	BSLErr
*/
static BSLErr _setCodec
	(
	BSLWavWriter *obj,
	BSL_WAV_CODEC codec
	)
{
	if (codec != BSL_WAV_CODEC_PCM) {
		return BSL_ERR_WAV_SET_CODEC;
	}

	return _bslWavWriterSetCodec (obj, codec);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ビット精度の設定を行う非公開関数。
	@param obj
	制御オブジェクトのアドレス
	@param bitsPerSample
	ビット精度[bit]
*/
static BSLErr _setBitsPerSample
	(
	BSLWavWriter *obj,
	int bitsPerSample
	)
{
	BSLErr err = BSL_OK;

	switch (bitsPerSample) {
	case 8:
	case 16:
		break;
	default:
		err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
		break;
	}

	if (err == BSL_OK) {
		err = _bslWavWriterSetBitsPerSample (obj, bitsPerSample);
	}

	if (err == BSL_OK) {
		err = obj->setBitsPerSampleRendering (obj, bitsPerSample);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Unsignedの設定を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
*/
static BSLErr _setUnsigned
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	if (flag) {
		return BSL_ERR_WAV_SET_UNSIGNED;
	}

	return _bslWavWriterSetUnsigned (obj, flag);
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	Big Endianの設定を行う非公開関数
	@param obj
	制御オブジェクトのアドレス
	@param flag
	フラグ
	@retval
	BSLErr
*/
static BSLErr _setBigEndian
	(
	BSLWavWriter *obj,
	BOOL flag
	)
{
	if (!flag) {
		return BSL_ERR_WAV_SET_BIGENDIAN;
	}

	return _bslWavWriterSetBigEndian (obj, flag);
}

/*---------------------------------------------------------------------------*/
static BSLErr _save
	(
	BSLFileSave *file, 
	void *user, 
	void *userParam
	)
{
	BSLErr err = BSL_OK;
	BSLWavWriter *obj = (BSLWavWriter *) user;
	BSLWavRender *render = (BSLWavRender *) userParam;
	DWORD renderedFrames = 0UL;
	DWORD header[4];
	unsigned long mark;
	DWORD dw;
	short sh;

	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : ---------------------------------------\n"));
		bslTrace (_TEXT("WAV : aiff file save\n"));
#endif /* BSL_DEBUG */

		/* header */
		header[0] = FORM_MARKER;
		header[1] = 0UL; /* dummy */
		header[2] = AIFF_MARKER;
		err = file->write (file, header, 12UL);
	}

	/* COMM - 12 */
	if (err == BSL_OK) {
		header[0] = COMM_MARKER;
		header[1] = 18UL;
#ifndef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, header, 8UL);
	}

	if (err == BSL_OK) {
		sh = (short) obj->getChannels (obj);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&sh, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &sh, 2UL);
	}

	if (err == BSL_OK) {
		dw = 0UL; /* dummy */
		err = file->write (file, &dw, 4UL);
	}

	if (err == BSL_OK) {
		sh = (short) obj->getBitsPerSample (obj);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&sh, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &sh, 2UL);
	}

	if (err == BSL_OK) {
		BYTE data[10];
		_storeFloat (data, obj->getSampleRate (obj));
		err = file->write (file, data, 10UL);
	}

	/* SSND - 38 offset */
	if (err == BSL_OK) {
		header[0] = SSND_MARKER;
		header[1] = 8UL + (DWORD) obj->getBytesPerFrame (obj) * obj->getFrames (obj);
		header[2] = 0UL;
		header[3] = 0UL;
#ifndef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, header, 16UL);
	}

	/* data chunk - data */
	if (err == BSL_OK) {
		err = _bslWavWriterBounce (file, obj, render, &renderedFrames);
	}

#ifdef BSL_WAV_LOOPS
	if (err == BSL_OK) {
		/* loop */
		if (0 < obj->getLoopNum (obj)) {
			BSL_WAV_LOOP l;

			header[0] = INST_MARKER;
			header[1] = 20UL;
#ifndef BSL_BIGENDIAN
			bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, header, 8UL);

			if (err == BSL_OK) {
				char baseNote;
				obj->getLoop (obj, 0, &l);
				baseNote = (char) l.dwUnityNote;
				err = file->write (file, &baseNote, 1UL);
			}

			if (err == BSL_OK) {
				char detune = (char) l.dwFineTune;
				err = file->write (file, &detune, 1UL);
			}

			if (err == BSL_OK) {
				char data = 0x00;
				err = file->write (file, &data, 1UL);
			}

			if (err == BSL_OK) {
				char data = 0x7F;
				err = file->write (file, &data, 1UL);
			}

			if (err == BSL_OK) {
				char data = 0x00;
				err = file->write (file, &data, 1UL);
			}

			if (err == BSL_OK) {
				char data = 0x7F;
				err = file->write (file, &data, 1UL);
			}

			if (err == BSL_OK) {
				short data = 0;
				err = file->write (file, &data, 2UL);
			}

			/* sustain loop */
			if (err == BSL_OK) {
				short data = BSL_WAV_AIFF_FORWARD_LOOPING;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				short data = 0;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				short data = 1;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			/* release loop */
			if (err == BSL_OK) {
				short data = BSL_WAV_AIFF_NO_LOOPING;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				short data = 0;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				short data = 0;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			/* MARK chunk */
			if (err == BSL_OK) {
				header[0] = MARK_MARKER;
				header[1] = 34UL;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, header, 8UL);
			}

			if (err == BSL_OK) {
				short data = 2;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			/* marker #0 */
			if (err == BSL_OK) {
				short data = 0;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				DWORD data = l.dwStart;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 4UL);
			}

			if (err == BSL_OK) {
				BYTE data[10] = {0x08, 0x62, 0x65, 0x67, 0x20, 0x6C, 0x6F, 0x6F, 0x70, 0x00};
				err = file->write (file, &data, 10UL);
			}

			/* marker #1 */
			if (err == BSL_OK) {
				short data = 1;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 2UL);
			}

			if (err == BSL_OK) {
				DWORD data = l.dwEnd + 1UL;
#ifndef BSL_BIGENDIAN
				bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &data, 4UL);
			}

			if (err == BSL_OK) {
				BYTE data[10] = {0x08, 0x65, 0x6E, 0x64, 0x20, 0x6C, 0x6F, 0x6F, 0x70, 0x00};
				err = file->write (file, &data, 10UL);
			}
		}
	}
#endif /* BSL_WAV_LOOPS */

	/* rewrite parameter */
	if (err == BSL_OK) {
		mark = file->getPos (file);
		err = file->seek (file, 4L, BSL_FILE_SEEK_START);
	}

	if (err == BSL_OK) {
		header[1] = (DWORD) (mark - 8UL);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &header[1], 4UL);
	}

	if (err == BSL_OK) {
		err = file->seek (file, 22L, BSL_FILE_SEEK_START);
	}

	if (err == BSL_OK) {
		dw = renderedFrames;
#ifndef BSL_BIGENDIAN
		bslFileSwap (&dw, 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &dw, 4UL);
	}

	if (err == BSL_OK) {
		err = file->seek (file, 42L, BSL_FILE_SEEK_START);
	}

	if (err == BSL_OK) {
		header[1] = 8UL + (DWORD) obj->getBytesPerFrame (obj) * renderedFrames;
#ifndef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &header[1], 4UL);
	}

	return (err);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	AIFF用BSLWavWriter継承制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
*/
void bslWavWriterAiff
	(
	BSLWavWriter *obj
	)
{
	bslWavWriter (obj);
	obj->setType (obj, BSL_WAV_FILE_TYPE_AIFF);
	obj->setCodec = _setCodec;
	obj->setBitsPerSample = _setBitsPerSample;
	obj->getBitRate = _bslWavWriterGetBitRateLinear;
	obj->setUnsigned = _setUnsigned;
	obj->setBigEndian = _setBigEndian;
	obj->_save = _save;

	obj->setUnsigned (obj, FALSE);
	obj->setBigEndian (obj, TRUE);
}

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_AIFF */

