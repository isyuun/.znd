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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.KPOP
 * filename	:	PhoneInformation3.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.data
 *    |_ PhoneInformation2.java
 * </pre>
 */

package kr.kymedia.karaoke.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.util.Base64;
import kr.kymedia.karaoke.util.EnvironmentUtils;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

/**
 * TODO NOTE:<br>
 * 기기정보<br>
 *
 * @author isyoon
 * @since 2012. 5. 16.
 * @version 1.0
 */

public class KPparam {
	String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private Context mContext;

	public String info = "";

	private String mParam = "";
	private TelephonyManager mTM;
	private String p_mac;

	public String getMacAdress() {
		return p_mac;
	}

	private String model = "";

	public String getModel() {
		return model;
	}

	private String udid = "";
	private String p_stbid = "";
	/**
	 * advertisingID
	 */
	Info adInfo = null;
	String advertisingID = "";

	public String getAdvertisingID() {
		return advertisingID;
	}

	boolean isLAT = false;

	/**
	 * 어플패키지명
	 */
	private String p_appname = "";
	/**
	 * 어플패키지버전명
	 */
	private String p_appversionname = "";
	/**
	 * 어플패키지버전코드
	 */
	private int p_appversioncode = 0;
	/**
	 * 전화번호
	 */
	private String p_phoneno = "";

	/**
	 * 사용자GMAIL계정(안드로이드마켓계정)
	 */
	private String p_gmail = "";

	public String getGmail() {
		return p_gmail;
	}

	public void setGmail(String p_gmail) {
		this.p_gmail = p_gmail;
	}

	/**
	 *
	 */
	Locale mLocale;
	/**
	 * 국가코드 : KR
	 *
	 * @return the p_ncode
	 */
	private String p_ncode = "";

	/**
	 * 국가코드 : KR
	 *
	 * @return the p_ncode
	 */
	public void setNcode(String ncode) {
		p_ncode = ncode;
	}

	/**
	 * 국가코드 : KR
	 *
	 * @return the p_ncode
	 */
	public String getNcode() {
		return p_ncode;
	}

	/**
	 * 언어코드 : KO
	 *
	 * @return the p_lcode
	 */
	private String p_lcode = "";

	/**
	 * 언어코드 : KO
	 *
	 * @return the p_lcode
	 */
	public void setLcode(String lcode) {
		p_lcode = lcode;
	}

	/**
	 * 언어코드 : KO
	 *
	 * @return the p_lcode
	 */
	public String getLcode() {
		return p_lcode;
	}

	/**
	 * os버전
	 */
	private String p_osversion = "";
	/**
	 * os버전
	 */
	private String p_apiversion = "";

	/**
	 * @return the mParamHeader
	 */
	public String getParam() {
		return mParam;
	}

	/**
	 * @param param
	 *          the mParamHeader to set
	 */
	public void setParam(String param) {
		this.mParam = param;
	}

