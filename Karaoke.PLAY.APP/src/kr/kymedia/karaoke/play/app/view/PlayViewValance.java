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
 * filename	:	PlayViewValance.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayViewValance.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.view.EnabledRadioGroup;
import kr.kymedia.karaoke.widget.AutoRepeatImageButton;
import kr.kymedia.karaoke.widget.BalanceSeekBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * @since 2014. 8. 8.
 * @version 1.0
 */
public class PlayViewValance extends PlayView {
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

	public PlayViewValance(Context context) {
		super(context);
	}

	public PlayViewValance(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewValance(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void start() {

		super.start();
		valance(true);
	}

	@Override
	public void prepare() {

		super.prepare();
		valance(false);
	}

	private void clearValance() {
		// _Log.e(__CLASSNAME__, getMethodName());
		((RadioButton) findViewById(R.id.radioValanceC)).setChecked(false);
		((RadioButton) findViewById(R.id.radioValanceL)).setChecked(false);
		((RadioButton) findViewById(R.id.radioValanceR)).setChecked(false);
		((RadioGroup) findViewById(R.id.radioValance)).clearCheck();
		((CheckBox) findViewById(R.id.checkMute)).setChecked(false);
	}

	float getValance() {
		return seekValance.getDisBalance();
	}

	private TextView textValance;
	private TextView textMinValance;
	private TextView textMaxValance;

	@Override
	void setPlayView() {

		_Log.e(__CLASSNAME__, getMethodName());
		super.setPlayView();

		seekValance = (BalanceSeekBar) findViewById(R.id.seekValance);
		textValance = (TextView) findViewById(R.id.textValance);
		textMinValance = (TextView) findViewById(R.id.textMinValance);
		textMaxValance = (TextView) findViewById(R.id.textMaxValance);
	}

	@Override
	protected void init(boolean init) {

		_Log.w(__CLASSNAME__, getMethodName() + init);
		super.init(init);

		try {
			// 좌우초기화
			if (init) {
				/**
				 * 절대좌우(씪빠)
				 */
				final float absVALANCE = 10f;
				/**
				 * 단위좌우(씪빠)
				 */
				final float untVALANCE = 1f;
				/**
				 * 절대좌우(표시)
				 */
				final float dabsVALANCE = 1.0f;
				/**
				 * 단위좌우(표시)
				 */
				final float duntVALANCE = 0.1f;

				seekValance.init(absVALANCE, untVALANCE, dabsVALANCE, duntVALANCE);
				textMinValance.setText(String.format("L:%.1f", Math.abs(seekValance.getDisMinBalance())));
				textMaxValance.setText(String.format("R:%.1f", Math.abs(seekValance.getDisMaxBalance())));
			}

			// 좌우설정
			float LR = seekValance.getDisBalance();
			if (isMute()) {
				mute(isMute());
			} else {
				setValance(LR);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public boolean isMute() {
		if (findViewById(R.id.checkMute) != null) {
			return ((CheckBox) findViewById(R.id.checkMute)).isChecked();
		} else {
			return false;
		}
	}

	public void mute(boolean enabled) {
		_Log.e(__CLASSNAME__, getMethodName() + enabled);

		float LR = seekValance.getDisBalance();

		if (enabled) {
			seekValance.setEnabled(false);
			setVolume(getMinVolume(), getMinVolume());
		} else {
			seekValance.setEnabled(true);
			setValance(LR);
		}
	}

	protected BalanceSeekBar seekValance;

	public BalanceSeekBar getSeekValance() {
		return seekValance;
	}

	private void valance(boolean init) {
		float balance = seekValance.getDisBalance();
		float min = seekValance.getDisMinBalance();
		float max = seekValance.getDisMaxBalance();
		_Log.i(__CLASSNAME__, getMethodName() + init + " - balance:" + balance + " - " + min + "~" + max);

		seekValance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				float LR = seekValance.getDisBalance();
				setValance(LR);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				clearValance();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				float LR = seekValance.getDisBalance();
				if (fromUser) {
					setValanceText(LR);
				} else {
					setValance(LR);
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonValance)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					seekValance.setBalance(0);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonValanceDn)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonValanceDn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						seekValance.setProgress(seekValance.getProgress() - seekValance.getUnitBalace());
					}
				});

		((ImageButton) findViewById(R.id.buttonValanceUp)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonValanceUp))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						seekValance.setProgress(seekValance.getProgress() + seekValance.getUnitBalace());
					}
				});

		((CheckBox) findViewById(R.id.checkMute))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

						mute(isChecked);
						((EnabledRadioGroup) findViewById(R.id.radioValance)).setEnabled(!isChecked);
					}
				});

		mute(((CheckBox) findViewById(R.id.checkMute)).isChecked());

		((RadioGroup) findViewById(R.id.radioValance))
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						// int val = 0;
						// if (checkedId == R.id.radioValanceC) {
						// } else if (checkedId == R.id.radioValanceL) {
						// } else if (checkedId == R.id.radioValanceR) {
						// }
						// _Log.e(__CLASSNAME__, getMethodName() + val + ":" + findViewById(checkedId));
					}
				});

		findViewById(R.id.radioValanceC).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				int val = 0;

				_Log.e(__CLASSNAME__, getMethodName() + val + ":" + v);

				seekValance.setBalance(val);

			}
		});

		findViewById(R.id.radioValanceL).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				int val = (int) (seekValance.getMinBalance());

				_Log.e(__CLASSNAME__, getMethodName() + val + ":" + v);

				seekValance.setBalance(val);

			}
		});

		findViewById(R.id.radioValanceR).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				int val = (int) (seekValance.getMaxBalance());

				_Log.e(__CLASSNAME__, getMethodName() + val + ":" + v);

				seekValance.setBalance(val);

			}
		});

	}

	@Override
	public void setValance(final float LR) {
		// _Log.w(__CLASSNAME__, getMethodName() + LR);


		super.setValance(LR);

		clearValance();
		handler.post(new Runnable() {

			@Override
			public void run() {

				setValanceText(LR);
			}
		});
	}

	public void setValanceText(float LR) {
		String lab = "C:";

		if (LR > 0) {
			lab = "R:";
		} else if (LR < 0) {
			lab = "L:";
		}

		// _Log.e(__CLASSNAME__, getMethodName() + lab + LR);

		// float balance = seekValance.getDisBalance();
		// float min = seekValance.getDisMinBalance();
		// float max = seekValance.getDisMaxBalance();
		// _Log.w(__CLASSNAME__, getMethodName() + "balance:" + balance + " - " + min + "~" + max);

		// String text = String.format(lab + "%.1f", Math.abs(seekValance.getDisBalance()));
		String text = String.format(lab + "%.1f", Math.abs(LR));
		textValance.setText(text);

		int id = 0;

		if (LR == 0.0) {
			// ((RadioButton) findViewById(R.id.radioValanceC)).setChecked(true);
			id = R.id.radioValanceC;
		} else if (LR == -1.0) {
			// ((RadioButton) findViewById(R.id.radioValanceL)).setChecked(true);
			id = R.id.radioValanceL;
		} else if (LR == 1.0) {
			// ((RadioButton) findViewById(R.id.radioValanceR)).setChecked(true);
			id = R.id.radioValanceR;
		}

		if (id > 0) {
			((RadioGroup) findViewById(R.id.radioValance)).check(id);
		}

		setInfo();
	}

}
