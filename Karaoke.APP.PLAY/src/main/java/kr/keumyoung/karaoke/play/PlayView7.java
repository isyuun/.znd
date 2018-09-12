package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import kr.keumyoung.karaoke.widget.ContCircularSeekBar;

public class PlayView7 extends PlayView6 {
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


    ContCircularSeekBar seek_pitch_tempo;
    TextView text_pitch_tempo;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void start() {
        super.start();

        seek_pitch_tempo = findViewById(R.id.layout_pitch_tempo);
        seek_pitch_tempo.setProgress(0);
        seek_pitch_tempo.setMax(0);
        seek_pitch_tempo.setOnCrossClickListener(new ContCircularSeekBar.OnCrossClickListener() {
            @Override
            public void onUpClick(View v) {
                //Log.e(__CLASSNAME__, getMethodName() + "[UP]");
                setPitchUP();
            }

            @Override
            public void onDownClick(View v) {
                //Log.e(__CLASSNAME__, "onDownClick()" + "[DOWN]");
                setPitchDN();
            }

            @Override
            public void onLeftClick(View v) {
                //Log.e(__CLASSNAME__, "onLeftClick()" + "[LEFT]");
                setTempoDN();
            }

            @Override
            public void onRightClick(View v) {
                //Log.e(__CLASSNAME__, "onRightClick()" + "[RIGHT]");
                setTempoUP();
            }

            @Override
            public void onCenterClick(View v) {
                if (!isPrepared()) {
                    open(PlayView7.this.song_id);
                }
                setPitch(0);
                setTempoPercent(0);
                clearPitchTempoText();
            }
        });

        text_pitch_tempo = findViewById(R.id.text_pitch_tempo);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(__CLASSNAME__, "onClick()" + view);
                post(togglePitchTempo);
            }
        });

        //setPitch(0);
        //setTempoPercent(0);
        clearPitchTempoText();

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int code, KeyEvent event) {
                //Log.e(__CLASSNAME__, "onKey()" + v + "," + code + "," + event);
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                switch (code) {
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_SPACE:
                        if (isPlaying()) {
                            if (!isPause()) {
                                pause();
                            } else {
                                play();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        post(showPitchTempo);
                        setPitchUP();
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        post(showPitchTempo);
                        setPitchDN();
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        post(showPitchTempo);
                        setTempoDN();
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        post(showPitchTempo);
                        setTempoUP();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void setPitch(int pitch) {
        if (!isPlaying()) return;
        super.setPitch(pitch);
        setSeekPitchInfo();
        postDelayed(hidePitchTempo, TIMER_HIDE_PITCH_TEMPO);
    }

    @Override
    public void setTempoPercent(int percent) {
        if (!isPlaying()) return;
        super.setTempoPercent(percent);
        setSeekTempoInfo();
        postDelayed(hidePitchTempo, TIMER_HIDE_PITCH_TEMPO);
    }

    private void clearPitchTempoText() {
        String text = getString(R.string.label_pitch) + "\n" + getString(R.string.label_tempo);
        text_pitch_tempo.setText(text);
        seek_pitch_tempo.setProgress(0);
        ((TextView)findViewById(R.id.txt_pitch)).setText(R.string.example_pitch_0);
        ((TextView)findViewById(R.id.txt_tempo)).setText(R.string.example_tempo_0);
    }

    private static int TIMER_HIDE_PITCH_TEMPO = 3000;

    protected boolean showPitchTempo() {
        return seek_pitch_tempo.show();
    }

    protected void showPitchTempo(boolean show) {
        seek_pitch_tempo.show(show);
    }

    protected Runnable showPitchTempo = new Runnable() {
        @Override
        public void run() {
            showPitchTempo(true);
        }
    };

    protected Runnable hidePitchTempo = new Runnable() {
        @Override
        public void run() {
            if (!seek_pitch_tempo.isLoading()) showPitchTempo(false);
        }
    };

    private void togglePitchTempo() {
        showPitchTempo(!showPitchTempo());
        postDelayed(hidePitchTempo, TIMER_HIDE_PITCH_TEMPO);
    }

    protected Runnable togglePitchTempo = new Runnable() {
        @Override
        public void run() {
            togglePitchTempo();
        }
    };

    /**
     * 음정:-12 to 12
     */
    protected void setSeekPitchInfo() {
        showPitchTempo();
        String text = getString(R.string.label_pitch) + "\n";
        text += ((TextView) findViewById(R.id.txt_pitch)).getText().toString();
        text_pitch_tempo.setText(text);
        seek_pitch_tempo.setMax(_PlayView.PITCH_MAX);
        seek_pitch_tempo.setProgress(getPitch());
    }


    /**
     * Tempo percentage must be between -50 and 100
     */
    protected void setSeekTempoInfo() {
        showPitchTempo();
        String text = getString(R.string.label_tempo) + "\n";
        text += ((TextView) findViewById(R.id.txt_tempo)).getText().toString();
        text_pitch_tempo.setText(text);
        seek_pitch_tempo.setMax(_PlayView.TEMPO_MAX);
        seek_pitch_tempo.setProgress((getTempoPercent() > 0 ? (int)(getTempoPercent() / 2.0f) : getTempoPercent()));
    }

    @Override
    public void open(String song_id) {
        super.open(song_id);
        seek_pitch_tempo.startLoading();
    }

    @Override
    public boolean play() {
        boolean ret = super.play();
        seek_pitch_tempo.stopLoading();
        return ret;
    }
}
