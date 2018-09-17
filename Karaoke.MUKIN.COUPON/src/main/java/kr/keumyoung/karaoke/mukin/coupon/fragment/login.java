package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;

public class login extends _user {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
        super.onActivityCreated(savedInstanceState);

        findViewById(R.id.form_coupon).setVisibility(View.GONE);
        findViewById(R.id.form_email).setVisibility(View.VISIBLE);
        findViewById(R.id.form_password).setVisibility(View.GONE);

    }

    @Override
    protected void attemptLogin() {
        super.attemptLogin();
    }

    @Override
    protected void sendUser() {
    }
}
