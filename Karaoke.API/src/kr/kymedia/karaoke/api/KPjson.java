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
 * filename	:	KPnnnnJSONAdapter.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.data
 *    |_ KPnnnnJSONAdapter.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpResponseException;
import is.yuun.com.loopj.android.http.api.AsyncHttpAgent;
import is.yuun.com.loopj.android.http.api.JsonHttpResponseListener;
import is.yuun.com.loopj.android.http.api.RequestParams3;
import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.util.TextUtil;

//import kr.kymedia.karaoke.http.BinaryHttpResponseListener;

/**
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 2. 29.
 * @version 1.0
 * @see KPjson
 */
public class KPjson extends AsyncHttpAgent implements _IKaraoke, JsonHttpResponseListener {
	protected String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s() ", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	private KPItem mInfo = null;
	private ArrayList<KPItem> mLists = null;
	protected RequestParams3 mRequestParams = null;

	/**
	 * 오퍼레이션코드 인테페이스명세서를 구성하고 있는 인터페이스 ID
	 */
	public String p_opcode = "";
	String mStrQuery = "";
	String r_code = "";
	String r_message = "";

	public KPjson() {
		super();
		mLists = new ArrayList<KPItem>();
	}

	/**
	 * 
	 * <pre>
	 * 핸들러는나발~
	 * </pre>
	 * 
	 * @param handler
	 */
	public KPjson(Handler handler) {
		super();
		mLists = new ArrayList<KPItem>();
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		mInfo = null;
		if (mLists != null) {
			mLists.clear();
		}
		mLists = null;
		if (mRequestParams != null) {
			mRequestParams.release();
		}
		mRequestParams = null;
	}

	/**
	 * @return the mInfo
	 */
	public KPItem getInfo() {
		return mInfo;
	}

	/**
	 * @param info
	 *          the mInfo to set
	 */
	public void setInfo(KPItem info) {
		this.mInfo = info;
	}

	/**
	 * @return the mList
	 */
	public ArrayList<KPItem> getLists() {
		return mLists;
	}

	public int getListCount() {
		try {
			return mLists.size();
		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}
	}

	public ArrayList<KPItem> setLists(ArrayList<KPItem> lists) {
		mLists = lists;
		return mLists;
	}

	public KPItem getList(int index) {

		try {
			if (mLists != null && index < mLists.size()) {
				return mLists.get(index);
			}
		} catch (Exception e) {

			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		}
		return null;
	}

	public void clear() {
		mInfo = null;
		if (mLists != null) {
			mLists.clear();
		}
	}

	public void release() {
		mInfo = null;
		if (mLists != null) {
			mLists.clear();
		}
		mLists = null;
	}

	public String getListString(int index, String name) {
		KPItem json = null;
		try {
			json = mLists.get(index);
		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return null;
		}
		return json.getValue(name);
	}

	public void close() {

	}

	public void open() {

	}

	protected void excute(String str) {

	}

	@Override
	protected void download() {
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, getMethodName());

