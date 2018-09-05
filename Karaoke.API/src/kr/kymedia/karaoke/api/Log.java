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
 * filename	:	Log.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.util
 *    |_ Log.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import kr.kymedia.karaoke._IKaraoke;

/**
 *
 * TODO
 * NOTE:전문API로그용
 *
 * @author isyoon
 * @since 2012. 5. 4.
 * @version 1.0
 * @see IKaraoke2#DEBUG
 */
public class Log {

	private static boolean enable = _IKaraoke.DEBUG;

	// public Log() {
	// // TODO Auto-generated constructor stub
	// }

	/**
	 * @return the enable
	 */
	public static boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable
	 *          the enable to set
	 */
	public static void setEnable(boolean enable) {
		Log.enable = enable;
	}

	public static int v(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.v(tag, msg);
		} else {
			return 0;
		}
	}

	public static int v(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.v(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.d(tag, msg);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.d(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.i(tag, msg);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.i(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.w(tag, msg);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.w(tag, msg, tr);
		} else {
			return 0;
		}
	}

	// public static native boolean isLoggable(String s, int j);

	public static int w(String tag, Throwable tr)
	{
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.w(tag, tr);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.e(tag, msg);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			return android.util.Log.v(tag, msg, tr);
		} else {
			return 0;
		}
	}

	public static int wtf(String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			if (_IKaraoke.IS_ABOVE_HONEYCOMB) {
				return android.util.Log.e(tag, msg);
			} else {
				return android.util.Log.e(tag, msg);
			}
		} else {
			return 0;
		}
	}

	public static int wtf(String tag, String[] msg) {
		if (msg == null) {
			return 0;
		}

		String txt = "";

		int idx = 0;
		for (String key : msg) {
			txt += key + ",";
			if (idx == msg.length-1) {
				txt += key;
			}
			idx++;
		}

		//if (_IKaraoke.DEBUG && enable) {
		//	if (_IKaraoke.IS_ABOVE_HONEYCOMB) {
		//		return android.util.Log.e(tag, msg);
		//	} else {
		//		return android.util.Log.e(tag, msg);
		//	}
		//} else {
		//	return 0;
		//}
		return android.util.Log.wtf(tag, txt);
	}

	public static int wtf(String tag, Throwable tr)
	{
		if (_IKaraoke.DEBUG && enable) {
			if (_IKaraoke.IS_ABOVE_HONEYCOMB) {
				return android.util.Log.wtf(tag, tr);
			} else {
				return android.util.Log.wtf(tag, tr.getMessage());
			}

		} else {
			return 0;
		}
	}

	public static int wtf(String tag, String msg, Throwable tr)
	{
		if (msg == null) {
			msg = "(null)";
		}
		if (_IKaraoke.DEBUG && enable) {
			if (_IKaraoke.IS_ABOVE_HONEYCOMB) {
				return android.util.Log.e(tag, msg, tr);
			} else {
				return android.util.Log.e(tag, msg, tr);
			}
		} else {
			return 0;
		}
	}

	public static String getStackTraceString(Throwable tr)
	{
		return android.util.Log.getStackTraceString(tr);
	}

	public static int println(int priority, String tag, String msg)
	{
		if (msg == null) {
			msg = "(null)";
		}
		return android.util.Log.println(priority, tag, msg);
	}

	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;

	public static int _LOG(String... txt) {
		if (txt == null) {
			return 0;
		}

		String tag = txt[0];
		String msg = "";

		int idx = 0;
		for (String key : txt) {
			if (idx > 0) {
				if (idx == txt.length - 1) {
					msg += key;
				} else {
					msg += key + " : ";
				}
			}
			idx++;
		}

		return android.util.Log.wtf(tag, msg);
	}

}
