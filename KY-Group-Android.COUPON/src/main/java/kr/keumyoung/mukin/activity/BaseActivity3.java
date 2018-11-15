package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.data.request.RegisterUserCustomRequest;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
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

    String email;
    String pass;

    public void login(String email, String password) {
        loginUser(email, password);
    }

    protected void loginUser(String email, String password) {
        Log.e(__CLASSNAME__, "loginUser(...)");
        showProgress();
        // first generate DF session and save the session token and the dfid
        restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "loginUser:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "loginUser:onResponse()" + "\n" + responseString);
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
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "loginUser:onResponse()" + "\n" + errorString);
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
                toastHelper.showError(R.string.common_api_error);
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
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "fetchUserData:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "fetchUserData:onResponse()" + "\n" + responseString);
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
                            toastHelper.showError(getString(R.string.login) + " " + nickName + ":" + userId);
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
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "fetchUserData:onResponse()" + "\n" + errorString);
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
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    protected void onLoginSuccess(String email, String nickName) {
        preferenceHelper.saveString(getString(R.string.email), email);
        getMainApplication().send("Q", email, "");
    }

    protected void callLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_logout));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    showProgress();
                    restApi.logout(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            hideProgress();
                            //preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
                            //preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
                            //navigationHelper.navigateWithClearTask(HomeActivity.this, LoginChoiceActivity.class);
                            onLogoutSuccess();
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
        preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
        preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
        preferenceHelper.saveString(getString(R.string.email), "");
        preferenceHelper.saveString(getString(R.string.coupon), "");
    }

    protected void registerUserToDF(String email, String nickName, String password, String profileImagePath) {
        Log.e(__CLASSNAME__, "loginUser(...)");
        restApi.registerUser(new RegisterUserRequest(email, nickName, "", password)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserToDF:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "registerUserToDF:onResponse()" + "\n" + responseString);
                        JSONObject object = new JSONObject(responseString);
                        if (object.has(Constants.SUCCESS))
                            loginUserAndAcquireSession(email, password, nickName, profileImagePath);
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "registerUserToDF:onResponse()" + "\n" + errorString);
                        // TODO: 29/01/18 handle DF registration error
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (errorObject.has(Constants.EMAIL)) {
                            //hideProgress();
                            //toastHelper.showError(R.string.email_already_exists);
                            loginUserAndAcquireSession(email, password, nickName, profileImagePath);
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserToDF:onResponse()");
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

    private void loginUserAndAcquireSession(String email, String password, String nickName, String profileImagePath) {
        restApi.login(new LoginRequest(email, password, 0)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "loginUserAndAcquireSession:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "loginUserAndAcquireSession:onResponse()" + "\n" + responseString);
                        JSONObject object = new JSONObject(responseString);
                        if (object.has(Constants.ERROR)) {
                            // need to handle the error response
                        } else {
                            String sessionToken = object.getString(Constants.SESSION_TOKEN);
                            preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
                            String dfid = object.getString(Constants.ID);
                            preferenceHelper.saveString(PreferenceKeys.DF_ID, dfid);

                            //updateRoleToUser(dfid, nickName, profileImagePath, email);
                            registerUserCustom(dfid, nickName, profileImagePath, email);
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "loginUserAndAcquireSession:onResponse()" + "\n" + errorString);
                        JSONObject errorObject = new JSONObject(errorString);
                        // TODO: 29/01/18 handle error response during registration
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "loginUserAndAcquireSession:onResponse()");
                    e.printStackTrace();
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

    //private void updateRoleToUser(String dfid, String nickName, String profileImagePath, String email) {
    //    restApi.updateUserRole(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), dfid, new UserRoleModel(dfid))
    //            .enqueue(new Callback<ResponseBody>() {
    //                @Override
    //                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //                    try {
    //                        ResponseBody responseBody = response.body();
    //                        ResponseBody errorBody = response.errorBody();
    //                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "updateRoleToUser:onResponse()" + "\n" + errorBody + "\n" + responseBody);
    //                        if (responseBody != null) {
    //                            String responseString = responseBody.string();
    //                            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "updateRoleToUser:onResponse()" + "\n" + responseString);
    //                            JSONObject object = new JSONObject(responseString);
    //                            if (!object.has(Constants.ERROR))
    //                                registerUserCustom(dfid, nickName, profileImagePath, email);
    //                        } else if (errorBody != null) {
    //                            String errorString = errorBody.string();
    //                            if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "updateRoleToUser:onResponse()" + "\n" + errorString);
    //                            JSONObject errorObject = new JSONObject(errorString);
    //                            // TODO: 29/01/18 handle error response during registration
    //                        }
    //                    } catch (Exception e) {
    //                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "updateRoleToUser:onResponse()");
    //                        e.printStackTrace();
    //                        hideProgress();
    //                    }
    //                }
    //
    //                @Override
    //                public void onFailure(Call<ResponseBody> call, Throwable t) {
    //                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "updateRoleToUser:onFailure()");
    //                    t.printStackTrace();
    //                    hideProgress();
    //                }
    //            });
    //}

    private void registerUserCustom(String dfid, String nickName, String profileImagePath, String email) {
        restApi.registerCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, nickName, profileImagePath, email)),
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "registerUserCustom:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "registerUserCustom:onResponse()" + "\n" + responseString);
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.has(Constants.RESOURCE)) {
                            JSONArray resourceArray = jsonObject.getJSONArray(Constants.RESOURCE);
                            String userId = resourceArray.getJSONObject(0).getString(Constants.USER_ID);
                            preferenceHelper.saveString(PreferenceKeys.USER_ID, userId);
                            // login process completed. proceed to home activity
                            //hideProgress();
                            //navigationHelper.navigate(SplashScreenActivity2.this, _HomeActivity.class);
                            hideProgress();
                            toastHelper.showError(getString(R.string.login) + " " + nickName + ":" + userId);
                            if (!preferenceHelper.getString(getString(R.string.coupon), "").isEmpty()) {
                                openHomeActivity();
                            } else {
                                openPreferenceCoupon();
                            }
                            onRegisterSuccess(email, nickName);
                        } else {
                            JSONObject errorObject = new JSONObject(responseString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                            if (errorObject.has(Constants.EMAIL)) {
                                hideProgress();
                                toastHelper.showError(R.string.email_already_exists);
                            }
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "registerUserCustom:onResponse()" + "\n" + errorString);
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (errorObject.has(Constants.EMAIL)) {
                            hideProgress();
                            toastHelper.showError(R.string.email_already_exists);
                        }
                        // TODO: 29/01/18 handle error response during registration
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
