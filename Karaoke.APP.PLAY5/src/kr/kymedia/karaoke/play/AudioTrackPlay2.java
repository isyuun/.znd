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
 * project	:	Karaoke.PLAY4
 * filename	:	AudioTrackPlay.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4
 *    |_ AudioTrackPlay.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play;

import java.util.Timer;
import java.util.TimerTask;

import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play3.AudioTrackPlay;
import kr.kymedia.karaoke.play3.SongPlayListener;

import android.os.Handler;
import android.util.Log;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 10. 20.
 * @version 1.0
 */
@Deprecated
public class AudioTrackPlay2 extends AudioTrackPlay implements ISongPlay, ISongPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	@Override
	public boolean load(String path) {

		isPrepared = false;
		return super.load(path);
	}

	@Override
	public boolean play() {

		return super.play();
	}

	@Override
	public void onPrepared() {

		isPrepared = true;
		Log.e(toString(), getMethodName() + isPrepared + ":" + listener);

		super.onPrepared();

		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onPrepared();
				}
			}
		});
	}

	@Override
	public void onCompletion() {

		super.onCompletion();
		postDelayed(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onCompletion();
				}
			}
		}, 100);
	}

	private TimerTask mTask;
	private Timer mTimer;

	private void startOnTime() {
		mTask = new TimerTask() {

			@Override
			public void run() {

				onTime();
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTask, 10, 10);
	}

	private void stopOnTime() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
	}

	private void onTime() {
		int t = getCurrentTime();
		onTime(t);
	}

	@Override
	public void onTime(final int t) {
		Log.e(__CLASSNAME__, getMethodName() + t);

		super.onTime(t);

		// if (this.listener != null) {
		// this.listener.onTime(t);
		// }
		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onTime(t);
				}
			}
		});
	}

	@Override
	public void onError(ERROR t, Exception e) {


	}

	@Override
	public void onRetry(int count) {


	}

	@Override
	public void onTimeout(long timeout) {


	}

	private String path;

	@Override
	public void setPath(String path) {
		Log.e(toString(), getMethodName() + " - " + path);

		this.path = path;
	}

	@Override
	public String getPath() {

		return this.path;
	}

	@Override
	public void prepare() {


	}

	private boolean isPrepared = false;

	@Override
	public boolean isPrepared() {

		return isPrepared;
	}

	@Override
	public void pause() {

		setPause();
	}

	@Override
	public void setTempoPercent(int percent) {

	}

	@Override
	public int getTempoPercent() {
		return 0;
	}

	@Override
	public void restart() {

		Log.e(this.toString(), getMethodName());


		stop();
		postDelayed(new Runnable() {

			@Override
			public void run() {

				load(path);
			}
		}, TIME_RESTART);
	}

	@Override
	public void repeat() {

		Log.e(this.toString(), getMethodName());


		// seek(0);
		postDelayed(new Runnable() {

			@Override
			public void run() {

				play(0);
			}
		}, TIME_RESTART);
		// mMediaPlayer.setLooping(true);
	}

	@Override
	public void release() {


	}

	private Handler handler;

	@Override
	public void setHandler(Handler handler) {

		this.handler = handler;
	}

	private void post(Runnable r) {
		if (handler != null) {
			handler.post(r);
		}
	}

	private void postDelayed(Runnable r, long delayMillis) {
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	Listener listener;

	@Override
	public void setOnListener(Listener listener) {
		Log.e(toString(), getMethodName() + (listener instanceof Listener) + ":" + listener);

		super.setOnListener(this);
		this.listener = listener;
	}

	@Override
	public void setOnListener(SongPlayListener listener) {
		Log.i(toString(), getMethodName() + (listener instanceof Listener) + ":" + listener);

		super.setOnListener(this);
		this.listener = (Listener) listener;
	}

	@Override
	public float getMinVolume() {

		return 0.0f;
	}

	@Override
	public float getMaxVolume() {

		return 1.0f;
	}

	@Override
	public void setVolume(float left, float right) {

	}

	@Override
	public void setValance(float LR) {

	}

	@Override
	public int getTotalTime() {

		int ret = super.getTotalTime();
		Log.e(__CLASSNAME__, getMethodName() + ret);
		return ret;
	}

	@Override
	public int getCurrentTime() {

		int ret = super.getCurrentTime();
		Log.e(__CLASSNAME__, getMethodName() + ret);
		return ret;
	}

}
