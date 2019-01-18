/*
 *  BSLWavWriterRiff.c
 *
 *  Copyright (c) 2004-2012 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavWriterRiff.c
	@brief
	Wave File出力制御オブジェクト (RIFF)
*/

/* includes */

#include "BSL.h"

#ifdef BSL_WAV_INCLUDE_FILE_TYPE_RIFF

#include "BSLMem.h"
#include "BSLFileSave.h"
#include "BSLWav.h"
#include "BSLWavWriterRiff.h"

#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
#include "BSLWavEncALaw.h"
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
#include "BSLWavEncULaw.h"
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


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
	switch (codec) {
	case BSL_WAV_CODEC_PCM:
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
	case BSL_WAV_CODEC_A_LAW:
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
	case BSL_WAV_CODEC_U_LAW:
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		return _bslWavWriterSetCodec (obj, codec);
	default:
		return BSL_ERR_WAV_SET_CODEC;
	}
}

/*---------------------------------------------------------------------------*/
/*!
	@brief
	ビット精度の設定を行う非公開関数。
	8[bit]の場合はunsigneフラグを自動的にONにする。
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

	if (err == BSL_OK) {
		err = _bslWavWriterSetBitsPerSample (obj, bitsPerSample);
	}

	if (err == BSL_OK) {
		switch (obj->getCodec (obj)) {
		case BSL_WAV_CODEC_PCM:
			switch (bitsPerSample) {
			case 8:
				_bslWavWriterSetUnsigned (obj, TRUE);
				break;
			case 16:
			case 32:
				_bslWavWriterSetUnsigned (obj, FALSE);
				break;
			default:
				err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
				break;
			}
			if (err == BSL_OK) {
				err = obj->setBitsPerSampleRendering (obj, bitsPerSample);
			}
			break;
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
		case BSL_WAV_CODEC_A_LAW:
			if (bitsPerSample != 8) {
				err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
			}
			if (err == BSL_OK) {
				err = obj->setBitsPerSampleRendering (obj, BSL_WAV_ENC_A_LAW_BIT_PER_SAMPLE);
			}
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
		case BSL_WAV_CODEC_U_LAW:
			if (bitsPerSample != 8) {
				err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
			}
			if (err == BSL_OK) {
				err = obj->setBitsPerSampleRendering (obj, BSL_WAV_ENC_U_LAW_BIT_PER_SAMPLE);
			}
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		default:
			break;
		}
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _setBitsPerSampleRendering
	(
	BSLWavWriter *obj,
	int bitsPerSampleRendering
	)
{
	BSLErr err = BSL_OK;

	switch (obj->getCodec (obj)) {
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
	case BSL_WAV_CODEC_A_LAW:
		if (bitsPerSampleRendering != BSL_WAV_ENC_A_LAW_BIT_PER_SAMPLE) {
			err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
		}
		break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
	case BSL_WAV_CODEC_U_LAW:
		if (bitsPerSampleRendering != BSL_WAV_ENC_U_LAW_BIT_PER_SAMPLE) {
			err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
		}
		break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
	default:
	case BSL_WAV_CODEC_PCM:
		if (obj->getBitsPerSample (obj) != bitsPerSampleRendering) {
			err = BSL_ERR_WAV_SET_BITS_PER_SAMPLE;
		}
		break;
	}

	if (err == BSL_OK) {
		err = _bslWavWriterSetBitsPerSampleRendering (obj, bitsPerSampleRendering);
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
	if (obj->getCodec (obj) == BSL_WAV_CODEC_PCM) {
		if (8 < obj->getBitsPerSample (obj)) {
			if (flag) {
				return BSL_ERR_WAV_SET_UNSIGNED;
			}
		}
		else {
			if (!flag) {
				return BSL_ERR_WAV_SET_UNSIGNED;
			}
		}
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
	if (obj->getCodec (obj) == BSL_WAV_CODEC_PCM) {
		if (flag) {
			return BSL_ERR_WAV_SET_BIGENDIAN;
		}
	}

	return _bslWavWriterSetBigEndian (obj, flag);
}

#if (defined BSL_WAV_INCLUDE_CODEC_A_LAW) || (defined BSL_WAV_INCLUDE_CODEC_U_LAW)
/*---------------------------------------------------------------------------*/
/*!
	@brief
	PCMコーデック以外での出力処理を行う非公開関数
	@param file
	ファイル出力制御オブジェクト(BSLFileSave)のアドレス
	@param obj
	制御オブジェクトのアドレス
	@param render
	Wave Rendering制御オブジェクト(BSLWavRender)のアドレス
	@param renderedFrames
	処理フレーム数
	@param encoder
	Waveエンコーダ制御オブジェクト(BSLWavEnc)のアドレス
	@retval
	BSLErr
*/
static BSLErr _bounce
	(
	BSLFileSave *file,
	BSLWavWriter *obj,
	BSLWavRender *render,
	DWORD *renderedFrames
	)
{
	long blockSize;
	DWORD frames = 0UL;
	BOOL more = TRUE;
	BSLWavEnc encoder;
	BSLErr err = BSL_OK;

	memset (&encoder, 0, sizeof (BSLWavEnc));

	if (err == BSL_OK) {
		if (!render) {
			err = BSL_ERR_UNDEFINED;
		}
	}

	if (err == BSL_OK) {
		switch (obj->getCodec (obj)) {
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
		case BSL_WAV_CODEC_A_LAW:
			bslWavEncALaw (&encoder);
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
		case BSL_WAV_CODEC_U_LAW:
			bslWavEncULaw (&encoder);
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		case BSL_WAV_CODEC_PCM:
		default:
			err = BSL_ERR_UNDEFINED;
			break;
		}
	}

	if (err == BSL_OK) {
		err = encoder.initialize (&encoder);
	}

	if (err == BSL_OK) {
		err = encoder.setBitsPerSample (&encoder, obj->getBitsPerSample (obj));
	}

	if (err == BSL_OK) {
		err = encoder.setChannels (&encoder, obj->getChannels (obj));
	}

	if (err == BSL_OK) {
		blockSize = render->getBlockSize (render);

		while (more && err == BSL_OK) {
#ifdef BSL_WAV_OUT_ACCURATE_SAMPLES
			long readFrames;
			BSL_WAV_RENDER_STATE state = render->render (render, render->getBuffer (render), blockSize, &readFrames);
#else /* BSL_WAV_OUT_ACCURATE_SAMPLES */
			long readFrames = blockSize;
			BSL_WAV_RENDER_STATE state = render->render (render, render->getBuffer (render), blockSize);
#endif /* BSL_WAV_OUT_ACCURATE_SAMPLES */
			frames += readFrames;
			switch (state) {
			case BSL_WAV_RENDER_STATE_FULL:
			case BSL_WAV_RENDER_STATE_COMPLETED:
				/* get stream buffer */
				{
					DWORD adpcmsize = encoder.getStreamSize (&encoder, NULL, readFrames);
					if (obj->dwInternalCacheSize < adpcmsize || !obj->pInternalCache) {
						if (obj->pInternalCache) {
							bslMemFree (obj->pInternalCache);
							obj->pInternalCache = NULL;
							obj->dwInternalCacheSize = 0UL;
						}
						obj->pInternalCache = (char *) bslMemAlloc (adpcmsize);
						if (obj->pInternalCache == NULL) {
							err = BSL_ERR_MEM_ALLOC;
							return err;
						}
						obj->dwInternalCacheSize = adpcmsize;
					}

					/* encode */
					if ((err = encoder.encode (&encoder, render->getBuffer (render), obj->pInternalCache, readFrames, &adpcmsize)) != BSL_OK) {
						return err;
					}

					/* write to file */
					if (file) {
						if ((err = file->write (file, obj->pInternalCache, adpcmsize)) != BSL_OK) {
							return err;
						}
					}
				}
				if (state == BSL_WAV_RENDER_STATE_COMPLETED) {
					more = FALSE;
				}
				break;
			case BSL_WAV_RENDER_STATE_ERROR:
			default:
				more = FALSE;
				err = BSL_ERR_UNDEFINED;
			}

			/* updater */
			if (obj->pUpdater) {
				int percent;
				DWORD fullframes = obj->getFrames (obj);
				if (0UL < fullframes) {
					percent = (int) (frames * 100 / fullframes);
				}
				else {
					percent = (int) (frames * 100 / (5 * 60 * obj->getSampleRate (obj)));
				}
				if (100 < percent) {
					percent = 100;
				}
				if (obj->pUpdater (percent, obj->pUpdateParam) != 0) {
					more = FALSE;
					err = BSL_ERR_CANCELED;
				}
			}
		}
	}

	if (renderedFrames) {
		*renderedFrames = frames;
	}

	bslWavEncExit (&encoder);

	return err;
}
#endif

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
	unsigned long marker;
	BSLWavRender *render = (BSLWavRender *) userParam;
	DWORD renderedFrames = 0UL;

	/* RIFF header */
	if (err == BSL_OK) {
		DWORD header[3];
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : ---------------------------------------\n"));
		bslTrace (_TEXT("WAV : riff file save\n"));
#endif /* BSL_DEBUG */

		header[0] = RIFF_MARKER;
		header[1] = 0UL; /* dummy */
		header[2] = WAVE_MARKER;
		err = file->write (file, header, 12UL);
	}

	/* fmt chunk header */
	if (err == BSL_OK) {
		DWORD header[2];
		header[0] = fmt__MARKER;
		header[1] = obj->getCodec (obj) == BSL_WAV_CODEC_PCM ? 16UL : 18UL;
#ifdef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, header, 8UL);
	}

	/* fmt chunk - format */
	if (err == BSL_OK) {
		WORD data = BSL_WAV_FILE_RIFF_FORMAT_PCM;
		switch (obj->getCodec (obj)) {
		case BSL_WAV_CODEC_PCM:
			data = BSL_WAV_FILE_RIFF_FORMAT_PCM;
			break;
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
		case BSL_WAV_CODEC_A_LAW:
			data = BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ALAW;
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
		case BSL_WAV_CODEC_U_LAW:
			data = BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ULAW;
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		default:
			err = BSL_ERR_UNDEFINED;
		}

		if (err == BSL_OK) {
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 2UL);
		}
	}

	/* fmt chunk - channels */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getChannels (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* fmt chunk - sample rate */
	if (err == BSL_OK) {
		DWORD data = (DWORD) obj->getSampleRate (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 4UL);
	}

	/* fmt chunk - */
	if (err == BSL_OK) {
		DWORD data = (obj->getSampleRate (obj) * (DWORD) obj->getBytesPerFrame (obj));
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 4UL);
	}

	/* fmt chunk - */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getBytesPerFrame (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* fmt chunk - bits per sample */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getBitsPerSample (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* fmt chunk - cbSize */
	if (err == BSL_OK) {
		if (obj->getCodec (obj) != BSL_WAV_CODEC_PCM) {
			WORD data = 0;
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 2UL);
		}
	}

	/* data chunk - header */
	if (err == BSL_OK) {
		DWORD header[2];
		marker = file->getPos (file) + 4;
		header[0] = data_MARKER;
		header[1] = 0; /* dummy */
		err = file->write (file, header, 8UL);
	}

	/* data chunk - data */
	if (err == BSL_OK) {
		switch (obj->getCodec (obj)) {
		case BSL_WAV_CODEC_PCM:
			err = _bslWavWriterBounce (file, obj, render, &renderedFrames);
			break;
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
		case BSL_WAV_CODEC_A_LAW:
			err = _bounce (file, obj, render, &renderedFrames);
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
		case BSL_WAV_CODEC_U_LAW:
			err = _bounce (file, obj, render, &renderedFrames);
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		default:
			err = BSL_ERR_UNDEFINED;
		}
	}

	/* data chunk - size */
	if (err == BSL_OK) {
		unsigned long current = file->getPos (file);
		DWORD data = (DWORD) (current - marker - 4UL);
		if (data % 2) {
			char c = 0;
			err = file->write (file, &c, 1);
			current++;
		}

		if (err == BSL_OK) {
			err = file->seek (file, marker, BSL_FILE_SEEK_START);
		}

		if (err == BSL_OK) {
			obj->setFrames (obj, data / obj->getBytesPerFrame (obj));
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 4);
		}

		if (err == BSL_OK) {
			err = file->seek (file, current, BSL_FILE_SEEK_START);
		}
	}

