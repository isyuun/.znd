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
 * project	:	Karaoke.APP.LIB
 * filename	:	VerticalSeekBar.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.widget
 *    |_ VerticalSeekBar.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 *
 *
 * <pre>
 * 	자막위치
 * 자막싱크
 * </pre>
 *
 * @author isyoon
 * @since 2013. 11. 6.
 * @version 1.0
 * @see
 */
public class SeekBarVertical extends SeekBar {

	public SeekBarVertical(Context context) {
		super(context);
	}

	public SeekBarVertical(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SeekBarVertical(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}

	OnSeekBarChangeListener listener;

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {

		super.setOnSeekBarChangeListener(l);
		this.listener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// case MotionEvent.ACTION_MOVE:
		// case MotionEvent.ACTION_UP:
		// int i=0;
		// i=getMax() - (int) (getMax() * event.getY() / getHeight());
		// setProgress(i);
		// //_Log.i("Progress",getProgress()+"");
		// onSizeChanged(getWidth(), getHeight(), 0, 0);
		// break;
		//
		// case MotionEvent.ACTION_CANCEL:
		// break;
		// }
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (listener != null) {
				listener.onStartTrackingTouch(this);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
			setProgress(progress);
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			if (listener != null) {
				if (progress > getMax() || progress < 0) {
					break;
				}
				listener.onProgressChanged(this, progress, true);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				listener.onStopTrackingTouch(this);
			}
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return true;
	}

}
