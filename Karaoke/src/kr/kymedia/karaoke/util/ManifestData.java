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
 * project	:	Karaoke
 * filename	:	ManifestData.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.util
 *    |_ ManifestData.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author Rodrigo N. Carreras
 * @author isyoon
 * @since 2014. 3. 6.
 * @version 1.0
 */
public class ManifestData {

	private static Object readKey(Context context, String keyName) {
		try {
			ApplicationInfo appi = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);

			Bundle bundle = appi.metaData;
			Object value = bundle.get(keyName);

			return value;
		} catch (NameNotFoundException ex) {
			// Si el meta-data no existe retorno null
		}
		return null;
	}

	public static String getString(Context context, String keyName) {
		return (String) readKey(context, keyName);
	}

	public static int getInt(Context context, String keyName) {
		return (Integer) readKey(context, keyName);
	}

	public static Boolean getBoolean(Context context, String keyName) {
		return (Boolean) readKey(context, keyName);
	}

	public static Object get(Context context, String keyName) {
		return readKey(context, keyName);
	}

}