		super.download();
	}

	private void checkRequestParams() throws Exception {
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[PARAM]\t" + getMethodName());

		if (_IKaraoke.DEBUG) {
			for (ConcurrentHashMap.Entry<String, String> entry : mRequestParams.getUrlParams().entrySet()) {
				try {
					if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, "[PARAM]\t" + entry.getKey() + "=" + entry.getValue());
				} catch (Exception e) {

					if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, getMethodName() + Log.getStackTraceString(e));
					throw e;
				}
			}
		}

	}

	/**
	 * 다운로드 (POST전송) HTTP GET/POST와 헷갈리지 말것
	 */
	protected void get(String query, String opcode, String path) throws Exception {

		__CLASSNAME__ = opcode;

		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, getMethodName());
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[OPCODE]\t" + opcode);
		// 노출금지
		// if (_IKaraoke.DEBUG)Log.e(__CLASSNAME__, "[QUERY]\t" + query);

		this.mStrQuery = query;
		this.p_opcode = opcode;

		int index = -1;

		index = query.indexOf("?");

		String url = "";
		String params = "";
		if (index == -1) {
			url = query;
			params = "";
		} else {
			url = query.substring(0, index);
			params = query.substring(index + 1);
		}

		// mRequestParams = RequestParams3.putUrlParams(param);
		// mRequestParams = new RequestParams3(params);
		if (mRequestParams != null) {
			// mRequestParams.clear();
			mRequestParams.putParams(params);
		} else {
			mRequestParams = new RequestParams3(params);
		}

		if (mRequestParams == null) {
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "RequestParams Error");
			return;
		}

		checkRequestParams();

		File file = new File(path);
		file.mkdirs();
		if (file != null) {
			file.mkdirs();
			if (file.exists() && !file.delete()) {
				IOException e = new IOException("File can't Delete");
				throw e;
			}

			if (!file.createNewFile() || !file.canWrite()) {
				IOException e = new IOException("File can't Write");
				throw e;
			}

		}


		try {
			isCanceled = false;
			if (mRequestParams != null) {
				if (file.exists()) {
					// 다운로드시
					mAsyncHttpClient.post(url, mRequestParams, mBinaryHttpResponseHandler);
				} else {
					// JSON데이터
					mAsyncHttpClient.post(url, mRequestParams, mJsonHttpResponseHandler);
				}
			}

		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			throw e;
		}

	}

	/**
	 * 조회/업로드(POST전송) HTTP GET/POST와 헷갈리지 말것
	 */
	protected void put(String query, String opcode, String path) throws Exception {

		__CLASSNAME__ = opcode;

		// 노출금지
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[PUT:" + opcode + "]" + getMethodName());
		if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[PUT][QUERY]\t" + query);
		if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[PUT][PATH]\t" + path);

		this.mStrQuery = query;
		this.p_opcode = opcode;

		int index = -1;

		index = query.indexOf("?");

		if (index == -1) {
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "URL Param Error");
			return;
		}

		String url = query.substring(0, index);
		String params = query.substring(index + 1);

		// mRequestParams = RequestParams3.putUrlParams(param);
		// mRequestParams = new RequestParams3(params);
		if (mRequestParams != null) {
			// mRequestParams.clear();
			mRequestParams.putParams(params);
		} else {
			mRequestParams = new RequestParams3(params);
		}

		if (mRequestParams == null) {
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, "RequestParams Error");
			return;
		}

		checkRequestParams();

		File file = new File(path);
		if (file != null && file.exists()) {
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, path);
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, file.toString());
			mRequestParams.put("uploadedfile", file.getPath(), null, mProgressListener);
		}


		try {
			isCanceled = false;
			if (mRequestParams != null) {
				mAsyncHttpClient.post(url, mRequestParams, mJsonHttpResponseHandler);
			}
		} catch (Exception e) {

			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			if (_IKaraoke.DEBUG) Log.wtf(__CLASSNAME__, file.toString());
			sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_UNKOWNDATAERROR, Log.getStackTraceString(e));
			throw e;
		}

	}

	protected void sendMessage(int what, String r_code, String r_message) {

	}

	private void putInfoErrorStatus(int statusCode, String statusMessage) {
		if (mInfo == null) {
			mInfo = new KPItem();
		}
		getInfo().putValue("HttpStatusCode", Integer.toString(statusCode));
		getInfo().putValue("HttpStatusMessage", statusMessage);
	}

	@Override
	public void onStart() {
		if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[" + p_opcode + "]" + "\n" + mInfo);
		if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, getMethodName() + this + "[START]");

		mInfo = null;
		KPjson.this.sendMessage(STATE_DATA_QUERY_START, "00000", "start");
	}

	@Override
	public void onFinish() {
		if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, getMethodName() + this + "[START]");
		if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[" + p_opcode + "]" + "\n" + mInfo);

		if (TextUtil.isEmpty(r_code) && mInfo == null) {
			KPjson.this.sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_JSONDATAPARSINGERORR, "onFinish() - Info Data Unknown Error");
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "]" + "[" + r_code + "]" + mInfo);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		try {
			if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, getMethodName() + this + "[START]");
			if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[" + p_opcode + "]" + "\n" + mInfo);

			super.onSuccess(statusCode, headers, response);
			// if (_IKaraoke.DEBUG)Log.d(__CLASSNAME__, obj.toString(2));

			JSONObject record = response.getJSONObject("record");
			// if (_IKaraoke.DEBUG)Log.i(__CLASSNAME__, "record - " + "\"" + record.toString(2) + "\"");

			JSONArray infos = record.getJSONArray("info");
			JSONObject info = infos.getJSONObject(0);

			try {
				mInfo = new KPItem(info);
			} catch (Exception e) {

				// e.printStackTrace();
				KPjson.this.sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_JSONDATAPARSINGERORR, "[info tag parse error]\n" + Log.getStackTraceString(e));
				if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "][NG]" + "info - error");
				if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, Log.getStackTraceString(e));
				if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, response != null ? response.toString(2) : "" + response);
				return;
			}

			if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "info - " + "\"" + mInfo.toString(2) + "\"");

			r_code = mInfo.getValue("result_code") == null ? "null" : mInfo.getValue("result_code");
			r_message = mInfo.getValue("result_message") == null ? "null" : mInfo.getValue("result_message");

			try {
				if (record.has("list")) {
					JSONArray lists = record.getJSONArray("list");

					// mLists = record.getJSONArray("list");

					if (lists != null) {
						if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[list:count]" + lists.length());
					}

					if (mLists == null) {
						mLists = new ArrayList<KPItem>();
					}

					for (int i = 0; i < lists.length(); i++) {
						JSONObject list = lists.getJSONObject(i);
						KPItem item = new KPItem(list);

						mLists.add(item);
						if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, "list - " + "\"" + item.toString(2) + "\"");

					}
				}
			} catch (Exception e) {

				// e.printStackTrace();
				KPjson.this.sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_JSONDATAPARSINGERORR, "[list tag parse error]\n" + Log.getStackTraceString(e));
				if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "][NG]" + "list - error");
				if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, Log.getStackTraceString(e));
				if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, response != null ? response.toString(2) : "" + response);
				return;
			}

			// if (_IKaraoke.DEBUG)Log.w(__CLASSNAME__, "info - " + "\"" + mInfo.toString(2) + "\"");

			if (r_code != null && r_code.equals("00000")) {
				if (getDownload() != null) {
					download();
				} else {
					KPjson.this.sendMessage(STATE_DATA_QUERY_SUCCESS, r_code, r_message);
				}
			} else {
				KPjson.this.sendMessage(STATE_DATA_QUERY_ERROR, r_code, r_message);
			}

			if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, getMethodName() + this + "[END]");
			if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, "[" + p_opcode + "]" + "\n" + mInfo);

			new Runnable() {
				public void run() {
					onFinish();
				}
			}.run();

		} catch (Exception e) {
			KPjson.this.sendMessage(STATE_DATA_QUERY_ERROR, ERROR_CODE_JSONDATAPARSINGERORR, "[unknown parse error]\n" + Log.getStackTraceString(e));
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "]" + getMethodName() + "unknown - error");
			if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, Log.getStackTraceString(e));
			if (_IKaraoke.DEBUG) Log.i(__CLASSNAME__, "" + response);
		}
	}

	public void onFailure(int statusCode, Throwable throwable) {
		if (statusCode > 0) {
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, getMethodName() + " : " + statusCode + " - " + throwable);
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, Log.getStackTraceString(throwable));
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "]");
			if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "info - " + (mInfo != null ? mInfo.toString(2) : mInfo));
			putInfoErrorStatus(statusCode, Log.getStackTraceString(throwable));
			this.r_code = ERROR_CODE_HTTPRESPONSEEXCEPTION;
			sendMessage(STATE_DATA_QUERY_FAIL, r_code, Log.getStackTraceString(throwable));
		} else {
			onFailure(throwable);
		}
	}

	public void onFailure(Throwable throwable) {
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, getMethodName() + throwable);
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, Log.getStackTraceString(throwable));
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "[" + p_opcode + "]");
		if (_IKaraoke.DEBUG) Log.e(__CLASSNAME__, "info - " + (mInfo != null ? mInfo.toString(2) : mInfo));

		// Log.e(__CLASSNAME__, Log.getStackTraceString(e));

		this.r_code = "";
		String r_code = ERROR_CODE_UNKOWNDATAERROR;
		if (throwable instanceof IOException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_IOEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_IOEXCEPTION;
		} else if (throwable instanceof UnknownHostException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_UNKNOWNHOSTEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_UNKNOWNHOSTEXCEPTION;
		} else if (throwable instanceof UnknownServiceException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_UNKNOWNSERVICEEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_UNKNOWNSERVICEEXCEPTION;
		} else if (throwable instanceof SocketTimeoutException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_TIMEOUTEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_TIMEOUTEXCEPTION;
		} else if (throwable instanceof SocketException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_SOCKETEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_SOCKETEXCEPTION;
		} else if (throwable instanceof HttpResponseException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_HTTPRESPONSEEXCEPTION), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_HTTPRESPONSEEXCEPTION;
		} else if (throwable instanceof JSONException) {
			putInfoErrorStatus(Integer.parseInt(ERROR_CODE_JSONDATAPARSINGERORR), Log.getStackTraceString(throwable));
			r_code = ERROR_CODE_JSONDATAPARSINGERORR;
			// } else if (e instanceof FileNotFoundException) {
			// putInfoErrorStatus(((FileNotFoundException) e).hashCode(), ((FileNotFoundException) e).getMessage());
			// r_code = Integer.toString(e.hashCode());
			// } else if (e instanceof IOException) {
			// putInfoErrorStatus(((IOException) e).hashCode(), ((IOException) e).getMessage());
			// r_code = Integer.toString(e.hashCode());
			// } else {
			// putInfoErrorStatus(e.hashCode(), Log.getStackTraceString(e));
			// r_code = Integer.toString(e.hashCode());
		}
		this.r_code = r_code;
		// sendMessage(STATE_DATA_QUERY_FAIL, Integer.toString(e.hashCode()), Log.getStackTraceString(e));
		sendMessage(STATE_DATA_QUERY_FAIL, r_code, Log.getStackTraceString(throwable));
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

		super.onFailure(statusCode, headers, throwable, errorResponse);
		onFailure(statusCode, throwable);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

		super.onFailure(statusCode, headers, throwable, errorResponse);
		onFailure(statusCode, throwable);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

		super.onFailure(statusCode, headers, responseString, throwable);
		onFailure(statusCode, throwable);
	}

	// 다운로드사용안하므로
	@Deprecated
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
		if (_IKaraoke.DEBUG) Log.w(__CLASSNAME__, getMethodName() + statusCode + "," + binaryData);

		super.onSuccess(statusCode, headers, binaryData);
		sendMessage(STATE_DATA_QUERY_SUCCESS, r_code, r_message);
	}

	// 다운로드사용안하므로
	@Deprecated
	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

		super.onFailure(statusCode, headers, binaryData, error);
		onFailure(statusCode, error);

	}

}
