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
 * project	:	Karaoke.KPOP
 * filename	:	SongRecorder5.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.record
 *    |_ SongRecorder5.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.record;

import android.content.res.Configuration;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.util.Log;

/**
 * 
 *
 * <pre>
 * 녹화기능추가 - 현재릴리스
 * </pre>
 * 
 * @author isyoon
 * @since 2013. 8. 19.
 * @version 1.0
 */
public class SongRecorder5 extends SongRecorder2 {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private CameraPreview2 mPreview;

	public SongRecorder5(String name, boolean compressed) throws Exception {
		super(name, compressed);
	}

	public SongRecorder5(String name, boolean compressed, CameraPreview2 preview)
			throws Exception {
		Log.e(__CLASSNAME__, getMethodName() + preview);

		// Create an instance of Camera
		// mCamera = preview.getCamera();

		// Create our Preview view and set it as the content of our activity.
		mPreview = preview;

		// SongRecorder2(songNumber, compressed);
		isRecording = false;

		String path = setFilePath(name);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		// Log.d(__CLASSNAME__, getMethodName() + songNumber + "," + mOutputFormat + "," + mAudioEncoder
		// + "," + mSampleBitRate + "," + mSampleRate);

		init(path);

	}

	@Override
	protected void init(String path) throws Exception {

		if (mPreview != null) {
			prepare(CamcorderProfile.QUALITY_HIGH);
		} else {
			super.init(path);
		}
	}

	@Override
	protected void prepare(int quality) throws Exception {
		Log.e(__CLASSNAME__, getMethodName());


		if (mPreview != null) {
			if (mMediaRecorder == null) {
				mMediaRecorder = new MediaRecorder();
			}

			mMediaRecorder.setOnErrorListener(this);
			mMediaRecorder.setOnInfoListener(this);

			// Step 1: Unlock and set camera to MediaRecorder
			try {
				mPreview.getCamera().unlock();
			} catch (Exception e) {

				e.printStackTrace();
			}
			mMediaRecorder.setCamera(mPreview.getCamera());

			// Step 2: Set sources
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Step 3: Set output format and encoding (for versions prior to API Level 8)
			// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			// 비디오옵션-지랄
			// Size size = mPreview.getPreviewSize();
			// mMediaRecorder.setVideoSize(size.width, size.height);
			// 오디오옵션-지랄
			// mMediaRecorder.setAudioChannels(mNumChannels);
			// mMediaRecorder.setAudioEncodingBitRate(96000);
			// mMediaRecorder.setAudioSamplingRate(44100);
			// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
			mMediaRecorder.setProfile(mPreview.getCamorderProfile());

			if (mPreview.getCamorderProfile().fileFormat == OutputFormat.MPEG_4) {
				mPath = replaceFileExt(mPath, "m4a", "mp4");
				mPath = replaceFileExt(mPath, "3gp", "mp4");
			} else {
				mPath = replaceFileExt(mPath, "m4a", "3gp");
				mPath = replaceFileExt(mPath, "3gp", "3gp");
			}
			// mPath = replaceFileExt(mPath, "m4a", "mp4");
			// mPath = replaceFileExt(mPath, "3gp", "mp4");

			Log.e(__CLASSNAME__, mPath);

			// Step 4: Set output file
			mMediaRecorder.setOutputFile(mPath);

			// Step 5: Set the preview output
			// mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

			int orientation = mPreview.getContext().getResources().getConfiguration().orientation;
			int facing = mPreview.getCameraInfo().facing;
			int degrees = mPreview.getCameraInfo().orientation;

			Log.e(__CLASSNAME__, "orientation: " + orientation + ", facing: " + facing + ", degrees: " + degrees);
			try {
				if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					mMediaRecorder.setOrientationHint(degrees);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			// Step 6: Prepare configured MediaRecorder
			mMediaRecorder.prepare();
		} else {
			super.prepare(quality);
		}

	}

}
