package kr.keumyoung.mukin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

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

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return MainApplication.getInstance();
                }
            };
        }
    }

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mainComponent = DaggerMainComponent.builder().build();
        //isyuun:갤럭시S9+오류발생
        //TypeFaceUtil.overrideFonts(this);

        // for facebook session
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // for kakao session
        //Session.initialize(this);
        KakaoSDK.init(new KakaoSDKAdapter());

        // key hash
        //generateAndShowKeyHash();

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
