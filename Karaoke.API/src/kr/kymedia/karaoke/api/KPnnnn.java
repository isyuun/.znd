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
 * project	:	Karaoke
 * filename	:	KPnnnn.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.data
 *    |_ KPnnnn.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import is.yuun.com.loopj.android.http.api.ProgressListener;
import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.data.SongUtil;
import kr.kymedia.karaoke.util.Base64;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * <pre>
 * static 선언시 반드시 핸들러/리스너 연결에 주의할것
 * </pre>
 * 
 * @author isyoon
 * @since 2012. 2. 29.
 * @version 1.0
 * 
 * @see #setOnKPnnnnListner(KPnnnnListener)
 * @see #setHandler(Handler)
 */
public class KPnnnn extends KPjson {

	/**
	 * 프로토콜버전넘버
	 */
	private final String p_ver = _IKaraoke.P_VER;

	private String p_apikey;

	/**
	 */
	private KPparam KPparam;
	// public KPinfo getKPInfo() {
	// return KP_info;
	// }
	// public void setKPInfo(KPinfo KP_info) {
	// this.KP_info = KP_info;
	// }

	/**
	 * <pre>
	 * KPOP
	 * 	상용 : http://kpop.kymedia.kr/ucc/index.php
	 * 	개발 : http://dev.mobileon.co.kr/kpop/ucc/index.php
	 * JPOP
	 * 	상용 : http://kpop.kymedia.kr/jap/index.php
	 * 	개발 : http://dev.mobileon.co.kr/kpop/jap/index.php
	 * </pre>
	 */
	private String p_hostpath = "";

	public String getHostPath() {
		return p_hostpath;
	}

	private String p_indexphp = "";

	private String mStrQuery = "";

	public String getQuery() {
		return mStrQuery;
	}

	/**
	 */
	public String getParams() {
		String param = mStrQuery;
		param = param.replace(p_hostpath + p_indexphp, "");
		return param;
	}

	/**
	 * <br>
	 * User id 로그인한 user id
	 */
	private String p_mid = "";
	/**
	 * <br>
	 * 이용권유형 무료사용자 :N 기간제이용권:Y 이벤트사용자:E/C
	 */
	private String p_passtype = "";
	/**
	 * <br>
	 * 잔여갯수 곡단위이용권 사용자일경우 잔여갯수
	 */
	private String p_passcnt = "";
	/**
	 * <br>
	 * 1차메뉴코드 KPOPApp가 요청하는 1차메뉴 코드 (3.4 메뉴코드 정의 참고)
	 */
	private String p_m1 = "";
	/**
	 * <br>
	 * 2차메뉴코드 KPOPApp가 요청하는 2차메뉴 코드 (3.4 메뉴코드 정의 참고)
	 */
	private String p_m2 = "";

	// /**
	// * 오퍼레이션코드 인테페이스명세서를 구성하고 있는 인터페이스 ID
	// */
	// String p_opcode = "KP_0000";

