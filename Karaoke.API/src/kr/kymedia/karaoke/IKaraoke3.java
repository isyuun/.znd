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
 * filename	:	_IKaraoke.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop
 *    |_ _IKaraoke.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke;

/**
 * 
 * KPOP 노래방 기본정보 클래스<br>
 * 
 * @author isyoon
 * @since 2012. 7. 25.
 * @version 1.0
 */
interface IKaraoke3 extends IKaraoke2 {
	/**
	 * 전문테스트여부구분 - 릴리스전 반드시 풀어놓는다.
	 */
	// @Deprecated
	public static final boolean APP_API_TEST = false;
	/**
	 * 미탭스테스트여부구분 - 릴리스전 반드시 풀어놓는다.
	 */
	@Deprecated
	public static final boolean APP_API_AD_TEST = false;
	/**
	 * <pre>
	 * KPOP		: 61G3M106HGRWMRPVS7O5
	 * LGT 		: T25MG2BEW5KOUGVVSZ7Z
	 * 광고전문	: T25MG2BEW5KOUGMETAPS
	 * </pre>
	 */
	public static final String APP_API_KEY_KPOP = "61G3M106HGRWMRPVS7O5";
	public static final String APP_API_KEY_LGT = "T25MG2BEW5KOUGVVSZ7Z";
	public static final String APP_API_KEY_AD_TEST = "T25MG2BEW5KOUGMETAPS";

	public static final String APP_PACKAGE_NAME_KPOP = "kr.kymedia.karaoke.kpop";
	public static final String APP_PACKAGE_NAME_JPOP = "kr.kymedia.karaoke.jpop";
	public static final String APP_PACKAGE_NAME_NAVER = "kr.kymedia.karaoke.kpop.naver";
	public static final String APP_PACKAGE_NAME_S5 = "kr.kymedia.karaoke.kpop.s5";
	public static final String APP_PACKAGE_NAME_ONSPOT = "kr.kymedia.karaoke.kpop.onspot";
	public static final String APP_PACKAGE_NAME_INDIA = "kr.kymedia.karaoke.kpop.india";

	public static final String APP_DECRIPTION_KEY = "6390";
	public static final String APP_DECRIPTION_KEY_JPOP = "6390";

	/**
	 * 유튜브 API 서비스관련정보
	 * 
	 */
	public static final class YOUTUBE_API {
		/**
		 * Please replace this with a valid API key which is enabled for the
		 * YouTube Data API v3 service. Go to the
		 * <a href="https://code.google.com/apis/console/">Google APIs Console</a> to
		 * register a new developer key.
		 */
		public static final String DEVELOPER_KEY = "AIzaSyA676zAGol7s4pTMaiLgivWAbP6Avjo4-8";
	}

	/**
	 * 구글 API 서비스관련정보 푸시서비스(C2DM->GCM)
	 * 
	 */
	public static final class GOOGLE_API {
		final public static String GCM_URL_C2DM = "https://android.apis.google.com/c2dm/send";
		final public static String GCM_URL_GCM = "https://android.googleapis.com/gcm/send";

