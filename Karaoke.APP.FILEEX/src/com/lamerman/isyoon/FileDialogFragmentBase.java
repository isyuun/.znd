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
 * project	:	Karaoke.FileExplorer
 * filename	:	FileDialogFragmentImpl.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app
 *    |_ FileDialogFragmentImpl.java
 * </pre>
 * 
 */

package com.lamerman.isyoon;

import kr.kymedia.karaoke.widget.FileAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 9. 4.
 * @version 1.0
 */
public class FileDialogFragmentBase extends FileDialogFragment implements
		DialogInterface.OnKeyListener {

	public static final String REQUEST_CODE = "REQUEST_CODE";

	public static int REQUEST_SAVE = Activity.RESULT_FIRST_USER + 1;
	public static int REQUEST_LOAD = Activity.RESULT_FIRST_USER + 2;

	private final FileDialogOpener opener;

	public interface FileDialogOpener {
		public void onFileDialogResult(final int requestCode, int resultCode, final Intent data);
	}

	public FileDialogFragmentBase(FileDialogOpener opener) {
		super();
		this.opener = opener;
	}

	@Override
	public void setResult(int resultCode, Intent data) {
		// Log.e(toString(), getMethodName() + resultCode + ":" + data);


		super.setResult(resultCode, data);

		int requestCode = getIntent().getIntExtra(REQUEST_CODE, REQUEST_LOAD);

		// ((_Activity) getActivity()).onFileDialogResult(requestCode, resultCode, data);
		if (opener != null) {
			opener.onFileDialogResult(requestCode, resultCode, data);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		// return super.onCreateView(inflater, container, savedInstanceState);
		root = inflater.inflate(R.layout.file_dialog_main, container, false);
		// Log.e(toString(), getMethodName() + root);
		return root;
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Log.i(toString(), getMethodName());

		// return super.onCreateDialog(savedInstanceState);
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	protected void onActivityCreated() {

		super.onActivityCreated();

		// 시작위치로
		String startPath = getIntent().getStringExtra(START_PATH);
		startPath = startPath != null ? startPath : ROOT;
		final Button retButton = (Button) findViewById(R.id.fdButtonRet);
		retButton.setText(startPath);

		retButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String startPath = getIntent().getStringExtra(START_PATH);
				getDir(startPath);
			}
		});

		layoutSelect.setVisibility(View.GONE);

		// 닫기버튼
		findViewById(R.id.fdButtonClose).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
			}
		});

		// 검색기능
		((EditText) findViewById(R.id.fdEditTextFileSearch)).addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				((FileAdapter) getListAdapter()).getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});

		// 키보드숨기기
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// 백키막기
		getDialog().setOnKeyListener(this);
	}

	public boolean onBackPressed() {

		if (!getCurrentPath().equals(ROOT) && !getCurrentPath().equals(getStartPath())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		// Log.e(toString(), getMethodName() + dialog + keyCode + event);


		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (onBackPressed()) {
				onKeyDown(keyCode, event);
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		((EditText) findViewById(R.id.fdEditTextFileSearch)).setText("");
	}

}
