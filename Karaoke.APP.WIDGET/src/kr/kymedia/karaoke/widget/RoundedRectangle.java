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
 * filename	:	RoundedRectangle.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ RoundedRectangle.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

/**
 *
 * TODO
 * NOTE:
 *
 * @author	isyoon
 * @since	2012. 8. 8.
 * @version 1.0
 * @see
 */

/**
 *  A LinearLayout that draws a rounded rectangle around the child View that was added to it.
 */
/**
 * A LinearLayout that has rounded corners instead of square corners.
 * 
 * @author Danny Remington
 * 
 * @see LinearLayout
 * 
 */
@Deprecated
public class RoundedRectangle extends LinearLayout {
	private int mInteriorColor = android.R.color.holo_blue_bright;

	public RoundedRectangle(Context p_context) {
		super(p_context);
	}

	public RoundedRectangle(Context p_context, AttributeSet attributeSet) {
		super(p_context, attributeSet);
	}

	// Listener for the onDraw event that occurs when the Layout is drawn.
	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect(0, 0, getWidth(), getHeight());
		RectF rectF = new RectF(rect);
		DisplayMetrics metrics = new DisplayMetrics();
		Activity activity = (Activity) getContext();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float density = metrics.density;
		int arcSize = Math.round(density * 10);

		Paint paint = new Paint();
		paint.setColor(mInteriorColor);

		canvas.drawRoundRect(rectF, arcSize, arcSize, paint);
	}

	/**
	 * Set the background color to use inside the RoundedRectangle.
	 * 
	 * @param Primitive
	 *          int - The color inside the rounded rectangle.
	 */
	public void setInteriorColor(int interiorColor) {
		mInteriorColor = interiorColor;
	}

	/**
	 * Get the background color used inside the RoundedRectangle.
	 * 
	 * @return Primitive int - The color inside the rounded rectangle.
	 */
	public int getInteriorColor() {
		return mInteriorColor;
	}
}
