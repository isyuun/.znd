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

import kr.kymedia.karaoke.widget.util.WidgetUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
@SuppressLint("NewApi")
public class KaraokeEdit extends RelativeLayout {
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

	private final KaraokePath edit;

	public KaraokeEdit(Context context) {
		super(context);
		this.edit = new KaraokePath(context);
	}

	public KaraokeEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.edit = new KaraokePath(context);
	}

	public KaraokeEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.edit = new KaraokePath(context);
	}

	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();

		addView(edit);

		LayoutParams params = (LayoutParams) this.edit.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		this.edit.setLayoutParams(params);

		this.edit.setFocusable(true);
		this.edit.setFocusableInTouchMode(true);
		this.edit.setClickable(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			this.edit.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {

				@Override
				public void onSystemUiVisibilityChange(int visibility) {

					// Log.d(edit.toString(), getMethodName() + visibility);
					if (visibility == 6) {
						setEditable(false);
					}

				}
			});
		}

		this.edit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// Log.w(edit.toString(), getMethodName() + v + actionId + event);

				return false;
			}
		});
	}

	public void setEditable(boolean enabled) {
		// Log.e(toString(), getMethodName() + enabled);

		this.edit.setFocusable(true);
		this.edit.setFocusableInTouchMode(true);
		this.edit.setClickable(true);

		if (enabled) {
			WidgetUtils.showSoftKeyboard(getContext(), edit);
			this.edit.setVisibility(View.VISIBLE);
		} else {
			WidgetUtils.hideSoftKeyboard(getContext(), edit);
			this.edit.setVisibility(View.GONE);
		}

		clearFocus();

		if (enabled) {
			WidgetUtils.showSoftKeyboard(getContext(), edit);
		} else {
			WidgetUtils.hideSoftKeyboard(getContext(), edit);
		}

	}

	public void setTextIsSelectable(boolean selectable) {
		// this.edit.setTextIsSelectable(selectable);
	}

	public Editable getText() {
		return this.edit.getText();
	}

	public void setText(CharSequence text) {
		this.edit.setText(text);
	}

	protected boolean getDefaultEditable() {
		return true;
	}

	protected MovementMethod getDefaultMovementMethod() {
		return ArrowKeyMovementMethod.getInstance();
	}

	/**
	 * Convenience for {@link Selection#setSelection(Spannable, int, int)}.
	 */
	public void setSelection(int start, int stop) {
		Selection.setSelection(getText(), start, stop);
	}

	/**
	 * Convenience for {@link Selection#setSelection(Spannable, int)}.
	 */
	public void setSelection(int index) {
		Selection.setSelection(getText(), index);
	}

	/**
	 * Convenience for {@link Selection#selectAll}.
	 */
	public void selectAll() {
		Selection.selectAll(getText());
	}

	/**
	 * Convenience for {@link Selection#extendSelection}.
	 */
	public void extendSelection(int index) {
		Selection.extendSelection(getText(), index);
	}

	public void setEllipsize(TextUtils.TruncateAt ellipsis) {
		if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
			throw new IllegalArgumentException("EditText cannot use the ellipsize mode "
					+ "TextUtils.TruncateAt.MARQUEE");
		}
		this.edit.setEllipsize(ellipsis);
	}

	@Override
	public void setGravity(int gravity) {
		super.setGravity(gravity);
		this.edit.setGravity(gravity);
	}

	public void setTextAppearance(Context context, int resid) {
		if (!isInEditMode()) {
			this.edit.setTextAppearance(context, resid);
		}
	}

	public void setSingleLine() {
		this.edit.setSingleLine();
	}

	public void setSingleLine(boolean singleLine) {
		this.edit.setSingleLine(singleLine);
	}

	public void setTextSize(float size) {
		this.edit.setTextSize(size);
	}

	public void setTextSize(int unit, float size) {
		this.edit.setTextSize(unit, size);
	}
}
