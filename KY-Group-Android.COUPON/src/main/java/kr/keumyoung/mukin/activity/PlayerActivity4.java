package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.elements.ControlPanelPlay;

public class PlayerActivity4 extends PlayerActivity3 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private ControlPanelPlay.PlayButtonState buttonState = ControlPanelPlay.PlayButtonState.INIT;

    @Override
    public void updatePlayerState(ControlPanelPlay.PlayButtonState buttonState) {
        this.buttonState = buttonState;
        super.updatePlayerState(buttonState);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean ret = false;

        if (action == KeyEvent.ACTION_DOWN) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + keyCode + ":" + event);
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    if (this.buttonState == ControlPanelPlay.PlayButtonState.PLAY) {
                        pause();
                    } else {
                        play();
                    }
                    ret = true;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    next();
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

    private void play() {
        updatePlayerState(ControlPanelPlay.PlayButtonState.PLAY);
        controlPanelComponent.updatePlayButtonWithState(ControlPanelPlay.PlayButtonState.PLAY);
    }

    private void pause() {
        updatePlayerState(ControlPanelPlay.PlayButtonState.PAUSE);
        controlPanelComponent.updatePlayButtonWithState(ControlPanelPlay.PlayButtonState.PAUSE);
    }

    @Override
    protected void next() {
        if (jump.getVisibility() == View.VISIBLE) {
            jump();
        } else {
            super.next();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreate() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onCreate();
    }

    @Override
    protected void initiatePlayer() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.initiatePlayer();
    }

    @Override
    protected void downlaodSongKY3() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.downlaodSongKY3();
    }

    @Override
    public void parseKY3() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.parseKY3();
    }

    @Override
    protected void unpack() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.unpack();
    }

    @Override
    protected void player() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.player();
    }

    @Override
    protected void progress() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.progress();
    }

    @Override
    protected void prepare() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.prepare();
    }

    @Override
    protected void start() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.start();
    }

    @Override
    protected void stop() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.stop();
    }

    @Override
    protected void release() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.release();
    }

    @Override
    protected void onPlayInit() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onPlayInit();
        hideProgress();
    }

    @Override
    protected void onPlayStart() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onPlayStart();
    }

    @Override
    protected void onPlayStop() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onPlayStop();
    }

    @Override
    protected void onPlayResume() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onPlayResume();
    }

    @Override
    protected void onPlayFinish() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onPlayFinish();
    }

    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onResume();
    }

    @Override
    public void showProgress() {
        post(showProgress);
    }

    private Runnable showProgress = () -> {
        showProgress(true);
    };

    private Runnable hideProgress = () -> {
        super.hideProgress();
    };

    @Override
    public void hideProgress() {
        postDelayed(hideProgress, 3000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + keyCode + ":" + event);
        boolean ret = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
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

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onDestroy();
    }
}
