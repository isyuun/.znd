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
 * filename	:	KaraokeText.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ KaraokeText.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import kr.kymedia.karaoke.util.TextUtil;
import kr.kymedia.karaoke.widget.util.WidgetUtils;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 29.
 * @version 1.0
 */
public class KaraokeText extends KaraokeEdit {
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

	private final TextView text;

	public KaraokeText(Context context) {
		super(context);
		text = new TextView(context);
	}

	public KaraokeText(Context context, AttributeSet attrs) {
		super(context, attrs);
		text = new TextView(context);
	}

	public KaraokeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		text = new TextView(context);
	}

	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();

		addView(text);

		LayoutParams params = (LayoutParams) this.text.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		this.text.setLayoutParams(params);

		this.text.setFocusable(true);
		this.text.setFocusableInTouchMode(true);
		this.text.setClickable(true);

		this.text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setTextMarquee();
			}
		});

		this.text.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				setEditable(true);
				return false;
			}
		});
	}

	@Override
	public void setEditable(boolean enabled) {
		super.setEditable(enabled);

		this.text.setFocusable(true);
		this.text.setFocusableInTouchMode(true);
		this.text.setClickable(true);

		if (enabled) {
			this.text.setVisibility(View.GONE);
		} else {
			this.text.setVisibility(View.VISIBLE);
		}

		setTextMarquee(!enabled);

		checkPath();
	}

	private boolean checkPath() {

		boolean ret = false;

		String path = getText().toString();
		ret = TextUtil.isKaraokeUri(KaraokePath.getHosts(), path);

		this.text.setText(path);

		if (ret) {
			this.text.setVisibility(View.INVISIBLE);
		}

		return ret;
	}

	@Override
	public void setTextIsSelectable(boolean selectable) {
		super.setTextIsSelectable(selectable);
		this.text.setTextIsSelectable(selectable);
	}

	@Override
	public void setText(CharSequence text) {
		super.setText(text);
		this.text.setText(text);
	}

	private void setTextMarquee() {
		if (text.getEllipsize() == TruncateAt.MARQUEE) {
			setTextMarquee(false);
		} else {
			setTextMarquee(true);
		}
	}

	private void setTextMarquee(boolean enable) {
		WidgetUtils.setTextViewMarquee(text, enable);
	}

	@Override
	public void setGravity(int gravity) {

		super.setGravity(gravity);
		this.text.setGravity(gravity);
	}

	@Override
	public void setTextAppearance(Context context, int resid) {

		super.setTextAppearance(context, resid);
		if (!isInEditMode()) {
			this.text.setTextAppearance(context, resid);
		}

	}

	@Override
	public void setSingleLine() {

		super.setSingleLine();
		this.text.setSingleLine();
	}

	@Override
	public void setSingleLine(boolean singleLine) {

		super.setSingleLine(singleLine);
		this.text.setSingleLine(singleLine);
	}

	@Override
	public void setTextSize(float size) {

		super.setTextSize(size);
		this.text.setTextSize(size);
	}

	@Override
	public void setTextSize(int unit, float size) {

		super.setTextSize(unit, size);
		this.text.setTextSize(unit, size);
	}

}
