package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;

public class SplashScreenActivity3 extends SplashScreenActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

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
    Runnable running = null;

    @Override
    protected void onPause() {
        removeCallbacks(running);
        super.onPause();
    }

    @Override
    protected void onResume() {
        postDelayed(running, 100);
        running = null;
        super.onResume();
    }


    @Override
    protected void openHomeActivity() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
        post(openHomeActivity);
        running = openHomeActivity;
    }

    private Runnable openHomeActivity = () -> {
        Intent i = new Intent(this, _HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivity(this, i, null);
        finish();
    };

    /**
     * {@link SplashScreenActivity3#openHomeActivity()}에서 알아서 한다 오지랄 하지마
     */
    @Override
    protected void onCompleteCopyFilesToLocal() {
        super.onCompleteCopyFilesToLocal();
    }
}
