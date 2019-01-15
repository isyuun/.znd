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

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    @Override
    protected void initiatePlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":mediaManager:" + mediaManager);
        //if (mediaManager == null) mediaManager = new MediaManager();
        super.initiatePlayer();
    }

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
