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
 * filename	:	Listen3.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv
 *    |_ Listen3.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import java.io.IOException;

/**
 * <pre>
 * 반주곡/녹음곡 스트리밍처리
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 4. 3.
 */
class Listen3 extends Listen2 {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public Listen3(Context context) {
		super(context);
	}

	private PLAY_ENGAGE m_state = PLAY_ENGAGE.PLAY_STOP;

	public void setPlayState(PLAY_ENGAGE state) {
		if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + state);
		this.m_state = state;
	}

	public PLAY_ENGAGE getPlayState() {
		if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + m_state);
		return m_state;
	}

	public void open() throws Exception {

		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]");
		load(this.url);
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]");
	}

	String path;

	@Override
	protected void setFile(String path) {

		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]");
		// super.setFile(load);
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]");
	}

	@Override
	protected boolean load(String path) throws Exception {

		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + path);
		this.path = path;

		try {
			reset();

			if (m_mp == null) {
				m_mp = new MediaPlayer();
			}
			// FileInputStream fs = new FileInputStream(sourceFile);
			// FileDescriptor fd = fs.getFD();
			// m_mp.setDataSource(fd);
			// fs.close();
			Log.w(__CLASSNAME__, "load()" + "[setAudioStreamType]");
			m_mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			Log.w(__CLASSNAME__, "load()" + "[setDataSource]");
			m_mp.setDataSource(path);
			Log.w(__CLASSNAME__, "load()" + "[prepareAsync]");
			m_mp.prepareAsync();

			m_mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + mp);
					if (mOnPreparedListener != null) {
						mOnPreparedListener.onPrepared(mp);
					}
				}
			});

			// m_mp.setOnCompletionListener(onListenComplete);
			m_mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {

					if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + mp);
					if (mOnCompletionListener != null) {
						mOnCompletionListener.onCompletion(mp);
					}
				}
			});

			m_mp.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {

					if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + mp + "(" + what + ", " + extra + ")");
					if (mOnErrorListener != null) {
						mOnErrorListener.onError(mp, what, extra);
					}
					return false;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean play() throws Exception {

		try {
			if (m_mp != null) {
				m_mp.start();
			}
		} catch (Exception e) {

			e.printStackTrace();
			throw (e);
		}
		return true;
	}

	@Override
	public void stop() {

		try {
			super.stop();
			reset();
			setPlayState(PLAY_ENGAGE.PLAY_STOP);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	protected void pause() {
		try {
			if (m_state == PLAY_ENGAGE.PLAY_PLAY) {
				m_mp.pause();
				m_state = PLAY_ENGAGE.PLAY_PAUSE;
			}
		} catch (Exception e) {
		}
	}

	protected void resume() {
		try {
			if (m_state == PLAY_ENGAGE.PLAY_PAUSE) {
				m_mp.start();
				m_state = PLAY_ENGAGE.PLAY_PLAY;
			}
		} catch (Exception e) {
		}
	}

}
