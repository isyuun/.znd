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
 * project	:	Karaoke.KPOP.LIB.LGE.SMARTPHONE
 * filename	:	SimpleBufferedHttpEntityMonitored.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.http
 *    |_ SimpleBufferedHttpEntityMonitored.java
 * </pre>
 * 
 */

package is.yuun.com.loopj.android.http.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import android.os.AsyncTask;
import android.util.Log;

/**
 *
 * TODO<br>
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 4. 5.
 * @version 1.0
 * @see
 */
@Deprecated
public class SimpleHttpEntityFileMonitored {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s() ", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	private long size = 0;
	private long total = 0;
	boolean isProgress = true;

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	private void reset() {
		size = 0;
		total = 0;
		isProgress = true;
	}

	private HttpResponse response;
	private File file;
	private byte[] responseBody = null;
	private ProgressListener mProgressListener = null;
	private InputStreamMonitored mInputStream = null;
	private OutputStreamMonitored mOutputStream = null;

	String[] mAllowedContentTypes = new String[] {
			"*/*",
			"image/jpeg",
			"image/png",
			"audio/mpeg",
			"text/plain",
	};

	public SimpleHttpEntityFileMonitored(HttpResponse response, File file, String[] allowedContentTypes) throws IOException {
		reset();
		this.response = response;
		this.file = file;
		this.mAllowedContentTypes = allowedContentTypes;
	}

	public SimpleHttpEntityFileMonitored(HttpResponse response, File file, String[] allowedContentTypes, ProgressListener listener) throws IOException {
		this(response, file, allowedContentTypes);
		this.mProgressListener = listener;
	}

	/**
	 * 절대로 UI에 접근하지 않는다.
	 * 
	 * @author isyoon
	 * 
	 *         [출처] 쉬운 안드로이드 쓰레드 관리 Class - AsyncTask|작성자 dythmall
	 */
	private class setProgress extends AsyncTask<Long, Integer, Integer> {

		@Override
		protected Integer doInBackground(Long... params) {

			setProgress(params);
			return null;
		}

	}

	private void setProgress(Long... params) {
		// Log.e(__CLASSNAME__, getMethodName() + "size:" + size + ", total:" + total);
		// int percent = (int) Math.round(100.0 * (double) size / (double) total);
		if (mProgressListener != null && params.length == 2) {
			// mProgressListener.onPercent(percent);
			mProgressListener.onProgress(params[0], params[2]);
		}
		if (size < total) {
			isProgress = true;
		} else {
			isProgress = false;
		}
	}

	/**
	 * 비동기처리(AsyncTask)안하는게정신건강에좋을꺼다...왜?
	 */
	private void setProgress() {
		// int percent = (int) Math.round(100.0 * (double) size / (double) total);
		if (mProgressListener != null) {
			// Log.e(__CLASSNAME__, getMethodName() + "size:" + size + ", total:" + total);
			// mProgressListener.onPercent(percent);
			mProgressListener.onProgress(size, total);
		}
	}

	boolean cancel = false;

	void down() throws Exception {
		Log.e(__CLASSNAME__, getMethodName());
		cancel = false;
		total = response.getEntity().getContentLength();
		Log.e(__CLASSNAME__, "StatusCode : " + response.getStatusLine().getStatusCode());
		Log.e(__CLASSNAME__, "Content-Type : " + response.getHeaders("Content-Type")[0].getValue());
		Log.e(__CLASSNAME__, "Content-Length : " + response.getEntity().getContentLength());
		Log.e(__CLASSNAME__, "size:" + size + ", total:" + total);
		StatusLine status = response.getStatusLine();
		Header[] contentTypeHeaders = response.getHeaders("Content-Type");
		responseBody = null;
		if (contentTypeHeaders.length != 1) {
			// malformed/ambiguous HTTP Header, ABORT!
			Exception e = new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!");
			throw e;
		}
		Header contentTypeHeader = contentTypeHeaders[0];
		boolean foundAllowedContentType = false;
		for (String anAllowedContentType : mAllowedContentTypes) {
			if (anAllowedContentType.equals(contentTypeHeader.getValue())) {
				foundAllowedContentType = true;
			}
		}
		if (!foundAllowedContentType) {
			// Content-Type not in allowed list, ABORT!
			Exception e = new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!");
			throw e;
		}
		HttpEntity entity = response.getEntity();
		// 인터넷으로 부터 데이터를 읽어들이기 위한 입력스트림을 얻어온다.
		// InputStream inputStream = entity.getContent();
		mInputStream = new InputStreamMonitored(entity.getContent(), total);
		// 파일을 오픈한다.
		// FileOutputStream outputStream = new FileOutputStream(file);
		mOutputStream = new OutputStreamMonitored(new FileOutputStream(file), total);
		// 버퍼를 생성한다.
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // 임시로 사용할 버퍼의 크기 지정

		// 입력버퍼로 부터 데이터를 읽어서 내용을 파일에 쓴다.
		while (!cancel && (bufferLength = mInputStream.read(buffer)) > 0) {
			// 버퍼에 읽어들인 데이터를 파일에 쓴다.
			mOutputStream.write(buffer, 0, bufferLength);
			// 다운로드 받은 바이트수를 계산한다.
			size += bufferLength;
			// progressDialog에 다운로드 받은 바이트수를 표시해 준다. <-따로 progressDialog스레드를 작성해둘것
			setProgress();
			// Log.e(__CLASSNAME__, getMethodName() + "size:" + size + ", total:" + total);
		}
		// 작업이 끝나면 파일을 close하여 저장한다.
		mOutputStream.flush();
		mOutputStream.close();
		mInputStream.close();

		if (status.getStatusCode() >= 300) {
			Exception e = new HttpResponseException(status.getStatusCode(), status.getReasonPhrase());
			throw e;
		}
	}

	public int getStatusCode() {
		if (response != null && response.getStatusLine() != null) {
			return response.getStatusLine().getStatusCode();
		} else {
			return 0;
		}
	}

	public byte[] getBytes() {
		return responseBody;
	}

	public String getReasonPhrase() {

		if (response != null && response.getStatusLine() != null) {
			return response.getStatusLine().getReasonPhrase();
		} else {
			return null;
		}
	}

	public Header[] getAllHeaders() {
		if (response != null && response.getStatusLine() != null) {
			return response.getAllHeaders();
		} else {
			return null;
		}
	}
}
