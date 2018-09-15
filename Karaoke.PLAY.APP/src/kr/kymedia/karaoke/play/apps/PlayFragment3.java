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
 * filename	:	PlayFragmentTempo.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ PlayFragmentTempo.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.PlayViewTempo;
import kr.kymedia.karaoke.play.app.view.SongPlayView;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.widget.AutoRepeatImageButton;
import kr.kymedia.karaoke.widget.BalanceSeekBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 
 * TODO<br>
 * 
 * <pre>
 * 템포기능실행
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 8. 8.
 * @version 1.0
 */
public class PlayFragment3 extends PlayFragment2 {
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
	public void start() {

		super.start();

		tempo(true);
	}

	@Override
	public void prepare() {

		super.prepare();

		tempo(false);
	}

	BalanceSeekBar seekTempo;

	private void tempo(boolean init) {
		if (seekTempo == null) {
			seekTempo = (BalanceSeekBar) findViewById(R.id.seekTempo);
		}

		float balance = seekTempo.getDisBalance();
		float min = seekTempo.getDisMinBalance();
		float max = seekTempo.getDisMaxBalance();
		Log.i(__CLASSNAME__, getMethodName() + init + " - balance:" + balance + " - " + min + "~" + max);

		// 템포설정
		int tempo = seekTempo.getBalance();
		setTempoPercent(tempo);

		seekTempo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int tempo = seekTempo.getBalance();
				setTempoPercent(tempo);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {


			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				int tempo = seekTempo.getBalance();
				if (fromUser) {
					setTempoText(tempo);
				} else {
					setTempoPercent(tempo);
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonTempo)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					seekTempo.setBalance(0);
					setTempoPercent(0);
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

						seekTempo.setProgress(seekTempo.getProgress() - seekTempo.getUnitBalace());
					}
				});

		((ImageButton) findViewById(R.id.buttonTempoUp)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonTempoUp))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						seekTempo.setProgress(seekTempo.getProgress() + seekTempo.getUnitBalace());
					}
				});

	}

	@Override
	public void setTempoPercent(int percent) {
		// Log.w(__CLASSNAME__, getMethodName() + tempo);


		super.setTempoPercent(percent);
	}

	private void setTempoText(int tempo) {
		for (SongPlayView choir : getChoirs()) {
			((PlayViewTempo) choir).setTempoText(tempo);
		}
	}

}
