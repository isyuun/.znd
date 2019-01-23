package kr.keumyoung.mukin.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.activity.PlayerActivity;

/**
 *  on 16/01/18.
 */

public class OperationPopup extends ControlsPopup {

    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;

    public enum PlayerOperation {
        RESUME, FINISH, RESTART, NEXT
    }

    public OperationPopup(PlayerActivity activity) {
        super(activity);
        hidePopupHeader();
    }

    @Override
    protected View getContainerView(FrameLayout parent) {
        View view = LayoutInflater.from(activity).inflate(R.layout.effects_popup, parent, false);
        ButterKnife.bind(this, view);
        initiateChildren();
        return view;
    }

    private void initiateChildren() {
        parentLayout.removeAllViews();
        parentLayout.addView(createElementForPopup(PlayerOperation.NEXT));
        //parentLayout.addView(createElementForPopup(PlayerOperation.FINISH));
        parentLayout.addView(createElementForPopup(PlayerOperation.RESTART));

    }

    public void enableElementForPopup(PlayerOperation operation, boolean bEnable)
    {
        for(int i = 0 ; i < parentLayout.getChildCount(); i++)
        {
            OperationPopupItem itemView = (OperationPopupItem) parentLayout.getChildAt(i);
            if(itemView != null && itemView.playerOperation == operation) {
                itemView.setEnabled(bEnable);
                break;
            }
        }
    }

    private OperationPopupItem createElementForPopup(PlayerOperation operation) {
        OperationPopupItem popupItem;
        switch (operation) {
            case NEXT:
            default:
                popupItem = new OperationPopupItem(activity, R.drawable.resume_01_icon, R.string.reserve_next, operation);
                break;
            case FINISH:
                popupItem = new OperationPopupItem(activity, R.drawable.finish_01_icon, R.string.finish, operation);
                break;
            case RESTART:
                popupItem = new OperationPopupItem(activity, R.drawable.restart_01_icon, R.string.restart, operation);
                break;
        }

        int margin = activity.getResources().getDimensionPixelSize(R.dimen.sixteen_dp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        if (operation != PlayerOperation.RESTART) params.setMargins(0, 0, margin, 0);

        popupItem.setLayoutParams(params);

        return popupItem;
    }

    @Override
    protected int getPopupHeading() {
        return R.string.adjust_tempo;
    }

    @Override
    protected void onPopupClose() {

    }
}
