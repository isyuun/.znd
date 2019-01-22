package kr.keumyoung.mukin.activity;

import android.util.Log;
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
import kr.keumyoung.mukin.elements.OperationPopup;

import static kr.keumyoung.mukin.elements.ControlPanelPlay.PlayButtonState.FINISHED;
import static kr.keumyoung.mukin.elements.ControlPanelPlay.PlayButtonState.INIT;

public class HomeActivity3 extends HomeActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.control_panel_component)
    ControlPanel controlPanelComponent;
    @BindView(R.id.controls_panel)
    RelativeLayout controlsPanel;

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

    @Subscribe
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":playerOperation:" + playerOperation);
    }

    @Subscribe
    public void updateViewWithPanelOptions(ControlPanelItemAction action) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
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
