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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.TV
 * filename	:	Const2.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.tv
 *    |_ Const2.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.api;

/**
 * <pre>
 * 리모트키타입상수추가
 * </pre>
 *
 * @author isyoon
 * @since 2015. 2. 24.
 * @version 1.0
 */
public interface _Const extends Const2 {
	/**
	 * 전문마이너버전/배포회차 일치킬것
	 * <pre>
	 * <font color="blue"><b>■ KY노래방 37차 Release</b></font>
	 * -CHG: Android 라이브러리업데이트
	 *     studio:2.1.3(build 143.3101438)->2.2.0(build 145.3276617)
	 *     support:24.2.0->24.2.1
	 *     buildtool:24.0.0->24.0.2
	 * -BUG: Android 라이브러리업데이트 후 어플파일(classes.dex) 날짜정보조회오류
	 * <font color="red"><b>7.1.0.BTV.RC(710-2.37)</b></font>
	 * <font color="red"><b>2.0.0.GTV.CBT(200-2.37)</b></font>
	 * </pre>
	 */
	final static String P_VER = "2.37";
	/**
	 * 단말 정보
	 */
	/**
	 * 마켓구분
	 */
	final static String P_MARKET = "ATV";
	/**
	 * 기기구분
	 */
	final static String P_OS = "A";

	///*
	// * VASS
	// */
	//final static String VASS_REQUEST_PAGE = "http://vass.hanafostv.com:8080/service/serviceAction.hm?";
	//final static String M_PPV_PURCHASE = "purchasePpvProduct";
	//final static String M_PPV_CHECK = "checkPurchasePpvProduct";
	//final static String M_PPM_PURCHASE = "purchasePpxProduct";
	//final static String M_PPM_CHECK = "checkPurchasePpmProduct";
	//final static String VASS_PPM_PROMISE = "10";
	//final static String M_PASSWORD_CHECK = "checkPurchasePw";

	// 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// /**
	// * 상품코드(KP:id_product):SKT:BOX:월이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_MONTH_SKT_BOX = "2837471";
	// /**
	// * 상품코드(KP:id_product):SKT:BOX:일이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_DAY_SKT_BOX = "2837472";
	// /**
	// * 상품코드(KP:id_product):SKB:STB/UHD:월이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_MONTH_SKB_STBUHD = "2837449";
	// /**
	// * 상품코드(KP:id_product):SKB:STB/UHD:일이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_DAY_SKB_STBUHD = "2837470";
	// /**
	// * 상품코드(KP:id_product):SKB:AOSP:월이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_MONTH_BTV_1 = "3848347"; // 신규발급요
	// /**
	// * 상품코드(KP:id_product):SKB:AOSP:일이용권 - 이런미친븅신새끼!!!또꼬라박고지랄이네!!!
	// */
	// final static String VASS_PRODUCT_ID_DAY_BTV_1 = "3843733"; // 신규발급요

	/**
	 * 어플패키지네임(KP:p_apkname):데모
	 */
	final static String P_APKNAME_ATV = "kr.kymedia.kykaraoke.tv.atv";
	/**
	 * 어플패키지네임(KP:p_apkname):ST.COM
	 */
	final static String P_APKNAME_STC = "kr.kymedia.kykaraoke.tv.atv.st.com";
	/**
	 * 어플패키지네임(KP:p_apkname):GTV:GTV1/GTV2
	 */
	final static String P_APKNAME_GTV = "com.kumyoung.gtvkaraoke";
	/**
	 * 어플패키지네임(KP:p_apkname):SKT:BOX
	 * 어플패키지네임(KP:p_apkname):SKB:STB/UHD
	 * 어플패키지네임(KP:p_apkname):SKB:AOSP
	 */
	final static String P_APKNAME_BTV = "kr.kymedia.kykaraoke"; // 신규발급요

	/**
	 * 단말 인증값(KP:p_appname):데모
	 */
	final static String P_APPNAME_DEMO = "SMARTDEM";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):ST.COM
	 */
	final static String P_APPNAME_STC = "SMARTSTC";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):GTV:GTV1/GTV2
	 */
	final static String P_APPNAME_GTV = "SMARTGTV";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):SKT:BOX
	 */
	// final static String P_APPNAME_SKT_BOX = "kr.kymedia.kykaraokebox"; //2.0이하
	final static String P_APPNAME_SKT_BOX = "SMARTBOX";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):SKB:STB
	 */
	// final static String P_APPNAME_SKB_STB = "kr.kymedia.kykaraoke"; //2.0이하
	final static String P_APPNAME_SKB_STB = "SMARTSTB";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):SKB:UHD
	 */
	// final static String P_APPNAME_SKB_UHD = "kr.kymedia.kykaraokeuhd"; //2.0이하
	final static String P_APPNAME_SKB_UHD = "SMARTUHD";  // 2.0이후
	/**
	 * 단말 인증값(KP:p_appname):SKB:AOSP
	 */
	// final static String P_APPNAME_BTV_1 = "kr.kymedia.kykaraoke.tv.btv.1"; //2.0이하
	final static String P_APPNAME_BTV_1 = "SMARTBT1";  // 2.0이후

	/**
	 * 단말 인증키(KP:p_apikey):데모
	 */
	final static String P_APIKEY_DEMO = "SMARTDEMW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):ST.COM
	 */
	final static String P_APIKEY_STC = "SMARTSTCW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):데모
	 */
	final static String P_APIKEY_GTV = "SMARTGTVW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):SKT:BOX
	 */
	final static String P_APIKEY_SKT_BOX = "SMARTBOXW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):SKB:STB
	 */
	final static String P_APIKEY_SKB_STB = "SMARTSTBW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):SKB:UHD
	 */
	final static String P_APIKEY_SKB_UHD = "SMARTUHDW5KOUGVVSZ7Z";
	/**
	 * 단말 인증키(KP:p_apikey):SKB:AOSP stb
	 */
	final static String P_APIKEY_BTV_1 = "SMARTBT1W5KOUGVVSZ7Z";  // 신규발급요

	/**
	 * 단말 주소
	 */
	final static String P_DOMAIN_ATV = "http://stb.kymedia.kr/SmartSTBOX/index.php";
	final static String P_DOMAIN_ATV_TEST = "http://stb.mobileon.co.kr/SmartSTBOX/index.php";

	final static String P_DOMAIN_BTV = "http://stb.kymedia.kr/SmartSTBOX/index.php";
	final static String P_DOMAIN_BTV_TEST = "http://stb.mobileon.co.kr/SmartSTBOX/index.php";

	final static String P_DOMAIN_GTV = "http://lgu.keumyoung.kr/index.php";
	final static String P_DOMAIN_GTV_TEST = "http://dev.mobileon.co.kr/gTV/index.php";

	/**
	 * 마이크 이미지
	 */
	final static String MIC_URL_STB = "http://kumyoung.hscdn.com/Btv/help_buy_mike_stb.png";
	/**
	 * 마이크 이미지
	 */
	final static String MIC_URL_BOX = "http://kumyoung.hscdn.com/Btv/help_buy_mike.png";

}
