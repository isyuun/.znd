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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.PLAY4.APP
 * filename	:	PlayFragment6.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ PlayFragment6.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import java.io.File;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.PlayViewPitch;
import kr.kymedia.karaoke.play.app.view._PlayView;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.widget.BalanceSeekBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * <pre>
 * 테스트기능추가
 * </pre>
 *
 * @author isyoon
 * @since 2015. 6. 26.
 * @version 1.0
 */
public class PlayFragment6 extends PlayFragment5 {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		Log.e(__CLASSNAME__, getMethodName() + menu + "," + inflater);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.e(__CLASSNAME__, getMethodName() + item);

		if (item.getItemId() == R.id.pitch_test_1) {
			pitch_test_1();
			return true;
		} else if (item.getItemId() == R.id.pitch_test_2) {
			pitch_test_2();
			return true;
		} else if (item.getItemId() == R.id.choir_test_1) {
			choir_test_1();
			return true;
		} else if (item.getItemId() == R.id.choir_test_2) {
			choir_test_2();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void reset() {
		Log.e(__CLASSNAME__, getMethodName());
		findViewById(R.id.buttonStop).performClick();

		findViewById(R.id.buttonHide).performClick();

		removeChoirs();

		findViewById(R.id.buttonReset).performClick();
	}

	private void pitch_test_1() {
		Log.e(__CLASSNAME__, getMethodName());
		reset();

		findViewById(R.id.buttonShow).performClick();

		String path1 = "file:///android_asset" + File.separator + "piano" + File.separator + "Piano_060_C5.ogg";
		setPath(path1);

		((CheckBox) findViewById(R.id.txt_pitch)).setChecked(true);
		((RadioButton) findViewById(R.id.radioPitchUp)).setChecked(true);

		findViewById(R.id.buttonPlay).performClick();
	}

	private void pitch_test_2() {
		Log.e(__CLASSNAME__, getMethodName());
		reset();

		findViewById(R.id.buttonShow).performClick();

		String path1 = "file:///android_asset" + File.separator + "music" + File.separator + "1.mp3";
		_PlayView l = getLead();
		l.setPath(path1);

		BalanceSeekBar seekPitch = (BalanceSeekBar) l.findViewById(R.id.seekPitch);
		seekPitch.setBalance((PlayViewPitch.PITCH_FEMALE - 1) * seekPitch.getUnitBalace());

		findViewById(R.id.buttonPlay).performClick();
	}

	private void choir_test_1() {
		Log.e(__CLASSNAME__, getMethodName());
		reset();

		String path1 = "file:///android_asset" + File.separator + "music" + File.separator + "1.mp3";
		_PlayView l = getLead();
		l.setPath(path1);
		l.findViewById(R.id.radioValanceL).performClick();

		String path2 = "file:///android_asset" + File.separator + "music" + File.separator + "2.mp3";
		_PlayView c = addChoir();
		c.setPath(path2);
		c.findViewById(R.id.radioValanceR).performClick();

		findViewById(R.id.buttonPlay).performClick();
	}

	private void choir_test_2() {
		Log.e(__CLASSNAME__, getMethodName());
		reset();

		String path1 = "file:///android_asset" + File.separator + "music" + File.separator + "1.mp3";
		_PlayView l = getLead();
		l.setPath(path1);
		l.findViewById(R.id.radioValanceL).performClick();

		String path2 = "file:///android_asset" + File.separator + "music" + File.separator + "1.mp3";
		_PlayView c = addChoir();
		c.setPath(path2);
		c.findViewById(R.id.radioValanceR).performClick();

		BalanceSeekBar seekPitch = (BalanceSeekBar) c.findViewById(R.id.seekPitch);
		seekPitch.setBalance((PlayViewPitch.PITCH_FEMALE - 1) * seekPitch.getUnitBalace());

		findViewById(R.id.buttonPlay).performClick();
	}
}
