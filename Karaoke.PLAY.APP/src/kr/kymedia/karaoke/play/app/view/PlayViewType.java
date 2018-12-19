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
 * filename	:	PlayViewPlayType.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app.view
 *    |_ PlayViewPlayType.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util._Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 27.
 * @version 1.0
 */
public class PlayViewType extends PlayViewChoir {
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

	public PlayViewType(Context context) {
		super(context);
	}

	public PlayViewType(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewType(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setEnabled(boolean enabled) {

		// _Log.e(__CLASSNAME__, getMethodName() + type);

		super.setEnabled(enabled);

		if (type == TYPE.AUDIOTRACKPLAY) {
		} else if (type == TYPE.MEDIAPLAYERPLAY) {
		} else if (type == TYPE.SOUNDTOUCHPLAY) {
		}

	}

	@Override
	public void showSongControl(boolean visible) {

		_Log.e(__CLASSNAME__, getMethodName() + type);

		findViewById(R.id.buttonShow).setEnabled(true);

		String text = "N";
		if (type == TYPE.AUDIOTRACKPLAY) {
			text = "A";
		} else if (type == TYPE.MEDIAPLAYERPLAY) {
			text = "M";
			visible = false;
			findViewById(R.id.buttonShow).setEnabled(false);
		} else if (type == TYPE.SOUNDTOUCHPLAY) {
			text = "S";
		}

		super.showSongControl(visible);

		((TextView) findViewById(R.id.textPlayType)).setText("TYP:" + text);

		if (isChoir()) {
			findViewById(R.id.textPlayType).setVisibility(View.INVISIBLE);
		} else {
			findViewById(R.id.textPlayType).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.textPlayType).setVisibility(View.INVISIBLE);
	}

	@Override
	public void start() {
		_Log.i(__CLASSNAME__, getMethodName() + type);

		super.start();
	}

}
