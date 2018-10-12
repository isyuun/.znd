package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static kr.keumyoung.mukin.elements.ControlPanel.ControlPanelOption.EFFECT;

/**
 *  on 15/01/18.
 */

public class ControlPanel extends LinearLayout {


    public enum ControlPanelOption {
        EFFECT, TEMPO, PITCH, MODE
    }

    ControlPanelOption controlPanelOption;

    ControlPanelPlay controlPanelPlay;

    Map<ControlPanelOption, ControlPanelItem> optionMap = new HashMap<>();
    Context context;

    @Inject
    Bus bus;

    public ControlPanel(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.control_panel, this);
        initiate();
    }

    public ControlPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.control_panel, this);
        initiate();
    }

    public ControlPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.control_panel, this);
        initiate();
    }

    private void initiate() {
        MainApplication.getInstance().getMainComponent().inject(this);

        setOrientation(HORIZONTAL);

        removeAllViews();

        createOptionForItem(EFFECT);
        createOptionForItem(ControlPanelOption.TEMPO);
        createPlayButton();
        createOptionForItem(ControlPanelOption.PITCH);
        createOptionForItem(ControlPanelOption.MODE);

        bus.register(this);
    }

    @Subscribe
    public void updateViewWithPanelOptions(ControlPanelItemAction action) {
        if (getPlayState() == ControlPanelPlay.PlayButtonState.INIT
                || getPlayState() == ControlPanelPlay.PlayButtonState.PAUSE)
            return;
        for (ControlPanelOption option : optionMap.keySet()) {

            //dsjung for disable status
            if(optionMap.get(option).getSelectionState().equals(ControlPanelItem.SelectionState.DISABLE))
                return;

            if (option.equals(action.getPanelOption()))
                optionMap.get(option).updateSelectionWithState(ControlPanelItem.SelectionState.SELECTED);
            else
                optionMap.get(option).updateSelectionWithState(ControlPanelItem.SelectionState.NOT_SELECTED);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bus.unregister(this);
    }

    private void createPlayButton() {
        controlPanelPlay = new ControlPanelPlay(context);
        LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1.5f;
        controlPanelPlay.setLayoutParams(params);

        addView(controlPanelPlay);
    }

    private void createOptionForItem(ControlPanelOption option) {
        ControlPanelItem panelItem = new ControlPanelItem(context);
        panelItem.initiate(option);
        LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1f;
        params.gravity = Gravity.CENTER;
        panelItem.setLayoutParams(params);
        optionMap.put(option, panelItem);

        addView(panelItem);
    }

    public ControlPanelPlay.PlayButtonState getPlayState() {
        return controlPanelPlay.getPlayButtonState();
    }

    public void updateOptionsWithOption(ControlPanelOption controlPanelOption) {

    }

    public void deselectAllPanels() {
        for (ControlPanelOption option : optionMap.keySet()) {
            //dsjung test
            if(optionMap.get(option).getSelectionState().equals(ControlPanelItem.SelectionState.DISABLE))
                continue;
            optionMap.get(option).updateSelectionWithState(ControlPanelItem.SelectionState.NOT_SELECTED);
        }
    }

    public void updatePlayButtonWithState(ControlPanelPlay.PlayButtonState buttonState) {
        controlPanelPlay.updateViewWithState(buttonState);
    }

    //dsjung add ����ũ ȿ�� ��Ȱ��ȭ
    public void updateEffectButtonWithState(ControlPanelItem.SelectionState buttonState) {

        for (ControlPanelOption option : optionMap.keySet()) {
            if (option.equals(EFFECT))
                optionMap.get(option).updateSelectionWithState(buttonState);
        }
    }
}
