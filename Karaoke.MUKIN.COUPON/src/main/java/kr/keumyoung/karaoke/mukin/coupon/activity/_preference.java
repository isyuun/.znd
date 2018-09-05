package kr.keumyoung.karaoke.mukin.coupon.activity;

import android.util.Log;

import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;
import kr.keumyoung.karaoke.mukin.coupon.fragment._login;
import kr.keumyoung.karaoke.mukin.coupon.fragment._notice;
import kr.keumyoung.karaoke.mukin.coupon.fragment._privacy;
import kr.keumyoung.karaoke.mukin.coupon.fragment._version;

public class _preference extends preference2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + fragmentName);
        boolean ret = super.isValidFragment(fragmentName);
        ret |= _login.class.getName().equals(fragmentName);
        ret |= _coupon.class.getName().equals(fragmentName);
        ret |= _notice.class.getName().equals(fragmentName);
        ret |= _privacy.class.getName().equals(fragmentName);
        ret |= _version.class.getName().equals(fragmentName);
        return ret;
    }
}
