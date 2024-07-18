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
 * filename	:	BaseFragmentActivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app.play4
 *    |_ BaseFragmentActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.util._Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 7. 23.
 * @version 1.0
 */
public class FragmentActivity extends AudioFocusActivity {
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
	protected void addFragment(int containerViewId, Fragment fragment, String tag) {
		_Log.i(__CLASSNAME__, getMethodName());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragment1, fragment, tag);
		ft.commit();
	}

	@Override
	protected void replaceFragment(int containerViewId, Fragment fragment, String tag) {
		_Log.i(__CLASSNAME__, getMethodName());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragment1, fragment, tag);
		ft.commit();
	}

	@Override
	public Fragment getCurrentFragment() {
		_Log.i(__CLASSNAME__, getMethodName());
		try {
			Fragment fragment = null;
			// fragment = getSupportFragmentManager().findFragmentById(R.id.fragment1);
			fragment = getSupportFragmentManager().findFragmentByTag("fragment1");
			return fragment;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

}
