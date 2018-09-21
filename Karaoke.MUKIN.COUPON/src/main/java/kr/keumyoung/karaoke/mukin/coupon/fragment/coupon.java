package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

        /**
         * mCouponView.setText("75UA7TV4US612Y41"); //정상:75UA7TV4US612Y41
         * mCouponView.setText("6N69FJTV7JWAWH5F"); //오류:6N69FJTV7JWAWH5F
         */
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String coupon = sharedPref.getString(getString(R.string.coupon), ""/*mCouponView.getText().toString()*/);
        /*if (!coupon.isEmpty()) */mCouponView.setText(coupon);

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
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     */
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void sendUser() {
        if (getCoupon() != null && !getCoupon().isEmpty()) {
            //String text = getString(R.string.message_already_coupon, getCoupon());
            String text = getString(R.string.message_already_coupon_1);
            text += "\n" + getApplication().checkDate();
            Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
            showProgress(false);
            return;
        }
        String email = mEmailView.getText().toString();
        String coupon = mCouponView.getText().toString().toUpperCase()/*.replace("-", "")*/;
        Log.e(__CLASSNAME__, getMethodName() + "[email]" + email + "[coupon]" + coupon);
        getApplication().send("I", email, coupon);
    }

    @Override
    public void onSuccess(int status, Header[] headers, String response) {
        super.onSuccess(status, headers, response);
        if (getCoupon() != null && !getCoupon().isEmpty()) {
            Log.e(__CLASSNAME__, getMethodName() + ":" + mCouponView + ":" + getCoupon() + ":" + getApplication().checkDate());
            ((TextView)findViewById(R.id.coupon_date)).setText(getApplication().checkDate() + "\n");
            findViewById(R.id.coupon_date).setVisibility(View.VISIBLE);
            mCouponView.setEnabled(false);
        } else {
            ((TextView)findViewById(R.id.coupon_date)).setText("\n");
            findViewById(R.id.coupon_date).setVisibility(View.GONE);
            mCouponView.setEnabled(true);
        }
    }
}
