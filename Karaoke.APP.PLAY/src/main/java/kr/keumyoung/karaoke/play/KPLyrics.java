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
 * filename	:	PlayViewThread.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayViewThread.java
 * </pre>
 */

package kr.keumyoung.karaoke.play;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import kr.keumyoung.karaoke.api._Const;
import kr.keumyoung.karaoke.data.SongUtil;

/**
 * @author isyoon
 * @version 1.0
 * @since 2015. 3. 12.
 */
class KPLyrics extends Thread {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }

    /**
     * <pre>
     * TODO
     * </pre>
     */
    protected final _LyricsPlay mLyricsPlay;
    protected boolean done;
    private final Paint m_paintLyrics;
    private final Paint m_paintSinger;
    private final Paint m_paintTitle;
    private final Paint m_paintReady;
    public boolean m_bReady;
    private int m_nInterval;
    private int m_nTickTime;
    private int m_nFirstTick;
    private int m_nEndTime;
    private int m_nDisplay;
    private int m_nLinePos;
    private int m_nSpanTime;
    private int m_itor;
    private String m_strReady;
    private final String[] m_strLine = new String[2];
    protected final String[] m_strLyrics = new String[2];
    protected final Point[] m_ptDrawPos = new Point[2];
    private String m_strPrev;
    private String m_strGo;
    protected Point m_ptLyrics;
    protected int m_width;
    protected int m_height;
    private final int[] m_nInColor = new int[2];
    private final int[] m_nOutColor = new int[2];
    private int m_line;

    KPLyrics(_LyricsPlay lyricsPlay) {
        super();
        this.mLyricsPlay = lyricsPlay;
        done = false;
        mSurfaceHolder = this.mLyricsPlay.getHolder();
        Rect rect = mSurfaceHolder.getSurfaceFrame();
        m_width = rect.width();
        m_height = rect.height();

        init();

        m_ptLyrics = new Point();
        m_ptDrawPos[0] = new Point();
        m_ptDrawPos[1] = new Point();

        m_paintLyrics = new Paint();
        m_paintLyrics.setAntiAlias(true);
        m_paintLyrics.setStyle(Paint.Style.STROKE);
        m_paintLyrics.setStrokeWidth(mLyricsPlay.m_iStrokeSize);
        m_paintLyrics.setTextSize(mLyricsPlay.m_iLyricsFontSize);
        m_paintLyrics.setTypeface(mLyricsPlay.mTypeface);

        m_paintTitle = new Paint();
        m_paintTitle.setAntiAlias(true);
        m_paintTitle.setStyle(Paint.Style.STROKE);
        m_paintTitle.setStrokeWidth(mLyricsPlay.m_iStrokeSize);
        m_paintTitle.setTextSize(mLyricsPlay.m_iTitleFontSize);
        m_paintTitle.setTypeface(mLyricsPlay.mTypeface);

        m_paintSinger = new Paint();
        m_paintSinger.setAntiAlias(true);
        m_paintSinger.setStyle(Paint.Style.STROKE);
        m_paintSinger.setStrokeWidth(mLyricsPlay.m_iStrokeSize);
        m_paintSinger.setTextSize(mLyricsPlay.m_iSingerFontSize);
        m_paintSinger.setTypeface(mLyricsPlay.mTypeface);

        m_paintReady = new Paint();
        m_paintReady.setAntiAlias(true);
        m_paintReady.setStyle(Paint.Style.STROKE);
        m_paintReady.setStrokeWidth(mLyricsPlay.m_iStrokeSize);
        m_paintReady.setTextSize(mLyricsPlay.m_iReadyFontSize);
        m_paintReady.setTypeface(mLyricsPlay.mTypeface);
    }


    public void setTitleFontSize(int iTitleFontSize) {
        //if (m_paintTitle != null) m_paintTitle.setTextSize(iTitleFontSize);
    }

    public void setLyricsFontSize(int iLyricsFontSize) {
        //if (m_paintLyrics != null) m_paintLyrics.setTextSize(iLyricsFontSize);
    }

    public void setSingerFontSize(int iSingerFontSize) {
        //if (m_paintSinger != null) m_paintSinger.setTextSize(iSingerFontSize);
    }

    public void setReadyFontSize(int iReadyFontSize) {
        //if (m_paintReady != null) m_paintReady.setTextSize(iReadyFontSize);
    }

    public void init() {
        m_itor = 0;
        m_bReady = false;
        m_nInterval = 0;
        m_nTickTime = 0;
        m_nFirstTick = 0;
        m_nEndTime = 0;
        m_nDisplay = 0;
        m_nLinePos = 0;
        m_nSpanTime = 0;
        m_line = 0;

        // bgkim LYRICS COLOR 정의
        m_nInColor[0] = Color.WHITE;
        m_nOutColor[0] = Color.BLACK;
        m_nInColor[1] = Color.WHITE;
        m_nOutColor[1] = Color.BLACK;
    }

    @Override
    public synchronized void start() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.start();
    }

    protected SurfaceHolder mSurfaceHolder;

    @Override
    public void run() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + mLyricsPlay.m_redraw + ":" + mLyricsPlay.getHolder() + ":" + mLyricsPlay.getHolder().getSurfaceFrame());

        // 크기확인
        Rect rect = mSurfaceHolder.getSurfaceFrame();
        m_width = rect.width();
        m_height = rect.height();
    }

    public void requestExitAndWait() {
        done = true;

        try {
            join();
        } catch (Exception e) {
            Log.wtf(__CLASSNAME__ + _Const.TAG_LYRIC, "requestExitAndWait()" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public void onWindowResize(int w, int h) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + w + ", " + h);

        m_width = w;
        m_height = h;
    }

    protected void outTextWithOutStroke(Canvas canvas, Paint paint, String str, int x, int y, int inColor) {
        paint.setColor(inColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(str, x, y, paint);
    }

    protected void outText(Canvas canvas, Paint paint, String str, int x, int y, int inColor, int outColor) {
        paint.setColor(outColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mLyricsPlay.m_iStrokeSize);
        canvas.drawText(str, x, y, paint);

        paint.setColor(inColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(str, x, y, paint);
    }

    protected Rect outSize(Paint paint, String str) {
        Rect rect = new Rect();

        if (str.length() == 0) {
            rect.set(0, 0, 0, 0);
            return rect;
        }

        paint.getTextBounds(str, 0, str.length(), rect);

        if (rect.width() > 0) {
            rect.right += 2;
            rect.bottom += 2;
        }

        return rect;
    }

    protected void doDraw(Canvas canvas) {
        try {
            if (!mLyricsPlay.m_redraw) {
                //if (mLyricsPlay.isPlaying() || mLyricsPlay.getVisibility() == View.VISIBLE)
                {
                    drawLyrics(canvas, mLyricsPlay.getCurrentPosition());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void drawLyrics(Canvas canvas, int t) {
        int nState = _Const.SYNC_STATUS_NO;
        int nType = timeOut(t);
        int nLinePos = getLinePos();

        if (m_bReady == false) {
            nState = 0;
            setSinger(canvas, m_paintSinger, mLyricsPlay.m_data.getLyricsInfoTag().strSinger, mLyricsPlay.m_data.getLyricsInfoTag().strAuthor, mLyricsPlay.m_data.getLyricsInfoTag().strLyricsAuthor);
            setTitle(canvas, m_paintTitle, mLyricsPlay.m_data.getLyricsInfoTag().strTitle1, mLyricsPlay.m_data.getLyricsInfoTag().strTitle2);
        } else {
            nState = 1;

            if (nType == _Const.SYNC_STATUS_NUM) {
                setReady(canvas, m_paintReady, m_strReady);
            }

            setLyrics(canvas, m_paintLyrics, m_strLine[0], 0, m_nInColor[0], m_nOutColor[0]);
            setLyrics(canvas, m_paintLyrics, m_strLine[1], 1, m_nInColor[1], m_nOutColor[1]);
        }

        if (nState == 1) {
            Rect curRect = outSize(m_paintLyrics, m_strGo);
            Rect preRect = outSize(m_paintLyrics, m_strPrev);

            int nPixel = (int) (curRect.width() * ((float) m_nTickTime / (float) m_nSpanTime)) + preRect.width();

            // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[nPixel] : " + String.valueOf(nPixel));

            redrawLyrics(canvas, m_paintLyrics, m_strLine[nLinePos], nPixel, nLinePos);
        }
    }

    void redrawLyrics(Canvas canvas, Paint paint, String str, int end, int type) {
        Rect rect = outSize(paint, str);

        mLyricsPlay.m_iBeforeEnd = end;

        if (end == 0) {
            if (mLyricsPlay.m_iBeforeEnd != end) {
                int pos = 1 - getLinePos();
                m_nInColor[pos] = Color.BLACK;
                m_nOutColor[pos] = Color.YELLOW;
            }
        }

        if (end > rect.width()) {
            end = rect.width();

            // bgkim
            if (!mLyricsPlay.m_strContinueLyrics.equals("")) {
                int pos = getLinePos();
                m_nInColor[pos] = Color.BLACK;
                m_nOutColor[pos] = Color.YELLOW;
            }
        }

        canvas.save();

        canvas.translate(m_ptDrawPos[type].x, m_ptDrawPos[type].y);
        // bgkim 진행 중인 가사를 그릴 때 높이 유동 조정 (기존 25로 고정)
        canvas.clipRect(0, (mLyricsPlay.m_iLyricsFontSize * -1) + 10, end, rect.height() - 25);
        outText(canvas, paint, str, 0, 0, Color.BLACK, Color.YELLOW); // LYRICS COLOR

        canvas.restore();
    }

    void setReady(Canvas canvas, Paint paint, String str) {
        Rect rect = outSize(paint, str);

        // bgkim Ready 표시 위치 y 값 유동 조정 (기존 -18 고정)
        outText(canvas, paint, str, m_ptLyrics.x, m_ptLyrics.y - rect.height() - (mLyricsPlay.m_iLyricsFontSize / 2), Color.YELLOW, Color.BLACK);
    }

    private final int mTitleMarginBottom = 0;

    public Resources getResources() {
        return mLyricsPlay.getResources();
    }

    void setTitle(Canvas canvas, Paint paint, String str1, String str2) {
        int rotation = mLyricsPlay.getWindowManager().getDefaultDisplay().getRotation();
        int marginBottom = 0;

        if (rotation == Surface.ROTATION_0) {
            marginBottom = mTitleMarginBottom;
        } else {
            marginBottom -= mTitleMarginBottom;
        }

        Rect rect1 = outSize(paint, str1);
        Rect rect2 = outSize(paint, str2);

        if (str2 != null && !str2.equals("")) {
            int x1 = (m_width - rect1.width()) / 2;
            int y1 = (m_height - rect1.height() - rect2.height() - 80) / 2;
            int x2 = (m_width - rect2.width()) / 2;
            int y2 = (m_height - rect2.height()) / 2;

            y1 += marginBottom;
            y2 += marginBottom;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                y1 /= 2;
                y2 /= 2;
            }

            outText(canvas, paint, str1, x1, y1, Color.WHITE, Color.BLACK);
            outText(canvas, paint, str2, x2, y2, Color.WHITE, Color.BLACK);
        } else {
            int x = (m_width - rect1.width()) / 2;
            int y = (m_height - rect1.height()) / 2;

            y += marginBottom;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                y /= 2;
            }

            outText(canvas, paint, str1, x, y, Color.WHITE, Color.BLACK);
        }
    }

    /**
     * 아주잘~~~헌다이븅신...이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
     */
    void setSinger(Canvas canvas, Paint paint, String str1, String str2, String str3) {
        String strSinger, strAuthor, strLyricsAuthor;
        strSinger = "노래   " + str1;
        strAuthor = "작곡   " + str2;
        strLyricsAuthor = "작사   " + str3;

        Rect rect1 = outSize(paint, strSinger);
        Rect rect2 = outSize(paint, strAuthor);
        Rect rect3 = outSize(paint, strLyricsAuthor);

        int iBackWidth = 0;

        if (rect1.width() > rect2.width() && rect1.width() > rect3.width()) {
            iBackWidth = rect1.width();
        } else if (rect2.width() > rect1.width() && rect2.width() > rect3.width()) {
            iBackWidth = rect2.width();
        } else if (rect3.width() > rect1.width() && rect3.width() > rect2.width()) {
            iBackWidth = rect3.width();
        }

        int iSingerY = m_height - (rect1.height() * 3 + 150);
        int iAuthorY = m_height - (rect1.height() * 2 + 110);
        int iLyricsAuthorY = m_height - (rect1.height() + 70);

        outRect(canvas, paint, 30, iBackWidth, iSingerY, iSingerY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));
        outRect(canvas, paint, 30, iBackWidth, iAuthorY, iAuthorY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));
        outRect(canvas, paint, 30, iBackWidth, iLyricsAuthorY, iLyricsAuthorY, Color.parseColor("#70608F"), Color.parseColor("#4D4379"));

        outTextWithOutStroke(canvas, paint, strSinger, 50 + mLyricsPlay.m_iSongInfoPosition, iSingerY, Color.WHITE);
        outTextWithOutStroke(canvas, paint, strAuthor, 50 + mLyricsPlay.m_iSongInfoPosition, iAuthorY, Color.WHITE);
        outTextWithOutStroke(canvas, paint, strLyricsAuthor, 50 + mLyricsPlay.m_iSongInfoPosition, iLyricsAuthorY, Color.WHITE);
    }

    /**
     * 아주잘~~~헌다이븅신...이러니까값이안넘어오지...이래놓고강제로처박고앉았네...
     */
    private void outRect(Canvas canvas, Paint paint, int x, int w, int y, int y2, int colorTitle, int colorName) {
        int iCenter = 0;

        if (mLyricsPlay.m_iSongInfoPosition > 950) {
            // 1920 x 1080
            iCenter = 220;
        } else if (mLyricsPlay.m_iSongInfoPosition > 630) {
            // 1280 x 720
            iCenter = 170;
        } else {
            // 960 x 540
            iCenter = 100;
        }

        x = x + mLyricsPlay.m_iSongInfoPosition;
        iCenter = iCenter + mLyricsPlay.m_iSongInfoPosition;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorTitle);
        paint.setAlpha(200);
        canvas.drawRect(x, y - mLyricsPlay.m_iSingerFontSize, iCenter, y2 + (mLyricsPlay.m_iSingerFontSize / 3), paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorName);
        paint.setAlpha(200);
        canvas.drawRect(iCenter, y - mLyricsPlay.m_iSingerFontSize, mLyricsPlay.m_iSongInfoPosition * 2, y2 + (mLyricsPlay.m_iSingerFontSize / 3), paint);
    }

    void setLyrics(Canvas canvas, Paint paint, String str, int type, int inColor, int outColor) {
        Rect rect = outSize(paint, str);
        if (type == 0) {
            m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
            // bgkim 가사 위치 조절 (메뉴 단 감안)
            m_ptDrawPos[type].y = m_height - rect.height() * 2 - 30;
            m_strLyrics[0] = str;
            m_ptLyrics = m_ptDrawPos[type];
        } else {
            m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
            // bgkim 가사 위치 조절 (메뉴 단 감안)
            m_ptDrawPos[type].y = m_height - rect.height() - 10;
            m_strLyrics[1] = str;
        }

        outText(canvas, paint, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, inColor, outColor);
    }

    int getLinePos() {
        return (m_nLinePos + m_nDisplay) % 2;
    }

    int timeOut(int nTime) {
        int nCodeFlag = -1;
        int nInterval = 0;
        int nState = _Const.SYNC_STATUS_NO;

        if (nTime < 0) {
            nTime = 0;
        }

        nInterval = nTime - m_nInterval;

        if (nInterval > 0) {
            m_nInterval = nTime;
        }

        m_nTickTime += nInterval;

        if (m_itor < mLyricsPlay.m_data.getListSyncTag().size()) {
            if (m_nFirstTick != 0) {
                nState = _Const.SYNC_STATUS_NUM;
                int nPercent = (int) (100 * ((float) m_nTickTime / (float) m_nFirstTick));

                if (nPercent > 95) {
                    m_strReady = "GO";
                } else if (nPercent > 80) {
                    m_strReady = "1";
                } else if (nPercent > 60) {
                    m_strReady = "2";
                } else if (nPercent > 40) {
                    m_strReady = "3";
                } else if (nPercent > 20) {
                    m_strReady = "4";
                } else if (nPercent >= 0) {
                    m_strReady = "5";
                }
            }

            if (mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncStart <= nTime) {
                nCodeFlag = mLyricsPlay.m_data.getListSyncTag().get(m_itor).nAttribute;
            }

            if (m_nEndTime > 0 && nTime > m_nEndTime + 2000) {
                if (mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay % 2 == 1) {
                    m_nDisplay++;
                }

                m_strLine[0] = "(간주중)";
                m_strLine[1] = "";

                m_nInColor[0] = Color.WHITE;
                m_nOutColor[0] = Color.BLACK;

                m_nInColor[1] = Color.WHITE;
                m_nOutColor[1] = Color.BLACK;

                mLyricsPlay.m_iBeforeEnd = 0;
                mLyricsPlay.m_strContinueLyrics = "";
                mLyricsPlay.m_bContinue = false;
                mLyricsPlay.m_bSkipChangeColor = true;

                m_nEndTime = 0;
                nState = _Const.SYNC_STATUS_DIVISION;
            }

            switch (nCodeFlag) {
                case _Const.SYNC_ENDDIVISION:
                case _Const.SYNC_TEXT:
                    if (!m_bReady) {
                        for (int i = 0; i < _Const.LINE_VIEW; i++) {
                            m_strLine[i] = SongUtil.byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay + i).strLineLyrics);
                        }

                        // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[0] : " + m_strLine[0]);
                        // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[1] : " + m_strLine[1]);

                        m_nFirstTick = 1;
                        m_bReady = true;
                    }

                    if (m_nFirstTick == 0 && mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosLyrics == 0) {
                        nState = _Const.SYNC_STATUS_START;
                        m_nLinePos += 1;
                    } else if (m_nFirstTick != 0) {
                        nState = _Const.SYNC_STATUS_DEL;
                        m_nLinePos = mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosOneLine;
                    } else {
                        nState = _Const.SYNC_STATUS_TEXT;
                        m_nLinePos = mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosOneLine;
                    }

                    m_nFirstTick = 0;
                    m_strReady = "";

                    m_nSpanTime = (int) (mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncEnd - mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncStart);
                    m_nTickTime = 1;

                    int bufSize = mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosLyrics;
                    byte[] buff = new byte[bufSize + 1];
                    for (int i = 0; i < bufSize; i++) {
                        buff[i] = mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosOneLine).strLineLyrics[i];
                    }

                    buff[bufSize] = '\0';

                    int bufSize2 = mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosLen;
                    byte[] buff2 = new byte[bufSize2 + 1];
                    for (int i = 0; i < bufSize2; i++) {
                        buff2[i] = mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nPosOneLine).strLineLyrics[bufSize + i];
                    }

                    buff2[bufSize2] = '\0';

                    m_strPrev = SongUtil.byteToString(buff);
                    m_strGo = SongUtil.byteToString(buff2);

                    if ((mLyricsPlay.m_data.getListSyncTag().get(m_itor).nNextDisplay == 1) && (nState != _Const.SYNC_STATUS_DEL && mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay < mLyricsPlay.m_data.getListLyricsTag().size() - 1)) {
                        m_strLine[(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay + m_nDisplay + 1) % 2] = SongUtil
                                .byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay + 1).strLineLyrics);
                        nState = _Const.SYNC_STATUS_NEXT;

                        int pos = 1 - getLinePos();

                        // bgkim LYRICS COLOR
                        if (!mLyricsPlay.m_bContinue) {
                            mLyricsPlay.m_bSkipChangeColor = false;
                            mLyricsPlay.m_strContinueLyrics = m_strLine[pos];
                        } else {
                            mLyricsPlay.m_bSkipChangeColor = true;
                        }

                        mLyricsPlay.m_bContinue = true;

                        m_nInColor[pos] = Color.WHITE;
                        m_nOutColor[pos] = Color.BLACK;

                        // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[pos] : " + String.valueOf(pos));
                        // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[pos] : " + m_strLine[pos]);
                    }

                    if (nCodeFlag == _Const.SYNC_ENDDIVISION) {
                        m_nEndTime = (int) (mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncEnd);
                    }

                    m_itor++;
                    break;
                case _Const.SYNC_READY:
                    for (int i = 0; i < _Const.LINE_VIEW; i++) {
                        m_strLine[i] = SongUtil.byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(m_itor).nLineDisplay + i).strLineLyrics);
                    }

                    nState = _Const.SYNC_STATUS_READY;
                    m_strPrev = "";
                    m_strGo = "";
                    m_nSpanTime = 0;
                    m_nTickTime = 1;
                    m_nFirstTick = (int) (mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncEnd - mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncStart);
                    m_bReady = true;

                    m_itor++;
                    break;
            }
        }

        if (getLinePos() != m_line) {
            mLyricsPlay.m_bContinue = false;

            if (!mLyricsPlay.m_bSkipChangeColor) {
                // bgkim LYRICS COLOR 완료색으로 처리하는 부분 보완
                if (mLyricsPlay.m_strContinueLyrics != m_strLine[m_line]) {
                    m_nInColor[m_line] = Color.BLACK;
                    m_nOutColor[m_line] = Color.YELLOW;

                    // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[m_line] 01 : " + String.valueOf(m_line));
                    // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[m_line] 01 : " + m_strLine[m_line]);

                    mLyricsPlay.m_bSkipChangeColor = false;
                }
            } else {
                if (mLyricsPlay.m_strContinueLyrics == m_strLine[m_line]) {
                    m_nInColor[m_line] = Color.BLACK;
                    m_nOutColor[m_line] = Color.YELLOW;

                    // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[m_line] 02 : " + String.valueOf(m_line));
                    // if (BuildConfig.DEBUG) _LOG.i("BGKIM", "[m_line] 02 : " + m_strLine[m_line]);

                    mLyricsPlay.m_bSkipChangeColor = false;
                }
            }

            // bgkim 마지막 태그일 경우 흰색(진행 안된 부분)이 남는 경우가 생겨 보완 처리
            if (m_itor >= mLyricsPlay.m_data.getListSyncTag().size()) {
                m_nInColor[m_line] = Color.BLACK;
                m_nOutColor[m_line] = Color.YELLOW;
            }

            m_line = getLinePos();
        }

        return nState;
    }

    public void rePaint(int nTime) {
        init();

        int nCodeFlag = -1;
        int nState = _Const.SYNC_STATUS_NO;

        boolean find = false;
        for (int itor = 0; itor < mLyricsPlay.m_data.getListSyncTag().size(); itor++) {
            nCodeFlag = -1;
            nState = _Const.SYNC_STATUS_NO;

            if (m_nFirstTick != 0) {
                nState = _Const.SYNC_STATUS_NUM;
                int nPercent = (int) (100 * ((float) m_nTickTime / (float) m_nFirstTick));

                if (nPercent > 95) {
                    m_strReady = "GO!";
                } else if (nPercent > 80) {
                    m_strReady = "1";
                } else if (nPercent > 60) {
                    m_strReady = "2";
                } else if (nPercent > 40) {
                    m_strReady = "3";
                } else if (nPercent > 20) {
                    m_strReady = "4";
                } else if (nPercent >= 0) {
                    m_strReady = "5";
                }
            }

            if (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncStart <= nTime) {
                nCodeFlag = mLyricsPlay.m_data.getListSyncTag().get(itor).nAttribute;
            }

            if (m_nEndTime > 0 && nTime > m_nEndTime + 2000) {
                if (mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay % 2 == 1) {
                    m_nDisplay++;
                }

                m_strLine[0] = "(반주중)";
                m_strLine[1] = "";

                m_nInColor[0] = Color.WHITE;
                m_nOutColor[0] = Color.BLACK;

                m_nInColor[1] = Color.WHITE;
                m_nOutColor[1] = Color.BLACK;

                mLyricsPlay.m_iBeforeEnd = 0;
                mLyricsPlay.m_strContinueLyrics = "";
                mLyricsPlay.m_bContinue = false;
                mLyricsPlay.m_bSkipChangeColor = true;

                m_nEndTime = 0;
                nState = _Const.SYNC_STATUS_DIVISION;
            }

            switch (nCodeFlag) {
                case _Const.SYNC_ENDDIVISION:
                case _Const.SYNC_TEXT:
                    if (!m_bReady) {
                        for (int i = 0; i < _Const.LINE_VIEW; i++) {
                            m_strLine[i] = SongUtil.byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay + i).strLineLyrics);
                        }

                        m_nFirstTick = 1;
                        m_bReady = true;
                    }

                    if (m_nFirstTick == 0 && mLyricsPlay.m_data.getListSyncTag().get(itor).nPosLyrics == 0) {
                        nState = _Const.SYNC_STATUS_START;
                        m_nLinePos += 1;
                    } else if (m_nFirstTick != 0) {
                        nState = _Const.SYNC_STATUS_DEL;
                        m_nLinePos = mLyricsPlay.m_data.getListSyncTag().get(itor).nPosOneLine;
                    } else {
                        nState = _Const.SYNC_STATUS_TEXT;
                        m_nLinePos = mLyricsPlay.m_data.getListSyncTag().get(itor).nPosOneLine;
                    }

                    m_nFirstTick = 0;
                    m_strReady = "";

                    m_nSpanTime = (int) (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncEnd - mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncStart);
                    m_nTickTime = 1;

                    if ((mLyricsPlay.m_data.getListSyncTag().get(itor).nNextDisplay == 1) && (nState != _Const.SYNC_STATUS_DEL && mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay < mLyricsPlay.m_data.getListLyricsTag().size() - 1)) {
                        m_strLine[(mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay + m_nDisplay + 1) % 2] = SongUtil
                                .byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay + 1).strLineLyrics);
                        nState = _Const.SYNC_STATUS_NEXT;
                    }

                    if (nCodeFlag == _Const.SYNC_ENDDIVISION) {
                        m_nEndTime = (int) (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncEnd);
                    }

                    break;
                case _Const.SYNC_READY:
                    for (int i = 0; i < _Const.LINE_VIEW; i++) {
                        m_strLine[i] = SongUtil.byteToString(mLyricsPlay.m_data.getListLyricsTag().get(mLyricsPlay.m_data.getListSyncTag().get(itor).nLineDisplay + i).strLineLyrics);
                    }

                    nState = _Const.SYNC_STATUS_READY;
                    m_strPrev = "";
                    m_strGo = "";
                    m_nSpanTime = 0;
                    m_nTickTime = 1;
                    m_nFirstTick = (int) (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncEnd - mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncStart);
                    m_bReady = true;

                    break;
            }

            if (getLinePos() != m_line) {
                // bgkim LYRICS COLOR
                m_nInColor[m_line] = Color.BLACK;
                m_nOutColor[m_line] = Color.YELLOW;
                m_line = getLinePos();
            }

            m_nTickTime = 1;
            m_nInterval = nTime;

            if (m_nFirstTick > 0) {
                m_nFirstTick = (int) (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncStart) - nTime;
            }

            m_itor = itor;

            if (mLyricsPlay.m_data.getListSyncTag().get(itor).lTimeSyncStart >= nTime) {
                find = true;
                break;
            }
        }

        long start = mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncStart;
        long end = mLyricsPlay.m_data.getListSyncTag().get(m_itor).lTimeSyncEnd;

        if (find) {
            if (BuildConfig.DEBUG) Log.i("rePaint()", "OK()" + nState + "()" + nCodeFlag + "()" + m_itor + "()" + mLyricsPlay.m_data.getListSyncTag().size() + "()" + nTime + "()" + start + "()" + end + "()" + m_nEndTime + "()");
        } else {
            if (BuildConfig.DEBUG) Log.i("rePaint()", "NG()" + nState + "()" + nCodeFlag + "()" + m_itor + "()" + mLyricsPlay.m_data.getListSyncTag().size() + "()" + nTime + "()" + start + "()" + end + "()" + m_nEndTime + "()");
        }

    }
}
