package kr.keumyoung.mukin.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.helper.ImageUtils;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.util.PreferenceKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *  on 11/01/18.
 */

public class SplashScreenActivity extends _BaseActivity {

    protected static final int REQUEST_PERMISSION = 10001;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    NavigationHelper navigationHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_splash, null, false);
        inflateContainerView(view);

        ImageUtils imageUtils = new ImageUtils(this);
        imageUtils.checkForSdCardFolder();

        checkPermissions();

        TextView version_text = (TextView)findViewById(R.id.version_text);
        if(version_text != null)
        {
            version_text.setText(String.format("Ver: 1.%s", BuildConfig.VERSION_CODE));
        }
    }

    protected void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                        }, REQUEST_PERMISSION);
            } else {
                proceedToNextActivity();
            }
        } else {
            proceedToNextActivity();
        }
    }

    public boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (verifyPermissions(grantResults)) proceedToNextActivity();
        else closeApplication();
    }

    private void closeApplication() {
        navigationHelper.finish(this);
    }

    // planning for checking whether the session is active or not here before going to the next activity when already logged in
    protected void proceedToNextActivity() {
        postDelayed(() -> {
            showProgress();
            copyFilesToLocal()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(s -> System.out.println("FILE COPY: " + s), throwable -> {
                        hideProgress();
                        throwable.printStackTrace();
                    }, () -> {
                        String userId = preferenceHelper.getString(PreferenceKeys.USER_ID);
                        String sessionToken = preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN);
                        hideProgress();
                        if (userId.isEmpty() && sessionToken.isEmpty()) {
                            // user is not logged in
                            //navigationHelper.navigate(this, LoginChoiceActivity.class);
                        } else {
                            // user is logged in
                            openHomeActivity();
                        }
                    });

//            throw new RuntimeException("Intentional crash test");
        }, 1000);
    }

    @Override
    protected boolean withStatusBar() {
        return false;
    }

    private Observable<String> copyFilesToLocal() {
        return Observable.create(subscriber -> {
            File obbDir = getObbDir();
            if (Boolean.parseBoolean(AppConstants.DEVELOPMENT))
//            if (BuildConfig.DEBUG)
                obbDir = ImageUtils.getBaseFolder();
            System.out.println("OBB DIR: " + obbDir.getAbsolutePath());
            if (obbDir.exists()) {
                String obbFileName = String.format("main.%s.%s.obb", AppConstants.OBB_VERSION, getPackageName());
                File obbFile = new File(obbDir, obbFileName);

                try {
                    final String libraryName = "Soundlib-20161010.dlgse";
                    String libraryPath = String.format("/data/data/%s/files/%s", getPackageName(), libraryName);

                    preferenceHelper.saveString(PreferenceKeys.LIBRARY_PATH, libraryPath);

                    File library = new File(libraryPath);

                    if (!library.exists()) {

                        try
                        {
                            copyFile(obbFile, library);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }


                        subscriber.onNext("Library copied...");
                    }

                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // unused
    public void unzip(String _zipFile, String _targetLocation) {
        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                FileOutputStream fout = new FileOutputStream(_targetLocation);
                for (int c = zin.read(); c != -1; c = zin.read()) fout.write(c);
                zin.closeEntry();
                fout.close();
            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /*unused*/
    private void copy2Local(String fileName) throws IOException {
        AssetManager as = getResources().getAssets();
        InputStream input = as.open(fileName);
        FileOutputStream output = openFileOutput(fileName, MODE_PRIVATE);
        int DEFAULT_BUFFER_SIZE = 1024 * 4;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = input.read(buffer))) output.write(buffer, 0, n);
        output.close();
        input.close();
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
