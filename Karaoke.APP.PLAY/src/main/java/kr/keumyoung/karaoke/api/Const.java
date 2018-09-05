package kr.keumyoung.karaoke.api;

import android.graphics.Typeface;

interface Const {
	/*
	 * STATE
	 */
	final static int STATE_HOME_MENU = 1;
	final static int STATE_MAIN_MENU = 2;
	final static int STATE_SING_MENU = 3;
	final static int STATE_SING_GENRE = 4;
	final static int STATE_SONG_LIST = 5;
	final static int STATE_SONG_LIST_DETAIL = 6;
	final static int STATE_MY_MENU = 7;
	final static int STATE_SHOP_MENU = 8;
	final static int STATE_SHOP_TICKET = 9;
	final static int STATE_SHOP_CERTIFY = 10;
	final static int STATE_CUSTOMER_MENU = 11;
	final static int STATE_CUSTOMER_LIST = 12;
	final static int STATE_CUSTOMER_LIST_DETAIL = 13;
	//븅신개삽지랄...안쓸껀왜만들어선!!!
	//final static int STATE_CUSTOMER_APP = 14;
	//븅신개삽지랄...안쓸껀왜만들어선!!!
	//final static int STATE_CUSTOMER_MIC = 15;
	final static int STATE_SEARCH_MENU = 16;
	final static int STATE_SEARCH_SELF = 17;
	final static int STATE_SEARCH_LETTER_KOR = 18;
	final static int STATE_SEARCH_LETTER_ENG = 19;
	final static int STATE_SEARCH_LETTER_NUM = 20;
	final static int STATE_SEARCH_LIST = 21;
	final static int STATE_MY_LIST = 22;
	final static int STATE_LISTEN_MENU = 23;
	final static int STATE_LISTEN_LIST = 24;
	final static int STATE_LISTENING = 25;
	final static int STATE_SEARCH_LIST_DETAIL = 26;
	final static int STATE_LISTEN_OTHER = 27;
	final static int STATE_CERTIFY_HP = 28;
	final static int STATE_CERTIFY = 29;
	final static int STATE_MESSAGE_OK = 30;
	final static int STATE_MESSAGE_PPX_INFO = 31;
	final static int STATE_MESSAGE_PPX_NOTICE = 35;
	final static int STATE_MESSAGE_PPX_PASS = 32;
	final static int STATE_MESSAGE_PPX_SUCCESS = 33;
	//븅신개삽지랄...안쓸껀왜만들어선!!!
	//final static int STATE_MESSAGE_PPM_INFO = 34;
	//븅신개삽지랄...안쓸껀왜만들어선!!!
	//final static int STATE_MESSAGE_PPM_PASS = 36;
	//븅신개삽지랄...안쓸껀왜만들어선!!!
	//final static int STATE_MESSAGE_PPM_SUCCESS = 37;
	final static int STATE_MESSAGE_GO_CERTIFY = 38;
	final static int STATE_MY_RECORD_LIST = 39;
	final static int STATE_MY_RECORD_NONE = 40;
	final static int STATE_MY_RECORD_BEFORE = 41;
	final static int STATE_CUSTOMER_LIST_EVENT = 42;
	final static int STATE_EVENT_HP = 43;

	/*
	 * 키 타입
	 */
	final static int REMOTE_NONE = 0;
	final static int REMOTE_LEFT = 1;
	final static int REMOTE_RIGHT = 2;
	final static int REMOTE_UP = 3;
	final static int REMOTE_DOWN = 4;
	final static int REMOTE_ENTER = 5;
	final static int REMOTE_RETURN = 6;
	final static int REMOTE_INIT = 7;
	final static int REMOTE_MENU = 8;

	/*
	 * 재생 종류
	 */
	final static int REDRAW = 1;
	final static int NEWDRAW = 2;

	/*
	 * 팝업
	 */
	final static int POPUP_NONE = 0;
	final static int POPUP_OK = 1;
	final static int POPUP_BACK = 2;
	final static int POPUP_CANCEL = 3;
	final static int POPUP_EXIT = 4;

	/*
	 * 팝업 닫힘
	 */
	final static int CLOSE_AUTO = 1;
	final static int CLOSE_OK = 2;
	final static int CLOSE_AUTO_LONG = 3;
	final static int CLOSE_AUTO_MID_BOTTOMRIGHT = 4;

