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
 * filename	:	MediaPlayerPlay2.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2
 *    |_ MediaPlayerPlay2.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play;

import java.util.Timer;
import java.util.TimerTask;

import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play3.SongPlayListener;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 7. 25.
 * @version 1.0
 */
public class MediaPlayerPlay2 extends MediaPlayerPlay implements ISongPlay, ISongPlay.Listener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
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
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()) + ":id:" + id;
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

	private int id = -1;

	@Override
	public boolean open(String path) {
		Log.i(__CLASSNAME__, getMethodName());

		this.path = path;
		isPrepared = false;

		boolean ret = false;

		try {
			ret = super.open(path);

			id = mMediaPlayer.getAudioSessionId();
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnPreparedListener(this);

		} catch (Exception e) {

			e.printStackTrace();
			onError(ERROR.MEDIAPLAYERPLAY, e);
		}

		return ret;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		onError(ERROR.MEDIAPLAYERPLAY, new Exception("MediaPlayer.onError - (what:" + what + ", extra:" + extra + ")"));
		return true;
	}

	@Override
	public void onError(final ERROR t, final Exception e) {

		Log.e(toString(), getMethodName() + "\n" + t + "\n" + Log.getStackTraceString(e));

		final String err = e.getMessage();

		post(new Runnable() {

			@Override
			public void run() {

				if (listener != null && listener instanceof Listener) {
					listener.onError(t, new Exception(MediaPlayerPlay2.this.toString() + "\n" + err));
				}
			}
		});
	}

	@Deprecated
	void startTimeout() {
		try {
			if (timeout != null) {
				timeout.cancel();
				timeout.purge();
			}

			timeout = new Timer();

			if (timeout != null) {
				timeout.schedule(new onTimeout(), TIME_TIMEOUT_OPEN);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Deprecated
	void stopTimeout() {
		try {
			if (timeout != null) {
				timeout.cancel();
				timeout.purge();
				timeout = null;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	Timer timeout;

	class onTimeout extends TimerTask {

		@Override
		public void run() {
			Log.e(toString(), this.toString() + getMethodName());

			try {
				cancel();
				stop();
				onTimeout(TIME_TIMEOUT_OPEN);
				onError(ERROR.TIMEOUT, new Exception("Opening... Timeout Error!!!"));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	@Override
	public void onTimeout(final long timeout) {

		post(new Runnable() {

			@Override
			public void run() {
				Log.d(toString(), getMethodName() + listener);

				if (listener != null) {
					listener.onTimeout(timeout);
				}
			}
		});

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

	@Override
	public boolean play() {
		Log.w(toString(), getMethodName());

		boolean ret = super.play();

		if (ret) {
			startOnTime();
		}

		return ret;
	}

	@Override
	public void stop() {
		Log.w(toString(), getMethodName());

		stopOnTime();
		reset();

		super.stop();
	}

	@Override
	public void close() {
		Log.w(toString(), getMethodName());

		reset();

		super.close();
	}

	private void reset() {
		mPlaying = false;
		mPause = false;
		isPrepared = false;
	}

	@Override
	public boolean isPlaying() {

		return super.isPlaying();
	}

	@Override
	public boolean isPause() {

		return super.isPause();
	}

	@Override
	public boolean isSeek() {

		return super.isSeek();
	}

	@Override
	public void seek(int sec) {
		Log.w(toString(), getMethodName() + (sec / USEC2MSEC));

		super.seek((int) (sec / USEC2MSEC));
	}

	private void onTime() {
		int t = mMediaPlayer.getCurrentPosition();
		onTime(t);
	}

	@Override
	public void onTime(final int t) {
		// Log.w(toString(), getMethodName() + t);

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
	public int getCurrentTime() {
		// Log.e(toString(), getMethodName() + isPrepared);

		super.getCurrentTime();

		// return mMediaPlayer.getCurrentPosition();
		if (isPrepared) {
			return mMediaPlayer.getCurrentPosition();
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalTime() {
		// Log.e(toString(), getMethodName() + isPrepared);

		super.getTotalTime();

		// return mMediaPlayer.getDuration();
		if (isPrepared) {
			return mMediaPlayer.getDuration();
		} else {
			return 0;
		}

	}

	@Override
	public void start() {


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
	public void onPrepared(MediaPlayer mp) {
		// Log.e(toString(), getMethodName() + isPrepared + ":" + mp);

		onPrepared();
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

	@Override
	public void onRetry(final int count) {

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
	public void onBufferingUpdate(final int percent) {

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

		if (this.listener != null) {
			listener.onReady(count);
		}
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
	public void repeat() {
		Log.e(this.toString(), getMethodName());


		if (mMediaPlayer != null) {
			// seek(0);
			postDelayed(new Runnable() {

				@Override
				public void run() {

					play(0);
				}
			}, TIME_RESTART);
			// mMediaPlayer.setLooping(true);
		}
	}

	@Override
	public void restart() {
		Log.e(this.toString(), getMethodName());


		stop();
		postDelayed(new Runnable() {

			@Override
			public void run() {

				open(path);
			}
		}, TIME_RESTART);
	}

	// private class restart extends PriorityAsyncTask<Void, Void, Boolean> {
	//
	// @Override
	// protected Boolean doInBackground(Void... params) {
	//
	// restart();
	// return null;
	// }
	//
	// }

	@Override
	public void destroy() {

		super.destroy();

		release();
	}

	@Override
	public void release() {

		reset();

		this.listener = null;
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

		if (mMediaPlayer != null) {
			mMediaPlayer.setVolume(left, right);
		}
	}

	@Override
	public void setValance(float LR) {

		if (mMediaPlayer != null) {
			mMediaPlayer.setVolume(LR, LR);
		}
	}
}
