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
 * filename	:	AutoRepeatTextView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.widget
 *    |_ AutoRepeatTextView.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 5. 14.
 * @version 1.0
 * @see AutoRepeatTextView.java
 */

public class AutoRepeatTextView extends TextView {

	private long initialRepeatDelay = 500;
	private long repeatIntervalInMilliseconds = 100;

	private Runnable repeatClickWhileTextViewHeldRunnable = new Runnable() {
		@Override
		public void run() {
			// Perform the present repetition of the click action provided by the user
			// in setOnClickListener().
			performClick();

			// Schedule the next repetitions of the click action, using a faster repeat
			// interval than the initial repeat delay interval.
			postDelayed(repeatClickWhileTextViewHeldRunnable, repeatIntervalInMilliseconds);
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
					removeCallbacks(repeatClickWhileTextViewHeldRunnable);

					// Perform the default click action.
					performClick();

					// Schedule the start of repetitions after a one half second delay.
					postDelayed(repeatClickWhileTextViewHeldRunnable, initialRepeatDelay);
				} else if (action == MotionEvent.ACTION_UP) {
					// Cancel any repetition in progress.
					removeCallbacks(repeatClickWhileTextViewHeldRunnable);
				}

				// Returning true here prevents performClick() from getting called
				// in the usual manner, which would be redundant, given that we are
				// already calling it above.
				return true;
			}
		});
	}

	public AutoRepeatTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonConstructorCode();
	}

	public AutoRepeatTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonConstructorCode();
	}

	public AutoRepeatTextView(Context context) {
		super(context);
		commonConstructorCode();
	}
}
