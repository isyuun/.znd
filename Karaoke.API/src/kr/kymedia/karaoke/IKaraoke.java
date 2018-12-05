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
 *    |_ IKaraoke.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke;

import java.io.File;

import android.app.Activity;
import android.os.Environment;

/**
 * TODO NOTE:<br>
 * 
 * <pre>
 * 구노래방책/노래방 기본정보 클래스<br>
 * 절~대 직접비지니스로직에서직접상속하지않는다.
 * 절~대 시스템리소스에직접근하지않는다.
 * 	final static Typeface TYPEFACE_KARAOKE = Typeface.createFromFile("/system/fonts/DroidSansFallback.ttf");
 * </pre>
 * 
 * @author isyoon
 * @since 2012. 12. 4.
 * @version 1.0
 */

interface IKaraoke {

	public static boolean DEBUG = true;

	/**
	 * 구노래방책 메뉴이동 관련변수
	 */
	@Deprecated
	final static String KEY_M1_CODE = "M1_CODE";
	@Deprecated
	final static String KEY_M2_CODE = "M2_CODE";
	@Deprecated
	final static String KEY_ACTIVITY = "ACTIVITY";
	@Deprecated
	final static String KEY_SEARCH_KEY = "SEARCH_KEY";
	@Deprecated
	final static String KEY_SEARCH_VALUE = "SEARCH_VALUE";
	@Deprecated
	final static String KEY_SEARCH_WHERE = "SEARCH_WHERE";
	@Deprecated
	final static String KEY_SUBJECT_ID = "SUBJECT_ID";

	/**
	 * 구노래방책 DB정보관련
	 */
	@Deprecated
	final static String INTENT_FILLTER_SONGDB_UPDATE = "AONMEDIA_KARAOKE_SONGDB_UPDATE";

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
	 * 녹음파일확장자(m4a)
	 */
	final static String RECFILE_EXT = "m4a"; // 녹음파일확장자(m4a)

	// final static String SKYMDOWN_URL = "http://cyms.chorus.co.kr/cykara_dl.asp?song_id=";
	//final static String SKYMDOWN_URL = "http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=";
	final static String SKYMDOWN_URL = "http://www.keumyoung.kr/.api/.skym.asp?song_id=";
	final static String MP3DOWN_URL = "http://www.ikaraoke.kr/common/sound.asp?song_id=";
	final static String LYCDOWN_URL = "http://www.ikaraoke.kr/common/lyrics.asp?song_id=";
	/**
	 * 플레이어 처리 상태값
	 */
	final static int STATE_SONG_DOWNLOAD_START = 0;
	final static int STATE_SONG_DOWNLOAD_RUNNING = 1;
	final static int STATE_SONG_DOWNLOAD_COMPLETE = 2;
	final static int STATE_SONG_DOWNLOAD_CANCEL = 3;
	final static int STATE_SONG_DOWNLOAD_ERROR = 4;
	final static int STATE_SONG_PLAY_START = 5;
	final static int STATE_SONG_PLAY_RUNNING = 6;
	final static int STATE_SONG_PLAY_PAUSE = 7;
	final static int STATE_SONG_PLAY_STOP = 8;
	final static int STATE_SONG_PLAY_ERROR = 9;
	final static int STATE_SONG_RECORD_START = 10;
	final static int STATE_SONG_RECORD_RUNNING = 11;
	final static int STATE_SONG_RECORD_PAUSE = 12;
	final static int STATE_SONG_RECORD_STOP = 13;
	final static int STATE_SONG_RECORD_ERROR = 14;
	final static int STATE_SONG_PLAY_MESSAGE = 15;

	/**
	 * 로컬데이터 처리 상태값
	 */
	final static int STATE_DATA_QUERY_START = 0;
	final static int STATE_DATA_QUERY_SUCCESS = 1;
	@Deprecated
	final static int STATE_DATA_QUERY_FINISH = 2;
	final static int STATE_DATA_QUERY_FAIL = 3;
	final static int STATE_DATA_QUERY_ERROR = 4;
	final static int STATE_DATA_QUERY_CANCEL = 5;

	/**
	 * 인텐트 액션 상태값
	 */
	final static int KARAOKE_INTENT_ACTION_DEFAULT = 0x01;
	final static int KARAOKE_INTENT_ACTION_LOGIN = 0x02;
	final static int KARAOKE_INTENT_ACTION_REFRESH = 0x03;
	final static int KARAOKE_INTENT_ACTION_SHARE = 0x04;
	final static int KARAOKE_INTENT_ACTION_COMMENT = 0x05;
	final static int KARAOKE_INTENT_ACTION_PICK = 0x06;
	final static int KARAOKE_INTENT_ACTION_PICK_FROM_IMAGE = 0x07;
	final static int KARAOKE_INTENT_ACTION_PICK_FROM_CAMERA = 0x08;
	final static int KARAOKE_INTENT_ACTION_CROP_FROM_IMAGE = 0x09;
	final static int KARAOKE_INTENT_ACTION_CROP_FROM_CAMERA = 0x10;
	final static int KARAOKE_INTENT_ACTION_CROP_FROM_FILE = 0x11;
	final static int KARAOKE_INTENT_ACTION_PERMISSION_UPDATE = 0x12;
	final static int KARAOKE_INTENT_ACTION_GOOGLE_ACCOUNT_PICKER = 0x13;