	final static int TIMER_CLOSE_AUTO = 3000;
	final static int TIMER_CLOSE_AUTO_LONG = 5000;
	/*
	 * 화면 구분
	 */
	/**
	 * 홈화면:진짜PAIN이다...그지?
	 */
	final static int PANE_HOME = 1;
	/**
	 * 메인화면:진짜PAIN이다...그지?
	 */
	final static int PANE_MAIN = 2;

	/*
	 * 로딩
	 */
	final static int LOADING_NONE = 1;
	final static int LOADING_SHORT = 1;
	final static int LOADING_LONG = 2;
	final static int LOADING_SING = 3;
	final static int LOADING_LISTEN = 4;

	/*
	 * 재생
	 */
	final static int STOP = 1;
	final static int NEXT = 2;
	final static int PAUSE = 3;
	final static int PLAY = 4;

	/*
	 * 직접 검색
	 */
	final static int TITLE = 0;
	final static int ARTIST = 1;

	/*
	 * 색인 검색
	 */
	final static int KOR = 0;
	final static int ENG = 1;
	final static int NUM = 2;

	final static int ON = 0;
	final static int SELECTED = 1;

	/*
	 * 메인 이벤트 / 공지사항
	 */
	final static String CUSTOMER_ENTER_KEY = "KEY";
	final static String CUSTOMER_ENTER_EVENT = "EVENT";
	final static String CUSTOMER_ENTER_NOTICE = "NOTICE";

	/**
	 * KP/VASS공용:전문오류발생
	 */
	final static int COMPLETE_ERROR_REQUEST_NOT_RESPONSE = 500;

	/**
	 * 곡시작대기시간
	 */
	final static int TIMEOUT_WAIT_START_SING = 3 * 1000 /* milliseconds */;
	///**
	// * 로딩중지대기시간
	// */
	//final static int TIMEOUT_HIDE_LOADING_SHORT = 5 * 1000 /* milliseconds */;
	/**
	 * 로딩중지대기시간
	 */
	final static int TIMEOUT_HIDE_LOADING_DELAY = 500 /* milliseconds */;

	/**
	 * 버전정보숨기기:
	 */
	final static int TIMEOUT_HIDE_VERSION_INFO = 10 * 1000 /* milliseconds */;
	/**
	 * KP/VASS공용:전문TIMEOUT:
	 */
	final static int TIMEOUT_NOT_RESPONSE = 30 * 1000 /* milliseconds */;
	/**
	 * KP/VASS공용:전문TIMEOUT:
	 */
	final static int TIMEOUT_REQUEST_READ = 10 * 1000 /* milliseconds */;
	/**
	 * KP/VASS공용:전문TIMEOUT:
	 */
	final static int TIMEOUT_REQUEST_CONNECT = 15 * 1000 /* milliseconds */;

	/*
	 * 파일 다운 요청 형식
	 */
	final static int REQUEST_FILE_ARTIST_IMAGE = 1;
	final static int REQUEST_FILE_SONG = 2;
	final static int REQUEST_FILE_LISTEN = 3;
	final static int REQUEST_FILE_LISTEN_OTHER = 4;

	/*
	 * 파일 다운 완료 형식
	 */
	final static int COMPLETE_DOWN_ARTIST_IMAGE = 1001;
	final static int COMPLETE_DOWN_SONG = 1002;
	final static int COMPLETE_DOWN_LISTEN = 1003;
	final static int COMPLETE_DOWN_LISTEN_OTHER = 1004;

