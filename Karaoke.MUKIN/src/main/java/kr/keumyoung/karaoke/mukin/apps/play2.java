package kr.keumyoung.karaoke.mukin.apps;

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

        this.play = (FloatingActionButton) findViewById(R.id.fab);

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

    protected void start() {
        if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + findViewById(R.id.player));

        player = findViewById(R.id.player);
        player.start();

        //자막:폰트 TYPE 적용
        //player.setTypeface(Typeface.createFromAsset(getAssets(), "nanum.ttf"));

        //자막:테두리 크기 적용
        //player.setStrokeSize(6);

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
                play2.this.play.setImageResource(android.R.drawable.ic_media_pause);
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
        this.item = getIntent().getExtras().getParcelable("item");
        Log.e(__CLASSNAME__, getMethodName() + "\n" + this.item.toString(2));
        this.song_id = String.format("%05d", Integer.parseInt(item.getValue("number")));

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
