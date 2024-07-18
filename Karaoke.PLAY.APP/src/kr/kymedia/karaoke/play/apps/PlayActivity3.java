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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	PlayActivity1.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app
 *    |_ PlayActivity1.java
 * </pre>
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play.app.R;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

/**
 * TODO<br>
 *
 * <pre>
 * AndroidSlidingUpPanel.library
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 5. 22.
 */
public class PlayActivity3 extends PlayActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public String toString() {

        // return super.toString();
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    @Override
    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }

    @Override
    protected void setContentView() {

        Log.i(__CLASSNAME__, getMethodName());
        // super.setContentView();
        setContentView(R.layout.activity_play2);
    }

    boolean expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.wtf(__CLASSNAME__, getMethodName() + "[ST]");
        super.onCreate(savedInstanceState);
        Log.wtf(__CLASSNAME__, getMethodName() + "[ED]");
    }

    @Override
    protected void onResume() {

        Log.wtf(__CLASSNAME__, getMethodName() + "[ST]");
        super.onResume();
        Log.wtf(__CLASSNAME__, getMethodName() + "[ED]");
    }

    @Override
    protected void onStart() {

        Log.wtf(__CLASSNAME__, getMethodName() + "[ST]");
        super.onStart();
        setSlidingUpPanel();
        Log.wtf(__CLASSNAME__, getMethodName() + "[ED]");
    }

    private void setSlidingUpPanel() {
        Log.wtf(__CLASSNAME__, getMethodName());

        // setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));

        final ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return !expanded;
            }

        });

        final SlidingUpPanelLayout sliding = (SlidingUpPanelLayout) findViewById(R.id.sliding);
        sliding.setDragView(R.id.merge_play_control);
        sliding.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slidingOffset) {
                // _Log.w(__CLASSNAME__, getMethodName() + panel + "," + slidingOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED:
                        onPanelExpanded(panel);
                        break;
                    case COLLAPSED:
                        onPanelCollapsed(panel);
                        break;
                    case ANCHORED:
                        onPanelAnchored(panel);
                        break;
                    case HIDDEN:
                        onPanelHidden(panel);
                        break;
                    case DRAGGING:
                }
            }

            public void onPanelExpanded(View panel) {
                Log.w(__CLASSNAME__, getMethodName() + panel);
                expanded = true;
                // scroll.setScrollingEnabled(expanded);
            }

            public void onPanelCollapsed(View panel) {
                Log.w(__CLASSNAME__, getMethodName() + panel);
                expanded = false;
                // scroll.setScrollingEnabled(expanded);
            }

            public void onPanelAnchored(View panel) {
                Log.w(__CLASSNAME__, getMethodName() + panel);
            }

            public void onPanelHidden(View panel) {
                Log.w(__CLASSNAME__, getMethodName() + panel);
            }
        });
    }

}
