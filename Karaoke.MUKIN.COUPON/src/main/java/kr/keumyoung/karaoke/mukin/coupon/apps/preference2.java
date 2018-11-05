package kr.keumyoung.karaoke.mukin.coupon.apps;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.util.List;

import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;
import kr.keumyoung.karaoke.mukin.coupon.fragment._login;
import kr.keumyoung.karaoke.mukin.coupon.fragment._notice;
import kr.keumyoung.karaoke.mukin.coupon.fragment._privacy;
import kr.keumyoung.karaoke.mukin.coupon.fragment._user;
import kr.keumyoung.karaoke.mukin.coupon.fragment._version;

public class preference2 extends preference {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + target);
        //for (Header h : target) {
        //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + h.fragment);
        //}
    }

    @Override
    public void loadHeadersFromResource(int resid, List<Header> target) {
        super.loadHeadersFromResource(resid, target);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + resid + ":" + target);
        for (Header h : target) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + h.fragment);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        boolean ret = super.isValidFragment(fragmentName);
        ret |= _coupon.class.getName().equals(fragmentName);
        ret |= _login.class.getName().equals(fragmentName);
        ret |= _notice.class.getName().equals(fragmentName);
        ret |= _privacy.class.getName().equals(fragmentName);
        ret |= _user.class.getName().equals(fragmentName);
        ret |= _version.class.getName().equals(fragmentName);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + fragmentName + ":" + ret);
        return ret;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + l + ":" + v + ":" + position + ":" + id);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[PREF]" + getMethodName() + header.fragment + ":" + position);
        if (header.titleRes == R.string.pref_app_opensource) {
            Intent intent = new Intent(this, OssLicensesMenuActivity.class);
            String title = getString(R.string.pref_app_opensource);
            intent.putExtra("title", title);
            startActivity(intent);
        } else {
            super.onHeaderClick(header, position);
        }
    }
}

