package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class PlayerActivity2 extends PlayerActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.jump)
    FrameLayout jump;

    @Override
    protected void prepareMediaPlayer() {
        super.prepareMediaPlayer();
        int tempo = preferenceHelper.getInt(PreferenceKeys.TEMPO_VALUE);
        int pitch = preferenceHelper.getInt(PreferenceKeys.PITCH_VALUE);
        int sex = preferenceHelper.getInt(PreferenceKeys.SONG_GENDER, -1);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":tempo:" + tempo + ":pitch:" + pitch + ":sex:" + sex);
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
    }

    @Override
    protected void cancelRecording() {
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
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        jump.setVisibility(View.VISIBLE);
    }

    public void hideJump() {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        jump.setVisibility(View.INVISIBLE);
    }
}
