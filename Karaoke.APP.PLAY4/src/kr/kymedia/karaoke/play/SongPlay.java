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
 * filename	:	SongPlay.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play4
 *    |_ SongPlay.java
 * </pre>
 */

package kr.kymedia.karaoke.play;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play3.SongPlayListener;

/**
 * <p/>
 * <pre></pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2014. 9. 23.
 */
public class SongPlay /* extends SongService */ implements ISongPlay, ISongPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	// public SongPlay(String name) {
	// super(name);
	// }

	/**
	 * @see kr.kymedia.karaoke.play3.SongPlay#setOnListener(kr.kymedia.karaoke.play3.SongPlayListener)
	 */
	public void setOnListener(SongPlayListener listener) {


	}

	public void destroy() {


	}

	@Override
	public boolean load(String path) {

		return false;
	}

	public boolean mid_open(String path, String cfg, String patch) {

		return false;
	}

	@Override
	public boolean play() {

		return false;
	}

	public boolean play(int time) {

		return false;
	}

	@Override
	public void stop() {


	}

	@Override
	public void close() {

	}

	public void setPause() {


	}

	public void setResume() {


	}

	@Override
	public void setTempo(float tempo) {


	}

	@Override
	public float getTempo() {

		return 0;
	}

	@Override
	public void setTempoPercent(int percent) {

	}

	@Override
	public int getTempoPercent() {
		return 0;
	}

	@Override
	public void setPitch(int value) {


	}

	@Override
	public int getPitch() {

		return 0;
	}

	@Override
	public void seek(int msec) {


	}

	@Override
	public int getTotalTime() {

		return 0;
	}

	@Override
	public int getCurrentTime() {

		return 0;
	}

	@Override
	public boolean isPlaying() {

		return false;
	}

	@Override
	public boolean isPause() {

		return false;
	}

	public boolean isSeek() {

		return false;
	}

	public boolean getType() {

		return false;
	}

	public void setType(boolean b) {


	}

	public int getAudioSessionID() {

		return 0;
	}

	public void setPrevTime(int time) {


	}

	public void event(OnCompletionListener c, OnErrorListener e, OnPreparedListener p) {


	}

	private String path;

	@Override
	public void setPath(String path) {
		// Log.e(__CLASSNAME__, getMethodName() + " - " + path);

		this.path = path;
	}

	@Override
	public String getPath() {

		return this.path;
	}

	@Override
	public void start() {


	}

	@Override
	public void prepare() {


	}

	@Override
	public void pause() {


	}

	@Override
	public void restart() {

		stop();
		postDelayed(new Runnable() {

			@Override
			public void run() {

				load(getPath());
			}
		}, TIME_RESTART);
	}

	@Override
	public void repeat() {

		// seek(0);
		postDelayed(new Runnable() {

			@Override
			public void run() {

				seek(0);
				onPrepared();
			}
		}, TIME_RESTART);
	}

	@Override
	public void release() {

		listener = null;
		handler = null;
	}

	private Handler handler;

	public Handler getHandler() {
		return this.handler;
	}

	@Override
	public void setHandler(Handler handler) {

		this.handler = handler;
	}

	protected void post(Runnable r) {
		removeCallbacks(r);
		if (handler != null) {
			handler.post(r);
		}
	}

	protected final void postDelayed(Runnable r, long delayMillis) {
		removeCallbacks(r);
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	protected final void removeCallbacks(Runnable r) {
		if (handler != null) {
			handler.removeCallbacks(r);
		}
	}

	private Listener listener;

	@Override
	public void setOnListener(Listener listener) {
		// Log.e(__CLASSNAME__, getMethodName() + (listener instanceof Listener) + ":" + listener);

		this.listener = listener;
	}

	public Listener getOnListener() {
		return listener;
	}

	@Override
	public float getMinVolume() {

		return 0;
	}

	@Override
	public float getMaxVolume() {

		return 0;
	}

	@Override
	public void setVolume(float left, float right) {


	}

	@Override
	public void setValance(float LR) {


	}

	private TimerTask mTask;
	private Timer mTimer;
	int mOnTime = TIME_ONTIME;

	protected void onTime() {
		int t = getCurrentTime();
		onTime(t);
	}

	private final Runnable onTime = new Runnable() {
		@Override
		public void run() {
			onTime();
		}
	};

	protected void startOnTime() {
		mTask = new TimerTask() {

			@Override
			public void run() {

				post(onTime);
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTask, 0, mOnTime);
	}

	protected void stopOnTime() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
	}

	@Override
	public void onTime(final int t) {

		// post(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// if (listener != null) {
		// listener.onTime(t);
		// }
		// }
		// });
		if (listener != null) {
			listener.onTime(t);
		}
	}

	private volatile boolean isPrepared = false;

	@Override
	public boolean isPrepared() {
		return isPrepared;
	}

	@Override
	public void onPrepared() {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		isPrepared = true;
		postDelayed(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onPrepared();
				}
			}
		}, 0);
	}

	@Override
	public void onCompletion() {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		postDelayed(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onCompletion();
				}
			}
		}, 0);
	}

	@Override
	public void onError() {

		Log.e(__CLASSNAME__, getMethodName() + listener);

	}

	@Override
	public void onBufferingUpdate(final int percent) {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onBufferingUpdate(percent);
				}
			}
		});
	}

	@Override
	public void onRelease() {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onRelease();
				}
			}
		});
	}

	@Override
	public void onSeekComplete() {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onSeekComplete();
				}
			}
		});
	}

	@Override
	public void onReady(int count) {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		if (this.listener != null) {
			listener.onReady(count);
		}
	}

	@Override
	public void onError(final ERROR t, final Exception e) {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		Log.i(__CLASSNAME__, "" + t + Log.getStackTraceString(e));

		final String err = e.getMessage();
		// if (listener != null) {
		// listener.onError(t, new Exception(_SoundTouchPlay.this.toString() + "\n" + err));
		// }
		post(new Runnable() {

			@Override
			public void run() {
				Log.d(__CLASSNAME__, getMethodName() + listener);

				if (listener != null) {
					listener.onError(t, new Exception(SongPlay.this.toString() + "\n" + err));
				}
			}
		});
	}

	@Override
	public void onRetry(final int count) {

		Log.e(__CLASSNAME__, getMethodName() + listener);
		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null) {
					listener.onRetry(count);
				}
			}
		});

	}

	@Override
	public void onTimeout(final long timeout) {
		Log.e(__CLASSNAME__, getMethodName() + timeout + ":" + listener);


		// if (listener != null) {
		// listener.onTimeout(timeout);
		// }
		post(new Runnable() {

			@Override
			public void run() {
				Log.d(__CLASSNAME__, getMethodName() + listener);

				if (listener != null) {
					listener.onTimeout(timeout);
				}
			}
		});

	}

	public void reset() {

		isPrepared = false;
	}
}
