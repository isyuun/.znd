package kr.keumyoung.mukin.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.bubbleseekbar.BubbleSeekBar;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.util.PreferenceKeys;

/**
 * on 16/01/18.
 */

public class TempoPopup extends ControlsPopup implements BubbleSeekBar.OnProgressChangedListener {

    @BindView(R.id.seekbar)
    BubbleSeekBar seekbar;
    @BindView(R.id.seek_value)
    TextView seekValue;

    @Inject
    Bus bus;

    PlayerActivity instance;
    @BindView(R.id.left_arrow_button)
    ImageView leftArrowButton;
    @BindView(R.id.seekbar_min_value)
    TextView seekbarMinValue;
    @BindView(R.id.seek_bar_max_value)
    TextView seekBarMaxValue;
    @BindView(R.id.right_arrow_button)
    ImageView rightArrowButton;
    @BindView(R.id.left_arrow_button_ripple)
    RippleView leftArrowButtonRipple;
    @BindView(R.id.right_arrow_button_ripple)
    RippleView rightArrowButtonRipple;

    public TempoPopup(PlayerActivity activity) {
        super(activity);
        instance = activity;

        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @Override
    protected View getContainerView(FrameLayout parent) {
        View view = LayoutInflater.from(activity).inflate(R.layout.tempo_popup, parent, false);
        ButterKnife.bind(this, view);

        int maxValue = getMaxValue();
        //seekbar.setMax(maxValue);
        seekbar.setProgress(getMiddleValue());
        seekBarMaxValue.setText(String.format("%sx", String.valueOf(maxValue - getMiddleValue())));
        seekbarMinValue.setText(String.format("%sx", String.valueOf(getMiddleValue() - maxValue)));

        //seekbar.setOnSeekBarChangeListener(this);
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
        if (fromUser) {
            int paramValue = progress;
            //seekValue.setText(String.format("%sx", String.valueOf(paramValue)));
            if (instance != null && instance.getPlayerJNI() != null) {
                instance.getPlayerJNI().SetSpeedControl(paramValue * 2);
                instance.getPreferenceHelper().saveInt(PreferenceKeys.TEMPO_VALUE, paramValue);
            }
        }
    }


    /*
    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
//            int paramValue = Math.round(value / 5f) - 10;
            int paramValue = value - 4;
            seekValue.setText(String.format("%sx", String.valueOf(paramValue)));

            instance.getPlayerJNI().SetSpeedControl(paramValue);

            instance.getPreferenceHelper().saveInt(PreferenceKeys.TEMPO_VALUE, paramValue);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int value = seekBar.getProgress();
        int tempoValue = Math.round(value / 5f) - 10;
        bus.post(new PlayerLog(PlayerLog.LogType.TEMPO, tempoValue));
    }
    */

    public int getMaxValue() {
        return (int) seekbar.getMax();
    }


    @OnClick({R.id.left_arrow_button_ripple, R.id.right_arrow_button_ripple})
    public void onViewClicked(View view) {
        int currentValue = seekbar.getProgress();
        switch (view.getId()) {
            case R.id.left_arrow_button_ripple:
                leftArrowButtonRipple.setOnRippleCompleteListener(rippleView -> {

                    if (currentValue == seekbar.getMin())
                        return;
                    else {
                        int newValue = currentValue - 1;
                        seekbar.setProgress(newValue);
                        //onProgressChanged(seekbar, newValue, true);
                        getProgressOnFinally(seekbar, newValue, newValue, true);
                    }
                });

                break;
            case R.id.right_arrow_button_ripple:
                rightArrowButtonRipple.setOnRippleCompleteListener(rippleView -> {
                    if (currentValue == seekbar.getMax()) {
                        return;
                    } else {
                        int newValue = currentValue + 1;
                        seekbar.setProgress(newValue);
                        //onProgressChanged(seekbar, newValue, true);
                        getProgressOnFinally(seekbar, newValue, newValue, true);
                    }
                });

        }
    }

}
