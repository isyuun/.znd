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
 * filename	:	FileCache2.java
 * author	:	isyoon
 *
 * <pre>
 * com.fedorvlasov.lazylist
 *    |_ FileCache2.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.util;

import java.io.File;
import java.security.MessageDigest;

import android.content.Context;
import android.os.Environment;

/**
 * 
 * TODO NOTE:<br>
 * 리스트고속이미지처리용
 * 
 * @author isyoon
 * @since 2012. 7. 18.
 * @version 1.0
 * @see FileCache2.java
 */
public class FileCache2 {

	protected File cacheDir;

	public FileCache2(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
			cacheDir = new File(EnvironmentUtils.getCachePath(context));
		} else {
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(Context context, String url) {
		File f = null;
		// //I identify images by hashcode. Not a perfect solution, good for the demo.
		// String filename = String.valueOf(url.hashCode());
		// //Another possible solution (thanks to grantland)
		// //String filename = URLEncoder.encode(url);
		// File f = new File(cacheDir, filename);
		try {
			MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
			mDigest.update(url.getBytes());
			final String cacheKey = TextUtil.bytesToHexString(mDigest.digest());
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				// f = new File(EnvironmentUtils.getCachePath(context) + "bitmap_" + cacheKey + ".tmp");
				f = new File(cacheDir, "bitmap_" + cacheKey + ".tmp");
			}
		} catch (Exception e) {
			// Oh well, SHA-1 not available (weird), don't cache bitmaps.
			e.printStackTrace();
		}
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}
