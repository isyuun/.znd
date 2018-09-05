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
 * filename	:	_AudioTrack.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * com.smp.soundtouchandroid.isyoon
 *    |_ _AudioTrack.java
 * </pre>
 */
package kr.kymedia.karaoke.play.soundtouch;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @since 2016-03-04
 * @version 1.0
 */
public class _AudioTrack extends AudioTrack {
	public _AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode) throws IllegalArgumentException {
		super(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode);
	}

	public _AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode, int sessionId) throws IllegalArgumentException {
		super(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode, sessionId);
	}

	public _AudioTrack(AudioAttributes attributes, AudioFormat format, int bufferSizeInBytes, int mode, int sessionId) throws IllegalArgumentException {
		super(attributes, format, bufferSizeInBytes, mode, sessionId);
	}

	@Override
	public String toString() {
		//return super.toString();
		String ret = super.toString();
		String inf = "";
		inf += "\n" + "[INFO:AudioTrack]" + getSampleRate();
		ret += inf;
		return ret;
	}
}
