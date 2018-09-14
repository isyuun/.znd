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
 * project	:	Karaoke.PLAY4
 * filename	:	AsycTask.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.os
 *    |_ AsycTask.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.os;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 8. 26.
 * @version 1.0
 */
public class AsycTaskExcuter {

	// @SuppressWarnings("unchecked")
	// @SuppressLint("NewApi")
	// public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
	// asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	// else
	// asyncTask.execute(params);
	// }

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	public static <T> void executePriorityAsyncTask(PriorityAsyncTask<T, ?, ?> asyncTask, T... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		else
			asyncTask.execute(params);
	}
}
