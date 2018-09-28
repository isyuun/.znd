package kr.keumyoung.mukin.activity;

import android.accounts.Account;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kr.kymedia.karaoke.util.EnvironmentUtils;

import static android.Manifest.permission.READ_CONTACTS;

public class SplashScreenActivity2 extends SplashScreenActivity {
    protected String getGoogleAccount() {
        String email = null;
        Account accounts[] = EnvironmentUtils.getGoogleAccount(this);
        if (accounts.length > 0) {
            email = accounts[0].name;
        }
        return email;
    }
}
