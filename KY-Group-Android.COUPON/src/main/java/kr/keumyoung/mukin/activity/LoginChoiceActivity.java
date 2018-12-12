package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.KakaoApi;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.model.UserRoleModel;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.data.request.RegisterUserCustomRequest;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
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

public class LoginChoiceActivity extends _BaseActivity {

    @BindView(R.id.alternative_sign_in_button)
    CardView alternativeSignInButton;
    @BindView(R.id.kakao_button)
    FrameLayout kakaoButton;
    @BindView(R.id.facebook_button)
    FrameLayout facebookButton;
    @BindView(R.id.social_login_section)
    LinearLayout socialLoginSection;

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    @Inject
    KakaoApi kakaoApi;

    CallbackManager callbackManager;

    Session session;

    private static final String EMAIL = "email";

    SessionCallback sessionCallback = new SessionCallback() {
        @Override
        public void onSessionOpened() {
            kakaoLogin();
        }

        @Override
        public void onSessionClosed(KakaoException exception) {
            hideProgress();
        }

        @Override
        public void onSessionOpening() {
            showProgress();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login_choice, null, false);
        inflateContainerView(view);

        MainApplication.getInstance().getMainComponent().inject(this);

        ButterKnife.bind(this, view);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                (object, response) -> {
                                    try {
                                        String email = object.getString(Constants.EMAIL);
                                        String fbId = object.getString(Constants.ID);
                                        String name = object.getString(Constants.NAME);
                                        registerUser(email, fbId, name, Constants.FACEBOOK);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        toastHelper.showError(R.string.please_continue_with_facebook_login);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        toastHelper.showError(R.string.please_continue_with_facebook_login);
                        exception.printStackTrace();
                    }
                });

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        updateSavedValue();
    }

    private void updateSavedValue() {
        String savedEmail = preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        if (savedEmail == null || savedEmail.isEmpty()) {
            // nothing saved
        } else {
            String savedPassword = preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD);
            if (savedPassword == null || savedPassword.isEmpty()) {
                // email is saved but password is not. nothing to do
            } else {
                onViewClicked(alternativeSignInButton);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeCallback(sessionCallback);
    }

    private void registerUser(String email, String fbId, String name, String social) {
        showProgress();
        restApi.registerUser(new RegisterUserRequest(email, name, name, fbId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject responseObject = new JSONObject(responseString);
                        if (responseObject.has(Constants.SUCCESS))
                            getSessionForUser(email, fbId, name, social);
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (errorObject.has(Constants.EMAIL)) {
                            tryLogin(email, fbId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                    toastHelper.showError(R.string.common_api_error);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                hideProgress();
            }
        });
    }

    private void tryLogin(String email, String password) {
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
                        JSONObject errorObject = new JSONObject(errorString);
                        // TODO: 29/01/18 handle error response during login
                        toastHelper.showError(R.string.invalid_credential);
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
                            //navigationHelper.navigate(LoginChoiceActivity.this, _HomeActivity.class, true, null);
                            finish();
                        } else {
                            // user is not active. lets stop here
                            hideProgress();
                            toastHelper.showError(R.string.deactivated_user_message);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        // TODO: 29/01/18 handle login error
                        toastHelper.showError(R.string.invalid_credential);
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

    private void getSessionForUser(String email, String password, String nickName, String social) {
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
                            toastHelper.showError(R.string.invalid_credential);
                        } else {
                            String sessionToken = object.getString(Constants.SESSION_TOKEN);
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            String dfid = object.getString(Constants.ID);
                            preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);

                            updateRoleToUser(dfid, nickName, social, password, email);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        toastHelper.showError(R.string.invalid_credential);
                        // TODO: 29/01/18 handle error response during registration
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
                toastHelper.showError(R.string.invalid_credential);
            }
        });
    }

    private void updateRoleToUser(String dfid, String name, String social, String password, String email) {
        restApi.updateUserRole(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), dfid, new UserRoleModel(dfid))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject object = new JSONObject(responseString);
                                if (!object.has(Constants.ERROR))
                                    registerUserCustom(dfid, name, social, password, email);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                JSONObject errorObject = new JSONObject(errorString);
                                // TODO: 29/01/18 handle error response during registration
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

    public void registerUserCustom(String dfid, String name, String social, String password, String email) {
        restApi.registerCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, name, "", email, social, password)),
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.has(Constants.RESOURCE)) {
                            JSONArray resourceArray = jsonObject.getJSONArray(Constants.RESOURCE);
                            String userid = resourceArray.getJSONObject(0).getString(Constants.USER_ID);
                            preferenceHelper.saveString(PreferenceKeys.USER_ID, userid);
                            hideProgress();
                            //navigationHelper.navigate(LoginChoiceActivity.this, _HomeActivity.class, true, null);
                            finish();
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        // TODO: 29/01/18 handle error response during registration
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

    @OnClick({R.id.alternative_sign_in_button, R.id.kakao_button, R.id.facebook_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.alternative_sign_in_button:
                navigationHelper.navigate(this, _LoginActivity.class, true, null);
                break;
            case R.id.kakao_button:
                // handle kakao login
                kakaoLogin();
                break;
            case R.id.facebook_button:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                break;
        }
    }

    protected void kakaoLogin() {
        kakaoApi.userMeRequest(String.format("Bearer %s", Session.getCurrentSession().getAccessToken()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject responseObject = new JSONObject(responseString);
                                String nickName = responseObject.getJSONObject(Constants.PROPERTIES).getString(Constants.K_NICK_NAME);
                                String email = responseObject.getString(Constants.K_EMAIL);
                                String id = responseObject.getString(Constants.ID);
                                registerUser(email, id, nickName, Constants.KAKAO);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideProgress();
                        t.printStackTrace();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return;
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
