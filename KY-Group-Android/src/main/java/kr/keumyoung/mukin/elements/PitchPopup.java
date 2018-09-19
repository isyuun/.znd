package kr.keumyoung.mukin.elements;

import android.widget.SeekBar;

import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.data.PlayerLog;
import kr.keumyoung.mukin.util.PreferenceKeys;

/**
 * on 16/01/18.
 */

public class PitchPopup extends TempoPopup {

    public PitchPopup(PlayerActivity activity) {
        super(activity);
    }

    @Override
    protected int getPopupHeading() {
        return R.string.adjust_pitch;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
            int paramValue = value - 6;
            seekValue.setText(String.format("%sx", String.valueOf(paramValue)));

            instance.getPlayerJNI().SetKeyControl(paramValue);

            instance.getPreferenceHelper().saveInt(PreferenceKeys.PITCH_VALUE, paramValue);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int value = seekBar.getProgress();
        int pitchValue = Math.round(value / 5f) - 10;
        bus.post(new PlayerLog(PlayerLog.LogType.PITCH, pitchValue));
    }

    @Override
    public int getMaxValue() {
        return 12;
    }

    public int getMiddleValue() {
        return 6;
    }
}
