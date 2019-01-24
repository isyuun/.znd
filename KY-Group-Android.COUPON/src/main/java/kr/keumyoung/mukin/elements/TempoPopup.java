package kr.keumyoung.mukin.elements;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.bubbleseekbar.BubbleSeekBar;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity._BaseActivity;

/**
 * on 16/01/18.
 */

public class TempoPopup extends ControlsPopup implements BubbleSeekBar.OnProgressChangedListener {

    @BindView(R.id.seekbar)
    BubbleSeekBar seekbar;

    @Inject
    Bus bus;

    _BaseActivity instance;
    @BindView(R.id.left_arrow_button)
    ImageView leftArrowButton;
    @BindView(R.id.right_arrow_button)
    ImageView rightArrowButton;
    @BindView(R.id.left_arrow_button_ripple)
    View leftArrowButtonRipple;
    @BindView(R.id.right_arrow_button_ripple)
    View rightArrowButtonRipple;

    public TempoPopup(_BaseActivity activity) {
        super(activity);
        instance = activity;

        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @Override
    protected View getContainerView(FrameLayout parent) {
        View view = LayoutInflater.from(activity).inflate(R.layout.tempo_popup, parent, false);
        ButterKnife.bind(this, view);

        seekbar.setProgress(getMiddleValue());
        seekbar.setOnProgressChangedListener(this);
        return view;
    }

    public int getMiddleValue() {
        return getMaxValue() / 2;
    }

    public void updatePresetValue(int value) {
        seekbar.setProgress(value);
    }

    @Override
    protected int getPopupHeading() {
        return R.string.adjust_tempo;
    }

    @Override
    protected void onPopupClose() {
        activity.onPopupClose();
    }

    @Override
    public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
    }

    @Override
    public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
        if (BuildConfig.DEBUG) Log.e("TempoPopup", "progress:" + progress + ", progressFloat:" + progressFloat + ", fromUser:" + fromUser);
        if (fromUser) {
            int paramValue = progress;
            if (instance != null) {
                instance.tempo(paramValue);
            }
        }
    }

    public int getMaxValue() {
        return (int) seekbar.getMax();
    }


    @OnClick({R.id.left_arrow_button_ripple, R.id.right_arrow_button_ripple})
    public void onViewClicked(View view) {
        int currentValue = seekbar.getProgress();
        //if (BuildConfig.DEBUG) Log.e("TempoPopup", "onViewClicked()" + currentValue);
        switch (view.getId()) {
            case R.id.left_arrow_button_ripple:
                if (currentValue == seekbar.getMin())
                    return;
                else {
                    int newValue = currentValue - 1;
                    seekbar.setProgress(newValue);
                    getProgressOnFinally(seekbar, newValue, newValue, true);
                }
                break;
            case R.id.right_arrow_button_ripple:
                if (currentValue == seekbar.getMax()) {
                    return;
                } else {
                    int newValue = currentValue + 1;
                    seekbar.setProgress(newValue);
                    getProgressOnFinally(seekbar, newValue, newValue, true);
                }
                break;

        }
    }
}
