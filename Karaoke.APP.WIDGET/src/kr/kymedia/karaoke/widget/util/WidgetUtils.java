/*
 * Copyright 2011 The uAndroid Open Source Project
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
 * filename	:	WidgetUtils.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.util
 *    |_ WidgetUtils.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * 
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 3. 29.
 * @version 1.0
 * @see WidgetUtils.java
 */

public class WidgetUtils {

	static public boolean isTextView(View v) {
		return (v.getClass() == TextView.class);
	}

	static public boolean isEditText(View v) {
		return (v.getClass() == EditText.class);
	}

	static public boolean isButton(View v) {
		return (v.getClass() == Button.class);
	}

	static public boolean isImageButton(View v) {
		return (v.getClass() == ImageButton.class);
	}

	static public boolean isImageView(View v) {
		return (v.getClass() == ImageView.class);
	}

	static public void resizeImageView2Bitmap(ImageView v, Bitmap bm) {
		float h1, w1, h2, w2;
		h1 = bm.getHeight();
		w1 = bm.getWidth();
		h2 = v.getHeight();
		w2 = v.getWidth();
		// Log.e("RESIZE", "[BF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
		if (h1 > w1) {
			h2 = (h1 / w1) * w2;
		} else if (h1 < w1) {
			h2 = w2 / (w1 / h1);
		} else {
			h2 = w2;
		}
		LayoutParams params = v.getLayoutParams();
		params.height = (int) (h2 > 0 ? h2 : params.height);
		v.setLayoutParams(params);
		// Log.e("RESIZE", "[AF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
	}

	public static <T extends View> List<T> findViewsWithClass(View v, Class<T> clazz) {
		List<T> views = new ArrayList<T>();
		findViewsWithClass(v, clazz, views);
		return views;
	}

	@SuppressWarnings("unchecked")
	private static <T extends View> void findViewsWithClass(View v, Class<T> clazz, List<T> views) {
		if (v.getClass().getName().equals(clazz.getName())) {
			views.add((T) v);
		}
		if (v instanceof ViewGroup) {
			ViewGroup g = (ViewGroup) v;
			for (int i = 0; i < g.getChildCount(); i++) {
				findViewsWithClass(g.getChildAt(i), clazz, views);
			}
		}
	}

