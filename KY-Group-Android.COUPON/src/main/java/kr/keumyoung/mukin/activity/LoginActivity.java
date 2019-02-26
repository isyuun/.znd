package kr.keumyoung.mukin.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.PreferenceKeys;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends _BaseActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    ToastHelper toastHelper;

    @Inject
    RestApi restApi;

    @BindView(R.id.signup_anchor)
    TextView signupAnchor;
    @BindView(R.id.login_button)
    FrameLayout loginButton;
    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.login_text)
    TextView loginText;

    @Override
    protected void setFlags() {
        //super.setFlags();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate();
    }

    protected void onCreate() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login, null, false);
        inflateContainerView(view);

        MainApplication.getInstance().getMainComponent().inject(this);

        ButterKnife.bind(this, view);

        updateSavedValue();
    }

    protected void updateSavedValue() {
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
            }
        }
    }

    @Override
    protected void populateAutoComplete() {
        super.populateAutoComplete();

        String savedEmail = preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        if (savedEmail == null || savedEmail.isEmpty()) {
            emailEt.setText(getGoogleAccount());
        }
    }

    @Override
    protected boolean mayRequestPermissions() {
        boolean ret = super.mayRequestPermissions();
        if (!ret) {
            Snackbar.make(emailEt, kr.keumyoung.karaoke.mukin.coupon.R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        }
        return ret;
    }

    @Override
    protected void onLoginSuccess(String email, String nickName) {
        super.onLoginSuccess(email, nickName);
        loginSetting();
        getFragmentManager().popBackStack();
    }

    @Override
    protected void onLogoutSuccess() {
        super.onLogoutSuccess();
        //if (!BuildConfig.DEBUG)
        {
            //emailEt.setText("");
            passwordEt.setText("");
        }
        loginSetting();
    }

    protected boolean isLogin() {
        return (!preferenceHelper.getString(getString(R.string.email)).isEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginSetting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + (requestCode == KARAOKE_INTENT_ACTION_REGISTER) + ":" + (requestCode == KARAOKE_INTENT_ACTION_REGISTER) + ":" + requestCode + ":" + resultCode + ":" + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KARAOKE_INTENT_ACTION_REGISTER && resultCode == KARAOKE_RESULT_LOGIN_SUCCESS) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[KARAOKE_INTENT_ACTION_REGISTER]" + "[KARAOKE_RESULT_LOGIN_SUCCESS]");
            finish();
        }
    }

    protected void loginSetting() {
        updateSavedValue();
        if (isLogin()) {
            loginText.setText(R.string.logout);
            emailEt.setEnabled(false);
            passwordEt.setEnabled(false);
            loginText.setBackgroundColor(R.drawable.primary_button_bg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                emailEt.setTextColor(getColor(android.R.color.darker_gray));
                emailEt.setTextColor(getColor(android.R.color.darker_gray));
            } else {
                emailEt.setTextColor(getResources().getColor(android.R.color.darker_gray));
                emailEt.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        } else {
            loginText.setText(R.string.login);
            emailEt.setEnabled(true);
            passwordEt.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loginText.setBackground(getDrawable(R.drawable.primary_button_bg));
            } else {
                loginText.setBackground(getResources().getDrawable(R.drawable.primary_button_bg));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                emailEt.setTextColor(getColor(android.R.color.black));
                emailEt.setTextColor(getColor(android.R.color.black));
            } else {
                emailEt.setTextColor(getResources().getColor(android.R.color.black));
                emailEt.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    @OnClick({R.id.signup_anchor, R.id.login_button})
    public void onViewClicked(View view) {
        Log.e(__CLASSNAME__, "onViewClicked(...)" + ":" + view);
        hideKeyboard(this);
        switch (view.getId()) {
            case R.id.signup_anchor:
                openPreferenceRegister();
                break;
            case R.id.login_button:
                //로그아웃
                if (isLogin()) {
                    callLogout();
                    return;
                }
                //로그인
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

                login(email, password);

                break;
        }
    }
}
