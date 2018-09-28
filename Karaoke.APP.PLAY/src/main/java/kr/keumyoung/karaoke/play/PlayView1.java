package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import kr.kymedia.karaoke.play.impl.ISongPlay;

public class PlayView1 extends PlayView implements ISongPlay.Listener {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    public PlayView1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PlayView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayView1(Context context) {
        super(context);
    }


    private _Listener listener;

    public void setOnListener(ISongPlay.Listener listener) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + (listener instanceof ISongPlay.Listener) + ":" + listener);
        this.listener = (_Listener) listener;
    }

    @Override
    public void onPrepared() {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
        if (listener != null) {
            listener.onPrepared();
        }
    }

    @Override
    public void onTime(int t) {
        //if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + ":" + t + ":" + listener);
        if (listener != null) {
            listener.onTime(t);
        }
    }

    @Override
    public void onCompletion() {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
        if (listener != null) {
            listener.onCompletion();
        }
    }

    @Override
    public void onError() {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
        if (listener != null) {
            listener.onError();
        }
    }

    @Override
    public void onError(ISongPlay.ERROR t, Exception e) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + t + ":" + e + ":" + listener);
        if (listener != null) {
            listener.onError(t, e);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + percent + ":" + listener);
        if (listener != null) {
            listener.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onRelease() {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
        if (listener != null) {
            listener.onRelease();
        }
    }

    @Override
    public void onSeekComplete() {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + listener);
        if (listener != null) {
            listener.onSeekComplete();
        }
    }

    @Override
    public void onReady(int count) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + count + ":" + listener);
        if (listener != null) {
            listener.onReady(count);
        }
    }

    @Override
    public void onRetry(int count) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + count + ":" + listener);
        if (listener != null) {
            listener.onRetry(count);
        }
    }

    @Override
    public void onTimeout(long timeout) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + timeout + ":" + listener);
        if (listener != null) {
            listener.onTimeout(timeout);
        }
    }

}
