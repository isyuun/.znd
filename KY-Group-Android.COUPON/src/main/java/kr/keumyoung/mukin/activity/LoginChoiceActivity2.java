package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.UnlinkResponseCallback;
import com.kakao.UserManagement;
import com.kakao.exception.KakaoException;

import org.json.JSONObject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginChoiceActivity2 extends LoginChoiceActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onResume() {
        super.onResume();
        //로그인
        if (!preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL).isEmpty() && !preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD).isEmpty() &&
                !preferenceHelper.getString(PreferenceKeys.USER_ID).isEmpty() && !preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN).isEmpty()) {
            finish();
        }
    }

    SessionCallback sessionCallback = new SessionCallback() {
        @Override
        public void onSessionOpened() {
            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "sessionCallback:onSessionOpened()");
            kakaoLogin();
        }

        @Override
        public void onSessionClosed(KakaoException exception) {
            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "sessionCallback:onSessionClosed()" + exception);
            hideProgress();
        }

        @Override
        public void onSessionOpening() {
            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "sessionCallback:onSessionOpening()");
            showProgress();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session.addCallback(sessionCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeCallback(sessionCallback);
    }

    @Override
    protected void kakaoLogin() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        kakaoApi.userMeRequest(String.format("Bearer %s", Session.getCurrentSession().getAccessToken()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String email = "";
                        String password = "";
                        String nickName = "";
                        String profileImagePath = "";
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "kakaoLogin:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "kakaoLogin:onResponse()" + "\n" + responseString);
                                JSONObject responseObject = new JSONObject(responseString);
                                email = responseObject.getString(Constants.K_EMAIL);
                                password = responseObject.getString(Constants.ID);
                                nickName = responseObject.getJSONObject(Constants.PROPERTIES).getString(Constants.K_NICK_NAME);
                                profileImagePath = responseObject.getJSONObject(Constants.PROPERTIES).getString(Constants.K_NICK_NAME);
                                //registerUser(email, id, nickName, Constants.KAKAO);
                                registerUserToDF(email, nickName, password, profileImagePath);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "kakaoLogin:onResponse()" + "\n" + errorString);
                                JSONObject errorObject = new JSONObject(errorString);
                                String code = errorObject.getString(Constants.CODE);
                                String msg = errorObject.getString("msg");
                                toastHelper.showError(msg + "(" + code + ")");
                                kakaoLogout();
                                hideProgress();
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "kakaoLogin:onResponse()" + "[" + email + "]" + "[" + password + "]" + "[" + nickName + "]");
                            e.printStackTrace();
                            if (email.isEmpty()) {
                                toastHelper.showError(R.string.kakao_talk_email_check);
                            }
                            kakaoLogout();
                            hideProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "kakaoLogin:onFailure()");
                        hideProgress();
                        t.printStackTrace();
                        kakaoLogout();
                    }
                });
    }

    @Override
    protected void onRegisterSuccess(String email, String nickName) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onRegisterSuccess(email, nickName);
    }

    protected void kakaoLogout() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(long userId) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "kakaoLogout:onSuccess()" + userId);
            }

            @Override
            protected void onFailure(APIErrorResult errorResult) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "kakaoLogout:onFailure()" + errorResult);
            }
        });
        kakaoUnlink();
    }

    protected void kakaoUnlink() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        UserManagement.requestUnlink(new UnlinkResponseCallback() {
            @Override
            protected void onSuccess(long userId) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onSessionClosed()" + userId);
            }

            @Override
            protected void onSessionClosedFailure(APIErrorResult errorResult) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onSessionClosed()" + errorResult);
            }

            @Override
            protected void onFailure(APIErrorResult errorResult) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "kakaoUnlink:onSessionClosed()" + errorResult);
            }
        });
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
    }
}
