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
 * filename	:	PlayViewFileDialog.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.view
 *    |_ PlayViewFileDialog.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.apps.PlayActivity2;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.util.TextUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.lamerman.isyoon.FileDialogFragmentBase;
import com.lamerman.isyoon.FileDialogFragmentBase.FileDialogOpener;
import com.lamerman.isyoon.SelectionMode;

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
public class PlayViewFileDialog extends PlayViewType implements FileDialogOpener {
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

	public PlayViewFileDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PlayViewFileDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewFileDialog(Context context) {
		super(context);
	}

	@Override
	public void onFileDialogResult(final int requestCode, int resultCode, final Intent data) {
		// Log.e(__CLASSNAME__, getMethodName() + data.getExtras());

		String path = getPath();

		if (resultCode == Activity.RESULT_OK) {
			path = data.getStringExtra(FileDialogFragmentBase.RESULT_PATH);

			if (requestCode == FileDialogFragmentBase.REQUEST_SAVE) {
				System.out.println("Saving..." + path);
			} else if (requestCode == FileDialogFragmentBase.REQUEST_LOAD) {
				System.out.println("Loading..." + path);
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			System.out.println("Cancel..." + path);
		}

		Log.e(__CLASSNAME__, getMethodName() + path + "\n" + data.getExtras());

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

	}

	@Override
	public void start() {

		super.start();

		dialog(true);
	}

	private void dialog(boolean init) {
		Log.i(__CLASSNAME__, getMethodName() + init);

		ImageButton buttonOpen = (ImageButton) findViewById(R.id.buttonOpen);
		buttonOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(__CLASSNAME__, getMethodName() + v);

				String start = Environment.getExternalStorageDirectory() + "/Music";
				// openFileDialog(PlayViewFileDialog.this, root);
				if (getActivity() != null) {
					((PlayActivity2) getActivity()).openFileDialog(SelectionMode.MODE_OPEN_SINGLE,
							PlayViewFileDialog.this, start);
				}
			}
		});
	}

}
