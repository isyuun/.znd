package kr.keumyoung.mukin.activity;

import android.accounts.Account;
import android.content.Intent;
import android.preference.PreferenceActivity;

import kr.keumyoung.karaoke.mukin.coupon.apps._preference;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;
import kr.keumyoung.mukin.MainApplication;
import kr.kymedia.karaoke.util.EnvironmentUtils;

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

    protected void openPreference() {
        Intent i = new Intent(this, _preference.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    protected void openPreferenceCoupon() {
        Intent i = new Intent(this, _preference.class);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, _coupon.class.getName());
        i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        startActivity(i);
    }
}
