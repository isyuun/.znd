package kr.keumyoung.mukin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.kymedia.karaoke.util.BuildUtils;

public class SplashScreenActivity2 extends SplashScreenActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private PackageInfo pkgInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pkgInfo = BuildUtils.getPackageInfo(getApplicationContext());
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
    protected void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                                , Manifest.permission.READ_CONTACTS
                                , Manifest.permission.READ_PHONE_STATE
                        }, REQUEST_PERMISSION);
            } else {
                proceedToNextActivity();
            }
        } else {
            proceedToNextActivity();
        }
    }

    @Override
    protected void proceedToNextActivity() {
        Log.e(__CLASSNAME__, getMethodName());

        postDelayed(() -> {
            showProgress();
            copyFilesToLocal()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            message -> {
                                onNextCopyFilesToLocal(message);
                            },
                            throwable -> {
                                onFailureCopyFilesToLocal(throwable.getMessage());
                                hideProgress();
                                throwable.printStackTrace();
                            },
                            () -> {
                                onCompleteCopyFilesToLocal();
                                /**
                                 * {@link HomeActivity2#onResume()}에서 알아서 한다 오지랄 하지마
                                 */
                            });

            //throw new RuntimeException("Intentional crash test");
        }, 100);

    }

    private void onNextCopyFilesToLocal(final String message) {
        Log.e(__CLASSNAME__, getMethodName() + ":" + message);
    }

    private void onFailureCopyFilesToLocal(final String message) {
        Log.e(__CLASSNAME__, getMethodName() + ":" + message);
        post(() -> {
            showAlertDialog(message, (dialogInterface, i) -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                finish();
            });
        });
    }

    protected void onCompleteCopyFilesToLocal() {
        Log.e(__CLASSNAME__, getMethodName());
        postDelayed(() -> {
            openHomeActivity();
        }, 3000);
    }

    private Observable<String> copyFilesToLocal() {
        return Observable.create(subscriber -> {
            File obbDir = getObbDir();
            //if (BuildConfig.DEBUG) _Log.e(__CLASSNAME__, getMethodName()+ "[OBB.DIR]" + obbDir.getAbsolutePath());
            //if (Boolean.parseBoolean(AppConstants.DEVELOPMENT)) {
            //    obbDir = ImageUtils.getBaseFolder();
            //    if (BuildConfig.DEBUG) _Log.wtf(__CLASSNAME__, getMethodName() + "[OBB][OBB.DIR][DEV]" + obbDir.getAbsolutePath());
            //}
            if (obbDir.exists()) {
                String obbFileName = "main" + "." + AppConstants.OBB_VERSION + "." + getPackageName() + "." + "obb";
                //if (BuildConfig.DEBUG) _Log.e(__CLASSNAME__, getMethodName()+ "[OBB.FILE]" + obbFileName);
                File obbFile = new File(obbDir, obbFileName);
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[OBB][OBB.PATH]" + obbFile.exists() + ":" + obbFile.getAbsolutePath());
                if (!obbFile.exists()) {
                    Log.e(__CLASSNAME__, getMethodName() + "[OBB][OBB.PATH][NG]" + obbFile.exists() + ":" + obbFile.getAbsolutePath());
                    subscriber.onError(new Exception(getResources().getString(R.string.file_obb_not_found) + "\n" + getResources().getString(R.string.please_install_again)));
                    return;
                }

                try {
                    final String libraryName = "Soundlib-20161010.dlgse";
                    String libraryPath = String.format("/data/data/%s/files/%s", getPackageName(), libraryName);

                    preferenceHelper.saveString(PreferenceKeys.LIBRARY_PATH, libraryPath);

                    File library = new File(libraryPath);

                    if (!library.exists()) {

                        try {
                            copyFile(obbFile, library);
                        } catch (IOException e) {
                            Log.e(__CLASSNAME__, getMethodName() + "[OBB][OBB.LIB][COPY][NG]" + library.exists() + ":" + library.getAbsolutePath());
                            e.printStackTrace();
                            subscriber.onError(new Exception(getResources().getString(R.string.file_lib_not_found) + "\n" + getResources().getString(R.string.please_install_again)));
                            return;
                        }

                        subscriber.onNext("[OBB][OBB.PATH]Library COPIED...");
                    }

                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[OBB][LIB.PATH]" + library.exists() + ":" + library.getAbsolutePath());
                    if (!library.exists()) {
                        Log.e(__CLASSNAME__, getMethodName() + "[OBB][LIB.PATH][NG]" + library.exists() + ":" + library.getAbsolutePath());
                        subscriber.onError(new Exception(getResources().getString(R.string.file_lib_not_found) + "\n" + getResources().getString(R.string.please_install_again)));
                        return;
                    }

                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private void copyFile(File source, File destination) throws IOException {
        InputStream input = new FileInputStream(source);
        FileOutputStream output = new FileOutputStream(destination);
        int DEFAULT_BUFFER_SIZE = 1024 * 4;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = input.read(buffer))) output.write(buffer, 0, n);
        output.close();
        input.close();
    }
}
