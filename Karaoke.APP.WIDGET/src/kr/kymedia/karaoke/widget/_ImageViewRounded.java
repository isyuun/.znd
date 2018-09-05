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
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p>
 * project	:	.prj
 * filename	:	ImageViewRounded3.java
 * author	:	isyoon
 * <p>
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ ImageViewRounded3.java
 * </pre>
 */
package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * <pre>
 *  RoundedImageView:원또는사각구석둥근처리는아직멀러...
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-10-17
 */
public class _ImageViewRounded extends RoundedImageView {
	public _ImageViewRounded(Context context) {
		super(context);
	}

	public _ImageViewRounded(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public _ImageViewRounded(Context context, AttributeSet attrs, int defStyle) {
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

				Paint paint = new Paint();
				paint.setColor(0x0000ddff);
				if (mAction == MotionEvent.ACTION_DOWN || mAction == MotionEvent.ACTION_MOVE) {
					paint.setColor(0xa000ddff);
				}
				if (this instanceof RoundedImageView) {
					Rect rect = new Rect();
					getDrawingRect(rect);
					RectF rectF = new RectF();
					rectF.set(rect);
					float radius = getCornerRadius();
					if (isOval()) {
						canvas.drawOval(rectF, paint);
					} else {
						if (getCornerRadius() > 0) {
							canvas.drawRoundRect(rectF, radius, radius, paint);
						} else {
							canvas.drawRect(rectF, paint);
						}
					}
				} else {
					canvas.drawCircle(w / 2, h / 2, h / 2, paint);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (_IKaraoke.DEBUG)Log.e("ImageViewRounded", "onTouch(...) " + event.toString());
		if (mAction != event.getAction()) {
			mAction = event.getAction();
			invalidate();
		}
		return super.onTouchEvent(event);
	}
}
