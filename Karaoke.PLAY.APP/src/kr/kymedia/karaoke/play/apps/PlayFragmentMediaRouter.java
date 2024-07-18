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
 * project	:	Karaoke.PLAY4.APP.CAST
 * filename	:	PlayFragmentCast.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app.cast
 *    |_ PlayFragmentCast.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util.EnvironmentUtils;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.util.TextUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.mediarouter.app.MediaRouteActionProvider;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaItemStatus;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import androidx.mediarouter.media.MediaSessionStatus;
import androidx.mediarouter.media.RemotePlaybackClient;
import androidx.mediarouter.media.RemotePlaybackClient.ItemActionCallback;
import androidx.mediarouter.media.RemotePlaybackClient.SessionActionCallback;
import androidx.mediarouter.media.RemotePlaybackClient.StatusCallback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 *
 * TODO<br>
 * 
 * <pre>
 * 개시발되도안걸만들어서는씨팔
 * </pre>
 *
 * <a href="https://developer.android.com/guide/topics/media/mediarouter.html">Media Router</a>
 * 
 * @author isyoon
 * @since 2014. 9. 30.
 * @version 1.0
 */
@Deprecated
public class PlayFragmentMediaRouter extends PlayFragment4 {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		_Log.i(__CLASSNAME__, getMethodName());
		// Inflate the menu and configure the media router action provider.
		inflater.inflate(R.menu.play, menu);

		// Attach the MediaRouteSelector to the menu item
		MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
		MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat
				.getActionProvider(mediaRouteMenuItem);
		mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

