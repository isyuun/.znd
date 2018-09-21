package kr.keumyoung.mukin.elements;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if(enabled == false)
        {
            itemText.setAlpha(0.2f);
            itemIcon.setAlpha(0.2f);
            itemFrame.setAlpha(0.2f);
        }else
        {
            itemText.setAlpha(1f);
            itemIcon.setAlpha(1f);
            itemFrame.setAlpha(1f);
        }

    }

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
        //itemFrame.setBackground(context.getResources().getDrawable(R.drawable.effect_on_bg));
        itemFrame.setBackground(context.getResources().getDrawable(R.drawable.boarder_popup_item_off));

        itemFrame.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        itemFrame.setBackground(context.getResources().getDrawable(R.drawable.boarder_popup_item_on));
                        Log.d("TTTTT", "ACTION_DOWN");
                        break;
                    }
                }
                return false;
            }
        });
    }

    @OnClick(R.id.item_anchor)
    public void onViewClicked() {
        if(isEnabled()) {
            bus.post(playerOperation);
            itemFrame.setBackground(context.getResources().getDrawable(R.drawable.boarder_popup_item_off));
        }
    }




}
