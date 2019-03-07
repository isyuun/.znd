package kr.keumyoung.mukin.activity;

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
    protected void initiatePlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.initiatePlayer();
    }

    @Override
    protected void prepareMediaPlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.prepareMediaPlayer();
    }

    @Override
    protected void onPlayInit() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onPlayInit();
        hideProgress();
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

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
}
