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
 * filename	:	PlayView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ PlayView.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * <pre>
 * 기본:시스템관련기능
 * </pre>
 *
 * @author isyoon
 * @since 2015. 2. 3.
 * @version 1.0
 */
class LyricsPlay1 extends SurfaceView {

	private final Context context;

	public LyricsPlay1(Context context) {
		super(context);
		this.context = context;
	}

	public Context getApplicationContext() {
		return context.getApplicationContext();
	}

	WindowManager windowManager;

	public WindowManager getWindowManager() {

		if (windowManager == null) {
			windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		}
		return windowManager;
	}

	public Object getSystemService(String name) {

		return context.getSystemService(name);
	}

	public AssetManager getAssets() {
		return context.getAssets();
	}

	public int getCurrentPosition() {

		return 0;
	}

	public int getDuration() {
		return 0;
	}
}
