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
 * filename	:	FullActivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app.play4
 *    |_ FullActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.app.FSActivity;
import kr.kymedia.karaoke.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 7. 23.
 * @version 1.0
 */
public class FullScreenActivity extends FSActivity implements OnTouchListener {
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

	protected void setFullScreen(boolean enabled) {
		Log.i(__CLASSNAME__, getMethodName() + enabled);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

}
