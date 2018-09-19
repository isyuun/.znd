package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;
import kr.keumyoung.karaoke.mukin.coupon.app._Application;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class user3 extends user2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return getActivity().getSharedPreferences(name, mode);
    }

    public String getPackageName() {
        return getActivity().getPackageName();
    }

    public _Application getApplication() {
        return (_Application) getActivity().getApplication();
    }

    public Context getBaseContext() {
        return getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected String putEmail() {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.email), mEmailView.getText().toString());
        editor.commit();
        String email = sharedPref.getString(getString(R.string.email), "");
        return email;
    }

    protected String getCoupon() {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String coupon = sharedPref.getString(getString(R.string.coupon), "");
        if (!coupon.isEmpty()) {
            mCouponView.setText(coupon.toUpperCase());
            mCouponView.setEnabled(false);
        } else {
            mCouponView.setText(mCouponView.getText().toString().toUpperCase());
            mCouponView.setEnabled(true);
        }
        return coupon;
    }

    @Override
    protected String getGoogleAccount() {
        String email = super.getGoogleAccount();
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[ACCOUNT]" + email);
        mEmailView.setText(email);
        return email;
    }

    private void getUserInfo() {
        getGoogleAccount();
        String email = putEmail();
        Log.e(__CLASSNAME__, getMethodName() + email);
        if (!email.isEmpty()) getApplication().send("Q", email, "");
        getApplication().setResponsHandler(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                user3.this.onFailure(statusCode, headers, response, e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                user3.this.onSuccess(statusCode, headers, response);
            }
        });
    }


    //@Override
    public void onFailure(int status, Header[] headers, String response, Throwable e) {
        showProgress(false);
        getCoupon();
    }

    //@Override
    public void onSuccess(int status, Header[] headers, String response) {
        showProgress(false);
        getCoupon();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(__CLASSNAME__, getMethodName());
        super.onActivityCreated(savedInstanceState);
        populateAutoComplete();
        showProgress(true);
    }

    private void populateAutoComplete() {
        if (mayRequestPermissions()) {
            getLoaderManager().initLoader(0, null, this);
            getUserInfo();
        }
    }

    protected static final int REQUEST_PERMISSIONS = 0;

    @Override
    protected boolean mayRequestPermissions() {
        //super.mayRequestPermissions();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ArrayList<String> permissions = new ArrayList<>();
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_PERMISSIONS);
                        }
                    });
        } else {
            permissions.add(READ_CONTACTS);
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            Snackbar.make(mCouponView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_PERMISSIONS);
                        }
                    });
        } else {
            permissions.add(READ_PHONE_STATE);
        }
        requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] grants) {
        //Log.e(__CLASSNAME__, getMethodName() + code + "," + permissions + "," + grants);
        super.onRequestPermissionsResult(code, permissions, grants);
        if (code == REQUEST_PERMISSIONS) {
            if (grants.length == 2 && grants[0] == PackageManager.PERMISSION_GRANTED && grants[1] == PackageManager.PERMISSION_GRANTED) {
                Log.e(__CLASSNAME__, getMethodName() + code + "," + permissions + "," + grants);
                getUserInfo();
            }
        }
    }

}
