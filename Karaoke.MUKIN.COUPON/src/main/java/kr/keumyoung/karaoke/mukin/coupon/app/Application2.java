package kr.keumyoung.karaoke.mukin.coupon.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import kr.keumyoung.karaoke.mukin.coupon.BuildConfig;

public class Application2 extends Application {
    public String email;
    public String coupon;
    public String sdate;
    public String edate;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sendUser();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    TextHttpResponseHandler responsHandler;
    public void setResponsHandler(TextHttpResponseHandler responsHandler) {
        this.responsHandler = responsHandler;
    }
    /**
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     */
    protected void sendUser() {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        this.email = sharedPref.getString("email", "");
        this.coupon = sharedPref.getString("coupon", "");
        String kind = "Q".toUpperCase();

        if (!this.email.isEmpty() && !this.coupon.isEmpty()) sendQuery(kind, this.email, this.coupon);
    }

    public void sendQuery(String kind, String email, String coupon) {
        //String url = "https://www.keumyoung.kr:444/mukinapp/coupon.2.asp";
        String url = "http://www.keumyoung.kr:80/mukinapp/coupon.2.asp";
        //url += "?kind=i";
        //url += "&email=" + email;
        //url += "&coupon=" + coupon;

        /**
         * Create empty RequestParams and immediately add some parameters:
         */
        RequestParams params = new RequestParams();
        params.put("kind", kind);
        params.put("email", email);
        params.put("coupon", coupon);

        if (BuildConfig.DEBUG) Log.d(__CLASSNAME__, getMethodName() + url + "?" + params);
        url = url + "?" + params;

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
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                Application2.this.onFailure(statusCode, headers, response, e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Application2.this.onSuccess(statusCode, headers, response);
            }
        });
    }

    public SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMddhhmm");
    //SimpleDateFormat f2 = new SimpleDateFormat("yyyy년MM월dd일 hh시mm분");
    public SimpleDateFormat f2 = new SimpleDateFormat("yyyy/MM/dd-hh:mm");

    SharedPreferences sharedPref;

    //@Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        // called when response HTTP status is "200 OK"
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
        //Log.e(__CLASSNAME__, "[text]" + response);
        //showProgress(false);
        //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            JSONObject json = new JSONObject(response).getJSONObject("JSN");
            Log.e(__CLASSNAME__, "[JSN]" + json.toString(2));
            JSONObject err = json.getJSONObject("ERR");
            Log.e(__CLASSNAME__, "[ERR]" + err.toString(2));
            JSONObject dat = json.getJSONArray("DAT").getJSONObject(0);
            Log.e(__CLASSNAME__, "[DAT]" + dat.toString(2));
            String code = err.getString("code");
            String message = err.getString("message");
            this.email = (dat.has("email") ? dat.getString("email") : null);
            editor.putString("email", this.email);
            this.coupon = (dat.has("coupon_num") ? dat.getString("coupon_num") : null);
            editor.putString("coupon", this.coupon);
            this.sdate = (dat.has("limit_sdate") ? dat.getString("limit_sdate") : null);
            this.edate = (dat.has("limit_edate") ? dat.getString("limit_edate") : null);
            String date = "기간:" + f2.format(f1.parse(sdate)) + "~" + f2.format(f1.parse(edate));
            Log.e(__CLASSNAME__, "[date]" + date);
            String msg = "[" + code +"]";
            msg += message;
            msg += "\n" + date;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
        if (this.responsHandler != null) this.responsHandler.onSuccess(statusCode, headers, response);
    }

    //@Override
    public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "onFailure(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response + ",'" + e.getMessage());
        Log.e(__CLASSNAME__, "[text]" + response);
        //showProgress(false);
        if (this.responsHandler != null) this.responsHandler.onFailure(statusCode, headers, response, e);
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
