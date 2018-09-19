package kr.keumyoung.mukin.elements;

import android.content.Context;
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

/**
 *  on 16/01/18.
 */

public class OperationPopupItem extends LinearLayout {
    @BindView(R.id.item_icon)
    ImageView itemIcon;
    @BindView(R.id.item_frame)
    FrameLayout itemFrame;
    @BindView(R.id.item_text)
    TextView itemText;

    private Context context;
    private int icon, text;
    OperationPopup.PlayerOperation playerOperation;

    @Inject
    Bus bus;

    public OperationPopupItem(Context context, int icon, int text, OperationPopup.PlayerOperation playerOperation) {
        super(context);
        this.context = context;
        this.playerOperation = playerOperation;
        this.icon = icon;
        this.text = text;
        inflate(context, R.layout.effects_popup_item, this);
        initiate();
    }

    private void initiate() {
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this);

        itemText.setText(text);
        itemIcon.setImageResource(icon);
        itemFrame.setBackground(context.getResources().getDrawable(R.drawable.effect_on_bg));
    }

    @OnClick(R.id.item_anchor)
    public void onViewClicked() {
        bus.post(playerOperation);
    }
}
