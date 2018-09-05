package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.karaoke.api.MyCustomSSLFactory;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;
import kr.keumyoung.karaoke.mukin.coupon.R;

public class login extends user2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    /**
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
        super.onActivityCreated(savedInstanceState);

        findViewById(R.id.form_coupon).setVisibility(View.GONE);
        findViewById(R.id.form_email).setVisibility(View.VISIBLE);
        findViewById(R.id.form_password).setVisibility(View.GONE);

        //test
        mCouponView.setText("75UA-7TV4-US61-2Y41"); //정상:75UA-7TV4-US61-2Y41
        //mCouponView.setText("6N69-FJTV-7JWA-WH5F"); //오류:6N69-FJTV-7JWA-WH5F
    }

    @Override
    protected void sendUser() {
        super.sendUser();
    }
}
