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
 * filename	:	TextUtil.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ TextUtil.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author isyoon
 * @since 2012. 3. 16.
 * @version 1.0
 */

@SuppressLint("DefaultLocale")
public class TextUtil {

	public static boolean isNull(String str) {

		boolean ret = true;
		if (str != null && !("null").equalsIgnoreCase(str)) {
			ret = false;
		}
		return ret;
	}

	public static boolean isNotNull(String str) {

		return !isNull(str);
	}

	public static boolean isEmpty(CharSequence str) {

		return (TextUtils.isEmpty(str) || isNull(str.toString()));
	}

	public static boolean isNetworkUrl(final String url) {
		try {
			if (isEmpty(url)) {
				return false;
			}
			// new URL(url);
			return URLUtil.isNetworkUrl(url);
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	public static boolean isMarketUrl(String url) {
		if (isEmpty(url)) {
			return false;
		}
		boolean ret = false;
		try {
			if (url.contains("market://")) {
				ret = true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public static String encNetworkFileUrl(final String url) {
		String ret = url;
		// try {
		// String filename = ret.substring(ret.lastIndexOf("/")+1);
		// String filename_enc = URLEncoder.encode(filename, "UTF-8");
		// ret = ret.replace(filename, filename_enc);
		// } catch (Exception e) {
		//
		// Log.e("[encNetworkFileUrl]", Log.getStackTraceString(e));
		// };
		// 일단은 공백만 처리해 본다.
		ret = ret.replace(" ", "%20");
		return ret;
	}

	// public static String getRealPathFromURI(Activity activity, Uri uri) {
	// if (uri == null) {
	// return null;
	// }
	// String ret = "";
	// try {
	// String[] proj = { MediaStore.Images.Media.DATA };
	// Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
	// int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// ret = cursor.getString(column_index);
	// } catch (Exception e) {
	//
	// Log.e("[getRealPathFromURI]", Log.getStackTraceString(e));
	// }
	// return ret;
	// }

	public static String getRealPathFromURI(ContentResolver contentresolver, Uri uri) {
		if (uri == null) {
			return null;
		}
		String ret = "";
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = contentresolver.query(uri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			ret = cursor.getString(column_index);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	// [출처] JAVA 문자열이 한글인지 영문인지 알아내는 법|작성자 낭만며루치
	public static boolean isNotAlphaNumeric(final String word) {
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
			if (block != Character.UnicodeBlock.ALPHABETIC_PRESENTATION_FORMS
					&& block != Character.UnicodeBlock.NUMBER_FORMS) {
				return true;
			}
		}
		return false;
	}

	// 영문만 허용
	protected InputFilter filterAlpha = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
				int dend) {

			Pattern ps = Pattern.compile("^[a-zA-Z]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};

	// 영문만 허용 (숫자 포함)
	protected InputFilter filterAlphaNum = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
				int dend) {

			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};

	// 한글만 허용
	public InputFilter filterKor = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
				int dend) {

			Pattern ps = Pattern.compile("^[ㄱ-가-힣]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};

	public static boolean checkParameter(String pattern, String str) {

		boolean okPattern = false;
		String patternRegex = null;

		pattern = pattern.trim().toLowerCase();
		str = str.trim();

		// 숫자
		if ("num".equals(pattern)) {
			patternRegex = "^[0-9]*$";

			// 숫자포함
		} else if ("num_inc".equals(pattern)) {
			patternRegex = ".*[\\d].*";

			// 영어
		} else if ("eng".equals(pattern)) {
			patternRegex = "^[a-zA-Z]*$";

			// 영어포함
		} else if ("eng_inc".equals(pattern)) {
			patternRegex = ".*[a-zA-Z].*";

			// 한글
		} else if ("kor".equals(pattern)) {
			patternRegex = "^[ㄱ-ㅎ가-힣]*$";

			// 영숫자
		} else if ("eng_num".equals(pattern) || "num_eng".equals(pattern)) {
			patternRegex = "^[a-zA-Z0-9]*$";

			// 한숫자
		} else if ("kor_num".equals(pattern) || "num_kor".equals(pattern)) {
			patternRegex = "^[ㄱ-ㅎ가-힣0-9]*$";

			// 이메일
		} else if ("email".equals(pattern)) {
			// patternRegex = "^[_.0-9a-zA-Z-]+@[0-9a-zA-Z]+(.[_0-9a-zA-Z-]+)*$";
			patternRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+";
			// patternRegex = "[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
			// + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+";
			// 핸드폰
		} else if ("hp".equals(pattern)) {
			patternRegex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";

			// 전화
		} else if ("tel".equals(pattern)) {
			patternRegex = "^\\d{2,3}-\\d{3,4}-\\d{4}$";

			// 주민번호
		} else if ("ssn".equals(pattern)) {
			patternRegex = "^\\d{7}-[1-4]\\d{6}";

			// 아이피
		} else if ("ip".equals(pattern)) {
			patternRegex = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";

			// 일월년
		} else if ("date".equals(pattern)) {
			patternRegex = "([0-9]{1,2})\\.([0-9]{1,2})\\.([0-9]{4})";

		}

		okPattern = Pattern.matches(patternRegex, str);

		return okPattern;
	}

	// [출처] [자바스크립트 정규식] 비밀번호 체크, 영문 대문자 / 소문자 / 숫자 반드시 포함하여 8-20글자로 구성 |작성자 참새애인
	public static boolean checkPwd(String pwd) {
		boolean check = true;

		pwd = pwd.trim();

		// //Pattern p = Pattern.compile("^[a-zA-Z0-9]{4,12}$");
		// Pattern p = Pattern.compile("^[a-zA-Z0-9]{4,}$");
		// Matcher m = p.matcher(pwd);
		// check &= m.matches();
		//

		check |= TextUtil.checkParameter("num_inc", pwd);
		check |= TextUtil.checkParameter("eng_inc", pwd);

		return check;
	}

	/**
	 * formatMoney
	 * 
	 * @param str
	 * @return String
	 */
	public static String numberFormat(final String str) {
		try {
			String ret = str;
			ret = ret.replace(",", "");
			if (TextUtil.isEmpty(str)) {
				ret = "0";
			} else {
				// double num = (new Double(str)).doubleValue();
				double num = Double.valueOf(str);
				// java.text.DecimalFormat df = new java.text.DecimalFormat("###,###,###,###,###,###,###");
				DecimalFormat df = new DecimalFormat("#,###.##");
				ret = df.format(num);
			}
			return ret;
		} catch (Exception e) {

			// Log.e("[numberFormat]", Log.getStackTraceString(e));
			return str;
		}
	}

	public static String unNumberFormat(final String str) {
		try {
			String ret = str;
			ret = ret.replace(",", "");
			return ret;
		} catch (Exception e) {

			// Log.e("[unNumberFormat]", Log.getStackTraceString(e));
			return str;
		}
	}

	public static String getMD5Hash(String s) {
		MessageDigest m = null;
		String hash = null;

		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public static String md5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 인티저파스 : 기본:0
	 */
	public static int parseInt(String s) {
		int ret = 0;
		try {
			if (!TextUtil.isEmpty(s)) {
				ret = Integer.parseInt(s);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 인티저파스
	 */
	public static int parseInt(String s, int d) {
		int ret = d;
		try {
			if (!TextUtil.isEmpty(s)) {
				ret = Integer.parseInt(s);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 불리언파스
	 */
	public static boolean parseBoolean(String s, boolean d) {
		boolean ret = d;
		try {
			if (!TextUtil.isEmpty(s)) {
				ret = Boolean.parseBoolean(s);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public static boolean isNumeric(String string) {
		boolean ret = false;

		try {
			if (!TextUtil.isEmpty(string)) {
				Integer.parseInt(string);
				ret = true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public static String replacePwd(String pwd) {
		String ret = "";

		if (pwd == null) {
			return ret;
		}

		for (int i = 0; i < pwd.length(); i++) {
			ret += "*";
		}

		return ret;
	}

	// public static String getValueByName(Class cls, int value) {
	// String ret = "";
	// try {
	// Field f = cls.getDeclaredField("MY_KEYCODE_01");
	// Log.d(TAG, f.toString());
	// value = f.getInt(null);
	// } catch (Exception e) {
	//
	// Log.e("[getValueByName]", Log.getStackTraceString(e));
	// }
	//
	// Log.d(TAG, "value = " + value);
	// return ret;
	// }

	public static String getDecimalFormat(String format, long num) {
		String ret = "";
		try {
			// "#,##0"
			DecimalFormat df = new DecimalFormat(format);
			ret = df.format(num);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public static String getDecimalFormat(String format, double num) {
		String ret = "";
		try {
			// "#,##0"
			DecimalFormat df = new DecimalFormat(format);
			ret = df.format(num);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}

	public static String getFileExtension(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}

	public static ArrayList<String> retrieveLinks(String text) {
		ArrayList<String> links = new ArrayList<String>();

		// String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
		// Pattern p = Pattern.compile(regex);
		Pattern p = Patterns.WEB_URL;
		Matcher m = p.matcher(text);
		while (m.find()) {
			String urlStr = m.group();

			if (urlStr.startsWith("(") && urlStr.endsWith(")")) {

				char[] stringArray = urlStr.toCharArray();

				char[] newArray = new char[stringArray.length - 2];
				System.arraycopy(stringArray, 1, newArray, 0, stringArray.length - 2);
				urlStr = new String(newArray);
				// System.out.println("Finally Url =" + newArray.toString());

			}
			// System.out.println("...Url..." + urlStr);
			links.add(urlStr);
		}
		return links;
	}

	public static String retrieveHtmls(String text) {
		ArrayList<String> urls = TextUtil.retrieveLinks(text);

		for (String url : urls) {
			text = text.replace(url, "<a href='" + url + "'>" + url + "</a>");
		}

		return text;
	}

	public static boolean isSimilarString(String src, String dst, int max) {
		boolean ret = false;
		String[] srcs = src.toLowerCase().split(" ");
		String[] dsts = dst.toLowerCase().split(" ");
		String tgt;

		if (dsts.length > srcs.length) {
			tgt = dst.toLowerCase();
		} else {
			tgt = src.toLowerCase();
		}

		if (max < 0) {
			max = (int) (dsts.length * 0.6);
		}

		int count = 0;
		for (String string : (dsts.length > srcs.length ? srcs : dsts)) {
			if (tgt.contains(string)) {
				count++;
			}

			if (count > max) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	public static ArrayList<String> getFlagsNames(String lcode, ArrayList<String> flags) {
		ArrayList<String> names = new ArrayList<String>();

		for (int i = 0; i < flags.size(); i++) {
			String ncode = flags.get(i);
			Locale locale = new Locale(lcode, ncode);
			String name = locale.getDisplayCountry();
			names.add(name);
		}

		return names;
	}

	static public String getTimeTrackerString(int msec) {
		String ret = String
				.format("%02d:%02d", Math.abs(msec) / 60000, (Math.abs(msec) % 60000) / 1000);

		if (msec < 0) {
			ret = "-" + ret;
		}

		return ret;
	}

	static public String getTimeTrackerString(String format, int msec, boolean isHmsec) {
		String ret = String
				.format("%02d:%02d", Math.abs(msec) / 60000, (Math.abs(msec) % 60000) / 1000);

		if (isHmsec) {
			ret = String.format("%02d.%02d", (Math.abs(msec) % 60000) / 1000,
					(Math.abs(msec) % 1000) / 10);
		}

		if (msec < 0) {
			ret = "-" + ret;
		}

		if (!TextUtil.isEmpty(format)) {
			ret = String.format(format, ret);
		}

		return ret;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	static public void copyText2Clipboard(Context context, CharSequence text) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData.newPlainText("comment", text);
				clipboard.setPrimaryClip(clip);
			} else {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(text);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static String getMimeTypeFromFile(String file) {
		// Log.i("[MIME]", "" + file);

		String fileExtension = getFileExtension(file);
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

		Log.i("[MIME]", "getMimeType() " + mimeType);

		return mimeType;
	}

	/**
	 * <a href=
	 * "http://stackoverflow.com/questions/5795576/android-content-type-error-using-action-view-with-a-local-file"
	 * >Android: Content type error using ACTION_VIEW with a local file</a>
	 */
	public static String getMimeTypeFromFile(File file) {
		// Log.i("[MIME]", "" + file);

		// Get the file path
		Uri path = Uri.fromFile(file);
		MimeTypeMap type_map = MimeTypeMap.getSingleton();
		// Get the extension from the path
		String extension = MimeTypeMap.getFileExtensionFromUrl(path.toString());
		extension = extension.toLowerCase();
		if (extension.contains(".")) {
			extension = extension.substring(extension.lastIndexOf("."));
		}
		String mime_type = type_map.getMimeTypeFromExtension(extension);

		Log.i("[MIME]", "getMimeTypeFromFile() " + mime_type);

		return mime_type;
	}

	public static String getMimeTypeFromUrl(String url) {
		// Log.i("[MIME]", "" + url);

		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}

		Log.i("[MIME]", "getMimeType() " + type);

		return type;
	}

	// /**
	// * <a href="http://stackoverflow.com/questions/7664315/android-detect-url-mime-type">Android - Detect URL mime type?</a>
	// * @param url
	// * @return
	// */
	// public static String getMimeTypeFromUrl(String url)
	// {
	// Log.i("[MIME]", "" + url);
	//
	// String mimeType = "";
	//
	// // this is to handle call from main thread
	// StrictMode.ThreadPolicy prviousThreadPolicy = StrictMode.getThreadPolicy();
	//
	// // temporary allow network access main thread
	// // in order to get mime type from content-type
	//
	// StrictMode.ThreadPolicy permitAllPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	// StrictMode.setThreadPolicy(permitAllPolicy);
	//
	// try
	// {
	// URLConnection connection = new URL(url).openConnection();
	// connection.setConnectTimeout(150);
	// connection.setReadTimeout(150);
	// mimeType = connection.getContentType();
	// Log.i("[MIME]", "mimeType from content-type "+ mimeType);
	// }
	// catch (Exception ignored)
	// {
	// }
	// finally
	// {
	// // restore main thread's default network access policy
	// StrictMode.setThreadPolicy(prviousThreadPolicy);
	// }
	//
	// if(mimeType == null)
	// {
	// // Our B plan: guessing from from url
	// try
	// {
	// mimeType = URLConnection.guessContentTypeFromName(url);
	// }
	// catch (Exception ignored)
	// {
	// }
	// Log.i("[MIME]", "mimeType guessed from url "+ mimeType);
	// }
	// return mimeType;
	// }

	public static boolean isKaraokeUri(String[] hosts, String path) {
		if (!isEmpty(path)) {
			return isKaraokeUri(hosts, Uri.parse(path));
		} else {
			return false;
		}
	}

	public static boolean isKaraokeUri(String[] hosts, Uri uri) {
		boolean ret = false;

		if (uri == null) {
			return ret;
		}

		if (uri.getHost() == null) {
			return ret;
		}

		try {
			for (String host : hosts) {
				if (host == null) {
					continue;
				}

				if (!host.contains(uri.getHost()) || host.equalsIgnoreCase(uri.getHost())) {
					ret = true;
					break;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return ret;
	}

	public static String trim(String str) {
		try {
			return str.replaceAll("\\s+", "");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * YYYYMMDDXXXXXX
	 */
	public static String getTodayFull() {
		try {
			GregorianCalendar today = new GregorianCalendar();

			int year = today.get(GregorianCalendar.YEAR);
			int month = today.get(GregorianCalendar.MONTH) + 1;
			int day = today.get(GregorianCalendar.DATE);
			int hour = today.get(GregorianCalendar.HOUR);
			int min = today.get(GregorianCalendar.MINUTE);
			int sec = today.get(GregorianCalendar.SECOND);

			return String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, min, sec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * YYYYMMDD
	 */
	public static String getToday() {
		try {
			GregorianCalendar today = new GregorianCalendar();

			int year = today.get(today.YEAR);
			int month = today.get(today.MONTH) + 1;
			int day = today.get(today.DATE);

			return String.format("%04d%02d%02d", year, month, day);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param format 날짜포맷
	 * @param diff 일단위차이
	 */
	public static String getToday(String format, int diff) {
		try {
			long lTime = System.currentTimeMillis();

			Date date = new Date(lTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, diff);
			SimpleDateFormat CurDateFormat = new SimpleDateFormat(format);

			return CurDateFormat.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param format 날짜포맷
	 * @param text 시작날짜
	 * @param diff 일단위차이
	 */
	public static String getToday(String format, String text, int diff) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);

			Date date = dateFormat.parse(text);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, diff);
			SimpleDateFormat CurDateFormat = new SimpleDateFormat(format);

			return CurDateFormat.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getToday(String format, String text) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);

			Date date = dateFormat.parse(text);
			SimpleDateFormat CurDateFormat = new SimpleDateFormat(format);

			return CurDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getToday(String from, String to, String text) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(from);
			SimpleDateFormat ef = new SimpleDateFormat(to);

			Date date = sf.parse(text);

			return ef.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static long getToDayDiff(String format, String start, String end) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			GregorianCalendar sc = new GregorianCalendar();

			sc.setTime(df.parse(start));
			GregorianCalendar ec = new GregorianCalendar();
			ec.setTime(df.parse(end));

			long diff = Math.abs(ec.getTimeInMillis() - sc.getTimeInMillis());
			long days = diff / (24 * 60 * 60 * 1000);

			return days;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
}
