package kr.keumyoung.mukin.elements;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.helper.AnimationHelper;

/**
 *  on 15/01/18.
 */

public class ControlPanelPlay extends LinearLayout {

    public enum PlayButtonState {
        INIT, PLAY, PAUSE, RESUME, FINISHED
    }

    @BindView(R.id.play_bg)
    ImageView playBg;
    @BindView(R.id.play_icon)
    ImageView playIcon;
    @BindView(R.id.play_base)
    FrameLayout playBase;
    @BindView(R.id.play_anchor)
    LinearLayout playAnchor;

    Context context;
    PlayButtonState playButtonState;

    @Inject
    Bus bus;

    @Inject
    AnimationHelper animationHelper;

    public ControlPanelPlay(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.control_panel_play, this);
        initiateView();
    }

    public ControlPanelPlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.control_panel_play, this);
        initiateView();
    }

    public ControlPanelPlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.control_panel_play, this);
        initiateView();
    }

    private void initiateView() {
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this);
        updateViewWithState(PlayButtonState.INIT);
    }

    public void updateViewWithState(PlayButtonState buttonState) {
        playButtonState = buttonState;
        switch (buttonState) {
            case INIT:
            case FINISHED:
                playBg.setImageResource(R.drawable.play_base_02_icon);
                playIcon.setImageResource(R.drawable.play_icon);
                playBg.clearAnimation();
                break;
            case PLAY:
            case RESUME:
                playIcon.setImageResource(R.drawable.pause_icon);
                playBg.setImageResource(R.drawable.play_stroke_02_icon);
                animationHelper.rotateInfinite(playBg);
                break;
            case PAUSE:
                playBg.setImageResource(R.drawable.play_stroke_02_icon);
                playIcon.setImageResource(R.drawable.play_icon);
                playBg.clearAnimation();
                break;
        }
    }

    @OnClick(R.id.play_anchor_ripple)
    public void onViewClicked() {
        updateViewWithState(getNextState());
        bus.post(playButtonState);
    }

    private PlayButtonState getNextState() {
        switch (playButtonState) {
            case INIT:
                return PlayButtonState.PLAY;
            case PAUSE:
                return PlayButtonState.RESUME;
            case PLAY:
            case RESUME:
                return PlayButtonState.PAUSE;
            case FINISHED:
            default:
                return PlayButtonState.FINISHED;
        }
    }

    public PlayButtonState getPlayButtonState() {
        return playButtonState;
    }
}