		final public static String INAPP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCEEn9kZEcR8ad96w4AdrhbD3wegqz7kWshs5kCILAtatE9FQKSkEboWkf6Xe05jYQIgeJ8ZvhyFCv9oag0EfDvPSKtoKKUhb1XOljprdGeggGrhuyfoX1LQtqlTL73MkUoRxgayMyBYw7uBYvQ4pc+a4yXuZDuYZcDBp+tDlIkxzsGfjr0CjS2fj0oe4xS3GNiO4UBV1YZQDxewu8sooJdscg4BpAKj1eSIp27YCdXC/VjSLnduBGp1639cSC04/LGUXFb0F7R2HRTTdp5HIGYyKRHbaJtY8gLVcG1eKr7BlB6bC1/Qr3HU6P0uRu4f9qmprkdROO1Bsb2o27x8TwIDAQAB";
		final public static String INAPP_PUBLIC_KEY_KPOP = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs3/eZv1AKSV6hJWdCCvOVqre6i2E2AHeYCJzEqGnSbmQ0Tf3nCu/7YGYrtOU4ywhWU+j3lJ+klnt0vnglwZSgXinU/AU4epxHu5jppUPSqkXujGAVFdj277/8GU5LJUWdrF5mkmvjapKY8+HgaRIpntEyBSmEUGACcsTRf1wgUY0is10vpy9RLMABeU9NE00+m9QWqBGlKZ88mebbC+URwES3xjDa5wREtTfgmi73IZmpFYqpgkkMZkuYguCF9CRa8TuzH4X5u5k4krXYZjwZo7b+bTXLQ+bDqMOr6eqLjvZImHPJkCbdk1vdW6Iq6iZmZJsGVjm7GGtnsnBGzXUqwIDAQAB";
		final public static String INAPP_PUBLIC_KEY_JPOP = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlvbuNJZP4ftFakSRe4fXlZM52Gx3LYr7INou6sNGUyyTuVo1tQJSD1q3xINWSdxZV0K/xoiQtVjzOh0j3WwyAtFsV7R2uwwxv0MMivW4rB3s8ubmrXZBKMUboNn6Tq+AmUSV9BM9+srWPdAvScoGhnfoYVVrNUW4RsGvo4FBNWb8lqbLbVl+BP3t5RnYUx6lTzEsVkaYJ3Ub9tTZe4fk/hX4LO9qtdBpbnjiFwufQ9Y+q3cpDcqxSwDtHKkMwUsbwzTiFESlRGYczCkxV0DEUdCacWYSDqu/BxQunUm7qnk7qtASoBe+mv5IJd5Yrv1nd3A/uRa6bR0jMJbjY9ioXwIDAQAB";

		final public static String GCM_SENDER_ID = "141208102948";
		final public static String GCM_SENDER_ID_KPOP = "141208102948";
		final public static String GCM_SENDER_ID_JPOP = "141208102948";

		@Deprecated
		final public static String GCM_SERVER_REAL_API_KEY = "AIzaSyB1GIRP8CKb2Z5hxbh_Pv51SfRiRoaxvtg";
		/**
		 * 전달용커스텀인텐트필터
		 */
		final public static String GCM_INTENT_FROM_GCM_REGISTRATION_CALLBACK = "kr.kymedia.karaoke.kpop.gcm.intent.REGISTRATION";
		/**
		 * 전달용커스텀인텐트필터
		 */
		final public static String GCM_INTENT_FROM_GCM_MESSAGE = "kr.kymedia.karaoke.kpop.gcm.intent.RECEIVE";
	}

	// 0) 애드립 - AD - 보류
	// final static String ADLIBR_API_KEY = "5184d07ae4b03c9009dfa5ae"; // 테스트 키 입니다.
	final static String ADLIBR_API_KEY = "529816f5e4b04e45144353c7";	// KPOP 키 입니다.

	// 1) 구글애드몹 - G
	// final static String ADMOB_ID_BANNER = "a153143dae21905";
	final static String ADMOB_ID_BANNER = "ca-app-pub-2048245184843302/5854033675";
	// final static String ADMOB_ID_POPUP = "a153143e0f54f6d";
	final static String ADMOB_ID_POPUP = "ca-app-pub-2048245184843302/1144632473";
	// final static String ADMOB_ID_BANNER_S5 = "a1530d6d5eb19c6";
	final static String ADMOB_ID_BANNER_S5 = "ca-app-pub-2048245184843302/4098098870";
	// final static String ADMOB_ID_POPUP_S5 = "a1530d6dfe3a955";
	final static String ADMOB_ID_POPUP_S5 = "ca-app-pub-2048245184843302/2621365672";
	// final static String ADMOB_ID_BANNER_NAVER = "a1530d6c918875d";
	final static String ADMOB_ID_BANNER_NAVER = "ca-app-pub-2048245184843302/8807500070";
	// final static String ADMOB_ID_POPUP_NAVER = "a1530d6ce95ce04";
	final static String ADMOB_ID_POPUP_NAVER = "ca-app-pub-2048245184843302/5714432870";
	// final static String ADMOB_ID_BANNER_KOR = "a152d7228b3dc52";
	final static String ADMOB_ID_BANNER_KOR = "ca-app-pub-2048245184843302/2760966471";
	// final static String ADMOB_ID_POPUP_KOR = "a152d617ea9fef5";
	final static String ADMOB_ID_POPUP_KOR = "ca-app-pub-2048245184843302/7330766870";
	// final static String ADMOB_ID_BANNER_JAP = "a15240e50773d8f";
	final static String ADMOB_ID_BANNER_JAP = "ca-app-pub-2048245184843302/7191166076";
	// final static String ADMOB_ID_POPUP_JAP = "a152d502b3f24d3";
	final static String ADMOB_ID_POPUP_JAP = "ca-app-pub-2048245184843302/4237699676";

