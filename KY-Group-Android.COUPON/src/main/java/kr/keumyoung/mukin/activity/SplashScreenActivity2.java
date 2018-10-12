package kr.keumyoung.mukin.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.keumyoung.KEUMYOUNG;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.kymedia.karaoke.util.BuildUtils;

public class SplashScreenActivity2 extends SplashScreenActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageInfo pkgInfo = BuildUtils.getPackageInfo(getApplicationContext());
        if (pkgInfo != null) {
            int versionNumber = pkgInfo.versionCode;
            String versionName = pkgInfo.versionName;
            String version = getString(R.string.app_version);
            version += ":" + versionName + "(" + versionNumber + ")";
            if (BuildConfig.DEBUG) {
                version += ":" + "DEBUG";
            }
            // String installDate = DateUtils.getDate("yyyy/MM/dd hh:mm:ss", pkgInfo.lastUpdateTime);
            //String buildDate = BuildUtils.getDate("yyyy/MM/dd HH:mm", BuildUtils.getBuildDate(getApplicationContext()));
            String buildDate = (new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)).format(new Date(BuildConfig.TIMESTAMP/* + TimeZone.getTimeZone("Asia/Seoul").getRawOffset()*/));
            version += "-" + buildDate;
            TextView tv = (TextView) findViewById(R.id.version);
            if (tv != null) {
                tv.setText(version);
                tv.setSingleLine();
                //tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                //WidgetUtils.setTextViewMarquee(tv);
            }
        }
    }

    @Override
    protected void proceedToNextActivity() {
        super.proceedToNextActivity();

        this.email = getGoogleAccount();
        this.pass = KEUMYOUNG.DEFAULT_PASS;

        handler.postDelayed(() -> {
            //registerUserToDF(this.email, this.email, this.pass, "");
            navigationHelper.navigate(this, _HomeActivity.class);
        }, 2000);
    }
}
