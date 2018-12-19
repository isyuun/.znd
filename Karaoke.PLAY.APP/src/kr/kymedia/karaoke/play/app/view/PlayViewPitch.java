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
 * filename	:	PlayViewPitch.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayViewPitch.java
 * </pre>
 */

package kr.kymedia.karaoke.play.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.view.EnabledRadioGroup;
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
public class PlayViewPitch extends PlayViewValance2 {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayViewPitch(Context context) {
		super(context);
	}

	public PlayViewPitch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewPitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private CheckBox checkPitch;
	private TextView textMaxPitch;
	private TextView textMinPitch;
	private BalanceSeekBar seekPitch;

	public BalanceSeekBar getSeekPitch() {
		return seekPitch;
	}

	@Override
	public void start() {

		super.start();
		pitch(true);
	}

	@Override
	public void prepare() {

		super.prepare();
		pitch(false);
	}

	private void clearPitch() {
		((RadioButton) findViewById(R.id.radioPitchM)).setChecked(false);
		((RadioButton) findViewById(R.id.radioPitchN)).setChecked(false);
		((RadioButton) findViewById(R.id.radioPitchF)).setChecked(false);
		((RadioGroup) findViewById(R.id.radioPitchFM)).clearCheck();
	}

	@Override
	public int getPitch() {
		if (song != null) {
			return song.getPitch();
		} else {
			return 0;
		}
	}

	// private final int[] pitchUDs = { -12,//C♭
	// -10,//D
	// -8, //E
	// -7, //F
	// -5, //G
	// -3, //A
	// -1, //B
	// 0, //C
	// 2, //D
	// 4, //E
	// 5, //F
	// 7, //G
	// 9, //A
	// 11, //B
	// 12, //C#
	// };
	private int noteIndex = 0;

	private final int[] noteUPs = {
			0, // C
			2, // D
			4, // E
			5, // F
			7, // G
			9, // A
			11, // B
			12, // C#
	};

	private final int[] noteDNs = {
			0, // C
			-1, // B
			-3, // A
			-5, // G
			-7, // F
			-8, // E
			-10,// D
			-12,// C♭
	};

	private final String[] pitchSTs = {
			"C4",
			"C#4",
			"D4",
			"D#4",
			"E4",
			"F4",
			"F#4",
			"G4",
			"G#4",
			"A4",
			"A#4",
			"B4",
			"C5",
			"C#5",
			"D5",
			"D#5",
			"E5",
			"F5",
			"F#5",
			"G5",
			"G#5",
			"A5",
			"A#5",
			"B5",
			"C6",
	};

	private String pitchSTs(int index) {
		String ret = "???";

		if (index > -1 && index < pitchSTs.length) {
			ret = pitchSTs[index];
		}

		return ret;
	}

	private void noteIndex() {
		int pitch = seekPitch.getBalance();
		for (int i = 0; i < noteDNs.length; i++) {
			if (pitch == noteDNs[i]) {
				noteIndex = i;
				break;
			}
		}
		for (int i = 0; i < noteUPs.length; i++) {
			if (pitch == noteUPs[i]) {
				noteIndex = i;
				break;
			}
		}
		_Log.e(__CLASSNAME__, getMethodName() + pitch + ":" + noteIndex);
	}

	@Override
	public void stop() {

		super.stop();
		noteIndex();
	}

	@Override
	void setPlayView() {

		_Log.e(__CLASSNAME__, getMethodName());
		super.setPlayView();

		seekPitch = (BalanceSeekBar) findViewById(R.id.seekPitch);
		checkPitch = (CheckBox) findViewById(R.id.txt_pitch);
		textMinPitch = (TextView) findViewById(R.id.textMinPitch);
		textMaxPitch = (TextView) findViewById(R.id.textMaxPitch);
	}

	/**
	 * 절대피치(씪빠)
	 */
	final float absPITCH = pitchSTs.length / 2;
	/**
	 * 단위피치(씪빠)
	 */
	final float untPITCH = 1.0f;
	/**
	 * 절대피치(표시)
	 */
	final float dabsPITCH = pitchSTs.length / 2;
	/**
	 * 단위피치(표시)
	 */
	final float duntPITCH = 1.0f;

	public static int PITCH_FEMALE = 5;
	public static int PITCH_MAX = 12;
	public static int PITCH_NORMAL = 0;
	public static int PITCH_MALE = -5;
	public static int PITCH_MIN = -12;

