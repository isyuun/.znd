package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.elements.ControlPanelPlay;

public class PlayerActivity2 extends PlayerActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.jump)
    FrameLayout jump;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jump.setVisibility(View.INVISIBLE);
        jump.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
            if (lyricsTimingHelper != null && lyricsTimingHelper.isPlaying()) {
                lyricsTimingHelper.jump();
            }
        });
    }

    @Override
    public void updatePlayerState(ControlPanelPlay.PlayButtonState buttonState) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + buttonState);
        super.updatePlayerState(buttonState);
        switch (buttonState) {
            case INIT:
                hideJump();
                break;
            case PLAY:
                break;
            case PAUSE:
                break;
            case RESUME:
                break;
            case FINISHED:
                break;
        }
    }

    public void showJump() {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + isPlaying);
        if (isPlaying) jump.setVisibility(View.VISIBLE);
        else jump.setVisibility(View.INVISIBLE);
    }

    public void hideJump() {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + isPlaying);
        jump.setVisibility(View.INVISIBLE);
    }
}
