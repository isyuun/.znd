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
 * project	:	.prj
 * filename	:	SoundTouchPlayer.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * com.smp.soundtouchandroid.isyoon
 *    |_ SoundTouchPlayer.java
 * </pre>
 */
package kr.kymedia.karaoke.play.soundtouch;

import android.content.Context;
import android.util.Log;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015-10-13
 */
public class SoundTouchPlayer extends SoundTouchPlayable {

	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public SoundTouchPlayer(Context context, OnProgressChangedListener progressListener,
	                        String fileName, int id, float tempo, float pitchSemi)
			throws Exception {
		super(context, progressListener, fileName, id, tempo, pitchSemi);
	}

	public SoundTouchPlayer(Context context, String fileName, int id, float tempo, float pitchSemi)
			throws Exception {
		super(context, fileName, id, tempo, pitchSemi);
	}

	@Override
	public void run() {
		super.run();
	}

	@Override
	public void play() {
		if (!isFinished()) {
			Log.e(__CLASSNAME__, getMethodName() + isFinished() + ":" + this);
			new Thread(this).start();
			super.play();
		} else {
			Log.e(__CLASSNAME__, "[NG]" + getMethodName() + isFinished() + ":" + this);
		}
	}

	@Override
	public void stop() {
		super.stop();
		finished = true;
	}

	public void reset() {
		finished = true;
	}

	@Override
	public long getPlayedDuration() {
		try {
			return super.getPlayedDuration();
		} catch (Exception e) {
			//e.printStackTrace();
			return 0;
		}
	}

}