	// 2) 다음아담 - D
	final static String ADAM_ID_BANNER = "8a48Z4HT14487158696";
	final static String ADAM_ID_POPUP = "8a49Z4IT1448715eabf";
	final static String ADAM_ID_BANNER_JAP = "2dedZ2xT137ca9d2e94";
	final static String ADAM_ID_POPUP_JAP = "89aaZ33T1446c94acb1";
	final static String ADAM_ID_BANNER_KOR = "2dedZ2xT137ca9d2e94";
	final static String ADAM_ID_POPUP_KOR = "89aaZ33T1446c94acb1";
	// final static String ADAM_ID_BANNER_NAVER = "89a8Z2mT1446c907ff6";
	// final static String ADAM_ID_POPUP_NAVER = "89abZ2oT1446c959ace";
	// final static String ADAM_ID_BANNER_S5 = "89acZ2pT1446c983724";
	// final static String ADAM_ID_POPUP_S5 = "89adZ34T1446c987682";

	// 3) 에어푸시 - A - 삭제
	// final static String AIRPUSH_KEY = "1339496570106950794";
	// final static String AIRPUSH_ID = "206486";
	// final static String AIRPUSH_ID_KOR = "56328";
	// final static String AIRPUSH_ID_JAP = "56328";

	// 4) 카울리 - C
	final static String CAULY_ID = "eYrcdkW5";

	// 5) T애드 - T
	final static String SKTAD_ID_POPUP = "AX1000B4D";
	final static String SKTAD_ID_BANNER = "AX1000B4D";
	// KPOP HOLIC KOR
	// 2012-06-19 AX1000B4D inApp 320 X 480 엔터테인먼트 0 0 0 0.00 서비스 중 off
	// 2012-06-19 AX0000B4D inApp 320 X 50 엔터테인먼트 0 0 0 0.00 서비스 중 off
	final static String SKTAD_ID_POPUP_KOR = "AX1000B4D";
	final static String SKTAD_ID_BANNER_KOR = "AX0000B4D";
	// 2014-02-14 AX00045C6 inApp 320 X 480 엔터테인먼트 0 0 0 0.00 등록미완료 off
	// 2014-02-14 AX00045C5 inApp 320 X 50 엔터테인먼트 0 0 0 0.00 등록미완료 off
	// KPOP HOLIC JAP
	final static String SKTAD_ID_POPUP_JAP = "AX00045C6";
	final static String SKTAD_ID_BANNER_JAP = "AX00045C5";

	// 6) INMOBI - I - 삭제
	final static String INMOBI_ID = "4028cbff379738bf01380336fdab18cf";

	// 7) Tapjoy - J
	final static String TAPJOY_APP_ID = "4567901f-bb04-4eaf-bf08-d2dc4da814aa";
	final static String TAPJOY_APP_KEY = "Y3RYmodDKAO9lDruxQTh";
	final static String TAPJOY_NOR_ID = "dcc69792-c626-42a7-a216-fea65b04ed05";
	final static String TAPJOY_SDK_KEY = "RWeQH7sETq-_CNLcTagUqgECY3RYmodDKAO9lDruxQThXESeqHINj_EK2hHb";
	final static String TAPJOY_APP_ID_KOR = "452e81b6-ca39-413a-81c2-7a795731f3de";
	final static String TAPJOY_APP_KEY_KOR = "9jEOohwkBnFcoJtKUuCM";
	final static String TAPJOY_NOR_ID_KOR = "d33ef9c2-208d-4b70-8b02-37dd6ad456d1";
	final static String TAPJOY_SDK_KEY_KOR = "RS6Btso5QTqBwnp5VzHz3gEC9jEOohwkBnFcoJtKUuCMAl0sYl-EYnCMD0TH";
	final static String TAPJOY_APP_ID_JAP = "3813ba24-e17a-4cf2-9446-9c2aee96f1b9";
	final static String TAPJOY_APP_KEY_JAP = "pq5y5qeYOrnkgC7GgNSh";
	final static String TAPJOY_NOR_ID_JAP = "433373f5-724b-4db2-abdc-faba3c97ab4b";
	final static String TAPJOY_SDK_KEY_JAP = "OBO6JOF6TPKURpwq7pbxuQECpq5y5qeYOrnkgC7GgNShRECkDMV0WJrxY_PT";

