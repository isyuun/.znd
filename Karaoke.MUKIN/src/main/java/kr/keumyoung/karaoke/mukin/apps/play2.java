package kr.keumyoung.karaoke.mukin.apps;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import kr.keumyoung.karaoke.mukin.BuildConfig;
import kr.keumyoung.karaoke.mukin.R;
import kr.keumyoung.karaoke.play._Listener;
import kr.keumyoung.karaoke.play._PlayView;
import kr.kymedia.karaoke.api.KPItem;

public class play2 extends play {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    FloatingActionButton play;

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

        this.play = (FloatingActionButton) findViewById(R.id.fab);
        //play.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        start();
        open();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
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
    protected void start() {
        if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + findViewById(R.id.player));

        player = findViewById(R.id.player);
        player.start();

        // bgkim 폰트 TYPE 적용
        //player.setTypeface(Typeface.createFromAsset(getAssets(), "yun.ttf"));
        //player.setTypeface(Typeface.createFromAsset(getAssets(), "nanum.ttf"));

        //???
        player.setStrokeSize(6);

        player.setOnListener(new _Listener() {

            @Override
            public void onTime(int t) {
                super.onTime(t);
            }

            @Override
            public void onPrepared() {
                super.onPrepared();
                play2.this.play.setVisibility(View.VISIBLE);
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


        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                play2.this.play.setImageResource(android.R.drawable.ic_media_pause);
                //open
                if (player.isPlaying()) {
                    if (!player.isPause()) {
                        pause();
                    } else {
                        resume();
                    }
                } else if (player.isPrepared()) {
                    play();
                } else {
                    open();
                }
            }
        });
    }

    private void open() {
        player.open(this.song_id);
        play2.this.play.setImageResource(android.R.drawable.ic_media_play);
        this.play.setVisibility(View.INVISIBLE);
    }

    private void play() {
        player.play();
        play2.this.play.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pause() {
        player.pause();
        play2.this.play.setImageResource(android.R.drawable.ic_media_play);
    }

    private void resume() {
        player.resume();
        play2.this.play.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void stop() {
        player.stop();
        play2.this.play.setImageResource(android.R.drawable.ic_media_play);
    }
}
