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

package kr.kymedia.karaoke.widget;

import kr.kymedia.karaoke.util.TextUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 22.
 * @version 1.0
 */
public class KaraokePath extends EditText {
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

	/**
	 * <pre>
	 (구)KYMedia 서버
	 * mp3 : http://resource.kymedia.kr/ky/mp/01/00101.mp3
	 * 가사 : http://resource.kymedia.kr/ky/md/01/00101.mid
	 * skym : http://resource.kymedia.kr/ky/skym/01/00101.skym
	 (현) 금영그룹서버
	 * 211.236.190.103
	 * 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
	 * 사이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
	 * 신규서버(가사): http://www.keumyoung.kr/.api/.skym.asp?song_id=08888
	 * 신규서버(음원): http://www.keumyoung.kr/.api/.mmp3.asp?song_id=08888
	 * </pre>
	 */
	private final static String hosts[] = { ("211.236.190.103"), ("resource.kymedia.kr"), ("cyms.chorus.co.kr"), ("www.keumyoung.kr/.api")};
	public static String[] getHosts() {
		return hosts;
	}

	// private static String hosts[] = null;

	public boolean isKaraokeUri(Uri uri) {
		// _Log.e(toString(), getMethodName() + uri);

		if (!isKaraoke()) {
			return false;
		}

		boolean ret = TextUtil.isKaraokeUri(hosts, uri);

		// _Log.e(toString(), getMethodName() + ret + uri);

		return ret;
	}

	public void checkKaraoke(CharSequence text) {
		// _Log.e(toString(), getMethodName() + text);

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
		// _Log.e(toString(), getMethodName() + text);


		super.onTextChanged(text, start, lengthBefore, lengthAfter);

		checkKaraoke(text);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// _Log.e(toString(), getMethodName() + canvas);

		super.onDraw(canvas);
	}

	@Override
	public void setVisibility(int visibility) {

		String text = getText().toString();

		// _Log.e(toString(), getMethodName() + text);

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
