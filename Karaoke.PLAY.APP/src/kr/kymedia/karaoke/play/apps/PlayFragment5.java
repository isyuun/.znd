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
 * filename	:	PlayFragmentChromeCast.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ PlayFragmentChromeCast.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.os.PriorityAsyncTask;
import kr.kymedia.karaoke.play.SongPlayCastChrome;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view._PlayView;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 *
 * TODO<br>
 * 
 * <pre>
 * 크롬캐스트
 * </pre>
 *
 * @author isyoon
 * @since 2014. 9. 26.
 * @version 1.0
 */
public class PlayFragment5 extends PlayFragment4 {
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

	SongPlayCastChrome songCast;

	private static final int REQUEST_GMS_ERROR = 0;

	// private static final String APP_ID = "5001E5A6";
	private static final String APP_ID = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

	private MediaRouter.Callback mMediaRouterCallback;

	/**
	 * Called when the activity is first created. Initializes the game with
	 * necessary listeners for player interaction, and creates a new cast
	 * channel.
	 */
	@Override
	protected void onActivityCreated() {
		Log.i(__CLASSNAME__, getMethodName());
		songCast = new SongPlayCastChrome(getApplicationContext(), APP_ID);
		mMediaRouterCallback = new MediaRouterCallback();


		setHasOptionsMenu(true);

		super.onActivityCreated();
	}

	/**
	 * An extension of the MediaRoute.Callback specifically for the TicTacToe
	 * game.
	 */
	private class MediaRouterCallback extends MediaRouter.Callback {
		@Override
		public void onRouteSelected(MediaRouter router, RouteInfo route) {
			// _Log.d(__CLASSNAME__, "onRouteSelected: " + route);
			PlayFragment5.this.onRouteSelected(route);
		}

		@Override
		public void onRouteUnselected(MediaRouter router, RouteInfo route) {
			// _Log.d(__CLASSNAME__, "onRouteUnselected: " + route);
			PlayFragment5.this.onRouteUnselected(route);
		}
	}

	/**
	 * Called when a user selects a route.
	 */
	private void onRouteSelected(RouteInfo route) {
		Log.e(__CLASSNAME__, "onRouteSelected: " + route.getName());
		getLead().showSongControl(false);
		if (findViewById(R.id.buttonShow) != null) {
			findViewById(R.id.buttonShow).setEnabled(false);
		}
	}

	/**
	 * Called when a user unselects a route.
	 */
	private void onRouteUnselected(RouteInfo route) {
		Log.e(__CLASSNAME__, "onRouteUnselected: " + route.getName());
		// getLead().showSongControl(false);
		if (findViewById(R.id.buttonShow) != null) {
			findViewById(R.id.buttonShow).setEnabled(true);
		}
	}

	/**
	 * Called when the options menu is first created.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.play, menu);
		MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);

		songCast.onCreateOptionsMenu(mediaRouteMenuItem);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called on application start. Using the previously selected Cast device,
	 * attempts to begin a session using the application name TicTacToe.
	 */
	@Override
	public void onStart() {
		Log.e(__CLASSNAME__, getMethodName());
		super.onStart();

		songCast.onStart();
		songCast.addCallback(mMediaRouterCallback);
	}