	// /**
	// * 액티비티 리턴 확인값 받아서 뭐할~까~~~
	// */
	final static int KARAOKE_RESULT_CANCEL = Activity.RESULT_CANCELED;
	final static int KARAOKE_RESULT_OK = Activity.RESULT_OK;
	final static int KARAOKE_RESULT_DEFAULT = Activity.RESULT_FIRST_USER;
	final static int KARAOKE_RESULT_REFRESH = Activity.RESULT_FIRST_USER + 1;
	final static int KARAOKE_RESULT_PLAYING = Activity.RESULT_FIRST_USER + 2;
	final static int KARAOKE_RESULT_INFORM = Activity.RESULT_FIRST_USER + 3;
	final static int KARAOKE_RESULT_FINISH = Activity.RESULT_FIRST_USER + 4;

	// /**
	// * 경로관련환경변수
	// */
	final static String ANDROID_DATA_FOLDER = "Android/data";
	final static String AONMEDIA_FOLDER = "aonmedia";
	final static String KARAOKE_FOLDER = "karaoke";
	final static String SKYM_FOLDER = "skym";
	final static String DATA_FOLDER = "data";
	final static String IMAGE_FOLDER = "image";
	final static String CACHE_FOLDER = "cache";
	final static String RECORD_FOLDER = "record";
	final static String MUSIC_FOLDER = "music";
	final static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	final static String KARAOKE_ROOT = SDCARD_ROOT + File.separator + ANDROID_DATA_FOLDER + File.separator + AONMEDIA_FOLDER + File.separator + KARAOKE_FOLDER;
	final static String SKYM_PATH = KARAOKE_ROOT + File.separator + SKYM_FOLDER;
	final static String DATABASE_PATH = KARAOKE_ROOT + File.separator + DATA_FOLDER;
	final static String CACHE_PATH = KARAOKE_ROOT + File.separator + CACHE_FOLDER;
	final static String IMAGE_PATH = KARAOKE_ROOT + File.separator + IMAGE_FOLDER;
	final static String IMAGE_CACHE_PATH = IMAGE_PATH + File.separator + CACHE_FOLDER;
	final static String RECORD_PATH = KARAOKE_ROOT + File.separator + RECORD_FOLDER;
	final static String KARAOKE_RECORD_PATH = SDCARD_ROOT + File.separator + KARAOKE_FOLDER + File.separator + RECORD_FOLDER;

	// 플레이어관련
	final static int SYNC_TEXT = 0;
	final static int SYNC_IMAGE = 1;
	final static int SYNC_READY = 2;
	final static int SYNC_STARTRAP = 5;
	final static int SYNC_ENDRAP = 6;
	final static int SYNC_ENDDIVISION = 7;
	final static int SYNC_NOMESSAGE = -1;

	final static int SYNC_STATUS_NO = 0;
	final static int SYNC_STATUS_READY = 1;
	final static int SYNC_STATUS_NEXT = 2;
	final static int SYNC_STATUS_NUM = 3;
	final static int SYNC_STATUS_TEXT = 4;
	final static int SYNC_STATUS_DEL = 5;
	final static int SYNC_STATUS_START = 6;
	final static int SYNC_STATUS_DIVISION = 7;

	final static int FILE_KYM = 0;
	final static int FILE_SKYM = 1;
	final static int FILE_MP3 = 2;
	final static int FILE_WMA = 3;
	final static int FILE_UNKNOWVERSION = 4;
	final static int FILE_INVALID = 5;
	final static int FILE_OPENERROR = 6;
	final static int FILE_ROK = 7;
	final static int FILE_AAC = 8;
	final static int FILE_SOK = 9;
	final static int FILE_MID = 10;

	final static int TIME_TEXT = 1500;
	final static int TIME_READY = 4000;

	final static int STATE_STOP = 0;
	final static int STATE_PLAY = 1;
	final static int STATE_PAUSE = 2;

	final static int LINE_VIEW = 2;

	final static int TIMER_ROTATE = 10000;
	final static int TIMER_TICK = 1000;
	final static int TIMER_FLIPER = 5000;
	final static int TIMER_PREVIEW = 60000;
	final static int TIMER_ADMOB = 3000;

	final static String SONGPLAYER_SONGNUMBER = "SONGPLAYER_SONGNUMBER";
	final static String SONGPLAYER_SONGLIST = "SONGPLAYER_SONGLIST";
	final static String SONGPLAYER_SONGITEMS = "SONGPLAYER_SONGITEMS";
	final static String SONGPLAYER_SONGIMAGE = "SONGPLAYER_SONGIMAGE";
	final static String SONGPLAYER_RECORDCOMPRESS = "SONGPLAYER_RECORDCOMPRESS";
	final static String SONGPLAYER_RECORDNAME = "SONGPLAYER_RECORDNAME";
	final static String SONGPLAYER_SONGNUMBERIMAGE = "SONGPLAYER_SONGNUMBERIMAGE";
}
