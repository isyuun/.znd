package kr.keumyoung.mukin.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import kr.kymedia.karaoke.util.Log;

public class BaseActivity5 extends BaseActivity4 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    protected void volumeUp(int keyCode, KeyEvent event) {
        Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    protected void volumeDown(int keyCode, KeyEvent event) {
        Log.e(__CLASSNAME__, getMethodName() + keyCode + ", " + event);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    volumeUp(keyCode, event);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    volumeDown(keyCode, event);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
