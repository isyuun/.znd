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
 * filename	:	SongPlayView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ SongPlayView.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.play.AudioTrackPlay2;
import kr.kymedia.karaoke.play.MediaPlayerPlay2;
import kr.kymedia.karaoke.play._SoundTouchPlay;
import kr.kymedia.karaoke.play.impl.ISongPlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 7. 17.
 * @version 1.0
 */
public class SongPlayView extends LinearLayout implements ISongPlay, _SoundTouchPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public SongPlayView(Context context) {
		super(context);
		Log.i(__CLASSNAME__, getMethodName() + type);
		if (!isInEditMode()) {
			init(type);
		}
	}

	public SongPlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(__CLASSNAME__, getMethodName() + type);
		if (!isInEditMode()) {
			init(type);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SongPlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.i(__CLASSNAME__, getMethodName() + type);
		if (!isInEditMode()) {
			init(type);
		}
	}

	@Override
	public void start() {


	}

	@Override
	public void prepare() {


	}

	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();

		// if (!isInEditMode()) {
		// init();
		// }
		Log.e(toString(), getMethodName() + song);

		putIndex();
	}

	@Override
	protected void onDetachedFromWindow() {

		super.onDetachedFromWindow();
		release();
	}

	protected int putIndex() {
		index = ((ViewGroup) getParent()).indexOfChild(this);
		return index;
	}

	protected ISongPlay song;

	public void setSong(ISongPlay song) {
		if (this.song != null) {
			this.song.close();
		}
		this.song = song;
	}

	public ISongPlay getSong() {
		return song;
	}

	public static TYPE type;

	// void init() {
	// if (song == null) {
	// //song = new AudioTrackPlay();
	// //song = new MediaPlayerPlay2();
	// song = new _SoundTouchPlay();
	// }
	// Log.e(toString(), getMethodName());
	// }
	public void init(TYPE type) {
		SongPlayView.type = type;
		if (type == type.AUDIOTRACKPLAY) {
			song = new AudioTrackPlay2();
		} else if (type == type.MEDIAPLAYERPLAY) {
			song = new MediaPlayerPlay2();
		} else if (type == type.SOUNDTOUCHPLAY) {
			song = new _SoundTouchPlay(getContext());
		}
		Log.e(toString(), getMethodName() + type);
	}

	protected int index = -1;

	private String path;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	protected Handler handler;

	@Override
	public void setHandler(Handler handler) {

		this.handler = handler;

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setHandler(handler);
			}
		}
	}

	@Override
	public boolean post(Runnable action) {
		// Log.w(toString(), getMethodName() + action);


		return super.post(action);
	}

	@Override
	public boolean postDelayed(Runnable action, long delayMillis) {
		// Log.w(toString(), getMethodName() + action + delayMillis);


		return super.postDelayed(action, delayMillis);
	}

	Listener listener;

	@Override
	public void setOnListener(Listener listener) {

		this.listener = listener;
	}

	public int getID() {
		if (song != null) {
			if (song instanceof _SoundTouchPlay) {
				return ((_SoundTouchPlay) song).getID();
			}
			return -1;
		} else {
			return -1;
		}
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		release();
	}

	@Override
	public boolean load(String path) throws Exception {

		// Log.w(toString(), getMethodName() + handler + " - " + path);
		Log.w(toString(), getMethodName() + " - " + path);

		isPrepared = false;

		this.path = path;

		boolean ret = false;

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setHandler(this.handler);
				((ISongPlay) song).setOnListener(this);
			} else {
				song.setOnListener(this);
			}
			ret = song.load(path);
		}

		return ret;
	}

	@Override
	public boolean play() {
		if (song != null) {
			return song.play();
		} else {
			return false;
		}
	}

	@Override
	public void stop() {
		isPrepared = false;
		try {
			if (song != null) {
				if (song.isPlaying()) {
					song.stop();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void seek(int msec) {
		if (song != null) {
			song.seek(msec);
		}
	}

	@Override
	public boolean isPlaying() {
		if (song != null) {
			return song.isPlaying();
		}
		return false;
	}

	@Override
	public boolean isPause() {
		if (song != null) {
			return song.isPause();
		}
		return false;
	}

	@Override
	public boolean isPrepared() {
		if (song != null && song instanceof ISongPlay) {
			return ((ISongPlay) song).isPrepared();
		} else {
			return isPrepared;
		}
	}

	@Override
	public void pause() {
		if (song != null) {
			song.pause();
		}
	}

	/**
	 * <pre>
	 * 피치변경(반음)
	 * </pre>
	 * 
	 * @see _SoundTouchPlay#setPitch(int)
	 */
	@Override
	public void setPitch(int pitch) {

		if (song != null) {
			song.setPitch(pitch);
		}
	}

	/**
	 * <pre>
	 * 템포변경
	 * </pre>
	 * 
	 * @see _SoundTouchPlay#setTempo(float)
	 */
	@Override
	public void setTempo(float tempo) {

		if (song != null) {
			song.setTempo(tempo);
		}
	}

	@Override
	public float getTempo() {

		if (song != null) {
			song.getTempo();
		}
		return 0;
	}

	@Override
	public void setTempoPercent(int percent) {
		if (song != null) {
			song.setTempoPercent(percent);
		}
	}

	@Override
	public int getTempoPercent() {
		if (song != null) {
			song.getTempoPercent();
		}
		return 0;
	}

	@Override
	public int getTotalTime() {
		if (song != null && isPrepared()) {
			return song.getTotalTime();
		} else {
			return 0;
		}
	}

	@Override
	public int getCurrentTime() {
		if (song != null && isPrepared()) {
			return song.getCurrentTime();
		} else {
			return 0;
		}
	}

	@Override
	public void restart() {
		Log.w(toString(), getMethodName());

		try {
			if (song != null) {
				if (song instanceof ISongPlay) {
					((ISongPlay) song).setPath(path);
					((ISongPlay) song).restart();
				} else {
					stop();
					load(path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void repeat() {
		Log.w(toString(), getMethodName());

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setPath(path);
				((ISongPlay) song).repeat();
			} else {
				seek(0);
				postDelayed(new Runnable() {

					@Override
					public void run() {

						play();
					}
				}, TIME_RESTART);
			}
		}
	}

	@Override
	public void release() {
		Log.w(toString(), getMethodName());

		close();
		song = null;
	}

	@Override
	public void close() {
		Log.w(toString(), getMethodName());

		stop();

		if (song != null) {
			song.close();
		}
	}

	@Override
	public void onTime(int t) {

		if (listener != null) {
			listener.onTime(t);
		}
	}

	@Override
	public void onSeekComplete() {

		if (listener != null) {
			listener.onSeekComplete();
		}
	}

	@Override
	public void onRelease() {

		if (listener != null) {
			listener.onRelease();
		}
	}

	@Override
	public void onReady(int count) {

		if (listener != null) {
			listener.onReady(count);
		}
	}

	boolean isPrepared = false;

	@Override
	public void onPrepared() {
		Log.e(__CLASSNAME__, getMethodName());
		isPrepared = true;

		if (listener != null) {
			listener.onPrepared();
		}
	}

	@Override
	public void onError() {

		if (listener != null) {
			listener.onError();
		}
	}

	@Override
	public void onError(final ERROR t, final Exception e) {

		if (listener != null) {
			listener.onError(t, e);
		}
	}

	@Override
	public void onTimeout(final long timeout) {

		if (listener != null) {
			listener.onTimeout(timeout);
		}
	}

	@Override
	public void onRetry(final int count) {

		if (listener != null) {
			listener.onRetry(count);
		}
	}

	@Override
	public void onCompletion() {

		if (listener != null) {
			listener.onCompletion();
		}
	}

	@Override
	public void onBufferingUpdate(int percent) {

		if (listener != null) {
			listener.onBufferingUpdate(percent);
		}
	}

	@Override
	public float getMinVolume() {

		if (song != null) {
			if (song instanceof ISongPlay) {
				return ((ISongPlay) song).getMinVolume();
			}
		}
		return AudioTrack.getMinVolume();
	}

	@Override
	public float getMaxVolume() {

		if (song != null) {
			if (song instanceof ISongPlay) {
				return ((ISongPlay) song).getMaxVolume();
			}
		}
		return AudioTrack.getMaxVolume();
	}

	@Override
	public void setVolume(final float left, final float right) {

		try {
			if (song != null) {
				if (song instanceof ISongPlay) {
					((ISongPlay) song).setVolume(left, right);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setValance(final float LR) {

		float abs = (getMaxVolume() - getMinVolume()) / 2.0f;

		if (LR == 0) {
			abs = (getMaxVolume() - getMinVolume());
		}

		float left = abs - LR / 2.0f;
		float right = abs + LR / 2.0f;

		setVolume(left, right);
	}

	public void setChoir() {

	}

	@Override
	public int getPitch() {

		if (song != null) {
			song.getPitch();
		}
		return 0;
	}

}