	/*
	 * JSON 요청 형식
	 */
	final static int REQUEST_NONE = -1;
	final static int REQUEST_MAIN = 0;
	final static int REQUEST_SONG_LIST = 1;
	final static int REQUEST_SONG_FILE = 2;
	final static int REQUEST_SONG_SYNC = 3;
	final static int REQUEST_FAVOR = 4;
	final static int REQUEST_CUSTOMER_LIST = 5;
	final static int REQUEST_CUSTOMER_LIST_DETAIL = 6;
	final static int REQUEST_SEARCH_LIST = 7;
	final static int REQUEST_SONG_PLAY = 8;
	final static int REQUEST_LISTEN_LIST = 9;
	final static int REQUEST_LISTEN_SONG = 10;
	final static int REQUEST_LISTEN_OTHER = 11;
	final static int REQUEST_NUMBER_SEARCH = 12;
	final static int REQUEST_LISTEN_SONG_OTHER = 13;
	final static int REQUEST_SONG_PLAYED_TIME = 14;
	final static int REQUEST_LISTEN_PLAYED_TIME = 15;
	final static int REQUEST_AUTH_NUMBER = 16;
	final static int REQUEST_CERTIFY_STATE = 17;
	final static int REQUEST_AUTH_NUMBER_CORRECT = 18;
	final static int REQUEST_MY_RECORD_LIST = 19;
	final static int REQUEST_MY_SUB_MENU = 20;
	final static int REQUEST_SHOP_SUB_MENU = 21;
	final static int REQUEST_TICKET_SALES_STATE = 22;
	final static int REQUEST_TICKET_PURCHASE_COMPLETE = 23;
	//final static int REQUEST_YEAR_PURCHASE_COMPLETE = 23;
	//final static int REQUEST_MONTH_PURCHASE_COMPLETE = 24;
	//final static int REQUEST_DAY_PURCHASE_COMPLETE = 25;
	final static int REQUEST_EVENT_LIST = 26;
	final static int REQUEST_EVENT_LIST_DETAIL = 27;
	final static int REQUEST_EVENT_APPLY = 28;
	final static int REQUEST_EVENT_HP = 29;
	final static int REQUEST_COUPON_REGIST = 30;

	/*
	 * JSON 완료 형식
	 */
	final static int COMPLETE_NONE = -1;
	final static int COMPLETE_MAIN = 200;
	final static int COMPLETE_SONG_LIST = 201;
	final static int COMPLETE_FAVOR = 302;
	final static int COMPLETE_CUSTOMER_LIST = 303;
	final static int COMPLETE_CUSTOMER_LIST_DETAIL = 304;
	final static int COMPLETE_SEARCH_LIST = 305;
	final static int COMPLETE_SONG_PLAY = 306;
	final static int COMPLETE_LISTEN_LIST = 307;
	final static int COMPLETE_LISTEN_SONG = 308;
	final static int COMPLETE_LISTEN_OTHER = 309;
	final static int COMPLETE_NUMBER_SEARCH = 310;
	final static int COMPLETE_LISTEN_OTHER_SONG = 311;
	final static int COMPLETE_SONG_PLAYED_TIME = 312;
	final static int COMPLETE_LISTEN_PLAYED_TIME = 313;
	final static int COMPLETE_AUTH_NUMBER = 314;
	final static int COMPLETE_CERTIFY_STATE = 315;
	final static int COMPLETE_AUTH_NUMBER_CORRECT = 316;
	final static int COMPLETE_MY_RECORD_LIST = 317;
	final static int COMPLETE_MY_SUB_MENU = 318;
	final static int COMPLETE_SHOP_SUB_MENU = 319;
	final static int COMPLETE_TICKET_SALES_STATE = 320;
	final static int COMPLETE_EVENT_APPLY = 321;
	final static int COMPLETE_EVENT_HP = 322;
	final static int COMPLETE_COUPON_REGIST = 323;

