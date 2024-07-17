package kr.keumyoung.mukin.elements;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.lyrics.LyricsTime;

/**
 * on 28/03/18.
 * Project: KyGroup
 */

public class LyricsView extends LinearLayout {

    @BindView(R.id.song_lyrics_prev)
    TextView songLyricsPrev;
    @BindView(R.id.song_lyrics_next)
    TextView songLyricsNext;
    @BindView(R.id.song_lyrics_layout)
    LinearLayout songLyricsLayout;
    @BindView(R.id.lyric_view)
    LinearLayout lyricLayout;
    @BindView(R.id.init_text_view)
    TextView initTextView;

    int lineNumber, prevLineNumber;
    ValueAnimator anim;

    public LyricsView(Context context) {
        super(context);
        inflate(context, R.layout.view_lyrics, this);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_lyrics, this);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_lyrics, this);
        init();
    }

    private void init() {
        if (isInEditMode()) return;

        setGravity(Gravity.CENTER);

        ButterKnife.bind(this);
    }

    public void update(LyricsTime object) {

//        lineNumber = object.getLineNumber();
//        if (prevLineNumber == -1) {
//            prevLineNumber = lineNumber;
//        }

        if (object.getType() == 0) {
            initTextView.setVisibility(VISIBLE);
            lyricLayout.setVisibility(GONE);
            initTextView.setText(object.getCurrentLine());
        } else {
            initTextView.setVisibility(GONE);
            lyricLayout.setVisibility(VISIBLE);

            songLyricsPrev.setText(object.getPreviousLine());
            songLyricsNext.setText(object.getNextLine());

            lyricLayout.removeAllViews();
            int currentIndex = object.getWordIndex();
            for (int index = 0; index < object.getWords().size(); index++) {
                LyricView lyricView;
                if (index < currentIndex)
                    lyricView = new LyricView(getContext(), object, object.getWords().get(index), LyricView.State.PREVIOUS);
                else if (index == currentIndex)
                    lyricView = new LyricView(getContext(), object, object.getCurrentWord(), LyricView.State.CURRENT);
                else
                    lyricView = new LyricView(getContext(), object, object.getWords().get(index), LyricView.State.NEXT);

                lyricLayout.addView(lyricView);
            }
        }
    }

    public void pause() {
        if (Build.VERSION.SDK_INT >= 19) anim.pause();
    }

    public void resume() {
        if (Build.VERSION.SDK_INT >= 19) anim.resume();
    }

    public void clear() {
        initTextView.setText("");
        songLyricsNext.setText("");
        songLyricsPrev.setText("");
        lyricLayout.removeAllViews();
    }
}
