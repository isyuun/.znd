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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	_PlayView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ _PlayView.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.util._Log;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * <pre>
 * 절대PlayView
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 8. 7.
 * @version 1.0
 */
public class _PlayView extends PlayViewFileDialogMulti {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	public _PlayView(Context context) {
		super(context);
		_Log.w(__CLASSNAME__, toString());
	}

	public _PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public _PlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void start() {

		super.start();
	}

	@Override
	public void prepare() {

		super.prepare();
	}

	@Override
	public void onTime(int t) {
		// _Log.w(__CLASSNAME__, getMethodName() + t);

		super.onTime(t);
	}

}