	/*
	 * UTIL 요청 형식
	 */
	final static int REQUEST_UTIL_MAIN_EVENT_IMAGE = 0;
	final static int REQUEST_UTIL_CUSTOMER_DETAIL_IMAGE = 1;
	final static int REQUEST_UTIL_PROFILE_IMAGE_1 = 2;
	final static int REQUEST_UTIL_PROFILE_IMAGE_2 = 3;
	final static int REQUEST_UTIL_PROFILE_IMAGE_3 = 4;
	final static int REQUEST_UTIL_PROFILE_IMAGE_4 = 5;
	final static int REQUEST_UTIL_PROFILE_IMAGE_5 = 6;
	final static int REQUEST_UTIL_PROFILE_IMAGE_6 = 7;
	final static int REQUEST_UTIL_PROFILE_IMAGE_7 = 8;
	final static int REQUEST_UTIL_PROFILE_IMAGE_8 = 9;
	final static int REQUEST_UTIL_PROFILE_IMAGE_HOME = 10;
	final static int REQUEST_UTIL_CERTIFY_PROFILE_IMAGE = 11;
	final static int REQUEST_UTIL_MY_RECORD_PROFILE_IMAGE = 12;
	final static int REQUEST_UTIL_MAIN_QUICK_IMAGE_01_ON = 13;
	final static int REQUEST_UTIL_MAIN_QUICK_IMAGE_01_OFF = 14;
	final static int REQUEST_UTIL_MAIN_QUICK_IMAGE_02_ON = 15;
	final static int REQUEST_UTIL_MAIN_QUICK_IMAGE_02_OFF = 16;
	final static int REQUEST_UTIL_EVENT_DETAIL_ON = 17;
	final static int REQUEST_UTIL_EVENT_DETAIL_OFF = 18;
	final static int REQUEST_UTIL_SHOP_ITEM_01 = 19;
	final static int REQUEST_UTIL_SHOP_ITEM_02 = 20;
	final static int REQUEST_UTIL_KY_LOGO = 21;
	final static int REQUEST_UTIL_MIC = 22;

	/*
	 * UTIL 완료 형식
	 */
	final static int COMPLETE_UTIL_EVENT_IMAGE = 400;
	final static int COMPLETE_UTIL_CUSTOMER_DETAIL_IMAGE = 401;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_1 = 402;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_2 = 403;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_3 = 404;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_4 = 405;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_5 = 406;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_6 = 407;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_7 = 408;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_8 = 409;
	final static int COMPLETE_UTIL_PROFILE_IMAGE_HOME = 410;
	final static int COMPLETE_UTIL_CERTIFY_PROFILE_IMAGE = 411;
	final static int COMPLETE_UTIL_MY_RECORD_PROFILE_IMAGE = 412;
	final static int COMPLETE_UTIL_MAIN_QUICK_IMAGE_01_ON = 413;
	final static int COMPLETE_UTIL_MAIN_QUICK_IMAGE_01_OFF = 414;
	final static int COMPLETE_UTIL_MAIN_QUICK_IMAGE_02_ON = 415;
	final static int COMPLETE_UTIL_MAIN_QUICK_IMAGE_02_OFF = 416;
	final static int COMPLETE_UTIL_EVENT_DETAIL_ON = 417;
	final static int COMPLETE_UTIL_EVENT_DETAIL_OFF = 418;
	final static int COMPLETE_UTIL_SHOP_ITEM_01 = 419;
	final static int COMPLETE_UTIL_SHOP_ITEM_02 = 420;
	final static int COMPLETE_UTIL_KY_LOGO = 421;
	final static int COMPLETE_UTIL_MIC = 422;

	/*
	 * VASS 요청 형식
	 */
	final static int REQUEST_VASS_NONE = -1;
	//븅신지랄
	//final static int REQUEST_VASS_DAY_PURCHASE = 601;
	//븅신지랄
	//final static int REQUEST_VASS_MONTH_PURCHASE = 602;
	//븅신지랄
	//final static int REQUEST_VASS_YEAR_PURCHASE = 603;
	//븅신지랄
	//final static int REQUEST_VASS_DAY_CHECK = 604;
	//븅신지랄
	//final static int REQUEST_VASS_MONTH_CHECK = 605;
	//븅신지랄
	//final static int REQUEST_VASS_YEAR_CHECK = 606;
	//븅신지랄
	//final static int REQUEST_VASS_DAY_CHECK_PLAY = 607;
	//븅신지랄
	//final static int REQUEST_VASS_MONTH_CHECK_PLAY = 608;
	//븅신지랄
	//final static int REQUEST_VASS_YEAR_CHECK_PLAY = 609;
	//븅신지랄
	//final static int REQUEST_VASS_PASSWORD_FOR_DAY = 610;
	//븅신지랄
	//final static int REQUEST_VASS_PASSWORD_FOR_MONTH = 611;
	//븅신지랄
	//final static int REQUEST_VASS_PASSWORD_FOR_YEAR = 612;
	//븅신지랄
	//final static int REQUEST_VASS_PPM_PURCHASE = 613;
	//븅신지랄
	//final static int REQUEST_VASS_PPV_PURCHASE = 614;
	final static int REQUEST_VASS_PPX_CHECK = 615;
	final static int REQUEST_VASS_PPX_CHECK_PLAY = 616;
	final static int REQUEST_VASS_PPX_PASSWORD = 617;
	final static int REQUEST_VASS_PPX_PURCHASE = 618;

