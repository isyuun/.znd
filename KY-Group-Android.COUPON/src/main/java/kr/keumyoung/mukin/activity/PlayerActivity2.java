package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.elements.OperationPopup;

import static kr.keumyoung.mukin.elements.OperationPopup.PlayerOperation.FINISH;

public class PlayerActivity2 extends PlayerActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.jump)
    FrameLayout jump;

    @Override
    protected void cancelRecording() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        if (!isPlaying) {
            release();
            return;
        }
        super.cancelRecording();
    }

    private void release() {
        try {
            if (service != null) service.shutdown();
            closePlayer = true;
            preferenceHelper.clearSavedSettings();

            if (playerJNI != null) {
                playerJNI.FinalizePlayer();
                playerJNI = null;
            }

            if (audioJNI != null) {
                //dsjung 종료시 플레이중에 Finalize 하면 오류
                if (isPlayed) audioJNI.StopAudio();
                audioJNI.FinalizeAudio();
                audioJNI = null;
            }

            lyricsTimingHelper.stop();

            navigationHelper.finish(this);
            //navigationHelper.navigate(PlayerActivity.this, HomeActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //jump = findViewById(R.id.jump);
        jump.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
            if (lyricsTimingHelper != null && lyricsTimingHelper.isPlaying()) {
                lyricsTimingHelper.jump();
            }
        });
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