	// 8) TNK팩토리 - N - 매니페스트메타데이터입력
	// final static String TNK_ID = "2020a030-e081-a484-484a-180e0207090a";
	// final static String TNK_KEY = "62c8c2ff0ac2c3bbf83c83d2b53f8916";
	// final static String TNK_ID_KOR = "70a09020-0061-932a-a239-160002040e04";
	// final static String TNK_KEY_KOR = "e7b5d4bfe72b23e76059ab2e6582ed38";

	// 9) 애드팝콘 - P - 삭제
	final static String ADPOPCORN_KEY_MEDIA = "N1565535026";
	final static String ADPOPCORN_KEY_HASH = "af79a5862a1448be";

	// 10) 미탭스 - M
	final static String METAPS_CLIENT_ID = "FMGJZQDKVO0001";
	final static String METAPS_APPLICATION_ID = "NZKOZCIXBI0001";
	final static String METAPS_APPLICATION_KEY = "ak273468szavf7p";
	final static String METAPS_APPLICATION_ID_KOR = "MLCBVEWWWG0001";
	final static String METAPS_APPLICATION_KEY_KOR = "qgzmrfm1q6gyn7q";
	final static String METAPS_APPLICATION_ID_JAP = "IMCEJWHAIK0001";
	final static String METAPS_APPLICATION_KEY_JAP = "4oyj25ohr2thwh6";
	// final static String METAPS_APPLICATION_ID_NAVER = "NACKLMFIUF0001";
	// final static String METAPS_APPLICATION_KEY_NAVER = "5ylemlu8innhgr8";
	// final static String METAPS_APPLICATION_ID_S5 = "ZOSQWANLUO0001";
	// final static String METAPS_APPLICATION_KEY_S5 = "d4kxy9m9u9vwvs2";

	// 11) 챠트부스트 - CB
	final static String CHARTBOOST_ID = "53157e74f8975c082e431ddc";
	final static String CHARTBOOST_SIGNATURE = "d07ea63680a6dd1298c8c63a10e97e5f3997b5c9";
	final static String CHARTBOOST_ID_KOR = "507678b317ba47553f000033";
	final static String CHARTBOOST_SIGNATURE_KOR = "2ea142c9f0bb1bd7a451d84525a6c48fa6794bbc";
	final static String CHARTBOOST_ID_JAP = "5264c44317ba47137900000f";
	final static String CHARTBOOST_SIGNATURE_JAP = "3afcf2baec6626dc619bd60416c27a7e9e1be9d2";

	// 12) 애드콜로니 - AC - 삭제
	final static String ADCOLONY_ID = "app427bf3e30cc0481b931d4c";
	final static String ADCOLONY_KEY = "v4vc3f227302f7934ea288e80e";
	final static String ADCOLONY_ID_ZONE = "vz6c110f9c1845475781c6ef";

	// 13) kiip - K - 삭제
	final static String KIIP_APP_KEY = "154739909635bcf8c044d7085819832e";
	final static String KIIP_APP_SECRET = "f36973d061f95524e72af59da609f10b";
	final static String KIIP_ID_ACHIEVEMENT = "0";
	final static String KIIP_ID_LEADERBOARD = "0";

	// 14) w3i - W - 삭제
	final static int W3I_APP_ID = 17091;
	final static int W3I_APP_ID_KOR = 12109;
	final static int W3I_APP_ID_JAP = 17115;
	// final static int W3I_APP_ID_NAVER = 17114;
	// final static int W3I_APP_ID_S5 = 17116;

