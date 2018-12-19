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
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP.LIB.LGE.SMARTPHONE
 * filename	:	SongRecorder.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.record
 *    |_ SongRecorder.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.record;

import java.nio.ByteBuffer;

import kr.kymedia.karaoke.util._Log;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.os.Build;

/**
 *
 * NOTE: 녹음기본클래스<br>
 * 유틸리티정의<br>
 * 기본함수정의<br>
 *
 * @author isyoon
 * @since 2013. 4. 4.
 * @version 1.0
 * @see
 */
public class SongRecorder implements ISongRecorder {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s() ", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	final static int[] CAMCORDER_QUALITY = { CamcorderProfile.QUALITY_HIGH, CamcorderProfile.QUALITY_LOW, };

	final static int[] SAMPLE_RATE = { 44100, 22050, 11025, 8000, };

	protected static int mQuality = CamcorderProfile.QUALITY_HIGH;

	protected static int mNumChannels = 1;

	// 기기마다 TIMER_INTERVAL사이즈를 조절할 필요가 있다. (현재 50이 적당)
	// 너무적게 잡으면 AudioRecord Initialize시 최소버퍼로 잡혀 오히려 퍼포먼스가 떨어짐.
	// 좆되고 싶음 바꿔보던가...
	protected final static int TIMER_INTERVAL = 50;
	// protected static int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
	protected static int mAaudioFormat = AudioFormat.ENCODING_PCM_16BIT;
	protected static int mSampleRateInBit = 16;
	protected static int mBufferSizeInBytes = 0;
	protected int mPeriodInFrames = 0;

	protected static int mOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
	protected static int mAudioEncoder = MediaRecorder.AudioEncoder.AAC;
	protected static int mSampleBitRate = 128000;
	protected static int mSampleRate = 44100;

	protected String mPath = "";

	private String mInfo = "";

	public String getRecInfo() {
		return mInfo;
	}

	/**
	 * 녹음중확인
	 */
	protected boolean isRecording = false;

	/**
	 * <pre>
	 * 	int	AAC	AAC Low Complexity (AAC-LC) audio codec
	 * 	int	AAC_ELD	Enhanced Low Delay AAC (AAC-ELD) audio codec
	 * 	int	AMR_NB	AMR (Narrowband) audio codec
	 * 	int	AMR_WB	AMR (Wideband) audio codec
	 * 	int	DEFAULT	
	 * 	int	HE_AAC	High Efficiency AAC (HE-AAC) audio codec
	 * </pre>
	 * 
	 * @param audioCodec
	 * @return
	 */
	public static String getAudioCodec(int audioCodec) {
		switch (audioCodec) {
		case AudioEncoder.AAC:
			return "AAC";
		case AudioEncoder.AAC_ELD:
			return "AAC_ELD";
		case AudioEncoder.AMR_NB:
			return "AMR_NB";
		case AudioEncoder.AMR_WB:
			return "AMR_WB";
		case AudioEncoder.DEFAULT:
			return "DEFAULT";
		case AudioEncoder.HE_AAC:
			return "HE_AAC";
		default:
			return "unknown";
		}
	}

	public static String getFileFormat(int fileFormat) {
		switch (fileFormat) {
		case OutputFormat.MPEG_4:
			return "MPEG_4";
		case OutputFormat.RAW_AMR:
			return "RAW_AMR";
		case OutputFormat.THREE_GPP:
			return "THREE_GPP";
		case OutputFormat.DEFAULT:
			return "DEFAULT";
		default:
			return "unknown";
		}
	}

	public static String getVideoCodec(int videoCodec) {
		switch (videoCodec) {
		case VideoEncoder.DEFAULT:
			return "DEFAULT";
		case VideoEncoder.H263:
			return "H263";
		case VideoEncoder.H264:
			return "H264";
		case VideoEncoder.MPEG_4_SP:
			return "MPEG_4_SP";
		default:
			return "unknown";
		}
	}

	/**
	 * 해당설정CamcorderProfile유무확인
	 * 
	 * <pre>
	 * public static boolean hasProfile (int quality) 
	 * <b>Added in API level 11</b>
	 * </pre>
	 * 
	 * @see android.media.CamcorderProfile#hasProfile(int)
	 * @see android.media.CamcorderProfile#hasProfile(int, int)
	 * @see android.media.CamcorderProfile#get(int)
	 * @see android.media.CamcorderProfile#get(int, int)
	 */
	@SuppressLint("NewApi")
	public boolean hasCamcorderProfile(int quality) {
		// _Log.e(__CLASSNAME__, getMethodName() + ":" + quality);
		boolean ret = true;

		try {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ret = CamcorderProfile.hasProfile(quality);
				if (!ret) {
					int count = Camera.getNumberOfCameras();
					for (int i = 0; i < count; i++) {
						ret = CamcorderProfile.hasProfile(i, quality);
					}
				}
			}

			if (!ret) {
				_Log.e(__CLASSNAME__, getMethodName() + ret);
			}
		} catch (Exception e) {

			e.printStackTrace();
			_Log.e(__CLASSNAME__, getMethodName() + ret);
			_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}

