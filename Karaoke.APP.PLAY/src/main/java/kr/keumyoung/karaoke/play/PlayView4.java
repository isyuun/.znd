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
 * filename	:	PlayView4.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayView4.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import kr.keumyoung.karaoke.api._Const;
import kr.kymedia.karaoke.play.AudioTrackPlay2;
import kr.kymedia.karaoke.play.MediaPlayerPlay2;
import kr.kymedia.karaoke.play.SongPlay;
import kr.kymedia.karaoke.play._SoundTouchPlay;
import kr.kymedia.karaoke.play.impl.ISongPlay;

/**
 * <pre>
 * 재시도기능추가(10초/3회)
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @see SongPlay
 * @see AudioTrackPlay2
 * @see MediaPlayerPlay2
 * @see _SoundTouchPlay
 * @since 2015. 3. 12.
 */
class PlayView4 extends PlayView3 {
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

	public PlayView4(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView4(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView4(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView4(Context context) {
		super(context);
	}

	final static private int TIMER_RETRY = _Const.TIMER_MP3_RETRY;
	final static private int COUNT_RETRY = _Const.COUNT_MP3_RETRY;

	final Handler handler = new Handler();

	/**
	 * 재시도여부
	 */
	private boolean isRetry = true;

	/**
	 * 재시도여부
	 */
	public boolean isRetry() {
		return isRetry;
	}

	/**
	 * 재시도여부
	 */
	public void setIsRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}

	Timer retryTimer;

	private void startTry(String method) {
		if (isRetry) {
			retryTimer = new Timer();
			RetryTask retryTask = new RetryTask();
			retryTimer.schedule(retryTask, TIMER_RETRY, TIMER_RETRY);
		}
		Log.wtf(_toString(), "startTry() " + method + ":" + isRetry + ":" + count + ":" + retryTimer);
	}

	protected void stopTry(String method) {
		Log.wtf(_toString(), "stopTry() " + method + ":" + isRetry + ":" + count + ":" + retryTimer);
		try {
			if (retryTimer != null) {
				retryTimer.cancel();
				retryTimer.purge();
				retryTimer = null;
			}
			count = 0;
		} catch (Exception e) {

			e.printStackTrace();
		}
		Log.wtf(_toString(), getMethodName() + retryTimer);
	}

	public void cancel() {
		if (BuildConfig.DEBUG) Log.wtf(_toString(), getMethodName() + count);
		stopTry(getMethodName());
	}

	class RetryTask extends TimerTask {
		@Override
		public void run() {
			// sendMessage(COMPLETE_TIMER_HIDE_MESSAGE_COMMON);
			handler.removeCallbacks(retry);
			handler.post(retry);
		}
	}

	private final Runnable retry = new Runnable() {

		@Override
		public void run() {

			retry(getMethodName());
		}
	};

	int count = 0;

	private void retry(String method) {
		Log.wtf(_toString(), "retry() " + method + ":" + isRetry + ":" + count + ":" + isPlaying() + ":" + retryTimer);

		if (!isRetry) {
			stopTry(getMethodName());
			return;
		}

		if (isPlaying()) {
			stopTry(getMethodName());
			return;
		}

		try {
			if (count < COUNT_RETRY) {
				stop();
				reset();
				release();
				load(path);
				count++;
				Log.wtf(_toString() + TAG_SING, "onRetry() " + "(" + count + ")");
				onRetry(count);
				onTimeout(TIMER_RETRY);
			} else {
				//if (BuildConfig.DEBUG) Log.w(_toString(), getMethodName() + "[RO]" + count);
				ISongPlay.ERROR t = ISongPlay.ERROR.TRYOUT;
				Exception e = new Exception("RETRY OUT ERROR(" + count + ")");
				Log.wtf(_toString() + TAG_SING, "onError() " + "(" + t + ", " + e + ")"/* + player.getPath() */ + "\n" + Log.getStackTraceString(e));
				onError(t, e);
				stop();
				cancel();
				stopTry(getMethodName());
			}
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(_toString() + TAG_ERR, "[NG]" + getMethodName() + count);
			e.printStackTrace();
			stop();
			cancel();
			stopTry(getMethodName());
		}
		// if (BuildConfig.DEBUG) _LOG.e(_toString(), getMethodName() + "[ED]" + count);
	}

	@Override
	public void open() throws Exception {

		if (BuildConfig.DEBUG) Log.w(_toString(), getMethodName() + "[ST]");
		Log.wtf(_toString(), "load() " + count);

		super.open();
		cancel();
		startTry(getMethodName());

		if (BuildConfig.DEBUG) Log.w(_toString(), getMethodName() + "[ED]");
	}

	@Override
	protected void stream() throws Exception {

		Log.d(_toString(), "stream() " + "[ST]" + count);

		super.stream();

		Log.d(_toString(), "stream() " + "[ED]" + count);
	}

	@Override
	protected void local() throws Exception {

		Log.d(_toString(), "local() " + "[ST]" + count);

		super.local();

		Log.d(_toString(), "local() " + "[ED]" + count);
	}

	@Override
	public boolean play() {
		boolean ret = false;

		Log.d(_toString(), getMethodName() + "[ST]" + ret);

		Log.wtf(_toString(), getMethodName() + count);
		try {
			ret = super.play();
			stopTry(getMethodName());
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(_toString() + TAG_ERR, "[NG]" + getMethodName());
			e.printStackTrace();
			onError(ISongPlay.ERROR.MEDIAPLAYERPLAY, e);
		}

		Log.d(_toString(), getMethodName() + "[ED]" + ret);
		return ret;
	}

	@Override
	public void onPrepared() {
		super.onPrepared();
		stopTry(getMethodName());
	}
}
