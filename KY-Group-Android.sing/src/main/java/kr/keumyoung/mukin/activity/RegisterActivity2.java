package kr.keumyoung.mukin.activity;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import kr.kymedia.karaoke.util.EnvironmentUtils;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity2 extends RegisterActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateAutoComplete();
    }

    protected String getGoogleAccount() {
        String email = null;
        Account accounts[] = EnvironmentUtils.getGoogleAccount(this);
        if (accounts.length > 0) {
            email = accounts[0].name;
        }
        return email;
    }

    private static final int REQUEST_READ_CONTACTS = 0;

    private void populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return;
        }

        emailEt.setText(getGoogleAccount());
    }

    protected boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailEt, kr.keumyoung.karaoke.mukin.coupon.R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

}
