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
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	ClickableSeekBar.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.widget
 *    |_ ClickableSeekBar.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * TODO NOTE:<br>
 * 클릭가능썸버튼시크바~~~<br>
 * 드뎌만들었다시팔!!!!!!<br>
 * 벅스이개시키시팔!!!!!!<br>
 * 
 * @author isyoon
 * @since 2012. 5. 18.
 * @version 1.0
 */

public class SeekBarClickThumb extends SeekBar implements OnSeekBarChangeListener, OnTouchListener {
	protected String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s()", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	public interface OnClickSeekBarChangeListener extends OnSeekBarChangeListener {
		public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

		public abstract void onStartTrackingTouch(SeekBar seekbar);

		public abstract void onStopTrackingTouch(SeekBar seekbar);

		public abstract void onClickSeekBarTumb();
	}

	public SeekBarClickThumb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// Log.e(__CLASSNAME__, getMethodName());
		setOnTouchListener(this);
		setOnSeekBarChangeListener(this);
	}

	public SeekBarClickThumb(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Log.e(__CLASSNAME__, getMethodName());
		setOnTouchListener(this);
		setOnSeekBarChangeListener(this);
	}

	public SeekBarClickThumb(Context context) {
		super(context);
		// Log.e(__CLASSNAME__, getMethodName());
		setOnTouchListener(this);
		setOnSeekBarChangeListener(this);
	}

	/**
	 * 마지막터치지점확인
	 */
	float x = 0.0f;
	float y = 0.0f;
	/**
	 * 터치다운/업시간확인
	 */
	private static long MAX_CLICK_TIME = 1000;
	/**
	 * 터치와섬버튼간거리확인
	 */
	private static float MAX_CLICK_SIZE = 50.0f;
	/**
	 * 클릭발생여부확인
	 */
	private static boolean MAX_CLICK_FIRE = false;

	// private boolean isEnabled = true;

	@Override
	public boolean isEnabled() {

		return super.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
	}

	/**
	 * 
	 * <pre>
	 * 젖대기싫음안쓰는게좋아~~~
	 * </pre>
	 * 
	 * @see #setOnClickSeekBarChangeListener(OnClickSeekBarChangeListener, OnTouchListener)
	 */
	@Override
	public void setOnTouchListener(OnTouchListener l) {

		super.setOnTouchListener(this);
		this.tlistener = l;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// String vn = getResources().getResourceEntryName(v.getId());
		// String cn = v.getClass().getSimpleName();
		// Log.e(__CLASSNAME__, getMethodName() + vn + ", " + cn + ", " + event);


		if (!isEnabled()) {
			if (tlistener != null) {
				tlistener.onTouch(v, event);
			}
			return true;
		}

		boolean ret = false;

		long dtime = event.getDownTime();
		long etime = event.getEventTime();

		// Log.e(__CLASSNAME__, getMethodName() + Math.abs(x - event.getX()) + ":" + MAX_CLICK_SIZE);

		// MotionEvent.ACTION_MOVE를 제외하고 일단처먹고들어간다.
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 클릭여부확인
			if (Math.abs(x - event.getX()) < MAX_CLICK_SIZE) {
				ret = true;
				MAX_CLICK_FIRE = true;
			} else {
				MAX_CLICK_FIRE = false;
			}
			x = event.getX();
			y = event.getY();
			this.setPressed(true);
			break;

		case MotionEvent.ACTION_MOVE:
			if (MAX_CLICK_FIRE && Math.abs(x - event.getX()) < MAX_CLICK_SIZE) {
				// 클릭상태이고이동거리가작으면차단
				ret = true;
				MAX_CLICK_FIRE = true;
			} else {
				// 클릭상태이고이동거리가커지면해제
				MAX_CLICK_FIRE = false;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (MAX_CLICK_FIRE && Math.abs(etime - dtime) < MAX_CLICK_TIME) {
				ret = true;
				onClickSeekBarTumb();
			}
			x = event.getX();
			y = event.getY();
			this.setPressed(false);
			break;

		default:
			x = event.getX();
			y = event.getY();
			this.setPressed(false);
			break;
		}

		// Log.e(__CLASSNAME__, getMethodName() + event.getAction() + "-" + ret + "," + MAX_CLICK_FIRE + " - " + Math.abs(etime - dtime) + "," + Math.abs(x - event.getX()));
		if (!ret && tlistener != null) {
			tlistener.onTouch(v, event);
		}

		return ret;
	}

	private OnTouchListener tlistener = null;
	private OnClickSeekBarChangeListener clistener = null;

	public void setOnClickSeekBarChangeListener(OnClickSeekBarChangeListener clistener, OnTouchListener tlistener) {

		this.clistener = clistener;
		this.tlistener = tlistener;
	}

	public void onClickSeekBarTumb() {

		// Log.e(__CLASSNAME__, getMethodName());
		playSoundEffect(SoundEffectConstants.CLICK);
		if (clistener != null) {
			clistener.onClickSeekBarTumb();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		// Log.e(__CLASSNAME__, getMethodName() + progress + "," + fromUser);
		if (clistener != null) {
			clistener.onProgressChanged(seekBar, progress, fromUser);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		// Log.e(__CLASSNAME__, getMethodName());
		if (clistener != null) {
			clistener.onStartTrackingTouch(seekBar);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		// Log.e(__CLASSNAME__, getMethodName());
		if (clistener != null) {
			clistener.onStopTrackingTouch(seekBar);
		}
	}

	Drawable thumb = null;

	/**
	 * @see android.widget.AbsSeekBar#getThumb()
	 */
	public Drawable getThumb() {

		// return super.getThumb();
		return this.thumb;
	}

	public Rect getThumbRect() {
		Rect ret = new Rect();
		Drawable thumb = getThumb();
		if (thumb != null) {
			ret = thumb.getBounds();
		}
		return ret;
	}

	/**
	 * @see android.widget.AbsSeekBar#setThumb(android.graphics.drawable.Drawable)
	 */
	@Override
	public void setThumb(Drawable thumb) {

		Rect rect = getThumbRect();
		int l = rect.left;
		int t = rect.top;
		int r = rect.left + thumb.getIntrinsicWidth();
		int b = rect.top + thumb.getIntrinsicHeight();
		thumb.setBounds(new Rect(l, t, r, b));
		super.setThumb(thumb);
		this.thumb = thumb;

		if (thumb.getIntrinsicWidth() > 0) {
			MAX_CLICK_SIZE = thumb.getIntrinsicWidth() * 1.0f;
		} else {
			MAX_CLICK_SIZE = this.getWidth() / 15.0f;
		}

	}

	public void setThumb(int id) {

		Drawable thumb = getResources().getDrawable(id);
		setThumb(thumb);
	}

}
