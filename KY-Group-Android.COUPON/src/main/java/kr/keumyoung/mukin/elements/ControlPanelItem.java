package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;

/**
 *  on 15/01/18.
 */

public class ControlPanelItem extends LinearLayout {
    @BindView(R.id.item_icon)
    ImageView itemIcon;
    @BindView(R.id.item_text)
    TextView itemText;
    @BindView(R.id.item_anchor)
    LinearLayout itemAnchor;
    @BindView(R.id.item_anchor_ripple)
    RippleView itemAnchorRipple;

    private int icon, text;

    private Context context;

    private ControlPanel.ControlPanelOption panelOption;
    private SelectionState selectionState = SelectionState.NOT_SELECTED;

    @Inject
    Bus bus;

    //dsjung add disable status
    public enum SelectionState {
        SELECTED, NOT_SELECTED, DISABLE
    }

    public ControlPanelItem(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.control_panel_option, this);
        initiateView();
    }

    public ControlPanelItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.control_panel_option, this);
        initiateView();
    }

    public ControlPanelItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.control_panel_option, this);
        initiateView();
    }

    private void initiateView() {
        if (isInEditMode()) return;
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this);
    }

    public void initiate(ControlPanel.ControlPanelOption option) {
        panelOption = option;
        switch (option) {
            case FAVORITE:
                //icon = R.drawable.effect_off_icon;
                //text = R.string.effects;
                icon = R.drawable.favorite_off_icon;
                text = R.string.favorites;
                break;
            case TEMPO:
                icon = R.drawable.tempo_off_icon;
                text = R.string.tempo;
                break;
            case MODE:
                icon = R.drawable.mode_off_icon;
                text = R.string.mode;
                break;
            case PITCH:
                icon = R.drawable.pinch_off_icon;
                text = R.string.pitch;
                break;
        }

        itemIcon.setImageResource(icon);
        itemText.setText(text);

        updateSelectionWithState(SelectionState.NOT_SELECTED);
    }

    public SelectionState getSelectionState() {return  selectionState;}

    public void updateSelectionWithState(SelectionState selectionState) {

        this.selectionState = selectionState;
        switch (selectionState) {
            case SELECTED:
                itemText.setTextColor(Color.WHITE);
                itemIcon.setColorFilter(Color.WHITE);
                itemText.setAlpha(1f);
                itemIcon.setAlpha(1f);
                break;
            case NOT_SELECTED:
                int color = context.getResources().getColor(R.color.control_panel_off_icon_tint);
                itemText.setTextColor(color);
                itemIcon.setColorFilter(color);
                itemText.setAlpha(1f);
                itemIcon.setAlpha(1f);
                break;
            case DISABLE:
                color = context.getResources().getColor(R.color.control_panel_disable_icon_tint);
                itemText.setAlpha(0.2f);
                itemIcon.setAlpha(0.2f);
                break;
        }
    }

    public void setImageResource(int resId) {
        itemIcon.setImageResource(resId);
    }

    @OnClick(R.id.item_anchor_ripple)
    public void onViewClicked() {
        bus.post(new ControlPanelItemAction(panelOption, invert(selectionState)));
    }

    private SelectionState invert(SelectionState selectionState) {
        switch (selectionState) {
            case DISABLE:
                 return SelectionState.DISABLE;
            case SELECTED:
            default:
                return SelectionState.NOT_SELECTED;
            case NOT_SELECTED:
                return SelectionState.SELECTED;
        }
    }
}