	static public void createShortcut(Context context, String packageName, String className, String url, String type, /*
																																																										 * Bitmap
																																																										 * img
																																																										 */int img) {
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction(Intent.ACTION_VIEW);

		if (("web").equalsIgnoreCase(type)) {
			Uri uri = Uri.parse(url);
			shortcutIntent.setData(uri);
		} else if (("app").equalsIgnoreCase(type)) {

			// Log.d("DEBUGGING", "Inside app installation " + url + " : " + className + " : " + packageName);
			shortcutIntent.setClassName(url, className);
		}

		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, packageName);
		addIntent.putExtra("duplicate", false);
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, img));
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, R.drawable.ic_launcher);

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}

	public static void setTypeFaceBold(final TextView tv, int times) {
		if (tv == null) {
			return;
		}
		for (int i = 0; i < times; i++) {
			tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		}
	}

	public static void setTypeFaceBold(final TextView tv) {
		if (tv == null) {
			return;
		}
		if ((tv.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) == 0) {
			tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		}
	}

	public static void setTypeFaceBold(final TextView tv, boolean bold) {
		if (tv == null) {
			return;
		}

		// 텍스트볼드처리(PaintFlags)
		if (bold && (tv.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) == 0) {
			// 텍스트봉드처리추가
			tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		} else if (!bold && (tv.getPaintFlags() & Paint.FAKE_BOLD_TEXT_FLAG) != 0) {
			// 텍스트봉드처리헤제
			tv.setPaintFlags(tv.getPaintFlags() ^ Paint.FAKE_BOLD_TEXT_FLAG);
		}

		// //텍스트볼드처리(Typeface)
		// Typeface tf = tv.getTypeface();
		// if (tf != null) {
		// if (bold && !tf.isBold()) {
		// tf = Typeface.DEFAULT_BOLD;
		// tv.setTypeface(tf);
		// } else if (!bold && tf.isBold()) {
		// tf = Typeface.DEFAULT;
		// tv.setTypeface(tf);
		// }
		// }
	}

	public static void setTextViewMarquee(final TextView tv) {
		setTextViewMarquee(tv, true);
	}

	//public static void setTextOnlyMarquee(final TextView tv, boolean enable) {
	//	if (tv == null) {
	//		return;
	//	}
	//	if (!(tv instanceof EditText)) {
	//		// set the ellipsize mode to MARQUEE and make it scroll only once
	//		// tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	//		// tv.setMarqueeRepeatLimit(1);
	//		if (enable) {
	//			tv.setEllipsize(TruncateAt.MARQUEE);
	//		} else {
	//			tv.setEllipsize(null);
	//		}
	//		// in order to start strolling, it has to be focusable and focused
	//		// tv.setFocusable(true);
	//		// tv.setFocusableInTouchMode(true);
	//		// tv.requestFocus();
	//		tv.setSingleLine(true);
	//		tv.setSelected(enable);
	//	}
	//}

	public static void setTextViewMarquee(final TextView tv, boolean enable) {
		if (tv == null) {
			return;
		}
		// set the ellipsize mode to MARQUEE and make it scroll only once
		// tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// tv.setMarqueeRepeatLimit(1);
		// in order to start strolling, it has to be focusable and focused
		// tv.setFocusable(true);
		// tv.setFocusableInTouchMode(true);
		// tv.requestFocus();
		if (enable) {
			tv.setEllipsize(TruncateAt.MARQUEE);
		} else {
			tv.setEllipsize(TruncateAt.END);
		}
		tv.setSingleLine(true);
		tv.setSelected(enable);
	}

	public static int getIdentifier(Context context, String name, String defType) {
		try {
			return context.getResources().getIdentifier(name, defType, context.getPackageName());
		} catch (Exception e) {
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return 0;
		}
	}

	public static int getResource(Context context, String name, String defType) {
		try {
			return context.getResources().getIdentifier(name, defType, context.getPackageName());
		} catch (Exception e) {
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return 0;
		}
	}

	public static int getResourceID(Context context, String name) {
		try {
			return context.getResources().getIdentifier(name, "id", context.getPackageName());
		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return 0;
		}
	}

	public static int getDrawableID(Context context, String name) {
		try {
			return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		} catch (Exception e) {
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return 0;
		}
	}

	public static String getResourceEntryName(Context context, int resId) {
		String name = "";

		try {
			name = context.getResources().getResourceEntryName(resId);
			// name += ", " + getResources().getResourceName(resid);
			// name += ", " + getResources().getResourcePackageName(resid);
			// name += ", " + getResources().getResourceTypeName(resid);
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return name;
	}

	/**
	 * <pre>
	 * 딴데가서알아봐!!!
	 * </pre>
	 * 
	 * @see <a href="http://android-developers.blogspot.kr/2009_01_01_archive.html">Avoiding memory leaks</a>
	 */
	public static Drawable getDrawable(Context context, String name) {
		Drawable d = null;
		try {
			int res = getResource(context, name, "drawable");
			if (res == 0) {
				return null;
			}
			d = getDrawable(context, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * <pre>
	 * 딴데가서알아봐!!!
	 * </pre>
	 * 
	 * @see <a href="http://android-developers.blogspot.kr/2009_01_01_archive.html">Avoiding memory leaks</a>
	 */
	public static Drawable getDrawable(Context context, int id) {
		Drawable d = null;
		try {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
				d = context.getDrawable(id);
			} else {
				d = context.getResources().getDrawable(id);
			}
			d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Drawable resize(Context context, Drawable image, int w, int h) {
		Bitmap b = ((BitmapDrawable) image).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, w, h, false);
		// return new BitmapDrawable(bitmapResized);
		return new BitmapDrawable(context.getResources(), bitmapResized);
	}

	public static int getColorID(Context context, String name) {
		try {
			return context.getResources().getIdentifier(name, "color", context.getPackageName());
		} catch (Exception e) {
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return 0;
		}
	}

	public static int getColor(Context context, String name) {
		int c = 0xff000000;
		try {
			int res = getResource(context, name, "color");
			c = context.getResources().getColor(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static int getColor(Context context, int id) {
		int c = 0xff000000;
		try {
			c = context.getResources().getColor(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	@SuppressLint("NewApi")
	public static void setBackground(Context context, View v, int res) {
		try {
			Drawable d = null;
			d = context.getResources().getDrawable(res);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				v.setBackground(d);
			} else {
				v.setBackgroundDrawable(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static View[] getChildViews(ViewGroup group) {
		try {
			int childCount = 0;

			if (group != null) {
				childCount = group.getChildCount();
			}

			final View[] childViews = new View[childCount];

			if (group != null) {
				for (int i = 0; i < childCount; i++) {
					childViews[i] = group.getChildAt(i);
				}
			}

			return childViews;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setOnClickListener(Context context, ViewGroup g, OnClickListener listener, boolean enable) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					setOnClickListener(context, (ViewGroup) v, listener, enable);
				} else {
					// if (v instanceof EditText) {
					// } else {
					// v.setClickable(enable);
					// if (enable) {
					// v.setOnClickListener(listener);
					// } else {
					// v.setOnClickListener(null);
					// }
					// }
					v.setClickable(enable);
					if (enable) {
						v.setOnClickListener(listener);
					} else {
						v.setOnClickListener(null);
					}
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setOnLongClickListener(Context context, ViewGroup g, OnLongClickListener listener, boolean enable) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					setOnLongClickListener(context, (ViewGroup) v, listener, enable);
				} else {
					// if (v instanceof EditText) {
					// } else {
					// v.setClickable(enable);
					// v.setLongClickable(enable);
					// if (enable) {
					// v.setOnLongClickListener(listener);
					// } else {
					// v.setOnLongClickListener(null);
					// }
					// }
					v.setClickable(enable);
					v.setLongClickable(enable);
					if (enable) {
						v.setOnLongClickListener(listener);
					} else {
						v.setOnLongClickListener(null);
					}
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setOnTouchListener(Context context, ViewGroup g, OnTouchListener listener) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					v.setOnTouchListener(listener);
					setOnTouchListener(context, (ViewGroup) v, listener);
				} else {
					v.setOnTouchListener(listener);
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setOnFocusListener(Context context, ViewGroup g, OnFocusChangeListener listener) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					setOnFocusListener(context, (ViewGroup) v, listener);
				} else {
					v.setOnFocusChangeListener(listener);
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setOnKeyListener(Context context, ViewGroup g, OnKeyListener listener, boolean enable) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					setOnKeyListener(context, (ViewGroup) v, listener, enable);
				} else {
					if (enable) {
						v.setOnKeyListener(listener);
					} else {
						v.setOnKeyListener(null);
					}
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void requestFocus(Context context, ViewGroup g) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					requestFocus(context, (ViewGroup) v);
				} else {
					v.setSelected(true);
					v.setFocusable(true);
					v.setFocusableInTouchMode(true);
					v.requestFocus();
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void clearFocus(Context context, ViewGroup g) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					clearFocus(context, (ViewGroup) v);
				} else {
					v.clearFocus();
					v.setSelected(false);
					v.setFocusable(false);
					v.setFocusableInTouchMode(false);
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void setVisibility(Context context, ViewGroup g, int visibility) {
		View[] childViews = getChildViews(g);

		try {
			for (View v : childViews) {
				if (v instanceof ViewGroup) {
					setVisibility(context, (ViewGroup) v, visibility);
				} else {
					v.setVisibility(visibility);
				}

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * StatusBar Size 구하는 메서드 ( onCreate에서 실행 불가능 )
	 * </pre>
	 * 
	 * @param activity
	 */
	public static int getStatusBarSize(Activity activity) {
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int statusBarHeight = rectgle.top;
		// int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// int titleBarHeight = contentViewTop - StatusBarHeight;
		// Log.i("StatusBarTest" , "StatusBar Height= " + statusBarHeight +
		// " TitleBar Height = " + titleBarHeight);
		return statusBarHeight;
	}

	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

	/**
	 * <pre>
	 * onCreate()에서 StatusBar 구하는 메서드 ( Density 이용 )
	 * </pre>
	 * 
	 * @param context
	 */
	public static int getStatusBarSizeOnCreate(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

		int statusBarHeight;

		switch (displayMetrics.densityDpi) {
		case DisplayMetrics.DENSITY_HIGH:
			statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
			break;
		case DisplayMetrics.DENSITY_LOW:
			statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
			break;
		default:
			statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
		}
		// Log.i("StatusBarTest" , "onCreate StatusBar Height= " + statusBarHeight);

		return statusBarHeight;
	}

	public static boolean isAppInstalled(String uri, Context context) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a
	 * dialog that allows users to download the APK from the Google Play Store or enable it in the
	 * device's system settings.
	 */
	public static boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				// Log.i(TAG, "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	public static int getPosition(SpinnerAdapter a, String item) {
		int ret = 0;
		for (int i = 0; i < a.getCount(); i++) {
			if (a.getItem(i).toString().equalsIgnoreCase(item)) {
				ret = i;
			}
		}
		return ret;
	}

	public static boolean isShowSoftKeyboard(Context context) {
		try {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			return imm.isAcceptingText();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <pre>
	 * 키보드보임:포커스먼저
	 * </pre>
	 */
	public static void showSoftKeyboard(Context context, View v) {
		try {
			if (v != null) {
				// 포커스먼저
				v.requestFocus();
				// 키보드보임
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * 키보드보임:포커스나중
	 * </pre>
	 */
	public static void hideSoftKeyboard(Context context, View v) {
		try {
			if (v != null) {
				// 키보드숨김
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// 포커스나중
				v.clearFocus();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hideSoftKeyboard(Activity activity) {
		try {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * 키보드보임:포커스먼저
	 * </pre>
	 */
	public static void toggleSoftKeyboard(Context context) {
		try {
			// 키보드보임
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setEditable(View v, boolean enabled) {
		// hideSoftKeyboard(v.getContext(), v);
		v.setEnabled(enabled);
		v.setClickable(enabled);
		v.setFocusable(enabled);
		v.setFocusableInTouchMode(enabled);
	}

	public static int getStatusBarHeight(Context context) {
		if (context == null) {
			return 0;
		}
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static int getNavigationBarHeight(Context context) {
		if (context == null) {
			return 0;
		}
		int result = 0;
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");

		switch (context.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			resourceId = context.getResources().getIdentifier("navigation_bar_height_landscape", "dimen", "android");
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			break;
		default:
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			break;
		}

		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v == null) {
			return;
		}
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}

	/**
	 * <a
	 * href="http://stackoverflow.com/questions/21057035/detect-android-navigation-bar-orientation">
	 * Detect Android Navigation Bar orientation</a>
	 */
	public static boolean isNavBarBottom(Activity activity) {
		boolean ret = false;

		boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();

		// if there is not menu key then there is navigation bar
		if (!hasMenuKey && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			if (dm.heightPixels % 10 != 0) {
				// navigation bar is on bottom
				ret = true;
			} else {
				// navigation bar is on right side
			}
		}

		return ret;
	}

	/**
	 * <a
	 * href="http://stackoverflow.com/questions/21057035/detect-android-navigation-bar-orientation">
	 * Detect Android Navigation Bar orientation</a>
	 */
	public static boolean isNavBarRight(Activity activity) {
		boolean ret = false;

		boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();

		// if there is not menu key then there is navigation bar
		if (!hasMenuKey && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			if (dm.heightPixels % 10 != 0) {
				// navigation bar is on bottom
			} else {
				// navigation bar is on right side
				ret = true;
			}
		}

		return ret;
	}

	@SuppressLint("InlinedApi")
	public static boolean isStatusBarVisible(Activity activity) {
		boolean ret = true;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
			ret = ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0);
		}

		return ret;
	}
}
