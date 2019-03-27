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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.TV
 * filename	:	PlayView3.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ PlayView3.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import kr.keumyoung.karaoke.api._Const;

/**
 * @author isyoon
 * @version 1.0
 * @since 2015. 2. 5.
 */
class LyricsPlay3 extends LyricsPlay2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }

    public LyricsPlay3(Context context) {
        super(context);
    }

    /**
     * 자막하단여백
     */
    private int mLyricsMarginBottom = 0;

    /**
     * 자막하단여백
     */
    public void setLyricsMarginBottom(int lyricsMarginBottom) {
        this.mLyricsMarginBottom = lyricsMarginBottom;
    }

    /**
     * 자막하단여백
     */
    public int getLyricsMarginBottom() {
        return mLyricsMarginBottom;
    }

    /**
     * <pre>
     * AOSP(BHX-S300)
     * <a href="http://pms.skdevice.net/redmine/issues/3482">3482 일부노래 자막 하단이 잘려서 출력되는 현상</a>
     * 	48859 - '씨스타 - Shake It' 노래 부르기 진행 중 일부 가사 하단부분이 잘려서 출력됩니다.
     * </pre>
     *
     * @author isyoon
     * @version 1.0
     * @since 2015. 9. 3.
     */
    @Override
    protected void init() {

        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
        super.init();

        // bgkim 각각의 폰트 사이즈는 비율로 조절
        // int h = getWindowManager().getDefaultDisplay().getHeight();
        // int w = getWindowManager().getDefaultDisplay().getWidth();
        Rect rect = getHolder().getSurfaceFrame();
        int h = rect.height();
        int w = rect.width();
        Log.e(__CLASSNAME__ + _Const.TAG_LYRIC, "init() " + w + "," + h);

        //Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //
        //display.getSize(size);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //   display.getRealSize(size);
        //}
        //w = size.x;
        //h = size.y;
        //_Log.e(__CLASSNAME__ + _Const.TAG_LYRIC, "init() " + w + "," + h + ":" + size);
        //w = getWidth();
        //h = getHeight();
        ////_Log.e(__CLASSNAME__ + _Const.TAG_LYRIC, "init() " + w + "," + h + ":" + rect);
        //_Log.e(__CLASSNAME__ + _Const.TAG_LYRIC, "init() " + w + "," + h);

        mLyricsMarginBottom = h / 8;

        int iSongInfoPosition = w / 4;
        int iTitleFontSize = h / 13;
        int iLyricsFontSize = h / 12;
        int iSingerFontSize = h / 14;
        int iReadyFontSize = h / 18;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            iSongInfoPosition = w / 3;
            iTitleFontSize = w / 13;
            iLyricsFontSize = w / 12;
            iSingerFontSize = w / 14;
            iReadyFontSize = w / 18;
        }

        //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            iTitleFontSize /= 1.5f;
            iLyricsFontSize /= 1.5f;
            iSingerFontSize /= 1.5f;
            iReadyFontSize /= 1.5f;
        }

        setSongInfoPosition(iSongInfoPosition);
        setTitleFontSize(iTitleFontSize);
        setLyricsFontSize(iLyricsFontSize);
        setSingerFontSize(iSingerFontSize);
        setReadyFontSize(iReadyFontSize);
        setStrokeSize(6);
    }

    @Override
    protected void onAttachedToWindow() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onAttachedToWindow();
        //bgkim 배경을 투명하게
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + holder);
        //super.surfaceCreated(holder);
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + holder + ":" + format + ", " + w + ", " + h);
        //super.surfaceChanged(holder, format, w, h);
        init();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + holder);
        //super.surfaceDestroyed(holder);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean orientation = false;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                //_Log.e(__CLASSNAME__, getMethodName() + "[ORIENTATION_LANDSCAPE]" + newConfig);
                orientation = true;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                //_Log.e(__CLASSNAME__, getMethodName() + "[ORIENTATION_PORTRAIT]" + newConfig);
                orientation = true;
                break;
        }

        if (orientation) {
            init();
        }
    }

}
