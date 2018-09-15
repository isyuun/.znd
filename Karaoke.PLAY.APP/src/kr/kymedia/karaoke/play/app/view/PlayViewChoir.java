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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	PlayViewChoir.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayViewChoir.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 8. 20.
 * @version 1.0
 */
public class PlayViewChoir extends PlayViewTempo {
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

	public PlayViewChoir(Context context) {
		super(context);
	}

	public PlayViewChoir(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewChoir(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private boolean isChoirInsertInPlaying = false;

	@Override
	public void start() {

		super.start();

		choir(true);

		if (isChoir && !isChoirInsertInPlaying && getPlayFragment().isPlaying()) {
			isChoirInsertInPlaying = true;
			try {
				load(getPlayFragment().getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void prepare() {

		super.prepare();

		choir(false);

		if (isChoir && isChoirInsertInPlaying && getPlayFragment().isPlaying()) {
			// 일시정지경우
			getPlayFragment().tempo();
			play();
			getPlayFragment().pause();
			// //중지시킬경우
			// getPlayFragment().seek(getPlayFragment().getCurrentTime());
			// getPlayFragment().stop();
		}
		isChoirInsertInPlaying = false;
	}

	@Override
	void progress(boolean init) {

		super.progress(init);

		if (!isChoir) {
			seekPlay.setOnSeekBarChangeListener(null);
		}
	}

	private boolean isChoir = true;

	public boolean isChoir() {
		return isChoir;
	}

	public boolean isLead() {
		return !isChoir;
	}

	public void setChoir(boolean isChoir) {
		this.isChoir = isChoir;
	}

	void choir(boolean init) {
		Log.i(__CLASSNAME__, getMethodName() + isChoir + ":" + init);

		if (init) {
			if (isChoir) {
				showSongControl(false);
			} else {
				showSongControl(true);
			}
			showSongControl(false);

			((ImageButton) findViewById(R.id.buttonDel)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (getPlayFragment() != null) {
						getPlayFragment().removeChoir(R.id.choir, PlayViewChoir.this);
					}
				}

			});
		}

		setChoir();
	}

	@Override
	public void setChoir() {

		findViewById(R.id.buttonDel).setVisibility(View.GONE);

		if (!isChoir) {
			return;
		}

		findViewById(R.id.merge_stream_control).setVisibility(View.GONE);
		findViewById(R.id.buttonAdd).setVisibility(View.GONE);
		findViewById(R.id.buttonDel).setVisibility(View.VISIBLE);
		findViewById(R.id.merge_tempo_control).setVisibility(View.GONE);
		findViewById(R.id.seekPlay).setEnabled(false);

		findViewById(R.id.buttonPlay).setVisibility(View.GONE);
		findViewById(R.id.buttonStop).setVisibility(View.GONE);

	}

	@Override
	public void setEnabled(final boolean enabled) {
		// Log.w(__CLASSNAME__, getMethodName() + enabled + ":" + isPlaying());

		super.setEnabled(enabled);

		if (isChoir) {
			findViewById(R.id.buttonStream).setEnabled(false);
			findViewById(R.id.buttonPlay).setEnabled(false);
			findViewById(R.id.buttonStop).setEnabled(false);
			findViewById(R.id.seekPlay).setEnabled(false);
		}

		if (getPlayFragment() != null) {
			// getPlayFragment().setEnabled(enabled);
		}
	}

	@Override
	void setPlayView() {

		Log.e(__CLASSNAME__, getMethodName());
		super.setPlayView();

		((ImageButton) findViewById(R.id.buttonReset)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!isChoir) {
					getSeekTempo().setBalance(0);
				}

				getSeekValance().setBalance(0);

				getSeekPitch().setBalance(0);

				((CheckBox) findViewById(R.id.txt_pitch)).setChecked(false);
			}

		});
	}

	@Override
	public void showSongControl(boolean visible) {

		super.showSongControl(visible);

		if (isChoir) {
			findViewById(R.id.merge_tempo_control).setVisibility(View.GONE);
		}
	}

}
