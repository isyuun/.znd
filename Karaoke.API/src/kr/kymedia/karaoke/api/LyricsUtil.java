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
 * filename	:	LycricsUtil.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ LycricsUtil.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.data.SongData;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * 
 * TODO NOTE:<br>
 * 
 * <pre>
 * 가사파일다운로더
 * skym이외파일은다받는다.
 * 재수좋았다...빙신아.
 * </pre>
 * 
 * @author isyoon
 * @since 2012. 3. 20.
 * @version 1.0
 * @see LyricsUtil.java
 */

public class LyricsUtil {
	static String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s()", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	/**
	 * <pre>
	 * skym이외파일은다받는다.
	 * 재수좋았다...빙신아.
	 * </pre>
	 * 
	 * @see #down(URL, String)
	 */
	static public String down(String url) throws Exception {
		return down(new URL(url));
	}

	/**
	 * <pre>
	 * skym이외파일은다받는다.
	 * 재수좋았다...빙신아.
	 * </pre>
	 * 
	 * @see #down(URL, String)
	 */
	static public String down(URL url) throws Exception {
		return down(url, _IKaraoke.SKYM_PATH + File.separator + "nnnnn.skym");
	}

	/**
	 * <pre>
	 * skym이외파일은다받는다.
	 * 재수좋았다...빙신아.
	 * </pre>
	 * 
	 * @see #down(URL, String)
	 */
	static public String down(String url, String path) throws Exception {
		return down(new URL(url), path);
	}

	/**
	 * <pre>
	 * skym이외파일은다받는다.
	 * 재수좋았다...빙신아.
	 * </pre>
	 */
	static public String down(URL url, String path) throws Exception {

		if (TextUtil.isEmpty(path)) {
			path = _IKaraoke.SKYM_PATH + File.separator + "nnnnn.skym";
		}

		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() " + "[START]");
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() " + url.toString());
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() " + path);

		byte[] buf = new byte[1024];

		SongData data = new SongData();

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(30 * 1000);
		con.setReadTimeout(30 * 1000);

		if (con.getResponseCode() != 200 && con.getResponseCode() != 206) {
			path = null;
			throw new Exception(con.getResponseMessage() + ":" + Integer.toString(con.getResponseCode()));
		}

		InputStream in = con.getInputStream();

		File d = null;
		File f = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		String dir = _IKaraoke.SKYM_PATH;

		if (!TextUtil.isEmpty(path.substring(0, path.lastIndexOf(File.separator)))) {
			if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() [CHECK]" + dir);
			dir = path.substring(0, path.lastIndexOf(File.separator));
		}

		d = new File(dir);
		if (!d.exists()) {
			d.mkdirs();
		}

		f = new File(path);
		if (f.exists()) {
			f.delete();
		}

		f.createNewFile();

		fos = new FileOutputStream(f);
		bos = new BufferedOutputStream(fos, 8192);

		long r = 0;
		int l = 0;

		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() [CHECK]");
		while ((l = in.read(buf)) != -1) {
			r += l;
			bos.write(buf, 0, l);
			// 가사부분확인(100K이상시)
			if (r > 100 * 1024 && data.load(path)) {
				break;
			}
		}
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() [CHECK]");

		bos.flush();

		if (fos != null)
			fos.close();
		if (bos != null)
			bos.close();

		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "down() [END]");
		// if (_IKaraoke.DEBUG)_Log.e(__CLASSNAME__, getMethodName() + url.toString());
		// if (_IKaraoke.DEBUG)_Log.e(__CLASSNAME__, getMethodName() + path);

		return path;
	}
}
