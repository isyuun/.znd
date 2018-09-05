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
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	ListenTempo.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ ListenTempo.java
 * </pre>
 */
package kr.keumyoung.karaoke.play;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-01-15
 */
class ListenTempo extends ListenPitch {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {
		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public ListenTempo(Context context) {
		super(context);
		txt_tempo = (TextView) ((Activity) context).findViewById(R.id.txt_tempo);
		Log.wtf(_toString(), getMethodName() + ":" + txt_tempo);
	}

	@Override
	public boolean play() {
		Log.i(_toString(), getMethodName() + ":" + getTempoPercent() + "+" + getTempoPercentMargin());
		boolean ret = super.play();
		if (ret) {
			setTempoPercent(TEMPO_NORMAL);
		}
		return ret;
	}

	@Override
	public void stop() {
		Log.i(_toString(), getMethodName() + ":" + getTempoPercent() + "+" + getTempoPercentMargin());
		//setTempoPercent(TEMPO_NORMAL);
		super.stop();
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

	TextView txt_tempo;

	public TextView getTxtTempo() {
		return txt_tempo;
	}

	public void setTxtTempo(TextView txt_tempo) {
		this.txt_tempo = txt_tempo;
		//setTempoText(0);
	}

	@Override
	void setListen() {
		Log.e(_toString(), getMethodName());
		super.setListen();

		txt_tempo = (TextView) ((Activity) context).findViewById(R.id.txt_tempo);
	}

	/**
	 * 절대템포(씪빠)
	 */
	final float absTEMPO = 50.0f;
	/**
	 * 단위템포(씪빠)
	 */
	final float untTEMPO = 5.0f;
	/**
	 * 절대템포(표시)
	 */
	final float dabsTEMPO = 2.0f;
	/**
	 * 단위템포(표시)
	 */
	final float duntTEMPO = 0.1f;

	/**
	 * 최대템포:바꿔서 젖되도 난몰랑
	 */
	public static int TEMPO_MAX = 50;
	/**
	 * 빠른템포:바꿔서 젖되도 난몰랑
	 */
	public static int TEMPO_FAST = 10;
	/**
	 * 보통템포:바꿔서 젖되도 난몰랑
	 */
	public static int TEMPO_NORMAL = 0;
	/**
	 * 느린템포:바꿔서 젖되도 난몰랑
	 */
	public static int TEMPO_SLOW = -5;
	/**
	 * 최소템포:바꿔서 젖되도 난몰랑
	 */
	public static int TEMPO_MIN = -50;

	@Override
	protected void init(boolean init) {
		Log.w(_toString(), getMethodName() + init);
		super.init(init);
	}

	private void tempo(boolean init) {
	}

	/**
	 * Tempo percentage must be between -50 and 100
	 */
	public void setTempoDN() {
		int percent = getTempoPercent();
		if (TEMPO_MIN < percent) {
			percent -= (int) untTEMPO;
			//Tempo percentage must be between -50 and 100
			if (percent > 0) {
				percent -= untTEMPO;
			}
			Log.wtf(_toString(), getMethodName() + getTempo() + ":" + getTempoPercent() + "->" + percent);
			setTempoPercent(percent);
		}
	}

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 */
	public void setTempoUP() {
		int percent = getTempoPercent();
		if (TEMPO_MAX * 2 > percent) {
			percent += (int) untTEMPO;
			//Tempo percentage must be between -50 and 100
			if (percent > 0) {
				percent += untTEMPO;
			}
			Log.wtf(_toString(), getMethodName() + getTempo() + ":" + getTempoPercent() + "->" + percent);
			setTempoPercent(percent);
		}
	}

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	@Override
	public void setTempoPercent(int percent) {
		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + percent + ":" + TEMPO_PERCENT_MARGIN);

		percent += TEMPO_PERCENT_MARGIN;
		if (TEMPO_PERCENT_MARGIN != 0) {
			Log.wtf(_toString(), getMethodName() + percent + ":" + TEMPO_PERCENT_MARGIN);
		}

		super.setTempoPercent(percent);

		clearTempo();

		setTempoText(percent);

	}

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	@Override
	public int getTempoPercent() {
		int ret = super.getTempoPercent();
		ret -= TEMPO_PERCENT_MARGIN;
		return ret;
		//float percent = getTempo() * 100.00f - 100.00f;
		//int ret = Math.round(percent);
		//ret -= TEMPO_PERCENT_MARGIN;
		//return ret;
	}

	/**
	 * <pre>
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 * 이런개젖가튼자바썅나가뒤저라. String.format(...)
	 * <a href="http://stackoverflow.com/questions/6311599/why-is-androids-string-format-dog-slow">stackoverflow Why is android's String.format dog slow?</a>
	 * </pre>
	 */
	private void setTempoText(int percent) {
		percent -= TEMPO_PERCENT_MARGIN;
		String text = String.format(/*label + */"%.1f"/* + "(" + percent + ")"*/, percent > 0 ? ((float) percent) / 100.0f : ((float) percent) / 50.0f);
		if (txt_tempo != null) {
			txt_tempo.setText(text);
		}

		// _LOG.e(_toString(), getMethodName() + "balance:" + balance + " - " + min + "~" + max);

		//setInfo();
	}

	public void setTempoText() {
		int percent = getTempoPercent() + TEMPO_PERCENT_MARGIN;
		setTempoText(percent);
	}

	public void setTempoF() {
		setTempoPercent(getTempoPercent() + TEMPO_FAST);
	}

	public void setTempoN() {
		setTempoPercent(TEMPO_NORMAL);
	}

	public void setTempoM() {
		setTempoPercent(getTempoPercent() + TEMPO_SLOW);
	}

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	private int TEMPO_PERCENT_MARGIN = 0;

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	public void setTempoPercentMargin(int margin) {
		TEMPO_PERCENT_MARGIN = margin;
	}

	/**
	 * BKO-S200 기종 녹음곡 음정/템포 오류
	 * Tempo percentage must be between -50 and 100 : 젖같네시팔
	 *
	 * @see com.smp.soundtouchandroid.SoundTouch#setTempoChange(float)
	 */
	public int getTempoPercentMargin() {
		return TEMPO_PERCENT_MARGIN;
	}

}
