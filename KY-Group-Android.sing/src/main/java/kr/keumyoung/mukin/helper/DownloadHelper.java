package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.util.PreferenceKeys;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

/**
 *  on 30/01/18.
 * Project: KyGroup
 */

public class DownloadHelper {

    @Inject
    RestApi restApi;

    @Inject
    Context context;

    @Inject
    BlurHelper blurHelper;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    public DownloadHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public Observable<File> download(String url, String fileName) {
        return restApi.downloadFile(formatUrl(url)).flatMap(response -> saveFile(response, fileName));
    }

    private String formatUrl(String url) {
        return String.format("%s?api_key=%s&session_token=%s", url, AppConstants.DF_API_KEY, preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN));
    }

    public Observable<Bitmap> downloadBitmap(String url) {
        return restApi.downloadFile(formatUrl(url)).flatMap(this::saveBitmap);
    }

    private Observable<File> saveFile(Response<ResponseBody> response, String fileName) {
        return Observable.create(subscriber -> {
            try {
                File baseFolder = new File(ImageUtils.BASE_PATH);
                if (!baseFolder.exists()) {
                    boolean ret = baseFolder.mkdirs();
//                    System.out.println(ret);
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    subscriber.onError(new NullPointerException("file is not found"));
                } else {
                    File file = new File(ImageUtils.BASE_PATH, fileName);

                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(responseBody.source());
                    sink.close();

                    subscriber.onNext(file);
                    subscriber.onComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    private Observable<Bitmap> saveBitmap(Response<ResponseBody> response) {
        return Observable.create(subscriber -> {
            try {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    subscriber.onError(new NullPointerException("image response body is null"));
                } else {
                    InputStream input = responseBody.byteStream();
                    Bitmap inputBitmap = BitmapFactory.decodeStream(input);
                    subscriber.onNext(blurHelper.blurBitmap(inputBitmap));
                    subscriber.onComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }
}
