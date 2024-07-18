package kr.keumyoung.mukin.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.ShareItem;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  on 20/01/18.
 */

public class ShareViewHolder extends RecyclerView.ViewHolder {

    @Inject
    Bus bus;

    ShareItem shareItem;
    @BindView(R.id.item_icon)
    ImageView itemIcon;
    @BindView(R.id.item_text)
    TextView itemText;
    @BindView(R.id.item_anchor_ripple)
    RippleView itemAnchorRipple;

    public ShareViewHolder(View itemView) {
        super(itemView);

//        R.layout.item_share
        ButterKnife.bind(this,itemView);
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public void setData(ShareItem shareItem) {
        this.shareItem = shareItem;

        itemText.setText(shareItem.getName());
        itemIcon.setImageResource(shareItem.getIcon());
    }

    @OnClick(R.id.item_anchor_ripple)
    public void onViewClicked() {
        itemAnchorRipple.setOnRippleCompleteListener(rippleView -> bus.post(shareItem));
    }
}
