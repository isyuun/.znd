package kr.keumyoung.karaoke.mukin.activity;

import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;

import kr.keumyoung.karaoke.mukin.BuildConfig;
import kr.keumyoung.karaoke.mukin.R;
import kr.keumyoung.karaoke.play._Listener;
import kr.keumyoung.karaoke.play._PlayView;
import kr.kymedia.karaoke.widget.util.WidgetUtils;

public class play2 extends play {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    _PlayView player;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                play2.this.fab.setImageResource(android.R.drawable.ic_media_pause);
                //start
                if (!player.isPrepared()) {
                    start();
                } else if (player.isPlaying()) {
                    if (!player.isPause()) {
                        pause();
                    } else {
                        resume();
                    }
                }
            }
        });

        player();

        WidgetUtils.setOnKeyListener(this, player, new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int code, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                switch (code) {
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_SPACE:
                        //start
                        if (!player.isPrepared()) {
                            start();
                        } else if (player.isPlaying()) {
                            if (!player.isPause()) {
                                pause();
                            } else {
                                resume();
                            }
                        }
                        break;
                    //case KeyEvent.KEYCODE_DPAD_UP:
                    //    post(showPitchTempo);
                    //    setPitchUP();
                    //    break;
                    //case KeyEvent.KEYCODE_DPAD_DOWN:
                    //    post(showPitchTempo);
                    //    setPitchDN();
                    //    break;
                    //case KeyEvent.KEYCODE_DPAD_LEFT:
                    //    post(showPitchTempo);
                    //    setTempoDN();
                    //    break;
                    //case KeyEvent.KEYCODE_DPAD_RIGHT:
                    //    post(showPitchTempo);
                    //    setTempoUP();
                    //    break;
                }
                return false;
            }
        }, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //runOnUiThread(start);   //test
    }

    /**
     * <pre>
     * AOSP(BHX-S300)
     * <a href="http://pms.skdevice.net/redmine/issues/3482">3482 일부노래 자막 하단이 잘려서 출력되는 현상</a>
     * 	48859 - '씨스타 - Shake It' 노래 부르기 진행 중 일부 가사 하단부분이 잘려서 출력됩니다.
     * </pre>
     * <p/>
     * 자막하단여백
     */
    //private Runnable player = new Runnable() {
    //    @Override
    //    public void run() {
    //        player();
    //    }
    //};
    protected void player() {
        if (BuildConfig.DEBUG) Log.d(_toString(), getMethodName() + findViewById(R.id.player));

        player = findViewById(R.id.player);
        //player.setType(TYPE.MEDIAPLAYERPLAY);
        player.start();

        int lyricsMarginBottom = 10;

        int h = 0;
        int w = 0;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        }
        w = size.x;
        h = size.y;

        lyricsMarginBottom = h / 10;

        player.setLyricsMarginBottom(lyricsMarginBottom);

        // bgkim 폰트 TYPE 적용
        //player.setTypeface(Typeface.createFromAsset(getAssets(), "yun.ttf.mp3"));
        player.setTypeface(Typeface.createFromAsset(getAssets(), "nanum.ttf.mp3"));

        int iStrokeSize = 6;
        //if (P_APPNAME_SKT_BOX.equalsIgnoreCase(m_strSTBVender)) {
        //    iStrokeSize = 6;
        //}
        player.setStrokeSize(iStrokeSize);

        player.setOnListener(new _Listener() {

            @Override
            public void onTime(int t) {
                super.onTime(t);
            }

            @Override
            public void onPrepared() {
                super.onPrepared();
                play();
            }

            @Override
            public void onCompletion() {
                super.onCompletion();
                stop();
            }

            @Override
            public void onError() {
                super.onError();
                stop();
            }
        });


        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                play();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
    }

    private void  start() {
        player.open("08888");
    }

    private void play() {
        player.play();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pause() {
        player.pause();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_play);
    }

    private void resume() {
        player.resume();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void stop() {
        player.stop();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

}
