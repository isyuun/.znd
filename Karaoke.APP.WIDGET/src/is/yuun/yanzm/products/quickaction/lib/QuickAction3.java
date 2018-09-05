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
 * project	:	Karaoke.KPOP.APP
 * filename	:	QuickAction3.java
 * author	:	isyoon
 *
 * <pre>
 * yanzm.products.quickaction.lib
 *    |_ QuickAction3.java
 * </pre>
 * 
 */

package is.yuun.yanzm.products.quickaction.lib;

import kr.kymedia.karaoke.app.widget.R;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * TODO<br>
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 9. 25.
 * @version 1.0
 * @see QuickAction3.java
 */
public class QuickAction3 extends QuickAction2 {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	public QuickAction3(View anchor) {
		super(anchor);
	}

	public QuickAction3(View anchor, int layoutId, int layoutStyle, int itemLayoutId) {
		super(anchor, layoutId, layoutStyle, itemLayoutId);
	}

	public QuickAction3(View anchor, int layoutId, int layoutStyle) {
		super(anchor, layoutId, layoutStyle);
	}

	public ViewGroup getTrack() {
		return mTrack;
	}

	private OnLongClickListener listener;

	/**
	 * Set on click listener
	 * 
	 * @param listener
	 *          on click listener {@link View.OnClickListener}
	 */
	public void setOnLongClickListener(OnLongClickListener listener) {
		this.listener = listener;
	}

	@Override
	protected View getActionItem(String title, Drawable icon, OnClickListener listener) {

		View container = super.getActionItem(title, icon, listener);

		ImageView img = (ImageView) container.findViewById(R.id.icon);
		TextView text = (TextView) container.findViewById(R.id.title);

		// img.setContentDescription(title);
		text.setContentDescription(title);

		if (this.listener != null) {
			text.setClickable(true);
			text.setFocusable(true);
			// img.setOnLongClickListener(this.listener);
			text.setOnLongClickListener(this.listener);
		}

		return container;
	}

}
