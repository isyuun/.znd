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
 * filename	:	_Activity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ _Activity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps.test;

import kr.kymedia.karaoke.play.apps.PlayFragment;
import kr.kymedia.karaoke.play.apps._PlayActivity;
import kr.kymedia.karaoke.util._Log;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 7. 23.
 * @version 1.0
 */
public class _Activity extends kr.kymedia.karaoke.play.app._Activity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	protected void openIntent(Intent intent) {
		// _Log.e(__CLASSNAME__, getMethodName() + intent);

		if (intent.getData() != null) {
			final Uri uri = intent.getData();

			if (!TextUtils.isEmpty(uri.getPath())) {
				// _Log.e(__CLASSNAME__, getMethodName() + "[DATA]" + uri);

				Bundle extras = new Bundle();
				extras.putParcelable("pathPlay", uri);

				_Log.w(__CLASSNAME__, getMethodName() + "[DATA]" + uri);
				// openMain(extras, null);
				openPlay(extras, null);
			}
		} else if (intent.getExtras() != null) {
			final Uri uri = intent.getExtras().getParcelable("pathPlay");

			if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
				// _Log.e(__CLASSNAME__, getMethodName() + "[DATA]" + uri);


				Bundle extras = intent.getExtras();

				_Log.w(__CLASSNAME__, getMethodName() + "[EXTRA]" + extras);
				openPlay(extras, null);
			}
		}
	}

	@Override
	protected void openMain(Bundle extras, Uri data) {
		_Log.e(__CLASSNAME__, getMethodName() + "Extras[" + extras + "] - Data[" + data + "]");

		super.openMain(extras, data);
	}

	/**
	 * <pre>
	 * @see PlayFragment#start()
	 * </pre>
	 */
	protected void openPlay(Bundle extras, Uri data) {
		_Log.e(__CLASSNAME__, getMethodName() + "Extras[" + extras + "] - Data[" + data + "]");

		Intent intent = new Intent(getApplicationContext(), _PlayActivity.class);

		if (extras != null) {
			intent.putExtras(extras);
		}

		intent.setData(data);

		// _Log.e(__CLASSNAME__, getMethodName() + intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	@Override
	public void onHeadetReceive(Context context, Intent intent) {

		super.onHeadetReceive(context, intent);

		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

			int state = intent.getIntExtra("state", -1);
			switch (state) {
			case 0:
				_Log.e(__CLASSNAME__, "Headset unplugged");
				getAudioManager().setStreamMute(AudioManager.STREAM_MUSIC, true);
				Fragment fragment = getCurrentFragment();
				if (fragment instanceof PlayFragment) {
					((PlayFragment) fragment).pause();
				}
				post(new Runnable() {
					@Override
					public void run() {
						getAudioManager().setStreamMute(AudioManager.STREAM_MUSIC, false);
					}
				});
				break;
			case 1:
				_Log.e(__CLASSNAME__, "Headset plugged");
				// if (fragment instanceof PlayFragment) {
				// ((PlayFragment) fragment).resume();
				// }
				break;
			}

		}
	}

}
