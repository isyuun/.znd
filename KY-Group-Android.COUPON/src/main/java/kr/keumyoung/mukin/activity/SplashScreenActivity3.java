package kr.keumyoung.mukin.activity;

import android.arch.lifecycle.Lifecycle;
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

    /**
     * <a href="https://stackoverflow.com/questions/13560243/how-to-stop-runnable-when-the-app-goes-to-background">
     * How to stop runnable when the app goes to background?
     * </a>
     */
    private Runnable running = null;
    private void clearRunning() {
        this.running = null;
    }

    @Override
    protected void openHomeActivity() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData() + ":" + paused);
        if (paused) {
            this.running = openHomeActivity;
        } else {
            post(openHomeActivity);
        }
    }

    private Runnable openHomeActivity = () -> {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, "openHomeActivity(...)" + getIntent().getData());
        clearRunning();
        Intent i = new Intent(this, _HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivity(this, i, null);
        finish();
    };

    private boolean paused = false;

    @Override
    protected void onPause() {
        paused = true;
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + ":" + paused + ":" + running);
        removeCallbacks(running);
        super.onPause();
    }

    @Override
    protected void onResume() {
        paused = false;
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + ":" + paused + ":" + running);
        postDelayed(running, 100);
        clearRunning();
        super.onResume();
    }
}
