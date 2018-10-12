package kr.keumyoung.mukin;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;
import kr.keumyoung.karaoke.mukin.coupon.app._Application;
import kr.keumyoung.mukin.dagger.DaggerMainComponent;
import kr.keumyoung.mukin.dagger.MainComponent;
import kr.keumyoung.mukin.util.MicChecker;
import kr.keumyoung.mukin.util.RandromAlbumImage;
import kr.keumyoung.mukin.util.TypeFaceUtil;


/**
 * on 11/01/18.
 */

public class MainApplication extends _Application {
    static MainApplication mInstance;

    public static MainApplication getInstance() {
        return mInstance;
    }

    private MainComponent mainComponent;

    public MainComponent getMainComponent() {
        return mainComponent;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mainComponent = DaggerMainComponent.builder().build();
        TypeFaceUtil.overrideFonts(this);

        // for facebook session
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // for kakao session
        Session.initialize(this);

        // key hash
//        generateAndShowKeyHash();

        MicChecker.createInstance(this); //dsjung
        RandromAlbumImage.createInstance(this); //dsjung
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MicChecker.getInstance().unregistBrocastReceiver();
    }

    private void generateAndShowKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