		return ret;
	}

	/**
	 * 해당설정CamcorderProfile확인
	 * 
	 * <pre>
	 * public static boolean hasProfile (int quality) 
	 * <b>Added in API level 11</b>
	 * </pre>
	 * 
	 * @see android.media.CamcorderProfile#hasProfile(int)
	 * @see android.media.CamcorderProfile#hasProfile(int, int)
	 * @see android.media.CamcorderProfile#get(int)
	 * @see android.media.CamcorderProfile#get(int, int)
	 */
	public CamcorderProfile getCamcorderProfile(int quality) {
		// _Log.e(__CLASSNAME__, getMethodName() + ":" + quality);
		CamcorderProfile ret = null;

		ret = CamcorderProfile.get(quality);

		try {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (ret == null) {
					// Camera.CameraInfo.CAMERA_FACING_BACK;
					// Camera.CameraInfo.CAMERA_FACING_FRONT;
					int count = Camera.getNumberOfCameras();
					for (int i = 0; i < count; i++) {
						ret = CamcorderProfile.get(i, quality);
					}
				}
			}

			if (ret == null) {
				_Log.e(__CLASSNAME__, getMethodName() + ret);
				Exception e = new Exception("getMethodName() + ret");
				throw e;
			}
		} catch (Exception e) {

			e.printStackTrace();
			_Log.e(__CLASSNAME__, getMethodName() + ret);
			_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}

		return ret;
	}

	/**
	 * 해당설정CamcorderProfile<br>
	 * <b>샘플링비트레이트/샘플링주파수조정</b><br>
	 * 
	 * <pre>
	 * public static boolean hasProfile (int quality) 
	 * <b>Added in API level 11</b>
	 * </pre>
	 * 
	 * @see #mQuality
	 * @see #mSampleBitRate
	 * @see #mSampleRate
	 * @see android.media.CamcorderProfile#hasProfile(int)
	 * @see android.media.CamcorderProfile#hasProfile(int, int)
	 * @see android.media.CamcorderProfile#get(int)
	 * @see android.media.CamcorderProfile#get(int, int)
	 */
	public int getCamcorderQuality(final int quality) {
		_Log.e(__CLASSNAME__, getMethodName());

		mQuality = quality;

		if (!hasCamcorderProfile(quality)) {
			return -1;
		}

		CamcorderProfile profile = getCamcorderProfile(quality);

		if (profile != null) {
			mSampleBitRate = profile.audioBitRate;
			mSampleRate = profile.audioSampleRate;
		}

		_Log.e(__CLASSNAME__, getMethodName() + quality + ":" + mSampleBitRate + ":" + mSampleRate
				+ profile);

		return quality;
	}

	public void addRecInfo(String message) {
		mInfo += "\n[RECINFO]" + message;
	}

	/**
	 * 전체CamcorderProfile확인
	 * 
	 * <pre>
	 * public static boolean hasProfile (int quality) 
	 * <b>Added in API level 11</b>
	 * </pre>
	 * 
	 * @see android.media.CamcorderProfile#hasProfile(int)
	 */
	public void putRecInfo() {
		_Log.e(__CLASSNAME__, getMethodName());

		mInfo = "";
		try {
			mInfo += "\n[RECINFO]" + ("Build.MANUFACTURER - " + Build.MANUFACTURER.toString());
			mInfo += "\n[RECINFO]" + ("Build.MODEL - " + Build.MODEL.toString());
			mInfo += "\n[RECINFO]" + ("Build.PRODUCT - " + Build.PRODUCT.toString());
			mInfo += "\n[RECINFO]" + ("Build.VERSION.CODENAME - " + Build.VERSION.CODENAME.toString());
			mInfo += "\n[RECINFO]" + ("Build.VERSION.INCREMENTAL - " + Build.VERSION.INCREMENTAL.toString());
			mInfo += "\n[RECINFO]" + ("Build.VERSION.RELEASE - " + Build.VERSION.RELEASE.toString());
			mInfo += "\n[RECINFO]" + ("Build.VERSION.SDK_INT - " + Build.VERSION.SDK_INT + "");

			mInfo += "\n[RECINFO]" + (mPath);
			mInfo += "\n[RECINFO]" + ("Quality:\t" + mQuality);
			mInfo += "\n[RECINFO]" + ("NumChannels:\t" + mNumChannels);
			mInfo += "\n[RECINFO]" + ("OutputFormat\t" + mOutputFormat);
			mInfo += "\n[RECINFO]" + ("AudioEncoder:\t" + mAudioEncoder);
			mInfo += "\n[RECINFO]" + ("SampleBitRate:\t" + mSampleBitRate);
			mInfo += "\n[RECINFO]" + ("SampleRate:\t" + mSampleRate);
			mInfo += "\n[RECINFO]" + ("BufferSizeInBytes:\t" + mBufferSizeInBytes);

			mInfo += "\n[RECINFO]" + ("RECORDING.QUALITY >> " + "fileFormat : " + getFileFormat(mOutputFormat));
			mInfo += "\n[RECINFO]" + ("RECORDING.QUALITY >> " + "audioCodec : " + getAudioCodec(mAudioEncoder));
			mInfo += "\n[RECINFO]" + ("RECORDING.QUALITY >> " + "audioBitRate : " + mSampleBitRate);
			mInfo += "\n[RECINFO]" + ("RECORDING.QUALITY >> " + "audioSampleRate : " + mSampleRate);

			// 이런씨댕
			if (hasCamcorderProfile(CamcorderProfile.QUALITY_LOW)) {
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_LOW >> " +
						"fileFormat : " + getFileFormat(getCamcorderProfile(CamcorderProfile.QUALITY_LOW).fileFormat));
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_LOW >> " +
						"audioCodec : " + getAudioCodec(getCamcorderProfile(CamcorderProfile.QUALITY_LOW).audioCodec));
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_LOW >> " +
						"audioBitRate : " + getCamcorderProfile(CamcorderProfile.QUALITY_LOW).audioBitRate);
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_LOW >> " +
						"audioSampleRate : " + getCamcorderProfile(CamcorderProfile.QUALITY_LOW).audioSampleRate);
			}

			// 이런씨댕
			if (hasCamcorderProfile(CamcorderProfile.QUALITY_HIGH)) {
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_HIGH >> " +
						"fileFormat : " + getFileFormat(getCamcorderProfile(CamcorderProfile.QUALITY_HIGH).fileFormat));
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_HIGH >> " +
						"audioCodec : " + getAudioCodec(getCamcorderProfile(CamcorderProfile.QUALITY_HIGH).audioCodec));
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_HIGH >> " +
						"audioBitRate : " + getCamcorderProfile(CamcorderProfile.QUALITY_HIGH).audioBitRate);
				mInfo += "\n[RECINFO]" + ("CamcorderProfile.QUALITY_HIGH >> " +
						"audioSampleRate : " + getCamcorderProfile(CamcorderProfile.QUALITY_HIGH).audioSampleRate);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#isRecording()
	 */
	@Override
	public boolean isRecording() {

		return false;
	}

	/**
	 * @return
	 * @see kr.kymedia.karaoke.record.ISongRecorder#start()
	 */
	@Override
	public boolean start() throws Exception {

		return false;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#stop()
	 */
	@Override
	public boolean stop() throws Exception {

		return false;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#pause()
	 */
	@Override
	public void pause() {


	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#resume()
	 */
	@Override
	public void resume() {


	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getAudioFormat()
	 */
	@Override
	public int getAudioFormat() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getAudioSource()
	 */
	@Override
	public int getAudioSource() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getChannelConfiguration()
	 */
	@Override
	public int getChannelConfiguration() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getChannelCount()
	 */
	@Override
	public int getChannelCount() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getNotificationMarkerPosition()
	 */
	@Override
	public int getNotificationMarkerPosition() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getPositionNotificationPeriod()
	 */
	@Override
	public int getPositionNotificationPeriod() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getRecordingState()
	 */
	@Override
	public int getRecordingState() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getSampleRate()
	 */
	@Override
	public int getSampleRate() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getSampleRateBit()
	 */
	@Override
	public int getSampleRateBit() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#getState()
	 */
	@Override
	public int getState() {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#read(java.nio.ByteBuffer, int)
	 */
	@Override
	public int read(ByteBuffer audioBuffer, int sizeInBytes) {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#read(short[], int, int)
	 */
	@Override
	public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#release()
	 */
	@Override
	public void release() {


	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#setNotificationMarkerPosition(int)
	 */
	@Override
	public int setNotificationMarkerPosition(int markerInFrames) {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#setPositionNotificationPeriod(int)
	 */
	@Override
	public int setPositionNotificationPeriod(int periodInFrames) {

		return 0;
	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#setOnErrorListener(android.media.MediaRecorder.OnErrorListener)
	 */
	@Override
	public void setOnErrorListener(OnErrorListener l) {


	}

	/**
	 * @see kr.kymedia.karaoke.record.ISongRecorder#setOnInfoListener(android.media.MediaRecorder.OnInfoListener)
	 */
	@Override
	public void setOnInfoListener(OnInfoListener listener) {


	}

	SongRecorderListener listener;

	@Override
	public void setSongRecorderListener(SongRecorderListener listener) {

		this.listener = listener;
	}

}
