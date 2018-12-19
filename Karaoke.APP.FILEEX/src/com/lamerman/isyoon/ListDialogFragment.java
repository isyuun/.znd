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
 * filename	:	_DialogFragment.java
 * author	:	isyoon
 *
 * <pre>
 * com.lamerman.isyoon
 *    |_ _DialogFragment.java
 * </pre>
 * 
 */

package com.lamerman.isyoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
public class ListDialogFragment extends DialogFragment implements OnItemClickListener,
		OnItemLongClickListener, OnItemSelectedListener {
	@Override
	public String toString() {

		//return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		//int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		//name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	static int RESULT_OK = Activity.RESULT_OK;
	static int RESULT_CANCELED = Activity.RESULT_CANCELED;

	static String INPUT_METHOD_SERVICE = Activity.INPUT_METHOD_SERVICE;

	public ListDialogFragment() {
		super();
	}

	protected int getResource(String name, String defType) {
		try {
			return getResources().getIdentifier(name, defType, getActivity().getPackageName());
		} catch (Exception e) {

			//e.printStackTrace();
			return 0;
		}
	}

	public View findViewById(int id) {
		//_Log.e(toString(), getMethodName() + view.findViewById(id));
		if (root != null) {
			return root.findViewById(id);
		} else {
			if (getActivity() != null) {
				return getActivity().findViewById(id);
			} else {
				return null;
			}
		}
	}

	public View findViewById(String name) {
		//_Log.e(toString(), getMethodName() + name + ":" + view.findViewById(getResource(name, "id")));
		if (root != null) {
			return root.findViewById(getResource(name, "id"));
		} else {
			if (getActivity() != null) {
				return getActivity().findViewById(getResource(name, "id"));
			} else {
				return null;
			}
		}
	}

	Intent intent;

	@Override
	public void setArguments(Bundle args) {

		super.setArguments(args);
		intent = new Intent();
		intent.putExtras(args);
	}

	public Intent getIntent() {
		if (intent != null) {
			return intent;
		} else {
			if (getActivity() != null) {
				return getActivity().getIntent();
			} else {
				return null;
			}
		}
	}

	public Context getApplicationContext() {
		if (getActivity() != null) {
			return getActivity().getApplicationContext();
		} else {
			return null;
		}
	}

	/**
	 * <pre>
	 * 일단암것도안해~~~
	 * </pre>
	 */
	public void setContentView(int layoutResID) {

	}

	public void setResult(int resultCode, Intent data) {
		if (getActivity() != null) {
			getActivity().setResult(resultCode, data);
		}
	}

	public void finish() {
		dismiss();
	}

	public ListView getListView() {
		return ((ListView) findViewById(android.R.id.list));
	}

	public void setListAdapter(ListAdapter adapter) {
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);
		getListView().setOnItemSelectedListener(this);
	}

	public BaseAdapter getListAdapter() {
		return (BaseAdapter) getListView().getAdapter();
	}

	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		root = super.onCreateView(inflater, container, savedInstanceState);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		onActivityCreated();
	}

	protected void onActivityCreated() {
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		Log.e(toString(), getMethodName() + activity);
	}

	@Override
	public void onDetach() {

		super.onDetach();
		Log.e(toString(), getMethodName());
	}

	@Override
	public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//_Log.i(toString(), getMethodName() + position + "-" + view.isSelected());


		//if (view instanceof CheckableSelectableRelativeLayout) {
		//	boolean checked = ((CheckableSelectableRelativeLayout) view).isChecked();
		//	((CheckableSelectableRelativeLayout) view).setSelected(checked);
		//	//_Log.e(toString(), getMethodName() + position + "-" + view.isSelected() + ":" + checked);
		//}

		onListItemClick((ListView) parent, view, position, id);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		//_Log.i(toString(), getMethodName() + position + "-" + v.isSelected());

		//if (v instanceof CheckableSelectableRelativeLayout) {
		//	boolean checked = ((CheckableSelectableRelativeLayout) v).isChecked();
		//	((CheckableSelectableRelativeLayout) v).setSelected(checked);
		//	//_Log.e(toString(), getMethodName() + position + "-" + v.isSelected() + ":" + checked);
		//}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		Log.i(toString(), getMethodName() + position + "-" + view.isSelected());

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

		Log.i(toString(), getMethodName());

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(toString(), getMethodName() + position + "-" + view.isSelected());

		return true;
	}

}
