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
 * kr.kymedia.kykaraoke.tv.play
 *    |_ _SongPlayerView.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import kr.keumyoung.karaoke.play.BuildConfig;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 3. 12.
 * @version 1.0
 */
public class _PlayView extends PlayView7 {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}


	public _PlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public _PlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public _PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public _PlayView(Context context) {
		super(context);
	}

	@Override
	public void onPrepared() {

		Log.wtf(__CLASSNAME__, getMethodName() + song);
		super.onPrepared();
	}
}
