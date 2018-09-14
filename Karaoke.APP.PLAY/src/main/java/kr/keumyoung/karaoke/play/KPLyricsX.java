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
 * filename	:	KPLyricsX.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ KPLyricsX.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import kr.keumyoung.karaoke.api._Const;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * <pre>
 * AOSP(BHX-S300)
 * 	<a href="http://pms.skdevice.net/redmine/issues/3477">3477 노래 시작시 노래/작곡/작사 텍스트가 텍스트박스에서 약간 벗어나 출력 됨</a>
 * 	<a href="http://pms.skdevice.net/redmine/issues/3482">3482 일부노래 자막 하단이 잘려서 출력되는 현상</a>
 * 	48859 - '씨스타 - Shake It' 노래 부르기 진행 중 일부 가사 하단부분이 잘려서 출력됩니다.
 * 가사처리슬립추가
 * </pre>
 *
 * @author isyoon
 * @since 2015. 8. 25.
 * @version 1.0
 */
class KPLyricsX extends KPLyrics {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public DisplayMetrics getDisplayMetrics() {
		try {
			return mLyricsPlay.getResources().getDisplayMetrics();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public String getString(int id) {
		try {
			return mLyricsPlay.getResources().getString(id);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public WindowManager getWindowManager() {
		try {
			return mLyricsPlay.getWindowManager();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public SurfaceHolder getHolder() {
		return this.mLyricsPlay.getHolder();
	}

	/**
	 * <pre>
	 * <a href="http://stackoverflow.com/questions/24332205/android-graphics-picture-not-being-drawn-in-api-14">android.graphics.Picture not being drawn in API 14+</a>
	 * <a href="http://stackoverflow.com/questions/10384613/android-canvas-drawpicture-not-working-in-devices-with-ice-cream-sandwich/14054331#14054331">Android Canvas.drawPicture not working in devices with ice cream sandwich</a>
	 * view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	 * </pre>
	 */
	KPLyricsX(_LyricsPlay lyricsPlay) {
		super(lyricsPlay);

		DisplayMetrics metrics = getDisplayMetrics();

		String text = "" + metrics;
		text += "\n[RECT]" + "metrics.densityDpi:" + metrics.densityDpi;
		text += "\n[RECT]" + "metrics.density:" + metrics.density;
		text += "\n[RECT]" + "metrics.scaledDensity:" + metrics.scaledDensity;

		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + text);
	}

	@Override
	protected void doDraw(Canvas canvas) {

		super.doDraw(canvas);
	}

	@Override
	protected void drawLyrics(Canvas canvas, int t) {
		// 크기확인
		Rect rect = getHolder().getSurfaceFrame();
		m_width = rect.width();
		m_height = rect.height();

		// isyoon_20150427
		// canvas.setDensity(metrics.densityDpi);
		super.drawLyrics(canvas, t);
	}

	@Override
	void redrawLyrics(Canvas canvas, Paint paint, String str, int end, int type) {

		super.redrawLyrics(canvas, paint, str, end, type);
	}

	/**
	 * 아주잘~~~헌다이븅신!!!이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
	 *
	 * @see KPLyrics#outRect(Canvas, Paint, int, int, int, int, int, int)
	 */
	private void outRect(Canvas canvas, Paint paint, int m, int w, int y, int colorTitle, int colorName) {

		// 아주잘~~~헌다이븅신!!!이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
		// int iCenter = 0;
		//
		// if (mLyricsPlay.m_iSongInfoPosition > 950) {
		// // 1920 x 1080
		// iCenter = 220;
		// } else if (mLyricsPlay.m_iSongInfoPosition > 630) {
		// // 1280 x 720
		// iCenter = 170;
		// } else {
		// // 960 x 540
		// iCenter = 100;
		// }

		int l = m + mLyricsPlay.m_iSongInfoPosition;
		int c = w + mLyricsPlay.m_iSongInfoPosition;

		//int r = mLyricsPlay.m_iSongInfoPosition * 2;
		int r = m_width;

		Rect r1 = new Rect(l, y - mLyricsPlay.m_iSingerFontSize, c, y + (mLyricsPlay.m_iSingerFontSize / 3));
		Rect r2 = new Rect(c, y - mLyricsPlay.m_iSingerFontSize, r, y + (mLyricsPlay.m_iSingerFontSize / 3));

		// if (BuildConfig.DEBUG) _LOG.i(_toString(), getMethodName() + mLyricsPlay.m_iSongInfoPosition + ":" + r1 + ":" + r2);

		paint.setColor(colorTitle);
		paint.setAlpha(200);
		canvas.drawRect(r1, paint);

		paint.setColor(colorName);
		paint.setAlpha(200);
		canvas.drawRect(r2, paint);
	}

	public Resources getResources() { return mLyricsPlay.getResources(); }

	/**
	 * 아주잘~~~헌다이븅신!!!이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
	 *
	 * @see KPLyrics#setSinger(Canvas, Paint, String, String, String)
	 */
	@Override
	void setSinger(Canvas canvas, Paint paint, String str1, String str2, String str3) {
		int m = 40;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			m /= 2;
		}

		// 노래
		String strSinger = getString(R.string.hint_song_song) + "    " + str1;
		if (TextUtil.isEmpty(str1)) {
			strSinger = getString(R.string.hint_song_song) + "    " + getString(R.string.hint_song_unknown);
		}

		// 작곡
		String strComposer = getString(R.string.hint_song_composer) + "    " + str2;
		if (TextUtil.isEmpty(str2)) {
			strComposer = getString(R.string.hint_song_composer) + "    " + getString(R.string.hint_song_unknown);
		}
		// 작사
		String strLyricist = getString(R.string.hint_song_lyricist) + "    " + str3;
		if (TextUtil.isEmpty(str3)) {
			strLyricist = getString(R.string.hint_song_lyricist) + "    " + getString(R.string.hint_song_unknown);
		}

		Rect r1 = outSize(paint, getString(R.string.hint_song_song));
		Rect r2 = outSize(paint, getString(R.string.hint_song_composer));
		Rect r3 = outSize(paint, getString(R.string.hint_song_lyricist));

		//if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + strSinger + ":" + r1);
		//if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + strComposer + ":" + r2);
		//if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + strLyricist + ":" + r3);

		int w = r1.width();
		Rect rect = r1;

		// 아주잘~~~헌다이븅신!!!이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
		// if (r1.width() > r2.width() && r1.width() > r3.width()) {
		// w = r1.width();
		// rect = r1;
		// if (BuildConfig.DEBUG) _LOG.e(_toString(), getMethodName() + ":" + metrics.density + ":" + metrics.densityDpi + ":" + w + ":" + rect);
		// } else if (r2.width() > r1.width() && r2.width() > r3.width()) {
		// w = r2.width();
		// rect = r2;
		// if (BuildConfig.DEBUG) _LOG.e(_toString(), getMethodName() + ":" + metrics.density + ":" + metrics.densityDpi + ":" + w + ":" + rect);
		// } else if (r3.width() > r1.width() && r3.width() > r2.width()) {
		// w = r3.width();
		// rect = r3;
		// if (BuildConfig.DEBUG) _LOG.e(_toString(), getMethodName() + ":" + metrics.density + ":" + metrics.densityDpi + ":" + w + ":" + rect);
		// }

		if (r2 != null && r2.width() > w) {
			w = r2.width();
			rect = r2;
		}

		if (r3 != null && r3.width() > w) {
			w = r3.width();
			rect = r3;
		}

		w += m + m / 2;

		// if (BuildConfig.DEBUG) _LOG.e(_toString(), getMethodName() + w + ":" + rect + "\n" + strSinger + "\n" + strComposer + "\n" + strLyricist);

		int iSingerY = m_height - (r1.height() * 3 + 150);
		int iAuthorY = m_height - (r1.height() * 2 + 110);
		int iLyricsAuthorY = m_height - (r1.height() + 70);

		outRect(canvas, paint, 0, w, iSingerY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));
		outRect(canvas, paint, 0, w, iAuthorY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));
		outRect(canvas, paint, 0, w, iLyricsAuthorY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));

		outTextWithOutStroke(canvas, paint, strSinger, m + mLyricsPlay.m_iSongInfoPosition, iSingerY, Color.WHITE);
		outTextWithOutStroke(canvas, paint, strComposer, m + mLyricsPlay.m_iSongInfoPosition, iAuthorY, Color.WHITE);
		outTextWithOutStroke(canvas, paint, strLyricist, m + mLyricsPlay.m_iSongInfoPosition, iLyricsAuthorY, Color.WHITE);
	}