		// Return true to show the menu.
		super.onCreateOptionsMenu(menu, inflater);
	}

	private MediaRouter mMediaRouter;
	private MediaRouteSelector mMediaRouteSelector;

	@Override
	protected void onActivityCreated() {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onActivityCreated();

		// Create a route selector for the type of routes your app supports.
		mMediaRouteSelector = new MediaRouteSelector.Builder()
				// These are the framework-supported intents
				.addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
				.addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
				.addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK).build();

		// String APP_ID = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
		// mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(
		// CastMediaControlIntent.categoryForCast(APP_ID)).build();

		// Get the media router service.
		mMediaRouter = MediaRouter.getInstance(getActivity());

		setHasOptionsMenu(true);
	}

	// Add the callback on start to tell the media router what kinds of routes
	// your app works with so the framework can discover them.
	@Override
	public void onStart() {
		_Log.i(__CLASSNAME__, getMethodName());
		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
				MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
		super.onStart();
	}

	// Remove the selector on stop to tell the media router that it no longer
	// needs to discover routes for your app.
	@Override
	public void onStop() {
		_Log.i(__CLASSNAME__, getMethodName());
		mMediaRouter.removeCallback(mMediaRouterCallback);
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		_Log.i(__CLASSNAME__, getMethodName());

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		_Log.e(__CLASSNAME__, getMethodName());


		super.onDestroy();
		try {
			stop();

			if (mRemotePlaybackClient != null) {
				mRemotePlaybackClient.setStatusCallback(null);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {

		@Override
		public void onRouteSelected(MediaRouter router, RouteInfo route) {
			_Log.i(__CLASSNAME__, getMethodName());

			if (route.supportsControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
				// remote playback device
				updateRemotePlayer(route);
			} else {
				// secondary output device
				updatePresentation(route);
			}
		}

		@Override
		public void onRouteUnselected(MediaRouter router, RouteInfo route) {
			_Log.i(__CLASSNAME__, getMethodName());

			if (route.supportsControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
				// remote playback device
				updateRemotePlayer(route);
			} else {
				// secondary output device
				updatePresentation(route);
			}
		}

		@Override
		public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo route) {
			_Log.i(__CLASSNAME__, getMethodName());

			if (route.supportsControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
				// remote playback device
				updateRemotePlayer(route);
			} else {
				// secondary output device
				updatePresentation(route);
			}
		}
	};

	public void updatePresentation(RouteInfo route) {
	}

	private RouteInfo mRoute;
	private RemotePlaybackClient mRemotePlaybackClient;

	private void updateRemotePlayer(RouteInfo route) {
		_Log.e(__CLASSNAME__, getMethodName() + "route=" + route);

		// Changed route: tear down previous client
		if (mRoute != null && mRemotePlaybackClient != null) {
			mRemotePlaybackClient.release();
			mRemotePlaybackClient = null;
		}

		// Save new route
		mRoute = route;

		// Attach new playback client
		mRemotePlaybackClient = new RemotePlaybackClient(getActivity(), mRoute);

		play();

		mRemotePlaybackClient.setStatusCallback(new StatusCallback() {

			@Override
			public void onItemStatusChanged(Bundle data, String sessionId,
					MediaSessionStatus sessionStatus, String itemId, MediaItemStatus itemStatus) {
				_Log.e(__CLASSNAME__, getMethodName());

				super.onItemStatusChanged(data, sessionId, sessionStatus, itemId, itemStatus);
			}

			@Override
			public void onSessionChanged(String sessionId) {
				_Log.e(__CLASSNAME__, getMethodName());

				super.onSessionChanged(sessionId);
			}

			@Override
			public void onSessionStatusChanged(Bundle data, String sessionId,
					MediaSessionStatus sessionStatus) {
				_Log.e(__CLASSNAME__, getMethodName());

				super.onSessionStatusChanged(data, sessionId, sessionStatus);
			}
		});

	}

	@Override
	public boolean play() {

		String path = getPath();

		if (TextUtil.isEmpty(path)) {
			return false;
		}

		if (mRemotePlaybackClient != null) {

			if (!TextUtil.isNetworkUrl(path)) {
				String ip = EnvironmentUtils.getIpAddress();
				path = "http://" + ip + ":8089/nnnn.mp3";
				_Log.e(__CLASSNAME__, getMethodName() + path);
			}

			try {
				// Send file for playback
				mRemotePlaybackClient.play(Uri.parse(path), "audio/*",
						// Uri.parse("http://archive.org/download/Sintel/sintel-2048-stereo_512kb.mp4"), "video/mp4",
						null, 0, null, new ItemActionCallback() {

							@Override
							public void onResult(Bundle data, String sessionId, MediaSessionStatus sessionStatus,
									String itemId, MediaItemStatus itemStatus) {
								_Log.i(__CLASSNAME__, getMethodName() + "play: succeeded for item " + itemId);
								// super.onResult(data, sessionId, sessionStatus, itemId, itemStatus);
							}

							@Override
							public void onError(String error, int code, Bundle data) {
								_Log.i(__CLASSNAME__, getMethodName() + "play: failed - error:" + code + " - " + error);
								// super.onError(error, code, data);
							}
						});
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		} else {
			return super.play();
		}
		return true;
	}

	@Override
	public void stop() {

		if (mRemotePlaybackClient != null) {
			try {
				if (TextUtil.isEmpty(mRemotePlaybackClient.getSessionId())) {
					return;
				}
				mRemotePlaybackClient.stop(null, new SessionActionCallback() {

					@Override
					public void onResult(Bundle data, String sessionId, MediaSessionStatus sessionStatus) {
						_Log.i(__CLASSNAME__, getMethodName() + "stop: succeeded for item " + sessionStatus);

						super.onResult(data, sessionId, sessionStatus);
					}

					@Override
					public void onError(String error, int code, Bundle data) {
						_Log.i(__CLASSNAME__, getMethodName() + "stop: failed - error:" + code + " - " + error);

						super.onError(error, code, data);
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			super.stop();
		}
	}

	@Override
	public void pause() {

		if (mRemotePlaybackClient != null) {
			try {
				if (TextUtil.isEmpty(mRemotePlaybackClient.getSessionId())) {
					return;
				}
				mRemotePlaybackClient.pause(null, new SessionActionCallback() {

					@Override
					public void onResult(Bundle data, String sessionId, MediaSessionStatus sessionStatus) {
						_Log.i(__CLASSNAME__, getMethodName() + "pause: succeeded for item " + sessionStatus);

						super.onResult(data, sessionId, sessionStatus);
					}

					@Override
					public void onError(String error, int code, Bundle data) {
						_Log.i(__CLASSNAME__, getMethodName() + "pause: failed - error:" + code + " - " + error);

						super.onError(error, code, data);
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			super.pause();
		}
	}

	@Override
	public void seek(int msec) {

		if (mRemotePlaybackClient != null) {
			try {
				if (TextUtil.isEmpty(mRemotePlaybackClient.getSessionId())) {
					return;
				}
				mRemotePlaybackClient.seek(mRemotePlaybackClient.getSessionId(), msec, null,
						new ItemActionCallback() {

							@Override
							public void onResult(Bundle data, String sessionId, MediaSessionStatus sessionStatus,
									String itemId, MediaItemStatus itemStatus) {
								_Log.i(__CLASSNAME__, getMethodName() + "seek: succeeded for item " + itemId);
							}

							@Override
							public void onError(String error, int code, Bundle data) {
								_Log.i(__CLASSNAME__, getMethodName() + "seek: failed - error:" + code + " - " + error);
							}
						});
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			super.seek(msec);
		}
	}

}
