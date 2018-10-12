package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 11/01/18.
 */

public class LoginActivity extends BaseActivity {

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    @BindView(R.id.signup_anchor)
    TextView signupAnchor;
    @BindView(R.id.signup_section)
    LinearLayout signupSection;
    @BindView(R.id.login_button)
    CardView loginButton;
    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.middle_spacer)
    View middleSpacer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login, null, false);
        inflateContainerView(view);

        MainApplication.getInstance().getMainComponent().inject(this);

        ButterKnife.bind(this, view);

        updateSavedValue();
    }

    private void updateSavedValue() {
        String savedEmail = preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        if (savedEmail == null || savedEmail.isEmpty()) {
            // nothing saved
        } else {
            emailEt.setText(savedEmail);
            String savedPassword = preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD);
            if (savedPassword == null || savedPassword.isEmpty()) {
                // email is saved but password is not. nothing to do
            } else {
                passwordEt.setText(savedPassword);
                onViewClicked(loginButton);
            }
        }
    }

    @OnClick({R.id.signup_anchor, R.id.login_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signup_anchor:
                navigationHelper.navigate(this, RegisterActivity.class);
                break;
            case R.id.login_button:
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();

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

                preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, email);
                preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, password);

                loginUser(email, password);

                break;
        }
    }

    private void loginUser(String email, String password) {
        showProgress();
        // first generate DF session and save the session token and the dfid
        restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject object = new JSONObject(responseString);
                        if (object.has(Constants.ERROR)) {
                            // need to handle the error response
                        } else {
                            String sessionToken = object.getString(Constants.SESSION_TOKEN);
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            String dfid = object.getString(Constants.ID);
                            preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);

                            fetchUserData(dfid);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR);
                        String code = errorObject.getString(Constants.CODE);
                        if (code.equalsIgnoreCase(Constants.INVALID_SESSION)) {
                            toastHelper.showError(R.string.invalid_credential);
                        }
                        // TODO: 29/01/18 handle error response during login
                        hideProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                hideProgress();
            }
        });
    }

    private void fetchUserData(String dfid) {
        // fetch the table data from the user table using the dfid
        String filter = "dfid=" + dfid;
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.USER, filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();

                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONObject userObject = responseObject.getJSONArray(Constants.RESOURCE).getJSONObject(0);
                        String userId = userObject.getString(Constants.USER_ID);
                        String nickName = userObject.getString(Constants.NICK_NAME);
                        String profileImage = userObject.getString(Constants.PROFILE_IMAGE);
                        String active = userObject.getString(Constants.ACTIVE);
                        if (active.equalsIgnoreCase(Constants.TRUE)) {
                            preferenceHelper.saveString(PreferenceKeys.USER_ID, userId);
                            preferenceHelper.saveString(PreferenceKeys.NICK_NAME, nickName);
                            preferenceHelper.saveString(PreferenceKeys.PROFILE_IMAGE, profileImage);

                            // login process completed. proceed to home activity
                            hideProgress();
                            navigationHelper.navigate(LoginActivity.this, HomeActivity.class);
                        } else {
                            // user is not active. lets stop here
                            hideProgress();
                            toastHelper.showError(R.string.deactivated_user_message);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        // TODO: 29/01/18 handle login error
                        hideProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                hideProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigationHelper.navigateWithReverseAnim(this, LoginChoiceActivity.class);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        attachKeyboardListeners();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        detachKeyboardListener();
//    }
//
//    @Override
//    protected void onHideKeyboard() {
//        signupSection.setVisibility(View.VISIBLE);
//        middleSpacer.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected void onShowKeyboard() {
//        signupSection.setVisibility(View.GONE);
//        middleSpacer.setVisibility(View.GONE);
//    }
}
