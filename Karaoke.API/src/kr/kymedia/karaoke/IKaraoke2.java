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

import kr.kymedia.karaoke.api.BuildConfig;

import android.os.Build;

//import kr.kymedia.karaoke.kpop.lib.BuildConfig;

/**
 * 
 * TODO NOTE:<br>
 * 노래방 기본정보 클래스<br>
 * 
 * @author isyoon
 * @since 2012. 7. 25.
 * @version 1.0
 */

interface IKaraoke2 extends IKaraoke {
	/**
	 * 전문확인용 디버그옵션
	 */
	public static boolean DEBUG = BuildConfig.DEBUG;
	// test
	// public static boolean DEBUG = true;

	/**
	 * 마시멜로이상 6.0(23)
	 */
	public static final boolean IS_ABOVE_MARSHMELLOW = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
	/**
	 * 롤리팝이상 5.0(21)
	 */
	public static final boolean IS_ABOVE_LOLLIPOP = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
	/**
	 * 킷켓이상 4.4(19)
	 */
	public static final boolean IS_ABOVE_KITKAT = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
	/**
	 * 젤리빈이상 4.1(16)
	 */
	public static final boolean IS_ABOVE_JELLY_BEAN = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN);
	/**
	 * ICS이상 4.0(14)
	 */
	public static final boolean IS_ABOVE_ICE_CREAM_SANDWICH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	/**
	 * 허니콤이상 3.0(11)
	 */
	public static final boolean IS_ABOVE_HONEYCOMB = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	public static final boolean IS_HONEYCOMB = (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR2);
	/**
	 * 진저브레드이상 2.3.3(10)
	 */
	public static final boolean IS_ABOVE_GINGERBREAD_MR1 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1);

	/**
	 * 인텐트 테이터 전달관련
	 */
	@Deprecated
	final static String KEY_KPOP_LISTINDEX = "KPOP_LISTINDEX";
	/**
	 * 이전화면 "info" 태그데이터
	 */
	final static String KEY_KPOP_INFOITEM = "KPOP_INFOITEM";
	/**
	 * 이전화면 "list" 태그데이터
	 */
	final static String KEY_KPOP_LISTITEM = "KPOP_LISTITEM";
	/**
	 * 이전화면 "list" 태그데이터 목록
	 */
	@Deprecated
	final static String KEY_KPOP_LISTITEMS = "KPOP_LISTITEMS";

	/**
	 * 전문데이터 오류상태값 - 22222 : IOException - IOException(?) 오류관련
	 */
	final static String ERROR_CODE_IOEXCEPTION = "22222";
	/**
	 * 전문데이터 오류상태값 - 33333 : UnknownHostException - 네트워크(?) 오류관련
	 */
	final static String ERROR_CODE_UNKNOWNHOSTEXCEPTION = "33333";
	/**
	 * 전문데이터 오류상태값 - 44444 : UnknownServiceException - 웹(?)서비스 오류관련
	 */
	final static String ERROR_CODE_UNKNOWNSERVICEEXCEPTION = "44444";
	/**
	 * 전문데이터 오류상태값 - 55555 : SocketTimeoutException - 타임아웃 오류관련
	 */
	final static String ERROR_CODE_TIMEOUTEXCEPTION = "55555";
	/**
	 * 전문데이터 오류상태값 - 66666 : SocketException - 소켓(?) 오류관련
	 */
	final static String ERROR_CODE_SOCKETEXCEPTION = "66666";
	/**
	 * 전문데이터 오류상태값 - 77777 : HttpResponseException - HTTP 오류관련
	 */
	final static String ERROR_CODE_HTTPRESPONSEEXCEPTION = "77777";
	/**
	 * 전문데이터 오류상태값 - 88888 : JSONDataParsingErorr - JSON데이터 오류관련
	 */
	final static String ERROR_CODE_JSONDATAPARSINGERORR = "88888";
	/**
	 * 전문데이터 오류상태값 - 99999 : UnknownDataError - 그외 오류관련
	 */
	final static String ERROR_CODE_UNKOWNDATAERROR = "99999";

	/**
	 * 전문데이터 오류상태값 - 22222 : IOException - IOException(?) 오류관련
	 */
	final static String ERROR_MESSAGE_IOEXCEPTION = "IOException";
	/**
	 * 전문데이터 오류상태값 - 33333 : UnknownHostException - 네트워크(?) 오류관련
	 */
	final static String ERROR_MESSAGE_UNKNOWNHOSTEXCEPTION = "UnknownHostException";
	/**
	 * 전문데이터 오류상태값 - 44444 : UnknownServiceException - 웹(?)서비스 오류관련
	 */
	final static String ERROR_MESSAGE_UNKNOWNSERVICEEXCEPTION = "UnknownServiceException";
	/**
	 * 전문데이터 오류상태값 - 55555 : SocketTimeoutException - 타임아웃 오류관련
	 */
	final static String ERROR_MESSAGE_TIMEOUTEXCEPTION = "TimeoutException";
	/**
	 * 전문데이터 오류상태값 - 66666 : SocketException - 소켓(?) 오류관련
	 */
	final static String ERROR_MESSAGE_SOCKETEXCEPTION = "SocketException";
	/**
	 * 전문데이터 오류상태값 - 77777 : HttpResponseException - HTTP 오류관련
	 */
	final static String ERROR_MESSAGE_HTTPRESPONSEEXCEPTION = "HttpResponseException";
	/**
	 * 전문데이터 오류상태값 - 88888 : JSONDataParsingErorr - JSON데이터 오류관련
	 */
	final static String ERROR_MESSAGE_JSONDATAPARSINGERORR = "JSONDataParsingErorr";
	/**
	 * 전문데이터 오류상태값 - 99999 : UnknownDataError - 그외 오류관련
	 */
	final static String ERROR_MESSAGE_UNKOWNDATAERROR = "UnkownDataError";

	/**
	 * 전문데이터 처리상태값 - 전문시작
	 */
	final static int STATE_DATA_QUERY_START = 0;
	/**
	 * 전문데이터 처리상태값 - 전문성공
	 */
	final static int STATE_DATA_QUERY_SUCCESS = 1;
	/**
	 * 전문데이터 처리상태값 - 전문종료 - 사용안함.
	 */
	@Deprecated
	final static int STATE_DATA_QUERY_FINISH = 2;
	/**
	 * 전문데이터 처리상태값 - 전문실패 - 접속,전송등...실패
	 */
	final static int STATE_DATA_QUERY_FAIL = 3;
	/**
	 * 전문데이터 처리상태값 - 전문오류 - 전송후 - 데이터오류,전문오류코드등...오류
	 */
	final static int STATE_DATA_QUERY_ERROR = 4;
	/**
	 * 전문데이터 처리상태값 - 전문취소
	 */
	@Deprecated
	final static int STATE_DATA_QUERY_CANCEL = 5;

}
