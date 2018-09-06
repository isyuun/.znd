package kr.keumyoung.karaoke.mukin.activity;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.View;

import kr.keumyoung.karaoke.mukin.BuildConfig;
import kr.keumyoung.karaoke.mukin.R;
import kr.keumyoung.karaoke.play._Listener;
import kr.keumyoung.karaoke.play._PlayView;

public class play2 extends play {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    _PlayView play;
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
                if (!play.isPrepared()) {
                    runOnUiThread(start);
                } else if (play.isPlaying()) {
                    if (!play.isPause()) {
                        pause();
                    } else {
                        resume();
                    }
                }
            }
        });

        runOnUiThread(player);
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
    private Runnable player = new Runnable() {
        @Override
        public void run() {
            player();
        }
    };

    protected void player() {
        if (BuildConfig.DEBUG) Log.d(_toString(), getMethodName() + findViewById(R.id.player));

        play = findViewById(R.id.player);
        //play.setType(TYPE.MEDIAPLAYERPLAY);
        play.start();

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

        lyricsMarginBottom = h / 5;

        //if (findViewById(R.id.layout_bottom) != null) {
        //	lyricsMarginBottom += findViewById(R.id.layout_bottom).getHeight();
        //}
        //if (findViewById(R.id.layout_information) != null) {
        //    lyricsMarginBottom += findViewById(R.id.layout_information).getHeight();
        //}
        play.setLyricsMarginBottom(lyricsMarginBottom);

        // bgkim 폰트 TYPE 적용
        //play.setTypeface(Typeface.createFromAsset(getAssets(), "yun.ttf.mp3"));
        //play.setTypeface(Typeface.createFromAsset(getAssets(), "nanum.ttf.mp3"));

        int iStrokeSize = 6;
        //if (P_APPNAME_SKT_BOX.equalsIgnoreCase(m_strSTBVender)) {
        //    iStrokeSize = 6;
        //}
        play.setStrokeSize(iStrokeSize);

        play.setOnListener(new _Listener() {

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
                //finish();
            }

            @Override
            public void onError() {
                Log.wtf(__CLASSNAME__, "onError()");
                super.onError();
            }
        });


        play.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });

        play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                play();
            }
        });

        play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //finish();
            }
        });

        play.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        play.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
    }

    private Runnable start = new Runnable() {
        @Override
        public void run() {
            start();
        }
    };

    private void start() {
        play.open("08888");
    }

    private void play() {
        play.play();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pause() {
        play.pause();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_play);
    }

    private void resume() {
        play.resume();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void stop() {
        play.stop();
        play2.this.fab.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

}
