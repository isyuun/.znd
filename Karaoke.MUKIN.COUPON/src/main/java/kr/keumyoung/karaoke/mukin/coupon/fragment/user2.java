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

public class user2 extends user {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCouponView = (AutoCompleteTextView) findViewById(R.id.coupon);

        mCouponView.addTextChangedListener(new TextWatcher() {
            String lastChar = " ";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int digits = mCouponView.getText().toString().length();
                if (digits > 1)
                    lastChar = mCouponView.getText().toString().substring(digits - 1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int digits = mCouponView.getText().toString().length();
                Log.d("LENGTH", "" + digits);
                if (!lastChar.equals("-")) {
                    if (digits == 4 || digits == 9 || digits == 14) {
                        mCouponView.append("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getGoogleAccount();
        //기본패스워드
        mPasswordView.setText("user1234");
    }

    @Override
    protected String getGoogleAccount() {
        String email = super.getGoogleAccount();
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[ACCOUNT]" + email);
        mEmailView.setText(email);
        return email;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (BuildConfig.DEBUG)
            Log.e(__CLASSNAME__, getMethodName() + requestCode + permissions + grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGoogleAccount();
            }
        }
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

    /**
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     */
    protected void sendUser() {
        String email = mEmailView.getText().toString();
        String coupon = mCouponView.getText().toString().replace("-", "");
        //String url = "https://www.keumyoung.kr:444/mukinapp/coupon.asp";
        String url = "http://www.keumyoung.kr:80/mukinapp/coupon.asp";
        //url += "?kind=i";
        //url += "&email=" + email;
        //url += "&coupon=" + coupon;
        /**
         * Create empty RequestParams and immediately add some parameters:
         */
        RequestParams params = new RequestParams();
        params.put("kind", "i");
        params.put("email", email);
        params.put("coupon", coupon);
        ///**
        // * Create RequestParams for a single parameter:
        // */
        //RequestParams params = new RequestParams("single", "value");
        ///**
        // * Create RequestParams from an existing Map of key/value strings:
        // */
        //HashMap<String, String> paramMap = new HashMap<String, String>();
        //paramMap.put("key", "value");
        //RequestParams params = new RequestParams(paramMap);
        if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + url + "?" + params);

        AsyncHttpClient client = new AsyncHttpClient(80, 444);
        //try {
        //    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        //    keyStore.load(new FileInputStream(new File("keystoreCompletePath")), ("passwdKeyStore").toCharArray());
        //    client.setSSLSocketFactory((new MyCustomSSLFactory(keyStore)).getFixedSocketFactory());
        //    //} catch (KeyStoreException e) {
        //    //    e.printStackTrace();
        //    //} catch (IOException e) {
        //    //    e.printStackTrace();
        //    //} catch (NoSuchAlgorithmException e) {
        //    //    e.printStackTrace();
        //    //} catch (CertificateException e) {
        //    //    e.printStackTrace();
        //    //} catch (KeyManagementException e) {
        //    //    e.printStackTrace();
        //    //} catch (UnrecoverableKeyException e) {
        //    //    e.printStackTrace();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        /**
         * text
         */
        client.post(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // called when response HTTP status is "200 OK"
                if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
                Log.e(__CLASSNAME__, "[text]" + response);
                showProgress(false);
                if (response.equalsIgnoreCase("no data")) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response + ",'" + e.getMessage());
                Log.e(__CLASSNAME__, "[text]" + response);
                showProgress(false);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
            }
        });
        ///**
        // * resp
        // */
        //client.post(url, params, new AsyncHttpResponseHandler() {
        //
        //    @Override
        //    public void onStart() {
        //        // called before request is started
        //    }
        //
        //    @Override
        //    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        //        // called when response HTTP status is "200 OK"
        //        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
        //        Log.e(__CLASSNAME__, response);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        //        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + errorResponse + ",'" + e.getMessage());
        //        Log.e(__CLASSNAME__, errorResponse.toString());
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onRetry(int retryNo) {
        //        // called when request is retried
        //    }
        //
        //    @Override
        //    public void onFinish() {
        //        // Completed the request (either success or failure)
        //    }
        //});
        ///**
        // * json
        // */
        //client.post(url, params, new JsonHttpResponseHandler() {
        //
        //    @Override
        //    public void onStart() {
        //        // called before request is started
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onStart()");
        //    }
        //
        //    @Override
        //    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        //        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
        //        super.onSuccess(statusCode, headers, response);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        //        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
        //        super.onSuccess(statusCode, headers, response);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        //        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + responseString);
        //        super.onSuccess(statusCode, headers, responseString);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + throwable.getMessage() + errorResponse);
        //        super.onFailure(statusCode, headers, throwable, errorResponse);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + throwable.getMessage() + errorResponse);
        //        super.onFailure(statusCode, headers, throwable, errorResponse);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + throwable.getMessage());
        //        super.onFailure(statusCode, headers, responseString, throwable);
        //        showProgress(false);
        //    }
        //
        //    @Override
        //    public void onRetry(int retryNo) {
        //        // called when request is retried
        //        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onRetry(...)" + retryNo);
        //    }
        //});

    }

    private HashMap<String, String> convertHeadersToHashMap(Header[] headers) {
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
