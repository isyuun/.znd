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
 * project	:	Karaoke.APP.TEST
 * filename	:	PlayFragment.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app.play
 *    |_ PlayFragment.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import java.lang.ref.WeakReference;

import kr.kymedia.karaoke.play._SoundTouchPlay;
import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play.impl.ISongService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

/**
 * 
 * <pre>
 * 재생관련내용만구현
 * 왜~~~딴거뭉개놓으면복잡하니까!!!
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 7. 11.
 * @version 1.0
 */
public class SongPlayFragment extends SongServiceFragment implements ISongPlay, ISongService, _SoundTouchPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	protected View findViewById(int id) {

		try {
			return getActivity().findViewById(id);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void start() {

	}

	@Override
	public void prepare() {


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

	private String path;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * UI처리용핸들러 Need handler for callbacks to the UI thread
	 */
	// protected Handler handler;
	protected HandlerUI handler;

	public static class HandlerUI extends Handler {
		WeakReference<SongPlayFragment> m_HandlerObj;

		HandlerUI(SongPlayFragment handlerobj) {
			m_HandlerObj = new WeakReference<SongPlayFragment>(handlerobj);
		}

		@Override
		public void handleMessage(Message msg) {
			SongPlayFragment handlerobj = m_HandlerObj.get();
			if (handlerobj == null) {
				return;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	public void setHandler(Handler handler) {

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setHandler(handler);
			}
		}
	}

	protected void post(Runnable r) {
		if (handler != null) {
			handler.post(r);
		}
	}

	protected void postDelayed(Runnable r, long delayMillis) {
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	Listener listener;

	@Override
	public void setOnListener(Listener listener) {

		this.listener = listener;
	}

	public SongPlayFragment() {
		super();
	}

	//public SongPlayFragment(boolean init) {
	//	this();
	//	// TODO Auto-generated constructor stub
	//	if (init) {
	//		init();
	//	}
	//}

	public void init() {
		// song = new AudioTrackPlay();
		// song = new MediaPlayerPlay2();
		song = new _SoundTouchPlay(getActivity().getApplicationContext());
		Log.e(toString(), getMethodName() + song);
		handler = new HandlerUI(this);
	}

	protected void onActivityCreated() {
		init();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void connectSongService() {

		super.connectSongService();

		Class<?> cls = _SoundTouchPlay.class;
		// Class<?> cls = MediaPlayerPlay.class;
		// start our service
		getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(), cls));
		// bind to our service by first creating a new connectionIntent
		Intent connectionIntent = new Intent(getActivity().getApplicationContext(), cls);
		getActivity().getApplicationContext().bindService(connectionIntent, SongServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void disconnectSongService() {

		super.disconnectSongService();

		getActivity().getApplicationContext().unbindService(this.SongServiceConnection);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		super.onServiceConnected(name, service);

		// if (((LocalBinder) service).getService() instanceof MediaPlayerPlay) {
		// song = (MediaPlayerPlay) ((LocalBinder) service).getService();
		// } else if (((LocalBinder) service).getService() instanceof MediaPlayerPlay2) {
		// song = (MediaPlayerPlay2) ((LocalBinder) service).getService();
		// } else if (((LocalBinder) service).getService() instanceof AudioTrackPlay) {
		// song = (AudioTrackPlay) ((LocalBinder) service).getService();
		// } else if (((LocalBinder) service).getService() instanceof _SoundTouchPlay) {
		// song = (_SoundTouchPlay) ((LocalBinder) service).getService();
		// }

		Log.e(toString(), getMethodName() + song);

		try {
			if (!isPlaying()) {
				open(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		super.onServiceDisconnected(name);

		Log.e(toString(), getMethodName() + song);
	}

	@Override
	public boolean open(String path) throws Exception {

		// Log.w(toString(), getMethodName() + handler + " - " + path);
		Log.w(toString(), getMethodName() + song + " - " + path);

		this.path = path;

		boolean ret = false;

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setHandler(this.handler);
				((ISongPlay) song).setOnListener(this);
			} else {
				song.setOnListener(this);
			}
			ret = song.open(path);
		}

		return ret;
	}

	@Override
	public boolean play() {
		if (song != null) {
			return song.play();
		}

		return false;
	}

	@Override
	public void stop() {
		try {
			if (song != null) {
				if (song.isPlaying()) {
					song.stop();
				}
			}
		} catch (Exception e) {

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
		} else {
			return false;
		}
	}

	@Override
	public boolean isPause() {
		if (song != null) {
			return song.isPause();
		} else {
			return false;
		}
	}

	@Override
	public boolean isPrepared() {

		if (this.song != null && song instanceof ISongPlay) {
			return ((ISongPlay) song).isPrepared();
		} else {
			return false;
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
	 * @see ISongPlay#setPitch(int)
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
			return song.getTempo();
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
			return song.getTempoPercent();
		}
		return 0;
	}

	@Override
	public int getTotalTime() {
		if (song != null) {
			return song.getTotalTime();
		} else {
			return 0;
		}
	}

	@Override
	public int getCurrentTime() {
		if (song != null) {
			return song.getCurrentTime();
		} else {
			return 0;
		}
	}

	@Override
	public void restart() {

		try {
			if (song != null) {
				if (song instanceof ISongPlay) {
					((ISongPlay) song).restart();
				} else {
					stop();
					open(path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void repeat() {

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).repeat();
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

	@Override
	public void onPrepared() {

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
	public void setVolume(float left, float right) {

		if (song != null) {
			if (song instanceof ISongPlay) {
				((ISongPlay) song).setVolume(left, right);
			}
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

	@Override
	public int getPitch() {

		if (song != null) {
			return song.getPitch();
		}
		return 0;
	}

}
