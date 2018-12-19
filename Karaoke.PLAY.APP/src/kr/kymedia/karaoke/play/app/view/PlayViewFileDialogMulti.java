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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	PlayViewFileDialogMulti.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app.view
 *    |_ PlayViewFileDialogMulti.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import java.util.ArrayList;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.apps.PlayActivity2;
import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.util.TextUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.lamerman.isyoon.FileDialogFragmentBase;
import com.lamerman.isyoon.FileDialogFragmentMulti;
import com.lamerman.isyoon.SelectionMode;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 9. 17.
 * @version 1.0
 */
public class PlayViewFileDialogMulti extends PlayViewFileDialog {
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

	public PlayViewFileDialogMulti(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PlayViewFileDialogMulti(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewFileDialogMulti(Context context) {
		super(context);
	}

	ArrayList<String> paths;

	@Override
	public void onFileDialogResult(int requestCode, int resultCode, Intent data) {
		_Log.i(__CLASSNAME__, getMethodName() + data.getExtras());

		super.onFileDialogResult(requestCode, resultCode, data);

		String path = getPath();

		if (resultCode == Activity.RESULT_OK) {
			if (data.getExtras() != null) {
				paths = data.getExtras().getStringArrayList(FileDialogFragmentMulti.RESULT_PATHS);
				if (paths != null && paths.size() > 0) {
					path = paths.get(0);
				}
			}

			if (requestCode == FileDialogFragmentBase.REQUEST_SAVE) {
				System.out.println("Saving..." + path);
			} else if (requestCode == FileDialogFragmentBase.REQUEST_LOAD) {
				System.out.println("Loading..." + path);
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			System.out.println("Cancel..." + path);
		}

		_Log.e(__CLASSNAME__, getMethodName() + paths);

		if (!TextUtil.isEmpty(path)) {
			if (isPlaying() && !path.equalsIgnoreCase(getPath())) {
				getPlayFragment().stop(true);
			}

			setPath(path);

			// 리드곡은플래그먼트경로도바꾼다.
			if (isLead()) {
				getPlayFragment().setPath(path);
			}
		}

		if (paths != null && paths.size() > 1) {
			findViewById(R.id.buttonPrev).setEnabled(true);
			findViewById(R.id.buttonNext).setEnabled(true);
		} else {
			findViewById(R.id.buttonPrev).setEnabled(false);
			findViewById(R.id.buttonNext).setEnabled(false);
		}
	}

	@Override
	public void start() {

		super.start();

		dialog(true);
	}

	private void dialog(boolean init) {
		_Log.i(__CLASSNAME__, getMethodName() + init);

		if (init) {
			if (isChoir()) {
				findViewById(R.id.buttonPrev).setVisibility(View.GONE);
				findViewById(R.id.buttonNext).setVisibility(View.GONE);
			}
		}

		ImageButton buttonOpen = (ImageButton) findViewById(R.id.buttonOpen);
		buttonOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_Log.i(__CLASSNAME__, getMethodName() + v);

				String start = Environment.getExternalStorageDirectory() + "/Music";
				// openFileDialog(PlayViewFileDialog.this, root);

				int selectionMode = SelectionMode.MODE_OPEN_SINGLE;

				if (isLead()) {
					selectionMode = SelectionMode.MODE_OPEN_MULTI;
				}

				if (getActivity() != null) {
					((PlayActivity2) getActivity()).openFileDialog(selectionMode, PlayViewFileDialogMulti.this, start);
				}
			}
		});

		findViewById(R.id.buttonPrev).setEnabled(false);
		findViewById(R.id.buttonPrev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPlaying() && getCurrentTime() > 2 * ISongPlay.MSEC2SEC) {
					getPlayFragment().seek(0);
					return;
				}

				if (paths == null || paths.size() < 2) {
					return;
				}

				String path = getPath();

				index--;
				if (index < 0) {
					index = paths.size() - 1;
				}

				if (index > -1 && index < paths.size()) {
					path = paths.get(index);
				}

				_Log.e(__CLASSNAME__, getMethodName() + path);

				if (!TextUtil.isEmpty(path)) {

					setPath(path);

					// 리드곡은플래그먼트경로도바꾼다.
					if (isLead()) {
						getPlayFragment().setPath(path);
					}
				}

				stop();
				try {
					load(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.buttonNext).setEnabled(false);
		findViewById(R.id.buttonNext).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (paths == null || paths.size() < 2) {
					return;
				}

				String path = getPath();

				index++;
				if (index > paths.size() - 1) {
					index = 0;
				}

				if (index > -1 && index < paths.size()) {
					path = paths.get(index);
				}

				_Log.e(__CLASSNAME__, getMethodName() + path);

				if (!TextUtil.isEmpty(path)) {

					setPath(path);

					// 리드곡은플래그먼트경로도바꾼다.
					if (isLead()) {
						getPlayFragment().setPath(path);
					}
				}

				stop();
				try {
					load(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	int index = 0;

	public void putNextPath() {
		if (paths != null && paths.size() > 1) {
			String path = getPath();

			index++;
			if (index > paths.size() - 1) {
				index = 0;
			}

			if (index > -1 && index < paths.size()) {
				path = paths.get(index);
			}

			_Log.e(__CLASSNAME__, getMethodName() + path);

			if (!TextUtil.isEmpty(path)) {

				setPath(path);

				// 리드곡은플래그먼트경로도바꾼다.
				if (isLead()) {
					getPlayFragment().setPath(path);
				}
			}
		}
	}

	@Override
	public void onCompletion() {

		putNextPath();
		super.onCompletion();
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);

		if (paths != null && paths.size() > 1) {
			findViewById(R.id.buttonPrev).setEnabled(enabled);
			findViewById(R.id.buttonNext).setEnabled(enabled);
		} else {
			findViewById(R.id.buttonPrev).setEnabled(false);
			findViewById(R.id.buttonNext).setEnabled(false);
		}
	}

}
