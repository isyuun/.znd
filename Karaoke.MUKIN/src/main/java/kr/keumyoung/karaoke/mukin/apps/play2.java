package kr.keumyoung.karaoke.mukin.apps;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import kr.keumyoung.karaoke.mukin.BuildConfig;
import kr.keumyoung.karaoke.mukin.R;
import kr.keumyoung.karaoke.play._Listener;
import kr.keumyoung.karaoke.play._PlayView;
import kr.kymedia.karaoke.api.KPItem;

public class play2 extends play {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    FloatingActionButton fab;

    _PlayView player;
    KPItem item;
    String song_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.item = getIntent().getExtras().getParcelable("item");
        Log.e(__CLASSNAME__, getMethodName() + "\n" + this.item.toString(2));
        //String number = item.getValue("number");
        //String title = item.getValue("title");
        //String artist = item.getValue("artist");
        //String composer = item.getValue("composer");
        //String lyricist = item.getValue("lyricist");
        this.song_id = String.format("%05d", Integer.parseInt(item.getValue("number")));

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

        fab.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player();
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

        // bgkim 폰트 TYPE 적용
        player.setTypeface(Typeface.createFromAsset(getAssets(), "yun.ttf.mp3"));
        //player.setTypeface(Typeface.createFromAsset(getAssets(), "nanum.ttf.mp3"));

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

        player.song_id = this.song_id;
    }

    private void  start() {
        player.open(this.song_id);
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
