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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.PLAY4
 * filename	:	SongPlayCast.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play4
 *    |_ SongPlayCast.java
 * </pre>
 */

package kr.kymedia.karaoke.play;

import java.io.IOException;

import kr.kymedia.karaoke.os.PriorityAsyncTask;
import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.play.impl.ISongPlayCast;
import kr.kymedia.karaoke.util.TextUtil;

import org.jaudiotagger.tag.FieldKey;

import android.content.Context;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.mediarouter.app.MediaRouteActionProvider;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
//import com.google.android.gms.cast.RemoteMediaPlayer;
//import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 *
 * TODO<br>
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 10. 6.
 * @version 1.0
 */
public class SongPlayCastChrome extends SongPlayCast implements ISongPlayCast, ISongPlay.Listener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	Context context;
	String app_id;

	private CastDevice mSelectedDevice;
	private GoogleApiClient mApiClient;
	private final Cast.Listener mCastListener;
	private final ConnectionCallbacks mConnectionCallbacks;
	private final ConnectionFailedListener mConnectionFailedListener;
	private final MediaRouter mMediaRouter;
	private final MediaRouteSelector mMediaRouteSelector;
	private final MediaRouter.Callback mMediaRouterCallback;
	private final RemoteMediaPlayer mRemoteMediaPlayer;

	// private RemotePlaybackClient mRemotePlaybackClient;

	@Override
	public void reset() {
		super.reset();

		isPlaying = false;
		isPausing = false;

		// stopTimeout();
		// tryout = 0;

	}

	@Override
	public boolean isPrepared() {

		if (!isConnected()) {
			return false;
		}
		return super.isPrepared();
	}

	boolean isPlaying = false;

	@Override
	public boolean isPlaying() {

		if (!isPrepared()) {
			return false;
		}
		return isPlaying;
	}

	boolean isPausing = false;

	@Override
	public boolean isPause() {

		return isPausing;
	}

	public SongPlayCastChrome(Context context, String app_id) {
		// super("SongPlayCastChrome");
		super();
		this.context = context;
		this.app_id = app_id;

		mMediaRouter = MediaRouter.getInstance(context);
		mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(app_id)).build();

		mMediaRouterCallback = new MediaRouterCallback();
		mCastListener = new CastListener();
		mConnectionCallbacks = new ConnectionCallbacks();
		mConnectionFailedListener = new ConnectionFailedListener();

		mRemoteMediaPlayer = new RemoteMediaPlayer();
		mRemoteMediaPlayer.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener() {
			@Override
			public void onStatusUpdated() {
				try {
					// _Log.e(__CLASSNAME__, getMethodName() + mRemoteMediaPlayer.getMediaStatus());
					MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
					if (mediaStatus != null) {
						if (isPlaying && mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_IDLE) {
							onCompletion();
						}
						isPlaying = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
						isPausing = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED;
						Log.e(__CLASSNAME__, getMethodName() + mediaStatus.getPlayerState() + "-" + isPlaying + ":" + isPausing);
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});

		mRemoteMediaPlayer.setOnMetadataUpdatedListener(new RemoteMediaPlayer.OnMetadataUpdatedListener() {
			@Override
			public void onMetadataUpdated() {
				try {
					// _Log.e(__CLASSNAME__, getMethodName() + mRemoteMediaPlayer.getMediaInfo());
					// MediaInfo mediaInfo = mRemoteMediaPlayer.getMediaInfo();
					// if (mediaInfo != null) {
					// MediaMetadata metaData = mediaInfo.getMetadata();
					// _Log.e(__CLASSNAME__, getMethodName() + metaData);
					// }
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
	}

	public void onCreateOptionsMenu(MenuItem mediaRouteMenuItem) {

		MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
		mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
	}

	public void onStart() {
		Log.e(__CLASSNAME__, getMethodName());
		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	public void addCallback(MediaRouter.Callback mediaRouterCallback) {
		mMediaRouter.addCallback(mMediaRouteSelector, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	public void onResume() {
		Log.e(__CLASSNAME__, getMethodName());
	}

	/**
	 * Removes the activity from memory when the activity is paused.
	 */
	public void onPause() {
		Log.e(__CLASSNAME__, getMethodName());
	}

	/**
	 * Attempts to end the current game session when the activity stops.
	 */
	@Override
	public void onStop() {
		Log.e(__CLASSNAME__, getMethodName());
		// setSelectedDevice(null);
		// mMediaRouter.removeCallback(mMediaRouterCallback);
		setSelectedDevice(null);
		mMediaRouter.removeCallback(mMediaRouterCallback);
		super.onStop();
	}

	private void setSelectedDevice(CastDevice device) {
		Log.d(__CLASSNAME__, "setSelectedDevice: " + device);
		mSelectedDevice = device;

		if (mSelectedDevice != null) {
			try {
				disconnectApiClient();
				connectApiClient();
			} catch (IllegalStateException e) {
				Log.w(__CLASSNAME__, "Exception while connecting API client", e);
				disconnectApiClient();
			}
		} else {
			if (mApiClient != null) {
				if (mApiClient.isConnected()) {
					/*
					 * if (mRemoteMediaPlayer != null && isPlaying) {
					 * try {
					 * mRemoteMediaPlayer.pause(mApiClient);
					 * } catch (IOException e) {
					 * e.printStackTrace();
					 * }
					 * 
					 * }
					 */
				}
				disconnectApiClient();
			}
			mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
		}
	}

	private void connectApiClient() {
		Cast.CastOptions apiOptions = Cast.CastOptions.builder(mSelectedDevice, mCastListener).build();
		mApiClient = new GoogleApiClient.Builder(this.context).addApi(Cast.API, apiOptions).addConnectionCallbacks(mConnectionCallbacks)
				.addOnConnectionFailedListener(mConnectionFailedListener).build();
		mApiClient.connect();
		Log.e("Connection checking", mApiClient.isConnected() + "Status");
	}

	private void disconnectApiClient() {
		if (mApiClient != null && mApiClient.isConnected()) {
			/*
			 * if (mRemoteMediaPlayer != null && isPlaying) {
			 * try {
			 * mRemoteMediaPlayer.stop(mApiClient);
			 * } catch (IOException e) {
			 * e.printStackTrace();
			 * }
			 * }
			 */
			mApiClient.disconnect();
			mApiClient = null;
		}
	}

	public boolean isConnected() {
		boolean ret = mApiClient != null && mApiClient.isConnected() && mRemoteMediaPlayer != null;
		return ret;
	}

	/**
	 * Called when a user selects a route.
	 */
	private void onRouteSelected(RouteInfo route) {
		Log.e(__CLASSNAME__, "onRouteSelected: " + route.getName());

		CastDevice device = CastDevice.getFromBundle(route.getExtras());
		setSelectedDevice(device);
	}

	/**
	 * Called when a user unselects a route.
	 */
	private void onRouteUnselected(RouteInfo route) {
		Log.e(__CLASSNAME__, "onRouteUnselected: " + route.getName());
		setSelectedDevice(null);
	}

	/**
	 * An extension of the MediaRoute.Callback specifically for the TicTacToe
	 * game.
	 */
	private class MediaRouterCallback extends MediaRouter.Callback {
		@Override
		public void onRouteSelected(MediaRouter router, RouteInfo route) {
			Log.d(__CLASSNAME__, "onRouteSelected: " + route);
			SongPlayCastChrome.this.onRouteSelected(route);
		}

		@Override
		public void onRouteUnselected(MediaRouter router, RouteInfo route) {
			Log.d(__CLASSNAME__, "onRouteUnselected: " + route);
			SongPlayCastChrome.this.onRouteUnselected(route);
		}
	}

	private class CastListener extends Cast.Listener {
		@Override
		public void onApplicationDisconnected(int statusCode) {
			Log.e(__CLASSNAME__, "Cast.Listener.onApplicationDisconnected: " + statusCode);
			try {
				Cast.CastApi.removeMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace());
			} catch (IOException e) {
				Log.e(__CLASSNAME__, "Exception while launching application", e);
			}
			stop();
		}
	}

	public class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
		@Override
		public void onConnectionSuspended(int cause) {
			Log.d(__CLASSNAME__, "ConnectionCallbacks.onConnectionSuspended");
			stop();
		}

		@Override
		public void onConnected(Bundle connectionHint) {
			Log.d(__CLASSNAME__, "ConnectionCallbacks.onConnected");
			Cast.CastApi.launchApplication(mApiClient, app_id).setResultCallback(new ConnectionResultCallback());
		}
	}

	public class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
		@Override
		public void onConnectionFailed(ConnectionResult result) {
			Log.d(__CLASSNAME__, "ConnectionFailedListener.onConnectionFailed");
			setSelectedDevice(null);
			stop();
		}
	}

	private final class ConnectionResultCallback implements ResultCallback<ApplicationConnectionResult> {
		@Override
		public void onResult(ApplicationConnectionResult result) {
			Status status = result.getStatus();
			ApplicationMetadata appMetaData = result.getApplicationMetadata();

			if (status.isSuccess()) {
				Log.e(__CLASSNAME__, "ConnectionResultCallback: " + appMetaData.getName());
				try {
					Cast.CastApi.setMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer);
				} catch (IOException e) {
					Log.w(__CLASSNAME__, "Exception while launching application", e);
				}
			} else {
				Log.e(__CLASSNAME__, "ConnectionResultCallback. Unable to launch the game. statusCode: " + status.getStatusCode());
			}
		}
	}

	@Override
	public void start() {


	}

	@Override
	public void prepare() {


	}

	@Override
	public boolean load(String path) {
		Log.e(__CLASSNAME__, getMethodName() + path);


		setPath(path);

		reset();

		// (new open()).execute(path);
		open();

		return true;
	}

	class open extends PriorityAsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			Log.e(SongPlayCastChrome.this.toString(), getMethodName() + getPath());

			if (!TextUtils.isEmpty(getPath())) {
				open();
			}
			return null;
		}

	}

	// private final int tryout = 0;

	private boolean open() {
		String path = getPath();
		Log.e(__CLASSNAME__, getMethodName() + path);

		MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

		try {
			if (tag != null) {
				Log.e(__CLASSNAME__, getMethodName() + "[MP3Info]");
				Log.i("[MP3Info]ARTIST:", tag.getFirst(FieldKey.ARTIST));
				Log.i("[MP3Info]ALBUM:", tag.getFirst(FieldKey.ALBUM));
				Log.i("[MP3Info]TITLE:", tag.getFirst(FieldKey.TITLE));
				Log.i("[MP3Info]COMMENT:", tag.getFirst(FieldKey.COMMENT));
				Log.i("[MP3Info]YEAR:", tag.getFirst(FieldKey.YEAR));
				Log.i("[MP3Info]TRACK:", tag.getFirst(FieldKey.TRACK));
				Log.i("[MP3Info]DISC_NO:", tag.getFirst(FieldKey.DISC_NO));
				Log.i("[MP3Info]COMPOSER:", tag.getFirst(FieldKey.COMPOSER));
				Log.i("[MP3Info]ARTIST_SORT:", tag.getFirst(FieldKey.ARTIST_SORT));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.ARTIST)))
					mediaMetadata.putString(MediaMetadata.KEY_ARTIST, tag.getFirst(FieldKey.ARTIST));
				if (TextUtil.isEmpty(tag.getFirst(FieldKey.ARTIST)) && !TextUtil.isEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST)))
					mediaMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.ALBUM)))
					mediaMetadata.putString(MediaMetadata.KEY_ALBUM_TITLE, tag.getFirst(FieldKey.ALBUM));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.TITLE)))
					mediaMetadata.putString(MediaMetadata.KEY_TITLE, tag.getFirst(FieldKey.TITLE));
				// if (!TextUtil.isEmpty(tag.getFirst(FieldKey.COMMENT)))
				// _Log.i("[MP3Info]", "COMMENT:" + tag.getFirst(FieldKey.COMMENT));
				// if (!TextUtil.isEmpty(tag.getFirst(FieldKey.YEAR)))
				// mediaMetadata.putString(MediaMetadata.KEY_BROADCAST_DATE, tag.getFirst(FieldKey.YEAR));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.TRACK)) && TextUtil.isNumeric(tag.getFirst(FieldKey.TRACK)))
					mediaMetadata.putString(MediaMetadata.KEY_TRACK_NUMBER, tag.getFirst(FieldKey.TRACK));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.DISC_NO)) && TextUtil.isNumeric(tag.getFirst(FieldKey.DISC_NO)))
					mediaMetadata.putString(MediaMetadata.KEY_DISC_NUMBER, tag.getFirst(FieldKey.DISC_NO));
				if (!TextUtil.isEmpty(tag.getFirst(FieldKey.COMPOSER)))
					mediaMetadata.putString(MediaMetadata.KEY_COMPOSER, tag.getFirst(FieldKey.COMPOSER));
				// if (!TextUtil.isEmpty(tag.getFirst(FieldKey.ARTIST_SORT)))
				// _Log.i("[MP3Info]", "ARTIST_SORT:" + tag.getFirst(FieldKey.ARTIST_SORT));
			} else {
				String title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
				mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			MediaInfo mediaInfo = new MediaInfo.Builder(getUrl()).setContentType("audio/*").setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setMetadata(mediaMetadata).build();

			mRemoteMediaPlayer.load(mApiClient, mediaInfo, false).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {

				@Override
				public void onResult(MediaChannelResult result) {
					Log.e(__CLASSNAME__, getMethodName() + result);

					if (result.getStatus().isSuccess()) {
						onPrepared();
					}
				}
			});
		} catch (Exception e) {

			e.printStackTrace();
			return true;
		}
		return true;
	}

	long tick;

	@Override
	protected void startOnTime() {
		Log.e(__CLASSNAME__, getMethodName());

		tick = System.currentTimeMillis() / MSEC2SEC;
		super.startOnTime();
	}

	@Override
	protected void stopOnTime() {
		Log.e(__CLASSNAME__, getMethodName());

		super.stopOnTime();
	}

	@Override
	protected void onTime() {

		if (isPlaying()) {
			long tack = (System.currentTimeMillis() / MSEC2SEC) - tick;
			if (tack > 0) {
				tick = System.currentTimeMillis() / MSEC2SEC;
				mRemoteMediaPlayer.requestStatus(mApiClient);
			}
		}
		super.onTime();
	}

	@Override
	public boolean play() {

		if (mRemoteMediaPlayer != null) {
			try {
				mRemoteMediaPlayer.play(mApiClient);
				startOnTime();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public void stop() {

		if (mRemoteMediaPlayer != null && isPlaying) {
			try {
				stopOnTime();
				reset();
				mRemoteMediaPlayer.stop(mApiClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void seek(int msec) {

		if (mRemoteMediaPlayer != null && isPlaying) {
			try {
				mRemoteMediaPlayer.seek(mApiClient, msec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void pause() {

		if (mRemoteMediaPlayer != null) {
			try {
				mRemoteMediaPlayer.pause(mApiClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getTotalTime() {

		// return super.getTotalTime();
		int ret = 0;
		if (mRemoteMediaPlayer != null) {
			ret = (int) mRemoteMediaPlayer.getStreamDuration();
		}
		return ret;
	}

	@Override
	public int getCurrentTime() {

		// return super.getCurrentTime();
		int ret = 0;
		if (mRemoteMediaPlayer != null) {
			MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
			if (mediaStatus != null) {
				ret = (int) mediaStatus.getStreamPosition();
			}
		}
		return ret;
	}

	@Override
	public void restart() {
		Log.e(__CLASSNAME__, getMethodName());

		super.restart();
	}

	@Override
	public void repeat() {
		Log.e(__CLASSNAME__, getMethodName());

		super.repeat();
	}

}
