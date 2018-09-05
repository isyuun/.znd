package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.devadvance.circularseekbar.CircularSeekBar3;

public class PlayView7 extends PlayView6 implements CircularSeekBar3.OnCrossClickListener, View.OnClickListener {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    public PlayView7(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PlayView7(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayView7(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayView7(Context context) {
        super(context);
    }

    private  CircularSeekBar3 seekCircle;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        seekCircle = findViewById(R.id.seek_pitch_tempo);
        showCircle(false);
    }

    private void showCircle(boolean show) {
        if (show) {
            findViewById(R.id.layout_pitch_tempo).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layout_pitch_tempo).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void start(String song_id) {
        super.start(song_id);
        seekCircle.startLoading();
        showCircle(true);
    }

    @Override
    public boolean play() {
        seekCircle.stopLoading();
        showCircle(false);
        return super.play();
    }

    @Override
    public void onUpClick(View v) {
        Log.e(__CLASSNAME__, getMethodName() + "[UP]");
    }

    @Override
    public void onDownClick(View v) {
        Log.e(__CLASSNAME__, getMethodName() + "[DOWN]");
    }

    @Override
    public void onLeftClick(View v) {
        Log.e(__CLASSNAME__, getMethodName() + "[LEFT]");
    }

    @Override
    public void onRightClick(View v) {
        Log.e(__CLASSNAME__, getMethodName() + "[RIGHT]");
    }

    @Override
    public void onClick(View view) {
        Log.e(__CLASSNAME__, getMethodName() + view);
    }
}