	/**
	 * Usage of Android advertising ID
	 *
	 * <pre>
	 * Usage of Android advertising ID
	 *
	 * Google Play Services version 4.0 introduced new APIs and an ID for use by advertising and analytics providers. Terms for the use of these APIs and ID are below.
	 * <li><b>Usage.</b> The Android advertising identifier must only be used for advertising and user analytics. The status of the “Opt out of Interest-based Advertising” setting must be verified on each access of the ID.
	 * <li><b>Association with personally-identifiable</b> information or other identifiers. The advertising identifier must not be connected to personally-identifiable information or associated with any persistent device identifier (for example: SSAID, MAC address, IMEI, etc.,) without the explicit consent of the user.
	 * <li><b>Respecting users' selections.</b> Upon reset, a new advertising identifier must not be connected to a previous advertising identifier or data derived from a previous advertising identifier without the explicit consent of the user. Furthermore, you must abide by a user’s “opt out of interest-based advertising” setting. If a user has enabled this setting, you may not use the advertising identifier for creating user profiles for advertising purposes or for targeting users with interest-based advertising. Allowed activities include contextual advertising, frequency capping, conversion tracking, reporting and security and fraud detection.
	 * <li><b>Transparency to users.</b> The collection and use of the advertising identifier and commitment to these terms must be disclosed to users in a legally adequate privacy notification.
	 * <li><b>Abiding by the terms of use.</b> The advertising identifier may only be used in accordance with these terms, including by any party that you may share it with in the course of your business. Beginning August 1st 2014, all updates and new apps uploaded to the Play Store must use the advertising ID (when available on a device) in lieu of any other device identifiers for any advertising purposes.
	 *
	 * Do not call this function from the main thread. Otherwise,
	 * an IllegalStateException will be thrown.
	 *
	 * <a href="http://play.google.com/about/developer-content-policy.html#ADID">Usage of Android advertising ID</a>
	 * <a href="http://developer.android.com/google/play-services/id.html">Advertising ID | Android Developers</a>
	 * </pre>
	 *
	 */
	public void getAdvertisingIdInfo() {

		try {
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);

			advertisingID = adInfo.getId();
			isLAT = adInfo.isLimitAdTrackingEnabled();

			android.util.Log.w("[Ads]", "[Ads:Opt out of interest-based advertising]" + isLAT + ":" + advertisingID);

		} catch (IllegalStateException e) {

			// e.printStackTrace();
			android.util.Log.w("[Ads]", "[Ads:IllegalStateException]" + Log2.getStackTraceString(e));
		} catch (GooglePlayServicesRepairableException e) {

			// e.printStackTrace();
			android.util.Log.w("[Ads]", "[Ads:GooglePlayServicesRepairableException]" + Log2.getStackTraceString(e));
		} catch (IOException e) {
			// Unrecoverable error connecting to Google Play services (e.g.,
			// the old version of the service doesn't support getting AdvertisingId).
			// e.printStackTrace();
			android.util.Log.w("[Ads]", "[Ads:IOException]" + Log2.getStackTraceString(e));
			// } catch (GooglePlayServicesAvailabilityException e) {
			// // Encountered a recoverable error connecting to Google Play services.
			//
		} catch (GooglePlayServicesNotAvailableException e) {
			// Google Play services is not available entirely.
			// e.printStackTrace();
			android.util.Log.w("[Ads]", "[Ads:GooglePlayServicesNotAvailableException]" + Log2.getStackTraceString(e));
		} catch (Exception e) {
			android.util.Log.w("[Ads]", "[Ads:Exception]" + Log2.getStackTraceString(e));
		}

	}

	private void getInfo() {

		try {
			info = "[PHONEINFO]";
			info += ("\nADID\t:\t" + advertisingID);
			info += ("\nisLAT\t:\t" + isLAT);
			info += ("\nSTBID\t:\t" + p_stbid);
			info += ("\nMODEL\t:\t" + model);
			info += ("\nPHONENO\t:\t" + p_phoneno);
			info += ("\nFEATURE_TELEPHONY\t:\t" + mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY));
			info += ("\np_ncode\t:\t" + p_ncode);
			info += ("\np_lcode\t:\t" + p_lcode);
			info += ("\nUDID\t:\t" + udid);
			info += ("\nIMEI/MEID/ESN\t:\t" + mTM.getDeviceId());
			info += ("\nIMSI\t:\t" + mTM.getSubscriberId());
			info += ("\nSIM\t:\t" + mTM.getSimSerialNumber());
			info += ("\nMAC\t:\t" + p_mac);
			info += ("\nNetworkCountryIso\t:\t" + mTM.getNetworkCountryIso());
			info += ("\nSimCountryIso\t:\t" + mTM.getSimCountryIso());
			info += ("\nlocale.getISO3Country()\t:\t" + mLocale.getISO3Country());
			info += ("\nlocale.getISO3Language()\t:\t" + mLocale.getISO3Language());
			info += ("\nlocale.getCountry()\t:\t" + mLocale.getCountry());
			info += ("\nlocale.getLanguage()\t:\t" + mLocale.getLanguage());
			info += ("\nlocale.getDisplayCountry()\t:\t" + mLocale.getDisplayCountry());
			info += ("\nlocale.getDisplayLanguage()\t:\t" + mLocale.getDisplayLanguage());
			info += ("\nBuild.MANUFACTURER\t:\t" + Build.MANUFACTURER);
			info += ("\nBuild.MODEL\t:\t" + Build.MODEL);
			info += ("\nBuild.PRODUCT\t:\t" + Build.PRODUCT);
			info += ("\nBuild.VERSION.CODENAME\t:\t" + Build.VERSION.CODENAME);
			info += ("\nBuild.VERSION.INCREMENTAL\t:\t" + Build.VERSION.INCREMENTAL);
			info += ("\nBuild.VERSION.RELEASE\t:\t" + Build.VERSION.RELEASE);
			// info += ("\nBuild.VERSION.SDK\t:\t" + Build.VERSION.SDK);
			info += ("\nBuild.VERSION.SDK_INT\t:\t" + Build.VERSION.SDK_INT);
			info += ("\nBuild.BOARD\t:\t" + Build.BOARD);
			// Since: API Level 8
			if (Build.VERSION.SDK_INT >= 8) {
				info += ("\nBuild.BOOTLOADER\t:\t" + Build.BOOTLOADER);
			}
			info += ("\nBuild.BRAND\t:\t" + Build.BRAND);
			info += ("\nBuild.CPU_ABI\t:\t" + Build.CPU_ABI);
			// Since: API Level 8
			if (Build.VERSION.SDK_INT >= 8) {
				info += ("\nBuild.CPU_ABI2\t:\t" + Build.CPU_ABI2);
			}
			info += ("\nBuild.DEVICE\t:\t" + Build.DEVICE);
			info += ("\nBuild.DISPLAY\t:\t" + Build.DISPLAY);
			info += ("\nBuild.FINGERPRINT\t:\t" + Build.FINGERPRINT);
			// Since: API Level 8
			if (Build.VERSION.SDK_INT >= 8) {
				info += ("\nBuild.HARDWARE\t:\t" + Build.HARDWARE);
			}
			info += ("\nBuild.HOST\t:\t" + Build.HOST);
			info += ("\nBuild.ID\t:\t" + Build.ID);
			// Since: API Level 8
			// if (Build.VERSION.SDK_INT >= 8) {
			// info += ("\nBuild.RADIO\t:\t" + Build.RADIO);
			// }
			// Since: API Level 9
			if (Build.VERSION.SDK_INT >= 9) {
				info += ("\nBuild.SERIAL\t:\t" + Build.SERIAL);
			}
			info += ("\nBuild.TAGS\t:\t" + Build.TAGS);
			info += ("\nBuild.TIME\t:\t" + Build.TIME);
			info += ("\nBuild.TYPE\t:\t" + Build.TYPE);
			info += ("\nBuild.USER\t:\t" + Build.USER);
			info += ("\nSystem.getProperty(\"os.name\")\t:\t" + System.getProperty("os.name"));
			info += ("\nSystem.getProperty(\"os.arch\")\t:\t" + System.getProperty("os.arch"));
			info += ("\nSystem.getProperty(\"os.version\")\t:\t" + System.getProperty("os.version"));

			// Locale locale2 = new Locale(p_lcode, "KP");
			// info += ("\nlocale2.getDisplayCountry()\t:\t" + locale2.getDisplayCountry());

			if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, info);
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][getInfo()]" + Log2.getStackTraceString(e));
		}

	}

	/**
	 * param초기화
	 *
	 * @see #getAdvertisingIdInfo()
	 */
	public KPparam(Context context, boolean adsID) {
		mContext = context;

		init(context, adsID);

	}

	/**
	 * 기기정보 Advertising ID
	 *
	 * @see #getAdvertisingIdInfo()
	 */
	public void init(Context context, boolean adsID) {
		mContext = context;

		if (adsID) {
			getAdvertisingIdInfo();
		}

		mTM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mLocale = context.getResources().getConfiguration().locale;

		// model = Build.MODEL;
		try {
			model = URLEncoder.encode(Build.MODEL, "UTF-8");
		} catch (Exception e) {

			e.printStackTrace();
		}

		udid = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		String devid = "";

		try {
			devid = mTM.getDeviceId();
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][p_stbid][DeviceId]" + Log2.getStackTraceString(e));
		}

		p_stbid = udid + devid;

		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			p_appname = pkgInfo.packageName;
			p_appversionname = pkgInfo.versionName;
			p_appversioncode = pkgInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][PackageInfo]" + Log2.getStackTraceString(e));
		}


		try {
			p_phoneno = mTM.getLine1Number();
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][p_phoneno][Line1Number]" + Log2.getStackTraceString(e));
		}

		try {
			p_ncode = mTM.getNetworkCountryIso().toUpperCase();
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][p_ncode][CountryIso]" + Log2.getStackTraceString(e));
		}

		p_ncode = mLocale.getCountry().toUpperCase();
		p_lcode = mLocale.getLanguage().toUpperCase();

		Account accounts[] = EnvironmentUtils.getGoogleAccount(context);
		if (accounts.length > 0) {
			p_gmail = accounts[0].name;
		}

		// MAC ADDRESS
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		p_mac = wifiInfo.getMacAddress();

		try {
			setParam("");
			setParam(getParam() + "&p_stbid=" + Base64.encode(p_stbid.getBytes()));
			setParam(getParam() + "&p_appname=" + p_appname);
			setParam(getParam() + "&p_appversionname=" + p_appversionname);
			setParam(getParam() + "&p_appversioncode=" + p_appversioncode);
			setParam(getParam() + "&p_account=" + Base64.encode(p_gmail.getBytes()));
			if (_IKaraoke.APP_PACKAGE_NAME_S5.equalsIgnoreCase(context.getPackageName())) {
			} else {
				setParam(getParam() + ("&p_phoneno=" + p_phoneno));
			}
			setParam(getParam() + ("&p_ncode=" + p_ncode));
			setParam(getParam() + ("&p_lcode=" + p_lcode));
			try {
				setParam(getParam() + ("&model=" + URLEncoder.encode(model, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_model=" + URLEncoder.encode(model, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_manufacturer=" + URLEncoder.encode(Build.MANUFACTURER, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_device=" + URLEncoder.encode(Build.DEVICE, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_board=" + URLEncoder.encode(Build.BOARD, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_bootloader=" + URLEncoder.encode(Build.BOOTLOADER, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				setParam(getParam() + ("&p_brand=" + URLEncoder.encode(Build.BRAND, "UTF-8")));
			} catch (Exception e) {

				e.printStackTrace();
			}
			if (mTM.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
				// 유심이 없는 경우
			} else {
				// 유심이 존재하는 경우
			}
			setParam(getParam() + ("&p_mac=" + p_mac));

			// OS버전정보
			// String p_osname = "";
			// switch (Build.VERSION.SDK_INT) {
			// case Build.VERSION_CODES.CUR_DEVELOPMENT:
			// p_osname = "CUR_DEVELOPMENT";
			// break;
			// case Build.VERSION_CODES.BASE:
			// p_osname = "BASE";
			// break;
			// case Build.VERSION_CODES.BASE_1_1:
			// p_osname = "BASE_1_1";
			// break;
			// case Build.VERSION_CODES.CUPCAKE:
			// p_osname = "CUPCAKE";
			// break;
			// case Build.VERSION_CODES.DONUT:
			// p_osname = "DONUT";
			// break;
			// case Build.VERSION_CODES.ECLAIR:
			// p_osname = "ECLAIR";
			// break;
			// case Build.VERSION_CODES.ECLAIR_0_1:
			// p_osname = "ECLAIR_0_1";
			// break;
			// case Build.VERSION_CODES.ECLAIR_MR1:
			// p_osname = "ECLAIR_MR1";
			// break;
			// case Build.VERSION_CODES.FROYO:
			// p_osname = "FROYO";
			// break;
			// case Build.VERSION_CODES.GINGERBREAD:
			// p_osname = "GINGERBREAD";
			// break;
			// case Build.VERSION_CODES.GINGERBREAD_MR1:
			// p_osname = "GINGERBREAD_MR1";
			// break;
			// case Build.VERSION_CODES.HONEYCOMB:
			// p_osname = "HONEYCOMB";
			// break;
			// case Build.VERSION_CODES.HONEYCOMB_MR1:
			// p_osname = "HONEYCOMB_MR1";
			// break;
			// case Build.VERSION_CODES.HONEYCOMB_MR2:
			// p_osname = "HONEYCOMB_MR2";
			// break;
			// case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
			// p_osname = "ICE_CREAM_SANDWICH";
			// break;
			// case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
			// p_osname = "ICE_CREAM_SANDWICH_MR1";
			// break;
			// default:
			// p_osname = "ICE_CREAM_SANDWICH_OVER";
			// break;
			// }
			p_osversion = Build.VERSION.RELEASE.toString();
			p_apiversion = Integer.toString(Build.VERSION.SDK_INT);
			setParam(getParam() + ("&p_os=A"));
			setParam(getParam() + ("&p_osversion=" + p_osversion));
			setParam(getParam() + ("&p_apiversion=" + p_apiversion));

			// 보류
			// if (save) {
			// String path = EnvironmentUtils.getDataPath(context) + getStbid();
			// EnvironmentUtils.writeToDataFile(context, PhoneInformation2.info, path);
			// }
			getInfo();

		} catch (Exception e) {
			//e.printStackTrace();
			Log.e(__CLASSNAME__ + "[System.err]", "[PARAM][NG][init()]" + Log2.getStackTraceString(e));
		}

	}

	public String getStbid() {
		return p_stbid;
	}

}
