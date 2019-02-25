package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.request.PasswordChangeRequest;
import kr.keumyoung.mukin.data.request.PasswordResetRequest;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginChoiceActivity2 extends LoginChoiceActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.password_reset_anchor).setOnClickListener(v -> {
            passwordResetWarning();
        });
        findViewById(R.id.password_change_button).setOnClickListener(v -> {
            passwordChangeWarning();
        });
    }

    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
        super.onResume();
        passwordSetting(false);
        fetchUserSocial();
    }

    String confirm_code;

    private CheckableImageButton findCheckableImageButton(View view) {
        if (view instanceof CheckableImageButton) {
            return (CheckableImageButton) view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, ei = viewGroup.getChildCount(); i < ei; i++) {
                CheckableImageButton checkableImageButton = findCheckableImageButton(viewGroup.getChildAt(i));
                if (checkableImageButton != null) {
                    return checkableImageButton;
                }
            }
        }
        return null;
    }

    private void passwordSetting(boolean clear) {
        Intent intent = getIntent();
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + intent.getData());
        if (intent != null && intent.getData() != null) {
            this.email = intent.getData().getQueryParameter("email");
            this.confirm_code = intent.getData().getQueryParameter("confirm_code");
            //toastHelper.showError("email:" + email + ", confirm_code:" + confirm_code);
        }

        if (clear) this.confirm_code = "";

        boolean passwordChange;
        if ((email != null && !email.isEmpty()) && (confirm_code != null && !confirm_code.isEmpty())) {
            passwordChange = true;
            setTitle(R.string.password_change);
            findViewById(R.id.login_change_section).setVisibility(View.VISIBLE);
            findViewById(R.id.login_action_section).setVisibility(View.GONE);
            //show password
            //passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordChange = false;
            setTitle(R.string.login);
            findViewById(R.id.login_change_section).setVisibility(View.GONE);
            findViewById(R.id.login_action_section).setVisibility(View.VISIBLE);
            //hide password
            //passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        if (email != null && !email.isEmpty()) {
            emailEt.setText(email);
            passwordEt.requestFocus();
        }
        if (isLogin()) {
            findViewById(R.id.password_reset_section).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.password_reset_section).setVisibility(View.VISIBLE);
        }
        if (passwordChange) {
            //if (findCheckableImageButton(findViewById(R.id.password_anchor)) != null) {
            //    findCheckableImageButton(findViewById(R.id.password_anchor)).performClick();
            //}
        }
    }


    private void fetchUserSocial() {
        String dfid = preferenceHelper.getString(PreferenceKeys.DF_ID);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + dfid);
        if (dfid == null || dfid.isEmpty()) {
            return;
        }
        // fetch the table data from the user table using the dfid
        String filter = "dfid=" + dfid;
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.USER, filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgress();
                ResponseBody body = response.body();
                ResponseBody error = response.errorBody();
                if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "fetchUserSocial:onResponse()" + "\n" + error + "\n" + body);
                try {
                    if (body != null) {
                        String bodyString = body.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "fetchUserSocial:onResponse()" + "\n" + bodyString);
                        JSONObject json = new JSONObject(bodyString);
                        if (json.has(Constants.ERROR)) {
                            showError(json.getJSONObject(Constants.ERROR));
                        } else {
                            //toastHelper.showError(bodyString);
                            JSONObject responseObject = new JSONObject(bodyString);
                            JSONObject userObject = responseObject.getJSONArray(Constants.RESOURCE).getJSONObject(0);
                            preferenceHelper.saveString(PreferenceKeys.SOCIAL_LOGIN, userObject.getString(PreferenceKeys.SOCIAL_LOGIN));
                            preferenceHelper.saveString(PreferenceKeys.SOCIAL_ID, userObject.getString(PreferenceKeys.SOCIAL_ID));
                            if (!preferenceHelper.getString(PreferenceKeys.SOCIAL_LOGIN).isEmpty() && !preferenceHelper.getString(PreferenceKeys.SOCIAL_ID).isEmpty()) {
                                preferenceHelper.saveString(PreferenceKeys.SOCIAL_EMAIL, preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL));
                            } else {
                                preferenceHelper.saveString(PreferenceKeys.SOCIAL_EMAIL, "");
                            }
                        }
                    } else if (error != null) {
                        String errorString = error.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "fetchUserSocial:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgress();
                t.printStackTrace();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    @Override
    protected void onLoginSuccess(String email, String nickName) {
        super.onLoginSuccess(email, nickName);
        passwordSetting(true);
        fetchUserSocial();
    }

    @Override
    protected void onLoginFailure() {
        super.onLoginFailure();
        passwordSetting(true);
        preferenceHelper.saveString(PreferenceKeys.DF_ID, "");
    }

    @Override
    protected void onLogoutSuccess() {
        super.onLogoutSuccess();
        passwordSetting(true);
        preferenceHelper.saveString(PreferenceKeys.DF_ID, "");
    }

    private void passwordChangeWarning() {
        showAlertDialog(
                getString(R.string.password_change_warning),
                (dialog, which) -> {
                    passwordChange();
                },
                (dialog, which) -> {
                    passwordSetting(true);
                });
    }

    private void passwordChange() {
        if (emailEt.getText().toString().isEmpty()) {
            toastHelper.showError(R.string.email_blank_error);
            emailEt.requestFocus();
            return;
        }
        if (passwordEt.getText().toString().isEmpty()) {
            toastHelper.showError(R.string.password_blank_error);
            passwordEt.requestFocus();
            return;
        }

        showProgress(true);

        this.pass = passwordEt.getText().toString();

        restApi.changepassword(new PasswordChangeRequest(this.email, this.pass, confirm_code)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgress();
                ResponseBody body = response.body();
                ResponseBody error = response.errorBody();
                if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "changepassword:onResponse()" + "\n" + error + "\n" + body);
                try {
                    if (body != null) {
                        String bodyString = body.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "changepassword:onResponse()" + "\n" + bodyString);
                        JSONObject json = new JSONObject(bodyString);
                        if (json.has(Constants.ERROR)) {
                            showError(json.getJSONObject(Constants.ERROR));
                        } else {
                            toastHelper.showError(bodyString);
                            passwordSetting(true);
                            login(email, pass);
                        }
                    } else if (error != null) {
                        String errorString = error.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "changepassword:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgress();
                t.printStackTrace();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void passwordResetWarning() {
        String email = emailEt.getText().toString();
        if (email.equalsIgnoreCase(preferenceHelper.getString(PreferenceKeys.SOCIAL_EMAIL))) {
            showAlertDialog(
                    getString(R.string.password_reset_social),
                    (dialog, which) -> {
                    }
            );
            return;
        }
        showAlertDialog(
                getString(R.string.password_reset_check),
                (dialog, which) -> {
                    passwordReset();
                },
                (dialog, which) -> {
                    passwordSetting(true);
                });
    }

    private void passwordReset() {
        if (emailEt.getText().toString().isEmpty()) {
            toastHelper.showError(R.string.email_blank_error);
            emailEt.requestFocus();
            return;
        }

        showProgress(true);

        this.email = emailEt.getText().toString();

        restApi.resetpassword(new PasswordResetRequest(this.email), true).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgress();
                ResponseBody body = response.body();
                ResponseBody error = response.errorBody();
                if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, "resetpassword:onResponse()" + "\n" + error + "\n" + body);
                try {
                    if (body != null) {
                        String bodyString = body.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "resetpassword:onResponse()" + "\n" + bodyString);
                        JSONObject json = new JSONObject(bodyString);
                        if (json.has(Constants.ERROR)) {
                            showError(json.getJSONObject(Constants.ERROR));
                        } else {
                            toastHelper.showError(bodyString);
                            showAlertDialog(
                                    getString(R.string.password_reset_check),
                                    (dialog, which) -> {
                                        openMailApp();
                                    });
                        }
                    } else if (error != null) {
                        String errorString = error.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "resetpassword:onResponse()" + "\n" + errorString);
                        showError(new JSONObject(errorString).getJSONObject(Constants.ERROR));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgress();
                t.printStackTrace();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    private void openMailApp() {
        Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
        startActivity(intent);
    }
}
