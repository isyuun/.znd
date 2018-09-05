package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;

public class PlayView2X extends PlayView2 {
    public PlayView2X(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PlayView2X(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayView2X(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayView2X(Context context) {
        super(context);
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    protected void startOnTimer(){

        mRunnable = new Runnable() {
            @Override
            public void run() {
                onTime(getMediaPlayer().getCurrentPosition());
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }

    protected void stopOnTimer(){
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        super.onCompletion(mp);
        stopOnTimer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopOnTimer();
        return super.onError(mp, what, extra);
    }

    @Override
    public boolean play() throws Exception {
        startOnTimer();
        return super.play();
    }

    @Override
    public void stop() {
        stopOnTimer();
        super.stop();
    }
}
