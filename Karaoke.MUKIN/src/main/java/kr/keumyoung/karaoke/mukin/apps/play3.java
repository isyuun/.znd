package kr.keumyoung.karaoke.mukin.apps;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import kr.keumyoung.karaoke.mukin.BuildConfig;

public class play3 extends play2 {
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
}
