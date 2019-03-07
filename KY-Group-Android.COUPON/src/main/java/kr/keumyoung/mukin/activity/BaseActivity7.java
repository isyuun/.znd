package kr.keumyoung.mukin.activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import kr.keumyoung.mukin.BuildConfig;

public class BaseActivity7 extends BaseActivity6 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    protected void volumeUp(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    protected void volumeDown(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + keyCode + ":" + event);
        boolean ret = false;

        //if (action == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    ret = true;
                    break;
                default:
                    break;
            }
        }

        if (ret) {
            return ret;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + keyCode + event);

        boolean ret = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                ret = true;
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_EQUALS:
            case KeyEvent.KEYCODE_NUMPAD_ADD:
                volumeUp(keyCode, event);
                ret = true;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MINUS:
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                volumeDown(keyCode, event);
                ret = true;
                break;
            default:
                break;
        }

        if (ret) {
            return ret;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
