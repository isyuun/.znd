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
 * filename	:	PlayView.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ PlayView.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

import kr.keumyoung.karaoke.api._Const;
import kr.keumyoung.karaoke.data._SongData;

/**
 *
 * @author isyoon
 * @since 2015. 2. 3.
 * @version 1.0
 */
class LyricsPlay2 extends LyricsPlay1 implements _Const , SurfaceHolder.Callback  {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public _SongData m_data = null;

	public void setSongData(_SongData data) {
		this.m_data = data;
	}

	private MediaPlayer m_mp = null;

	public void setMediaPlayer(MediaPlayer mp) {
		this.m_mp = mp;
	}

	public boolean isPlaying() {
		//if (BuildConfig.DEBUG) _Log.i(__CLASSNAME__ + "MediaPlayer", getMethodName() + m_mp);
		if (m_mp != null && m_mp.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getCurrentPosition() {

		// return super.getCurrentPosition();
		if (isPlaying()) {
			return m_mp.getCurrentPosition();
		} else {
			return 0;
		}
	}

	@Override
	public int getDuration() {

		if (m_mp != null) {
			return m_mp.getDuration();
		} else {
			return 0;
		}
	}

	public boolean m_redraw = false;

	public void setRedraw(boolean redraw) {
		this.m_redraw = redraw;
	}

	public boolean isRedraw() {
		return m_redraw;
	}

	public int m_iSongInfoPosition = 0;

	public void setSongInfoPosition(int iSongInfoPosition) {
		this.m_iSongInfoPosition = iSongInfoPosition;
	}

	public int m_iTitleFontSize = 0;

	public void setTitleFontSize(int iTitleFontSize) {
		this.m_iTitleFontSize = iTitleFontSize;
	}

	public int m_iLyricsFontSize = 0;

	public void setLyricsFontSize(int iLyricsFontSize) {
		this.m_iLyricsFontSize = iLyricsFontSize;
	}

	public int m_iSingerFontSize = 0;

	public void setSingerFontSize(int iSingerFontSize) {
		this.m_iSingerFontSize = iSingerFontSize;
	}

	public int m_iReadyFontSize = 0;

	public void setReadyFontSize(int iReadyFontSize) {
		this.m_iReadyFontSize = iReadyFontSize;
	}

	public int m_iStrokeSize = 0;

	public void setStrokeSize(int iStrokeSize) {
		this.m_iStrokeSize = iStrokeSize;
	}

	// 아직 시작 안된 가사가 완료된 것으로 표시되는 증상 수정
	public int m_iBeforeEnd = 0;
	public String m_strContinueLyrics = "";
	public boolean m_bContinue = false;
	public boolean m_bSkipChangeColor = false;

	public Typeface mTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);

	public void setTypeface(Typeface typeface) {
		this.mTypeface = typeface;
	}

	public LyricsPlay2(Context context) {
		super(context);
	}

	private _KPLyrics mKPLyrics;

	public _KPLyrics getKPLyrics() {
		return mKPLyrics;
	}

	///**
	// * 가사처리슬립추가
	// */
	//private long mThreadSleepTime = 0;
    //
	///**
	// * 가사처리슬립추가
	// */
	//public long getSleepTime() {
	//	return mThreadSleepTime;
	//}
    //
	///**
	// * 가사처리슬립추가
	// */
	//public void setSleepTime(long time) {
    //
	//	_Log.wtf(__CLASSNAME__ + _Const.TAG_LYRIC, getMethodName() + time);
	//	this.mThreadSleepTime = time;
	//	if (mKPLyrics != null) {
	//		mKPLyrics.setSleepTime(time);
	//	}
	//	((TextView) findViewById(R.id.txt_lyric_sleep_info)).setText("" + time);
	//}

	private boolean hasSurface;

	SurfaceHolder holder;
	protected void init() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
		 this.holder = getHolder();
		 //bgkim 배경을 투명하게
		 setZOrderOnTop(true);    // necessary
		 getHolder().setFormat(PixelFormat.TRANSLUCENT);
		 getHolder().addCallback(this);
		 hasSurface = false;
		 //setFocusable(true);
		 //setFocusableInTouchMode(true);
	}

	private void start() {
		Log.w(__CLASSNAME__, getMethodName() + "[ST]" + mKPLyrics + ":" + getHolder());
		try {
			if (mKPLyrics != null) {
				mKPLyrics.interrupt();
				mKPLyrics = null;
			}

			// if (mKPLyrics == null)
			{
				mKPLyrics = new _KPLyrics((_LyricsPlay) this);
			}

			mKPLyrics.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void play() throws Exception {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + "[ST]"/* + ":" + mThreadSleepTime*/ + ":" + mKPLyrics + ":" + getHolder());

		try {
			setVisibility(View.VISIBLE);

			setRedraw(false);

			start();

			// 재생전설정값박기
			Log.e(__CLASSNAME__, getMethodName()/* + ":" + mThreadSleepTime*/ + ":" + mKPLyrics + ":" + getHolder());
			//setSleepTime(mThreadSleepTime);
			if (mKPLyrics != null) {
				mKPLyrics.setSleepTime(0);
				((TextView) findViewById(R.id.txt_lyric_sleep_info)).setText("" + mKPLyrics.getSleepTime());
			}

		} catch (Exception e) {

			Log.wtf(__CLASSNAME__, getMethodName() + "[NG]"/* + ":" + mThreadSleepTime*/ + ":" + mKPLyrics + ":" + getHolder() + "\n" + Log.getStackTraceString(e));
			//e.printStackTrace();
			throw (e);
		}

		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + "[ED]"/* + ":" + mThreadSleepTime*/ + ":" + mKPLyrics + ":" + getHolder());
	}

	public void stop() throws Exception {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + mKPLyrics + ":" + getHolder());

		try {
			setVisibility(View.INVISIBLE);

			setRedraw(true);

			if (mKPLyrics != null) {
				mKPLyrics.requestExitAndWait();
				mKPLyrics = null;
			}
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__ + TAG_ERR, "[NG]" + getMethodName() + mKPLyrics + ":" + getHolder());
			e.printStackTrace();
			throw (e);
		}

	}

	public void pause() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
		setVisibility(View.VISIBLE);
		setRedraw(false);
	}

	public void resume() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
		setVisibility(View.VISIBLE);
		setRedraw(false);
	}

	 @Override
	 public void surfaceCreated(SurfaceHolder holder) {
		 if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + holder);
		 this.holder = holder;
		 hasSurface = true;
		 //if (mLyricsViewThread == null) {
		 // mLyricsViewThread = new KPLyrics(this);
		 //}
	 }

	 @Override
	 public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + holder + ":" + format + ", " + w + ", " + h);
		this.holder = holder;
		hasSurface = true;
		//if (mKPLyrics != null) {
		//	mKPLyrics.onWindowResize(w, h);
		//}
	 }

	 @Override
	 public void surfaceDestroyed(SurfaceHolder holder) {
		 if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + holder);
		 //this.holder = holder;
		 hasSurface = false;
	 }
}
