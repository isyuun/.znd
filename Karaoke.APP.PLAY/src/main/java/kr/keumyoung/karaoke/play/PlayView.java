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
 * project	:	Karaoke.TV
 * filename	:	_SongPlayerView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ _SongPlayerView.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2015. 2. 3.
 * @version 1.0
 */
class PlayView extends RelativeLayout {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	Context context;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.context = context;
	}

	public PlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PlayView(Context context) {
		super(context);
		this.context = context;
	}

	public Context getApplicationContext() {
		return context.getApplicationContext();
	}

	WindowManager windowManager;

	public WindowManager getWindowManager() {

		if (windowManager == null) {
			windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		}
		return windowManager;
	}

	public Object getSystemService(String name) {

		return context.getSystemService(name);
	}

	public AssetManager getAssets() {
		return context.getAssets();
	}
}
