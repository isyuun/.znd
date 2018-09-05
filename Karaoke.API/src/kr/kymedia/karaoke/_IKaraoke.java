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
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	_IKaraoke.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke
 *    |_ _IKaraoke.java
 * </pre>
 */
package kr.kymedia.karaoke;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-07-07
 */
public interface _IKaraoke extends IKaraoke4 {
	/**
	 * 전문마이너버전/배포회차 일치킬것
	 * <pre>
	 * <font color="blue"><b>■ 금영노래방 64차 Release - 1.22(22-2.64)</b></font>
	 * ■ 금영노래방 63차 Release - 1.21(21-2.63)
	 * -BUG: 리스트 사용자 이미지 클릭시 화면이동 오류
	 * -CHG: 리스트 사용자 이미지 동그라미 처리
	 * -CHG: 리스트 상하여백 늘임
	 * -CHG: Android 라이브러리업데이트
	 *     studio:2.1.3(build 143.3101438)->2.2.1(build 145.3330264)
	 *     platform-tools:24.0.4
	 *     support:23.2.0->23.2.1
	 *     build-tools:24.0.0->24.0.2
	 * -BUG: Android 라이브러리업데이트: 어플파일(classes.dex) 날짜정보조회오류
	 * -CHG: Android 라이브러리업데이트: android:singLine="true"->android:maxLines="1"
	 * </pre>
	 */
	public static final String P_VER = "2.64";
	/**
	 * UI확인용 디버그옵션
	 *
	 * @see IKaraoke2#DEBUG
	 */
	//public static boolean DEBUG = BuildConfig.DEBUG;
	public static boolean DEBUG = false;

	public class LOGIN extends IKaraoke4.LOGIN {
	};
}
