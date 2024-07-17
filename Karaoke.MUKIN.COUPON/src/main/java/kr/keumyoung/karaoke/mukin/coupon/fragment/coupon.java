package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;

public class coupon extends _user {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
        super.onActivityCreated(savedInstanceState);

        findViewById(R.id.form_coupon).setVisibility(View.VISIBLE);
        findViewById(R.id.form_email).setVisibility(View.VISIBLE);
        findViewById(R.id.form_password).setVisibility(View.GONE);

        //mCouponView.setText("20GD5RI7MT466I40"); //test //정상:20GD5RI7MT466I40//정상:92FH1NP4UB286B82
        //mCouponView.setText("6N69FJTV7JWAWH5F"); //test //오류:6N69FJTV7JWAWH5F
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String coupon = sharedPref.getString(getString(R.string.coupon), ""/*mCouponView.getText().toString()*/);
        if (!coupon.isEmpty()) mCouponView.setText(coupon);
        if (coupon.isEmpty()) mCouponView.requestFocus();

        getActivity().setTitle(R.string.pref_coupon);
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] grants) {
        super.onRequestPermissionsResult(code, permissions, grants);
    }

    @Override
    protected void attemptLogin() {
        super.attemptLogin();
    }

    /**
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=i&email=test@keumyoung.kr&coupon=20GD5RI7MT466I40
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=q&email=test@keumyoung.kr&coupon=20GD5RI7MT466I40
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=i&email=test@keumyoung.kr&coupon=20GD5RI7MT466I40
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=q&email=test@keumyoung.kr&coupon=20GD5RI7MT466I40
     */
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void sendUser() {
        if (getCoupon() != null && !getCoupon().isEmpty()) {
            //String text = getString(R.string.message_already_coupon, getCoupon());
            String text = getString(R.string.message_already_coupon_1);
            text += "\n" + getApp().getCouponDate(true);
            Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
            showProgress(false);
            getActivity().onBackPressed();
            return;
        }
        String email = mEmailView.getText().toString();
        String coupon = mCouponView.getText().toString().toUpperCase()/*.replace("-", "")*/;
        Log.e(__CLASSNAME__, getMethodName() + "[email]" + email + "[coupon]" + coupon);
        getApp().send("I", email, coupon);
    }

    @Override
    public void onSuccess(int status, Header[] headers, String response) {
        try {
            super.onSuccess(status, headers, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String coupon = getCoupon();
            if (coupon != null && !coupon.isEmpty()) {
                String couponDate = getApp().getCouponDate(false);
                Log.e(__CLASSNAME__, getMethodName() + ":" + mCouponView + ":" + getCoupon() + ":" + couponDate);
                ((TextView) findViewById(R.id.coupon_date)).setText(couponDate + "\n");
                findViewById(R.id.coupon_date).setVisibility(View.VISIBLE);
                mCouponView.setEnabled(false);
                showKeyboard(false);
            } else {
                ((TextView) findViewById(R.id.coupon_date)).setText("\n");
                findViewById(R.id.coupon_date).setVisibility(View.GONE);
                mCouponView.setEnabled(true);
                mCouponView.requestFocus();
                showKeyboard(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
