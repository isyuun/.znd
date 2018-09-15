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
 * filename	:	EnabledChildViewGroup.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.view
 *    |_ EnabledChildViewGroup.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 26.
 * @version 1.0
 */
@Deprecated
public class EnabledViewGroup extends ViewGroup {

	public EnabledViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EnabledViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EnabledViewGroup(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.setEnabled(enabled);
			// if (v instanceof ViewGroup) {
			// setEnabled((ViewGroup) v, enabled);
			// } else {
			// v.setEnabled(enabled);
			// }
		}
	}

	public void setEnabled(ViewGroup p, boolean enabled) {

	}

}
