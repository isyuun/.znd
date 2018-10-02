package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.KEUMYOUNG;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;

public class user2 extends user {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //기본패스워드
        mPasswordView.setText(KEUMYOUNG.DEFAULT_PASS);

        mCouponView = (AutoCompleteTextView) findViewById(R.id.coupon);

        //mCouponView.addTextChangedListener(new TextWatcher() {
        //    String lastChar = " ";
        //
        //    @Override
        //    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //        int digits = mCouponView.getText().toString().length();
        //        if (digits > 1)
        //            lastChar = mCouponView.getText().toString().substring(digits - 1);
        //    }
        //
        //    @Override
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //        int digits = mCouponView.getText().toString().length();
        //        Log.d("LENGTH", "" + digits);
        //        if (!lastChar.equals("-")) {
        //            if (digits == 4 || digits == 9 || digits == 14) {
        //                mCouponView.append("-");
        //            }
        //        }
        //    }
        //
        //    @Override
        //    public void afterTextChanged(Editable s) {
        //    }
        //});


    }

    protected AutoCompleteTextView mCouponView;

    protected boolean isCouponValid(String coupon) {
        //TODO: Replace this with your own logic
        return (coupon.replace("-", "").length() == 16);
    }

    @Override
    protected void attemptLogin() {
        //super.attemptLogin();

        // Reset errors.
        mCouponView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String coupon = mCouponView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid coupon address.
        if (TextUtils.isEmpty(coupon)) {
            mCouponView.setError(getString(R.string.error_field_required));
            focusView = mCouponView;
            cancel = true;
        } else if (!isCouponValid(coupon)) {
            mCouponView.setError(getString(R.string.error_invalid_coupon));
            focusView = mCouponView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            sendUser();
        }
    }

    protected void sendUser() {
    }

    HashMap<String, String> convertHeadersToHashMap(Header[] headers) {
        if (headers == null) return null;
        HashMap<String, String> result = new HashMap<String, String>(headers.length);
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
            if (BuildConfig.DEBUG)
                Log.i(__CLASSNAME__, "[" + header.getName() + "]" + header.getValue());
        }
        return result;
    }
}
