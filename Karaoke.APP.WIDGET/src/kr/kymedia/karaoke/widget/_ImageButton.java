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
 * filename	:	ImageButton.java
 * author	:	isyoon
 *
 * <pre>
 * is.yuun.com.example.android.displayingbitmaps.ui
 *    |_ ImageButton.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 *
 *
 * <pre>
 * 비트맵리사이클용이미지버튼
 * </pre>
 *
 * @author isyoon
 * @since 2014. 4. 25.
 * @version 1.0
 */
public class _ImageButton extends ImageButton {

	public _ImageButton(Context context) {
		super(context);
	}

	public _ImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public _ImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		try {
			super.onDraw(canvas);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
