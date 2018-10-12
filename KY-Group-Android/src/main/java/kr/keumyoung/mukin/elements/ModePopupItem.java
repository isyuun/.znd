package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.graphics.Color;
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
import kr.keumyoung.mukin.data.bus.ModePopupAction;

/**
 *  on 16/01/18.
 */

public class ModePopupItem extends LinearLayout {
    @BindView(R.id.item_icon)
    ImageView itemIcon;
    @BindView(R.id.item_text)
    TextView itemText;
    @BindView(R.id.item_anchor)
    LinearLayout itemAnchor;

    private int icon, activeIcon, text;
    Context context;

    public enum SelectionState {
        SELECTED, NOT_SELECTED
    }

    @Inject
    Bus bus;

    SelectionState selectionState;
    ModePopup.ModeOptions modeOptions;

    public ModePopupItem(Context context, int icon, int activeIcon, int text, ModePopup.ModeOptions modeOptions) {
        super(context);
        this.modeOptions = modeOptions;
        this.context = context;
        this.icon = icon;
        this.activeIcon = activeIcon;
        this.text = text;
        selectionState = modeOptions == ModePopup.ModeOptions.MALE ? SelectionState.SELECTED : SelectionState.NOT_SELECTED;
        inflate(context, R.layout.mode_popup_item, this);
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
                itemIcon.setAlpha(1f);
                //itemText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                //itemAnchor.setBackground(context.getResources().getDrawable(R.drawable.effect_on_bg));
                itemText.setTextColor(context.getResources().getColor(R.color.boarder_popup_item_stroke_on));
                itemAnchor.setBackground(context.getResources().getDrawable(R.drawable.boarder_popup_item_on));
                break;
            case NOT_SELECTED:
                itemIcon.setImageResource(icon);
                itemIcon.setAlpha(0.5f);
                //itemText.setTextColor(Color.WHITE);
                //itemAnchor.setBackground(context.getResources().getDrawable(R.drawable.effect_off_bg));
                itemText.setTextColor(context.getResources().getColor(R.color.boarder_popup_item_stroke_off));
                itemAnchor.setBackground(context.getResources().getDrawable(R.drawable.boarder_popup_item_off));
                break;
        }
    }

    public void updateSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState;
        updateViewWithState();
    }

    @OnClick(R.id.item_anchor)
    public void onViewClicked() {
        selectionState = invertState();
        bus.post(new ModePopupAction(modeOptions, selectionState));
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
