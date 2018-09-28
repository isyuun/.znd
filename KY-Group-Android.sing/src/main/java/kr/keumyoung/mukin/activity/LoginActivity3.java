package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity3 extends LoginActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    String email;
    String pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.email = emailEt.getText().toString();
        this.pass = passwordEt.getText().toString();
    }

    protected void loginUser(String email, String password) {
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

                            preferenceHelper.saveString(getString(R.string.email), email);

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
            }
        });
    }

    protected void fetchUserData(String dfid) {
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
                        if (active.equalsIgnoreCase(Constants.TRUE)) {
                            preferenceHelper.saveString(PreferenceKeys.USER_ID, userId);
                            preferenceHelper.saveString(PreferenceKeys.NICK_NAME, nickName);
                            preferenceHelper.saveString(PreferenceKeys.PROFILE_IMAGE, profileImage);

                            // login process completed. proceed to home activity
                            //hideProgress();
                            //toastHelper.showError(getString(R.string.login));
                            //navigationHelper.navigate(LoginActivity.this, HomeActivity.class);
                            hideProgress();
                            toastHelper.showError(getString(R.string.login) + " " + nickName + ":" + userId);
                            preferenceHelper.saveString(getString(R.string.email), email);
                            if (!preferenceHelper.getString(getString(R.string.coupon), "").isEmpty()) {
                                navigationHelper.navigate(LoginActivity3.this, HomeActivity.class);
                            } else {
                                openPreferenceCoupon();
                            }
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
            }
        });
    }

}
