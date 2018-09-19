package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import kr.keumyoung.mukin.data.lyrics.LyricsTime;

public class LyricsLayout extends LinearLayout {
    Context context;

    public LyricsLayout(Context context) {
        super(context);
        init(context);
    }

    public LyricsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LyricsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(HORIZONTAL);
    }

    public void update(LyricsTime object) {
        removeAllViews();
        int currentIndex = object.getWordIndex();
        for (int index = 0; index < object.getWords().size(); index++) {
            LyricView lyricView;
            if (index < currentIndex)
                lyricView = new LyricView(getContext(), object, object.getWords().get(index), LyricView.State.PREVIOUS);
            else if (index == currentIndex)
                lyricView = new LyricView(getContext(), object, object.getCurrentWord(), LyricView.State.CURRENT);
            else
                lyricView = new LyricView(getContext(), object, object.getWords().get(index), LyricView.State.NEXT);

            addView(lyricView);
        }
    }
}
