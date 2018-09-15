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
 * filename	:	FullScreenActivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ FullScreenActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.app.FileOpenActivity;
import kr.kymedia.karaoke.util.Log;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.WindowCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * <pre>
 * 풀스크린처리
 * </pre>
 *
 * @author isyoon
 * @since 2014. 10. 2.
 * @version 1.0
 */
public class BaseFullActivity extends FileOpenActivity implements OnTouchListener, GestureDetector.OnDoubleTapListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	static int TIMER_SYSTEM_UI_REFRESH = 5000;

	@SuppressLint("InlinedApi")
	public void setTranslucentSystemUI(boolean enabled) {
		Log.i(__CLASSNAME__, getMethodName() + enabled);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (enabled) {
				Window w = getWindow(); // in Activity's onCreate() for instance
				w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			} else {
				final WindowManager.LayoutParams attrs = getWindow().getAttributes();
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().setAttributes(attrs);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			}
		}
	}

	int defVisibility;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		defVisibility = getDecorView().getSystemUiVisibility();
	}

	private View getDecorView() {
		return getWindow().getDecorView();
	}

	boolean show = false;

	public boolean isShowSystemUI() {
		return show;
	}

	boolean enabledFullScreen = false;

	public boolean isEnabledFullScreen() {
		return enabledFullScreen;
	}

	void enableFullScreen() {
		this.enabledFullScreen = !this.enabledFullScreen;
		enableFullScreen(this.enabledFullScreen);
	}

	void enableFullScreen(boolean enabled) {
		this.enabledFullScreen = enabled;
		enableFullScreenImmersive(enabled);
		// enableFullScreenLeanback(enabled);
		// enableFullScreenStickyImmersive(enabled);
		if (enabled) {
			hideActionBar();
		} else {
			showActionBar();
		}
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	void enableFullScreenImmersive(boolean enabled) {
		// Log.i(__CLASSNAME__, getMethodName() + enabled);

		this.enabledFullScreen = enabled;

		// int newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		int newVisibility = defVisibility;

		getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {

			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				show = ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);
				// Log.e(__CLASSNAME__, getMethodName() + visibility + ":" + show);

				if (show) {
					// in show
					showActionBar();
					BaseFullActivity.this.postDelayed(new Runnable() {

						@Override
						public void run() {

							enableFullScreenImmersive(true);
						}
					}, TIMER_SYSTEM_UI_REFRESH);
				} else {
					// in hide
					hideActionBar();
				}
			}
		});

		if (enabled) {
			newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
		}

		getDecorView().setSystemUiVisibility(newVisibility);
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	void enableFullScreenStickyImmersive(boolean enabled) {
		// Log.i(__CLASSNAME__, getMethodName() + enabled);

		this.enabledFullScreen = enabled;

		// int newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		int newVisibility = defVisibility;

		getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {

			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				show = ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);
				// Log.e(__CLASSNAME__, getMethodName() + visibility + ":" + show);

				if (show) {
					// in show
					showActionBar();
					BaseFullActivity.this.postDelayed(new Runnable() {

						@Override
						public void run() {

							enableFullScreenStickyImmersive(true);
						}
					}, TIMER_SYSTEM_UI_REFRESH);
				} else {
					// in hide
					hideActionBar();
				}
			}
		});

		if (enabled) {
			newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		getDecorView().setSystemUiVisibility(newVisibility);
	}

	int mLastSystemUIVisibility;

	@SuppressLint({ "InlinedApi", "NewApi" })
	void enableFullScreenLeanback(boolean enabled) {
		// Log.i(__CLASSNAME__, getMethodName() + enabled);

		this.enabledFullScreen = enabled;

		// int newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		int newVisibility = defVisibility;

		getDecorView().setOnSystemUiVisibilityChangeListener(null);

		if (enabled) {
			newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

			getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {

				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					show = ((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);
					// Log.e(__CLASSNAME__, getMethodName() + visibility + ":" + show);

					if (show) {
						// in show
						showActionBar();
						resetHideTimer();
					} else {
						// in hide
						hideActionBar();
					}
					mLastSystemUIVisibility = visibility;
				}
			});
		}

		// Set the visibility
		getDecorView().setSystemUiVisibility(newVisibility);
	}

	private void resetHideTimer() {
		// First cancel any queued events - i.e. resetting the countdown clock
		mLeanBackHandler.removeCallbacks(mEnterLeanback);
		// And fire the event in 3s time
		mLeanBackHandler.postDelayed(mEnterLeanback, TIMER_SYSTEM_UI_REFRESH);
	}

	private final Handler mLeanBackHandler = new Handler();
	private final Runnable mEnterLeanback = new Runnable() {
		@Override
		public void run() {
			enableFullScreenLeanback(true);
		}
	};

	private boolean isFullScreen = false;

	protected void setFullScreen(boolean enabled) {
		Log.i(__CLASSNAME__, getMethodName() + enabled);

		isFullScreen = enabled;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			enableFullScreen(enabled);
		} else {
			if (enabled) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		}
	}

	private boolean isNoTitle = false;

	@SuppressLint("NewApi")
	protected void setNoTitle() {
		Log.i(__CLASSNAME__, getMethodName());

		isNoTitle = true;
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private boolean isShowActionBar = true;

	@SuppressLint("NewApi")
	protected void hideActionBar() {
		// Log.i(__CLASSNAME__, getMethodName());

		isShowActionBar = false;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				getSupportActionBar().hide();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	boolean isActionBarOverlay = false;

	@SuppressLint("InlinedApi")
	protected void setActionBarOverlay(boolean enabled) {
		Log.i(__CLASSNAME__, getMethodName() + enabled);

		isActionBarOverlay = enabled;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (enabled) {
				getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			} else {
				getWindow().clearFlags(Window.FEATURE_ACTION_BAR_OVERLAY);
			}
		} else {
			getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
		}

	}

	@Override
	public void setContentView(int layoutResID) {

		super.setContentView(layoutResID);

		if (isActionBarOverlay) {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")));
		}
	}

	@SuppressLint("NewApi")
	protected void showActionBar() {
		// Log.i(__CLASSNAME__, getMethodName());

		isShowActionBar = true;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				getSupportActionBar().show();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void setResume() {
		setFullScreen(isFullScreen);

		if (isNoTitle) {

		}

		if (isShowActionBar) {
			showActionBar();
		} else {
			hideActionBar();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

		setResume();
	}

	public GestureDetector gestureDetector;

	@Override
	protected void onStart() {
		Log.e(__CLASSNAME__, getMethodName());

		super.onStart();

		ViewGroup g = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
		g = (ViewGroup) getWindow().getDecorView().getRootView();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			g = (ViewGroup) getWindow().getDecorView().getRootView();
		}
		g.setOnTouchListener(this);

		// WidgetUtils.setOnTouchListener(getApplicationContext(), g, this);
		gestureDetector = new GestureDetector(this, new GestureListener());
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Log.e(__CLASSNAME__, getMethodName() + v + event);


		// if (v == mRootView && event.getAction() == MotionEvent.ACTION_DOWN) {
		// ((_Activity) getActivity()).enableFullScreen();
		// }

		return gestureDetector.onTouchEvent(event);
	}

	public class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {

			BaseFullActivity.this.onDoubleTap(e);
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			BaseFullActivity.this.onSingleTapConfirmed(e);
			return super.onSingleTapConfirmed(e);
		}

	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		// Log.e(__CLASSNAME__, getMethodName() + isShowSystemUI());

		// if (isShowSystemUI()) {
		// enableFullScreen(true);
		// }
		enableFullScreen();

		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {

		// Log.e(__CLASSNAME__, getMethodName() + isShowSystemUI());

		// if (!isShowSystemUI()) {
		// enableFullScreen();
		// }
		// else
		// {
		// return super.onDoubleTap(e);
		// }
		enableFullScreen();

		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {

		return false;
	}

}