	@Override
	public void onResume() {
		Log.e(__CLASSNAME__, getMethodName());
		super.onResume();

		songCast.onResume();
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (errorCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), REQUEST_GMS_ERROR).show();
		}
	}

	/**
	 * Removes the activity from memory when the activity is paused.
	 */
	@Override
	public void onPause() {
		Log.e(__CLASSNAME__, getMethodName());
		super.onPause();

		songCast.onPause();
	}

	/**
	 * Attempts to end the current game session when the activity stops.
	 */
	@Override
	public void onStop() {
		Log.e(__CLASSNAME__, getMethodName());

		songCast.onStop();

		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e(__CLASSNAME__, getMethodName());


		songCast.onStop();

		super.onDestroy();
	}

	/**
	 * Returns the screen configuration to portrait mode whenever changed.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void setPath(String path) {
		Log.e(__CLASSNAME__, getMethodName() + songCast);


		super.setPath(path);

		if (songCast != null) {
			songCast.setPath(path);
		}
	}

	@Override
	protected void setEnabled(boolean enabled) {
		Log.e(__CLASSNAME__, getMethodName() + enabled);

		if (isDetached() || getActivity().isDestroyed()) {
			return;
		}
		findViewById(R.id.buttonStream).setEnabled(enabled);
		findViewById(R.id.buttonPlay).setEnabled(enabled);
		// findViewById(R.id.buttonStop).setEnabled(enabled);
		findViewById(R.id.seekPlay).setEnabled(enabled);


		super.setEnabled(enabled);
	}

	@Override
	public boolean load(final String path) throws Exception {
		Log.e(__CLASSNAME__, getMethodName() + path);


		if (songCast != null && songCast.isConnected()) {
			setEnabled(false);
			songCast.setHandler(this.handler);
			songCast.setOnListener(this);
			// songCast.open(path);
			(new open()).execute(path);
			return true;
		} else {
			return super.load(path);
		}
	}

	private class open extends PriorityAsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// _Log.e(PlayFragmentCastChrome.this.toString(), getMethodName() + getPath());

			if (!TextUtils.isEmpty(getPath())) {
				open();
			}
			return null;
		}

	}

	private void open() {
		songCast.load(getPath());
	}

	@Override
	protected _PlayView getLead() {
		return (_PlayView) super.getLead();
	}

	@Override
	public boolean play() {
		Log.e(__CLASSNAME__, getMethodName());

		boolean ret = false;
		if (songCast != null && songCast.isConnected()) {
			ret = songCast.play();
		} else {
			ret = super.play();
		}

		getLead().setPlay();

		return ret;
	}

	@Override
	public void prepare() {
		Log.e(__CLASSNAME__, getMethodName());

		super.prepare();

		post(new Runnable() {

			@Override
			public void run() {

				setEnabled(true);
			}
		});

	}

	@Override
	public void stop() {
		Log.e(__CLASSNAME__, getMethodName());


		if (songCast != null && songCast.isPrepared() && songCast.isPlaying()) {
			songCast.stop();
			getLead().setStop(true);
		} else {
			super.stop();
		}

	}

	@Override
	public void pause() {
		Log.e(__CLASSNAME__, getMethodName());

		if (songCast != null && songCast.isPrepared() && songCast.isPlaying()) {
			songCast.pause();
		} else {
			super.pause();
		}

		getLead().setPause();
	}

	@Override
	public void seek(int msec) {
		Log.e(__CLASSNAME__, getMethodName() + msec);

		if (songCast != null && songCast.isPrepared() && songCast.isPlaying()) {
			songCast.seek(msec);
		} else {
			super.seek(msec);
		}

		getLead().setSeek(msec);
	}

	@Override
	public void onTime(int t) {

		// _Log.d(__CLASSNAME__, getMethodName() + t);
		super.onTime(t);

		getLead().setOnTime(t);
	}

	@Override
	public int getTotalTime() {

		// return super.getTotalTime();
		int ret = 0;
		if (songCast != null && songCast.isPrepared()) {
			ret = songCast.getTotalTime();
		} else {
			ret = super.getTotalTime();
		}
		Log.e(__CLASSNAME__, getMethodName() + ret);
		return ret;
	}

	@Override
	public int getCurrentTime() {

		// return super.getCurrentTime();
		int ret = 0;
		if (songCast != null && songCast.isPrepared()) {
			ret = songCast.getCurrentTime();
		} else {
			ret = super.getCurrentTime();
		}
		Log.e(__CLASSNAME__, getMethodName() + ret);
		return ret;
	}

	public boolean tracking = false;

	@Override
	void progress(boolean init) {
		Log.e(__CLASSNAME__, getMethodName() + init);

		super.progress(init);

		seekPlay = (SeekBar) findViewById(R.id.seekPlay);
		if (seekPlay != null) {
			seekPlay.setMax(getTotalTime());
		}
	}

	@Override
	public void onCompletion() {
		Log.e(__CLASSNAME__, getMethodName());

		getLead().putNextPath();
		super.onCompletion();
	}

	@Override
	public void restart() {

		if (songCast != null && songCast.isPrepared()) {
			songCast.restart();
		} else {
			super.restart();
		}
	}

	@Override
	public void repeat() {

		if (songCast != null && songCast.isPrepared()) {
			songCast.repeat();
		} else {
			super.repeat();
		}
	}

}
