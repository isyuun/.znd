package kr.keumyoung.mukin.elements;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity._BaseActivity;
import kr.keumyoung.mukin.helper.AnimationHelper;

/**
 * on 16/01/18.
 */

public abstract class ControlsPopup {

    _BaseActivity activity;
    View view;
    ViewHolder viewHolder;

    @Inject
    AnimationHelper animationHelper;

    public ControlsPopup(_BaseActivity activity) {
        this.activity = activity;
        view = LayoutInflater.from(activity).inflate(R.layout.control_popup, null, false);
        viewHolder = new ViewHolder(view, this);
        MainApplication.getInstance().getMainComponent().inject(this);
        initiate();
    }

    public void initiate() {
        viewHolder.popupHeading.setText(getPopupHeading());
        viewHolder.popupContainerFrame.removeAllViews();
        View childView = getContainerView(viewHolder.popupContainerFrame);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        childView.setLayoutParams(params);
        viewHolder.popupContainerFrame.addView(childView);
    }

    public void hidePopupHeader() {
        viewHolder.popupHeader.setVisibility(View.INVISIBLE);
    }

    protected abstract View getContainerView(FrameLayout parent);

    protected abstract int getPopupHeading();

    protected abstract void onPopupClose();

    public View getView() {
        return view;
    }

    /*static */class ViewHolder {
        @BindView(R.id.popup_heading)
        TextView popupHeading;
        @BindView(R.id.close_button)
        TextView closeButton;
        @BindView(R.id.popup_container_frame)
        FrameLayout popupContainerFrame;
        @BindView(R.id.popup_header)
        RelativeLayout popupHeader;

        ControlsPopup popup;

        public ViewHolder(View view, ControlsPopup controlsPopup) {
            ButterKnife.bind(this, view);
            popup = controlsPopup;
        }

        @OnClick(R.id.close_button)
        public void onViewClicked() {
            popup.onPopupClose();
        }
    }
}