	/*
	 * VASS 완료 형식
	 */
	final static int COMPLETE_VASS_NONE = -1;
	//븅신지랄
	//final static int COMPLETE_VASS_DAY_PURCHASE = 701;
	//븅신지랄
	//final static int COMPLETE_VASS_DAY_CHECK = 702;
	//븅신지랄
	//final static int COMPLETE_VASS_MONTH_PURCHASE = 703;
	//븅신지랄
	//final static int COMPLETE_VASS_MONTH_CHECK = 704;
	//븅신지랄
	//final static int COMPLETE_VASS_YEAR_PURCHASE = 705;
	//븅신지랄
	//final static int COMPLETE_VASS_YEAR_CHECK = 706;
	//븅신지랄
	//final static int COMPLETE_VASS_PASSWORD_FOR_DAY = 707;
	//븅신지랄
	//final static int COMPLETE_VASS_PASSWORD_FOR_MONTH = 708;
	//븅신지랄
	//final static int COMPLETE_VASS_PASSWORD_FOR_YEAR = 709;
	//븅신지랄
	//final static int COMPLETE_VASS_DAY_CHECK_PLAY = 710;
	//븅신지랄
	//final static int COMPLETE_VASS_MONTH_CHECK_PLAY = 711;
	//븅신지랄
	//final static int COMPLETE_VASS_YEAR_CHECK_PLAY = 712;
	//븅신지랄
	//final static int COMPLETE_VASS_PPM_PURCHASE = 713;
	//븅신지랄
	//final static int COMPLETE_VASS_PPV_PURCHASE = 714;
	final static int COMPLETE_VASS_PPX_CHECK = 715;
	final static int COMPLETE_VASS_PPX_CHECK_PLAY = 716;
	final static int COMPLETE_VASS_PPX_PASSWORD = 717;
	final static int COMPLETE_VASS_PPX_PURCHASE = 718;

	/*
	 * 타이머
	 */
	final static int COMPLETE_TIMER_HIDE_MESSAGE_COMMON = 501;
	final static int COMPLETE_TIMER_HIDE_SCORE = 502;
	final static int COMPLETE_TIMER_SHOW_MESSAGE_NOT_RESPONSE = 503;
	final static int COMPLETE_TIMER_START_SING_NOW = 504;
	final static int COMPLETE_TIMER_START_SING_NEXT = 505;

	/*
	 * KP
	 */
	/**
	 * 메인
	 */
	final static String KP_0000 = "KP_0000";
	/**
	 * 서브 메뉴
	 */
	final static String KP_1500 = "KP_1500";
	/**
	 * 반주곡 목록
	 */
	final static String KP_1000 = "KP_1000";
	/**
	 * 애창곡 추가or삭제
	 */
	final static String KP_3010 = "KP_3010";
	/**
	 * 반주곡 재생
	 */
	final static String KP_1016 = "KP_1016";
	/**
	 * 녹음곡 목록
	 */
	final static String KP_2100 = "KP_2100";
	/**
	 * 녹음곡 목록 : 이 녹음곡의 다른 사람 곡
	 */
	final static String KP_2001 = "KP_2001";
	/**
	 * 녹음곡 재생
	 */
	final static String KP_2016 = "KP_2016";
	/**
	 * 반주곡or녹음곡 재생 시간 로그
	 */
	final static String KP_1012 = "KP_1012";
	/**
	 * 마이노래방 목록
	 */
	final static String KP_3000 = "KP_3000";
	/**
	 * (마이)녹음곡 목록
	 */
	final static String KP_3001 = "KP_3001";
	/**
	 * 공지사항or이용안내 목록
	 */
	final static String KP_0010 = "KP_0010";
	/**
	 * 공지사항or이용안내 상세
	 */
	final static String KP_0011 = "KP_0011";
	/**
	 * 이벤트 응모
	 */
	final static String KP_0012 = "KP_0012";
	/**
	 * 이벤트 응모 휴대폰 번호 전송
	 */
	final static String KP_0013 = "KP_0013";
	/**
	 * 검색 목록
	 */
	final static String KP_0020 = "KP_0020";
	/**
	 * 인증 상태
	 */
	final static String KP_9000 = "KP_9000";
	/**
	 * 휴대폰 인증 번호 전송
	 */
	final static String KP_9001 = "KP_9001";
	///**
	// *
	// */
	//final static String KP_4000 = "KP_4000";        // 판매 이용권 정보
	///**
	// *
	// */
	//final static String KP_4001 = "KP_4001";        // 판매 이용권 정보
	/**
	 * 판매 이용권 정보(KP_4002)
	 */
	final static String KP_4002 = "KP_4002";
	/**
	 * 판매 이용권 확인(KP_4003)
	 */
	final static String KP_4003 = "KP_4003";
	/**
	 *
	 */
	final static String KP_0014 = "KP_0014";        // 쿠폰등록

