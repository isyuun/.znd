/*
 * Copyright 201als1 The Android Open Source Project
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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	PlayView.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayView.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import java.io.File;
import java.util.Locale;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.apps._PlayFragment;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.widget.KaraokeTextEdit;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 7. 18.
 * @version 1.0
 */
public class PlayView extends SongPlayView {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		// return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()) + ":" + song;
		return (new Exception()).getStackTrace()[0].getFileName() + '@' + Integer.toHexString(hashCode()) + ":" + song;
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);

		return name + "() ";
	}

	public PlayView(Context context) {
		super(context);
		_Log.i(__CLASSNAME__, getMethodName());
	}

	public PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow() {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onAttachedToWindow();

		if (!isInEditMode()) {
			start();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onDetachedFromWindow();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// _Log.i(__CLASSNAME__, getMethodName());

		super.onLayout(changed, l, t, r, b);

	}

	protected void init(boolean init) {
		_Log.w(__CLASSNAME__, getMethodName() + init);

	}

	@Override
	public void start() {
		_Log.d(__CLASSNAME__, getMethodName() + handler);

		super.start();

		initStartTime();

		setStartTime();
		// setPlayTime();

		setPlayView();

		// setPlayControl();

		progress(true);

		initStartTime();

		setPlayTime();

		init(true);
	}

	void setPlayView() {
		((ImageButton) findViewById(R.id.buttonShow)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showSongControl(true);
			}

		});

		((ImageButton) findViewById(R.id.buttonHide)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showSongControl(false);
			}

		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			((KaraokeTextEdit) findViewById(R.id.editPath)).setTextIsSelectable(true);
		}

		((KaraokeTextEdit) findViewById(R.id.editPath)).setEditable(false);

		((ImageButton) findViewById(R.id.buttonError)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				findViewById(R.id.merge_play_error).setVisibility(View.GONE);
			}
		});
	}

	void setPlayControl() {
	}

	// private boolean visible = true;
	// private void showSongControl() {
	// showSongControl(visible = !visible);
	// }
	public void showSongControl(boolean visible) {
		// this.visible = visible;
		if (visible) {
			findViewById(R.id.buttonShow).setVisibility(View.INVISIBLE);
			findViewById(R.id.buttonHide).setVisibility(View.VISIBLE);
			findViewById(R.id.merge_pitch_control).setVisibility(View.VISIBLE);
			findViewById(R.id.merge_tempo_control).setVisibility(View.VISIBLE);
			findViewById(R.id.merge_valance_control).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.buttonShow).setVisibility(View.VISIBLE);
			findViewById(R.id.buttonHide).setVisibility(View.INVISIBLE);
			findViewById(R.id.merge_pitch_control).setVisibility(View.GONE);
			findViewById(R.id.merge_tempo_control).setVisibility(View.GONE);
			findViewById(R.id.merge_valance_control).setVisibility(View.GONE);
		}

		// 좌우볼륨슬라이더가려
		findViewById(R.id.merge_valance_control).setVisibility(View.GONE);

		findViewById(R.id.textInfo).setVisibility(View.VISIBLE);

	}

	protected void setInfo() {
		String info = "";

		ViewGroup p = (ViewGroup) findViewById(R.id.merge_song_control);
		for (int i = 0; i < p.getChildCount(); i++) {
			View c = p.getChildAt(i);
			// _Log.i(__CLASSNAME__, getMethodName() + c);

			if (c instanceof ViewGroup) {
				String text = "";

				if (c.getId() == R.id.merge_tempo_control) {
					text = ((TextView) findViewById(R.id.txt_tempo)).getText().toString().replace("TEMPO:", "T:");
				} else if (c.getId() == R.id.merge_pitch_control) {
					text = ((TextView) findViewById(R.id.txt_pitch)).getText().toString().replace("PITCH:", "P:");
				} else if (c.getId() == R.id.merge_valance_control) {
					text = ((TextView) findViewById(R.id.textValance)).getText().toString();
				}

				if (!text.isEmpty() && !info.isEmpty()) {
					text = " - " + text;
				}

				info += text;
			}
		}

		((TextView) findViewById(R.id.textInfo)).setText(info);
	}

	private long start;

	public long getStart() {
		return start;
	}

	private long ticky;

	public long getTicky() {
		return ticky;
	}

	public void initStartTime() {
		count = 0;
		start = System.currentTimeMillis();
	}

	private void setStartTime() {
		String time = android.text.format.DateFormat.format("yyyy/MM/dd HH:mm:ss", start).toString();

		((TextView) findViewById(R.id.textStart)).setText("ST(" + count + ") - " + time);
		ticky = System.currentTimeMillis();
	}

	public void setEndTime() {
		String time = android.text.format.DateFormat.format("yyyy/MM/dd HH:mm:ss", System.currentTimeMillis()).toString();
		((TextView) findViewById(R.id.textStart)).setText("ED(" + count + ") - " + time);
		// ((TextView) findViewById(R.id.textError)).setText("");
	}

	private void setErrorTime(ERROR t, Exception e) {
		// _Log.i(this.toString(), getMethodName() + "\n" + + t + "\n" + _Log.getStackTraceString(e));
		// try {
		//
		// String time = android.text.format.DateFormat.format("yyyy/MM/dd HH:mm:ss",
		// System.currentTimeMillis()).toString();
		// String err = "NG(" + count + ") - " + time + "\n" + t + "\n" + _Log.getStackTraceString(e);
		// ((TextView) findViewById(R.id.textError)).setText(err);
		// findViewById(R.id.merge_play_error).setVisibility(View.VISIBLE);
		// } catch (Exception e1) {
		//
		// e1.printStackTrace();
		// }
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (!ViewCompat.isAttachedToWindow(this)) {
			return;
		}

		findViewById(R.id.buttonStream).setEnabled(enabled);
		findViewById(R.id.buttonPlay).setEnabled(enabled);
		// findViewById(R.id.buttonStop).setEnabled(enabled);
		findViewById(R.id.seekPlay).setEnabled(enabled);
	}

	int count = 0;

	public int getCount() {
		return count;
	}

	@Override
	public void setPath(String path) {
		_Log.e(__CLASSNAME__, getMethodName() + path);


		super.setPath(path);

		((KaraokeTextEdit) findViewById(R.id.editPath)).setText(path);
		((KaraokeTextEdit) findViewById(R.id.editPath)).setEditable(false);

		try {
			if (path != null) {
				String name = path.substring(path.lastIndexOf(File.separator));
				if ((path.lastIndexOf(File.separator) + 1) < path.length() - 1) {
					name = path.substring(path.lastIndexOf(File.separator) + 1);
				}
				((TextView) findViewById(R.id.textName)).setText(name);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public boolean load(final String path) throws Exception {

		_Log.d(__CLASSNAME__, getMethodName() + "count:" + count);

		post(new Runnable() {

			@Override
			public void run() {
				// _Log.e(__CLASSNAME__, "open() post() " + getMethodName() + PlayView.this.getSong());


				try {
					setEnabled(false);
					setPath(path);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});

		boolean ret = super.load(path);

		if (ret) {
			if (path.equalsIgnoreCase(getPath())) {
				count++;
			} else {
				count = 0;
			}
		}

		return ret;
	}

	@Override
	public void prepare() {
		_Log.d(__CLASSNAME__, getMethodName() + "count:" + count);

		super.prepare();

		post(new Runnable() {

			@Override
			public void run() {

				setStartTime();
				// setPlayTime();

				// setEnabled(true);

				progress(false);

			}
		});

		init(false);
	}

	@Override
	public boolean play() {
		// _Log.e(__CLASSNAME__, getMethodName() + "count:" + count + " - " + path);
		super.play();

		setPlay();

		return true;
	}

	public void setPlay() {
		post(new Runnable() {

			@Override
			public void run() {

				// ((ImageButton) findViewById(R.id.buttonPlay)).setText("||");
				((ImageButton) findViewById(R.id.buttonPlay)).setImageResource(R.drawable.ic_action_pause);
				setEnabled(true);
			}
		});

	}

	@Override
	public void pause() {

		super.pause();

		setPause();
	}

	public void setPause() {
		post(new Runnable() {

			@Override
			public void run() {

				// ((ImageButton) findViewById(R.id.buttonPlay)).setText("▶");
				((ImageButton) findViewById(R.id.buttonPlay)).setImageResource(R.drawable.ic_action_play);
			}
		});
	}

	@Override
	public void stop() {
		_Log.d(__CLASSNAME__, getMethodName() + "count:" + count);
		super.stop();
		post(new Runnable() {

			@Override
			public void run() {

				setStop(false);
			}
		});
	}

	public void setStop(final boolean progress) {
		_Log.d(__CLASSNAME__, getMethodName() + progress);
		if (!ViewCompat.isAttachedToWindow(this)) {
			return;
		}

		setEnabled(true);

		post(new Runnable() {

			@Override
			public void run() {

				if (findViewById(R.id.buttonPlay) != null) {
					// ((ImageButton) findViewById(R.id.buttonPlay)).setText("▶");
					((ImageButton) findViewById(R.id.buttonPlay)).setImageResource(R.drawable.ic_action_play);
				}
				if (progress) {
					if (seekPlay != null) {
						seekPlay.setProgress(0);
					}
				}
			}
		});

	}

	@Override
	public void restart() {
		_Log.d(__CLASSNAME__, getMethodName() + "count:" + count);

		super.restart();

		post(new Runnable() {

			@Override
			public void run() {

				setEnabled(false);
				count++;
				setStartTime();
			}
		});
	}

	@Override
	public void repeat() {
		_Log.d(__CLASSNAME__, getMethodName() + "count:" + count);

		super.repeat();

		post(new Runnable() {

			@Override
			public void run() {

				// setEnabled(false);
				count++;
				setStartTime();
			}
		});
	}

	@Override
	public void seek(int msec) {
		// _Log.d(__CLASSNAME__, getMethodName() + msec);

		super.seek(msec);

		setSeek(msec);
	}

	public void setSeek(int msec) {
		seekPlay.setProgress(msec);
	}

	@Override
	public void onTime(int t) {

		// _Log.d(__CLASSNAME__, getMethodName() + t);

		super.onTime(t);

		setOnTime(t);
	}

	public void setOnTime(int t) {
		if (!tracking) {
			seekPlay.setProgress(t);
		}
	}

	@Override
	public void onSeekComplete() {

		_Log.w(__CLASSNAME__, getMethodName());

		super.onSeekComplete();

	}

	@Override
	public void onRelease() {

		_Log.w(__CLASSNAME__, getMethodName());

		super.onRelease();

	}

	@Override
	public void onReady(int count) {

		_Log.w(__CLASSNAME__, getMethodName());

		super.onReady(count);

	}

	@Override
	public void onPrepared() {

		_Log.w(__CLASSNAME__, getMethodName());

		super.onPrepared();

		prepare();
	}

	@Override
	public void onError() {

		_Log.w(__CLASSNAME__, getMethodName());

		super.onError();

		stop();
		close();

		ERROR t = ERROR.SOUNDTOUCHPLAY;

		if (type == TYPE.AUDIOTRACKPLAY) {
			t = ERROR.AUDIOTRACKPLAY;
		} else if (type == TYPE.MEDIAPLAYERPLAY) {
			t = ERROR.MEDIAPLAYERPLAY;
		} else if (type == TYPE.SOUNDTOUCHPLAY) {
			t = ERROR.SOUNDTOUCHPLAY;
		}

		Exception e = new Exception("On Error");
		setErrorTime(t, e);
		setEnabled(true);

		getPlayFragment().onError(t, e);

	}

	@Override
	public void onError(final ERROR t, final Exception e) {
		_Log.w(__CLASSNAME__, getMethodName() + t);


		super.onError(t, e);

		stop();
		close();

		setErrorTime(t, e);
		setEnabled(true);

		if (getPlayFragment() != null) {
			getPlayFragment().onError(t, e);
		}

	}

	@Override
	public void onTimeout(final long timeout) {
		_Log.w(__CLASSNAME__, getMethodName() + timeout);

		super.onTimeout(timeout);
	}

	@Override
	public void onRetry(final int count) {
		_Log.w(__CLASSNAME__, getMethodName() + count);


		super.onRetry(count);
	}

	@Override
	public void onCompletion() {

		boolean repeat = ((CheckBox) findViewById(R.id.checkRepeatRestart)).isChecked();
		_Log.w(__CLASSNAME__, getMethodName() + repeat + ", count:" + count);

		super.onCompletion();

	}

	@Override
	public void onBufferingUpdate(int percent) {

		_Log.w(__CLASSNAME__, getMethodName() + percent);

		super.onBufferingUpdate(percent);

	}

	public class InputFilterMinMax implements InputFilter {

		private final int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input)) return null;
			} catch (NumberFormatException nfe) {
			}
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}

	static public boolean tracking = false;
	SeekBar seekPlay;

	void progress(boolean init) {
		_Log.e(__CLASSNAME__, getMethodName() + init);

		seekPlay = (SeekBar) findViewById(R.id.seekPlay);
		seekPlay.setEnabled(false);

		seekPlay.setMax(getTotalTime());

		seekPlay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				tracking = false;
				seek(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				tracking = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// _Log.d(__CLASSNAME__, getMethodName() + progress + "/" + seekBar.getMax());
				setPlayTime();
			}
		});
	}

	public void setPlayTime() {
		if (seekPlay == null) {
			seekPlay = (SeekBar) findViewById(R.id.seekPlay);
		}
		long tak = System.currentTimeMillis() - ticky;
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

	FragmentActivity getActivity() {
		return (FragmentActivity) getContext();
	}

	FragmentManager getSupportFragmentManager() {

		if (getActivity() != null) {
			return getActivity().getSupportFragmentManager();
		}
		return null;
	}

	_PlayFragment getPlayFragment() {
		Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("fragment1");
		if (fragment instanceof _PlayFragment) {
			return (_PlayFragment) fragment;
		}
		return null;
	}
}