	/**
	 * <pre>
	 * AOSP(BHX-S300)
	 * <a href="http://pms.skdevice.net/redmine/issues/3482">3482 일부노래 자막 하단이 잘려서 출력되는 현상</a>
	 * 	48859 - '씨스타 - Shake It' 노래 부르기 진행 중 일부 가사 하단부분이 잘려서 출력됩니다.
	 * </pre>
	 *
	 * 아주잘~~~헌다이븅신!!!이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
	 *
	 * @see KPLyrics#setLyrics(Canvas, Paint, String, int, int, int)
	 */
	@Override
	void setLyrics(Canvas canvas, Paint paint, String str, int type, int inColor, int outColor) {

		// super.setLyrics(canvas, paint, str, type, inColor, outColor);
		Rect rect = outSize(paint, str);

		m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
		m_strLyrics[type] = str;

		/**
		 * 가사단간격
		 */
		int g = 20;
		/**
		 * 텍스트사이즈(높이)
		 */
		int h = (int) paint.getTextSize();
		/**
		 * 가사높이
		 */
		int l = rect.height();

		// if (BuildConfig.DEBUG) _LOG.w(_toString(), getMethodName() + "아주잘~~~헌다이븅신!!!" + "(" + "h:" + h + ", l:" + l + ")" + "-" + str);

		if (type == 0) {
			// isyoon지랄:1줄
			m_ptDrawPos[type].y = m_height - h * 2 - g;
		} else {
			// isyoon지랄:2줄
			m_ptDrawPos[type].y = m_height - h;
		}

		// 자막하단여백(메뉴단간격)
		m_ptDrawPos[type].y -= (mLyricsPlay.getLyricsMarginBottom());
		// 머다냐이건
		m_ptLyrics = m_ptDrawPos[0];

		outText(canvas, paint, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, inColor, outColor);
	}

