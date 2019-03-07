package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;

public class SplashScreenActivity3 extends SplashScreenActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onCompleteCopyFilesToLocal() {
        super.onCompleteCopyFilesToLocal();
    }

    @Override
    protected void proceedToNextActivity() {
        super.proceedToNextActivity();
        getApp().sendUser();
    }

    @Override
    protected void openHomeActivity() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData() + ":" + paused);
        if (paused) {
            putRunning(openHomeActivity);
        } else {
            post(openHomeActivity);
        }
    }

    private Runnable openHomeActivity = () -> {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, "openHomeActivity(...)" + getIntent().getData());
        putRunning(null);
        Intent i = new Intent(this, _HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivity(this, i, null);
        finish();
    };
}