	@Override
	protected void init(boolean init) {

		// _Log.e(__CLASSNAME__, getMethodName() + init);
		super.init(init);

		try {
			// 피치초기화
			if (init) {
				seekPitch.init(absPITCH, untPITCH, dabsPITCH, duntPITCH);
				textMinPitch.setText(String.format("%d", (int) seekPitch.getDisMinBalance()));
				textMaxPitch.setText(String.format("%d", (int) seekPitch.getDisMaxBalance()));
			}

			int pitch = seekPitch.getBalance();

			// 피치자동
			if (((CheckBox) findViewById(R.id.txt_pitch)).isChecked()) {
				int checkedPitch = ((RadioGroup) findViewById(R.id.radioPitchUD)).getCheckedRadioButtonId();

				boolean pich = false;
				if (checkedPitch == R.id.radioPitchUp) {
					if (noteIndex >= noteUPs.length || noteIndex < 0) {
						noteIndex = 0;
					}
					if (pitch == noteUPs[noteIndex]) {
						pich = true;
					}
					pitch = noteUPs[noteIndex];
				} else if (checkedPitch == R.id.radioPitchDn) {
					if (noteIndex >= noteDNs.length || noteIndex < 0) {
						noteIndex = 0;
					}
					if (pitch == noteDNs[noteIndex]) {
						pich = true;
					}
					pitch = noteDNs[noteIndex];
				}

				_Log.e(__CLASSNAME__, getMethodName() + noteIndex + ":" + pitch);

				noteIndex++;

				if (pich) {
					setPitch(pitch);
				} else {
					seekPitch.setBalance(pitch);
				}
			} else {
				// 피치설정
				setPitch(pitch);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void pitch(boolean init) {
		float balance = seekPitch.getDisBalance();
		float min = seekPitch.getDisMinBalance();
		float max = seekPitch.getDisMaxBalance();
		_Log.i(__CLASSNAME__, getMethodName() + init + " - balance:" + balance + " - " + min + "~" + max);

		seekPitch.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int pitch = seekPitch.getBalance();
				setPitch(pitch);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				clearPitch();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				int pitch = seekPitch.getBalance();
				if (fromUser) {
					setPitchText(pitch);
				} else {
					setPitch(pitch);
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonPitch)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					if (seekPitch.getBalance() != 0) {
						seekPitch.setBalance(0);
					}
					noteIndex = 0;
				} catch (NumberFormatException e) {

					e.printStackTrace();
				}
			}
		});

		((ImageButton) findViewById(R.id.buttonPitchDn)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonPitchDn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setPitchDN();
			}
		});

		((ImageButton) findViewById(R.id.buttonPitchUp)).setSoundEffectsEnabled(false);
		((AutoRepeatImageButton) findViewById(R.id.buttonPitchUp)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setPitchUP();
			}
		});

		checkPitch.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				((EnabledRadioGroup) findViewById(R.id.radioPitchUD)).setEnabled(isChecked);
				((EnabledRadioGroup) findViewById(R.id.radioPitchFM)).setEnabled(!isChecked);
				if (isPlaying() && isChecked) {
					noteIndex++;
				} else {
					noteIndex();
				}
			}
		});

		((RadioGroup) findViewById(R.id.radioPitchFM)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.radioPitchM) {
				} else if (checkedId == R.id.radioPitchF) {
				}
			}
		});

		findViewById(R.id.radioPitchM).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				seekPitch.setBalance(PITCH_MALE * seekPitch.getUnitBalace());

			}
		});

		findViewById(R.id.radioPitchN).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				seekPitch.setBalance(PITCH_NORMAL);

			}
		});

		findViewById(R.id.radioPitchF).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				seekPitch.setBalance(PITCH_FEMALE * seekPitch.getUnitBalace());
			}
		});
	}

	@Override
	public void setPitch(final int pitch) {
		_Log.e(__CLASSNAME__, getMethodName() + pitch);


		super.setPitch(pitch);

		clearPitch();
		handler.post(new Runnable() {

			@Override
			public void run() {

				setPitchText(pitch);
			}
		});
	}

	public void setPitchDN() {
		// if (0 != seekPitch.getProgress())
		{
			seekPitch.setProgress(seekPitch.getProgress() - seekPitch.getUnitBalace());
		}
	}

	public void setPitchUP() {
		// if (seekPitch.getMax() != seekPitch.getProgress())
		{
			seekPitch.setProgress(seekPitch.getProgress() + seekPitch.getUnitBalace());
		}
	}

	public void setPitchText(int pitch) {
		// float balance = seekPitch.getDisBalance();
		// float min = seekPitch.getDisMinBalance();
		// float max = seekPitch.getDisMaxBalance();
		// _Log.w(__CLASSNAME__, getMethodName() + "balance:" + balance + " - " + min + "~" + max);

		String label = "PITCH:";
		String text = String.format(label + "%d(%s)", pitch, pitchSTs(seekPitch.getProgress()));
		checkPitch.setText(text);
		// _Log.e(__CLASSNAME__, getMethodName() + text);

		// _Log.e(__CLASSNAME__, getMethodName() + "balance:" + balance + " - " + min + "~" + max);
		int id = 0;

		if (pitch == PITCH_NORMAL) {
			id = R.id.radioPitchN;
		} else if (pitch == PITCH_MALE) {
			id = R.id.radioPitchM;
		} else if (pitch == PITCH_FEMALE) {
			id = R.id.radioPitchF;
		}

		if (id > 0) {
			((RadioGroup) findViewById(R.id.radioPitchFM)).check(id);
		}

		setInfo();
	}
}