	// 15) PlayHaven - "PH" - 삭제
	final static String PLAYHAVEN_TOKEN = "217c69c150ce42dea6b15fe9c47731cf";
	final static String PLAYHAVEN_SECRET = "3c3c5e4d171f4446a2cf24eab977d16c";

	// 16) TAPAD - TA
	final static String TAPAD_ADVERTISER_ID = "xRJGTz+qOwNxOi9WMtt4nw==";
	final static String TAPAD_APP_ID = "KCcr6kGag9TBs2+C1klecA==";

	// 17) Flurry - F
	final static String FLURRY_API_KEY = "GG5QJBYZZXK27W979X2R";
	final static String FLURRY_API_KEY_KOR = "V4WPJ6H5HRR5G88PZR49";
	final static String FLURRY_API_KEY_JPOP = "FZBZG5HJ6MCZYQ8Z4WSX";
	// final static String FLURRY_API_KEY_NAVER = "HY2TCWMR36GHB2WYRZYT";
	// final static String FLURRY_API_KEY_S5 = "T6QYS5BWN9JZP64JHHJ4";

	// 18) 네이버애드포스트 - NA
	final static String NAVERADPOST_ID = "mandroid_eca9250756734ba484ac13dd797d7507";
	final static String NAVERADPOST_ID_KOR = "mandroid_e56be7e7840e45b981a9d067c9b7473c";
	final static String NAVERADPOST_ID_JAP = "mandroid_bb13b05a1d80435298a1c08844f550c8";
	// final static String NAVERADPOST_ID_NAVER = "mandroid_8ce3cdf8f9d04599ab49a5ece38bad36";
	// final static String NAVERADPOST_ID_S5 = "mandroid_4a560689e54045d5be2a44d5410032f0";

	// 19) 그레이스트라이프 - Greystripe/ValueClickMedia - GS
	final static String GREYSTRIPE_GUID_KOR = "ec88bfb4-624a-4404-b06b-747412374c35";
	final static String GREYSTRIPE_GUID_JAP = "34280a62-3b44-11e3-a724-b060d99cb883";

	// 22) SponsorPay - S -> "S2"
	final static String SPONSORPAY_APP_ID = "20444";
	final static String SPONSORPAY_SECURITY_TOKEN = "830e7b2157d5301a3a4960861e400bdf";
	final static String SPONSORPAY_APP_ID_KOR = "12555";
	final static String SPONSORPAY_SECURITY_TOKEN_KOR = "a01602bff55b1e3ccd9952fd980f61b0";
	final static String SPONSORPAY_APP_ID_JAP = "20453";
	final static String SPONSORPAY_SECURITY_TOKEN_JAP = "9bc792962b8efedff2188c55b0fd8ddc";
	// final static String SPONSORPAY_APP_ID_NAVER = "20456";
	// final static String SPONSORPAY_SECURITY_TOKEN_NAVER = "d9ab2ed336c5c62f40bc6e920c376643";
	// final static String SPONSORPAY_APP_ID_S5 = "20459";
	// final static String SPONSORPAY_SECURITY_TOKEN_S5 = "a12a7f5e20e5dd88052663180573b5d7";

	// 23) Vungle Dashboard - V - 보류
	final static String VUNGLE_APP_ID = APP_PACKAGE_NAME_KPOP;

	// 24) DirectTap - DA
	final static String DIRECTTAP_APP_CODE = "1af6ea457e3b4df71705394e5c017d47e4d9130501";
	final static String DIRECTTAP_APP_CODE_KPOP = "af963b1ded44511915230feba0c35c38647b130501";
	final static String DIRECTTAP_APP_CODE_JPOP = "bcec2f730a2e68b51524d1ab063b54502039130501";

	// 25) StartApp - SA
	final static String STARTAPP_DEV_ID = "103371712";
	final static String STARTAPP_APP_ID = "203516308";
	final static String STARTAPP_APP_ID_KPOP = "203812065";
	final static String STARTAPP_APP_ID_JPOP = "203224178";
	// final static String STARTAPP_APP_ID_NAVER = "203010510";
	// final static String STARTAPP_APP_ID_S5 = "203563573";

}
