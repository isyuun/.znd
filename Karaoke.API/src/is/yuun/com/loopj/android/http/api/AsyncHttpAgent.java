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
 * <p>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p>
 * project	:	Karaoke.KPOP.LIB.LGE.SMARTPHONE
 * filename	:	AsyncHttpAgent.java
 * author	:	isyoon
 * <p>
 * <pre>
 * com.loopj.android.http
 *    |_ AsyncHttpAgent.java
 * </pre>
 */

package is.yuun.com.loopj.android.http.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import is.yuun.com.loopj.android.http.BinaryHttpResponseHandler2;
import is.yuun.com.loopj.android.http.JsonHttpResponseHandler2;

/**
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 4. 5.
 * @version 1.0
 * @see
 */
public class AsyncHttpAgent implements JsonHttpResponseListener, BinaryHttpResponseListener, ProgressListener {
    final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s() ", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    /**
     *
     */
    public AsyncHttpAgent() {
    }

    protected AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    protected boolean isCanceled = false;

    final protected void cancelRequests(Context context, boolean mayInterruptIfRunning) {
        isCanceled = true;
        if (mAsyncHttpClient != null) {
            mAsyncHttpClient.cancelRequests(context, mayInterruptIfRunning);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

    }

    @Override
    public void onFailure(Throwable e) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {

    }

    protected ProgressListener mProgressListener = null;

    protected void setProgressListener(ProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public void onProgress(long size, long total) {
        if (mProgressListener != null) {
            mProgressListener.onProgress(size, total);
        }
        Log.d(__CLASSNAME__, getMethodName() + "(" + size + "/" + total + ")");
    }

    /**
     */
    protected _JsonHttpResponseHandler mJsonHttpResponseHandler = new _JsonHttpResponseHandler();

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {

    }

    @Override
    public void sendStartMessage() {

    }

    @Override
    public void sendFinishMessage() {

    }

    @Override
    public void sendProgressMessage(long bytesWritten, long bytesTotal) {

    }

    @Override
    public void sendCancelMessage() {

    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }

    @Override
    public void sendRetryMessage(int retryNo) {

    }

    @Override
    public URI getRequestURI() {
        return null;
    }

    @Override
    public void setRequestURI(URI requestURI) {

    }

    @Override
    public Header[] getRequestHeaders() {
        return new Header[0];
    }

    @Override
    public void setRequestHeaders(Header[] requestHeaders) {

    }

    @Override
    public boolean getUseSynchronousMode() {
        return false;
    }

    @Override
    public void setUseSynchronousMode(boolean useSynchronousMode) {

    }

    @Override
    public boolean getUsePoolThread() {
        return false;
    }

    @Override
    public void setUsePoolThread(boolean usePoolThread) {

    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public void setTag(Object TAG) {

    }

    protected class _JsonHttpResponseHandler extends JsonHttpResponseHandler2 {

        //@Override
        //public void onFailure(Throwable error, String content) {
        //	super.onFailure(error, content);
        //	AsyncHttpAgent.this.onFailure(error);
        //}

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            AsyncHttpAgent.this.onSuccess(statusCode, headers, response);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            AsyncHttpAgent.this.onSuccess(statusCode, headers, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            AsyncHttpAgent.this.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            AsyncHttpAgent.this.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            AsyncHttpAgent.this.onFailure(statusCode, headers, responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            AsyncHttpAgent.this.onSuccess(statusCode, headers, responseString);
        }

        //@Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
            AsyncHttpAgent.this.onProgress(bytesWritten, totalSize);
        }

        @Override
        public void onStart() {
            super.onStart();
            AsyncHttpAgent.this.onStart();
        }

        /**
         * 전달순서가젖같다. AsyncHttpAgent.onFinish()호출불가
         */
        @Deprecated
        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onRetry(int retryNo) {

            super.onRetry(retryNo);
        }

        @Override
        public void onCancel() {
            super.onCancel();
        }

    }

    ;

    @Override
    protected void finalize() throws Throwable {
        mAsyncHttpClient = null;
        mJsonHttpResponseHandler = null;
        mBinaryHttpResponseHandler = null;
        super.finalize();
    }

    // /**
    // * Upload File's handle
    // */
    // private File upload = null;
    // protected void upload() {
    // }
    //
    // /**
    // * @return the file
    // */
    // public File getUpload() {
    // return upload;
    // }
    //
    // /**
    // * @param file the file to set
    // */
    // public void setUpload(File file) {
    // this.upload = file;
    // }

    /**
     * Download File's handle
     */
    private File download = null;

    protected void download() {
    }

    /**
     * @return the file
     */
    public File getDownload() {
        return download;
    }

    /**
     * @param file
     *          the file to set
     */
    public void setDownload(File file) {
        this.download = file;
    }

    final static String[] allowedContentTypes = new String[]{"*/*", "image/jpeg", "image/png", "audio/mpeg", "text/plain",};

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {


    }

    protected _BinaryHttpResponseHandler mBinaryHttpResponseHandler = new _BinaryHttpResponseHandler(allowedContentTypes);

    public class _BinaryHttpResponseHandler extends BinaryHttpResponseHandler2 {

        String[] mAllowedContentTypes = new String[]{"*/*", "image/jpeg", "image/png", "audio/mpeg", "text/plain",};

        /**
         * Creates a new BinaryHttpResponseHandler, and overrides the default allowed content types with
         * passed String array (hopefully) of content types.
         */
        public _BinaryHttpResponseHandler(String[] allowedContentTypes) {
            super(allowedContentTypes);
            mAllowedContentTypes = allowedContentTypes;
            reset();
        }

        long size = 0;
        long total = 0;

        private void reset() {
            size = 0;
            total = 0;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
            reset();
            AsyncHttpAgent.this.onSuccess(statusCode, headers, binaryData);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
            reset();
            AsyncHttpAgent.this.onFailure(statusCode, headers, binaryData, error);
        }

        //@Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
            AsyncHttpAgent.this.onProgress(bytesWritten, totalSize);
        }

        @Override
        public void onStart() {
            super.onStart();
            AsyncHttpAgent.this.onStart();
        }

        /**
         * 전달순서가젖같다. AsyncHttpAgent.onFinish()호출불가
         */
        @Deprecated
        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }

        @Override
        public void onCancel() {
            super.onCancel();
        }

    }

}
