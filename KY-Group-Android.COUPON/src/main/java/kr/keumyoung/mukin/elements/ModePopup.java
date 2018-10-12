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
import kr.keumyoung.mukin.data.bus.ModePopupAction;

/**
 *  on 16/01/18.
 */

public class ModePopup extends ControlsPopup {

    @Inject
    Bus bus;

    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;

    public enum ModeOptions {
        MALE, FEMALE
    }

    Map<ModeOptions, ModePopupItem> popupItemMap;

    public ModePopup(PlayerActivity activity) {
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
        parentLayout.addView(createElementForPopup(ModeOptions.MALE));
        parentLayout.addView(createElementForPopup(ModeOptions.FEMALE));
    }

    private ModePopupItem createElementForPopup(ModeOptions options) {
        ModePopupItem popupItem;
        switch (options) {
            case MALE:
            default:
                popupItem = new ModePopupItem(activity, R.drawable.male_off_icon, R.drawable.male_on_icon, R.string.male, options);
                break;
            case FEMALE:
                popupItem = new ModePopupItem(activity, R.drawable.femal_off_icon, R.drawable.femal_on_icon, R.string.female, options);
                break;
        }

        int margin = activity.getResources().getDimensionPixelSize(R.dimen.sixteen_dp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        if (options != ModeOptions.FEMALE) params.setMargins(0, 0, margin, 0);

        popupItem.setLayoutParams(params);

        popupItemMap.put(options, popupItem);

        return popupItem;
    }

    @Subscribe
    public void onSelectionEffectItem(ModePopupAction action) {
        if (action.getSelectionState() == ModePopupItem.SelectionState.SELECTED) {
            for (ModeOptions modeOptions : popupItemMap.keySet()) {
                if (modeOptions == action.getModeOptions())
                    popupItemMap.get(modeOptions).updateSelectionState(ModePopupItem.SelectionState.SELECTED);
                else
                    popupItemMap.get(modeOptions).updateSelectionState(ModePopupItem.SelectionState.NOT_SELECTED);
            }
        }
    }

    public void onDestroy() {
        bus.unregister(this);
    }

    @Override
    protected int getPopupHeading() {
        return R.string.select_a_mode;
    }

    @Override
    protected void onPopupClose() {
        activity.onPopupClose();
    }
}
