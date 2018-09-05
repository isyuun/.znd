/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	MediaCodecAudioDecoder.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * com.smp.soundtouchandroid.isyoon
 *    |_ MediaCodecAudioDecoder.java
 * </pre>
 */
package kr.kymedia.karaoke.play.soundtouch;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.smp.soundtouchandroid.AudioDecoder;
import com.smp.soundtouchandroid.AudioFormatChangedListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-08-30
 */
@SuppressLint("NewApi")
public class MediaCodecAudioDecoder implements AudioDecoder
{
	public static String getExtension(String uri)
	{
		if (uri == null)
		{
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0)
		{
			return uri.substring(dot);
		}
		else
		{
			// No extension.
			return "";
		}
	}

	private static final long TIMEOUT_US = 0;

	private long durationUs; // track duration in us
	private volatile long currentTimeUs; // total played duration thus far
	private MediaCodec.BufferInfo info;
	private MediaCodec codec;
	private MediaExtractor extractor;
	private MediaFormat format;
	private ByteBuffer[] codecInputBuffers;
	private ByteBuffer[] codecOutputBuffers;
	private byte[] lastChunk;
	private AudioFormatChangedListener changedListener;
	private volatile boolean sawOutputEOS;

	public int getChannels() throws IOException
	{
		if (format.containsKey(MediaFormat.KEY_CHANNEL_COUNT))
			return format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
		throw new IOException("Not a valid audio file");
	}

	public long getDuration()
	{
		return durationUs;
	}

	@Override
	public long getPlayedDuration()
	{
		return currentTimeUs;
	}

