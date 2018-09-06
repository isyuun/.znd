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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	CircularSeekBar.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.widget
 *    |_ CircularSeekBar.java
 * </pre>
 */
package com.devadvance.circularseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import kr.kymedia.karaoke.app.widget.BuildConfig;
import kr.kymedia.karaoke.app.widget.R;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015-11-03
 */
public class CircularSeekBar2 extends CircularSeekBar {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public CircularSeekBar2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CircularSeekBar2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircularSeekBar2(Context context) {
		super(context);
	}

	Animation animLoadingRotate;
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		animLoadingRotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate);
	}

	@Override
	protected void calculateProgressDegrees() {
		//super.calculateProgressDegrees();
		mProgressDegrees = mPointerPosition - mStartAngle; // Verified
		if (mProgress > 0) {
			mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
		}
	}

	private int getComplimentColor(int color) {
		// get existing colors
		int alpha = Color.alpha(color);
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);

		// find compliments
		red = (~red) & 0xff;
		blue = (~blue) & 0xff;
		green = (~green) & 0xff;

		return Color.argb(alpha, red, green, blue);
	}

	private void checkProgressColor() {
		if (mProgress < 0) {
			mCircleProgressPaint.setColor(getComplimentColor(mCircleProgressColor));
			mPointerPaint.setColor(getComplimentColor(mPointerColor));
			mPointerHaloPaint.setColor(getComplimentColor(mPointerHaloColor));
		} else {
			mCircleProgressPaint.setColor(mCircleProgressColor);
			mPointerPaint.setColor(mPointerColor);
			mPointerHaloPaint.setColor(mPointerHaloColor);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		checkProgressColor();

		canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

		canvas.drawPath(mCirclePath, mCirclePaint);

		canvas.drawPath(mCircleProgressPath, mCircleProgressGlowPaint);
		canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);

		canvas.drawPath(mCirclePath, mCircleFillPaint);

		if (!isLoading) {
			canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth, mPointerHaloPaint);
			canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius, mPointerPaint);
			//if (mUserIsMovingPointer)
			{
				canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth + (mPointerHaloBorderWidth / 2f), mPointerHaloBorderPaint);
			}
		}
	}

	private Runnable invalidate = new Runnable() {
		@Override
		public void run() {
			invalidate();
		}
	};

	boolean isLoading = false;

	public void startLoading() {
		isLoading = true;
		post(invalidate);
		setMax(100);
		setProgress(0);
		post(invalidate);
		(new startLoading()).start();
	}

	class startLoading extends Thread {
		long tick = 0;

		@Override
		public void run() {
			super.run();
			//post(startRotateAnimation);
			while (isLoading) {
				try {
					//_LOG.e(_toString(), getMethodName() + (long) (Math.abs(Math.sin(Math.toRadians(tick++))) * 100.0f + 10.0f));
					if (getMax() > getProgress()) {
						post(increaseProgress);
					} else {
						post(startRotateAnimation);
						interrupt();
						break;
					}
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Runnable increaseProgress = new Runnable() {
		@Override
		public void run() {
			if (getMax() > getProgress()) {
				setProgress(getProgress() + 1);
			} else {
				setProgress(0);
			}
			post(invalidate);
		}
	};

	private Runnable startRotateAnimation = new Runnable() {
			@Override
			public void run() {
			setMax(100);
			setProgress(5);
			startAnimation(animLoadingRotate);
			post(invalidate);
		}
	};

	public void stopLoading() {
		isLoading = false;
		post(invalidate);
		post(clearAnimation);
	}

	private Runnable clearAnimation = new Runnable() {
		@Override
		public void run() {
			clearAnimation();
			setMax(100);
			setProgress(0);
		}
	};
}
