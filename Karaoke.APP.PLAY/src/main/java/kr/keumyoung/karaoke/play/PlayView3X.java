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
 * filename	:	PlayView3X.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayView3X.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/**
 * <pre>
 * AOSP(BHX-S300)
 * <a href="http://pms.skdevice.net/redmine/issues/3482">3482 일부노래 자막 하단이 잘려서 출력되는 현상</a>
 * 	48859 - '씨스타 - Shake It' 노래 부르기 진행 중 일부 가사 하단부분이 잘려서 출력됩니다.
 * </pre>
 * 
 * @author isyoon
 * @since 2015. 9. 3.
 * @version 1.0
 */
class PlayView3X extends PlayView3 {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayView3X(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView3X(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView3X(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView3X(Context context) {
		super(context);
	}

	/**
	 * 자막하단여백
	 */
	private int mLyricsMarginBottom = 0;

	/**
	 * 자막하단여백
	 */
	public void setLyricsMarginBottom(int lyricsMarginBottom) {
		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + lyricsMarginBottom + ":" + getLyricsPlay());
		this.mLyricsMarginBottom = lyricsMarginBottom;
		if (getLyricsPlay() != null) {
			getLyricsPlay().setLyricsMarginBottom(lyricsMarginBottom);
		}
	}

	/**
	 * @see kr.keumyoung.karaoke.play.PlayView2#create()
	 */
	@Override
	protected void onAttachedToWindow() {

		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName());
		super.onAttachedToWindow();
		setLyricsMarginBottom(mLyricsMarginBottom);
	}

	@Override
	protected void onDetachedFromWindow() {

		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName());
		super.onDetachedFromWindow();
	}

}