	public int getSamplingRate() throws IOException
	{
		if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE))
			return format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		throw new IOException("Not a valid audio file");
	}

	FileOutputStream fis = null;

	@SuppressLint("NewApi")
	public MediaCodecAudioDecoder(String fullPath, AudioFormatChangedListener changedListener) throws IOException
	{
		this.changedListener = changedListener;
		Locale locale = Locale.getDefault();
/*		if (getExtension(fullPath).toLowerCase(locale).equals(".wma"))
			throw new IOException("WMA file not supported");*/
		RandomAccessFile fis = null;
		extractor = new MediaExtractor();
		try
		{
			extractor = new MediaExtractor();
			extractor.setDataSource(fullPath);
		}
		catch (IOException e)
		{
			try
			{
				Thread.sleep(25);
			}
			catch (InterruptedException e2)
			{
				e2.printStackTrace();
			}
			try
			{
				extractor = new MediaExtractor();
				extractor.setDataSource(fullPath);
			}
			catch (IOException e1)
			{
				try
				{
					Thread.sleep(25);
				}
				catch (InterruptedException e2)
				{
					e2.printStackTrace();
				}
				extractor = new MediaExtractor();
				extractor.setDataSource(fullPath);
			}
		}

		format = extractor.getTrackFormat(0);
		durationUs = format.getLong(MediaFormat.KEY_DURATION);

		configureCodec();

		extractor.selectTrack(0);
		info = new MediaCodec.BufferInfo();
	}

	@SuppressLint("NewApi")
	public MediaCodecAudioDecoder(AssetFileDescriptor sampleFD, AudioFormatChangedListener changedListener) throws IOException
	{
		this.changedListener = changedListener;
		Locale locale = Locale.getDefault();
/*		if (getExtension(fullPath).toLowerCase(locale).equals(".wma"))
			throw new IOException("WMA file not supported");*/
		RandomAccessFile fis = null;
		extractor = new MediaExtractor();
		try
		{
			extractor = new MediaExtractor();
			extractor.setDataSource(sampleFD.getFileDescriptor(),
					sampleFD.getStartOffset(), sampleFD.getLength());
		}
		catch (IOException e)
		{
			try
			{
				Thread.sleep(25);
			}
			catch (InterruptedException e2)
			{
				e2.printStackTrace();
			}
			try
			{
				extractor = new MediaExtractor();
				extractor.setDataSource(sampleFD.getFileDescriptor(),
						sampleFD.getStartOffset(), sampleFD.getLength());
			}
			catch (IOException e1)
			{
				try
				{
					Thread.sleep(25);
				}
				catch (InterruptedException e2)
				{
					e2.printStackTrace();
				}
				extractor = new MediaExtractor();
				extractor.setDataSource(sampleFD.getFileDescriptor(),
						sampleFD.getStartOffset(), sampleFD.getLength());
			}
		}

		format = extractor.getTrackFormat(0);
		durationUs = format.getLong(MediaFormat.KEY_DURATION);

		configureCodec();

		extractor.selectTrack(0);
		info = new MediaCodec.BufferInfo();
	}

	private void configureCodec()
	{
		String mime = format.getString(MediaFormat.KEY_MIME);

		try {
			codec = MediaCodec.createDecoderByType(mime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		codec.configure(format, null, null, 0);
		codec.start();
		codecInputBuffers = codec.getInputBuffers();
		codecOutputBuffers = codec.getOutputBuffers();
	}

	@Override
	public void close()
	{
		try
		{
			codec.stop();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		codec.release();
		codec = null;
		extractor.release();
		extractor = null;
	}

	@Override
	public boolean decodeChunk()
	{
		boolean newBytes = false;
		advanceInput();

		final int res = codec.dequeueOutputBuffer(info, TIMEOUT_US);
		if (res >= 0)
		{
			if (info.size - info.offset < 0)
			{
				Log.d("DECODE", "BURRRRP");
				int outputBufIndex = res;
				ByteBuffer buf = codecOutputBuffers[outputBufIndex];
				if (lastChunk == null || lastChunk.length != info.size)
				{
					//this should always be info.size == 0, should the whole thing be refactored out?
					lastChunk = new byte[info.size];
				}
				buf.get(lastChunk);
				buf.clear();
				codec.releaseOutputBuffer(outputBufIndex, false);
				newBytes = true;
			}
			else
			{
				processBytes(res);
				newBytes = true;
			}
		}
		if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
		{
			currentTimeUs = durationUs;
			sawOutputEOS = true;
		}
		else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
		{
			codecOutputBuffers = codec.getOutputBuffers();
		}
		else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
		{
			format = codec.getOutputFormat();
			try
			{
				changedListener.onAudioFormatChanged(getSamplingRate(), getChannels());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("MP3", "Output format has changed to " + format);
		}

		return newBytes;
	}
	private void processBytes(int res)
	{
		int outputBufIndex = res;
		ByteBuffer buf = codecOutputBuffers[outputBufIndex];
		if (lastChunk == null
				|| lastChunk.length != info.size - info.offset)
		{
			lastChunk = new byte[info.size - info.offset];
		}
		if (info.size != 0) currentTimeUs = info.presentationTimeUs;
		buf.position(info.offset);
		buf.get(lastChunk);
		buf.clear();
		codec.releaseOutputBuffer(outputBufIndex, false);
	}
	@Override
	public void resetEOS()
	{
		sawOutputEOS = false;
		info.flags = 0;
	}

	@Override
	public boolean sawOutputEOS()
	{
		return sawOutputEOS;
	}

	// not flushing creates distortion
	@Override
	public void seek(long timeInUs, boolean shouldFlush)
	{
		extractor.seekTo(timeInUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
		currentTimeUs = timeInUs;
		//Log.d("DECODE", "CURRENT TIME: " + String.valueOf(currentTimeUs));
		//Log.d("DECODE", "total duration: " + String.valueOf(durationUs));
		// if (shouldFlush)
		codec.flush();
	}

	private void advanceInput()
	{
		boolean sawInputEOS = false;

		int inputBufIndex = codec.dequeueInputBuffer(TIMEOUT_US);
		if (inputBufIndex >= 0)
		{
			ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];

			int sampleSize = extractor.readSampleData(dstBuf, 0);
			long presentationTimeUs = 0;

			if (sampleSize < 0)
			{
				sawInputEOS = true;
				sampleSize = 0;
			}
			else
			{
				presentationTimeUs = extractor.getSampleTime();
				//currentTimeUs = presentationTimeUs;
			}

			codec.queueInputBuffer(inputBufIndex, 0, sampleSize,
					presentationTimeUs,
					sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
			if (!sawInputEOS)
			{
				extractor.advance();
			}
		}
	}

	@Override
	public byte[] getLastChunk()
	{
		return lastChunk;
	}

}