	/**
	 * 1. 기본URI주소생성<br>
	 * 2. 메뉴코드전달<br>
	 * 3. API키 설정<br>
	 */
	private String KP_xxxx(String mid, String m1, String m2, String opcode) {
		isQuerying = false;

		p_apikey = _IKaraoke.APP_API_KEY_KPOP;
		if (_IKaraoke.APP_API_AD_TEST) {
			p_apikey = _IKaraoke.APP_API_KEY_AD_TEST;
		}

		if (_IKaraoke.APP_PACKAGE_NAME_JPOP.equalsIgnoreCase(context.getPackageName())) {
			// KPOP HOLIC for JAPAN
			p_hostpath = "http://kpop.kymedia.kr/jap/";
			//if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) {
			//	p_hostpath = "http://dev.mobileon.co.kr/kpop/jap/";
			//}
			// if (_IKaraoke.APP_API_TEST || _IKaraoke.APP_API_AD_TEST) {
			// p_hostpath = "http://dev.mobileon.co.kr/kpop/jap/";
			// }
			// test
			// p_hostpath = "http://dev.mobileon.co.kr/kpop/jap/";
			// release
			// p_hostpath = "http://kpop.kymedia.kr/jap/";
		} else {
			// 그외
			p_hostpath = "http://kpop.kymedia.kr/ucc/";
			//if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) {
			//	p_hostpath = "http://dev.mobileon.co.kr/kpop/ucc/";
			//}
			// if (_IKaraoke.APP_API_TEST || _IKaraoke.APP_API_AD_TEST) {
			// p_hostpath = "http://dev.mobileon.co.kr/kpop/ucc/";
			// }
			// test
			// p_hostpath = "http://dev.mobileon.co.kr/kpop/ucc/";
			// release
			// p_hostpath = "http://kpop.kymedia.kr/ucc/";
		}

		p_indexphp = "index.php?";

		p_mid = mid;
		p_m1 = m1;
		p_m2 = m2;
		p_opcode = opcode;
		__CLASSNAME__ = toString();

		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "[INIT:" + p_opcode + "]" + getMethodName());
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "[INIT:" + p_opcode + "]" + this.getClass().getSimpleName());
		if (_IKaraoke.DEBUG) Log2.i(__CLASSNAME__, "[OPCODE]" + p_opcode);
		if (_IKaraoke.DEBUG) Log2.i(__CLASSNAME__, "[M1]" + p_m1);
		if (_IKaraoke.DEBUG) Log2.i(__CLASSNAME__, "[M2]" + p_m2);

		String param = "";

		if (_IKaraoke.DEBUG) {
			param += "&p_debug=debug";
		}
		param += "&market=G";
		param += "&p_ver=" + p_ver;
		param += "&p_mid=" + p_mid;
		param += KPparam.getParam();
		param += "&p_passtype=" + p_passtype;
		param += "&p_passcnt=" + p_passcnt;
		param += "&p_opcode=" + p_opcode;
		param += "&p_m1=" + p_m1;
		param += "&p_m2=" + p_m2;

		mStrQuery = p_hostpath + p_indexphp + param;
		mStrQuery += "&p_apikey=" + p_apikey;

		return mStrQuery;
	}

	/**
	 * 
	 * TODO<br>
	 * NOTE:<br>
	 * 
	 * @author isyoon
	 * @since 2013. 10. 23.
	 * @version 1.0
	 * @see KPxxxxListener
	 */
	public interface KPnnnnListener {

		/**
		 * 조회전체
		 *
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		public void onKPnnnnResult(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 *
		 * 조회시작
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		public void onKPnnnnStart(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회시작
		 */
		public interface onKPnnnnStart {
			public void onStart(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 조회성공
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		public void onKPnnnnSuccess(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회성공
		 *
		 */
		public interface onKPnnnnSuccess {
			public void onSuccess(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 조회실패
		 *
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		public void onKPnnnnFail(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회실패
		 *
		 */
		public interface onKPnnnnFail {
			public void onFail(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 조회오류
		 *
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		public void onKPnnnnError(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회오류
		 */
		public interface onKPnnnnError {
			public void onError(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 조회취소 - 보류
		 *
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		@Deprecated
		public void onKPnnnnCancel(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회취소 - 보류
		 */
		@Deprecated
		public interface onKPnnnnCancel {
			@Deprecated
			public void onCancel(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 조회종료 - 보류
		 *
		 * @param what
		 * @param r_opcode
		 * @param r_code
		 * @param r_message
		 * @param r_info
		 */
		@Deprecated
		public void onKPnnnnFinish(int what, String r_opcode, String r_code, String r_message, KPItem r_info);

		/**
		 * 조회종료 - 보류
		 */
		@Deprecated
		public interface onKPnnnnFinish {
			@Deprecated
			public void onFinish(int what, String r_opcode, String r_code, String r_message, KPItem r_info);
		}

		/**
		 * 전문진행확인
		 * 
		 * @param size
		 * @param total
		 */
		public void onKPnnnnProgress(long size, long total);

		/**
		 * 전문진행확인
		 */
		public interface onKPnnnnProgress {
			public void onProgress(long size, long total);
		}

	}

	/**
	 * 전문리스너
	 * 
	 * @see #sendMessage(int, String, String)
	 * @see #mHandler
	 * @see #mListener
	 */
	private KPnnnnListener mListener = null;

	/**
	 * 전문리스너
	 */
	public void setOnKPnnnnListner(KPnnnnListener listener) {
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, getMethodName() + listener);
		this.mListener = listener;
	}

	@Deprecated
	private KPnnnnListener.onKPnnnnCancel mOnCancelListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnCancelListener to set
	 */
	public void setOnCancelListener(KPnnnnListener.onKPnnnnCancel listener) {
		this.mOnCancelListener = listener;
	}

	private KPnnnnListener.onKPnnnnError mOnErrorListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnErrorListener to set
	 */
	public void setOnErrorListener(KPnnnnListener.onKPnnnnError listener) {
		this.mOnErrorListener = listener;
	}

	private KPnnnnListener.onKPnnnnFail mOnFailListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnFailListener to set
	 */
	public void setOnFailListener(KPnnnnListener.onKPnnnnFail listener) {
		this.mOnFailListener = listener;
	}

	@Deprecated
	private KPnnnnListener.onKPnnnnFinish mOnFinishListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnFinishListener to set
	 */
	public void setOnFinishListener(KPnnnnListener.onKPnnnnFinish listener) {
		this.mOnFinishListener = listener;
	}

	private KPnnnnListener.onKPnnnnProgress mOnProgressListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnProgressListener to set
	 */
	public void setOnProgressListener(KPnnnnListener.onKPnnnnProgress listener) {
		this.mOnProgressListener = listener;
	}

	private KPnnnnListener.onKPnnnnStart mOnStartListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnStartListener to set
	 */
	public void setOnStartListener(KPnnnnListener.onKPnnnnStart listener) {
		this.mOnStartListener = listener;
	}

	private KPnnnnListener.onKPnnnnSuccess mOnSuccessListener = null;

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * @param listener
	 *          the mOnSuccessListener to set
	 */
	public void setOnSuccessListener(KPnnnnListener.onKPnnnnSuccess listener) {
		this.mOnSuccessListener = listener;
	}

	public void setHandler(Handler handler) {
		if (handler != null) {
			this.mHandler = handler;
		}
	}

	// String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();
	// String __CLASSNAME__ = "KPnnnn";
	@Override
	public String toString() {

		// return super.toString();
		// return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
		// return getClass().getSimpleName() + ':' + p_opcode + '@' + Integer.toHexString(hashCode());
		return p_opcode + '@' + Integer.toHexString(hashCode());
	}

	/**
	 */
	@Override
	public void clear() {

		super.clear();
	}

	public KPnnnn() {
		super();
		__CLASSNAME__ = this.getClass().getSimpleName();

	}

	@Override
	public void release() {
		try {
			super.release();

			clear();

			mHandler = null;

			mListener = null;
			mOnCancelListener = null;
			mOnErrorListener = null;
			mOnFailListener = null;
			mOnFinishListener = null;
			mOnProgressListener = null;
			mOnStartListener = null;
			mOnSuccessListener = null;

			// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)Log.e("MEMINFO-HEAPINFO", this + "::" + getMethodName() + "[BF]");
			// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)EnvironmentUtils.getMemoryInfo(__CLASSNAME__ + ":" + getMethodName(), context);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();

		release();

		// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)Log.d("MEMINFO-HEAPINFO", this + "::" + getMethodName() + "[BF]");
		// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)EnvironmentUtils.getMemoryInfo(__CLASSNAME__ + ":" + getMethodName(), context);
		// System.gc();
		// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)Log.d("MEMINFO-HEAPINFO", this + "::" + getMethodName() + "[AF]");
		// if (_IKaraoke.APP_API_TEST || _IKaraoke.DEBUG)EnvironmentUtils.getMemoryInfo(__CLASSNAME__ + ":" + getMethodName(), context);
	}

	/**
	 * 생성자:컨텍스트생성후사용한다.
	 */
	public KPnnnn(Context context, Handler handler, KPparam KP_info, String mid, String passtype, String passcnt) {
		super(handler);

		this.mHandler = handler;

		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "mid:" + mid + ", passtype:" + passtype + ", passcnt:" + passcnt);
		isQuerying = false;

		this.KPparam = KP_info;
		p_mid = mid;
		p_passtype = passtype;
		p_passcnt = passcnt;

		KP_init(context);

		// setProgressListener(new ProgressListener() {
		//
		// @Override
		// public void onProgress(long size, long total) {
		// try {
		// //long diff = 0;
		// //
		// //long len = 0;
		// //if (file != null) {
		// // len = file.length();
		// //}
		// //
		// //String message = String.format("(%s/%s bytes)",
		// // TextUtil.getDecimalFormat("#,##0", size), TextUtil.getDecimalFormat("#,##0", total));
		// //
		// //
		// ////업로드인경우
		// //if (mRequestParams.getMultipartEntity() != null) {
		// // //diff = total - mRequestParams.getMultipartEntity().getContentLength();
		// // diff = total - len;
		// // message = "Uploading..." + message;
		// //} else {
		// // message = "Downloading..." + message;
		// //}
		// //
		// //if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, message + " - HEADER:" + diff + " - " + size + " / " + total + " - " + (size) + " / " + (total-diff));
		//
		// if (mListener != null) {
		// mListener.onKPnnnnProgress(size, total);
		// }
		// if (mOnProgressListener != null) {
		// mOnProgressListener.onProgress(size, total);
		// }
		//
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		// }
		// });
		setProgressListener(new ProgressListener() {

			@Override
			public void onProgress(long size, long total) {

				try {
					if (mListener != null) {
						mListener.onKPnnnnProgress(size, total);
					}
					if (mOnProgressListener != null) {
						mOnProgressListener.onProgress(size, total);
					}
					String message = String.format("(%s/%s bytes)", TextUtil.getDecimalFormat("#,##0", size), TextUtil.getDecimalFormat("#,##0", total));

					// long diff = 0;
					// long len = 0;
					// File file = mRequestParams.getFile();
					// if (file != null) {
					// len = file.length();
					// }
					//
					// if (mRequestParams.getMultipartEntity() != null) {
					// //diff = total - mRequestParams.getMultipartEntity().getContentLength();
					// diff = total - len;
					// message = "Uploading..." + message;
					// } else {
					// message = "Downloading..." + message;
					// }
					//
					// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, message + " - HEADER:" + diff + " - " + size + " / " + total + " - " + (size) + " / " + (total-diff));

					if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "onProgress" + message);
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 재초기화
	 */
	public void KP_nnnn(KPparam KPparam, String mid, String passtype, String passcnt, String m1, String m2) {
		this.KPparam = KPparam;
		this.p_mid = mid;
		this.p_passtype = passtype;
		this.p_passcnt = passcnt;

		String query = KP_xxxx(mid, m1, m2, p_opcode);
		mStrQuery = query;
	}

	Context context = null;

	/**
	 * 0. API버전 정보설정<br>
	 * 1. API서버 주소설정<br>
	 * 2. API사용 어플설정<br>
	 */
	private void KP_init(Context context) {
		if (_IKaraoke.DEBUG) Log2.i(__CLASSNAME__, getMethodName());

		this.context = context;

		KP_xxxx(p_mid, p_m1, p_m2, p_opcode);
	}

	/**
	 * <pre>
	 * 전문핸들러
	 * </pre>
	 * 
	 * @see #sendMessage(int, String, String)
	 * @see #mHandler
	 * @see #mListener
	 */
	private Handler mHandler = null;

	public void post(Runnable r) {
		if (mHandler != null) {
			mHandler.post(r);
		}

	}

	public void postDelayed(Runnable r, long delayMillis) {
		if (mHandler != null) {
			mHandler.postDelayed(r, delayMillis);
		}

	}

	/**
	 * 조회중확인 start->finish까지
	 */
	private boolean isQuerying = false;

	public boolean isQuerying() {
		return isQuerying;
	}

	/**
	 * 
	 * <pre>
	 * 전문리스너가 설정된경우 리스너 콜백만 호출 (전문핸들러중단)
	 * </pre>
	 * 
	 * @see #sendMessage(int, String, String)
	 * @see #mHandler
	 * @see #mListener
	 */
	@Override
	protected void sendMessage(final int what, final String r_code, final String r_message) {
		if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.e(toString(), "[RECV:" + p_opcode + "]" + getMethodName() + what + ", " + r_code + ", " + r_message);
		if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(toString(), "[RECV:" + p_opcode + "]" + mListener);


		super.sendMessage(what, r_code, r_message);

		// if (getLists() != null) {
		// Log.i(__CLASSNAME__, getMethodName() + what + ", " + p_opcode + ", size:" + getLists().size());
		// }

		post(new Runnable() {

			@Override
			public void run() {

				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + what);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnStartListener);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnFailListener);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnErrorListener);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnSuccessListener);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnCancelListener);
				// if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnFinishListener);

				boolean sent = false;

				switch (what) {
				case STATE_DATA_QUERY_START:
					isQuerying = true;
					if (mListener != null) {
						mListener.onKPnnnnStart(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnStartListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnStartListener);
						mOnStartListener.onStart(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				case STATE_DATA_QUERY_FAIL:
					isQuerying = false;
					if (mListener != null) {
						mListener.onKPnnnnFail(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnFailListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnFailListener);
						mOnFailListener.onFail(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				case STATE_DATA_QUERY_ERROR:
					isQuerying = false;
					if (mListener != null) {
						mListener.onKPnnnnError(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnErrorListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnErrorListener);
						mOnErrorListener.onError(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				case STATE_DATA_QUERY_SUCCESS:
					isQuerying = false;
					if (mListener != null) {
						mListener.onKPnnnnSuccess(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnSuccessListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnSuccessListener);
						mOnSuccessListener.onSuccess(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				case STATE_DATA_QUERY_CANCEL:
					isQuerying = false;
					if (mListener != null) {
						mListener.onKPnnnnCancel(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnCancelListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnCancelListener);
						mOnCancelListener.onCancel(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				case STATE_DATA_QUERY_FINISH:
					isQuerying = false;
					if (mListener != null) {
						mListener.onKPnnnnFinish(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					if (mOnFinishListener != null) {
						if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.i(KPnnnn.this.toString(), "[RECV:" + p_opcode + "]" + mOnFinishListener);
						mOnFinishListener.onFinish(what, p_opcode, r_code, r_message, getInfo());
						sent = true;
					}
					break;

				default:
					isQuerying = false;
					break;
				}

				try {
					if (mListener != null) {
						if (_IKaraoke.DEBUG) Log2.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "][mListener]" + "[M1]\t" + p_m1 + "[M2]\t" + p_m2 + "\t" + r_code);
						// 리스너를사용한경우(리스너에게전달)
						mListener.onKPnnnnResult(what, p_opcode, r_code, r_message, getInfo());
					} else {
						// 리스너를안쓰는경우(핸들러에게전달)
						if (!sent && mHandler != null) {
							if (_IKaraoke.DEBUG) Log2.w(KPnnnn.this.toString(), "[RECV:" + p_opcode + "][mHandler]" + "[M1]\t" + p_m1 + "[M2]\t" + p_m2 + "\t" + r_code);
							Message msg = mHandler.obtainMessage();
							msg.what = what;
							msg.getData().putString("opcode", p_opcode);
							msg.getData().putString("result_code", r_code);
							msg.getData().putString("result_message", r_message);
							if (getInfo() != null) {
								msg.getData().putString("result_info", getInfo().toString());
							}
							if (isCanceled) {
								msg.what = STATE_DATA_QUERY_CANCEL;
							}
							mHandler.sendMessage(msg);
						}
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * 기본조회처리 <br>
	 */
	private void send(String path) {
		if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[SEND:" + p_opcode + "]" + "[MID]\t" + p_mid + "[M1]\t" + p_m1 + "[M2]\t" + p_m2);
		if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[SEND:" + p_opcode + "]" + "[PATH]\t" + path);

		if (isQuerying) {
			if (_IKaraoke.DEBUG || _IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[SEND:" + p_opcode + "]:RETURN" + isQuerying);
			return;
		}

		isQuerying = true;

		if (!TextUtil.isEmpty(path)) {
			if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, path);
			File file = new File(path);
			if (file == null || !file.exists()) {
				Throwable e = new IOException("File not exist error");
				onFailure(e);
				isQuerying = false;
				return;
			}
		}

		try {
			put(mStrQuery, p_opcode, path);
		} catch (Exception e) {

			e.printStackTrace();
			onFailure(e);
			isQuerying = false;
		}
	}

	/**
	 * KP_nnnn 전문호출기본함수.<br>
	 * <ul>
	 * <nl>
	 * 0. 제외전문<br>
	 * KP_1011 (녹음곡 업로드 요청) KP_1016 (녹음곡 다운로드 요청)
	 * <nl>
	 * 1. 전문호출함수를 호출하지않고 opcode이하 param구성이 필요한 경우<br>
	 * <nl>
	 * 2. 전문호출함수가 웹API와 달리 누락되는 경우<br>
	 * </ul>
	 * <br>
	 * <ul>
	 * <nl>
	 * 예) "KP_0001"대체하는경우<br>
	 * //KP_nnnn.KP_0001(p_mid, "MAIN", "MENU", p_email);<br>
	 * Map&lt;String, String&gt; params = new HashMap&lt;String, String&gt;();<br>
	 * params.put(LOGIN.KEY_EMAIL, p_email);<br>
	 * KP_nnnn.KP_0000(p_mid, "MAIN", "MENU", "KP_0001", true, params);<br>
	 * </ul>
	 * <br>
	 * 
	 * @param mid
	 *          로그인한 user id
	 * @param m1
	 *          메뉴 1차 코드 - 미사용시 empty_space("")
	 * @param m2
	 *          메뉴 2차 코드 - 미사용시 empty_space("")
	 * @param opcode
	 *          프로토콜 번호
	 * @param clear
	 *          이전 조회 데이터 삭제
	 * @param params
	 *          전송 데이터 page, line, email, ...
	 * 
	 */
	public void KP_0000(String mid, String m1, String m2, String opcode, boolean clear, Map<String, String> params) {
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, getMethodName() + params);

		if (clear) {
			clear();
		}

		String query = KP_xxxx(mid, m1, m2, opcode);
		if (params != null) {
			for (Map.Entry<String, ?> entry : params.entrySet()) {
				try {
					String param = "&" + entry.getKey() + "=" + entry.getValue();
					if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, param);
					query += param;
				} catch (Exception e) {

					// e.printStackTrace();
					if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, Log2.getStackTraceString(e));
				}
			}
		}

		mStrQuery = query;
		send("");
	}

	/**
	 * //KP_0000 메인 가변메뉴 요청<br>
	 * <br>
	 * 인터페이스ID : KP_0000<br>
	 * 인터페이스명 : 메인 가변메뉴 요청<br>
	 * 처리 내역<br>
	 * KPOPApp에서 MAIN 접속시 가변메뉴 정보요청.<br>
	 * ※ 공지사항을 제외한 나머지 메뉴는 id가 존재하거나 list일 경우 다음 메뉴가 곡목록임.<br>
	 * ※ p_mid값이 없으며, 받은 결과값으로 p_mid 셋팅,<br>
	 * <br>
	 * <b>변수명 데이터 설명</b><br>
	 * 
	 * @param email
	 *          이메일<br>
	 * <br>
	 */
	// @Deprecated
	// public void KP_0000(String mid, String m1, String m2, String email) {
	// clear();
	// String query = KPinit(mid, m1, m2, "KP_0000");
	// query += "&email=" + Base64.encode(email.getBytes());
	// mStrQuery = query;
	// put("");
	// }

	/**
	 * // KP_0001 동적메인메뉴 요청 <br>
	 * <br>
	 * 인터페이스 ID KP_0001 <br>
	 * 인터페이스명 메인 가변메뉴 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 MAIN 접속시 가변메뉴 정보요청. <br>
	 * ※ 공지사항을 제외한 나머지 메뉴는 id가 존재하거나 list일 경우 다음 메뉴가 곡목록임. <br>
	 * ※ p_mid값이 없으며, 받은 결과값으로 p_mid 셋팅, <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일
	 * 
	 */
	public void KP_0001(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0001");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0002 동적메인메뉴 요청 <br>
	 * <br>
	 * 인터페이스 ID KP_0002 <br>
	 * 인터페이스명 메인 가변메뉴 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 MAIN 접속시 가변메뉴 정보요청. <br>
	 * ※ 공지사항을 제외한 나머지 메뉴는 id가 존재하거나 list일 경우 다음 메뉴가 곡목록임. <br>
	 * ※ p_mid값이 없으며, 받은 결과값으로 p_mid 셋팅, <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일
	 * 
	 */
	@Deprecated
	public void KP_0002(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0002");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	public void KP_0003(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0003");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * 
	 * <pre>
	 * KP_0004 옵션메뉴내용조회
	 * </pre>
	 * 
	 * @param mid
	 * @param m1
	 * @param m2
	 */
	public void KP_0004(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0004");
		// query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0010 공지사항목록 <br>
	 * <br>
	 * 인터페이스 ID KP_0010 <br>
	 * 인터페이스명 공지사항 목록 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 공지사항 목록 요청 <br>
	 */
	public void KP_0010(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0010");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0011 공지사항상세 <br>
	 * <br>
	 * 인터페이스 ID KP_0011 <br>
	 * 인터페이스명 공지사항 상세 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 공지사항 상세정보 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * notice_id 공지id
	 * 
	 */
	public void KP_0011(String mid, String m1, String m2, String id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_0011");
		query += "&id=" + id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0020 반주곡 검색 <br>
	 * <br>
	 * 인터페이스 ID KP_0020 <br>
	 * 인터페이스명 반주곡 검색 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 검색 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type 검색구분(SONG,ARTIST,LYRIC,NUMBER) <br>
	 * search_word 검색어 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_0020(String mid, String m1, String m2, String type, String search_word, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_0020");
		query += "&type=" + type;
		try {
			query += "&search_word=" + URLEncoder.encode(search_word, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0020 반주곡 검색 <br>
	 * <br>
	 * 인터페이스 ID KP_0020 <br>
	 * 인터페이스명 반주곡 검색 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 검색 요청 <br>
	 * 곡명 우선 검색 목록 출력 후 가수명 검색 목록 출력(각 목록은 등록일 순 내림차순 정렬) <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type 검색구분(SONG,ARTIST,LYRIC,NUMBER) <br>
	 * search_word 검색어 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_0021(String mid, String m1, String m2, String type, String search_word, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_0021");
		query += "&type=" + type;
		try {
			query += "&search_word=" + URLEncoder.encode(search_word, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_0022 반주곡 검색 <br>
	 * <br>
	 * 인터페이스 ID KP_0020 <br>
	 * 인터페이스명 반주곡 검색 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 검색 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type 검색구분(SONG,ARTIST,LYRIC,NUMBER) <br>
	 * search_word 검색어 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_0022(String mid, String m1, String m2, String type, String search_word, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_0022");
		query += "&type=" + type;
		try {
			query += "&search_word=" + URLEncoder.encode(search_word, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2100 LISTEN메뉴 <br>
	 * <br>
	 * 인터페이스 ID KP_2100 <br>
	 * 인터페이스명 TOP100/실시간등록곡 목록(가변메뉴) <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 <br>
	 * TOP100/실시간등록곡 목록 요청(1page=30lists) <br>
	 * ※p_m2:LISTEN_TOP100, LISTEN_TIMELINE, 기타 가변메뉴 코드 추가 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_2100(String mid, String m1, String m2, String id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2100");
		if (!TextUtil.isEmpty(id)) {
			query += "&id=" + id;
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1000 SING메뉴 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_1000 <br>
	 * 인터페이스명 SING메뉴/서브/곡목록요청 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 SING MENU/서브MENU/곡목록 요청(모든 메뉴 및 목록은 30개 단위가 1page) <br>
	 * ※ 전달받은 메뉴에 해당하는 sect값이 존재하거나 list일 경우 다음 메뉴가 곡목록임 <br>
	 * <br>
	 * <b>변수명 데이터 설명</b> <br>
	 * id <br>
	 * page
	 * 
	 */
	public void KP_1000(String mid, String m1, String m2, String id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_1000");
		if (!TextUtil.isEmpty(id)) {
			query += "&id=" + id;
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1500 메뉴목록 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_1500 <br>
	 * 인터페이스명 메뉴 목록 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 KP_0000의 m1(1뎁스)메뉴를 통해 2뎁스 메뉴 정보를 반환한다. <br>
	 * <br>
	 * <b>변수명 데이터 설명
	 * 
	 */
	public void KP_1500(String mid, String m1, String m2, String uid, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_1500");
		// if (!TextUtil.isEmpty(id)) {
		// query += "&id=" + id;
		// }
		query += "&uid=" + uid;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1010 반주곡재생 <br>
	 * <br>
	 * 인터페이스 ID KP_1010 <br>
	 * 인터페이스명 반주곡 재생(하위버젼사용자용) <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 재생 요청 <br>
	 * ※ 해당 곡의 p_ncode값에 따른 독음이 존재할 경우, dokeum은 해당 언어의 독음값 <br>
	 * ※ p_m2는 해당 2차 메뉴코드값(SING_RECENT, KPOP 등) <br>
	 * ※ 2가지 유입경로 <br>
	 * SING>곡목록>반주곡 재생 <br>
	 * SING>곡목록>BEST HOLIC>Sing a SONG <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * L:1분미리듣기 <br>
	 * F:일일무료곡 <br>
	 * P:유료재생 <br>
	 * song_id 곡번호
	 * 
	 */
	@Deprecated
	public void KP_1010(String mid, String m1, String m2, String play_type, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1010");
		query += "&type=" + play_type;
		query += "&song_id=" + song_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * KP_1013 반주곡재생 KP_1010 대체 <br>
	 * 인터페이스명 반주곡 재생(하위버젼사용자용) <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 재생 요청 <br>
	 * ※ 해당 곡의 p_ncode값에 따른 독음이 존재할 경우, dokeum은 해당 언어의 독음값 <br>
	 * ※ p_m2는 해당 2차 메뉴코드값(SING_RECENT, KPOP 등) <br>
	 * ※ 2가지 유입경로 <br>
	 * SING>곡목록>반주곡 재생 <br>
	 * SING>곡목록>BEST HOLIC>Sing a SONG <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * L:1분미리듣기 <br>
	 * F:일일무료곡 <br>
	 * P:유료재생 <br>
	 * song_id 곡번호<br>
	 * audition_id 오디션번호<br>
	 * <br>
	 * 
	 */
	@Deprecated
	public void KP_1013(String mid, String m1, String m2, String play_type, String song_id, String audition_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1013");
		query += "&type=" + play_type;
		query += "&song_id=" + song_id;
		query += "&audition_id=" + audition_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * KP_1016 반주곡재생 KP_1013 대체 <br>
	 * 인터페이스명 반주곡 재생(하위버젼사용자용) <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 반주곡 재생 요청 <br>
	 * ※ 해당 곡의 p_ncode값에 따른 독음이 존재할 경우, dokeum은 해당 언어의 독음값 <br>
	 * ※ p_m2는 해당 2차 메뉴코드값(SING_RECENT, KPOP 등) <br>
	 * ※ 2가지 유입경로 <br>
	 * SING>곡목록>반주곡 재생 <br>
	 * SING>곡목록>BEST HOLIC>Sing a SONG <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * song_id 곡번호<br>
	 * audition_id 오디션번호<br>
	 * <br>
	 * 
	 */
	public void KP_1016(String mid, String m1, String m2, String song_id, String audition_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1016");
		query += "&song_id=" + song_id;
		query += "&audition_id=" + audition_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * KP_1014 1일 녹음곡 최대 업로드 확인 <br>
	 * - Response "list"의<br>
	 * : "is_upload" 값이 "Y"인 경우(녹음곡 업로드 가능)<br>
	 * - 현재 녹음곡 업로드 화면을 유지<br>
	 * - Response "info"의 "result_code"는 "00000", "result_message"는 "Success"<br>
	 * <br>
	 * : "is_upload" 값이 "N"인 경우(녹음곡 업로드 불가능)<br>
	 * - "업로드", "취소" 버튼 히든 처리<br>
	 * - Response "info"의 "result_code"는 "00900", "result_message"는 "업로드 불가 메세지"<br>
	 * <br>
	 * 
	 */
	public void KP_1014(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1014");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1011 파일업로드 (폐기->유지) <br>
	 * <br>
	 * 인터페이스 ID KP_1011 <br>
	 * 인터페이스명 파일 업로드 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 프로필 사진/녹음곡 업로드 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * PHOTO:프로필 사진 <br>
	 * RECORD:녹음곡파일 <br>
	 * filename upload할 file명
	 * 
	 */
	public void KP_1011(String mid, String m1, String m2, String song_id, String type, String audition_id, String path) {
		clear();
		String filename = path.substring(path.lastIndexOf(File.separator) + 1);
		String query = KP_xxxx(mid, m1, m2, "KP_1011");
		query += "&song_id=" + song_id;
		query += "&type=" + type;
		query += "&audition_id=" + audition_id;
		query += "&filename=" + filename;
		mStrQuery = query;
		send(path);

		// file = new File(path);
		upload();

		// 구파일업로드
		// String res = "";
		//
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, getParams());
		//
		// try {
		// res = FileUploder.HttpFileUpload(mStrQuery, getParams(), path);
		//
		// JSONObject obj = new JSONObject(res);
		// //if (_IKaraoke.DEBUG)Log.d(__CLASSNAME__, obj.toString(2));
		//
		// JSONObject record = obj.getJSONObject("record");
		// //if (_IKaraoke.DEBUG)Log.i(__CLASSNAME__, "record - " + "\"" + record.toString() + "\"");
		//
		// JSONArray infos = record.getJSONArray("info");
		// JSONObject info = infos.getJSONObject(0);
		//
		// setInfo(new KPnnnnItem(info));
		// if (_IKaraoke.DEBUG)Log.i(__CLASSNAME__, "info - " + "\"" + getInfo().toString(2) + "\"");
		//
		// if (getInfo().getString("result_code").equals("00000")) {
		// sendMessage(STATE_DATA_QUERY_SUCCESS, getInfo().getString("result_code"), getInfo()
		// .getString("result_message"));
		// } else {
		// sendMessage(STATE_DATA_QUERY_ERROR, getInfo().getString("result_code"), getInfo()
		// .getString("result_message"));
		// }
		//
		// } catch (Exception e) {
		//
		// sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "Unknown Error");
		// e.printStackTrace();
		// }
	}

	public void KP_1015(String mid, String m1, String m2, String song_id, String type, String audition_id, String path, long size) {
		clear();
		String filename = path.substring(path.lastIndexOf(File.separator) + 1);
		String query = KP_xxxx(mid, m1, m2, "KP_1015");
		query += "&song_id=" + song_id;
		query += "&type=" + type;
		query += "&audition_id=" + audition_id;
		query += "&filename=" + filename;
		query += "&size=" + size;
		mStrQuery = query;
		send(path);

		// file = new File(path);
		upload();
	}

	/**
	 * //KP_1016 파일다운로드<br>
	 * <br>
	 * 인터페이스ID : KP_1016<br>
	 * 인터페이스명 : 파일 다운로드 요청<br>
	 * 처리 내역<br>
	 * KPOPApp에서 녹음곡 다운로드 요청<br>
	 * <br>
	 * <b>변수명 데이터 설명</b><br>
	 * 
	 * @param type
	 *          : SONG(보류)/RECORD
	 * @param play_id
	 *          : song_id(보류)/record_id
	 * @param path
	 *          path 다운로드할 file명<br>
	 */
	public void KP_1016(String mid, String m1, String m2, String type, String play_id, String path) {
		clear();
		// String filename = path.substring(path.lastIndexOf(File.separator) + 1);
		String query = KP_xxxx(mid, m1, m2, "KP_1016");
		query += "&type=" + type;
		query += "&play_id=" + play_id;
		mStrQuery = query;
		setDownload(new File(path));
		send("");
		// file = getDownload();
	}

	/**
	 * <br>
	 * KP_6001 FEEL관리(파일업로드) KP_1011 대체 <br>
	 * type <br>
	 * PHOTO:프로필 사진 <br>
	 * POST:게시물 <br>
	 * RECORD:녹음곡 <br>
	 * <br>
	 * feel_id 녹음곡 또는 게시물 id <br>
	 * mode <br>
	 * ADD:등록 <br>
	 * UPDATE:수정 <br>
	 * DEL:삭제 <br>
	 * feel_type FEEL 등록인 경우 FEEL등록 <br>
	 * 화면 설정 값 <br>
	 * K : 반주곡 화면에서 등록 <br>
	 * R : 녹음곡재생 화면에서 등록 <br>
	 * T : 반주곡/녹음곡 및 오디션 <br>
	 * 화면을 제외한 화면에서 등록 <br>
	 * A : 오디션 화면에서 등록 <br>
	 * csid 반주곡/녹음곡 ID <br>
	 * comment 멘트(FEEL / 녹음곡소개글) <br>
	 * furl URL 정보 <br>
	 * <br>
	 * 
	 */
	public void KP_6001(String mid, String m1, String m2, String type, String feel_id, String mode, String title, String comment, String feel_type, String csid, String furl,
			String fhtm, String url_comment, String path, String order) {

		clear();

		String filename = "";
		try {
			filename = path.substring(path.lastIndexOf(File.separator) + 1);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String fn_rec = "", fn_img = "";

		if (("RECORD").equalsIgnoreCase(type)) {
			fn_rec = filename;
		} else {
			fn_img = filename;
		}

		String query = KP_xxxx(mid, m1, m2, "KP_6001");
		query += "&type=" + type;
		query += "&feel_id=" + feel_id;
		query += "&mode=" + mode;
		try {
			query += "&title=" + URLEncoder.encode(title, "UTF-8");
			query += "&comment=" + URLEncoder.encode(comment, "UTF-8");
			query += "&url_comment=" + URLEncoder.encode(url_comment, "UTF-8");
			;
			query += "&furl=" + URLEncoder.encode(furl, "UTF-8");
			;
			query += "&fhtm=" + URLEncoder.encode(fhtm, "UTF-8");
			;
		} catch (Exception e) {

			// e.printStackTrace();
		}
		query += "&fn_rec=" + fn_rec;
		query += "&fn_img=" + fn_img;
		query += "&feel_type=" + feel_type;
		query += "&csid=" + csid;
		query += "&order=" + order;

		mStrQuery = query;
		send(path);

		// file = new File(path);
	}

	/**
	 * // KP_1012 재생시간 기록 <br>
	 * <br>
	 * 인터페이스 ID KP_1012 <br>
	 * 인터페이스명 재생시간 기록 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 유료사용자의 재생시간을 기록 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * play_id 재생id <br>
	 * time 재생시간(초단위)
	 * 
	 */
	public void KP_1012(String mid, String m1, String m2, String play_id, String time, String song_id, String type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1012");
		query += "&play_id=" + play_id;
		query += "&time=" + time;
		query += "&song_id=" + song_id;
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1020 무료곡등록 <br>
	 * <br>
	 * 인터페이스 ID KP_1020<br>
	 * 관련 프로그램ID KPOPApp<br>
	 * 인터페이스명 무료곡 등록<br>
	 * 요청 작업주기 수시 처리 내역 KPOPApp에서 하루 무료곡 등록 요청 전송 데이터<br>
	 * 응답 데이터 변수명 데이터<br>
	 * <br>
	 * 변수명 데이터 설명<br>
	 * song_id 곡번호 <br>
	 * <br>
	 */
	public void KP_1020(String mid, String m1, String m2, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1020");
		query += "&song_id=" + song_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6050 애창곡등록 <br>
	 * <br>
	 * 인터페이스 ID KP_6050<br>
	 * 관련 프로그램ID KPOPApp<br>
	 * 인터페이스명 애창곡 등록<br>
	 * 요청 작업주기 수시 처리 내역 KPOPApp에서 하루 애창곡 등록 요청 전송 데이터<br>
	 * 응답 데이터 변수명 데이터<br>
	 * <br>
	 * 변수명 데이터 설명<br>
	 * song_id 곡번호 <br>
	 * <br>
	 */
	public void KP_6050(String mid, String m1, String m2, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6050");
		query += "&song_id=" + song_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1030 가사/독음정보 <br>
	 * <br>
	 * 인터페이스 ID KP_1030 <br>
	 * 인터페이스명 가사 독음 및 곡소개 정보 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 해당 곡의 가사보기 요청 <br>
	 * ※ p_m2는 해당 2차 메뉴의 코드값. <br>
	 * <b>변수명 데이터 설명 <br>
	 * song_id 곡번호 <br>
	 * type <br>
	 * GASA:가사 <br>
	 * INFO:곡소개정보
	 * 
	 */
	public void KP_1030(String mid, String m1, String m2, String type, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1030");
		query += "&song_id=" + song_id;
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1040 가수의 다른곡 목록 <br>
	 * <br>
	 * 인터페이스 ID KP_1040 <br>
	 * 인터페이스명 가수의 전체곡 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 해당 가수의 전체 곡 목록 요청(1page=30lists) <br>
	 * ※ p_m2는 해당 2차 메뉴의 코드값. <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * artist_id 곡번호 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_1040(String mid, String m1, String m2, String artist_id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_1040");
		query += "&artist_id=" + artist_id;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1050 BEST HOLIC(폐기) <br>
	 * <br>
	 * 인터페이스 ID KP_1050 <br>
	 * 인터페이스명 BEST HOLIC <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 BEST HOLIC선택시 해당 반주곡의 스트리밍 재생 시간 최고 기록사용자 정보 요청 <br>
	 * ※ 2가지 유입경로 <br>
	 * SING>곡목록>BEST HOLIC <br>
	 * LISTEN>녹음곡목록>BEST HOLIC <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * song_id 곡번호
	 * 
	 */
	@Deprecated
	public void KP_1050(String mid, String m1, String m2, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1050");
		query += "&song_id=" + song_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_1051 BEST HOLIC <br>
	 * <br>
	 * 인터페이스 ID KP_1051 <br>
	 * 인터페이스명 BEST HOLIC <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 BEST HOLIC선택시 해당 반주곡의 스트리밍 재생 시간 최고 기록사용자 정보 요청 <br>
	 * ※ 2가지 유입경로 <br>
	 * SING>곡목록>BEST HOLIC <br>
	 * LISTEN>녹음곡목록>BEST HOLIC <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * song_d 곡번호 <br>
	 * 
	 */
	public void KP_1051(String mid, String m1, String m2, String song_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_1051");
		query += "&song_id=" + song_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2000 TOP100/실시간등록곡 <br>
	 * <br>
	 * 인터페이스 ID KP_2000 <br>
	 * 인터페이스명 TOP100/실시간등록곡 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 <br>
	 * TOP100/실시간등록곡 목록 요청(1page=30lists) <br>
	 * ※p_m2:LISTEN_TOP100, LISTEN_TIMELINE <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * page 페이지번호
	 * 
	 */
	@Deprecated
	public void KP_2000(String mid, String m1, String m2, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2000");
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2100 TOP100/실시간등록곡(가변메뉴) <br>
	 * <br>
	 * 인터페이스 ID KP_2100 <br>
	 * 인터페이스명 TOP100/실시간등록곡 목록(가변메뉴) <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 <br>
	 * TOP100/실시간등록곡 목록 요청(1page=30lists) <br>
	 * ※p_m2:LISTEN_TOP100, LISTEN_TIMELINE, 기타 가변메뉴 코드 추가 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_2100(String mid, String m1, String m2, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2100");
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2001 반주SONG's (이 노래의 녹음곡) <br>
	 * <br>
	 * 인터페이스 ID KP_2001 <br>
	 * 인터페이스명 반주SONG's (이 노래의 녹음곡) 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 해당 반주SONG's (이 노래의 녹음곡) 목록 요청(1page는 30개목록) <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * song_id 곡id <br>
	 * page 페이지번호 <br>
	 */
	public void KP_2001(String mid, String m1, String m2, String song_id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2001");
		query += "&song_id=" + song_id;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2010 녹음곡재생 <br>
	 * <br>
	 * 인터페이스 ID KP_2010 <br>
	 * 인터페이스명 녹음곡 재생 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 녹음곡 재생 요청 <br>
	 * ※ p_m1, p_m2는 해당 1,2차 메뉴의 코드값. <br>
	 * ※ 유입경로는 <br>
	 * BEST Heart Hit SONG : 해당 회원의 최고 하트 보유 녹음곡 재생 <br>
	 * MY BEST : 해당 곡의 최대 추천수 보유 회원의 녹음곡 재생화면 <br>
	 * RECORD_ID를 통한 재생이 있음. <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * type <br>
	 * RECORD:녹음곡id <br>
	 * UID:회원id <br>
	 * SONG:곡id <br>
	 * play_id type에 따른 id값
	 * 
	 */
	@Deprecated
	public void KP_2010(String mid, String m1, String m2, String type, String play_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2010");
		query += "&type=" + type;
		query += "&play_id=" + play_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2016 녹음곡재생 <br>
	 * <br>
	 * KP_2016 녹음곡재생 KP_2010 대체 <br>
	 * 인터페이스 ID KP_2016 <br>
	 * 인터페이스명 녹음곡 재생 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 녹음곡 재생 요청 <br>
	 * ※ p_m1, p_m2는 해당 1,2차 메뉴의 코드값. <br>
	 * ※ 유입경로는 <br>
	 * BEST Heart Hit SONG : 해당 회원의 최고 하트 보유 녹음곡 재생 <br>
	 * MY BEST : 해당 곡의 최대 추천수 보유 회원의 녹음곡 재생화면 <br>
	 * RECORD_ID를 통한 재생이 있음. <br>
	 * <br>
	 * ※reply_hit가 ments_cnt로 변경 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * RECORD:녹음곡id <br>
	 * UID:회원id <br>
	 * SONG:곡id <br>
	 * play_id type에 따른 id값 <br>
	 * 
	 */
	public void KP_2016(String mid, String m1, String m2, String type, String play_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2016");
		query += "&type=" + type;
		query += "&play_id=" + play_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2011 댓글 좌우 스크롤 (폐기/유지) <br>
	 * <br>
	 * 인터페이스 ID KP_2011 <br>
	 * 인터페이스명 댓글요청 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 녹음곡 재생화면에서 댓글 회원 프로필 요청(1page는 8개 댓글목록) <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * record_id 녹음곡id <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_2011(String mid, String m1, String m2, String record_id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2011");
		query += "&record_id=" + record_id;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2017 댓글요청 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 녹음곡 재생화면 또는 뷰화면에서 댓글 목록 요청(1page는 8개 댓글목록) <br>
	 * 변수명 데이터 설명 <br>
	 * feel_id 녹음곡 또는 FEEL 게시물 id <br>
	 * <br>
	 * - 녹음곡 : <br>
	 * KP_2016 :record_id <br>
	 * - FEEL게시물 : KP_6000 :feel_id <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_2017(String mid, String m1, String m2, String play_id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2017");
		query += "&feel_id=" + play_id;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2012 공유 <br>
	 * <br>
	 * 인터페이스 ID KP_2012 <br>
	 * 인터페이스명 공유하기 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 공유한 로그 기록 요청 <br>
	 * (서버응답 결과 무시) <br>
	 * p_m2:LISTEN_TOP100 or LISTEN_TIMELINE <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * record_id 녹음곡id <br>
	 * type SNS종류 <br>
	 * M:me2day <br>
	 * T:Twitter <br>
	 * F:Facebook <br>
	 * E:Email
	 * 
	 */
	public void KP_2012(String mid, String m1, String m2, String uid, String record_id, String mode) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2012");
		query += "&uid=" + uid;
		query += "&record_id=" + record_id;
		query += "&mode=" + mode;
		mStrQuery = query;
		send("");
	}

	// KP_2013 이메일보내기

	/**
	 * // KP_2014 하트쏘기 <br>
	 * <br>
	 * 인터페이스 ID KP_2014 <br>
	 * 인터페이스명 하트쏘기 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 하트쏘기 요청 <br>
	 * p_m2:LISTEN_TOP100 or LISTEN_TIMELINE <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * uid 하트받은 회원id <br>
	 * record_id 녹음곡id <br>
	 * heart 하트수(최대 100개)
	 * 
	 */
	@Deprecated
	public void KP_2014(String mid, String m1, String m2, String uid, String record_id, String heart) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2014");
		query += "&uid=" + uid;
		query += "&record_id=" + record_id;
		query += "&heart=" + heart;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2015 녹음곡 댓글입력&수정 <br>
	 * <br>
	 * 인터페이스 ID KP_2015 <br>
	 * 인터페이스명 녹음곡 댓글입력&수정 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 댓글입력 요청 <br>
	 * p_m2:LISTEN_TOP100 or LISTEN_TIMELINE <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * mode <br>
	 * ADD:등록 <br>
	 * UPDATE:수정 <br>
	 * record_id 녹음곡id <br>
	 * ment 댓글내용
	 * 
	 */
	@Deprecated
	public void KP_2015(String mid, String m1, String m2, String mode, String record_id, String comment) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2015");
		query += "&mode=" + mode;
		query += "&record_id=" + record_id;
		try {
			query += "&comment=" + URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		mStrQuery = query;
		send("");
	}

	/**
	 * <pre>
	 * KP_2018 댓글/신고 등록/수정/삭제 FEEL 게시물/녹음곡 통합 댓글/신고 등록 KP_2015 대체
	 * feel_id 녹음곡 또는 FEEL게시물 id 
	 * - 녹음곡 : 
	 * KP_2016 :record_id 
	 * - FEEL게시물 : KP_6000 :feel_id 
	 * mode 
	 * ADD : 등록 
	 * UPDATE : 수정 
	 * DEL : 삭제 
	 * seq 댓글 시퀀스 번호 
	 * siren_code 
	 * A : 광고 
	 * E : 기타 
	 * F : 욕설 
	 * S : 선정성 
	 * comment 댓글 내용
	 *  
	 * 5. 댓글 등록(KP_2018)
	 * : 댓글 등록 시 댓글 목록의 회원 닉네임을 선택 후 등록 기능 추가(카카오스토리와 동일 방식)
	 * : Request
	 *    - "uid" 변수에 선택한 닉네임을 ","(콤마) 로 구분해서 KP_2018 Request 파라미터로 전달
	 * </pre>
	 * 
	 * <br>
	 */
	public void KP_2018(String mid, String m1, String m2, String feel_id, String mode, String seq, String siren_code, String comment, String uid) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_2018");
		query += "&feel_id=" + feel_id;
		query += "&mode=" + mode;
		query += "&seq=" + seq;
		query += "&siren_code=" + siren_code;
		try {
			query += "&comment=" + URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		query += "&uid=" + uid;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_2020 녹음곡 검색 <br>
	 * <br>
	 * 인터페이스 ID KP_2020 <br>
	 * 인터페이스명 녹음곡 검색 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 녹음곡 검색 요청(1페이지는 30개목록) <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * type <br>
	 * SONG:제목 <br>
	 * ARTIST:가수명 <br>
	 * MEMBER:회원명 <br>
	 * 통합검색으로 수정함. <br>
	 * search_word 검색어(곡명,가수명,곡번호,닉네임) <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_2020(String mid, String m1, String m2, String type, String search_word, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_2020");
		query += "&type=" + type;
		try {
			query += "&search_word=" + URLEncoder.encode(search_word, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3000 MY HOLIC정보(폐기) <br>
	 * <br>
	 * 인터페이스 ID KP_3000 <br>
	 * 인터페이스명 프로필 정보 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 자신 또는 타회원의 프로필 정보 요청 <br>
	 * ※ 아래 4가지 유입경로가 있음. <br>
	 * MAIN>HOLIC PEOPLE <br>
	 * SING>곡목록>BEST HOLIC>GO HOLIC <br>
	 * MYHOLIC <br>
	 * MYHOLIC>프로필 <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * uid 회원id
	 * 
	 */
	@Deprecated
	public void KP_3000(String mid, String m1, String m2, String uid) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_3000");
		query += "&uid=" + uid;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3001 MY HOLIC정보 <br>
	 * <br>
	 * 인터페이스 ID KP_3001 <br>
	 * 인터페이스명 프로필 정보 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 자신 또는 타회원의 프로필 정보 요청 <br>
	 * ※ 아래 4가지 유입경로가 있음. <br>
	 * MAIN>HOLIC PEOPLE <br>
	 * SING>곡목록>BEST HOLIC>GO HOLIC <br>
	 * MYHOLIC <br>
	 * MYHOLIC>프로필 <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * uid 회원id <br>
	 */
	public void KP_3001(String mid, String m1, String m2, String uid) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_3001");
		query += "&uid=" + uid;
		mStrQuery = query;
		send("");
	}

	/**
	 * KP_3002 GO(MY/BEST) HOLIC 회원의 녹음곡/팔로워 목록 정보<br>
	 * <br>
	 * 인터페이스 ID KP_3002 인터페이스명 GO(MY/BEST) HOLIC 회원의 녹음곡/팔로워 목록 정보 <br>
	 * <b>변수명 데이터 설명<br>
	 * uid GO(MY/BEST) 회원id<br>
	 * type<br>
	 * “n” : 최신곡목록<br>
	 * “p” : 인기곡목록<br>
	 * “f” : 팔로워목록<br>
	 */
	public void KP_3002(String mid, String m1, String m2, String uid, String type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_3002");
		query += "&uid=" + uid;
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3010 녹음곡목록 <br>
	 * <br>
	 * 인터페이스 ID KP_3010 <br>
	 * 인터페이스명 녹음곡 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원자신 또는 타회원의 녹음곡 목록요청 <br>
	 * (타회원의 녹음곡은 공개목록만 출력되며, 1page는 됨-30개목록단위) <br>
	 * ※ 아래 3가지 유입경로가 있음. <br>
	 * SING>곡목록>BEST HOLIC>All Recording SONGs(선택 회원의 녹음곡 목록 공개곡 목록) <br>
	 * LISTEN>녹음곡목록>이 회원의 다른 녹음곡 듣기(해당 회원의 MYHOLIC>나의녹음곡>공개곡목록) <br>
	 * MYHOLIC>녹음곡목록 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * uid 회원id <br>
	 * order <br>
	 * UPDATE:업데이트순 <br>
	 * ON:공개 <br>
	 * OFF:비공개 <br>
	 * HEART:추천수 <br>
	 * page 페이지번호
	 * 
	 */
	@Deprecated
	public void KP_3010(String mid, String m1, String m2, String uid, String order, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_3010");
		query += "&uid=" + uid;
		query += "&order=" + order;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3013 녹음곡목록 <br>
	 * <br>
	 * 인터페이스 ID KP_3013 <br>
	 * 인터페이스명 녹음곡 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원자신 또는 타회원의 녹음곡 목록요청 <br>
	 * (타회원의 녹음곡은 공개목록만 출력되며, 1page는 됨-30개목록단위) <br>
	 * ※ 아래 3가지 유입경로가 있음. <br>
	 * SING>곡목록>BEST HOLIC>All Recording SONGs(선택 회원의 녹음곡 목록 공개곡 목록) <br>
	 * LISTEN>녹음곡목록>이 회원의 다른 녹음곡 듣기(해당 회원의 MYHOLIC>나의녹음곡>공개곡목록) <br>
	 * MYHOLIC>녹음곡목록 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * uid 회원id <br>
	 * order <br>
	 * UPDATE:업데이트순 <br>
	 * ON:공개 <br>
	 * OFF:비공개 <br>
	 * HEART:추천수 <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_3013(String mid, String m1, String m2, String uid, String order, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_3013");
		query += "&uid=" + uid;
		query += "&order=" + order;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3011 녹음곡 공개설정/삭제 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원자신의 녹음곡에 대해 공개/비공개 설정 및 삭제 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * mode <br>
	 * ON:공개전환 <br>
	 * OFF:비공개전화 <br>
	 * DEL:삭제 <br>
	 * record_id 녹음곡id(:구분 반복) <br>
	 */
	public void KP_3011(String mid, String m1, String m2, String mode, String record_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_3011");
		query += "&mode=" + mode;
		query += "&record_id=" + record_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_3012 내 프로필 자기소개 수정 <br>
	 * <br>
	 * 인터페이스 ID KP_3012 <br>
	 * 인터페이스명 내 프로필 자기소개 수정 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원자신의 프로필 소개정보 수정 요청 <br>
	 * <br>
	 * 변수명 데이터 설명 <br>
	 * comment 자기소개 내용
	 * 
	 */
	public void KP_3012(String mid, String m1, String m2, String comment) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_3012");
		try {
			query += "&comment=" + URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4000 상품권 목록 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_4000 <br>
	 * 인터페이스명 상품권 목록 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 상품권 목록 요청 <br>
	 */
	@Deprecated
	public void KP_4000(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4000");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4001 상품권 목록<br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_4001 <br>
	 * 인터페이스명 상품권 목록 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 상품권 목록 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 */
	public void KP_4001(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4001");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4010 상품권 구매 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_4010 <br>
	 * 인터페이스명 상품권 구매 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 상품권 구매 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * goodscode 상품 코드 <br>
	 * is_pay 결제성공유무 <br>
	 * Y:성공,N:실패,D:취소 <br>
	 * pay_type 결제방법 <br>
	 * HP:휴대폰, <br>
	 * CARD:신용카드, <br>
	 * MARKET:안드로이드 결제 <br>
	 * iu_type 결제요청:I, 결제완료:U <br>
	 * tid 안드로이드 결제 시스템의 주문번호
	 * 
	 */
	@Deprecated
	public void KP_4010(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4010");
		query += "&goodscode=" + goodscode;
		query += "&tid=" + tid;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		mStrQuery = query;
		send("");
	}

	/**
	 * 기존:KP_4010 상품권 구매 <br>
	 * 신규:KP_4005(충전 상품 구매:KP_4004 -> KP_4005 연동)
	 */
	public void KP_4005(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4005");
		query += "&goodscode=" + goodscode;
		query += "&tid=" + tid;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		mStrQuery = query;
		send("");
	}

	/**
	 * 기존:KP_4010 상품권 구매 <br>
	 * 신규:KP_4005(충전 상품 구매:KP_4004 -> KP_4005 연동)
	 */
	public void KP_40050(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type, String list) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4005");
		query += "&goodscode=" + goodscode;
		query += "&tid=" + tid;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		query += "&list=" + list;
		mStrQuery = query;
		send("");
	}

	/**
	 * //KP_4011 holic 상품권 구매
	 */
	@Deprecated
	public void KP_4011(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4011");
		query += "&goodscode=" + goodscode;
		query += "&tid=" + tid;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		mStrQuery = query;
		send("");
	}

	/**
	 * 기존:KP_4011 holic 상품권 구매<br>
	 * 신규:KP_4003(이용권 구매:KP_4002 -> KP_4003 연동)<br>
	 */
	public void KP_4003(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4003");
		query += "&goodscode=" + goodscode;
		query += "&tid=" + tid;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4012 Tapjoy 마켓플레이스 광고 진입 버튼(이미지) 선택 시 사용자 로그 기록
	 */
	public void KP_4012(String mid, String m1, String m2, String type) {
		String query = KP_xxxx(mid, m1, m2, "KP_4012");
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4013 전면광고 오픈 로그 <br>
	 * <br>
	 * 인터페이스 ID KP_4013 <br>
	 * 인터페이스명 전면광고 오픈 로그 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 반주곡 재생 및 메인 종료 시 전면광고 오픈 로그<br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 */
	public void KP_4013(String mid, String m1, String m2, String type) {
		String query = KP_xxxx(mid, m1, m2, "KP_4013");
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4014 광고정보조회
	 */
	public void KP_4014(String mid, String m1, String m2) {
		String query = KP_xxxx(mid, m1, m2, "KP_4014");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4020 내 이용권정보 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_4020 <br>
	 * 인터페이스명 내 이용권 정보 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 내 이용권 정보 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type 이용권타입 <br>
	 * (UNIT:곡단위,TERM:기간제)
	 * 
	 */
	public void KP_4020(String mid, String m1, String m2, String type) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4020");
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_4021 환불요청 <br>
	 * <br>
	 * 인터페이스 ID KP_4021 <br>
	 * 인터페이스명 환불요청 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 에서 환불요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * pay_id 구매id
	 * 
	 */
	public void KP_4021(String mid, String m1, String m2, String pay_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_4021");
		query += "&pay_id=" + pay_id;
		mStrQuery = query;
		send("");
	}

	// KP_4030 결제하기

	/**
	 * // KP_5000 이용약관정보 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_5000 <br>
	 * 인터페이스명 이용약관정보 요청 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 회원등록 정보 요청
	 * 
	 */
	public void KP_5000(String mid, String m1, String m2) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5000");
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5010 회원가입
	 * 
	 * <br>
	 * 인터페이스 ID KP_5010 <br>
	 * 인터페이스명 회원가입/수정 요청 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원등록/수정 정보 요청 <br>
	 * ※ email, pwd를 비교하여 기존 등록회원이면 수정, email이 없으면 등록함. <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * nickname 별칭 <br>
	 * email 이메일주소 <br>
	 * pwd 패스워드 <br>
	 * sex 성별(M:남성, F:여성) <br>
	 * birthday 생년월일 <br>
	 * ncode 국가코드 <br>
	 * article_id 이용약관id <br>
	 * using_id 개인정보 수집 및 이용안내id
	 * 
	 */
	@Deprecated
	public void KP_5010(String mid, String m1, String m2, String nickname, String email, String pwd, String sex, String birthday, String ncode, String article_id, String using_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5010");
		try {
			query += "&nickname=" + URLEncoder.encode(nickname, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&pwd=" + Base64.encode(pwd.getBytes());
		query += "&sex=" + sex;
		query += "&birthday=" + birthday;
		query += "&ncode=" + ncode.toUpperCase();
		query += "&article_id=" + article_id;
		query += "&using_id=" + using_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5020 회원가입
	 * 
	 * <br>
	 * 인터페이스 ID KP_5020 <br>
	 * 인터페이스명 회원가입 요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원등록/수정 정보 요청 <br>
	 * ※ 이메일, 비밀번호, 닉네임의 정보로 회원가입 요청. <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * nickname 별칭 <br>
	 * email 이메일주소 <br>
	 * pwd 패스워드 <br>
	 * article_id 이용약관id <br>
	 * using_id 개인정보 수집 및 이용안내id
	 * 
	 */
	public void KP_5020(String mid, String m1, String m2, String nickname, String email, String pwd, String sex, String birthday, String ncode, String article_id, String using_id,
			String recommend_id, String cardno) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5020");
		try {
			query += "&nickname=" + URLEncoder.encode(nickname, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&pwd=" + Base64.encode(pwd.getBytes());
		query += "&sex=" + sex;
		query += "&birthday=" + birthday;
		query += "&ncode=" + ncode.toUpperCase();
		query += "&article_id=" + article_id;
		query += "&using_id=" + using_id;
		query += "&recommend_id=" + recommend_id;
		if (!TextUtil.isEmpty(cardno)) {
			query += "&cardno=" + Base64.encode(cardno.getBytes());
		}
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * 인터페이스 ID KP_5021 <br>
	 * 인터페이스명 회원정보 수정 <br>
	 * 처리 내역 <br>
	 * KPOPApp 회원정보 수정 요청 <br>
	 * ※ 회원의 p_mid 정보로 회원수정 요청.
	 * 
	 * @param mid
	 * @param m1
	 * @param m2
	 * @param nickname
	 * @param email
	 * @param pwd
	 * @param sex
	 * @param birthday
	 * @param ncode
	 * @param article_id
	 * @param using_id
	 */
	public void KP_5021(String mid, String m1, String m2, String nickname, String email, String pwd, String sex, String birthday, String ncode, String article_id, String using_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5021");
		try {
			query += "&nickname=" + URLEncoder.encode(nickname, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&pwd=" + Base64.encode(pwd.getBytes());
		query += "&sex=" + sex;
		query += "&birthday=" + birthday;
		query += "&ncode=" + ncode.toUpperCase();
		query += "&article_id=" + article_id;
		query += "&using_id=" + using_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5011 EMAIL중복체크 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_5011 <br>
	 * 인터페이스명 EMAIL 중복체크 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 회원등록시 이메일 중북 체크 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일주소
	 * 
	 */
	public void KP_5011(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5011");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5012 EMAIL중복체크 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_5012 <br>
	 * 인터페이스명 회원정보 조회 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 회원등록시 이메일 중북 체크 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일주소
	 * 
	 */
	public void KP_5012(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5012");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5013 회원탈퇴 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_5013 <br>
	 * 인터페이스명 회원탈퇴 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 회원탈퇴 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일주소
	 * 
	 */
	public void KP_5013(String mid, String m1, String m2, String email) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5013");
		query += "&email=" + Base64.encode(email.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5014 비밀번호변경 <br>
	 * <br>
	 * <br>
	 * 인터페이스 ID KP_5014 <br>
	 * 인터페이스명 비밀번호 변경 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp 비밀번호 변경 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일주소 <br>
	 * new_pwd 변경될 비밀번호 <br>
	 * con_pwd 변경될 비밀번호 확인
	 * 
	 */
	public void KP_5014(String mid, String m1, String m2, String email, String new_pwd, String con_pwd) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5014");
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&new_pwd=" + Base64.encode(new_pwd.getBytes());
		query += "&con_pwd=" + Base64.encode(con_pwd.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_5015 아이디/비밀번호 찾기
	 * 
	 * <br>
	 * 작업주기 수시 <br>
	 * <br>
	 * 처리 내역 <br>
	 * <br>
	 * KPOPApp 비밀번호 변경 요청 <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일 <br>
	 * nickname 닉네임 <br>
	 * birthday 생년월일(dd.mm.yyyy) <br>
	 * 
	 */
	public void KP_5015(String mid, String m1, String m2, String email, String nickname, String birthday) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_5015");
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&nickname=" + nickname;
		query += "&birthday=" + birthday;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6000 FEEL목록요청
	 * 
	 * <br>
	 * 인터페이스 ID KP_6000 <br>
	 * 인터페이스명 FEEL목록요청 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 FEEL목록(녹음글, 게시물) 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * type <br>
	 * ”ALL” : 전체 FEEL 게시물 <br>
	 * ”MY” : 내가 쓴 FEEL 게시물 <br>
	 * - MY HOLIC > FEELING <br>
	 * ”OT” : 다른 uid가 쓴 FEEL 게시물 <br>
	 * - GO HOLIC > FEELING <br>
	 * uid GO HOLIC > FEELING 접근 시 해당 회원의 MEMBER_ID <br>
	 * Type 이 OT인 경우 uid 전달. <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_6000(String mid, String m1, String m2, String uid, String type, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6000");
		query += "&uid=" + uid;
		query += "&type=" + type;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * KP_6002 FEEL 상세화면 요청 <br>
	 * <br>
	 * 인터페이스 ID KP_6002 <br>
	 * 인터페이스명 FEEL 상세화면 요청 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 FEEL상세화면 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * feel_id feel 게시물 유니크 ID
	 * 
	 */
	public void KP_6002(String mid, String m1, String m2, String feel_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6002");
		query += "&feel_id=" + feel_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * // KP_6003 좋아요(하트) 등록/수정요청 <br>
	 * <br>
	 * <b>type <br>
	 * RECORD : 녹음곡재생화면 <br>
	 * FEEL : FEEL 상세화면 <br>
	 * <br>
	 * record_id 녹음곡 ID(record_id) <br>
	 * feel_id FEEL ID(feel_id) <br>
	 */
	public void KP_6003(String mid, String m1, String m2, String type, String record_id, String feel_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6003");
		query += "&type=" + type;
		query += "&record_id=" + record_id;
		query += "&feel_id=" + feel_id;
		mStrQuery = query;
		send("");
	}

	public void KP_6004(String mid, String m1, String m2, String type, String feel_id, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6004");
		query += "&type=" + type;
		query += "&feel_id=" + feel_id;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6010 팔로잉/팔로워 등록/수정/삭제 <br>
	 * 인터페이스 ID KP_6010 <br>
	 * 인터페이스명 팔로잉/팔로워 등록/수정/삭제 <br>
	 * <br>
	 * <b>처리 내역 <br>
	 * KPOPApp에서 팔로잉/팔로우 등록/수정 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * uid 팔로잉 아이디 <br>
	 * seq 유니크 시퀀스번호
	 * 
	 */
	public void KP_6010(String mid, String m1, String m2, String uid, String seq) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6010");
		query += "&uid=" + uid;
		query += "&seq=" + seq;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6011 팔로잉/팔로워 목록 <br>
	 * <br>
	 * 인터페이스 ID KP_6011 <br>
	 * 인터페이스명 팔로잉/팔로워 목록 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 팔로잉/팔로워 목록 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * ”FOLLOWING” : 팔로잉 목록 <br>
	 * ”FOLLOWER” : 팔로워 목록 <br>
	 * uid HOLIC uid <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_6011(String mid, String m1, String m2, String type, String uid, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6011");
		query += "&type=" + type;
		query += "&uid=" + uid;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6020 방명록 등록/삭제 <br>
	 * <br>
	 * 인터페이스 ID KP_6020 <br>
	 * 인터페이스명 방명록 등록/삭제 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 방명록 등록/삭제 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * type <br>
	 * ADD : 방명록 내용 등록 <br>
	 * DEL : 방명록 삭제 <br>
	 * uid HOLIC 회원 ID <br>
	 * seq 방명록 시퀀스번호 <br>
	 * comment 방명록 내용
	 * 
	 */
	public void KP_6020(String mid, String m1, String m2, String mode, String uid, String seq, String comment) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6020");
		query += "&mode=" + mode;
		query += "&uid=" + uid;
		query += "&seq=" + seq;
		try {
			query += "&comment=" + URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			// e.printStackTrace();
		}
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6021 방명록 목록 <br>
	 * <br>
	 * 인터페이스 ID KP_6021 <br>
	 * 인터페이스명 방명록 목록 <br>
	 * <br>
	 * <b>처리 내역</b> <br>
	 * KPOPApp에서 방명록 목록 요청 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * uid HOLIC uid <br>
	 * page 페이지번호
	 * 
	 */
	public void KP_6021(String mid, String m1, String m2, String uid, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6021");
		query += "&uid=" + uid;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_9001 로그인체크 <br>
	 * <br>
	 * 인터페이스 ID KP_9001 관련 프로그램ID KPOPApp 인터페이스명 로그인 체크 작업주기 수시 처리 내역 KPOPApp에서 MAIN 접속시 로그인 정보를 취득하여 <br>
	 * 로그인 유무 체크. 전송 데이터 응답 데이터 변수명 데이터 설명 변수명 데이터 설명 email 이메일 pwd 사용자 패스워드 Request <br>
	 * http://kpop.kymedia <br>
	 * .kr/protocol/index.php?p_ver=1.0&p_mid=&p_stbid=xxx&p_m1=AUTH&p_m2=LOGIN&p_opcode <br>=
	 * KP_9001&email=isyoon@kymedia.kr&pwd=is110315 <br>
	 * <br>
	 * 
	 * @param email
	 * <br>
	 * @param pwd
	 */
	@Deprecated
	public void KP_9001(String mid, String email, String pwd) {
		clear();
		String query = KP_xxxx(mid, "AUTH", "LOGIN", "KP_9001");
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&pwd=" + Base64.encode(pwd.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_9002 로그인체크 <br>
	 * <br>
	 * 인터페이스 ID KP_9002 <br>
	 * 인터페이스명 로그인 체크 <br>
	 * 처리 내역 <br>
	 * KPOPApp에서 MAIN 접속시 로그인 정보를 취득하여 로그인 유무 체크. <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 * email 이메일 <br>
	 * pwd 사용자 패스워드
	 */
	public void KP_9002(String mid, String email, String pwd) {
		clear();
		String query = KP_xxxx(mid, "AUTH", "LOGIN", "KP_9002");
		query += "&email=" + Base64.encode(email.getBytes());
		query += "&pwd=" + Base64.encode(pwd.getBytes());
		mStrQuery = query;
		send("");
	}

	/**
	 * <br>
	 * KP_9003 사용자푸시정보등록
	 */
	public void KP_9003(String mid, String Auth, String REGID, String SID, String LSID, String type) {
		clear();
		String query = KP_xxxx(mid, "AUTH", "PUSH", "KP_9003");
		query += "&Auth=" + Auth;
		query += "&REGID=" + REGID;
		query += "&SID=" + SID;
		query += "&LSID=" + LSID;
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_9004 알림 설정 목록 <br>
	 * <br>
	 * 인터페이스 ID KP_9004 <br>
	 * 인터페이스명 알림 설정 목록 <br>
	 * 처리 내역 <br>
	 * 알림 설정 목록 <br>
	 * <br>
	 * <b>변수명 데이터 설명 <br>
	 */
	public void KP_9004(String mid) {
		clear();
		String query = KP_xxxx(mid, "PUSH", "PUSH_LIST", "KP_9004");
		mStrQuery = query;
		send("");
	}

	/**
	 * KP_6032 오디션 개최권 구매 <br>
	 * <br>
	 * 인터페이스 ID KP_6032 <br>
	 * 인터페이스명 오디션 개최권 구매 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 오디션 개최권 구매 요청
	 */
	@Deprecated
	public void KP_6032(String mid, String m1, String m2, String goodscode, String tid, String is_pay, String pay_type, String iu_type, String song_id, String audition_title) {
		String query = KP_xxxx(mid, m1, m2, "KP_6032");
		query += "&goodscode=" + goodscode;
		query += "&is_pay=" + is_pay;
		query += "&pay_type=" + pay_type;
		query += "&iu_type=" + iu_type;
		query += "&tid=" + tid;
		query += "&song_id=" + song_id;
		query += "&audition_title=" + audition_title;
		mStrQuery = query;
		send("");
	}

	/**
	 * KP_6033 오디션 목록 <br>
	 * <br>
	 * 인터페이스 ID KP_6033 <br>
	 * 인터페이스명 오디션 목록 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 오디션 목록
	 */
	public void KP_6033(String mid, String m1, String m2, String uid, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6033");
		query += "&uid=" + uid;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * // KP_6035 오디션 공개/비공개 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 오디션의 공개/비공개 <br>
	 * <br>
	 * <b>변수명 데이터 설명 변수명 데이터 설명 <br>
	 * uid 회원id<br>
	 * mode <br>
	 * ON:공개전환 <br>
	 * OFF:비공개전화 <br>
	 * DEL:삭제 <br>
	 * audition_id 오디션id <br>
	 */
	public void KP_6035(String mid, String m1, String m2, String uid, String mode, String audition_id) {
		clear();
		String query = KP_xxxx(mid, m1, m2, "KP_6035");
		query += "&uid=" + uid;
		query += "&mode=" + mode;
		query += "&audition_id=" + audition_id;
		mStrQuery = query;
		send("");
	}

	/**
	 * KP_6041 쪽지함 <br>
	 * <br>
	 * 인터페이스 ID KP_6041 <br>
	 * 인터페이스명 쪽지함 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 쪽지함
	 */
	public void KP_6041(String mid, String m1, String m2, String uid, int page) {
		if (page == 1) {
			clear();
		}
		String query = KP_xxxx(mid, m1, m2, "KP_6041");
		query += "&uid=" + uid;
		query += "&page=" + page;
		mStrQuery = query;
		send("");
	}

	/**
	 * KP_6043 쪽지삭제/차단 <br>
	 * <br>
	 * 인터페이스 ID KP_6043 <br>
	 * 인터페이스명 쪽지삭제/차단 <br>
	 * <br>
	 * 처리 내역 <br>
	 * KPOPApp 에서 쪽지삭제/차단 <br>
	 * <b> "id" - 쪽지 유니크ID<br>
	 * "type" : "D":쪽지삭제, "B":수신차단<br>
	 */
	public void KP_6043(String mid, String m1, String m2, String uid, String id, String type) {
		String query = KP_xxxx(mid, m1, m2, "KP_6043");
		query += "&uid=" + uid;
		query += "&id=" + id;
		query += "&type=" + type;
		mStrQuery = query;
		send("");
	}

	/**
	 * 오류보고타입
	 * 
	 * @author isyoon
	 * 
	 */
	public static enum MEDIAERROR {

		MEDIAERROR(1);

		public static int WHAT_MEDIA_ERROR = -66666;
		public static int EXTRA_PLAYER_START_ERROR = -22222;
		public static int EXTRA_PLAYER_FILE_ERROR = -33333;
		public static int EXTRA_RECORD_START_ERROR = -44444;
		public static int EXTRA_RECORD_FILE_ERROR = -55555;
		public static int EXTRA_KYNNNN_DATA_ERROR = -88888;

		private final int index;

		MEDIAERROR(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}

		public static enum TYPE {

			MEDIAPLAYER(1), MEDIARECORDER(2), MEDIAUPLOAD(3);

			private final int index;

			TYPE(int index) {
				this.index = index;
			}

			public int index() {
				return index;
			}
		}

		public static enum LEVEL {

			E(1), I(2), W(3);

			private final int index;

			LEVEL(int index) {
				this.index = index;
			}

			public int index() {
				return index;
			}
		}

	}

	/**
	 * 재생/녹음/업로드-오류보고
	 */
	public void KP_9999(String mid, String m1, String m2, MEDIAERROR.TYPE type, MEDIAERROR.LEVEL level, String song_id, String record_id, String where, int what, int extra,
			String wname, String ename, KPItem info, KPItem list, String message) {
		String query = KP_xxxx(mid, m1, m2, "KP_9999");
		query += "&type=" + type;
		query += "&level=" + level;
		query += "&song_id=" + song_id;
		query += "&record_id=" + record_id;
		query += "&where=" + where;
		query += "&what=" + what;
		query += "&extra=" + extra;
		query += "&wname=" + wname;
		query += "&ename=" + ename;
		try {
			query += "&info=" + info;
			query += "&list=" + list;
			query += "&message=" + URLEncoder.encode(message, "UTF-8");
		} catch (Exception e) {

			e.printStackTrace();
		}
		mStrQuery = query;
		send("");
	}

	/**
	 * 전송취소
	 */
	public void cancel() {
		cancelRequests(context, true);
	}

	@Deprecated
	@Override
	protected void download() {

		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, getMethodName());
		super.download();
		KPItem list = getLists().get(0);
		if (list == null) {
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "List Data Error");
			return;
		}
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, "list - " + list.toString(2));
		// test
		// String tst = "http://resource.kymedia.kr/record/kpop/20130402/64/1304022G9NPQU64.m4a";
		// tst = android.util.Base64.encodeToString(SongUtil.makeEncryption("6390", tst).getBytes(), android.util.Base64.DEFAULT);
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "test_url:" + tst);
		// tst = SongUtil.makeDecryption("6390", new String(android.util.Base64.decode(tst.replace(" ", "+"), 0)));
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "test_url:" + tst);
		// 노출금지
		String url = list.getValue("down_url");
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "down_url:" + url);
		url = SongUtil.makeDecryption("6390", new String(android.util.Base64.decode(url.replace(" ", "+"), 0)));
		// 노출금지
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "down_url:" + url);
		long size = 0;
		long total = Long.parseLong(list.getValue("filesize"));
		if (!URLUtil.isNetworkUrl(url)) {
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "URL Parse Error");
			return;
		}
		try {
			if (mListener != null) {
				mListener.onKPnnnnProgress(size, total);
			}
			if (mOnProgressListener != null) {
				mOnProgressListener.onProgress(size, total);
			}
			get(url, p_opcode, getDownload().getPath());
		} catch (Exception e) {

			e.printStackTrace();
			onFailure(e);
		}
	}

	protected void upload() {
		if (_IKaraoke.DEBUG) Log2.e(__CLASSNAME__, getMethodName());
		if (mRequestParams.getMultipartEntity() != null) {
			if (mListener != null) {
				mListener.onKPnnnnProgress(0, mRequestParams.getMultipartEntity().getContentLength());
			}
			if (mOnProgressListener != null) {
				mOnProgressListener.onProgress(0, mRequestParams.getMultipartEntity().getContentLength());
			}
		}
	}
}
