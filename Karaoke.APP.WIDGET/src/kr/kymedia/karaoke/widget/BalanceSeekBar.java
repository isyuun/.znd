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
 * filename	:	SeekBarBalance.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ SeekBarBalance.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * 
 *
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 8. 7.
 * @version 1.0
 */
public class BalanceSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {
	// private String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();
	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s() ", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	public BalanceSeekBar(Context context) {
		super(context);
		super.setOnSeekBarChangeListener(this);
	}

	public BalanceSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BalanceSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 절대(표시)
	 */
	private float dabs;

	public float getDabs() {
		return dabs;
	}

	/**
	 * 단위(표시)
	 */
	private float dunt;

	public float getDunt() {
		return dunt;
	}

	/**
	 * 절대(씪빠)
	 */
	private float abs;

	public float getAbs() {
		return abs;
	}

	/**
	 * 단위(씪빠)
	 */
	private float unt;

	public float getUnt() {
		return unt;
	}

	public int getUnitBalace() {
		int ret = 1;
		ret = (int) (dunt / (dabs / abs));
		if (ret < 1) {
			ret = 1;
		}
		return ret;
	}

	/**
	 * 최소(씪빠)
	 */
	private float min;

	public float getMinBalance() {
		return min;
	}

	public float getDisMinBalance() {
		return min * unt / (abs / dabs);
	}

	/**
	 * 최대(씪빠)
	 */
	private float max;

	public float getMaxBalance() {
		return max;
	}

	public float getDisMaxBalance() {
		return max * unt / (abs / dabs);
	}

	int balance;

	public int getBalance() {
		return balance;
	}

	public float getDisBalance() {
		return (float) ((float) balance / (float) (abs / dabs));
	}

	public void setBalance(int balance) {
		this.balance = balance;
		int progress = (int) ((balance - min) / unt);
		setProgress(progress);
	}

	public void init(float abs, float unt, float dabs, float dunt) {

		if (unt <= 0) {
			unt = 1;
		}

		if (abs < unt) {
			abs = unt;
		}

		this.abs = abs;
		this.unt = unt;
		this.dabs = dabs;
		this.dunt = dunt;

		this.min = -(abs / unt);
		this.max = (abs / unt);

		// 템포초기화
		int progress = (int) ((this.max - this.min) * Math.abs((float) this.min
				/ (float) (this.max - this.min)));

		// 템포진행
		int max = (int) (this.max - this.min);

		// 밸런스
		setMax(max);
		setProgress(progress);

		balance = (int) ((getProgress() + min) * unt);

		// _Log.e(toString(), getMethodName() + "balance:" + balance + " - " + min + "~" + max);

	}

	SeekBar.OnSeekBarChangeListener listener;

	@Override
	public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {

		super.setOnSeekBarChangeListener(this);
		this.listener = listener;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		balance = (int) ((progress + min) * unt);

		if (this.listener != null) {
			listener.onProgressChanged(seekBar, progress, fromUser);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		if (this.listener != null) {
			listener.onStartTrackingTouch(seekBar);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		if (this.listener != null) {
			listener.onStopTrackingTouch(seekBar);
		}
	}

}
