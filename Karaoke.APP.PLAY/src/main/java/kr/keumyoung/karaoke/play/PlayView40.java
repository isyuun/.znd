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
 * filename	:	PlayView40.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayView40.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * <pre>
 * 터치이벤트확인
 * </pre>
 *
 * @author isyoon
 * @since 2015. 8. 27.
 * @version 1.0
 */
@Deprecated
class PlayView40 extends PlayView4 implements OnTouchListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayView40(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView40(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView40(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView40(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		String vn = v.getContext().getResources().getResourceEntryName(v.getId());
		String cn = v.getClass().getSimpleName();
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + vn + ", " + cn + "\n" + event);
		return false;
	}

}
