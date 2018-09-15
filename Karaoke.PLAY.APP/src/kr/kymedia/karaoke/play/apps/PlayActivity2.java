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
 * filename	:	_PlayActiviy.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ _PlayActiviy.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util.Log;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * 
 * <pre>
 * 기본레이아웃
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 8. 12.
 * @version 1.0
 */
public class PlayActivity2 extends PlayActivity {
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
		Log.i(__CLASSNAME__, getMethodName());


		super.onCreate(savedInstanceState);

		setContentView();

		_PlayFragment fragment = new _PlayFragment();
		replaceFragment(R.id.fragment1, fragment, "fragment1");
	}

	protected void setContentView() {
		setContentView(R.layout.activity_play);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(__CLASSNAME__, getMethodName() + intent);


		super.onNewIntent(intent);

		((_PlayFragment) getCurrentFragment()).onNewIntent(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		Log.e(__CLASSNAME__, getMethodName() + newConfig);
		super.onConfigurationChanged(newConfig);
	}
}
