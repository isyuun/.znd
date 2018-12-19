package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.KakaoApi;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginChoiceActivity extends LoginActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    @Inject
    KakaoApi kakaoApi;

    CallbackManager callbackManager;

    @BindView(R.id.kakao_button)
    FrameLayout kakaoButton;
    @BindView(R.id.facebook_button)
    FrameLayout facebookButton;

    @Override
    protected void setFlags() {
        //super.setFlags();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //로그인
        if (!preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL).isEmpty() && !preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD).isEmpty() &&
                !preferenceHelper.getString(PreferenceKeys.USER_ID).isEmpty() && !preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN).isEmpty()) {
        }
    }

    class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_login_choice_2, null, false);
        inflateContainerView(view);

        MainApplication.getInstance().getMainComponent().inject(this);

        ButterKnife.bind(this, view);

        updateSavedValue();

        //FACEBOOK
        makeFacebookLogin();

        //KAKAO
        makeKakaoLogin();
    }

    /**
     * 가지랄하네
     * {@link LoginActivity#onCreate()}
     */
    @Override
    protected void onCreate() {
    }

    //@OnClick({R.id.kakao_button, R.id.facebook_button})
    @OnClick({R.id.signup_anchor, R.id.login_button, R.id.kakao_button, R.id.facebook_button})
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.kakao_button:
                kakaoButton();
                break;
            case R.id.facebook_button:
                facebookButton();
                break;
        }
    }

    protected void updateSavedValue() {
        super.updateSavedValue();
        String savedEmail = preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        if (savedEmail == null || savedEmail.isEmpty()) {
            // nothing saved
        } else {
            String savedPassword = preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD);
            if (savedPassword == null || savedPassword.isEmpty()) {
                // email is saved but password is not. nothing to do
            } else {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return;
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onDestroy();
    }

    @Override
    protected void onLoginSuccess(String email, String nickName) {
        super.onLoginSuccess(email, nickName);
        finish();
    }

    @Override
    protected void onLoginFailure() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onLoginFailure();
        kakaoLogoutUnlink();
        facebookLogoutUnlink();
    }

    @Override
    protected void onLogoutSuccess() {
        super.onLogoutSuccess();
    }

    private void kakaoButton() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    private void makeKakaoLogin() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        Session.getCurrentSession().clearCallbacks();
        Session.getCurrentSession().addCallback(new SessionCallback() {
            @Override
            public void onSessionOpened() {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "sessionCallback:onSessionOpened()");
                kakaoLogin();
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "sessionCallback:onSessionOpenFailed()");
                super.onSessionOpenFailed(exception);
                exception.printStackTrace();
            }
        });
    }

    private void kakaoLogin() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        kakaoApi.userMeRequest(String.format("Bearer %s", Session.getCurrentSession().getAccessToken()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String email = "";
                        String password = "";
                        String name = "";
                        String profileImage = "";
                        String sociallogin = Constants.KAKAO;
                        String socialid = "";
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "kakaoLogin:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "kakaoLogin:onResponse()" + "\n" + responseString);
                                JSONObject json = new JSONObject(responseString);
                                if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                                email = json.getString(Constants.K_EMAIL);
                                password = socialid = json.getString(Constants.ID);
                                name = json.getJSONObject(Constants.PROPERTIES).getString(Constants.K_NICK_NAME);
                                profileImage = json.getJSONObject(Constants.PROPERTIES).getString(Constants.K_NICK_NAME);
                                registerUserToDF(email, name, password, profileImage, sociallogin, socialid);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "kakaoLogin:onResponse()" + "\n" + errorString);
                                JSONObject json = new JSONObject(errorString);
                                if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                                String code = json.getString(Constants.CODE);
                                String msg = json.getString("msg");
                                toastHelper.showError(msg + "(" + code + ")");
                                kakaoLogoutUnlink();
                                hideProgress();
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "kakaoLogin:onResponse()" + "[" + email + "]" + "[" + password + "]" + "[" + name + "]");
                            e.printStackTrace();
                            if (email.isEmpty()) {
                                toastHelper.showError(R.string.kakao_talk_email_check);
                            }
                            kakaoLogoutUnlink();
                            hideProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "kakaoLogin:onFailure()");
                        t.printStackTrace();
                        kakaoLogoutUnlink();
                        hideProgress();
                    }
                });
    }

    private void kakaoLogoutUnlink() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        kakaoLogout();
        kakaoUnlink();
    }

    private void kakaoLogout() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "kakaoLogout:onCompleteLogout()");
            }
        });
    }

    private void kakaoUnlink() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onSessionClosed()" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onNotSignedUp()");
            }

            @Override
            public void onSuccess(Long result) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onSuccess()" + result);
            }
        });
    }

    private void facebookButton() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void makeFacebookLogin() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());

        callbackManager = CallbackManager.Factory.create();

        //FACEBOOK
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[OK]" + "FacebookCallback:" + getMethodName() + loginResult);
                        facebookLogin(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "FacebookCallback:" + getMethodName());
                        toastHelper.showError(R.string.please_continue_with_facebook_login);
                        facebookLogoutUnlink();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "FacebookCallback:" + getMethodName());
                        toastHelper.showError(R.string.please_continue_with_facebook_login);
                        facebookLogoutUnlink();
                        exception.printStackTrace();
                    }
                });
    }

    private void facebookLogin(LoginResult loginResult) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + loginResult);
        // App code
        AccessToken accessToken = loginResult.getAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                (object, response) -> {
                    String email = "";
                    String password = "";
                    String name = "";
                    String profileImage = "";
                    String sociallogin = Constants.FACEBOOK;
                    String socialid = "";
                    try {
                        email = object.getString(Constants.EMAIL);
                        password = socialid = object.getString(Constants.ID);
                        name = object.getString(Constants.NAME);
                        profileImage = "https://graph.facebook.com/" + socialid + "/picture";
                        registerUserToDF(email, name, password, profileImage, sociallogin, socialid);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "facebookLogin:onResponse()" + "[" + email + "]" + "[" + password + "]" + "[" + name + "]");
                        e.printStackTrace();
                        if (email.isEmpty()) {
                            toastHelper.showError(R.string.facebook_talk_email_check);
                        }
                        facebookLogoutUnlink();
                        hideProgress();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void facebookLogoutUnlink() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + AccessToken.getCurrentAccessToken());
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "facebookLogoutUnlink:" + getMethodName());
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }
}
