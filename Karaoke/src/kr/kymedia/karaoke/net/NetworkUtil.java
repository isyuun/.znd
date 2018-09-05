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
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	NetworkUtil.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.network
 *    |_ NetworkUtil.java
 * </pre>
 */
package kr.kymedia.karaoke.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-04-22
 */
public class NetworkUtil {

	public static int getType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		if (null != activeNetwork) {
			boolean isConnected = activeNetwork.isConnectedOrConnecting();

			if (isConnected) {
				return activeNetwork.getType();
			}
		}

		return -1;
	}

	public static String getTypeName(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		if (null != activeNetwork) {
			boolean isConnected = activeNetwork.isConnectedOrConnecting();

			if (isConnected) {
				return activeNetwork.getTypeName();
			}
		}

		return "";
	}
}
