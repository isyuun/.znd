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
 * project	:	Karaoke
 * filename	:	MonkeyListView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ MonkeyListView.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

/**
 * 
 * <pre>
 * 몽키리스트뷰(HeaderViewListAdapter) 우끼끼끼끼~~~~
 * </pre>
 * 
 * <a href=
 * "http://stackoverflow.com/questions/12140665/android-monkey-causes-adapter-notification-exception-in-android-widget-headervie"
 * >Android monkey causes adapter notification exception in android.widget.HeaderViewListAdapter</a>
 * 
 * @author isyoon
 * @since 2014. 3. 28.
 * @version 1.0
 */
public class MonkeyListView extends ListView {
	private String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		String text = String.format("%s() ", name);
		return text;
	}

	public MonkeyListView(Context context) {
		super(context);
	}

	public MonkeyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MonkeyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onTouchModeChanged(boolean isInTouchMode) {
		// Log.d(__CLASSNAME__, getMethodName());

		try {
			super.onTouchModeChanged(isInTouchMode);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void layoutChildren() {
		// Log.d(__CLASSNAME__, getMethodName());

		try {
			super.layoutChildren();
		} catch (Exception e) {
			// Log.e(__CLASSNAME__, getMethodName() + "This is not realy dangerous problem");
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {
			// samsung error
			// Log.e(__CLASSNAME__, getMethodName() + "[ERROR]ListView dispatchDraw ERROR!!!");
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {

		try {
			super.onWindowFocusChanged(hasWindowFocus);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
