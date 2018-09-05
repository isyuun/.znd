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
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke
 * filename	:	JsonHttpResponseHandler2.java
 * author	:	isyoon
 *
 * <pre>
 * isyoon.com.loopj.android.http
 *    |_ JsonHttpResponseHandler2.java
 * </pre>
 * 
 */

package is.yuun.com.loopj.android.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 4. 15.
 * @version 1.0
 */
public class JsonHttpResponseHandler2 extends JsonHttpResponseHandler {

	@Override
	public void onSuccess(int statusCode, JSONObject response) {
		super.onSuccess(statusCode, response);
		onSuccess(statusCode, null, response);
	}

	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

	}

	@Override
	public void onSuccess(int statusCode, JSONArray response) {
		super.onSuccess(statusCode, response);
		onSuccess(statusCode, null, response);
	}

	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

	}

	@Override
	public void onFailure(Throwable e, JSONObject errorResponse) {
		super.onFailure(e, errorResponse);
		onFailure(0, null, e, errorResponse);
	}

	public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			JSONObject errorResponse) {

	}

	@Override
	public void onFailure(Throwable e, JSONArray errorResponse) {
		super.onFailure(e, errorResponse);
		onFailure(0, null, e, errorResponse);
	}

	public void onFailure(int statusCode, Header[] headers, Throwable throwable,
			JSONArray errorResponse) {

	}

	public void onFailure(int statusCode, Header[] headers, String responseString,
			Throwable throwable) {

	}

	public void onSuccess(int statusCode, Header[] headers, String responseString) {

	}

	public void onProgress(int bytesWritten, int totalSize) {

	}

	public void onRetry(int retryNo) {

	}

	public void onCancel() {

	}
}
