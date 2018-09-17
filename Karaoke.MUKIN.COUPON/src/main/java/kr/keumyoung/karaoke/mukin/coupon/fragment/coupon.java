package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;
import kr.keumyoung.karaoke.mukin.coupon.app.Application2;

public class coupon extends _user {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
        super.onActivityCreated(savedInstanceState);

        findViewById(R.id.form_coupon).setVisibility(View.VISIBLE);
        findViewById(R.id.form_email).setVisibility(View.GONE);
        findViewById(R.id.form_password).setVisibility(View.GONE);

        //test
        //mCouponView.setText("75UA7TV4US612Y41"); //정상:75UA7TV4US612Y41
        //mCouponView.setText("6N69FJTV7JWAWH5F"); //오류:6N69FJTV7JWAWH5F
        //
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", mEmailView.getText().toString());
        String coupon = sharedPref.getString("coupon", mCouponView.getText().toString());
        //
        if (!email.isEmpty()) mEmailView.setText(email);
        if (!coupon.isEmpty()) mCouponView.setText(coupon);
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
    @Override
    protected void sendUser() {
        String email = mEmailView.getText().toString();
        final String coupon = mCouponView.getText().toString().toUpperCase()/*.replace("-", "")*/;
        String kind = "I".toUpperCase();

        getApplication().sendQuery(kind, email, coupon);
        getApplication().setResponsHandler(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                coupon.this.onFailure(statusCode, headers, response, e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                coupon.this.onSuccess(statusCode, headers, response);
            }
        });
    }

    //@Override
    public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
        showProgress(false);
    }

    //@Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        showProgress(false);
    }

}
