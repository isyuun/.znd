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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.PLAY4
 * filename	:	SoundTouchPlayer.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play
 *    |_ SoundTouchPlayer.java
 * </pre>
 */

package kr.kymedia.karaoke.play;

import android.content.Context;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

import kr.kymedia.karaoke.play.soundtouch.OnProgressChangedListener;
import kr.kymedia.karaoke.play.soundtouch.SoundTouchPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play3.SongPlayListener;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * TODO<br>
 * <p/>
 * <p/>
 * <pre></pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2014. 7. 7.
 */
class SoundTouchPlay extends SongPlay implements ISongPlay, ISongPlay.Listener, OnProgressChangedListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	protected String _toString() {

		// return super.toString();
		return (BuildConfig.DEBUG ? __CLASSNAME__ : /*getClass().getSimpleName()*/"SoundTouchPlay") + '@' + Integer.toHexString(hashCode()) + ":id:" + id;
	}

	private final Context context;

	private volatile SoundTouchPlayer st;

	int id = 0;

	public int getID() {
		return id;
	}

	private static SparseBooleanArray tracks = new SparseBooleanArray();

	private int putID() {
		// Log.i(_toString(), getMethodName() + tracks);
		int id = 0;
		while (tracks.get(id)) {
			id++;
		}
		if (id < MAX_TRACKS) {
			tracks.put(id, true);
		} else {
			id = -1;
		}
		Log.i(_toString(), getMethodName() + "id:" + id + tracks);
		return id;
	}

	private int delID() {
		if (id < 0) {
			return id;
		}
		if (tracks.get(id)) {
			tracks.delete(id);
		}
		Log.i(_toString(), getMethodName() + "id:" + id + tracks);
		return id;
	}

	private volatile boolean isOpening = false;
	private volatile boolean isPlaying = false;

	public SoundTouchPlay(Context context) {
		super();
		Log.i(_toString(), getMethodName());
		this.context = context;
		id = putID();
	}

	@Override
	protected void finalize() throws Throwable {

		Log.wtf(_toString(), getMethodName());
		super.finalize();

	}

	@Override
	public void reset() {

		Log.wtf(_toString(), getMethodName());
		super.reset();
		if (open != null) {
			open.interrupt();
		}
		open = null;

		if (st != null) {
			st.stop();
			st.reset();
		}
		st = null;

		stopTimeout();

		isPlaying = false;
		isOpening = false;

	}

	@Override
	public void release() {

		Log.wtf(_toString(), getMethodName());
		super.release();

		reset();

		delID();

		st = null;

		setHandler(null);
		setOnListener(null);
		setPath(null);
	}

	@Override
	public void setPath(String path) {
		Log.i(_toString(), getMethodName());
		super.setPath(path);
	}

	@Override
	public void setOnListener(Listener listener) {
		Log.i(_toString(), getMethodName() + listener);
		super.setOnListener(listener);
	}

	@Override
	public Handler getHandler() {
		Log.i(_toString(), getMethodName());
		return super.getHandler();
	}

	@Override
	public void setHandler(Handler handler) {
		Log.i(_toString(), getMethodName() + handler);
		super.setHandler(handler);
	}

	private volatile open open;

	/**
	 * <pre>
	 * </pre>
	 *
	 * @see SongPlay#load(java.lang.String)
	 */
	@Override
	public boolean load(String path) {
		Log.wtf(_toString(), getMethodName());

		if (isOpening || (open != null && open.isAlive())) {
			Log.wtf(_toString(), getMethodName() + "[OK]");
			//onError(ERROR.OPENING, new Exception("Opening... Please Wait..."));
			return true;
		}

		if (TextUtils.isEmpty(path)) {
			Log.wtf(_toString(), getMethodName() + "[NG]");
			onError(ERROR.OPENING, new Exception("Opening... Path Error!!!" + path));
			return false;
		}

		setPath(path);

		try {
			if (open != null /*&& load.isAlive()*/) {
				open.interrupt();
			}
			open = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		open = new open();
		(open).execute();

		return true;
	}

	// 븅신...지랄~~~
	// private class load extends AsyncTask<Void, Integer, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// Log.e(SoundTouchPlay.this._toString(), getMethodName());
	//
	// load();
	// return null;
	// }
	//
	// }
	private class open extends Thread {
		@Override
		public void run() {
			Log.e(SoundTouchPlay.this._toString(), getMethodName());
			super.run();
			open();
		}

		public void execute() {
			start();
		}
	}

	/**
	 * <pre>
	 * 재시도:시작
	 * </pre>
	 */
	private void startTry(String method) {
		Log.wtf(_toString(), "startTry() " + method + ":" + isRetry + ":" + count);

		count++;

		onRetry(count);

		if (repeat) {
			// repeat();
			// AsycTaskExcuter.executePriorityAsyncTask(new repeat());
			(new repeat()).execute();
		} else {
			// restart();
			// AsycTaskExcuter.executePriorityAsyncTask(new restart());
			(new restart()).execute();
		}
	}

	private void stopTry(String method) {
		Log.wtf(_toString(), "stopTry() " + method + ":" + isRetry + ":" + count);
		count = 0;
	}

	/**
	 * 재시도:횟수
	 */
	private int count = 0;

	/**
	 * <pre>
	 * 재시도:방법
	 *  재시작(오픈:O):false
	 *  재반복(오픈:X):true
	 * </pre>
	 */
	private boolean repeat = false;

	private Runnable tryout = new Runnable() {
		@Override
		public void run() {
			startTry("tryout() ");
		}
	};

	/**
	 * 재시도:시도
	 */
	private void tryout(final boolean repeat, final Exception e) {
		Log.wtf(_toString(), getMethodName() + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + repeat + "," + e);
		stopTimeout();

		// 재생중인데?
		if (isPlaying) {
			return;
		}

		// Exception확인
		if (!(e instanceof IOException)) {
			Log.wtf(_toString(), getMethodName() + "[NG][IO]" + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + repeat + "," + e);
			onError(ERROR.SOUNDTOUCHPLAY, e);
			stopTry(getMethodName());
		}

		if (!isRetry) {
			Log.wtf(_toString(), getMethodName() + "[NG][RT]" + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + repeat + "," + e);
			onError(ERROR.IOEXCEPTION, e);
			stopTry(getMethodName());
			return;
		}

		this.repeat = repeat;

		if (count < TIME_TRYOUT) {
			postDelayed(tryout, 1000);
		} else {
			Log.wtf(_toString(), getMethodName() + "[NG][TR]" + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + repeat + "," + e);
			String msg = ("Opening... Tryout Error!!!");
			if (isPlaying) {
				msg = ("Playing... Tryout Error!!!");
			}
			if (count > 0) {
				onError(ERROR.TRYOUT, new Exception(msg));
			} else {
				onError(ERROR.IOEXCEPTION, e);
			}
			stopTry(getMethodName());
		}
	}

	// 븅신...지랄~~~
	private class restart extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			restart();
			return null;
		}

	}

	// private class restart extends Thread {
	// @Override
	// public void run() {
	// Log.i(_toString(), getMethodName());
	// super.run();
	// restart();
	// }
	//
	// public void execute() {
	// start();
	// }
	// }

	@Override
	public void restart() {
		Log.wtf(_toString(), getMethodName() + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + this);


		super.restart();
	}

	private class repeat extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			repeat();
			return null;
		}

	}

	// private class repeat extends Thread {
	// @Override
	// public void run() {
	// Log.i(_toString(), getMethodName());
	// super.run();
	// repeat();
	// }
	//
	// public void execute() {
	// start();
	// }
	// }

	@Override
	public void repeat() {
		Log.wtf(_toString(), getMethodName() + isOpening + ":" + isPlaying + ":" + isRetry + ":" + count + "-" + this);


		super.repeat();
	}

	private void startTimeout(long delay, boolean retry) {
		Log.d(_toString(), getMethodName() + delay + "," + retry + ":" + timeout);
		try {
			removeCallbacks(onTimeout);
			this.timeout = new Timer();
			this.timeout.schedule(new onTimeout(retry), delay);
			this.delay = delay;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void stopTimeout() {
		try {
			if (timeout != null) {
				Log.d(_toString(), getMethodName() + delay + ":" + timeout);
				timeout.cancel();
				timeout.purge();
				timeout = null;
			}
			removeCallbacks(onTimeout);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	Timer timeout;
	long delay;

	private class onTimeout extends TimerTask {

		/**
		 * 재시도여부:타임아웃시
		 */
		boolean retry;

		public onTimeout(boolean retry) {
			super();
			this.retry = retry;
		}

		@Override
		public void run() {
			Log.wtf(_toString(), "onTimeout.run()" + ":" + retry);

			cancel();
			stopTimeout();
			if (retry) {
				String msg = ("Opening... Timeout Error!!!");
				if (isPlaying) {
					msg = ("Playing... Timeout Error!!!");
				}
				tryout(!isOpening, new IOException(msg));
			} else {
				postDelayed(onTimeout, TIME_RESTART);
			}
		}
	}

	private final Runnable onTimeout = new Runnable() {
		@Override
		public void run() {
			try {
				String msg = ("Opening... Timeout Error!!!");
				if (isPlaying) {
					msg = ("Playing... Timeout Error!!!");
				}
				stop();
				onTimeout(delay);
				onError(ERROR.TIMEOUT, new Exception(msg));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	};

	@Override
	public void setOnListener(SongPlayListener listener) {

		// super.setOnListener(listener);
		setOnListener((Listener) listener);
	}

	private final Runnable onPrepared = new Runnable() {
		@Override
		public void run() {
			onPrepared();
		}
	};

	@Override
	public void onPrepared() {
		Log.e(_toString(), getMethodName() + "id:" + id + tracks);
		super.onPrepared();
	}

	@Override
	public void onError(ERROR t, Exception e) {
		super.onError(t, e);
		reset();
	}

	/**
	 * <pre>
	 *   문제는 synchronization이 너무 비싸다는 데에 있다. 우리는 저렇게 비싼걸 접근시 매번 불러주기는 싫다.
	 *   그래서, 아래처럼 double checked locking이라는 요상한 방법을 고안해낸다.
	 * </pre>
	 * <a href ="https://www.google.co.kr/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&ved=0ahUKEwiQg4iIjKbKAhVLjpQKHcRtCOMQFggcMAA&url=http%3A%2F%2Fkwanseob.blogspot.com%2F2012%2F08%2Fjava-volatile.html&usg=AFQjCNGarJ2kMZu-JLhMgHxQmWxg8_PvxQ&sig2=RO1hK7t0Pbh8NjxVvbFyYQ&bvm=bv.111677986,d.dGo&cad=rja">
	 * Dream Repository: Java의 volatile 키워드에 대한 이해
	 * </a>
	 */
	private void get(String path) throws Exception {
		//Log.wtf(_toString(), getMethodName() + path);
		if (BuildConfig.DEBUG) {
			Log.wtf(_toString(), getMethodName() + path);
		} else {
			Log.wtf(_toString(), getMethodName());
		}

		if (TextUtil.isEmpty(path)) {
			throw (new Exception("SoundTouchPlay getId() Error..."));
		}

		if (id < 0) {
			throw (new Exception("SoundTouchPlay getId() Error..."));
		}

		isOpening = true;

		if (st == null) {
			synchronized (this) {
				if (st == null) {
					st = new SoundTouchPlayer(this.context, this, path, id, 1, 0);
				}
			}
		}

		// Log.i(_toString(), getMethodName() + st);

		// test
		// throw (new IOException("SoundTouchPlay get() TEST...ERROR..."));

		isOpening = false;
	}

	@Override
	public void onTrackEnd(int track) {

		Log.w(_toString(), getMethodName());
		postDelayed(new Runnable() {
			@Override
			public void run() {
				onCompletion();
			}
		}, TIME_RESTART);
	}

	@Override
	public void onExceptionThrown(String string) {
		Log.w(_toString(), getMethodName());
		onError(ERROR.SOUNDTOUCHPLAY, new Exception(string));
	}

	@Override
	public void onProgressChanged(int track, double currentPercentage, long position) {

		// Log.e(_toString(), getMethodName() + track + "," + currentPercentage + "," + position);

		isOpening = false;
		isPlaying = true;

		//stopTry(getMethodName());

		if (timeout != null) {
			Log.e(_toString(), getMethodName() + track + "," + currentPercentage + "," + position);
			stopTimeout();
		}
	}

	@Override
	protected void onTime() {
		// if (timeout != null) {
		// Log.e(_toString(), getMethodName());
		// stopTimeout();
		// }
		super.onTime();
	}

	@Override
	public void onTime(int t) {
		// if (timeout != null) {
		// Log.e(_toString(), getMethodName() + t);
		// stopTimeout();
		// }
		super.onTime(t);
	}

	/**
	 * 재시도여부:오류발생시
	 */
	private volatile boolean isRetry = false;

	/**
	 * 재시도여부
	 */
	public void setIsRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}

	/**
	 * 재시도여부
	 */
	public boolean isRetry() {
		return this.isRetry;
	}

	private void open() {
		Log.wtf(_toString(), getMethodName() + "id:" + id + tracks);

		String path = getPath();

		try {
			synchronized (open) {
				if (!isOpening) {
					get(getPath());
				}

				if (!isOpening && path != null && path.equalsIgnoreCase(getPath())) {
					post(onPrepared);
				}
			}
		} catch (final Exception e) {
			Log.wtf(_toString(), getMethodName() + "[NG]" + Log.getStackTraceString(e));
			stopTimeout();
			tryout(false, e);
		}

		isOpening = false;
		isPlaying = false;

		startTimeout(TIME_TIMEOUT_OPEN, isRetry);
	}

	@Override
	public boolean isPrepared() {
		return (super.isPrepared() & !isOpening);
	}

	/**
	 * <pre>
	 * 스레드처리주의
	 * </pre>
	 *
	 * @see SongPlay#play()
	 */
	@Override
	public boolean play() {

		Log.wtf(_toString(), getMethodName() + (st != null ? st.isFinished() : false) + ":" + st);

		try {
			synchronized (st) {
				if (st != null && isPrepared()) {

					st.play();

					stopTimeout();
					startTimeout(TIME_TIMEOUT_PLAY, isRetry);

					stopOnTime();
					startOnTime();

					isOpening = false;
					isPlaying = false;

					return true;
				}
			}
		} catch (final Exception e) {
			Log.wtf(_toString(), getMethodName() + "[NG]" + Log.getStackTraceString(e));
			stopTimeout();
			tryout(false, e);
		}

		return false;
	}

	@Override
	public void stop() {
		Log.wtf(_toString(), getMethodName());

		stopOnTime();

		if (st != null) {
			st.stop();
		}

		isPlaying = false;
		isOpening = false;

		reset();
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#close()
	 */
	@Override
	public void close() {
		Log.i(_toString(), getMethodName());
		release();
	}

	@Override
	public void pause() {
		if (st != null && isPrepared()) {
			st.pause();
		}
	}

	/**
	 * <pre>
	 * Tempo must be between 0.5 and 2.0
	 * </pre>
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempo(float)
	 */
	@Override
	public void setTempo(float tempo) {
		Log.wtf(_toString(), getMethodName() + tempo);
		if (st != null && isPrepared()) {
			//if (tempo < 0.5f || tempo > 2.0f) {
			//	Log.wtf(_toString(), "Tempo must be between 0.5f and 2.0f");
			//	return;
			//}
			st.setTempo(tempo);
		}
	}

	/**
	 * <pre>
	 * Tempo percentage must be between 0.5 and 2.0
	 * </pre>
	 *
	 * @see SongPlay#getTempo()
	 */
	@Override
	public float getTempo() {
		if (st != null) {
			return st.getTempo();
		} else {
			return 0;
		}
	}

	/**
	 * <pre>
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 * </pre>
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	@Override
	public void setTempoPercent(int percent) {
		if (st != null && isPrepared()) {
			//if (percent < -50 || percent > 100) {
			//	Log.wtf(_toString(), "Tempo percentage must be between -50 and 100");
			//	return;
			//}
			float temp = Math.round((0.01f * percent) * 100.0f) / 100.0f;
			float tempo = (1.00f + temp);
			Log.wtf(_toString(), getMethodName() + percent + "->" + tempo + ":" + temp);
			setTempo(tempo);
		}
	}

	/**
	 * <pre>
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 * </pre>
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	@Override
	public int getTempoPercent() {
		if (st != null) {
			float percent = st.getTempo() * 100f - 100f;
			// Log.wtf(_toString(), getMethodName() + percent + ":" + (int) percent + ":" + st.getTempo());
			int ret = Math.round(percent);
			return ret;
		} else {
			return 0;
		}
	}

	/**
	 * <pre>
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Pitch -1 and +1 octave must be between -12 and 12
	 * </pre>
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setPitchSemi(float)
	 */
	@Override
	public void setPitch(int pitch) {

		Log.wtf(_toString(), getMethodName() + pitch);
		setPitchSemi(pitch);
	}

	/**
	 * <pre>
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Pitch -1 and +1 octave must be between -12 and 12
	 * </pre>
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setPitchSemi(float)
	 */
	private void setPitchSemi(float pitch) {
		if (st != null) {
			st.setPitchSemi(pitch);
		}
	}

	@Override
	public int getPitch() {
		return (int) getPitchSemi();
	}

	private float getPitchSemi() {
		if (st != null) {
			return st.getPitchSemi();
		} else {
			return 0.0f;
		}
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#seek(int)
	 */
	@Override
	public void seek(int msec) {

		if (st != null && isPrepared()) {
			st.seekTo(msec * USEC2MSEC, false);
		}
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#getTotalTime()
	 */
	@Override
	public int getTotalTime() {

		return (int) getDuration();
	}

	private long getDuration() {

		try {
			if (st != null) {
				return st.getDuration() / USEC2MSEC;
			} else {
				return 0;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#getCurrentTime()
	 */
	@Override
	public int getCurrentTime() {
		// Log.i(_toString(), getMethodName() + st.getPlayedDuration() + "-" + st.getPlayedDuration() / USEC2MSEC);

		try {
			if (st != null) {
				return (int) (st.getPlayedDuration() / USEC2MSEC);
			} else {
				return 0;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#isPlaying()
	 */
	@Override
	public boolean isPlaying() {

		if (isOpening) {
			return false;
		}
		if (st != null) {
			return isPlaying;
		} else {
			return false;
		}
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @see SongPlay#isPause()
	 */
	@Override
	public boolean isPause() {

		if (isOpening) {
			return false;
		}
		if (!isPlaying) {
			return false;
		}
		if (st != null) {
			return st.isPaused();
		} else {
			return false;
		}
	}

	@Override
	public float getMinVolume() {

		return AudioTrack.getMinVolume();
	}

	@Override
	public float getMaxVolume() {

		return AudioTrack.getMaxVolume();
	}

	float left, right;

	@Override
	public void setVolume(float left, float right) {

		if (left >= getMinVolume() && left <= getMaxVolume()) {
			this.left = left;
		} else {
			return;
		}

		if (right >= getMinVolume() && right <= getMaxVolume()) {
			this.right = right;
		} else {
			return;
		}

		if (st != null && isPrepared()) {
			st.setVolume(left, right);
		}
	}

	@Override
	public void start() {


	}

	@Override
	public void prepare() {


	}

	@Override
	public void setValance(float LR) {


	}

	@Override
	public void onError() {


	}

}
