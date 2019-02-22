package kr.keumyoung.mukin.activity;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import kr.keumyoung.karaoke.mukin.coupon.apps._preference;
import kr.keumyoung.karaoke.mukin.coupon.fragment._coupon;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin._MainApplication;
import kr.keumyoung.mukin.fragment._BaseFragment;
import kr.kymedia.karaoke.util.EnvironmentUtils;

import static android.Manifest.permission.READ_CONTACTS;

public class BaseActivity2 extends BaseActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    public _MainApplication getApp() {
        return (_MainApplication) super.getApplication();
    }

    // current fragment always holds the currently added fragment to the container
    _BaseFragment currentFragment;

    public void replaceFragment(_BaseFragment fragment, boolean addToStack, int containerId) {
        currentFragment = fragment;
        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentManager manager = getSupportFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(fragmentTag, 0);

        if (!fragmentPopped) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (addToStack) transaction.addToBackStack(fragmentTag);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(containerId, fragment);
            transaction.commit();
        }
    }

    public boolean popFragment() {
        boolean result = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            result = getSupportFragmentManager().popBackStackImmediate();
            currentFragment = (_BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
        return result;
    }

    public _BaseFragment getCurrentFragment() {
        //return ((_BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        return currentFragment;
    }

    public _BaseFragment getChildCurrentFragment() {
        //return ((_BaseFragment) getSupportFragmentManager().findFragmentById(R.id.child_fragment_container));
        if (currentFragment != null) {
            return currentFragment.getChildCurrentFragment();
        }
        return null;
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

    //REQUEST
    int KARAOKE_INTENT_ACTION_DEFAULT = 0x01;
    int KARAOKE_INTENT_ACTION_PREFERENCE = 0x02;
    int KARAOKE_INTENT_ACTION_REGISTER = 0x03;
    int KARAOKE_INTENT_ACTION_LOGIN = 0x04;
    int KARAOKE_INTENT_ACTION_COUPON = 0x05;
    //RESULT
    int KARAOKE_RESULT_CANCEL = Activity.RESULT_CANCELED;
    int KARAOKE_RESULT_OK = Activity.RESULT_OK;
    int KARAOKE_RESULT_DEFAULT = Activity.RESULT_FIRST_USER;
    int KARAOKE_RESULT_REFRESH = Activity.RESULT_FIRST_USER + 1;
    int KARAOKE_RESULT_LOGIN_SUCCESS = Activity.RESULT_FIRST_USER + 2;
    int KARAOKE_RESULT_LOGIN_FAILURE = Activity.RESULT_FIRST_USER + 3;

    /**
     * <a href="https://stackoverflow.com/questions/13560243/how-to-stop-runnable-when-the-app-goes-to-background">
     * How to stop runnable when the app goes to background?
     * </a>
     */
    private Runnable running = null;
    private void clearRunning() {
        this.running = null;
    }

    public final void removeCallbacks(Runnable r)
    {
        handler.removeCallbacks(r);
    }

    /**
     * 일단 {@link #postDelayed(Runnable, long)}에서만
     */
    public final boolean post(Runnable r) {
        //this.running = r;
        removeCallbacks(r);
        return handler.post(r);
    }

    /**
     * 일단 {@link #postDelayed(Runnable, long)}에서만
     */
    public final boolean postDelayed(Runnable r, long delayMillis) {
        this.running = r;
        removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }

    @Override
    protected void onPause() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
        removeCallbacks(running);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
        postDelayed(running, 100);
        clearRunning();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
    }

    /**
     * {@link SplashScreenActivity3#openHomeActivity()}에서 알아서 한다 오지랄 하지마
     */
    protected void openHomeActivity() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
        post(openHomeActivity);
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

    public void openPreferenceLoginChoice() {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + getIntent().getData());
        post(openPreferenceLoginChoice);
    }

    private Runnable openPreferenceLoginChoice = () -> {
        Intent i = new Intent(this, _LoginChoiceActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + i.getData());
        ActivityCompat.startActivityForResult(this, i, KARAOKE_INTENT_ACTION_LOGIN, null);
    };

    protected void openPreference() {
        postDelayed(openPreference, 1000);
    }

    private Runnable openPreference = () -> {
        Intent i = new Intent(this, _preference.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivityForResult(this, i, KARAOKE_INTENT_ACTION_PREFERENCE, null);
    };

    public void openPreferenceRegister() {
        post(openPreferenceRegister);
    }

    private Runnable openPreferenceRegister = () -> {
        Intent i = new Intent(this, _RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivityForResult(this, i, KARAOKE_INTENT_ACTION_REGISTER, null);
    };

    protected void openPreferenceCoupon() {
        post(openPreferenceCoupen);
    }

    private Runnable openPreferenceCoupen = () -> {
        Intent i = new Intent(this, _preference.class);
        i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, _coupon.class.getName());
        i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        i.setData(getIntent().getData());
        getIntent().setData(null);
        ActivityCompat.startActivityForResult(this, i, KARAOKE_INTENT_ACTION_COUPON, null);
    };

    @Deprecated
    @Override
    public void showProgress() {
        //super.showProgress();
    }

    public void showProgress(boolean show) {
        if (show) super.showProgress();
    }

    @Deprecated
    @Override
    public void hideProgress() {
        super.hideProgress();
    }
}
