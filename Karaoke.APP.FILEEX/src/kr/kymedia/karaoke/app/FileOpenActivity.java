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
 * project	:	Karaoke.APP.TEST
 * filename	:	_Activity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app.play
 *    |_ _Activity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.app;

import java.io.File;
import java.util.ArrayList;

import kr.kymedia.karaoke.util._Log;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lamerman.isyoon.FileDialogFragmentBase;
import com.lamerman.isyoon.FileDialogFragmentBase.FileDialogOpener;
import com.lamerman.isyoon.FileDialogFragmentMulti;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 7. 11.
 * @version 1.0
 */
public class FileOpenActivity extends AppCompatFragmentActivity implements FileDialogOpener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	public ArrayList<File> getListFiles(File root) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			} else {
				if (file.isFile()) {
					inFiles.add(file);
				}
			}
		}

		return inFiles;
	}

	public String getFilePath(String root, String name) {
		// _Log.e(__CLASSNAME__, getMethodName() + root);

		String path = "";
		ArrayList<File> inFiles = getListFiles(new File(root));
		for (File file : inFiles) {
			// _Log.d(__CLASSNAME__, file.getParent() + "/" + file.getName());
			if (name.equalsIgnoreCase(file.getName())) {
				path = file.getPath();
				break;
			}
		}
		_Log.e(__CLASSNAME__, getMethodName() + path);
		return path;
	}

	FileDialogFragmentMulti mFileDialogfragment;

	public void openFileDialog(int selectionMode, FileDialogOpener opener, String start) {
		_Log.e(__CLASSNAME__, getMethodName() + start);

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		ft.commit();

		// Create and show the dialog.
		Bundle args = new Bundle();
		args.putString(FileDialogFragmentBase.START_PATH, start);

		// can user select directories or not
		args.putBoolean(FileDialogFragmentBase.CAN_SELECT_DIR, true);

		// alternatively you can set file filter
		args.putStringArray(FileDialogFragmentBase.FORMAT_FILTER, new String[] { "wav", "mp3", "ogg", "m4a" });

		// You can add one more parameter to the Intent: SelectionMode.MODE_OPEN or SelectionMode.MODE_CREATE
		args.putInt(FileDialogFragmentBase.SELECTION_MODE, selectionMode);

		args.putInt(FileDialogFragmentBase.REQUEST_CODE, FileDialogFragmentBase.REQUEST_LOAD);

		mFileDialogfragment = new FileDialogFragmentMulti(opener);
		mFileDialogfragment.setArguments(args);
		mFileDialogfragment.setRetainInstance(true);
		mFileDialogfragment.show(getSupportFragmentManager(), "dialog");

	}

	protected String path = "";

	@Override
	public void onFileDialogResult(final int requestCode, int resultCode, final Intent data) {
		// _Log.e(__CLASSNAME__, getMethodName() + data.getExtras());

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

		_Log.e(__CLASSNAME__, getMethodName() + path + "\n" + data.getExtras());
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

	}

}
