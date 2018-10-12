package kr.keumyoung.mukin.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import kr.keumyoung.mukin.util.PreferenceKeys;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity2 extends LoginActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        finish();
    }
}
