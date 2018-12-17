package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.data.request.RegisterUserCustomRequest;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
import kr.keumyoung.mukin.data.request.UpdateUserCustomRequest;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity3 extends BaseActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    private void showError(JSONObject error) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + error);
        try {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "\n" + (error != null ? error.toString(2): ""));
            String code = error.getString("code");
            String message = error.getString("message");
            toastHelper.showError("오류:" + message + "[" + code + "]");
        } catch (Exception e) {
            e.printStackTrace();
            toastHelper.showError(R.string.invalid_credential);
        }
    }

    String email;
    String pass;

    public void login(String email, String password) {
        loginUser(email, password, "", "", "", "");
    }

    private void loginUser(String email, String password, String name, String profileImage, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        showProgress();
        // first generate DF session and save the session token and the dfid
        restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "loginUser:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "loginUser:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (json.has(Constants.ERROR)) {
                            // need to handle the error response
                        } else {
                            String sessionToken = json.getString(Constants.SESSION_TOKEN);
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            String dfid = json.getString(Constants.ID);
                            preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);

                            preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, email);
                            preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, password);

                            fetchUserData(dfid);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "loginUser:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        onLoginFailure();
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
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void fetchUserData(String dfid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        // fetch the table data from the user table using the dfid
        String filter = "dfid=" + dfid;
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.USER, filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "fetchUserData:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "fetchUserData:onResponse()" + "\n" + responseString);
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONObject userObject = responseObject.getJSONArray(Constants.RESOURCE).getJSONObject(0);
                        String userId = userObject.getString(Constants.USER_ID);
                        String nickName = userObject.getString(Constants.NICK_NAME);
                        String profileImage = userObject.getString(Constants.PROFILE_IMAGE);
                        String active = userObject.getString(Constants.ACTIVE);
                        String email = userObject.getString(Constants.EMAIL);
                        if (active.equalsIgnoreCase(Constants.TRUE)) {
                            preferenceHelper.saveString(PreferenceKeys.USER_ID, userId);
                            preferenceHelper.saveString(PreferenceKeys.NICK_NAME, nickName);
                            preferenceHelper.saveString(PreferenceKeys.PROFILE_IMAGE, profileImage);

                            // login process completed. proceed to home activity
                            //hideProgress();
                            //toastHelper.showError(getString(R.string.login));
                            //navigationHelper.navigate(LoginActivity.this, _HomeActivity.class);
                            hideProgress();
                            toastHelper.showError(getString(R.string.login) /*+ " " + nickName*/ + ":" + userId);
                            //화면이동...
                            if (preferenceHelper.getString(getString(R.string.coupon), "").isEmpty()) {
                                openPreferenceCoupon();
                                //로그인창에서만
                                if (BaseActivity3.this instanceof _LoginActivity) {
                                    finish();
                                }
                            }
                            onLoginSuccess(email, nickName);
                        } else {
                            // user is not active. lets stop here
                            hideProgress();
                            toastHelper.showError(R.string.deactivated_user_message);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "fetchUserData:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        onLoginFailure();
                        hideProgress();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "fetchUserData:onResponse()");
                    e.printStackTrace();
                    toastHelper.showError(getString(R.string.common_api_error));
                    onLoginFailure();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                hideProgress();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    protected void onLoginSuccess(String email, String nickName) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        preferenceHelper.saveString(getString(R.string.email), email);
        getMainApplication().send("Q", email, "");
    }

    protected void onLoginFailure() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
        preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
        preferenceHelper.saveString(getString(R.string.email), "");
        preferenceHelper.saveString(getString(R.string.coupon), "");
    }

    protected void callLogout() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_logout));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    showProgress();
                    restApi.logout(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            onLogoutSuccess();
                            hideProgress();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hideProgress();
                            toastHelper.showError(R.string.common_api_error);
                        }
                    });
                }
        );

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void onLogoutSuccess() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
        preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
        preferenceHelper.saveString(getString(R.string.email), "");
        preferenceHelper.saveString(getString(R.string.coupon), "");
    }

    protected void registerUserToDF(String email, String name, String password, String profileImage, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        restApi.registerUser(new RegisterUserRequest(email, name, "", password)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "registerUserToDF:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "registerUserToDF:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.SUCCESS)) {
                            loginUserAndAcquireSession(email, password, name, profileImage, sociallogin, socialid);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "registerUserToDF:onResponse()" + "\n" + errorString);
                        JSONObject json = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.EMAIL)) {
                            loginUserAndAcquireSession(email, password, name, profileImage, sociallogin, socialid);
                        } else {
                            showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "registerUserToDF:onResponse()");
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserToDF:onFailure()");
                t.printStackTrace();
                hideProgress();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void loginUserAndAcquireSession(String email, String password, String name, String profileImage, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "loginUserAndAcquireSession:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "loginUserAndAcquireSession:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.ERROR)) {
                            // need to handle the error response
                        } else {
                            String sessionToken = json.getString(Constants.SESSION_TOKEN);
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            String dfid = json.getString(Constants.ID);
                            preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);

                            preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, email);
                            preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, password);

                            registerUpdateUserCustom(dfid, name, profileImage, email, sociallogin, socialid);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "loginUserAndAcquireSession:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        onLoginFailure();
                        hideProgress();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "loginUserAndAcquireSession:onResponse()");
                    e.printStackTrace();
                    toastHelper.showError(getString(R.string.common_api_error));
                    onLoginFailure();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "loginUserAndAcquireSession:onFailure()");
                t.printStackTrace();
                hideProgress();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void registerUpdateUserCustom(String dfid, String name, String profileImage, String email, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        // fetch the table data from the user table using the dfid
        String filter = "dfid=" + dfid;
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.USER, filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "registerUpdateUserCustom:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "registerUpdateUserCustom:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.RESOURCE)) {
                            if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "[UP]" + json.getJSONArray(Constants.RESOURCE).isNull(0));
                            if (!json.getJSONArray(Constants.RESOURCE).isNull(0)) {
                                JSONObject user = json.getJSONArray(Constants.RESOURCE).getJSONObject(0);
                                String userId = user.getString(Constants.USER_ID);
                                updateUserCustom(userId, dfid, name, profileImage, email, sociallogin, socialid);
                            } else {
                                registerUserCustom(dfid, name, profileImage, email, sociallogin, socialid);
                            }
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "registerUpdateUserCustom:onResponse()" + "\n" + errorString);
                        JSONObject json = new JSONObject(errorString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.RESOURCE)) {
                            registerUserCustom(dfid, name, profileImage, email, sociallogin, socialid);
                        } else {
                            showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "registerUpdateUserCustom:onResponse()");
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                hideProgress();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void updateUserCustom(String userid, String dfid, String name, String profileImage, String email, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        restApi.updateUserCustom(new RequestModel<>(new UpdateUserCustomRequest(userid, dfid, name, profileImage, email, sociallogin, socialid)),
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "updateUserCustom:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "updateUserCustom:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.RESOURCE)) {
                            fetchUserData(dfid);
                        } else {
                            json = new JSONObject(responseString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                            if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                            if (json.has(Constants.EMAIL)) {
                                hideProgress();
                                toastHelper.showError(R.string.email_already_exists);
                            }
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "updateUserCustom:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NO]" + "updateUserCustom:onResponse()");
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "updateUserCustom:onFailure()");
                t.printStackTrace();
                hideProgress();
            }
        });
    }

    private void registerUserCustom(String dfid, String name, String profileImage, String email, String sociallogin, String socialid) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        //restApi.registerUserCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, name, profileImage, email)),
        restApi.registerUserCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, name, profileImage, email, sociallogin, socialid)),
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "registerUserCustom:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "registerUserCustom:onResponse()" + "\n" + responseString);
                        JSONObject json = new JSONObject(responseString);
                        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                        if (json.has(Constants.RESOURCE)) {
                            fetchUserData(dfid);
                        } else {
                            json = new JSONObject(responseString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                            if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "\n" + (json != null ? json.toString(2) : ""));
                            if (json.has(Constants.EMAIL)) {
                                hideProgress();
                                toastHelper.showError(R.string.email_already_exists);
                            }
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "registerUserCustom:onResponse()" + "\n" + errorString);
                        JSONObject json = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (json.has(Constants.EMAIL)) {
                            hideProgress();
                            toastHelper.showError(R.string.email_already_exists);
                        } else {
                            showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserCustom:onResponse()");
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserCustom:onFailure()");
                t.printStackTrace();
                hideProgress();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    protected void onRegisterSuccess(String email, String nickName) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        preferenceHelper.saveString(getString(R.string.email), email);
        getMainApplication().send("Q", email, "");
    }

    public boolean handleDFError(JSONObject errorObject, SessionRefreshListener listener) throws JSONException {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + errorObject + ":" + listener + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID));
        String errorCode = errorObject.getJSONObject("error").getString("code");
        if (!errorCode.equalsIgnoreCase("200")) { // session has expired. need to refresh the session_token
            restApi.refreshSessionToken(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN),
                    preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ResponseBody responseBody = response.body();
                        ResponseBody errorBody = response.errorBody();
                        if (responseBody != null) {
                            String responseString = responseBody.string();
                            JSONObject responseObject = new JSONObject(responseString);
                            String sessionToken = responseObject.getString("session_token");
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            listener.onSessionRefresh();
                        } else if (errorBody != null) {
                            listener.logout(BaseActivity3.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.logout(BaseActivity3.this);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    listener.logout(BaseActivity3.this);
                    toastHelper.showError(R.string.common_api_error);
                }
            });
            return true;
        }
        return false;
    }
}
