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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	PlayFragment.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * com.example.soundtouch_test
 *    |_ PlayFragment.java
 * </pre>
 */

package kr.kymedia.karaoke.play.apps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import kr.kymedia.karaoke.play.app.ChoirPlayFragment;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.PlayView;
import kr.kymedia.karaoke.play.app.view.PlayViewValance;
import kr.kymedia.karaoke.play.app.view.SongPlayView;
import kr.kymedia.karaoke.play.app.view._PlayView;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.util.TextUtil;
import kr.kymedia.karaoke.view.EnabledRadioGroup;
import kr.kymedia.karaoke.widget.KaraokePath;

/**
 *
 * TODO<br>
 *
 * <pre>
 * SongPlay 재생UI플래그먼트
 * 딴거썪지않는다~~~
 * </pre>
 *
 * @author isyoon
 * @since 2014. 3. 14.
 * @version 1.0
 */
public class PlayFragment extends ChoirPlayFragment {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()) + ":" + song;
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	Context getApplicationContext() {
		return getActivity().getApplicationContext();
	}

	PowerManager mPowerManager;
	WakeLock mWakeLock;

	@Override
	public View findViewById(int id) {
		if (getActivity() != null) {
			return getActivity().findViewById(id);
		} else {
			return null;
		}
	}

	private _PlayView mPlayView;

	protected _PlayView getPlayView() {
		return mPlayView;
	}

	private void setPlayView() {
		Log.e(__CLASSNAME__, getMethodName());

		setSong(null);

		mPlayView = (_PlayView) findViewById(R.id.include_play_song);
		mPlayView.setHandler(handler);
		mPlayView.setOnListener(this);
		mPlayView.setPath(getPath());
		mPlayView.setChoir(false);
		mPlayView.start();

		((ImageButton) findViewById(R.id.buttonError)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				findViewById(R.id.merge_play_error).setVisibility(View.GONE);
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		release();
	}

	@Override
	public void release() {
		Log.d(__CLASSNAME__, getMethodName());
		super.release();


		if (mWakeLock == null) {
			mWakeLock.release();
		}

		handler = null;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Log.i(__CLASSNAME__, getMethodName());

		super.onCreate(savedInstanceState);
	}

	ViewGroup mRootView;

	public ViewGroup getRootView() {
		return mRootView;
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	// @Nullable Bundle savedInstanceState) {
	//
	// // return super.onCreateView(inflater, container, savedInstanceState);
	// return mRootView;
	// }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.e(__CLASSNAME__, getMethodName() + "[ST]" + savedInstanceState);
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_play, container, false);
		Log.e(__CLASSNAME__, getMethodName() + "[ED]" + savedInstanceState);
		// return super.onCreateView(inflater, container, savedInstanceState);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(__CLASSNAME__, getMethodName() + savedInstanceState);

		super.onActivityCreated(savedInstanceState);

		onActivityCreated();
	}

	@Override
	protected void onActivityCreated() {
		Log.i(__CLASSNAME__, getMethodName());


		super.onActivityCreated();

		// ((EditText) findViewById(R.id.editSong)).setText("61666");
		// ((EditText) findViewById(R.id.editSong)).setText("60770");
		((EditText) findViewById(R.id.editSong)).setText("61751");

		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mPowerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");

		start();
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.i(__CLASSNAME__, getMethodName());

		super.onDestroy();

		stop();
		release();
	}

	private void openIntent(final Intent intent) {

		if (intent == null) {
			Log.e(__CLASSNAME__, getMethodName() + "[NG]" + intent);
			return;
		}

		Bundle extras = intent.getExtras();

		String path = "";

		if (extras == null) {
			Log.e(__CLASSNAME__, getMethodName() + "[NG]" + extras);
			return;
		}

		if (extras.getParcelable("pathPlay") != null) {
			Uri uri = extras.getParcelable("pathPlay");
			// Log.e(__CLASSNAME__, getMethodName() + uri);
			path = uri.toString();
			// URL확인후아니면로컬경로확인!!!지랄~~~
			if (!TextUtil.isNetworkUrl(path)) {
				path = uri.getPath();
			}
		} else {
			// path = openKaraoke(extras);
			openKaraoke(extras);
		}

		Log.e(__CLASSNAME__, getMethodName() + path + "\n" + extras);

		if (!TextUtil.isEmpty(path)) {
			if (isPlaying() && !path.equalsIgnoreCase(getPath())) {
				stop(true);
			}
		}

		if (TextUtil.isEmpty(path)) {
			// path = IKaraoke.SDCARD_ROOT + File.separator + ".PIANO" + File.separator + "Piano_060_C5.ogg";
			path = "file:///android_asset" + File.separator + "piano" + File.separator + "Piano_060_C5.ogg";
		}

		setPath(path);
	}

	protected void onNewIntent(Intent intent) {
		Log.e(__CLASSNAME__, getMethodName() + intent.getExtras());


		openIntent(intent);
	}

	@Override
	public void start() {
		Log.e(__CLASSNAME__, getMethodName());

		super.start();

		setPlayView();

		setPlayControl();

		progress(true);

		setPlayTime();

		openIntent(getActivity().getIntent());
	}

	private void setPlayControl() {
		Log.e(__CLASSNAME__, getMethodName());


		((RadioGroup) findViewById(R.id.radioServer))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						int server = checkedId;
						int number = Integer.parseInt("" + ((EditText) findViewById(R.id.editSong)).getText());

						if (!isPlaying()) {
							setPath(getKaraoke(server, number));
						}
					}
				});

		((ImageButton) findViewById(R.id.buttonStream)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initStartTime();
				stop();

				postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							int server = ((RadioGroup) findViewById(R.id.radioServer)).getCheckedRadioButtonId();
							int number = Integer.parseInt("" + ((EditText) findViewById(R.id.editSong)).getText());

							String path = getKaraoke(server, number);
							setPath(path);
							load(path);
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
				}, 300);
			}
		});

		((ImageButton) findViewById(R.id.buttonPlay)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.i(__CLASSNAME__, getMethodName() + isPlaying() + " - " + isPause());

				if (isPlaying()) {
					if (isPause()) {
						resume();
					} else {
						pause();
					}
				} else {
					try {
						// String fileName = ((TextView) findViewById(R.id.editPath)).getText().toString();
						initStartTime();
						if (!TextUtils.isEmpty(getPath())) {
							stop();
							load(getPath());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonStop)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stop(true);
			}
		});

	}

	private void resume() {
		if (isPause()) {
			play();
            //seek(getCurrentTime());
		}
	}

	public void stop(boolean progress) {
		stop();
		setStop(true);
		setEndTime();
	}

	public void setTextViewMarquee(final TextView tv, boolean enable) {
		if (tv == null) {
			return;
		}
		// set the ellipsize mode to MARQUEE and make it scroll only once
		// tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// tv.setMarqueeRepeatLimit(1);
		// in order to start strolling, it has to be focusable and focused
		// tv.setFocusable(true);
		// tv.setFocusableInTouchMode(true);
		// tv.requestFocus();
		if (!(tv instanceof EditText)) {
			if (enable) {
				tv.setEllipsize(TruncateAt.MARQUEE);
			} else {
				tv.setEllipsize(null);
			}
			tv.setSingleLine(true);
			tv.setSelected(enable);
		}
	}

	/**
	 * <pre>
	 * mp3 : http://resource.kymedia.kr/ky/mp/01/00101.mp3
	 * 가사 : http://resource.kymedia.kr/ky/md/01/00101.mid
	 * skym : http://resource.kymedia.kr/ky/skym/01/00101.skym
	 * url_skym: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
	 * url_lyric: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=8888
	 *
	 * 211.236.190.103
	 * 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
	 * KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
	 * 사이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
	 * </pre>
	 */
	private String openKaraoke(Bundle extras) {
		Log.d(__CLASSNAME__, getMethodName() + extras);

		String ret = null;
		try {
			// 갖고와서
			// boolean check = extras.getBoolean("checkKYmedia");
			String song = extras.getString("editSong");
			int server = extras.getInt("radioServer");

			// 박아넣고
			// ((CheckBox) findViewById(R.id.checkKYmedia)).setChecked(check);
			((EditText) findViewById(R.id.editSong)).setText(song);
			((RadioGroup) findViewById(R.id.radioServer)).check(server);

			// 가져온다.쿨!!!
			int number = Integer.parseInt(((EditText) findViewById(R.id.editSong)).getText().toString());
			ret = getKaraoke(server, number);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public String getKaraoke(int server, int number) {
		String ret;

		// 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
		String host = "http://211.236.190.103:8080/svc_media/";
		String path = "mmp3";
		String form = "/%05d.mp3";

		if (server == R.id.radioServerKYS) {
			// 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
			//host = "http://" + KaraokePath.getHosts()[0] + ":8080/svc_media/";
			host = "http://211.236.190.103:8080/svc_media/";
			path = "mmp3";
			form = "/%05d.mp3";
		} else if (server == R.id.radioServerKYM) {
			// KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
			//host = "http://" + KaraokePath.getHosts()[1] + "/ky/mp/";
			host = "http://resource.kymedia.kr/ky/mp/";
			path = String.format(getResources().getConfiguration().locale, "%05d", number).substring(3);
			form = "/%05d.mp3";
		} else if (server == R.id.radioServerCYW) {
			// 싸이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
			//host = "http://" + KaraokePath.getHosts()[2] + "/";
			host = "http://cyms.chorus.co.kr/";
			path = "cykara_dl2.asp?song_id=";
			form = "%05d";
		} else if (server == R.id.radioServerKYG) {
			 //* 신규서버(음원): http://www.keumyoung.kr/.api/.mmp3.asp?song_id=08888
			//host = "http://" + KaraokePath.getHosts()[3] + "/";
			host = "http://www.keumyoung.kr/.api/";
			path = ".mmp3.asp?song_id=";
			form = "%05d";
			//host = "http://www.keumyoung.kr/.api/";	//test
			//path = "";	//test
			//form = "%05d.mp3";	//test
		}

		ret = String.format(host + path + form, number);

		Log.e(__CLASSNAME__, getMethodName() + number + "-" + ret);

		return ret;
	}

	@Override
	public void setPath(String path) {
		// Log.e(__CLASSNAME__, getMethodName() + path);

		super.setPath(path);

		if (mPlayView != null) {
			mPlayView.setPath(path);
		}
	}

	@Override
	public boolean load(String path) throws Exception {
		Log.d(__CLASSNAME__, getMethodName());

		// mPlayView.open(path);
		// setPath(path);
		// executeAsyncTask(new open(), mPlayView);

		return super.load(path);
	}

	@Override
	public void prepare() {
		Log.d(__CLASSNAME__, getMethodName());

		if (mWakeLock == null) {
			mWakeLock.acquire();
		}

		progress(false);

	}

	@Override
	public void onPrepared() {
		Log.w(__CLASSNAME__, getMethodName());


		super.onPrepared();

		prepare();

		play();
	}

	@Override
	public boolean play() {
		Log.d(__CLASSNAME__, getMethodName());

		// mPlayView.play();
		// executeAsyncTask(new play(), mPlayView);

		return super.play();
	}

	@Override
	public void seek(int msec) {

		// mPlayView.seek(msec);
		this.seek = msec;
		// executeAsyncTask(new seek(), mPlayView);

		super.seek(msec);
	}

	@Override
	public void stop() {

		// mPlayView.stop();
		// executeAsyncTask(new stop(), mPlayView);
		super.stop();

		if (mWakeLock == null) {
			mWakeLock.release();
		}
	}

	@Override
	public void pause() {
		if (!isPlaying()) {
			return;
		}


		// mPlayView.pause();
		// executeAsyncTask(new pause(), mPlayView);

		super.pause();
	}

	@Override
	public void restart() {
		Log.d(__CLASSNAME__, getMethodName());

		// mPlayView.restart();
		// executeAsyncTask(new restart(), mPlayView);

		super.restart();
	}

	@Override
	public void repeat() {
		Log.d(__CLASSNAME__, getMethodName());

		// mPlayView.repeat();
		// executeAsyncTask(new repeat(), mPlayView);

		super.repeat();
	}

	@Override
	public boolean isPlaying() {

		if (mPlayView != null) {
			return mPlayView.isPlaying();
		} else {
			return super.isPlaying();
		}
	}

	@Override
	public boolean isPause() {

		if (mPlayView != null) {
			return mPlayView.isPause();
		} else {
			return super.isPause();
		}
	}

	boolean isDestroyed() {
		if (getActivity() != null) {
			return isDetached() || getActivity().isDestroyed();
		} else {
			return true;
		}
	}

	protected void setStop(boolean progress) {

		if (mPlayView != null) {
			mPlayView.setStop(progress);
		}
		for (SongPlayView choir : getChoirs()) {
			((PlayView) choir).setStop(progress);
		}
	}

	protected void initStartTime() {
		if (mPlayView != null) {
			mPlayView.initStartTime();
		}
		for (SongPlayView choir : getChoirs()) {
			((PlayView) choir).initStartTime();
		}
	}

	protected void setEndTime() {
		if (mPlayView != null) {
			mPlayView.setEndTime();
		}
		for (SongPlayView choir : getChoirs()) {
			((PlayView) choir).setEndTime();
		}
	}

	protected long getTicky() {
		if (mPlayView != null) {
			return mPlayView.getTicky();
		} else {
			return 0;
		}
	}

	protected int getID() {
		if (mPlayView != null) {
			return mPlayView.getID();
		} else {
			return getID();
		}
	}

	@Override
	public int getCurrentTime() {

		if (mPlayView != null) {
			return mPlayView.getCurrentTime();
		} else {
			return super.getCurrentTime();
		}
	}

	@Override
	public void onError() {
		Log.w(__CLASSNAME__, getMethodName());

		super.onError();
	}

	@Override
	public void onError(final ERROR t, final Exception e) {

		Log.w(__CLASSNAME__, getMethodName() + t);

		super.onError(t, e);

		try {
			int count = mPlayView.getCount();
			String time = android.text.format.DateFormat.format("yyyy/MM/dd HH:mm:ss",
					System.currentTimeMillis()).toString();
			String err = "NG(" + count + ") - " + time + "\n" + t + "\n" + Log.getStackTraceString(e);
			((TextView) findViewById(R.id.textError)).setText(err);
			findViewById(R.id.merge_play_error).setVisibility(View.VISIBLE);
		} catch (Exception e1) {

			e1.printStackTrace();
		}

	}

	@Override
	public void onTimeout(long timeout) {
		Log.w(__CLASSNAME__, getMethodName() + timeout);

		Toast.makeText(getActivity().getApplicationContext(),
				"ERROR:" + ERROR.TIMEOUT + "(" + PlayView.TIME_TIMEOUT_OPEN + ")",
				Toast.LENGTH_SHORT).show();
		super.onTimeout(timeout);
	}

	@Override
	public void onCompletion() {

		boolean repeat = ((CheckBox) findViewById(R.id.checkRepeatRestart)).isChecked();
		Log.w(__CLASSNAME__, getMethodName() + repeat);

		super.onCompletion();

		if (repeat) {
			int radio = ((RadioGroup) findViewById(R.id.radioRepeatRestart)).getCheckedRadioButtonId();

			if (radio == R.id.radioRepeat) {
				repeat();
			} else if (radio == R.id.radioRestart) {
				restart();
			}
		} else {
			stop();
		}
	}

	SeekBar seekPlay;

	void progress(boolean init) {
		if (isDestroyed()) {
			return;
		}
		seekPlay = (SeekBar) findViewById(R.id.seekPlay);
		seekPlay.setEnabled(false);

		seekPlay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				PlayView.tracking = false;
				seek(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				PlayView.tracking = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// Log.d(__CLASSNAME__, getMethodName() + progress + "/" + seekBar.getMax());
				if (fromUser) {
				}
				setPlayTime();
			}
		});
	}

	private void setPlayTime() {
		if (seekPlay == null) {
			seekPlay = (SeekBar) findViewById(R.id.seekPlay);
		}
		long tak = System.currentTimeMillis() - getTicky();
		int cur = seekPlay.getProgress();
		int tot = seekPlay.getMax();
		String min, sec, msec;

		min = String.format(Locale.getDefault(), "%02d", cur / 60000);
		sec = String.format(Locale.getDefault(), "%02d", (cur % 60000) / 1000);
		msec = String.format(Locale.getDefault(), "%02d", (cur % 1000) / 10);
		String pos = min + ":" + sec + "." + msec;

		min = String.format(Locale.getDefault(), "%02d", tot / 60000);
		sec = String.format(Locale.getDefault(), "%02d", (tot % 60000) / 1000);
		msec = String.format(Locale.getDefault(), "%02d", (tot % 1000) / 10);
		String dur = min + ":" + sec + "." + msec;

		min = String.format(Locale.getDefault(), "%02d", tak / 60000);
		sec = String.format(Locale.getDefault(), "%02d", (tak % 60000) / 1000);
		msec = String.format(Locale.getDefault(), "%02d", (tak % 1000) / 10);
		String tik = min + ":" + sec + "." + msec;

		String play = pos + "(" + tik + ")" + "/" + dur;
		play += "-" + getID();
		if (findViewById(R.id.textPlay) != null) {
			((TextView) findViewById(R.id.textPlay)).setText(play);
		}

		min = String.format(Locale.getDefault(), "%02d", cur / 60000);
		sec = String.format(Locale.getDefault(), "%02d", (cur % 60000) / 1000);
		msec = String.format(Locale.getDefault(), "%02d", (cur % 1000) / 10);
		pos = min + ":" + sec;
		if (findViewById(R.id.textCurTime) != null) {
			((TextView) findViewById(R.id.textCurTime)).setText(pos);
		}

		min = String.format(Locale.getDefault(), "%02d", tot / 60000);
		sec = String.format(Locale.getDefault(), "%02d", (tot % 60000) / 1000);
		msec = String.format(Locale.getDefault(), "%02d", (tot % 1000) / 10);
		dur = min + ":" + sec;
		if (findViewById(R.id.textTotTime) != null) {
			((TextView) findViewById(R.id.textTotTime)).setText(dur);
		}
	}

	protected void setEnabled(final boolean enabled) {
		// Log.d(__CLASSNAME__, getMethodName() + enabled + ":" + isPlaying());

		boolean enable = enabled;

		if (isPlaying()) {
			enable = false;
		}

		findViewById(R.id.merge_stream_control).setEnabled(enable);
		((EnabledRadioGroup) findViewById(R.id.radioServer)).setEnabled(enable);
		findViewById(R.id.buttonAdd).setEnabled(true);
	}

	public void mute(boolean force, boolean enabled) {

		for (SongPlayView choir : getChoirs()) {
			if (force) {
				((PlayViewValance) choir).mute(enabled);
			} else {
				((PlayViewValance) choir).mute(((PlayViewValance) choir).isMute());
			}
		}
	}
}
