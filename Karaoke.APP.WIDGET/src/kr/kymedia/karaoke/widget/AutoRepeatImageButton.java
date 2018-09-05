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
 * filename	:	AutoRepeatableButton.java
 * author	:	isyoon
 *
 * <pre>
 * com.example.soundtouch_test
 *    |_ AutoRepeatableButton.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 3. 7.
 * @version 1.0
 */
public class AutoRepeatImageButton extends ImageButton {
	private final long initialRepeatDelay = 500;
	private final long repeatIntervalInMilliseconds = 100;

	// speedup
	private long repeatIntervalCurrent = repeatIntervalInMilliseconds;
	private final long repeatIntervalStep = 2;
	private final long repeatIntervalMin = 10;

	private final Runnable repeatClickWhileButtonHeldRunnable = new Runnable() {
		@Override
		public void run() {
			// Perform the present repetition of the click action provided by the user
			// in setOnClickListener().
			performClick();

			// Schedule the next repetitions of the click action,
			// faster and faster until it reaches repeaterIntervalMin
			if (repeatIntervalCurrent > repeatIntervalMin)
				repeatIntervalCurrent = repeatIntervalCurrent - repeatIntervalStep;

			postDelayed(repeatClickWhileButtonHeldRunnable, repeatIntervalCurrent);
		}
	};

	private void commonConstructorCode() {
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					// Just to be sure that we removed all callbacks,
					// which should have occurred in the ACTION_UP
					removeCallbacks(repeatClickWhileButtonHeldRunnable);

					// Perform the default click action.
					performClick();

					// Schedule the start of repetitions after a one half second delay.
					repeatIntervalCurrent = repeatIntervalInMilliseconds;
					postDelayed(repeatClickWhileButtonHeldRunnable, initialRepeatDelay);
				} else if (action == MotionEvent.ACTION_UP) {
					// Cancel any repetition in progress.
					removeCallbacks(repeatClickWhileButtonHeldRunnable);
				}

				// Check user moved outside bounds
				Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
				if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
					removeCallbacks(repeatClickWhileButtonHeldRunnable);
				}

				// Returning true here prevents performClick() from getting called
				// in the usual manner, which would be redundant, given that we are
				// already calling it above.
				return true;
			}

		});
	}

	public AutoRepeatImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonConstructorCode();
	}

	public AutoRepeatImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonConstructorCode();
	}

	public AutoRepeatImageButton(Context context) {
		super(context);
		commonConstructorCode();
	}
}
