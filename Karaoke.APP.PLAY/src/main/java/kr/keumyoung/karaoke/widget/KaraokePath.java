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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	KaraokeTextView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ KaraokeTextView.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import kr.kymedia.karaoke.util.TextUtil;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 22.
 * @version 1.0
 */
public class KaraokePath extends android.support.v7.widget.AppCompatEditText {
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

	public KaraokePath(Context context) {
		super(context);
	}

	public KaraokePath(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KaraokePath(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	boolean isAttached = false;

	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();

		isAttached = true;

	}

	private boolean isKaraoke() {
		if (isInEditMode()) {
			return false;
		}

		if (!isAttached) {
			return false;
		}

		if (hosts == null) {
			return false;
		}

		return true;
	}

	// 211.236.190.103
	private final static String hosts[] = { ("211.236.190.103"), ("resource.kymedia.kr"), ("cyms.chorus.co.kr"), };

	public static String[] getHosts() {
		return hosts;
	}

	// private static String hosts[] = null;

	public boolean isKaraokeUri(Uri uri) {
		// Log.e(toString(), getMethodName() + uri);

		if (!isKaraoke()) {
			return false;
		}

		boolean ret = TextUtil.isKaraokeUri(hosts, uri);

		// Log.e(toString(), getMethodName() + ret + uri);

		return ret;
	}

	public void checkKaraoke(CharSequence text) {
		// Log.e(toString(), getMethodName() + text);

		if (!isKaraoke()) {
			return;
		}

		int visibility = getVisibility();

		if (text != null && isKaraokeUri(Uri.parse(text.toString()))) {
			visibility = View.GONE;
		}

		setVisibility(visibility);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		// Log.e(toString(), getMethodName() + text);


		super.onTextChanged(text, start, lengthBefore, lengthAfter);

		checkKaraoke(text);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Log.e(toString(), getMethodName() + canvas);

		super.onDraw(canvas);
	}

	@Override
	public void setVisibility(int visibility) {

		String text = getText().toString();

		// Log.e(toString(), getMethodName() + text);

		if (isKaraokeUri(Uri.parse(text))) {
			visibility = View.GONE;
		}

		super.setVisibility(visibility);
	}

	public final void setPath(CharSequence text) {
		setText(text);
		checkKaraoke(text);
	}
}
