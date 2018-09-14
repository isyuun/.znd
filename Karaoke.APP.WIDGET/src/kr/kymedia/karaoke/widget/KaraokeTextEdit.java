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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	KaraokeEditText.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ KaraokeEditText.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import kr.kymedia.karaoke.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 29.
 * @version 1.0
 */
public class KaraokeTextEdit extends KaraokeText implements OnClickListener, OnLongClickListener {
	// private static String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s()", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	public KaraokeTextEdit(Context context) {
		super(context);
	}

	public KaraokeTextEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KaraokeTextEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();

		setGravity(Gravity.CENTER_VERTICAL);
		setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
		setSingleLine(true);
		setTextSize(16);

		setOnClickListener(this);
		setOnLongClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.w(toString(), getMethodName() + v);

		setEditable(true);
	}

	@Override
	public boolean onLongClick(View v) {
		Log.w(toString(), getMethodName() + v);

		setEditable(true);
		return true;
	}

	@Override
	public void setEditable(boolean enabled) {

		super.setEditable(enabled);
	}

	@Override
	public void setTextIsSelectable(boolean selectable) {
		super.setTextIsSelectable(selectable);
	}

	@Override
	public final void setText(CharSequence text) {
		super.setText(text);
	}

}
