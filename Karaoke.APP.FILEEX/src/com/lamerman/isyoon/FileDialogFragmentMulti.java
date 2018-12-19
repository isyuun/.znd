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
 * filename	:	_FileDialogFragment.java
 * author	:	isyoon
 *
 * <pre>
 * com.lamerman.isyoon
 *    |_ _FileDialogFragment.java
 * </pre>
 * 
 */

package com.lamerman.isyoon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import kr.kymedia.karaoke.view.CheckableSelectableRelativeLayout;
import kr.kymedia.karaoke.widget.FileAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 8. 19.
 * @version 1.0
 */
public class FileDialogFragmentMulti extends FileDialogFragmentBase {

	public static final String RESULT_PATHS = "RESULT_PATHS";

	/**
	 * path아이다~~~
	 */
	ArrayList<String> paths = new ArrayList<String>();

	public FileDialogFragmentMulti(FileDialogOpener opener) {
		super(opener);
	}

	Button newButton;

	@SuppressLint("InlinedApi")
	@Override
	protected void onActivityCreated() {
		Log.i(toString(), getMethodName());

		super.onActivityCreated();

		newButton = (Button) findViewById(R.id.fdButtonNew);

		setSelectionMode();

		// WidgetUtils.setTextViewMarquee((TextView) findViewById(R.id.fdButtonRet));

		// if (selectionMode == SelectionMode.MODE_OPEN_MULTI) {
		// findViewById(R.id.fdButtonCheckShow).setVisibility(View.VISIBLE);
		// } else {
		// findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);
		// }
		findViewById(R.id.fdButtonCheckHide).setVisibility(View.GONE);
		findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);

		// 숨기기
		findViewById(R.id.fdButtonCheckHide).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(toString(), getMethodName() + v);

				findViewById(R.id.fdButtonCheckShow).setVisibility(View.VISIBLE);
				findViewById(R.id.fdButtonCheckHide).setVisibility(View.GONE);