	/**
	 * 가사처리슬립추가
	 */
	private long mSleepTime = 0;

	/**
	 * 가사처리슬립추가
	 */
	public void setSleepTime(long time) {
		this.mSleepTime = time;
	}

	public long getSleepTime() {
		return this.mSleepTime;
	}

	@Override
	public void requestExitAndWait() {

		interrupt();

		super.requestExitAndWait();
	}

	@Override
	public void interrupt() {

		this.done = true;

		try {
			super.interrupt();
		} catch (Exception e) {

			Log.wtf(_toString() + _Const.TAG_LYRIC, "interrupt()" + Log.getStackTraceString(e));
			e.printStackTrace();
		}
	}

	/**
	 * 가사처리슬립추가
	 *
	 * @see Thread#sleep(long)
	 */
	@Override
	protected void runDraw() {
		while (!done) {
			try {
				if (!isAlive() || isInterrupted()) {
					done = true;
					break;
				}
				draw();
			} catch (Exception e) {
				Log.wtf(toString(), "runDraw() " + "draw()" + "[NG]" + "\n" + Log.getStackTraceString(e));
				e.printStackTrace();
			}
			try {
				if (mSleepTime > 100 && !isInterrupted()) {
					sleep(mSleepTime);
				}
			} catch (Exception e) {
				//Log.wtf(toString(), "runDraw() " + "sleep()" + "[NG]" + "\n" + Log.getStackTraceString(e));
				//e.printStackTrace();
			}
		}
	}

	@Override
	public void draw() {
		Canvas canvas = null;

		try {
			if (!mLyricsPlay.m_redraw) {
				// if (BuildConfig.DEBUG) _LOG.i(_toString(), "run()" + surfaceHolder);
				canvas = mSurfaceHolder.lockCanvas();
				if (canvas != null) {
					// bgkim 투명 배경이라서 뭘 그리기 전에 한번 지워줌
					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
				}
				doDraw(canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
