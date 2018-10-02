package kr.keumyoung.mukin.activity;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import kr.keumyoung.karaoke.mukin.coupon.apps._preference;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;
import kr.keumyoung.mukin.MainApplication;
import kr.kymedia.karaoke.util.EnvironmentUtils;

import static android.Manifest.permission.READ_CONTACTS;

public class BaseActivity2 extends BaseActivity {
    //private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    public MainApplication getMainApplication() {
        return (MainApplication) super.getApplication();
    }

    protected String getGoogleAccount() {
        String email = null;
        Account accounts[] = EnvironmentUtils.getGoogleAccount(this);
        if (accounts.length > 0) {
            email = accounts[0].name;
        }
        return email;
    }

    protected static final int REQUEST_READ_CONTACTS = 0;

    protected void populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return;
        }
    }

    protected boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
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

    public final boolean post(Runnable r) {
        handler.removeCallbacks(r);
        return handler.post(r);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        handler.removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }

    protected void openPreference() {
        postDelayed(openPreference, 500);
    }

    Runnable openPreference = () -> {
        Intent i = new Intent(BaseActivity2.this, _preference.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    };

    protected void openPreferenceCoupon() {
        postDelayed(openPreferenceCoupen, 500);
    }

    Runnable openPreferenceCoupen = () -> {
        Intent i = new Intent(BaseActivity2.this, _preference.class);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, _coupon.class.getName());
        i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        startActivity(i);
    };
}
