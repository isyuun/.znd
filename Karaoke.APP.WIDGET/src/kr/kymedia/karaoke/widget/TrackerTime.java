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
 * filename	:	TimerPopup.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.widget
 *    |_ TimerPopup.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import kr.kymedia.karaoke.util.TextUtil;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 3. 21.
 * @version 1.0
 */

public class TrackerTime extends PopupWindow implements OnTouchListener {
	private String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	TextView mTextPopupTime = null;
	boolean isShow = false;
	/**
	 * 그래비티~~~
	 */
	public int gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

	public boolean isShow() {
		return isShow;
	}

	// /**
	// * @param isShow the isShow to set
	// */
	// public void setShow(boolean isShow) {
	// this.isShow = isShow;
	// }

	Context mContext = null;

	public TrackerTime(Context context, int bcolor) {
		super(context);

		this.mContext = context;

		int w = 100;
		int h = 60;

		mTextPopupTime = new TextView(context);
		mTextPopupTime.setGravity(Gravity.CENTER);
		mTextPopupTime.setText("00:00");
		mTextPopupTime.setWidth(w);
		mTextPopupTime.setHeight(h);
		mTextPopupTime.setBackgroundColor(bcolor);

		// mPopupWindowTime = new PopupWindow(context);
		setContentView(mTextPopupTime);
		setWidth(w);
		setHeight(h);
		setAnimationStyle(android.R.style.Animation_InputMethod);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		dismiss();
		return false;
	}

	public void showPopupTime(View parent, boolean show) {

		try {
			isShow = show;

			// devbible :: [Android] View의 절대좌표 구하기
			View root = parent.getRootView();
			int sumX = 0;
			int sumY = 0;

			boolean chk = false;
			while (!chk) {
				sumX = sumX + parent.getLeft();
				sumY = sumY + parent.getTop();

				parent = (View) parent.getParent();
				if (root == parent) {
					chk = true;
				}
			}
			// android.util.if (_IKaraoke.DEBUG)_Log.d("","rawX : "+sumX);
			// android.util.if (_IKaraoke.DEBUG)_Log.d("","rawY : "+sumY);

			int offX = 0;
			int offY = sumY;

			if (show) {
				showAtLocation(parent, gravity, offX, offY);
			} else {
				dismiss();
			}

			// if (_IKaraoke.DEBUG)_Log.i(__CLASSNAME__, getMethodName());

		} catch (Exception e) {

			// e.printStackTrace();
		}
	}

	public void updatePopupTime(int msec) {
		if (mTextPopupTime == null) {
			return;
		}

		if (!isShow) {
			return;
		}

		String pos = TextUtil.getTimeTrackerString(msec);

		mTextPopupTime.setText(pos);
	}

	public void updatePopupTime(String format, int msec, boolean isHmsec) {
		if (mTextPopupTime == null) {
			return;
		}

		if (!isShow) {
			return;
		}

		String pos = TextUtil.getTimeTrackerString(format, msec, isHmsec);

		mTextPopupTime.setText(pos);
	}

}
