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

/**
 *  on 16/01/18.
 */

public class OperationPopup extends ControlsPopup {

    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;

    public enum PlayerOperation {
        RESUME, FINISH, RESTART
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
        parentLayout.addView(createElementForPopup(PlayerOperation.RESUME));
        parentLayout.addView(createElementForPopup(PlayerOperation.FINISH));
        parentLayout.addView(createElementForPopup(PlayerOperation.RESTART));
    }

    private OperationPopupItem createElementForPopup(PlayerOperation operation) {
        OperationPopupItem popupItem;
        switch (operation) {
            case RESUME:
            default:
                popupItem = new OperationPopupItem(activity, R.drawable.resume_01_icon, R.string.resume, operation);
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
