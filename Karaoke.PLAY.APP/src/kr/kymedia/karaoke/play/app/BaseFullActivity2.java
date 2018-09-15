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
 * project	:	Karaoke.PLAY4
 * filename	:	PlayActivity.java
 * author	:	isyoon
 *
 * <pre>
 * com.example.soundtouch_test
 *    |_ PlayActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.util.Log;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

/**
 * 
 * <pre>
 * 풀스크린터치처리
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 2. 24.
 * @version 1.0
 */
public class BaseFullActivity2 extends BaseFullActivity implements OnGestureListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Log.i(__CLASSNAME__, getMethodName() + "[START]");

		// setNoTitle();
		// setActionBarOverlay(true);


		super.onCreate(savedInstanceState);

		// Log.i(__CLASSNAME__, getMethodName() + "[END]");
	}

	@Override
	protected void setFullScreen(boolean enabled) {
		Log.i(__CLASSNAME__, getMethodName() + enabled);

		super.setFullScreen(enabled);

		// hideActionBar();

		// setTranslucentSystemUI(enabled);

		mDetector = new GestureDetectorCompat(this, this);
	}

	private GestureDetectorCompat mDetector;
	Animation slide_in_left, slide_out_right;
	Animation slide_in_right, slide_out_left;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		// return false;

		if (findViewById(R.id.choir) instanceof ViewFlipper) {
			float sensitvity = 50;

			ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.choir);

			if ((e1.getX() - e2.getX()) > sensitvity) {
				viewFlipper.setInAnimation(slide_in_right);
				viewFlipper.setOutAnimation(slide_out_left);
				viewFlipper.showPrevious();
				// Toast.makeText(MainActivity.this, "Previous", Toast.LENGTH_SHORT).show();
			} else if ((e2.getX() - e1.getX()) > sensitvity) {
				viewFlipper.setInAnimation(slide_in_left);
				viewFlipper.setOutAnimation(slide_out_right);
				viewFlipper.showNext();
				// Toast.makeText(MainActivity.this, "Next", Toast.LENGTH_SHORT).show();
			}

		}

		return true;

	}

	@Override
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {


	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {


	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

}
