package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.helper.ImageUtils;
import kr.keumyoung.mukin.helper.MediaManager;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.popup.ImageOptionChooserPopup;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.PreferenceKeys;

/**
 * on 11/01/18.
 * Project: KyGroup
 */

public class RegisterActivity extends _BaseActivity {

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    @Inject
    MediaManager mediaManager;

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    ImageOptionChooserPopup imageOptionChooserPopup;

    File profileImageFile;

    @BindView(R.id.profile_image)
    FrameLayout profileImage;
    @BindView(R.id.login_anchor)
    TextView loginAnchor;
    @BindView(R.id.signup_button)
    CardView signupButton;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.nickname_et)
    EditText nicknameEt;
    @BindView(R.id.top_bg)
    ImageView topBg;
    @BindView(R.id.profile_image_ripple)
    RippleView profileImageRipple;
    @BindView(R.id.login_section)
    LinearLayout loginSection;
    @BindView(R.id.profile_image_content)
    CircleImageView profileImageContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_register_2, null, false);
        inflateContainerView(view);
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.profile_image, R.id.login_anchor, R.id.signup_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                profileImageRipple.setOnRippleCompleteListener(rippleView -> openImageChoosePopup());
                break;
            case R.id.login_anchor:
                openPreferenceLoginChoice();
                break;
            case R.id.signup_button:
                signupUser();
                break;
        }
    }

    private void openImageChoosePopup() {
        imageOptionChooserPopup = new ImageOptionChooserPopup(this);
        addPopupView(imageOptionChooserPopup.getView());
    }

    private void signupUser() {
        String email = emailEt.getText().toString();
        String nickName = nicknameEt.getText().toString();
        String password = passwordEt.getText().toString();

        if (nickName.isEmpty()) {
            toastHelper.showError(R.string.nick_name_blank_error);
            nicknameEt.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            toastHelper.showError(R.string.email_blank_error);
            emailEt.requestFocus();
            return;
        }

        if (!CommonHelper.EmailValidator(email)) {
            toastHelper.showError(R.string.email_not_valid_error);
            emailEt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            toastHelper.showError(R.string.password_blank_error);
            passwordEt.requestFocus();
            return;
        }

        if (password.length() < 8) {
            toastHelper.showError(R.string.password_eight_character);
            passwordEt.requestFocus();
            return;
        }

        showProgress();
        //if (profileImageFile != null) {
        //    //restApi.saveUploadedSong(imageOptionChooserPopup.getFilePart(profileImageFile))
        //    //        .subscribeOn(Schedulers.io())
        //    //        .observeOn(AndroidSchedulers.mainThread())
        //    //        .subscribe(s -> registerUserToDF(email, nickName, password, s), throwable -> {
        //    //            throwable.printStackTrace();
        //    //            hideProgress();
        //    //        });
        //    registerUserToDF(email, nickName, password, "");
        //} else {
        //    registerUserToDF(email, nickName, password, "");
        //}
        registerUserToDF(email, nickName, password, "", "", "");
    }

    //protected void registerUserToDF(String email, String nickName, String password, String profileImagePath) {
    //    restApi.registerUser(new RegisterUserRequest(email, nickName, "", password)).enqueue(new Callback<ResponseBody>() {
    //        @Override
    //        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //            try {
    //                ResponseBody responseBody = response.body();
    //                ResponseBody errorBody = response.errorBody();
    //                if (responseBody != null) {
    //                    String responseString = responseBody.string();
    //                    JSONObject object = new JSONObject(responseString);
    //                    if (object.has(Constants.SUCCESS))
    //                        loginUserAndAcquireSession(email, password, nickName, profileImagePath);
    //                } else if (errorBody != null) {
    //                    String errorString = errorBody.string();
    //                    // TODO: 29/01/18 handle DF registration error
    //                    JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
    //                    ;
    //                    if (errorObject.has(Constants.EMAIL)) {
    //                        hideProgress();
    //                        toastHelper.showError(R.string.email_already_exists);
    //                    }
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //                hideProgress();
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<ResponseBody> call, Throwable t) {
    //            t.printStackTrace();
    //            hideProgress();
    //        }
    //    });
    //}

    //private void loginUserAndAcquireSession(String email, String password, String nickName, String profileImagePath) {
    //    restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
    //        @Override
    //        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //            try {
    //                ResponseBody responseBody = response.body();
    //                ResponseBody errorBody = response.errorBody();
    //                if (responseBody != null) {
    //                    String responseString = responseBody.string();
    //                    JSONObject object = new JSONObject(responseString);
    //                    if (object.has(Constants.ERROR)) {
    //                        // need to handle the error response
    //                    } else {
    //                        String sessionToken = object.getString(Constants.SESSION_TOKEN);
    //                        preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
    //                        String dfid = object.getString(Constants.ID);
    //                        preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);
    //
    //                        //updateRoleToUser(dfid, nickName, profileImagePath, email);
    //                        registerUserCustom(dfid, nickName, profileImagePath, email);
    //                    }
    //                } else if (errorBody != null) {
    //                    String errorString = errorBody.string();
    //                    JSONObject errorObject = new JSONObject(errorString);
    //                    // TODO: 29/01/18 handle error response during registration
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //                hideProgress();
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<ResponseBody> call, Throwable t) {
    //            t.printStackTrace();
    //            hideProgress();
    //        }
    //    });
    //}

    //private void updateRoleToUser(String dfid, String nickName, String profileImagePath, String email) {
    //    restApi.updateUserRole(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), dfid, new UserRoleModel(dfid))
    //            .enqueue(new Callback<ResponseBody>() {
    //                @Override
    //                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //                    try {
    //                        ResponseBody responseBody = response.body();
    //                        ResponseBody errorBody = response.errorBody();
    //                        if (responseBody != null) {
    //                            String responseString = responseBody.string();
    //                            JSONObject object = new JSONObject(responseString);
    //                            if (!object.has(Constants.ERROR))
    //                                registerUserCustom(dfid, nickName, profileImagePath, email);
    //                        } else if (errorBody != null) {
    //                            String errorString = errorBody.string();
    //                            JSONObject errorObject = new JSONObject(errorString);
    //                            // TODO: 29/01/18 handle error response during registration
    //                        }
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                        hideProgress();
    //                    }
    //                }
    //
    //                @Override
    //                public void onFailure(Call<ResponseBody> call, Throwable t) {
    //                    t.printStackTrace();
    //                    hideProgress();
    //                }
    //            });
    //}

    //private void registerUserCustom(String dfid, String nickName, String profileImagePath, String email) {
    //    restApi.registerUserCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, nickName, profileImagePath, email)),
    //            preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
    //        @Override
    //        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //            try {
    //                ResponseBody responseBody = response.body();
    //                ResponseBody errorBody = response.errorBody();
    //                if (responseBody != null) {
    //                    String responseString = responseBody.string();
    //                    JSONObject jsonObject = new JSONObject(responseString);
    //                    if (jsonObject.has(Constants.RESOURCE)) {
    //                        JSONArray resourceArray = jsonObject.getJSONArray(Constants.RESOURCE);
    //                        String userid = resourceArray.getJSONObject(0).getString(Constants.USER_ID);
    //                        preferenceHelper.saveString(PreferenceKeys.USER_ID, userid);
    //                        hideProgress();
    //                        navigationHelper.navigate(RegisterActivity.this, _HomeActivity.class);
    //                    } else {
    //                        JSONObject errorObject = new JSONObject(responseString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
    //                        ;
    //                        if (errorObject.has(Constants.EMAIL)) {
    //                            hideProgress();
    //                            toastHelper.showError(R.string.email_already_exists);
    //                        }
    //                    }
    //                } else if (errorBody != null) {
    //                    String errorString = errorBody.string();
    //                    JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
    //                    ;
    //                    if (errorObject.has(Constants.EMAIL)) {
    //                        hideProgress();
    //                        toastHelper.showError(R.string.email_already_exists);
    //                    }
    //                    // TODO: 29/01/18 handle error response during registration
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //                hideProgress();
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<ResponseBody> call, Throwable t) {
    //            t.printStackTrace();
    //            hideProgress();
    //        }
    //    });
    //}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtils.REQUEST_CAPTURE || requestCode == ImageUtils.REQUEST_GALLERY)
            imageOptionChooserPopup.onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onImageOperationComplete(File file) {
        removePopupView();
        profileImageFile = file;
        mediaManager.loadLocalFileIntoImage(file.getAbsolutePath(), profileImageContent);
    }
}
