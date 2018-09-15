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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	PlayFragmentType.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ PlayFragmentType.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.PlayView;
import kr.kymedia.karaoke.play.app.view.SongPlayView;
import kr.kymedia.karaoke.util.Log;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * TODO<br>
 * 
 * <pre>
 * 재생종류선택
 * 	AudioTrack
 * 	MediaPlayer
 * 	SoundTouch
 * </pre>
 *
 * @author isyoon
 * @since 2014. 8. 27.
 * @version 1.0
 */
public class PlayFragment4 extends PlayFragment3 {
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

	SongPlayView.TYPE type = TYPE.SOUNDTOUCHPLAY;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		int type = 0;

		Intent intent = getActivity().getIntent();
		if (intent != null && intent.getExtras() != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				type = extras.getInt("radioPlayType");
			}
		}

		if (type == R.id.radioPlayTypeA) {
			this.type = TYPE.AUDIOTRACKPLAY;
		} else if (type == R.id.radioPlayTypeM) {
			this.type = TYPE.MEDIAPLAYERPLAY;
		} else if (type == R.id.radioPlayTypeS) {
			this.type = TYPE.SOUNDTOUCHPLAY;
		}

		PlayView.type = this.type;
		Log.e(__CLASSNAME__, getMethodName() + PlayView.type);
	}

}