	/*
	 * M1
	 */
	final static String M1_MAIN = "MAIN";
	final static String M1_MENU_SING = "MENU_SING";
	final static String M1_SING_GENRE = "SING_GENRE";
	final static String M1_MENU_LISTEN = "MENU_LISTEN";
	final static String M1_MENU_MYLIST = "MENU_MYLIST";
	final static String M1_MENU_HELP = "MENU_HELP";
	final static String M1_MENU_SEARCH = "MENU_SEARCH";

	/*
	 * M2
	 */
	final static String M2_MENU = "MENU";
	final static String M2_SING_HOT = "SING_HOT";
	final static String M2_SING_RECENT = "SING_RECENT";
	final static String M2_GENRE_1 = "GENRE_1";
	final static String M2_GENRE_2 = "GENRE_2";
	final static String M2_GENRE_3 = "GENRE_3";
	final static String M2_GENRE_4 = "GENRE_4";
	final static String M2_GENRE_5 = "GENRE_5";
	final static String M2_GENRE_6 = "GENRE_6";
	final static String M2_MYLIST_RECENT = "MYLIST_RECENT";
	final static String M2_MYLIST_FAVORITE = "MYLIST_FAVORITE";
	final static String M2_MYLIST_RECORD = "MYLIST_RECORD";
	final static String M2_HELP_NOTICE = "HELP_NOTICE";
	final static String M2_HELP_USEINFO = "HELP_USEINFO";
	final static String M2_SEARCH_1 = "SEARCH_1";
	final static String M2_SEARCH_2 = "SEARCH_2";
	final static String M2_SEARCH_3 = "SEARCH_3";
	final static String M2_SEARCH_4 = "SEARCH_4";
	final static String M2_LISTEN_TIMELINE = "LISTEN_TIMELINE";
	final static String M2_LISTEN_WEEK = "LISTEN_WEEK";
	final static String M2_LISTEN_TOP100 = "LISTEN_TOP100";
	final static String M2_SHOP_AUTH = "SHOP_AUTH";
	final static String M2_MENU_SHOP = "MENU_SHOP";
	final static String M2_SHOP_TICKET = "SHOP_TICKET";
	final static String M2_HELP_MIKE = "HELP_MIKE";
	final static String M2_HELP_EVENT = "HELP_EVENT";

	final String[][] SEARCH_LETTER_KOR = {{"", ""}, {"", "ㄱ", "ㄲ"}, {"", "ㄴ", "ㄸ"}, {"", "ㄷ", "ㅃ"}, {"", "ㄹ", "ㅆ"}, {"", "ㅁ", "ㅉ"}, {"", "ㅂ"}, {"", "ㅅ"}, {"", "ㅇ"}, {"", "ㅈ"}, {"", "ㅊ"}, {"", "ㅋ"}, {"", "ㅌ"}, {"", "ㅍ"}, {"", "ㅎ"}};

	final String[][] SEARCH_LETTER_ENG = {{"", ""}, {"", "A", "O"}, {"", "B", "P"}, {"", "C", "Q"}, {"", "D", "R"}, {"", "E", "S"}, {"", "F", "T"}, {"", "G", "U"}, {"", "H", "V"}, {"", "I", "W"}, {"", "J", "X"}, {"", "K", "Y"}, {"", "L", "Z"}, {"", "M"}, {"", "N"}};

