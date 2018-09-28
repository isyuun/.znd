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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p>
 * project	:	Karaoke.TV
 * filename	:	Download2.java
 * author	:	isyoon
 * <p>
 * <pre>
 * kr.kymedia.kykaraoke.tv.api
 *    |_ Download2.java
 * </pre>
 */

package kr.keumyoung.karaoke.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import kr.keumyoung.karaoke.api._Download.onDownloadListener;
import kr.keumyoung.karaoke.play.BuildConfig;
import kr.kymedia.karaoke.api.LyricsUtil;
import kr.kymedia.karaoke.play.impl.ISongPlay.ERROR;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 9. 25.
 */
class Download2 extends Download implements _Const {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }

    String mMp3;

    public void setMp3(String mp3) {
        mMp3 = mp3;
    }

    String mLyc, mFileName;
    int mType;
    Handler handler;
    String newPath;

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String path) {
        this.newPath = path;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    public void setLyc(String lyc) {
        mLyc = lyc;
    }

    @Override
    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public Download2(Handler h) {
        super(h);
        handler = h;
    }

    @Override
    public void sendMessage(int state) {
        Bundle b = new Bundle();
        b.putInt("state", state);

        Message msg = handler.obtainMessage();
        msg.setData(b);
        handler.sendMessage(msg);
    }

    protected void removeCallbacks(Runnable r) {
        if (handler != null) {
            handler.removeCallbacks(r);
        }
    }

    protected void post(Runnable r) {
        removeCallbacks(r);
        if (handler != null) {
            handler.post(r);
        }
    }

    protected void postDelayed(Runnable r, long delayMillis) {
        removeCallbacks(r);
        if (handler != null) {
            handler.postDelayed(r, delayMillis);
        }
    }

    private onDownloadListener listener;

    public onDownloadListener getListener() {
        return listener;
    }

    public void setListener(onDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {

        final String url = mLyc;
        final String path = newPath + File.separator + mFileName;
        final int type = mType;

        if (BuildConfig.DEBUG)
            Log.w(__CLASSNAME__, getMethodName() + "[ST]" + type + ":" + url + "->" + path);

        // super.run();
        try {

            if (TextUtil.isNetworkUrl(mMp3)) {
                if (BuildConfig.DEBUG)
                    Log.i(__CLASSNAME__, "lyric.down() " + "[ST]" + type + ":" + url + "->" + path);
                LyricsUtil.down(url, path);
                if (BuildConfig.DEBUG)
                    Log.i(__CLASSNAME__, "lyric.down() " + "[ED]" + type + ":" + url + "->" + path);
            } else {
                down();
            }

            if (interrupted()) {
                File file = new File(path);
                if (file != null && file.exists()) {
                    file.delete();
                }
            } else {
                switch (type) {
                    case REQUEST_FILE_ARTIST_IMAGE:
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "_COMPLETE_ARTIST_IMAGE");
                        sendMessage(COMPLETE_DOWN_ARTIST_IMAGE);
                        break;
                    case REQUEST_FILE_SONG:
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "_COMPLETE_SONG");
                        sendMessage(COMPLETE_DOWN_SONG);
                        break;
                    case REQUEST_FILE_LISTEN:
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "_COMPLETE_LISTEN");
                        sendMessage(COMPLETE_DOWN_LISTEN);
                        break;
                    case REQUEST_FILE_LISTEN_OTHER:
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "_COMPLETE_LISTEN_OTHER_DOWN");
                        sendMessage(COMPLETE_DOWN_LISTEN_OTHER);
                        break;
                }
            }

        } catch (final Exception e) {
            if (BuildConfig.DEBUG)
                Log.w(__CLASSNAME__ + TAG_ERR, "[NG]" + getMethodName() + type + ":" + url + "->" + path);

            e.printStackTrace();
            post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onDownError(ERROR.OPENING, e);
                    }
                }
            });
            return;
        }

        if (BuildConfig.DEBUG)
            Log.w(__CLASSNAME__, getMethodName() + "[ED]" + type + ":" + url + "->" + path);
    }

    public void down() {
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]" + mLyc);

        // int fileType = mType;

        String sdpath = newPath;

        HttpClient downClient = new DefaultHttpClient();
        HttpGet testHttpGet = new HttpGet(mLyc);
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "httpget mUrl = " + mLyc);

        try {
            HttpResponse testResponse = downClient.execute(testHttpGet);
            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "excute");

            HttpEntity downEntity = testResponse.getEntity();
            if (downEntity != null) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "entity not null");

                int BUFFER_SIZE = 1024 * 10;
                byte[] buffer = new byte[BUFFER_SIZE];

                InputStream testInputStream = null;
                testInputStream = downEntity.getContent();
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "getcontent");
                BufferedInputStream testInputBuf = new BufferedInputStream(testInputStream, BUFFER_SIZE);

                File file = null;
                file = new File(sdpath + File.separator + mFileName);
                file.createNewFile();
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "createnewfile");
                FileOutputStream testFileOutputStream = new FileOutputStream(file, false);
                BufferedOutputStream testOutputBuf = new BufferedOutputStream(testFileOutputStream, BUFFER_SIZE);

                int readSize = -1;

                while ((readSize = testInputBuf.read(buffer)) != -1) {
                    // if (BuildConfig.DEBUG) _LOG.i(__CLASSNAME__, "readSize = " + String.valueOf(readSize));
                    testOutputBuf.write(buffer, 0, readSize);
                }

                // switch (fileType) {
                // case FILE_ARTIST_IMAGE:
                // if (BuildConfig.DEBUG) _LOG.i(__CLASSNAME__, "_COMPLETE_ARTIST_IMAGE");
                // sendMessage(_COMPLETE_ARTIST_IMAGE);
                // break;
                // case FILE_SONG:
                // if (BuildConfig.DEBUG) _LOG.i(__CLASSNAME__, "_COMPLETE_SONG");
                // sendMessage(_COMPLETE_SONG);
                // break;
                // case FILE_LISTEN:
                // if (BuildConfig.DEBUG) _LOG.i(__CLASSNAME__, "_COMPLETE_LISTEN");
                // sendMessage(_COMPLETE_LISTEN);
                // break;
                // case FILE_LISTEN_OTHER:
                // if (BuildConfig.DEBUG) _LOG.i(__CLASSNAME__, "_COMPLETE_LISTEN_OTHER_DOWN");
                // sendMessage(_COMPLETE_LISTEN_OTHER_DOWN);
                // break;
                // }

                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "write end");
                testOutputBuf.flush();

                testInputBuf.close();
                testFileOutputStream.close();
                testOutputBuf.close();
            }
        } catch (Exception e) {
            testHttpGet.abort();
            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "execute fail");
        }

        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]" + mLyc);
    }

}
