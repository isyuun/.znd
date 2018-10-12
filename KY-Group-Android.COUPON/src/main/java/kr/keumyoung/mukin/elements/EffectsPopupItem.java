package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;

/**
 *  on 16/01/18.
 */

public class EffectsPopupItem extends LinearLayout {

    @BindView(R.id.item_icon)
    ImageView itemIcon;
    @BindView(R.id.item_frame)
    FrameLayout itemFrame;
    @BindView(R.id.item_text)
    TextView itemText;
    @BindView(R.id.item_anchor)
    LinearLayout itemAnchor;

    @Inject
    Bus bus;

    private Context context;

    private int icon, activeIcon, text;

    public enum SelectionState {
        SELECTED, NOT_SELECTED
    }

    SelectionState selectionState;

    EffectsPopup.EffectOptions effectOptions;

    public EffectsPopupItem(Context context, int icon, int activeIcon, int text, EffectsPopup.EffectOptions effectOptions) {
        super(context);
        this.effectOptions = effectOptions;
        this.context = context;
        this.icon = icon;
        this.activeIcon = activeIcon;
        this.text = text;
        selectionState = effectOptions == EffectsPopup.EffectOptions.NONE ? SelectionState.SELECTED : SelectionState.NOT_SELECTED;
        inflate(context, R.layout.effects_popup_item, this);
        initiate();
    }

    private void initiate() {
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this);

        itemText.setText(text);

        updateViewWithState();
    }

    private void updateViewWithState() {
        switch (selectionState) {
            case SELECTED:
                itemIcon.setImageResource(activeIcon);
                itemText.setTextColor(Color.WHITE);
                itemFrame.setBackground(context.getResources().getDrawable(R.drawable.effect_on_bg));
                break;
            case NOT_SELECTED:
                itemIcon.setImageResource(icon);
                itemText.setTextColor(context.getResources().getColor(R.color.thirty_white));
                itemFrame.setBackground(context.getResources().getDrawable(R.drawable.effect_off_bg));
                break;
        }
    }

    public void updateSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState;
        updateViewWithState();
    }

    public SelectionState getSelectionState() {
        return this.selectionState;
    }

    @OnClick(R.id.item_anchor)
    public void onViewClicked() {
        selectionState = invertState();
        bus.post(new EffectPopupAction(effectOptions, selectionState));
    }

    private SelectionState invertState() {
        switch (selectionState) {
            case SELECTED:
            default:
                return SelectionState.NOT_SELECTED;
            case NOT_SELECTED:
                return SelectionState.SELECTED;
        }
    }
}
