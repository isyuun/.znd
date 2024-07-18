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
 * filename	:	RequestParams2.java
 * author	:	isyoon
 * <p>
 * <pre>
 * kr.kymedia.karaoke.http
 *    |_ RequestParams2.java
 * </pre>
 */

package is.yuun.com.loopj.android.http.api;

import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListMap;

import cz.msebera.android.httpclient.HttpEntity;
import kr.kymedia.karaoke.api.Log;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 3. 19.
 * @version 1.0
 * @see
 */
public class RequestParams3 extends RequestParams2 {
    final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s() ", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    public RequestParams3() {
        super();
    }

    public RequestParams3(final String params) {
        super();
        putParams(params);
    }

    public RequestParams3(final String params, File file) {
        super();
        putParams(params);
        this.file = file;
    }

    private File file = null;

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *          the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    private void putParam(String param) {
        int index = param.indexOf("=");
        if (index > 0 && index < param.length()) {
            String key = param.substring(0, index);
            String val = param.substring(index + 1);
            if (!TextUtil.isEmpty(key)) {
                put(key, val);
            }
        }
    }

    public void putParams(final String params) {
        // int index = -1;
        //
        // index = str.indexOf("?");
        //
        // if (index == -1) {
        // return null;
        // }
        //
        // String query = str.substring(index + 1);
        final String aParam[] = TextUtils.split(params, "\\&");

        if (!TextUtil.isEmpty(params) && aParam.length > 0) {
            if (aParam.length > 1) {
                // param이둘이상
                for (int i = 0; i < aParam.length; i++) {
                    String param = aParam[i];
                    // _Log.e(__CLASSNAME__, param[i]);
                    putParam(param);
                }
            } else {
                String param = params;
                // param이하나만
                // _Log.e(__CLASSNAME__, param);
                putParam(param);
            }
        }
    }

    public ConcurrentSkipListMap<String, String> getUrlParams() {
        return urlParams;
    }

    @Override
    public HttpEntity getEntity() {
        Log.e(__CLASSNAME__, getMethodName());
        Log.e(__CLASSNAME__, "file:" + file);
        Log.e(__CLASSNAME__, "fileParams:" + fileParams);

        HttpEntity entity = super.getEntity();
        return entity;
    }

    /**
     * @see is.yuun.com.loopj.android.http.api.RequestParams2#put(java.lang.String, java.lang.String, java.lang.String, is.yuun.com.loopj.android.http.api.ProgressListener)
     */
    @Override
    public void put(String key, String fileName, String contentType, ProgressListener listener) {
        Log.e(__CLASSNAME__, getMethodName());
        Log.e(__CLASSNAME__, "key:" + key);
        Log.e(__CLASSNAME__, "fileName:" + fileName);
        Log.e(__CLASSNAME__, "contentType:" + contentType);
        Log.e(__CLASSNAME__, "listener:" + listener);

        super.put(key, fileName, contentType, listener);
    }
}
