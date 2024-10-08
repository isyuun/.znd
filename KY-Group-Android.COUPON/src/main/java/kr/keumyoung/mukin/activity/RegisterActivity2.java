package kr.keumyoung.mukin.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity2 extends RegisterActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.email = emailEt.getText().toString();
        this.pass = passwordEt.getText().toString();
        populateAutoComplete();
    }

    @Override
    protected void setFlags() {
        //super.setFlags();
    }

    @Override
    protected void populateAutoComplete() {
        super.populateAutoComplete();
        emailEt.setText(getGoogleAccount());
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
        setResult(KARAOKE_RESULT_LOGIN_SUCCESS);
        finish();
    }
}