	final String[][] SEARCH_LETTER_NUM = {{"", ""}, {"", "1"}, {"", "2"}, {"", "3"}, {"", "4"}, {"", "5"}, {"", "6"}, {"", "7"}, {"", "8"}, {"", "9"}, {"", "0"},};

	// =====================================================================

	final static String AID = "OA00023245";
	final static String MYPREFS = "mySharedPref";
	final static String ISDB = "DataBase";

	final static Typeface TYPEFACE_KARAOKE = Typeface.createFromFile("/system/fonts/DroidSansFallback.ttf");

	final static int ACTIVITY_MAIN = 0;
	final static int ACTIVITY_MYSONG = 1;
	final static int ACTIVITY_HIT = 2;
	final static int ACTIVITY_NEW = 3;
	final static int ACTIVITY_SEARCH = 4;
	final static int ACTIVITY_FAVORITE = 5;
	final static int ACTIVITY_UPDATE = 6;
	final static int ACTIVITY_HELP = 7;
	final static int ACTIVITY_MENU = 8;
	final static int ACTIVITY_PLAYER = 9;

	final static int ACTIVITY_SUBJECT = 10;
	final static int ACTIVITY_SUBJECT_SUB = 11;
	final static int ACTIVITY_ARTIST = 12;
	final static int ACTIVITY_ARTIST_SUB = 13;
	final static int ACTIVITY_ARTIST_DETAIL = 14;
	final static int ACTIVITY_LYRICS = 15;
	final static int ACTIVITY_LYRICS_SUB = 16;
	final static int ACTIVITY_DETAIL = 17;
	final static int ACTIVITY_DETAIL_SUB = 18;

	final static String KEY_ACTIVITY = "ACTIVITY";
	final static String KEY_SEARCH_KEY = "SEARCH_KEY";
	final static String KEY_SEARCH_VALUE = "SEARCH_VALUE";
	final static String KEY_SEARCH_WHERE = "SEARCH_WHERE";
	final static String KEY_SUBJECT_ID = "SUBJECT_ID";

	final static int DIALOG_ALERT_AIDL = 0;
	final static int DIALOG_ALERT_NOSONG = 1;
	final static int DIALOG_ALERT_EXISTS = 2;
	final static int DIALOG_ALERT_YESNO = 3;
	final static int DIALOG_ALERT_REG_YESNO = 4;
	final static int DIALOG_ALERT_DEL_YESNO = 5;
	final static int DIALOG_ALERT_SONGDOWN_COMPLETE = 9;
	final static int DIALOG_ALERT_SONGDOWN_CANCEL = 10;
	final static int DIALOG_ALERT_SONGDOWN_ERROR = 11;
	final static int DIALOG_ALERT_NOSDCARD = 12;
	final static int DIALOG_PROGRESS_SONG_DATALOAD = 6;
	final static int DIALOG_PROGRESS_SONG_DOWNLOAD = 7;

	final static String SONGDOWN_URL = "http://cyms.chorus.co.kr/cykara_dl.asp?song_id=";

	final static String INTENT_FILLTER_SONGDB_UPDATE = "AONMEDIA_KARAOKE_SONGDB_UPDATE";

	final static int STATE_SONGDB_UPDATE_RUNNING = 5;
	final static int STATE_SONGDB_UPDATE_COMPLETE = 6;
	final static int STATE_SONGDB_UPDATE_DBEXISTS = 7;
	final static int STATE_SONGDB_UPDATE_CANCEL = 8;
	final static int STATE_SONGDB_UPDATE_ERROR = 9;
	final static int STATE_SONG_DOWNLOAD_START = 0;
	final static int STATE_SONG_DOWNLOAD_RUNNING = 1;
	final static int STATE_SONG_DOWNLOAD_COMPLETE = 2;
	final static int STATE_SONG_DOWNLOAD_CANCEL = 3;
	final static int STATE_SONG_DOWNLOAD_ERROR = 4;

	final static int DATABASE_VERSION = 1;

	final static String AONMEDIA_FOLDER = "aonmedia";
	final static String KARAOKE_FOLDER = "karaoke";
	final static String SONGDOWN_FOLDER = "skym";
	final static String DATABASE_FOLDER = "data";
	final static String BACKIMAGE_FOLDER = "image";
	final static String SONGRECORD_FOLDER = "record";

