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
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.APP.TEST
 * filename	:	_Activity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app.play
 *    |_ _Activity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.play.app.content.HeadsetBroadcastReceiver;
import kr.kymedia.karaoke.util._Log;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lamerman.isyoon.FileDialogFragmentBase.FileDialogOpener;

/**
 * <pre>
 * 볼륨(버튼)조절
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 7. 11.
 * @version 1.0
 */
public class AudioActivity extends BaseFullActivity2 implements FileDialogOpener {
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

	private HeadetReceiver mHeadsetReceiver;
	private AudioManager mAudioManager;

	public AudioManager getAudioManager() {
		return mAudioManager;
	}

	public void setAudioManager(AudioManager mAudioManager) {
		this.mAudioManager = mAudioManager;
	}

	public class HeadetReceiver extends HeadsetBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// super.onReceive(context, intent);
			onHeadetReceive(context, intent);
		}
	}

	public void onHeadetReceive(Context context, Intent intent) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onCreate(savedInstanceState);

		mHeadsetReceiver = new HeadetReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(mHeadsetReceiver, filter);

		setAudioManager((AudioManager) getSystemService(Context.AUDIO_SERVICE));
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		unregisterReceiver(mHeadsetReceiver);
	}

	protected void volumeUp(int keyCode, KeyEvent event) {
		_Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	protected void volumeDown(int keyCode, KeyEvent event) {
		_Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (mFileDialogfragment != null) {
		// _Log.d(__CLASSNAME__, getMethodName() + mFileDialogfragment.isVisible() + keyCode + event);
		// }
		_Log.d(__CLASSNAME__, getMethodName() + keyCode + event);

		boolean ret = false;

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_EQUALS:
		case KeyEvent.KEYCODE_NUMPAD_ADD:
			volumeUp(keyCode, event);
			ret = true;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_MINUS:
		case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
			volumeDown(keyCode, event);
			ret = true;
			break;
		default:
			break;
		}

		if (ret) {
			return ret;
		}

		return super.onKeyDown(keyCode, event);
	}

}
