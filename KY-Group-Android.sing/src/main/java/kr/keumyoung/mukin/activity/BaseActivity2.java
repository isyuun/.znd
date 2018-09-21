package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.preference.PreferenceActivity;

import kr.keumyoung.karaoke.mukin.coupon.apps._preference;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;

public class BaseActivity2 extends BaseActivity {
    protected void openPreferenceCoupon() {
        Intent i = new Intent(this, _preference.class);
        i.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, _coupon.class.getName() );
        i.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        startActivity(i);
    }
}
