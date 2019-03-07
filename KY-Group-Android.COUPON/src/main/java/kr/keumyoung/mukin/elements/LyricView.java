package kr.keumyoung.mukin.elements;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.lyrics.LyricsTime;

/**
 *  on 02/04/18.
 * Project: KyGroup
 */

public class LyricView extends FrameLayout {

    @BindView(R.id.song_lyrics_current)
    TextView songLyricsCurrent;
    @BindView(R.id.song_lyrics_current_overlay)
    TextView songLyricsCurrentOverlay;

    public enum State {
        PREVIOUS, CURRENT, NEXT
    }

    int currentColor, previousColor, nextColor;

    LyricsTime lyricsTime;
    State state;
    String word;

    public LyricView(Context context) {
        super(context);
        inflate(context, R.layout.view_lyric, this);
        init();
    }

    public LyricView(Context context, LyricsTime lyricsTime, String word, State state) {
        super(context);
        this.lyricsTime = lyricsTime;
        this.state = state;
        this.word = word;
        inflate(context, R.layout.view_lyric, this);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_lyric, this);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_lyric, this);
        init();
    }

    boolean animationStarted = false;

    public void init() {
        ButterKnife.bind(this);

        Resources resources = getResources();

        nextColor = resources.getColor(R.color.song_lyrics_color_current);
        previousColor = resources.getColor(R.color.song_lyrics_color_prev);
        currentColor = nextColor;

        if (lyricsTime != null && state != null) {
            switch (state) {
                case NEXT:
                    songLyricsCurrentOverlay.setVisibility(GONE);
                    songLyricsCurrent.setText(word);
                    songLyricsCurrent.setTextColor(nextColor);
                    break;
                case CURRENT:
                    songLyricsCurrentOverlay.setVisibility(GONE);
                    songLyricsCurrent.setText(word);
                    songLyricsCurrentOverlay.setText(word);
                    songLyricsCurrent.setTextColor(nextColor);
                    songLyricsCurrentOverlay.setTextColor(previousColor);
                    break;
                case PREVIOUS:
                    songLyricsCurrentOverlay.setVisibility(GONE);
                    songLyricsCurrent.setText(word);
                    songLyricsCurrent.setTextColor(previousColor);
                    break;
            }
        }

        animationStarted = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!animationStarted) {
            if (state == State.CURRENT) {
                final ValueAnimator anim = ValueAnimator.ofInt(0, songLyricsCurrent.getMeasuredWidth());
                anim.addUpdateListener(valueAnimator -> {
	                songLyricsCurrentOverlay.setVisibility(View.VISIBLE);
                    int val = (Integer) anim.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = songLyricsCurrentOverlay.getLayoutParams();
                    layoutParams.width = val;
                    songLyricsCurrentOverlay.setLayoutParams(layoutParams);
                });
                //anim.setDuration(lyricsTime.getLength() * 4);
 			  	//int time_offset = (int)((60.0f / (lyricsTime.getBpm() * 120.0f)) * 1000);
             	//System.out.println("~~~~ getBpm : " + lyricsTime.getBpm() + " / time_offset : " + time_offset);
				//anim.setDuration(lyricsTime.getLength() * time_offset);
				//System.out.println("~~~~ lyricsTime.getTimeOffset() : " + lyricsTime.getTimeOffset());
			 	anim.setDuration(lyricsTime.getTimeOffset());
                anim.start();
            }
            animationStarted = true;
        }
    }
}
