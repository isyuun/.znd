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
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	ActionBarActivity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.app
 *    |_ ActionBarActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.app;

import kr.kymedia.karaoke.util.Log;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

/**
 *
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @since 2013. 7. 1.
 * @version 1.0
 * @see
 */
public class AppCompatActivity extends android.support.v7.app.AppCompatActivity {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	private final Handler handler = new Handler();

	protected void removeCallbacks(Runnable r) {
		if (handler != null) {
			handler.removeCallbacks(r);
		}
	}

	protected void post(Runnable r) {
		if (handler != null) {
			handler.post(r);
		}
	}

	protected void postDelayed(Runnable r, long delayMillis) {
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	protected void openMain(Bundle extras, Uri data) {
		Log.e(__CLASSNAME__, getMethodName() + "Extras[" + extras + "] - Data[" + data + "]");

		Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());

		if (extras != null) {
			intent.putExtras(extras);
		}

		intent.setData(data);

		startActivity(intent);
		// startActivityForResult(launchIntent, requestCode);
	}

	Menu mMenu = null;

	protected MenuItem findMenuItem(int id) {
		if (mMenu == null) {
			return null;
		}

		MenuItem item = mMenu.findItem(id);
		return item;
	}

	public boolean setShowAsAction(int id, int actionEnum) {
		try {
			MenuItem item = findMenuItem(id);
			if (item == null) {
				return false;
			}
			MenuItemCompat.setShowAsAction(item, actionEnum);
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	protected MenuItem setOptionMenuItemVisible(MenuItem item, boolean visible) {

		// return null;
		try {
			item.setVisible(visible);
			item.setEnabled(visible);
			return item;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * <pre>
	 * 개샹바버튼을 항상보이도록 한다.
	 * </pre>
	 */
	private MenuItem setActionMenuItemVisible(MenuItem item, boolean visible) {
		MenuItem ret = setOptionMenuItemVisible(item, visible);
		setShowAsAction(item.getItemId(), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return ret;
	}

	public MenuItem setActionMenuItemVisible(int id, boolean visible) {

		try {
			MenuItem item = findMenuItem(id);
			if (item == null) {
				return null;
			}
			return setActionMenuItemVisible(item, visible);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

		supportInvalidateOptionsMenu();
	}

	/**
	 * <pre>
	 * 쓰지마!!!
	 * </pre>
	 */
	@Deprecated
	@Override
	public ActionBar getActionBar() {

		// return super.getActionBar();
		return null;
	}

}
