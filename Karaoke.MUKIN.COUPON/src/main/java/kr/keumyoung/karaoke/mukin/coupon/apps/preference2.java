package kr.keumyoung.karaoke.mukin.coupon.apps;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.List;

import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;

public class preference2 extends preference {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if(BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + target);
        for (Header h :target) {
            if(BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + h);
        }
        super.onBuildHeaders(target);
    }

    @Override
    public void loadHeadersFromResource(int resid, List<Header> target) {
        if(BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + target);
        for (Header h :target) {
            if(BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + h);
        }
        super.loadHeadersFromResource(resid, target);
    }
}

