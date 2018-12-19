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
 * filename	:	PlayActivityCast.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ PlayActivityCast.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import android.view.Menu;
import kr.kymedia.karaoke.util._Log;

/**
 *
 * TODO<br>
 * 
 * <pre>
 * 크롬캐스트(?)
 * </pre>
 *
 * @author isyoon
 * @since 2014. 9. 25.
 * @version 1.0
 */
public class PlayActivity4 extends PlayActivity2 {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		_Log.i(__CLASSNAME__, getMethodName());

		return super.onCreateOptionsMenu(menu);
		// getMenuInflater().inflate(R.menu.play, menu);
		//
		// MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
		// mediaRouteMenuItem.setVisible(true);
		// mediaRouteMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//
		// return true;
	}

}