#ifdef BSL_WAV_LOOPS
	/* smpl chunk */
	if (err == BSL_OK) {
		if (0 < obj->getLoopNum (obj)) {
			DWORD header[2];
			header[0] = smpl_MARKER;
			header[1] = 36UL + 24UL; /* dummy */
#ifdef BSL_BIGENDIAN
			swap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, header, 8UL);
			if (err == BSL_OK) {
				BSL_WAV_LOOP l;
				BSL_WAV_RIFF_SMPL smpl;

				obj->getLoop (obj, 0, &l);
				memset (&smpl, 0, sizeof (BSL_WAV_RIFF_SMPL));

				smpl.dwSamplePeriod = 1000UL * 1000UL * 1000UL / obj->getSampleRate (obj);
				smpl.dwMidiUnityNote = (DWORD) l.dwUnityNote;
				smpl.dwMidiPitchFraction = (DWORD) ((float) l.dwFineTune * 4294967296.f / 100.f + 0.5f);
/*				smpl.dwNumSampleLoops = obj->getLoopNum (obj); */
				smpl.dwNumSampleLoops = 1UL;
#ifdef BSL_BIGENDIAN
				swap (&smpl.dwManufacturer, 4);
				swap (&smpl.dwProduct, 4);
				swap (&smpl.dwSamplePeriod, 4);
				swap (&smpl.dwMidiUnityNote, 4);
				swap (&smpl.dwMidiPitchFraction, 4);
				swap (&smpl.dwSmpteFormat, 4);
				swap (&smpl.dwSmpteOffset, 4);
				swap (&smpl.dwNumSampleLoops, 4);
				swap (&smpl.dwSamplerData, 4);
#endif /* BSL_BIGENDIAN */
				err = file->write (file, &smpl, 36UL);

				if (err == BSL_OK) {
					BSL_WAV_RIFF_SMPL_LOOP loop;
					memset (&loop, 0, sizeof (BSL_WAV_RIFF_SMPL_LOOP));
					loop.dwType = l.dwType;
					loop.dwStart = l.dwStart;
					loop.dwEnd = l.dwEnd;
#ifdef BSL_BIGENDIAN
					swap (&loop.dwCuePointId, 4);
					swap (&loop.dwType, 4);
					swap (&loop.dwStart, 4);
					swap (&loop.dwEnd, 4);
					swap (&loop.dwFraction, 4);
					swap (&loop.dwPlayCount, 4);
#endif /* BSL_BIGENDIAN */
					err = file->write (file, &loop, 24UL);
				}
			}
		}
	}
