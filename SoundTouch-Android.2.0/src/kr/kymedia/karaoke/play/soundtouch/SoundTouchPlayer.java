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

import com.smp.soundtouchandroid.OnProgressChangedListener;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015-10-13
 */
public class SoundTouchPlayer extends SoundStreamAudioPlayer {

	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	private Context context;

	public SoundTouchPlayer(Context context, OnProgressChangedListener progressListener,
	                        String fileName, int id, float tempo, float pitchSemi)
			throws Exception {
		super(context, id, fileName, tempo, pitchSemi);
		this.context = context;
		setOnProgressChangedListener(progressListener);
	}

	//public SoundTouchPlayer(int id, String fileName, float tempo, float pitchSemi)
	//		throws Exception {
	//	super(id, fileName, tempo, pitchSemi);
	//}

	Thread thread = null;

	//@Override
	public void play() {
		Log.e(__CLASSNAME__, getMethodName() + isFinished() + ":" + this);
		if (thread == null) {
			(thread = new Thread(this)).start();
		} else {
		}
		start();
	}

	@Override
	public void pause() {
		Log.e(__CLASSNAME__, getMethodName() + isPaused() + ":" + this);
		super.pause();
	}

	@Override
	public void stop() {
		super.stop();
		finished = true;
		if (thread != null && !thread.isInterrupted()) thread.interrupt();
		thread = null;
	}

	public void reset() {
		finished = true;
		if (thread != null && !thread.isInterrupted()) thread.interrupt();
		thread = null;
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
