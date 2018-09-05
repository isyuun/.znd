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
 * filename	:	IKaraoke4.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke
 *    |_ IKaraoke4.java
 * </pre>
 */
package kr.kymedia.karaoke;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @since 2016-05-19
 * @version 1.0
 */
interface IKaraoke4 extends IKaraoke3 {

	public class LOGIN {
		public static String KEY_LOGIN_CHECK = "login_check";
		public static String KEY_NETWORK_USEMOBILE = "network_usemobile";
		public static String KEY_MID = "mid";
		public static String KEY_UID = "uid";
		public static String KEY_EMAIL = "email";
		public static String KEY_PASSTYPE = "passtype";
		public static String KEY_FREE = "free";
		public static String KEY_NICKNAME = "nickname";
		public static String KEY_BIRTHDAY = "birthday";
		public static String KEY_SEX = "sex";
		public static String KEY_PWD = "pwd";
		public static String KEY_GOODSNAME = "goodsname";
		public static String KEY_GOODSCODE = "goodscode";
		public static String KEY_NOTIFICATION_SOUND = "notification_sound";
		public static String KEY_NOTIFICATION_VIBRATION = "notification_vibration";
	}

	public static enum KARAOKE_RESULT {

		KARAOKE_RESULT_CANCEL(IKaraoke3.KARAOKE_RESULT_CANCEL),
		KARAOKE_RESULT_OK(IKaraoke3.KARAOKE_RESULT_OK),
		KARAOKE_RESULT_DEFAULT(IKaraoke3.KARAOKE_RESULT_DEFAULT),
		KARAOKE_RESULT_REFRESH(IKaraoke3.KARAOKE_RESULT_REFRESH),
		KARAOKE_RESULT_PLAYING(IKaraoke3.KARAOKE_RESULT_PLAYING),
		KARAOKE_RESULT_INFORM(IKaraoke3.KARAOKE_RESULT_INFORM),
		KARAOKE_RESULT_FINISH(IKaraoke3.KARAOKE_RESULT_FINISH);

		private final int value;

		KARAOKE_RESULT(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static KARAOKE_RESULT get(int value) {
			KARAOKE_RESULT ret = null;
			for (KARAOKE_RESULT remote : KARAOKE_RESULT.values()) {
				if (value == remote.value()) {
					ret = remote;
				}
			}
			return ret;
		}
	}

	public static enum KARAOKE_REQUEST {

		//
		KARAOKE_INTENT_ACTION_DEFAULT(IKaraoke3.KARAOKE_INTENT_ACTION_DEFAULT),
		KARAOKE_INTENT_ACTION_LOGIN(IKaraoke3.KARAOKE_INTENT_ACTION_LOGIN),
		KARAOKE_INTENT_ACTION_REFRESH(IKaraoke3.KARAOKE_INTENT_ACTION_REFRESH),
		KARAOKE_INTENT_ACTION_SHARE(IKaraoke3.KARAOKE_INTENT_ACTION_SHARE),
		KARAOKE_INTENT_ACTION_COMMENT(IKaraoke3.KARAOKE_INTENT_ACTION_COMMENT),
		//사진편집
		KARAOKE_INTENT_ACTION_PICK(IKaraoke3.KARAOKE_INTENT_ACTION_PICK),
		KARAOKE_INTENT_ACTION_PICK_FROM_IMAGE(IKaraoke3.KARAOKE_INTENT_ACTION_PICK_FROM_IMAGE),
		KARAOKE_INTENT_ACTION_PICK_FROM_CAMERA(IKaraoke3.KARAOKE_INTENT_ACTION_PICK_FROM_CAMERA),
		KARAOKE_INTENT_ACTION_CROP_FROM_IMAGE(IKaraoke3.KARAOKE_INTENT_ACTION_CROP_FROM_IMAGE),
		KARAOKE_INTENT_ACTION_CROP_FROM_CAMERA(IKaraoke3.KARAOKE_INTENT_ACTION_CROP_FROM_CAMERA),
		KARAOKE_INTENT_ACTION_CROP_FROM_FILE(IKaraoke3.KARAOKE_INTENT_ACTION_CROP_FROM_FILE),
		//권한설정
		KARAOKE_INTENT_ACTION_PERMISSION_UPDATE(IKaraoke3.KARAOKE_INTENT_ACTION_PERMISSION_UPDATE),
		//계정확인
		KARAOKE_INTENT_ACTION_GOOGLE_ACCOUNT_PICKER(IKaraoke3.KARAOKE_INTENT_ACTION_GOOGLE_ACCOUNT_PICKER);

		private final int value;

		KARAOKE_REQUEST(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static KARAOKE_REQUEST get(int value) {
			KARAOKE_REQUEST ret = null;
			for (KARAOKE_REQUEST remote : KARAOKE_REQUEST.values()) {
				if (value == remote.value()) {
					ret = remote;
				}
			}
			return ret;
		}
	}
}
