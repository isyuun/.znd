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
 * filename	:	EnvironmentUtils.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ EnvironmentUtils.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 시스템환경 접근클래스
 * 
 * @author isyoon
 * @since 2012. 3. 30.
 * @version 1.0
 */
public class EnvironmentUtils {
	public static String getDataPath(Context context) {
		return Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator
				+ "data" + File.separator + context.getPackageName() + File.separator;
	}

	public static String getCachePath(Context context) {
		return Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator
				+ "data" + File.separator + context.getPackageName() + File.separator + "cache"
				+ File.separator;
	}

	// /data/data/kr.kymedia.karaoke.kpop/shared_prefs/kr.kymedia.karaoke.kpop.xml
	public static String getSharePrefPath(Context context) {
		if (context != null) {
			return Environment.getDataDirectory() + File.separator + "data" + File.separator
					+ context.getPackageName() + File.separator + "shared_prefs" + File.separator
					+ context.getPackageName() + ".xml";
		} else {
			return null;
		}
	}

	public static void deleteAllFile(String path) {
		File file = new File(path);
		File[] files = file.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					// Log.d("Directory : ", "" + files[i]);
				} else {
					// Log.d("File :", "" + files[i]);
					files[i].delete();
				}
			}
		}
	}

	/**
	 * <pre>
	 * 사용자의 마켓계정(구글계정)을 가져온다. 안드로이드에서 구글 계정 가져오는 방법 http://www.androes.com/145
	 * getAccountsByType("com.google"); [출처] [Account Manager] google 계정으로 로그인 하는 방법|작성자 Roider
	 * </pre>
	 *
	 * Lists all accounts of a particular type.  The account type is a
	 * string token corresponding to the authenticator and useful domain
	 * of the account.  For example, there are types corresponding to Google
	 * and Facebook.  The exact string token to use will be published somewhere
	 * associated with the authenticator in question.
	 *
	 * <p>It is safe to call this method from the main thread.
	 *
	 * <p>Clients of this method that have not been granted the
	 * {@link android.Manifest.permission#GET_ACCOUNTS} permission,
	 * will only see those accounts managed by AbstractAccountAuthenticators whose
	 * signature matches the client.
	 *
	 * <p><b>NOTE:</b> If targeting your app to work on API level 22 and before,
	 * GET_ACCOUNTS permission is needed for those platforms, irrespective of uid
	 * or signature match. See docs for this function in API level 22.
	 *
	 * @param type The type of accounts to return, null to retrieve all accounts
	 * @return An array of {@link Account}, one per matching account.  Empty
	 *     (never null) if no accounts of the specified type have been added.

	 */
	@Deprecated
	public static Account[] getGoogleAccount(Context context) {
		AccountManager mgr = AccountManager.get(context);
		// Account[] accts = mgr.getAccounts();
		Account[] accounts = mgr.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

		// final int count = accts.length;
		// for (int i = 0; i < count; i++) {
		// Account acct = accts[i];
		// Log.i("ANDROES", "Account - name=" + acct.name + ", type=" + acct.type);
		// }

		return accounts;
	}

	public static String getThemeName(Context context) {
		String ret = "";

		//PackageInfo packageInfo;
		//int themeResId = 0;
		//try {
		//	Resources.Theme theme = context.getTheme();
		//	TypedValue outValue = new TypedValue();
		//	theme.resolveAttribute(android.R.attr.name, outValue, true);
		//	ret = outValue.toString().toLowerCase();
		//
		//	packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		//	themeResId = packageInfo.applicationInfo.theme;
		//	ret = context.getResources().getResourceEntryName(themeResId).toLowerCase();
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}

		return ret;
	}

	public static String getThemeParentName(Context context) {
		String ret = "";

		//PackageInfo packageInfo;
		//int themeResId = 0;
		//try {
		//	Resources.Theme theme = context.getTheme();
		//	TypedValue outValue = new TypedValue();
		//	theme.resolveAttribute(android.R.attr.name, outValue, true);
		//	ret = outValue.toString().toLowerCase();
		//
		//	packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		//	themeResId = packageInfo.applicationInfo.theme;
		//	ret = context.getResources().getResourceEntryName(themeResId).toLowerCase();
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}

		return ret;
	}

	public static void newChooseGoogleAccount(android.support.v4.app.Fragment fragment, int requestCode, boolean dark) {
		String[] types = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, types, false, null, null, null, null);
		// set the style
		String theme = getThemeParentName(fragment.getActivity()).toLowerCase();
		if (dark || (null != theme && theme.contains("dark"))) {
			intent.putExtra("overrideTheme", 0);
		} else {
			intent.putExtra("overrideTheme", 1);
		}
		intent.putExtra("overrideCustomTheme", 0);
		//Intent intent = AccountPicker.zza(null, null, types, false, null, null, null, null, false, 1, 0);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void newChooseGoogleAccount(android.support.v4.app.FragmentActivity activity, int requestCode, boolean dark) {
		String[] types = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, types, false, null, null, null, null);
		// set the style
		String theme = getThemeName(activity).toLowerCase();
		if (dark || (null != theme && theme.contains("dark"))) {
			intent.putExtra("overrideTheme", 0);
		} else {
			intent.putExtra("overrideTheme", 1);
		}
		intent.putExtra("overrideCustomTheme", 0);
		//Intent intent = AccountPicker.zza(null, null, types, false, null, null, null, null, false, 1, 0);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void newChooseGoogleAccount(Fragment fragment, int requestCode, boolean dark) {
		String[] types = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, types, false, null, null, null, null);
		// set the style
		String theme = getThemeName(fragment.getActivity()).toLowerCase();
		if (dark || (null != theme && theme.contains("dark"))) {
			intent.putExtra("overrideTheme", 0);
		} else {
			intent.putExtra("overrideTheme", 1);
		}
		intent.putExtra("overrideCustomTheme", 0);
		//Intent intent = AccountPicker.zza(null, null, types, false, null, null, null, null, false, 1, 0);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void newChooseGoogleAccount(Activity activity, int requestCode, boolean dark) {
		String[] types = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, types, false, null, null, null, null);
		// set the style
		String theme = getThemeName(activity).toLowerCase();
		if (dark || (null != theme && theme.contains("dark"))) {
			intent.putExtra("overrideTheme", 0);
		} else {
			intent.putExtra("overrideTheme", 1);
		}
		intent.putExtra("overrideCustomTheme", 0);
		//Intent intent = AccountPicker.zza(null, null, types, false, null, null, null, null, false, 1, 0);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * AccountManagerからOAuthを取得する [출처] google account 계정 로그인 2|작성자 Roider
	 */
	public static void authGoogleAccount(Bundle options, Activity activity,
			AccountManagerCallback<Bundle> callback, Handler handler) {
		// AccountManager mgr = AccountManager.get(activity);
		// Account[] accts = mgr.getAccountsByType("com.google");
		// Account acct = accts[0];
		// AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, "ah", null,
		// activity, callback, handler);
		// try {
		// Bundle authTokenBundle = accountManagerFuture.getResult();
		// String authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
		// Log.i("ANDROES", "authToken:" + authToken);
		// } catch (OperationCanceledException e) {
		//
		// //Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		// } catch (AuthenticatorException e) {
		//
		// //Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		// } catch (IOException e) {
		//
		// //Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		// }
	}

	/**
	 * http://stackoverflow.com/questions/5795576/android-content-type-error-using-action-view-with-a-
	 * local-file
	 */
	public static String getMimeTypeFromPath(File file) {
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
		return mime_type;
	}

	public static long moveFile(File src, File dst) {
		long ret = 0;
		try {
			FileChannel inChannel = new FileInputStream(src).getChannel();
			FileChannel outChannel = new FileOutputStream(dst).getChannel();
			try {
				ret = inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				// if (inChannel != null)
				inChannel.close();
				// if (outChannel != null)
				outChannel.close();
			}
			return ret;
		} catch (Exception e) {

			e.printStackTrace();
			return ret;
		}
	}

	public static void writeToIntFile(Context context, String src, String name) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name,
					Context.MODE_PRIVATE));
			outputStreamWriter.write(src);
			outputStreamWriter.close();
		} catch (Exception e) {
			// Log.e("Exception", "File write failed: " + e.toString());
			e.printStackTrace();
		}
	}

	public static String readFromIntFile(Context context, String name) {

		String ret = "";

		try {
			InputStream inputStream = context.openFileInput(name);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
			// } catch (FileNotFoundException e) {
			// Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			// Log.e("login activity", "Can not read file: " + e.toString());
			e.printStackTrace();
		}

		return ret;
	}

	public static void writeToDataFile(Context context, String src, String path) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
			outputStreamWriter.write(src);
			outputStreamWriter.close();
		} catch (Exception e) {
			// Log.e("Exception", "File write failed: " + e.toString());
			e.printStackTrace();
		}
	}

	public static String readFromDataFile(Context context, String path) {

		String ret = "";

		try {
			InputStream inputStream = new FileInputStream(path);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (IOException e) {
			// Log.e("login activity", "Can not read file: " + e.toString());
			e.printStackTrace();
		}

		return ret;
	}

	public static boolean isDebuggable(Context context) {
		if (context == null || context.getApplicationInfo() == null) {
			return false;
		}
		boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
		return isDebuggable;
	}

	static int mLastTotalPss = 0;
	static int mLastTotalSharedDirty = 0;
	static int mLastTotalPrivateDirty = 0;

	static long mDalvikLastHeapSize = 0;
	static long mDalvikLastHeapAlloc = 0;
	static long mDalvikLastHeapFree = 0;
	static long mDalvikMaxHeapSize = 0;
	static long mDalvikMaxHeapAlloc = 0;

	static long mNativeLastHeapSize = 0;
	static long mNativeLastHeapAlloc = 0;
	static long mNativeLastHeapFree = 0;
	static long mNativeMaxHeapSize = 0;
	static long mNativeMaxHeapAlloc = 0;

	public static void getMemoryInfo(Context context, String where) {
		if (context == null) {
			return;
		}

		// if (!isDebuggable(context)) {
		// return;
		// }

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		// android.util.Log.d(__CLASSNAME__, " memoryInfo.availMem " + memoryInfo.availMem + "\n" );
		// android.util.Log.d(__CLASSNAME__, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n" );
		// android.util.Log.d(__CLASSNAME__, " memoryInfo.threshold " + memoryInfo.threshold + "\n" );

		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		Map<Integer, String> pidMap = new TreeMap<Integer, String>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			if (context.getPackageName() != null
					&& context.getPackageName().equalsIgnoreCase(runningAppProcessInfo.processName)) {
				pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
			}
		}

		Collection<Integer> keys = pidMap.keySet();

		int pid = 0;
		int totalPss = 0;
		int totalSharedDirty = 0;
		int totalPrivateDirty = 0;
		long heapMaximum = 0;

		long dalvikHeapSize = 0;
		long dalvikHeapAlloc = 0;
		long dalvikHeapFree = 0;

		long nativeHeapSize = 0;
		long nativeHeapAlloc = 0;
		long nativeHeapFree = 0;

		for (int key : keys) {
			int pids[] = new int[1];
			pids[0] = key;
			android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
			for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
				pid = pids[0];
				totalPss = pidMemoryInfo.getTotalPss();
				totalSharedDirty = pidMemoryInfo.getTotalSharedDirty();
				totalPrivateDirty = pidMemoryInfo.getTotalPrivateDirty();
			}
		}

		android.util.Log.d("MEMINFO-PAGEINFO",
				String.format("** MEMINFO-PAGEINFO in pid %d [%s] **\n", pid, pidMap.get(pid)));
		android.util.Log.d("MEMINFO-PAGEINFO", "         Shared  Private            Shared  Private");
		android.util.Log.d("MEMINFO-PAGEINFO", "   Pss    Dirty    Dirty    L.Pss  L.Dirty  L.Dirty");
		android.util.Log.d("MEMINFO-PAGEINFO", "------   ------   ------   ------   ------   ------");

		String memInfo = String.format("%6d   %6d   %6d   %6d   %6d   %6d", totalPss, totalSharedDirty,
				totalPrivateDirty, mLastTotalPss, mLastTotalSharedDirty, mLastTotalPrivateDirty);
		android.util.Log.i("MEMINFO-PAGEINFO", memInfo);

		heapMaximum = activityManager.getMemoryClass() * 1000 * 1000;
		// Log.v("MEMINFO-HEAPINFO", String.format("** HEAPINFO Max : memoryClass : %d kB", heapMaximum / 1000));
		// Log.v("MEMINFO-HEAPINFO", String.format("** HEAPINFO Max : maxMemory : %d kB", Runtime.getRuntime().maxMemory() / 1000));

		// dalvikHeapSize = (Runtime.getRuntime().totalMemory() + Debug.getNativeHeapSize());
		// dalvikHeapAlloc = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + (Debug
		// .getNativeHeapSize() - Debug.getNativeHeapFreeSize()));
		// dalvikHeapFree = (Runtime.getRuntime().freeMemory() + Debug.getNativeHeapFreeSize());
		dalvikHeapSize = Runtime.getRuntime().totalMemory();
		dalvikHeapAlloc = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		dalvikHeapFree = Runtime.getRuntime().freeMemory();

		nativeHeapSize = Debug.getNativeHeapSize();
		nativeHeapAlloc = (Debug.getNativeHeapSize() - Debug.getNativeHeapFreeSize());
		nativeHeapFree = Debug.getNativeHeapFreeSize();

		android.util.Log.d("MEMINFO-HEAPINFO",
				String.format("** HEAPINFO Max : %d kB **", heapMaximum / 1000));
		if ((dalvikHeapAlloc - mDalvikLastHeapAlloc) < 0) {
			android.util.Log.e("MEMINFO-HEAPWARN", "\t Heap Alloc: " + mDalvikLastHeapAlloc / 1000
					+ " kB -> " + dalvikHeapAlloc / 1000 + " kB : "
					+ (dalvikHeapAlloc - mDalvikLastHeapAlloc) / 1000 + " kB");
		} else {
			android.util.Log.w("MEMINFO-HEAPINFO", "\t Heap Alloc: " + mDalvikLastHeapAlloc / 1000
					+ " kB -> " + dalvikHeapAlloc / 1000 + " kB : "
					+ (dalvikHeapAlloc - mDalvikLastHeapAlloc) / 1000 + " kB");
		}
		if ((dalvikHeapFree - mDalvikLastHeapFree) < 0) {
			android.util.Log.e("MEMINFO-HEAPWARN", "\t Heap Free: " + mDalvikLastHeapFree / 1000
					+ " kB -> " + dalvikHeapFree / 1000 + " kB : " + (dalvikHeapFree - mDalvikLastHeapFree)
					/ 1000 + " kB");
		} else {
			android.util.Log.w("MEMINFO-HEAPINFO", "\t Heap Free: " + mDalvikLastHeapFree / 1000
					+ " kB -> " + dalvikHeapFree / 1000 + " kB : " + (dalvikHeapFree - mDalvikLastHeapFree)
					/ 1000 + " kB");
		}
		android.util.Log.d("MEMINFO-HEAPINFO",
				"  Heap     Heap     Heap   L.Heap   L.Heap   L.Heap [D]Heap  [D]Heap");
		android.util.Log.d("MEMINFO-HEAPINFO",
				"  Size    Alloc     Free     Size    Alloc     Free    Size    Alloc");
		android.util.Log.d("MEMINFO-HEAPINFO",
				"------   ------   ------   ------   ------   ------   ------   ------");

		String info = "";

		// DalvikHeapInfo
		info = String
				.format(
						"%6d   %6d   %6d   %6d   %6d   %6d   %6d   %6d",
						dalvikHeapSize / 1000,
						dalvikHeapAlloc / 1000,
						dalvikHeapFree / 1000,
						mDalvikLastHeapSize / 1000,
						mDalvikLastHeapAlloc / 1000,
						mDalvikLastHeapFree / 1000,
						(mDalvikMaxHeapSize - (mDalvikLastHeapSize == 0 ? dalvikHeapSize : mDalvikLastHeapSize)) / 1000,
						(mDalvikMaxHeapAlloc - (mDalvikLastHeapAlloc == 0 ? dalvikHeapAlloc
								: mDalvikLastHeapAlloc)) / 1000);
		android.util.Log.i("MEMINFO-HEAPINFO", info);

		boolean warning = false;
		boolean alert = false;

		if ((dalvikHeapSize + nativeHeapSize) > heapMaximum
				|| (dalvikHeapAlloc + nativeHeapAlloc) > heapMaximum) {
			alert = true;
		}

		if ((mDalvikLastHeapSize == 0 ? dalvikHeapSize : mDalvikLastHeapSize) > mDalvikMaxHeapSize) {
			mDalvikMaxHeapSize = (mDalvikLastHeapSize == 0 ? dalvikHeapSize : mDalvikLastHeapSize);
			// heapInfo += "\t[HeapSize]";
			warning = true;
		}

		if ((mDalvikLastHeapAlloc == 0 ? dalvikHeapAlloc : mDalvikLastHeapAlloc) > mDalvikMaxHeapAlloc) {
			mDalvikMaxHeapAlloc = (mDalvikLastHeapAlloc == 0 ? dalvikHeapAlloc : mDalvikLastHeapAlloc);
			// heapInfo += "\t[HeapAlloc]";
			// warning = true;
		}

		if (alert) {
			warning = true;
		}

		if (warning) {
			info += " -> " + where;
			if (alert) {
				android.util.Log.wtf("*MEMWARN-HEAPALERT*", info);
			} else {
				android.util.Log.wtf("MEMWARN-HEAPWARN", info);
			}
			popupToast(context, memInfo + "\n" + info, Toast.LENGTH_SHORT);
			warning = false;
			alert = false;
		}

		// NativeHeapInfo
		info = String
				.format(
						"*%5d   *%5d   *%5d   *%5d   *%5d   *%5d   *%5d   *%5d",
						nativeHeapSize / 1000,
						nativeHeapAlloc / 1000,
						nativeHeapFree / 1000,
						mNativeLastHeapSize / 1000,
						mNativeLastHeapAlloc / 1000,
						mNativeLastHeapFree / 1000,
						(mNativeMaxHeapSize - (mNativeLastHeapSize == 0 ? nativeHeapSize : mNativeLastHeapSize)) / 1000,
						(mNativeMaxHeapAlloc - (mNativeLastHeapAlloc == 0 ? nativeHeapAlloc
								: mNativeLastHeapAlloc)) / 1000);
		// android.util.Log.i("MEMINFO-HEAPINFO", heapInfo);

		warning = false;

		if ((mNativeLastHeapSize == 0 ? nativeHeapSize : mNativeLastHeapSize) > mNativeMaxHeapSize) {
			mNativeMaxHeapSize = (mNativeLastHeapSize == 0 ? nativeHeapSize : mNativeLastHeapSize);
			// heapInfo += "\t[NativeSize]";
			warning = true;
		}

		if ((mNativeLastHeapAlloc == 0 ? nativeHeapAlloc : mNativeLastHeapAlloc) > mNativeMaxHeapAlloc) {
			mNativeMaxHeapAlloc = (mNativeLastHeapAlloc == 0 ? nativeHeapAlloc : mNativeLastHeapAlloc);
			// heapInfo += "\t[NativeAlloc]";
			// warning = true;
		}

		if (alert) {
			warning = true;
		}

		if (warning) {
			info += " -> " + where;
			if (alert) {
				android.util.Log.wtf("*MEMWARN-HEAPALERT*", info);
			} else {
				android.util.Log.wtf("MEMWARN-HEAPWARN", info);
			}
			// popupToast(context, memInfo + "\n" + heapInfo, Toast.LENGTH_SHORT);
			warning = false;
			alert = false;
		}

		mLastTotalPss = totalPss;
		mLastTotalSharedDirty = totalSharedDirty;
		mLastTotalPrivateDirty = totalPrivateDirty;

		mDalvikLastHeapSize = dalvikHeapSize;
		mDalvikLastHeapAlloc = dalvikHeapAlloc;
		mDalvikLastHeapFree = dalvikHeapFree;

		mNativeLastHeapSize = nativeHeapSize;
		mNativeLastHeapAlloc = nativeHeapAlloc;
		mNativeLastHeapFree = nativeHeapFree;

	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static FeatureInfo[] getSystemAvailableFeatures(Context context) {
		if (!isDebuggable(context)) {
			return null;
		}

		final PackageManager packageManager = context.getPackageManager();
		final FeatureInfo[] featuresList = packageManager.getSystemAvailableFeatures();
		for (FeatureInfo f : featuresList) {
			try {
				Log.w("[INFO-FEATURES]", "" + f.name);
				// if (f.name != null && f.name.equals(feature)) {
				// return true;
				// }
			} catch (Exception e) {

				// e.printStackTrace();
			}
		}
		return featuresList;
	}

	static Toast mToast = null;

	public static void popupToast(Context context, CharSequence text, int dur) {
		try {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, text, dur);
			mToast.show();
		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		}
	}

	public static void popupToast(Context context, String text, int dur) {
		try {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, text, dur);
			mToast.show();
		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		}
	}

	public static String getIpAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						String ipAddress = inetAddress.getHostAddress().toString();
						// Log.e("IP address", "" + ipAddress);
						return ipAddress;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
		}
		return null;
	}

	@Deprecated
	public static String getLocalPort() {
		String ret = null;
		int port = 9999;

		ServerSocket s;
		try {
			s = new ServerSocket(0);
			port = s.getLocalPort();
			ret = String.format(":%d", port);
			s.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return ret;

	}
}