				setCheck2All(false);
				setSelectable(false);
			}
		});

		findViewById(R.id.fdButtonCheckHide).setLongClickable(true);
		findViewById(R.id.fdButtonCheckHide).setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.i(toString(), getMethodName() + v);

				setCheck2All(false);
				return true;
			}
		});

		// 보이기
		findViewById(R.id.fdButtonCheckShow).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(toString(), getMethodName() + v);

				findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);
				findViewById(R.id.fdButtonCheckHide).setVisibility(View.VISIBLE);

				setSelectable(true);
				setCheck2All(false);
			}
		});

		findViewById(R.id.fdButtonCheckShow).setLongClickable(true);
		findViewById(R.id.fdButtonCheckShow).setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.i(toString(), getMethodName() + v);

				findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);
				findViewById(R.id.fdButtonCheckHide).setVisibility(View.VISIBLE);

				setSelectable(true);
				setCheck2All(true);
				return true;
			}
		});
	}

	private void setCheck2All(boolean check) {
		// _Log.e(toString(), getMethodName() + check);

		paths.clear();

		((FileAdapter) getListAdapter()).setNotifyOnChange(false);

		for (int i = 0; i < getListAdapter().getCount(); i++) {
			setCheck(i, check, false);
		}

		((FileAdapter) getListAdapter()).setNotifyOnChange(true);
		((FileAdapter) getListAdapter()).notifyDataSetChanged();

		// _Log.e(toString(), getMethodName() + paths);
	}

	private void setCheck(int position, boolean check, boolean refresh) {
		// _Log.e(toString(), getMethodName() + position + "-" + check);

		if (refresh) {
			((FileAdapter) getListAdapter()).setNotifyOnChange(false);
		}

		((FileAdapter) getListAdapter()).setChecked(position, check);

		HashMap<String, Object> item = ((FileAdapter) getListAdapter()).getItem(position);
		Integer res = (Integer) item.get(SelectionItem.ITEM_IMAGE);

		if (res == R.drawable.file) {
			if (check) {
				paths.add((String) item.get(SelectionItem.ITEM_PATH));
			} else {
				paths.remove(item.get(SelectionItem.ITEM_PATH));
			}
		}

		if (refresh) {
			((FileAdapter) getListAdapter()).setNotifyOnChange(true);
			((FileAdapter) getListAdapter()).notifyDataSetChanged();
		}

		// _Log.e(toString(), getMethodName() + paths);
	}

	private void setSelectionMode() {
		Log.w(toString(), getMethodName() + selectionMode);

		// 일단먹구들어간다.
		getListView().setItemsCanFocus(true);
		newButton.setVisibility(View.GONE);
		layoutSelect.setVisibility(View.GONE);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		switch (selectionMode) {
		case SelectionMode.MODE_CREATE:
			newButton.setVisibility(View.VISIBLE);
			layoutSelect.setVisibility(View.VISIBLE);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			break;

		case SelectionMode.MODE_OPEN_SINGLE:
			newButton.setVisibility(View.GONE);
			layoutSelect.setVisibility(View.GONE);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			break;

		case SelectionMode.MODE_OPEN_MULTI:
			newButton.setVisibility(View.GONE);
			layoutSelect.setVisibility(View.GONE);
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			break;

		default:
			break;
		}

	}

	boolean isSelectable() {
		boolean ret = ((FileAdapter) getListAdapter()).isSelectable();
		return ret;
	}

	void setSelectable(boolean selectable) {
		if (selectionMode == SelectionMode.MODE_OPEN_MULTI) {
			((FileAdapter) getListAdapter()).setSelectable(selectable);
		} else {
			((FileAdapter) getListAdapter()).setSelectable(false);
		}

		newButton.setVisibility(View.GONE);

		if (isSelectable()) {
			layoutSelect.setVisibility(View.VISIBLE);
			findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);
			findViewById(R.id.fdButtonCheckHide).setVisibility(View.VISIBLE);
		} else {
			layoutSelect.setVisibility(View.GONE);
			findViewById(R.id.fdButtonCheckShow).setVisibility(View.VISIBLE);
			findViewById(R.id.fdButtonCheckHide).setVisibility(View.GONE);
		}
		findViewById(R.id.fdButtonCheckHide).setVisibility(View.GONE);
		findViewById(R.id.fdButtonCheckShow).setVisibility(View.GONE);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// return super.onItemLongClick(parent, view, position, id);
		// _Log.i(toString(), getMethodName() + getPath().get(position));


		Log.i(toString(), getMethodName() + isSelectable());

		if (getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE
				|| getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
			setSelectable(!isSelectable());
			if (isSelectable()) {
				getListView().performItemClick(view, position, id);
			} else {
				setCheck2All(false);
			}
		}

		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// _Log.i(toString(), getMethodName() + getPath().get(position));

		super.onListItemClick(l, v, position, id);

		File file = getSelectedFile();
		// _Log.e(toString(), getMethodName() + isSelectable() + ":" + file.isFile() + "-" + file.getPath());

		if (file.isFile()) {
			if (isSelectable()) {
				setCheck(position, !((CheckableSelectableRelativeLayout) v).isChecked(), true);
			} else {
				selectButton.performClick();
			}
		} else {
			setCheck2All(false);
		}

		if (isSelectable()) {
			layoutSelect.setVisibility(View.VISIBLE);
		} else {
			layoutSelect.setVisibility(View.GONE);
		}
		// _Log.e(toString(), getMethodName() + v.isSelected() + ":" + paths);
	}

	@Override
	public void setResult(int resultCode, Intent data) {
		Log.e(toString(), getMethodName() + paths);


		getIntent().putStringArrayListExtra(RESULT_PATHS, paths);
		// _Log.e(toString(),
		// getIntent().getExtras().getStringArrayList(FileDialogFragmentMulti.RESULT_PATHS));

		super.setResult(resultCode, data);
	}

	@Override
	public boolean onBackPressed() {

		if (isSelectable()) {
			setSelectable(false);
			return true;
		} else {
			return super.onBackPressed();
		}
	}

}
