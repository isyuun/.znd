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
 * filename	:	LyricsPlay3X.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ LyricsPlay3X.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;

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
class LyricsPlay3X extends LyricsPlay3 {
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

	public LyricsPlay3X(Context context) {
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
		this.mLyricsMarginBottom = lyricsMarginBottom;
	}

	/**
	 * 자막하단여백
	 */
	public int getLyricsMarginBottom() {
		return mLyricsMarginBottom;
	}

	@Override
	protected void init() {

		super.init();
	}

}
