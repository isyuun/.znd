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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.TV
 * filename	:	MusicVideoView.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv
 *    |_ MusicVideoView.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import kr.keumyoung.karaoke.api._Const;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 6. 11.
 */
public class MusicVideoView extends android.widget.VideoView {
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

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 */
	Context context;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public MusicVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.context = context;
	}

	public MusicVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public MusicVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public MusicVideoView(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!isInEditMode()) {
			getDisPlaySize();
		}
	}

	public WindowManager getWindowManager() {
		if (this.context == null) {
			return null;
		}
		return ((Activity) this.context).getWindowManager();
	}

	Point displaySize = new Point();

	private void getDisPlaySize() {
		DisplayMetrics outMetrics = getResources().getDisplayMetrics();

		Display display = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			display = ((Activity) this.context).getWindow().getDecorView().getDisplay();
			//Display display = getWindowManager().getDefaultDisplay();

			if (display != null) {
				//display.getRealMetrics(outMetrics);
				display.getRealSize(displaySize);
			}
		}

		Log.e(_toString() + _Const.TAG_VIDEO, "getDisPlaySize() " + displaySize);
	}

	// 븅신...어디선본건있어가지구...쓸데없는짓은...
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int x = widthMeasureSpec;
		int y = heightMeasureSpec;
		// 븅신...이렇게 하면 어찌될까~~~
		// if (displaySize != null) {
		// 	x = displaySize.x;
		// 	y = displaySize.y;
		// }
		setMeasuredDimension(x, y);
		if (BuildConfig.DEBUG) Log.wtf(_toString() + _Const.TAG_VIDEO, "onMeasure() " + "(" + widthMeasureSpec + "," + heightMeasureSpec + ")" + "->" + "(" + x + "," + y + ")" + ":" + displaySize);
	}
}
