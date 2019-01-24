package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;
import kr.keumyoung.mukin.data.bus.ModePopupAction;
import kr.keumyoung.mukin.elements.ControlPanel;
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.EffectsPopup;
import kr.keumyoung.mukin.elements.ModePopup;
import kr.keumyoung.mukin.elements.ModePopupItem;
import kr.keumyoung.mukin.elements.OperationPopup;
import kr.keumyoung.mukin.elements.PitchPopup;
import kr.keumyoung.mukin.elements.TempoPopup;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;

import static kr.keumyoung.mukin.elements.ControlPanelPlay.PlayButtonState.INIT;

public class HomeActivity3 extends HomeActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.controls_popup)
    RelativeLayout controlsPopup;

    @BindView(R.id.control_panel_component)
    ControlPanel controlPanelComponent;

    @BindView(R.id.controls_panel)
    RelativeLayout controlsPanel;

    EffectsPopup effectsPopup;
    ModePopup modePopup;
    TempoPopup tempoPopup;
    PitchPopup pitchPopup;
    OperationPopup operationPopup;
    ControlPanelItemAction lastAction;

    @Subscribe
    protected void updateViewWithState(ControlPanelPlay.PlayButtonState buttonState) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + buttonState);
        switch (buttonState) {
            case INIT:
                break;
            case PLAY:
                play();
                break;
            case PAUSE:
                break;
            case FINISHED:
                break;
        }
        controlPanelComponent.updatePlayButtonWithState(INIT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preset();
    }

    private void preset() {
        // update pitch based on song gender
        if (modePopup == null) modePopup = new ModePopup(this);
        ModePopupAction modePopupActionSettings = new ModePopupAction(
                //song.getGender().equalsIgnoreCase(Constants.MALE) ? ModePopup.ModeOptions.MALE : ModePopup.ModeOptions.FEMALE,
                ModePopup.ModeOptions.MALE,
                ModePopupItem.SelectionState.SELECTED
        );
        modePopup.onSelectionEffectItem(modePopupActionSettings);
        onSelectionModeItem(modePopupActionSettings);

        // update preset values
        int tempoValue = tempo();
        //if (playerJNI != null) playerJNI.SetSpeedControl(tempoValue);
        if (tempoPopup == null) tempoPopup = new TempoPopup(this);
        tempoPopup.updatePresetValue(tempoValue);

        int pitchValue = pitch();
        //if (playerJNI != null) playerJNI.SetKeyControl(pitchValue);
        if (pitchPopup == null) pitchPopup = new PitchPopup(this);
        pitchPopup.updatePresetValue(pitchValue);

        int presetSongGender = preferenceHelper.getInt(PreferenceKeys.SONG_GENDER, -1);
        if (presetSongGender != -1) {
            ModePopupAction modePopupAction = new ModePopupAction(
                    presetSongGender == 0 ? ModePopup.ModeOptions.MALE : ModePopup.ModeOptions.FEMALE,
                    ModePopupItem.SelectionState.SELECTED
            );
            modePopup.onSelectionEffectItem(modePopupAction);
            onSelectionModeItem(modePopupAction);
        }
    }

    @Override
    protected void onPause() {
        onPopupClose();
        super.onPause();
    }

    @Override
    public void onPopupClose() {
        controlPanelComponent.deselectAllPanels();
        controlsPopup.setVisibility(View.GONE);
        lastAction = null;
    }

    @Subscribe
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":playerOperation:" + playerOperation);
    }

    @Subscribe
    public void updateViewWithPanelOptions(ControlPanelItemAction action) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action.getPanelOption());
        //toastHelper.showError(R.string.in_playing_work);
        ////if (controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.INIT
        ////        || controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.PAUSE)
        ////    return;
        //
        //boolean hide = (lastAction != null && lastAction.getPanelOption() == action.getPanelOption() && controlsPopup.getVisibility() == View.VISIBLE);
        //lastAction = action;
        //
        ////if (BuildConfig.DEBUG) Log.e(TAG, "updateViewWithPanelOptions(...)" + ":" + hide + ":" + controlsPopup.getVisibility());
        //if (hide) {
        //    controlsPopup.setVisibility(View.GONE);
        //    return;
        //}
        //
        //switch (action.getPanelOption()) {
        //    case FAVORITE:
        //        //마이크
        //        //toastHelper.showError(R.string.mic_not_working);
        //        //if (isPossibleRecord() == false) {
        //        //    toastHelper.showError(R.string.error_miceffect_support);
        //        //    break;
        //        //}
        //        //if (effectsPopup == null) effectsPopup = new EffectsPopup(this);
        //        //controlsPopup.removeAllViews();
        //        //controlsPopup.addView(effectsPopup.getView());
        //        //controlsPopup.setVisibility(visibility);
        //        //toastHelper.showError(R.string.in_playing_work);
        //        ///**
        //        // * 애창곡
        //        // */
        //        //if (!isFavorites(song.getSongId())) {
        //        //    addFavoriteSong(song);
        //        //} else {
        //        //    delFavoriteSong(song);
        //        //}
        //        break;
        //
        //    case MODE:
        //        //toastHelper.showError(R.string.in_playing_work);
        //        //if (modePopup == null) modePopup = new ModePopup(this);
        //        //controlsPopup.removeAllViews();
        //        //controlsPopup.addView(modePopup.getView());
        //        //controlsPopup.setVisibility(View.VISIBLE);
        //        break;
        //
        //    case PITCH:
        //        if (pitchPopup == null) pitchPopup = new PitchPopup(this);
        //        controlsPopup.removeAllViews();
        //        controlsPopup.addView(pitchPopup.getView());
        //        controlsPopup.setVisibility(View.VISIBLE);
        //        pitchPopup.updatePresetValue(pitch());
        //        break;
        //
        //    case TEMPO:
        //        if (tempoPopup == null) tempoPopup = new TempoPopup(this);
        //        controlsPopup.removeAllViews();
        //        controlsPopup.addView(tempoPopup.getView());
        //        controlsPopup.setVisibility(View.VISIBLE);
        //        tempoPopup.updatePresetValue(tempo());
        //        break;
        //}
    }

    @Subscribe
    public void onSelectionModeItem(ModePopupAction action) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
    }

    @Subscribe
    public void onSelectionEffectItem(EffectPopupAction action) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
    }
}
