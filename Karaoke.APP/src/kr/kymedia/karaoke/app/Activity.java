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
 * project	:	Karaoke
 * filename	:	Activity.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.app
 *    |_ Activity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.app;

import kr.kymedia.karaoke.util._Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 4. 2.
 * @version 1.0
 */
public class Activity extends android.app.Activity {
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

	public Handler getHandler() {
		return handler;
	}

	protected void removeCallbacks(Runnable r) {
		if (handler != null) {
			handler.removeCallbacks(r);
		}
	}

	protected void post(Runnable r) {
		removeCallbacks(r);
		if (handler != null) {
			handler.post(r);
		}
	}

	protected void postDelayed(Runnable r, long delayMillis) {
		removeCallbacks(r);
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	@SuppressLint("InlinedApi")
	protected void openMain(Bundle extras, Uri data) {
		_Log.e(toString(), getMethodName() + "Extras[" + extras + "] - Data[" + data + "]");

		Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());

		if (extras != null) {
			intent.putExtras(extras);
		}

		intent.setData(data);

		startActivity(intent);
		// startActivityForResult(launchIntent, requestCode);
	}

}
