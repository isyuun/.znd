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
 * filename	:	ImageViewRounded.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ ImageViewRounded.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import kr.kymedia.karaoke.widget.util.BitmapUtils2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

/**
 * 
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 8. 8.
 * @version 1.0
 */

class ImageViewCircled extends _ImageView {

	public ImageViewCircled(Context context) {
		super(context);
	}

	public ImageViewCircled(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewCircled(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		onDrawRound(canvas);
	}

	/**
	 * 이미지라운드처리
	 */
	void onDrawRound(Canvas canvas) {
		try {
			BitmapDrawable drawable = (BitmapDrawable) getDrawable();

			if (drawable == null) {
				return;
			}

			if (getWidth() == 0 || getHeight() == 0) {
				return;
			}

			Bitmap fullSizeBitmap = drawable.getBitmap();

			int scaledWidth = getMeasuredWidth();
			int scaledHeight = getMeasuredHeight();

			Bitmap mScaledBitmap;
			if (scaledWidth == fullSizeBitmap.getWidth() && scaledHeight == fullSizeBitmap.getHeight()) {
				mScaledBitmap = fullSizeBitmap;
			} else {
				mScaledBitmap = Bitmap.createScaledBitmap(fullSizeBitmap, scaledWidth, scaledHeight, true /* filter */);
			}

			Bitmap roundBitmap = BitmapUtils2.getRoundedCornerBitmap(getContext(), mScaledBitmap, 40, scaledWidth, scaledHeight, false, false, false, false, 4);
			canvas.drawBitmap(roundBitmap, 0, 0, null);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
