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
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	BaseFragmentInterface.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.app
 *    |_ BaseFragmentInterface.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.impl;

import kr.kymedia.karaoke.play3.SongPlayListener;
import android.os.Handler;

/**
 * 
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 2. 28.
 * @version 1.0
 */

public interface ISongPlay {

	/**
	 * MICROsec. to MILIsec.
	 */
	public static long USEC2MSEC = 1000;
	/**
	 * MILIsec. to Sec.
	 */
	public static long MSEC2SEC = 1000;

	public static int TIME_RESTART = 500;
	public static long TIME_TIMEOUT_OPEN = 10 * MSEC2SEC;
	public static long TIME_TIMEOUT_PLAY = 5 * MSEC2SEC;
	public static int TIME_TRYOUT = 3;
	public static int TIME_ONTIME = 10;
	public static int MAX_TRACKS = 16;
	public static int MAX_CHOIRS = 4;

	public static enum TYPE {

		AUDIOTRACKPLAY(0), MEDIAPLAYERPLAY(1), SOUNDTOUCHPLAY(2);

		private final int index;

		TYPE(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	public static enum ERROR {

		AUDIOTRACKPLAY(0), MEDIAPLAYERPLAY(1), SOUNDTOUCHPLAY(2), EXCEPTION(3), IOEXCEPTION(4), HANDLER(
				5), LISTENER(6), OPENING(7), TRYOUT(8), TIMEOUT(9);

		private final int index;

		ERROR(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	public interface Listener extends SongPlayListener {
		// public abstract void onError(Exception e);
		public abstract void onError(final ERROR t, final Exception e);

		public abstract void onRetry(final int count);

		public abstract void onTimeout(final long timeout);

	}

	// public void onError(final ERROR t, final Exception e);
	void setPath(String path);

	String getPath();

	void start();

	void prepare();

	boolean open(final String path) throws Exception;

	boolean play();

	void stop();

	void seek(int msec);

	boolean isPlaying();

	boolean isPause();

	boolean isPrepared();

	void pause();

	// void resume();

	void setPitch(int pitch);

	int getPitch();

	void setTempo(float tempo);

	float getTempo();

	void setTempoPercent(int percent);

	int getTempoPercent();

	int getTotalTime();

	int getCurrentTime();

	void restart();

	void repeat();

	void release();

	void close();

	void setHandler(Handler handler);

	void setOnListener(Listener listener);

	public float getMinVolume();

	public float getMaxVolume();

	public void setVolume(float left, float right);

	/**
	 * <pre>
	 * 전체볼륨아이다~~~
	 * </pre>
	 */
	public void setValance(float LR);
}
