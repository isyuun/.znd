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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.PLAY4.APP
 * filename	:	AudioFocusActivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app
 *    |_ AudioFocusActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.play.apps._PlayFragment;
import kr.kymedia.karaoke.util._Log;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 6. 1.
 * @version 1.0
 */
public class AudioFocusActivity extends AudioActivity implements OnAudioFocusChangeListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	private OnAudioFocusChangeListener mListener;

	public int requestAudioFocus(OnAudioFocusChangeListener l) {
		_Log.w(__CLASSNAME__, getMethodName() + mListener + ":" + l);
		mListener = l;
		// Request audio focus for playback
		int ret = getAudioManager().requestAudioFocus(this,
				// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN);

		if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			// Start playback.
		}

		return ret;
	}

	public int abandonAudioFocus() {
		_Log.w(__CLASSNAME__, getMethodName() + mListener);
		mListener = null;
		// Abandon audio focus when playback complete
		int ret = getAudioManager().abandonAudioFocus(this);
		return ret;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {

		// _Log.w(__CLASSNAME__, getMethodName() + mListener + ":" + focusChange);

		String msg = null;
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
			// Pause playback
			msg = getMethodName() + mListener + ":" + "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT";
		} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			// Resume playback
			msg = getMethodName() + mListener + ":" + "AudioManager.AUDIOFOCUS_GAIN";
		} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			abandonAudioFocus();
			// Stop playback
			msg = getMethodName() + mListener + ":" + "AudioManager.AUDIOFOCUS_LOSS";
		} else {
			msg = getMethodName() + mListener + ":" + focusChange + ":" + "AudioManager.UNKNOWN";
		}

		// test
		if (getCurrentFragment() instanceof _PlayFragment) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				// Pause playback
				((_PlayFragment) getCurrentFragment()).pause();
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				// Resume playback
				// ((_PlayFragment) getCurrentFragment()).play();
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				// Stop playback
				// ((_PlayFragment) getCurrentFragment()).stop();
			}
		}

		if (mListener != null) {
			mListener.onAudioFocusChange(focusChange);
		}

		_Log.w(__CLASSNAME__, msg);
		// Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestAudioFocus(null);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		abandonAudioFocus();
	}

}
