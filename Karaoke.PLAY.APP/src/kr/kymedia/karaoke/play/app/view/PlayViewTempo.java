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
 * filename	:	PlayViewTempo.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayViewTempo.java
 * </pre>
 */

package kr.kymedia.karaoke.play.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import kr.kymedia.karaoke.play.app.BuildConfig;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.widget.AutoRepeatImageButton;
import kr.kymedia.karaoke.widget.BalanceSeekBar;

/**
 * TODO<br>
 * <p/>
 *
 * <pre></pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2014. 8. 8.
 */
public class PlayViewTempo extends PlayViewPitch {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()));
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayViewTempo(Context context) {
		super(context);
	}

	public PlayViewTempo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewTempo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private TextView textTempo;
	private TextView textMinTempo;
	private TextView textMaxTempo;
	private BalanceSeekBar seekTempo;

	public BalanceSeekBar getSeekTempo() {
		return seekTempo;
	}

	@Override
	public void start() {

		super.start();
		tempo(true);
	}

	@Override
	public void prepare() {

		super.prepare();
		tempo(false);
	}

	private void clearTempo() {


	}

	@Override
	public float getTempo() {
		if (song != null) {
			return song.getTempo();
		}
		return 0;
	}

	@Override
	void setPlayView() {

		Log.e(_toString(), getMethodName());
		super.setPlayView();

		seekTempo = (BalanceSeekBar) findViewById(R.id.seekTempo);
		textTempo = (TextView) findViewById(R.id.txt_tempo);
		textMinTempo = (TextView) findViewById(R.id.textMinTempo);
		textMaxTempo = (TextView) findViewById(R.id.textMaxTempo);
	}

	/**
	 * 절대템포(씪빠)
	 */
	final float absTEMPO = 50f;
	/**
	 * 단위템포(씪빠)
	 */
	final float untTEMPO = 1f;
	/**
	 * 절대템포(표시)
	 */
	final float dabsTEMPO = 2.0f;
	/**
	 * 단위템포(표시)
	 */
	final float duntTEMPO = 0.1f;

	public static int TEMPO_MAX = 100;
	public static int TEMPO_FEMALE = 5;
	public static int TEMPO_NORMAL = 0;
	public static int TEMPO_MALE = -5;
	public static int TEMPO_MIN = -50;

	@Override
	protected void init(boolean init) {

		Log.wtf(_toString(), getMethodName() + init);
		super.init(init);

		try {
			// 템포초기화
			if (init) {
				seekTempo.init(absTEMPO, untTEMPO, dabsTEMPO, duntTEMPO);
				textMinTempo.setText(String.format("%.1f", seekTempo.getDisMinBalance()));
				textMaxTempo.setText(String.format("%.1f", seekTempo.getDisMaxBalance()));
			}

			// 템포설정
			int tempo = seekTempo.getBalance();
			setTempoPercent(tempo);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void tempo(boolean init) {
		float balance = seekTempo.getDisBalance();
		float min = seekTempo.getDisMinBalance();
		float max = seekTempo.getDisMaxBalance();
		Log.wtf(_toString(), getMethodName() + init + " - balance:" + balance + " - " + min + "~" + max);

		seekTempo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int percent = seekTempo.getBalance();
				setTempoPercent(percent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				clearTempo();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				int percent = seekTempo.getBalance();
				if (fromUser) {
					setTempoText(percent);
				} else {
					setTempoPercent(percent);
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonTempo)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					seekTempo.setBalance(0);
				} catch (NumberFormatException e) {

					e.printStackTrace();
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonTempoDn)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonTempoDn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						setTempoDN();
					}
				});

		((ImageButton) findViewById(R.id.buttonTempoUp)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonTempoUp))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						setTempoUP();
					}
				});

	}

	@Override
	public void setTempoPercent(final int percent) {
		Log.wtf(_toString(), getMethodName() + percent);


		int temp = percent;
		if (temp > 0) {
			temp *= 2;
		}

		super.setTempoPercent(temp);

		clearTempo();
		handler.post(new Runnable() {

			@Override
			public void run() {

				// 템포표시
				setTempoText(percent);
			}
		});
	}

	public void setTempoDN() {
		seekTempo.setProgress(seekTempo.getProgress() - seekTempo.getUnitBalace());
	}

	public void setTempoUP() {
		seekTempo.setProgress(seekTempo.getProgress() + seekTempo.getUnitBalace());
	}

	/**
	 * <pre>
	 * 이런개젖가튼자바썅나가뒤저라. String.format(...)
	 * <a href="http://stackoverflow.com/questions/6311599/why-is-androids-string-format-dog-slow">stackoverflow Why is android's String.format dog slow?</a>
	 * </pre>
	 */
	public void setTempoText(float tempo) {
		// float balance = seekTempo.getDisBalance();
		// float min = seekTempo.getDisMinBalance();
		// float max = seekTempo.getDisMaxBalance();
		// Log.w(_toString(), getMethodName() + "balance:" + balance + " - " + min + "~" + max);

		float abs = seekTempo.getAbs();
		float dabs = seekTempo.getDabs();
		float temp = tempo / (abs / dabs);

		String label = "TEMPO:";
		String text = String.format(label + "%.1f", temp);
		textTempo.setText(text);
		// Log.e(_toString(), getMethodName() + text);

		// Log.e(_toString(), getMethodName() + "balance:" + balance + " - " + min + "~" + max);

		setInfo();
	}

}
