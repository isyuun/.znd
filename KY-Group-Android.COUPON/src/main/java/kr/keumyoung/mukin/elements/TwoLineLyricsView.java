package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.lyrics.LyricsTime;
import kr.keumyoung.mukin.helper.LyricsTimingHelper;

public class TwoLineLyricsView extends LinearLayout {
    Context context;
    @BindView(R.id.lyric_text_top)
    TextView lyricTextTop;
    @BindView(R.id.lyric_layout_top)
    LyricsLayout lyricLayoutTop;
    @BindView(R.id.lyric_frame_top)
    FrameLayout lyricFrameTop;
    @BindView(R.id.lyric_text_bottom)
    TextView lyricTextBottom;
    @BindView(R.id.lyric_layout_bottom)
    LyricsLayout lyricLayoutBottom;
    @BindView(R.id.lyric_frame_bottom)
    FrameLayout lyricFrameBottom;
    @BindView(R.id.count_text_view)
    TextView countTextView;
    boolean isLyricsStarted = false;
    private int currentLine = 0;
    private int noteCount = 0;

    public TwoLineLyricsView(Context context) {
        super(context);
        init(context);
    }

    public TwoLineLyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TwoLineLyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        inflate(this.context, R.layout.element_two_line_lyrics, this);
        ButterKnife.bind(this);
    }

    public void update(LyricsTime object) {

        if (!object.isCountDownItem()) {
            if (!isLyricsStarted) {
				if(countTextView.getVisibility() == VISIBLE)
					countTextView.setVisibility(INVISIBLE);
                if (object.getLineNumber() == 0) {
                    lyricTextTop.setVisibility(GONE);
                }
            }

            isLyricsStarted = true;

            boolean isLineChange;
            isLineChange = currentLine != object.getLineNumber();

            currentLine = object.getLineNumber();
            if (isLineChange) {
                if (currentLine % 2 == 0) {
                    // select top layout for animation
                    lyricLayoutTop.setVisibility(VISIBLE);
                    lyricLayoutBottom.setVisibility(GONE);
					if(object.getNextLine().length() != 0)
	                    lyricTextTop.setVisibility(GONE);
                    lyricTextBottom.setVisibility(VISIBLE);
                    lyricTextBottom.setText(object.getNextLine());
                } else {
                    // select bottom layout for animation
                    lyricLayoutBottom.setVisibility(VISIBLE);
					if(object.getNextLine().length() != 0)
                  	  lyricLayoutTop.setVisibility(GONE);
                    lyricTextBottom.setVisibility(GONE);
                    lyricTextTop.setVisibility(VISIBLE);
                    lyricTextTop.setText(object.getNextLine());
                }
            }

            if (currentLine % 2 == 0) lyricLayoutTop.update(object);
            else lyricLayoutBottom.update(object);
        } else {
            lyricTextTop.setVisibility(VISIBLE);
            lyricTextTop.setText(object.getCurrentLine());
            lyricTextBottom.setVisibility(VISIBLE);
            lyricTextBottom.setText(object.getNextLine());

			processCountDown(noteCount, object.getCountDelayTime());
			noteCount++;
        }
    }

    private void processCountDown(int count, int delay_time) {
        if (count <= 4) {
            countTextView.setVisibility(VISIBLE);
            countTextView.setText(Integer.toString(4 - count));
            switch (4 - count) {
                case 4:
                    countTextView.setBackgroundResource(R.drawable.count_bg_blue);
                    countTextView.setTextColor(Color.BLACK);
                    break;
                case 3:
                    countTextView.setBackgroundResource(R.drawable.count_bg_green);
                    countTextView.setTextColor(Color.BLACK);
                    break;
                case 2:
                    countTextView.setBackgroundResource(R.drawable.count_bg_yellow);
                    countTextView.setTextColor(Color.BLACK);
                    break;
                case 1:
                    countTextView.setBackgroundResource(R.drawable.count_bg_red);
                    countTextView.setTextColor(Color.WHITE);
                    break;
                case 0:
                    countTextView.setBackgroundResource(R.drawable.count_bg_red_dark);
                    countTextView.setTextColor(Color.WHITE);
                    break;
            }
        }
    }

    public void clear() {
        lyricLayoutBottom.removeAllViews();
        lyricLayoutTop.removeAllViews();
        lyricTextBottom.setText("");
        lyricTextTop.setText("");
        isLyricsStarted = false;
        currentLine = 0;
        noteCount = 0;
        countTextView.setVisibility(INVISIBLE);
    }
}
