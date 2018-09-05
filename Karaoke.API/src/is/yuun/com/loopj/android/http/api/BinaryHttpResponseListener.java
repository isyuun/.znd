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
 * filename	:	BinaryHttpResponseListener.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.http
 *    |_ BinaryHttpResponseListener.java
 * </pre>
 * 
 */

package is.yuun.com.loopj.android.http.api;

import org.apache.http.Header;

/**
 *
 * TODO<br>
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 4. 5.
 * @version 1.0
 * @see BinaryHttpResponseListener.java
 */
public interface BinaryHttpResponseListener extends AsyncHttpResponseListener {

	/**
	 * Fired when a request returns successfully, override to handle in your own code
	 * 
	 * @param binaryData
	 *          the body of the HTTP response from the server
	 */
	// public void onSuccess(byte[] binaryData);

	/**
	 * Fired when a request returns successfully, override to handle in your own code
	 * 
	 * @param statusCode
	 *          the status code of the response
	 * @param binaryData
	 *          the body of the HTTP response from the server
	 */
	// public void onSuccess(int statusCode, byte[] binaryData);
	public void onSuccess(int statusCode, Header[] headers, byte[] binaryData);

	/**
	 * Fired when a request fails to complete, override to handle in your own code
	 * 
	 * @param e
	 *          the underlying cause of the failure
	 * @param binaryData
	 *          the response body, if any
	 */
	// public void onFailure(Throwable e, byte[] binaryData);
	public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error);

}
