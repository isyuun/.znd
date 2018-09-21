package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;

import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.model.UserRoleModel;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.data.request.RegisterUserCustomRequest;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity3 extends SplashScreenActivity2 {
    String email;
    String pass = "user1234";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateAutoComplete();
    }

    private void populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return;
        }

        this.email = getGoogleAccount();
    }

    @Inject
    ToastHelper toastHelper;

    private void registerUserToDF(String email, String nickName, String password, String profileImagePath) {
        restApi.registerUser(new RegisterUserRequest(email, nickName, "", password)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject object = new JSONObject(responseString);
                        if (object.has(Constants.SUCCESS))
                            loginUserAndAcquireSession(email, password, nickName, profileImagePath);
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        // TODO: 29/01/18 handle DF registration error
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        if (errorObject.has(Constants.EMAIL)) {
                            hideProgress();
                            toastHelper.showError(R.string.email_already_exists);
                        }
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

    private void loginUserAndAcquireSession(String email, String password, String nickName, String profileImagePath) {
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

                            updateRoleToUser(dfid, nickName, profileImagePath, email);
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

    private void updateRoleToUser(String dfid, String nickName, String profileImagePath, String email) {
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
                                    registerUserCustom(dfid, nickName, profileImagePath, email);
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

    private void registerUserCustom(String dfid, String nickName, String profileImagePath, String email) {
        restApi.registerCustom(new RequestModel<>(new RegisterUserCustomRequest(dfid, nickName, profileImagePath, email)),
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
                            navigationHelper.navigate(SplashScreenActivity3.this, HomeActivity.class);
                        } else {
                            JSONObject errorObject = new JSONObject(responseString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                            ;
                            if (errorObject.has(Constants.EMAIL)) {
                                hideProgress();
                                toastHelper.showError(R.string.email_already_exists);
                            }
                        }
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString).getJSONObject(Constants.ERROR).getJSONObject(Constants.CONTEXT);
                        ;
                        if (errorObject.has(Constants.EMAIL)) {
                            hideProgress();
                            toastHelper.showError(R.string.email_already_exists);
                        }
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
}
