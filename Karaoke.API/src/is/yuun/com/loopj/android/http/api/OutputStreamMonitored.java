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
 * project	:	Karaoke.KPOP.LIB
 * filename	:	OutputStreamMonitored.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.http
 *    |_ OutputStreamMonitored.java
 * </pre>
 * 
 */

package is.yuun.com.loopj.android.http.api;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.AsyncTask;

/**
 *
 * TODO<br>
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 4. 4.
 * @version 1.0
 * @see
 */
public class OutputStreamMonitored extends FilterOutputStream {
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
	private OutputStream m_stream = null;

	public void reset() {
		total = 0;
		size = 0;
		m_stream = null;
		isProgress = true;
	}

	public OutputStreamMonitored(OutputStream out) {
		super(out);
		reset();
	}

	ProgressListener mProgressListener;

	public OutputStreamMonitored(OutputStream out, long total) {
		this(out);
		this.total = total;
		m_stream = out;
		setProgress(size, total);
	}

	public OutputStreamMonitored(OutputStream out, long total, ProgressListener listener) {
		this(out);
		this.total = total;
		m_stream = out;
		mProgressListener = listener;
		setProgress(size, total);
	}

	/**
	 * 절대로 UI에 접근하지 않는다.<br>
	 * 쓰지않는게좋것다...왜냐?...알아봐<br>
	 * 
	 * @author isyoon
	 * 
	 *         [출처] 쉬운 안드로이드 쓰레드 관리 Class - AsyncTask|작성자 dythmall
	 */
	@Deprecated
	private class setProgress extends AsyncTask<Long, Integer, Integer> {

		@Override
		protected Integer doInBackground(Long... params) {

			if (params.length == 2) {
				setProgress(params[0], params[1]);
			}
			return null;
		}

	}

	private void setProgress(long size, long total) {
		// Log.e(__CLASSNAME__, getMethodName() + "size:" + size + ", total:" + total);
		// int percent = (int) Math.round(100.0 * (double) size / (double) total);
		if (mProgressListener != null) {
			// mProgressListener.onPercent(percent);
			mProgressListener.onProgress(size, total);
		}
		if (size < total) {
			isProgress = true;
		} else {
			isProgress = false;
		}
	}

	@Deprecated
	private void setProgress() {
		// Log.e(__CLASSNAME__, getMethodName() + "size:" + size + ", total:" + total);
		// int percent = (int) Math.round(100.0 * (double) size / (double) total);
		if (mProgressListener != null) {
			// mProgressListener.onPercent(percent);
			mProgressListener.onProgress(size, total);
		}
		if (size < total) {
			isProgress = true;
		} else {
			isProgress = false;
		}
	}

	// private int PercentUploaded() {
	// return (int) Math.round(100.0 * (double) m_bytes_transferred / (double) m_length);
	// }

	public void write(byte[] b, int off, int len) throws IOException {
		m_stream.write(b, off, len);
		size += len;
		setProgress(size, total);
	}

	public void write(int b) throws IOException {
		m_stream.write(b);
		size += 1;
		setProgress(size, total);
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		if (m_stream != null) {
			m_stream.close();
		}
		m_stream = null;
		mProgressListener = null;
	}

}
