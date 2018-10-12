package kr.keumyoung.mukin.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;

/**
 *  on 16/01/18.
 */

public class EffectsPopup extends ControlsPopup {

    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;

    @Inject
    Bus bus;

    public enum EffectOptions {
        NONE, ECHO, REVERV, DELAY
    }

    Map<EffectOptions, EffectsPopupItem> popupItemMap;

    public EffectsPopup(PlayerActivity activity) {
        super(activity);
        MainApplication.getInstance().getMainComponent().inject(this);
        bus.register(this);
    }

    @Override
    protected View getContainerView(FrameLayout parent) {
        View view = LayoutInflater.from(activity).inflate(R.layout.effects_popup, parent, false);
        ButterKnife.bind(this, view);
        initiateChildren();
        return view;
    }

    private void initiateChildren() {
        if (popupItemMap == null) popupItemMap = new HashMap<>();
        parentLayout.removeAllViews();
        parentLayout.addView(createElementForPopup(EffectOptions.NONE));
        parentLayout.addView(createElementForPopup(EffectOptions.ECHO));
        parentLayout.addView(createElementForPopup(EffectOptions.REVERV));
        parentLayout.addView(createElementForPopup(EffectOptions.DELAY));
    }

    @Subscribe
    public void onSelectionEffectItem(EffectPopupAction action) {
        if (action.getEffectOptions() == EffectOptions.NONE && action.getSelectionState() == EffectsPopupItem.SelectionState.SELECTED) {
            for (EffectOptions effectOptions : popupItemMap.keySet()) {
                if (effectOptions == EffectOptions.NONE)
                    popupItemMap.get(effectOptions).updateSelectionState(EffectsPopupItem.SelectionState.SELECTED);
                else
                    popupItemMap.get(effectOptions).updateSelectionState(EffectsPopupItem.SelectionState.NOT_SELECTED);
            }
        } else if (action.getEffectOptions() != EffectOptions.NONE) {
            for (EffectOptions effectOptions : popupItemMap.keySet()) {
                if (effectOptions == EffectOptions.NONE && action.getSelectionState() == EffectsPopupItem.SelectionState.SELECTED)
                    popupItemMap.get(effectOptions).updateSelectionState(EffectsPopupItem.SelectionState.NOT_SELECTED);
                else if (effectOptions == action.getEffectOptions())
                    popupItemMap.get(effectOptions).updateSelectionState(action.getSelectionState());
            }
        }

        boolean anySelected = false;
        for (EffectOptions effectOptions : popupItemMap.keySet()) {
            if (effectOptions != EffectOptions.NONE) {
                if (popupItemMap.get(effectOptions).getSelectionState() == EffectsPopupItem.SelectionState.SELECTED) {
                    anySelected = true;
                    break;
                }
            }
        }
        if (!anySelected)
            popupItemMap.get(EffectOptions.NONE).updateSelectionState(EffectsPopupItem.SelectionState.SELECTED);
    }

    public void onDestroy() {
        bus.unregister(this);
    }

    private View createElementForPopup(EffectOptions options) {
        EffectsPopupItem popupItem;
        switch (options) {
            case NONE:
            default:
                popupItem = new EffectsPopupItem(activity, R.drawable.none_off_icon, R.drawable.none_on_icon, R.string.none, options);
                break;
            case ECHO:
                popupItem = new EffectsPopupItem(activity, R.drawable.echo_off_icon, R.drawable.echo_on_icon, R.string.echo, options);
                break;
            case REVERV:
                popupItem = new EffectsPopupItem(activity, R.drawable.reverv_off_icon, R.drawable.reverv_on_icon, R.string.reverb, options);
                break;
            case DELAY:
                popupItem = new EffectsPopupItem(activity, R.drawable.delay_off_icon, R.drawable.delay_on_icon, R.string.delay, options);
                break;
        }

        int margin = activity.getResources().getDimensionPixelSize(R.dimen.sixteen_dp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        if (options != EffectOptions.DELAY) params.setMargins(0, 0, margin, 0);

        popupItem.setLayoutParams(params);

        popupItemMap.put(options, popupItem);

        return popupItem;
    }

    @Override
    protected int getPopupHeading() {
        return R.string.choose_an_effect;
    }

    @Override
    protected void onPopupClose() {
        activity.onPopupClose();
    }
}
