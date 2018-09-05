package com.devadvance.circularseekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import kr.kymedia.karaoke.app.widget.BuildConfig;

public class CircularSeekBar3 extends CircularSeekBar2 {

    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {

        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    @Override
    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }


    public CircularSeekBar3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CircularSeekBar3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularSeekBar3(Context context) {
        super(context);
    }

    public interface OnCrossClickListener {
        void onUpClick(View v);
        void onDownClick(View v);
        void onLeftClick(View v);
        void onRightClick(View v);
    }

    OnCrossClickListener listener;
    public void setOnCrossClickListener(OnCrossClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(__CLASSNAME__, getMethodName() + event);

        float x = event.getX();
        float y = event.getY();
        int w = getWidth();
        int h = getHeight();

        //up
        if ((x > w * 1/3) && (x < w * 2/3) && y < h * 1/3) {
            //Log.e(__CLASSNAME__, getMethodName() + "[UP]");
            if (this.listener != null) listener.onUpClick(this);
        } else if ((x > w * 1/3) && (x < w * 2/3) && y > h * 2/3) {
            //Log.e(__CLASSNAME__, getMethodName() + "[DOWN]");
            if (this.listener != null) listener.onDownClick(this);
        } else if ((y > h * 1/3) && (y < h * 2/3) && x < w * 1/3) {
            //Log.e(__CLASSNAME__, getMethodName() + "[LEFT]");
            if (this.listener != null) listener.onLeftClick(this);
        } else if ((y > h * 1/3) && (y < h * 2/3) && x > w * 2/3) {
            //Log.e(__CLASSNAME__, getMethodName() + "[RIGHT]");
            if (this.listener != null) listener.onRightClick(this);
        }

        isTouchEnabled = false;
        if (!isTouchEnabled) {
            return false;
        }
        return super.onTouchEvent(event);
    }

}