#endif /* BSL_WAV_LOOPS */

	/* RIFF size */
	if (err == BSL_OK) {
		unsigned long current = file->getPos (file);
		DWORD data;
		err = file->seek (file, 4L, BSL_FILE_SEEK_START);

		if (err == BSL_OK) {
			data = (DWORD) (current - 8UL);
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 4UL);
		}
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _saveStream
	(
	BSLFileSave *file, 
	void *user, 
	void *userParam
	)
{
	BSLErr err = BSL_OK;
	BSLWavWriter *obj = (BSLWavWriter *) user;
	unsigned long marker;
	BSL_WAV_WRITER_STREAM stream = (BSL_WAV_WRITER_STREAM) userParam;
	DWORD streamSize = 0UL;

	/* RIFF header */
	if (err == BSL_OK) {
		DWORD header[3];
#ifdef BSL_DEBUG
		bslTrace (_TEXT("WAV : ---------------------------------------\n"));
		bslTrace (_TEXT("WAV : riff file save (stream)\n"));
#endif /* BSL_DEBUG */

		header[0] = RIFF_MARKER;
		header[1] = 0UL; /* dummy */
		header[2] = WAVE_MARKER;
		err = file->write (file, header, 12UL);
	}

	/* fmt chunk header */
	if (err == BSL_OK) {
		DWORD header[2];
		header[0] = fmt__MARKER;
		header[1] = 16UL;
#ifdef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, header, 8UL);
	}

	/* fmt chunk - format */
	if (err == BSL_OK) {
		WORD data = BSL_WAV_FILE_RIFF_FORMAT_PCM;
		switch (obj->getCodec (obj)) {
		case BSL_WAV_CODEC_PCM:
			data = BSL_WAV_FILE_RIFF_FORMAT_PCM;
			break;
#ifdef BSL_WAV_INCLUDE_CODEC_A_LAW
		case BSL_WAV_CODEC_A_LAW:
			data = BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ALAW;
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_A_LAW */
#ifdef BSL_WAV_INCLUDE_CODEC_U_LAW
		case BSL_WAV_CODEC_U_LAW:
			data = BSL_WAV_FILE_RIFF_FORMAT_ITUG711_ULAW;
			break;
#endif /* BSL_WAV_INCLUDE_CODEC_U_LAW */
		default:
			err = BSL_ERR_UNDEFINED;
		}

		if (err == BSL_OK) {
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 2UL);
		}
	}

	/* fmt chunk - channels */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getChannels (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* fmt chunk - sample rate */
	if (err == BSL_OK) {
		DWORD data = obj->getSampleRate (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 4UL);
	}

	/* fmt chunk - */
	if (err == BSL_OK) {
		DWORD data = (obj->getSampleRate (obj) * (DWORD) obj->getBytesPerFrame (obj));
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 4UL);
	}

	/* fmt chunk - */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getBytesPerFrame (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* fmt chunk - bits per sample */
	if (err == BSL_OK) {
		WORD data = (WORD) obj->getBitsPerSample (obj);
#ifdef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
		err = file->write (file, &data, 2UL);
	}

	/* data chunk - header */
	if (err == BSL_OK) {
		DWORD header[2];
		marker = file->getPos (file) + 4;
		header[0] = data_MARKER;
		header[1] = 0; /* dummy */
		err = file->write (file, header, 8UL);
	}

	/* data chunk - data */
	if (err == BSL_OK) {
		err = stream (file, &streamSize, obj->pStreamParam);
	}

	/* data chunk - size */
	if (err == BSL_OK) {
		unsigned long current = file->getPos (file);
		DWORD data = (DWORD) (current - marker - 4UL);
		if (data % 2) {
			char c = 0;
			err = file->write (file, &c, 1);
			current++;
		}

		if (err == BSL_OK) {
			err = file->seek (file, marker, BSL_FILE_SEEK_START);
		}

		if (err == BSL_OK) {
			obj->setFrames (obj, data / obj->getBytesPerFrame (obj));
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 4);
		}

		if (err == BSL_OK) {
			err = file->seek (file, current, BSL_FILE_SEEK_START);
		}
	}

	/* RIFF size */
	if (err == BSL_OK) {
		unsigned long current = file->getPos (file);
		DWORD data;
		err = file->seek (file, 4L, BSL_FILE_SEEK_START);

		if (err == BSL_OK) {
			data = (DWORD) (current - 8UL);
#ifdef BSL_BIGENDIAN
			bslFileSwap (&data, 4);
#endif /* BSL_BIGENDIAN */
			err = file->write (file, &data, 4UL);
		}
	}

	return err;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
/*!
	@brief
	RIFF用BSLWavWriter継承制御オブジェクトを構築する
	@param obj
	制御オブジェクトのアドレス
*/
void bslWavWriterRiff
	(
	BSLWavWriter *obj
	)
{
	bslWavWriter (obj);
	obj->setType (obj, BSL_WAV_FILE_TYPE_RIFF);
	obj->setCodec = _setCodec;
	obj->setBitsPerSample = _setBitsPerSample;
	obj->setBitsPerSampleRendering = _setBitsPerSampleRendering;
	obj->getBitRate = _bslWavWriterGetBitRateLinear;
	obj->setUnsigned = _setUnsigned;
	obj->setBigEndian = _setBigEndian;
	obj->_save = _save;
	obj->_saveStream = _saveStream;

	obj->setUnsigned (obj, FALSE);
	obj->setBigEndian (obj, FALSE);
}

#endif /* BSL_WAV_INCLUDE_FILE_TYPE_RIFF */

