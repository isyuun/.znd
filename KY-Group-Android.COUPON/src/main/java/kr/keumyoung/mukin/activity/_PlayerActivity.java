package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Subscribe;

import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;
import kr.keumyoung.mukin.data.bus.ModePopupAction;
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.OperationPopup;

public class _PlayerActivity extends PlayerActivity3 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private Object busEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busEventListener = new Object() {
            @Subscribe
            public void post(ControlPanelPlay.PlayButtonState buttonState) {
                updatePlayerState(buttonState);
            }

            @Subscribe
            public void post(OperationPopup.PlayerOperation playerOperation) {
                onControlOperation(playerOperation);
            }

            @Subscribe
            public void post(ControlPanelItemAction action) {
                updateViewWithPanelOptions(action);
            }

            @Subscribe
            public void post(ModePopupAction action) {
                onSelectionModeItem(action);
            }

            @Subscribe
            public void post(EffectPopupAction action) {
                onSelectionEffectItem(action);
            }
        };
    }

    @Override
    protected void onStart() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStart();
        //bus.register(this);
        bus.register(busEventListener);
    }

    @Override
    protected void onStop() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStop();
        //bus.unregister(this);
        bus.unregister(busEventListener);
    }

    //@Override
    //public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
    //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":playerOperation:" + playerOperation);
    //    super.onControlOperation(playerOperation);
    //}
    //
    //@Override
    //public void updatePlayerState(ControlPanelPlay.PlayButtonState buttonState) {
    //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":buttonState:" + buttonState);
    //    super.updatePlayerState(buttonState);
    //}
    //
    //@Override
    //public void updateViewWithPanelOptions(ControlPanelItemAction action) {
    //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
    //    super.updateViewWithPanelOptions(action);
    //}
    //
    //@Override
    //public void onSelectionModeItem(ModePopupAction action) {
    //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
    //    super.onSelectionModeItem(action);
    //}
    //
    //@Override
    //public void onSelectionEffectItem(EffectPopupAction action) {
    //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":action:" + action);
    //    super.onSelectionEffectItem(action);
    //}
}
