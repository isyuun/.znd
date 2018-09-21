package kr.keumyoung.mukin.popup;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.RegisterActivity;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.ImageUtils;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *  on 24/01/18.
 */

public class ImageOptionChooserPopup {

    private View view;

    private RegisterActivity activity;

    private ImageUtils imageUtils;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.camera_anchor)
    ImageView cameraAnchor;
    @BindView(R.id.gallery_anchor)
    ImageView galleryAnchor;

    public ImageOptionChooserPopup(RegisterActivity activity) {
        this.activity = activity;

        MainApplication.getInstance().getMainComponent().inject(this);

        view = LayoutInflater.from(activity).inflate(R.layout.popup_image_chooser, activity.getPopupView(), false);

        imageUtils = new ImageUtils(activity);

        ButterKnife.bind(this, view);
    }

    public View getView() {
        return view;
    }

    @OnClick({R.id.camera_anchor, R.id.gallery_anchor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.camera_anchor:
                imageUtils.openCamera();
                break;
            case R.id.gallery_anchor:
                imageUtils.openGallery();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activity.onImageOperationComplete(imageUtils.retrieve(requestCode, resultCode, data));
    }

    public MultipartBody.Part getFilePart(File file) {
        return MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
    }
}
