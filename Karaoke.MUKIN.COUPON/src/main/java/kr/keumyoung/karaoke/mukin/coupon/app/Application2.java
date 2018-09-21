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
import kr.keumyoung.karaoke.mukin.coupon.R;
import kr.kymedia.karaoke.util.DeviceUuidFactory;

public class Application2 extends Application {
    public String email;
    public String coupon;
    public String sdate;
    public String edate;

    DeviceUuidFactory device;

    @Override
    public void onCreate() {
        Log.i(__CLASSNAME__, getMethodName() + "[email]" + this.email + "[coupon]" + this.coupon);
        super.onCreate();
        device = new DeviceUuidFactory(this);
        sendUser();
    }

    @Override
    public void onTerminate() {
        Log.i(__CLASSNAME__, getMethodName() + "[email]" + this.email + "[coupon]" + this.coupon);
        super.onTerminate();
    }

    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    /**
     * 쿠폰등록시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(https) :https://www.keumyoung.kr:444/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰등록시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=i&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     * 쿠폰조회시(http)  :http://www.keumyoung.kr:80/mukinapp/coupon.2.asp?kind=q&email=test@kymedia.kr&coupon=75UA7TV4US612Y41
     */
    protected void sendUser() {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        this.email = sharedPref.getString(getString(R.string.email), "");
        //this.coupon = sharedPref.getString(getString(R.string.coupon), "");
        Log.e(__CLASSNAME__, getMethodName() + "[email]" + email + "[coupon]" + coupon);
        if (!this.email.isEmpty()) send("Q", this.email, this.coupon);
    }

    public void send(String kind, String email, String coupon) {
        //String url = "https://www.keumyoung.kr:444/mukinapp/coupon.2.asp";
        String url = "http://www.keumyoung.kr:80/mukinapp/coupon.2.asp";
        //url += "?kind=i";
        //url += "&email=" + email;
        //url += "&coupon=" + coupon;

        String device = this.device.getDeviceUuid().toString();

        /**
         * Create empty RequestParams and immediately add some parameters:
         */
        RequestParams params = new RequestParams();
        params.put(getString(R.string.kind), kind);
        params.put(getString(R.string.email), email);
        params.put(getString(R.string.coupon), coupon);
        params.put(getString(R.string.device), device);

        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + url + "?" + params);
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

    TextHttpResponseHandler responsHandler;
    public void setResponsHandler(TextHttpResponseHandler responsHandler) {
        this.responsHandler = responsHandler;
    }

    public String checkDate() {
        //2018-09-18 13:52:47.067
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //2018/09/18-13:52
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
        String date = "기간:";
        try {
            if (sdate != null && !sdate.isEmpty()) date += f2.format(f1.parse(sdate));
            if (edate != null && !edate.isEmpty()) date += "~" + f2.format(f1.parse(edate));
            //Toast.makeText(getBaseContext(), date, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    //@Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        // called when response HTTP status is "200 OK"
        if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, "onSuccess(...)" + statusCode + "," + convertHeadersToHashMap(headers) + "." + response);
        //Log.e(__CLASSNAME__, "[text]" + response);
        //showProgress(false);
        //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String code = "";
        String message = "";
        String date = "";
        try {
            JSONObject json = new JSONObject(response).getJSONObject(getString(R.string.JSN));
            //Log.e(__CLASSNAME__, "[JSN]" + json.toString(2));
            JSONObject err = json.getJSONObject(getString(R.string.ERR));
            code = err.getString(getString(R.string.code));
            message = err.getString(getString(R.string.message));
            //Log.e(__CLASSNAME__, "[ERR]" + err.toString(2));
            JSONObject dat = json.getJSONArray(getString(R.string.DAT)).getJSONObject(0);
            //Log.e(__CLASSNAME__, "[DAT]" + dat.toString(2));
            this.email = (dat.has(getString(R.string.email)) ? dat.getString(getString(R.string.email)) : null);
            editor.putString(getString(R.string.email), this.email);
            this.coupon = (dat.has("coupon_num") ? dat.getString("coupon_num") : null);
            editor.putString(getString(R.string.coupon), this.coupon);
            this.sdate = (dat.has(getString(R.string.sdate)) ? dat.getString(getString(R.string.sdate)) : null);
            this.edate = (dat.has(getString(R.string.edate)) ? dat.getString(getString(R.string.edate)) : null);
            date = checkDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(__CLASSNAME__, "[date]" + date);
        if (!code.equalsIgnoreCase("000")) {
            editor.remove(getString(R.string.coupon));
        }
        String msg = "[" + code +"]";
        msg += message;
        msg += "\n" + date;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