	final static String DATABASE_SONG = "song.db";
	final static String DATABASE_USER = "user.db";
	final static String TABLE_ARTIST = "tbl_artist";
	final static String TABLE_HIT = "tbl_hit";
	final static String TABLE_INFO = "tbl_info";
	final static String TABLE_LYRICS = "tbl_lyrics";
	final static String TABLE_NEW = "tbl_new";
	final static String TABLE_SONG = "tbl_song";
	final static String TABLE_TYPE = "tbl_type";

	final static String ARTIST_COL_DATA_ID = "data_id";
	final static String ARTIST_COL_ARTIST_TYPE = "artist_type";
	final static String ARTIST_COL_ARTIST = "artist";
	final static String ARTIST_COL_SONG_CNT = "song_cnt";

	final static String HIT_COL_DATA_ID = "data_id";
	final static String HIT_COL_TJ_ID = "tj_id";
	final static String HIT_COL_SONG = "song";
	final static String HIT_COL_ARTIST = "artist";
	final static String HIT_COL_RANK = "rank";

	final static String INFO_COL_IDX = "idx";
	final static String INFO_COL_DB_NAME = "db_name";
	final static String INFO_COL_DB_VER = "db_ver";
	final static String INFO_COL_SERVICE_INFO = "service_info";
	final static String INFO_COL_MAKE_DATE = "make_date";
	final static String INFO_COL_ETC = "etc";

	final static String LYRICS_COL_DATA_ID = "data_id";
	final static String LYRICS_COL_LYRICS = "lyrics";

	final static String NEW_COL_DATA_ID = "data_id";
	final static String NEW_COL_TJ_ID = "tj_id";
	final static String NEW_COL_SONG = "song";
	final static String NEW_COL_ARTIST = "artist";
	final static String NEW_COL_RANK = "rank";

	final static String SONG_COL_DATA_ID = "data_id";
	final static String SONG_COL_TJ_ID = "tj_id";
	final static String SONG_COL_ARTIST_ID = "artist_id";
	final static String SONG_COL_SONG_TYPE = "song_type";
	final static String SONG_COL_SONG = "song";
	final static String SONG_COL_ARTIST = "artist";
	final static String SONG_COL_SONG_SRCH = "song_srch";
	final static String SONG_COL_ARTIST_SRCH = "artist_srch";

	final static String TYPE_COL_DATA_ID = "data_id";
	final static String TYPE_COL_NAME = "name";

	final static String[] HANGUL = {"ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};
	final static String[] ENGLISH = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	final static String[] HANGUL_TYPE = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014"};
	final static String[] ENGLISH_TYPE = {"015", "016", "017", "018", "019", "020", "021", "022", "023", "024", "025", "026", "027", "028", "029", "030", "031", "032", "033", "034", "035", "036", "037", "038", "039", "040"};

	final static int color_list_order = 0xFFFFFFFF;
	final static int color_list_title = 0xFFFFFFFF;
	final static int color_list_number = 0xFFFF4444;
	final static int color_list_artist = 0xFFC0C0C0;
	final static int color_list_len = 0xFFC0C0C0;

	final static int list_text_highlight = 0xFF000000;
	final static int list_normal = 0xA0404040;
	final static int list_normal_alternate = 0xA0505050;
	final static int list_highlight = 0xFFFFFFFF;
	final static int list_highlight_alternate = 0xA0FFFFFF;

	final static int[] list_normal_colors = new int[]{list_normal, list_normal_alternate};
	final static int[] list_highlight_colors = new int[]{list_highlight, list_highlight_alternate};

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

	final static int LINE_VIEW = 2;

	final static int TIMER_ROTATE = 2000;
	final static int TIMER_TICK = 1000;
	final static int TIMER_FLIPER = 3000;
	final static int TIMER_PREVIEW = 60000;

	final static String ROOT = "Karaoke";
	final static String SONGPLAYER_SKYM = "SongPlayerSkym";
	final static String SONGPLAYER_IMAGE = "SongPlayerImage";
	final static String SONGPLAYER_RECORD_COMP = "SongPlayerRecComp";
}
