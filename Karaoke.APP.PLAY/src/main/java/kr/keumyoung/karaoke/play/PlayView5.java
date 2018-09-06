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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	PlayView5.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayView5.java
 * </pre>
 */
package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

/**
 * <pre>
 * 쒹빠처리
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015-10-29
 */
class PlayView5 extends PlayView4XX {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayView5(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView5(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView5(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView5(Context context) {
		super(context);
	}

	SeekBar seekBar;

	@Override
	protected void setPlayView() {
		Log.e(_toString(), getMethodName());
		super.setPlayView();

		seekBar = (SeekBar) findViewById(R.id.seek_bar);

		if (seekBar != null) {
			//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			//	seekBar.getThumb().mutate().setAlpha(0);
			//} else {
			//	seekBar.setThumb(null);
			//}
			seekBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean play() {
		boolean ret =  super.play();
		if (seekBar != null) {
			seekBar.setProgress(0);
		}
		return ret;
	}

	@Override
	public void stop() {
		super.stop();
		if (seekBar != null) {
			seekBar.setProgress(0);
		}
	}

	@Override
	public void setOnListener(Listener listener) {
		super.setOnListener(listener);
	}

	@Override
	public void onPrepared() {
		super.onPrepared();

		if (seekBar != null) {
			seekBar.setProgress(0);
			seekBar.setMax(getTotalTime());
		}
	}

	@Override
	public void onTime(int t) {
		//Log.e(_toString(), getMethodName() + t + "/" + getTotalTime() + ":" + seekBar);
		super.onTime(t);

		if (seekBar != null) {
			seekBar.setProgress(t);
		}
	}
}
