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
 * project	:	Karaoke.APP
 * filename	:	ImageViewFocus.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ ImageViewFocus.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 4. 30.
 * @version 1.0
 */
@Deprecated
public class _ImageViewFocused extends _ImageView {

	public _ImageViewFocused(Context context) {
		super(context);
	}

	public _ImageViewFocused(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public _ImageViewFocused(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onDrawFocus(canvas);
	}

	int mAction = MotionEvent.ACTION_UP;

	/**
	 * 클릭시포커스처리
	 */
	void onDrawFocus(Canvas canvas) {
		try {
			if (isClickable()) {
				int w = getMeasuredWidth();
				int h = getMeasuredHeight();
				Rect r = new Rect(0, 0, w, h);

				Paint paint = new Paint();
				paint.setColor(0x0000ddff);
				if (mAction == MotionEvent.ACTION_DOWN || mAction == MotionEvent.ACTION_MOVE) {
					paint.setColor(0xa000ddff);
				}
				canvas.drawRect(r, paint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isClickable()) {
			// if (_IKaraoke.DEBUG)_Log.e("ImageViewRounded", "onTouch(...) " + event.toString());
			if (mAction != event.getAction()) {
				mAction = event.getAction();
				invalidate();
			}
		}
		return super.onTouchEvent(event);
	}

}
