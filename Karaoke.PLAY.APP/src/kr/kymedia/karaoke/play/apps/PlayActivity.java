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
 * filename	:	PlayActiivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ PlayActiivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app._Activity;
import kr.kymedia.karaoke.util.Log;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 7. 23.
 * @version 1.0
 */
public class PlayActivity extends _Activity {
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

		super.onCreate(savedInstanceState);
		setFullScreen(false);
	}

	@Override
	protected void onStart() {

		if (getCurrentFragment() != null && getCurrentFragment() instanceof _PlayFragment) {
			((_PlayFragment) getCurrentFragment()).getRootView().setOnTouchListener(this);
		}
		super.onStart();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		openMain(null, null);

		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(__CLASSNAME__, getMethodName() + item);

		return super.onOptionsItemSelected(item);
		// if (item.getItemId() == R.id.full_screen_enable) {
		// setFullScreen(true);
		// return true;
		// } else if (item.getItemId() == R.id.full_screen_unable) {
		// setFullScreen(false);
		// return true;
		// } else {
		// return super.onOptionsItemSelected(item);
		// }
	}
}
