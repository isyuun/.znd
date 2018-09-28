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
 * filename	:	Listen4.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ Listen4.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import kr.keumyoung.karaoke._Thread;
import kr.keumyoung.karaoke.api._Const;
import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play.impl.ISongPlay.Listener;

/**
 * <pre>
 * 재시도기능추가(10초/3회)
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 4. 3.
 */
abstract class Listen4 extends Listen3 implements ISongPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public Listen4(Context context) {
		super(context);
	}

	final static private int TIMER_RETRY = _Const.TIMER_MP3_RETRY;
	final static private int COUNT_RETRY = _Const.COUNT_MP3_RETRY;

	Handler handler = new Handler();

	public Handler getHandler() {
		return handler;
	}

	protected void removeCallbacks(Runnable r) {
		if (handler != null) {
			handler.removeCallbacks(r);
		}
	}

	protected void post(Runnable r) {
		removeCallbacks(r);
		if (handler != null) {
			handler.post(r);
		}
	}

	protected void postDelayed(Runnable r, long delayMillis) {
		removeCallbacks(r);
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

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
		Log.wtf(__CLASSNAME__, "startTry() " + method + ":" + isRetry + ":" + count + ":" + retryTimer);
	}

	protected void stopTry(String method) {
		Log.wtf(__CLASSNAME__, "stopTry() " + method + ":" + isRetry + ":" + count + ":" + retryTimer);
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
		Log.wtf(__CLASSNAME__, getMethodName() + retryTimer);
	}

	public void cancel() {
		if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + count);
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
		Log.wtf(__CLASSNAME__, "retry() " + method + ":" + isRetry + ":" + count + ":" + isPlaying() + ":" + retryTimer);

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
				Log.wtf(__CLASSNAME__ + TAG_SING, "onRetry() " + "(" + count + ")");
				onRetry(count);
				onTimeout(TIMER_RETRY);
			} else {
				//if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[RO]" + count);
				ISongPlay.ERROR t = ISongPlay.ERROR.TRYOUT;
				Exception e = new Exception("RETRY OUT ERROR(" + count + ")");
				Log.wtf(__CLASSNAME__ + TAG_SING, "onError() " + "(" + t + ", " + e + ")"/* + player.getPath() */ + "\n" + Log.getStackTraceString(e));
				onError(t, e);
				stop();
				cancel();
				stopTry(getMethodName());
			}
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__ + TAG_ERR, "[NG]" + getMethodName() + count);
			e.printStackTrace();
			stop();
			cancel();
			stopTry(getMethodName());
		}
		// if (BuildConfig.DEBUG) _LOG.e(__CLASSNAME__, getMethodName() + "[ED]" + count);
	}

	@Override
	public void open() throws Exception {
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]");
		Log.wtf(__CLASSNAME__, "load() " + count);
		//super.load();
		(new open()).execute();
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]");
	}

	//private class load extends AsyncTask<Void, Void, Void> {
	//
	//	@Override
	//	protected Void doInBackground(Void... params) {
	//
	//		postDelayed(load, 100);
	//		return null;
	//	}
	//
	//}
	private class open extends _Thread {
		@Override
		public void run() {
			postDelayed(open, 100);
			super.run();
		}
	}

	private final Runnable open = new Runnable() {
		@Override
		public void run() {
			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]");
			try {
				load(url);
				cancel();
				startTry(getMethodName());
			} catch (Exception e) {
				//e.printStackTrace();
				if (BuildConfig.DEBUG) Log.w(__CLASSNAME__ + TAG_ERR, "[NG]" + getMethodName() + url);
				e.printStackTrace();
				onError(ISongPlay.ERROR.MEDIAPLAYERPLAY, e);
			}
			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]");
		}
	};

	@Override
	public boolean play() {
		boolean ret = false;

		Log.d(__CLASSNAME__, getMethodName() + "[ST]" + ret);

		Log.wtf(__CLASSNAME__, getMethodName() + count);
		try {
			ret = super.play();
			stopTry(getMethodName());
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__ + TAG_ERR, "[NG]" + getMethodName());
			e.printStackTrace();
			onError(ISongPlay.ERROR.MEDIAPLAYERPLAY, e);
		}

		Log.d(__CLASSNAME__, getMethodName() + "[ED]" + ret);
		return ret;
	}

	private _Listener listener;

	public void setOnListener(Listener listener) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + (listener instanceof Listener) + ":" + listener);
		this.listener = (_Listener) listener;
	}

	@Override
	public void onPrepared() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
		stopTry(getMethodName());
		if (listener != null) {
			listener.onPrepared();
		}
	}

	@Override
	public void onTime(int t) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + ":" + t + ":" + listener);
		if (listener != null) {
			listener.onTime(t);
		}
	}

	@Override
	public void onCompletion() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
		if (listener != null) {
			listener.onCompletion();
		}
	}

	@Override
	public void onError() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
		if (listener != null) {
			listener.onError();
		}
	}

	@Override
	public void onError(ISongPlay.ERROR t, Exception e) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + t + ":" + e + ":" + listener);
		if (listener != null) {
			listener.onError(t, e);
		}
	}

	@Override
	public void onBufferingUpdate(int percent) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + percent + ":" + listener);
		if (listener != null) {
			listener.onBufferingUpdate(percent);
		}
	}

	@Override
	public void onRelease() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
		if (listener != null) {
			listener.onRelease();
		}
	}

	@Override
	public void onSeekComplete() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
		if (listener != null) {
			listener.onSeekComplete();
		}
	}

	@Override
	public void onReady(int count) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + count + ":" + listener);
		if (listener != null) {
			listener.onReady(count);
		}
	}

	@Override
	public void onRetry(int count) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + count + ":" + listener);
		if (listener != null) {
			listener.onRetry(count);
		}
	}

	@Override
	public void onTimeout(long timeout) {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + timeout + ":" + listener);
		if (listener != null) {
			listener.onTimeout(timeout);
		}
	}

}
