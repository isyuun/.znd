package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.util.BlurTransformation;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.RandromAlbumImage;

/**
 * on 05/09/17.
 */

public class MediaManager {

    public static final String TAG = MediaManager.class.getName();

    @Inject
    Context context;

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    public MediaManager() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public void loadImageIntoView(String url, ImageView imageView) {
        String formattedUrl = String.format("%s%s%s?api_key=%s&session_token=%s",
                AppConstants.API_BASE_URL,
                Constants.FILE_API,
                url,
                AppConstants.DF_API_KEY,
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN));

        //Log.d(TAG, " loadImageIntoView img url = " + url);
        if (BuildConfig.DEBUG) Log.d(TAG, "loadImageIntoView" + "[" + url + "][" + formattedUrl + "]");

        int res = RandromAlbumImage.getInstance().getAlbumResourceID();

        ////db 바뀌면 어차피 바뀔텐데...isyuun:그런다고 마구 박으면...
        if (url.compareToIgnoreCase("images/album/albumart.jpg") == 0) {
            if (imageView.getDrawable() == null || imageView.getTag() == null || ((Integer) imageView.getTag()) == 0) {
                //imageView.setImageResource(res);
                Picasso.get().load(res).into(imageView);
                imageView.setTag(res);
            }
        } else {
            Picasso.get().load(formattedUrl).into(imageView);
        }
    }

    public void loadImageIntoViewBlur(String url, ImageView imageView) {
        String formattedUrl = String.format("%s%s%s?api_key=%s&session_token=%s",
                AppConstants.API_BASE_URL,
                Constants.FILE_API,
                url,
                AppConstants.DF_API_KEY,
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN));

        Log.d(TAG, " loadImageIntoViewBlur img url = " + url);

        //dsjung blur test
        Picasso.get()
                .load(formattedUrl)
                .transform(new BlurTransformation(context, 2)).into(imageView);
    }

    public void setPlayerImages(String url, ImageView imageViewAlbum, ImageView imageViewBackgrounf) {
        String formattedUrl = String.format("%s%s%s?api_key=%s&session_token=%s",
                AppConstants.API_BASE_URL,
                Constants.FILE_API,
                url,
                AppConstants.DF_API_KEY,
                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN));

        Log.d(TAG, " loadImageIntoView img url = " + url);

        int res = RandromAlbumImage.getInstance().getAlbumResourceID();

        //db 바뀌면 어차피 바뀔텐데...
        if (res != 0 && url.compareToIgnoreCase("images/album/albumart.jpg") == 0) {
            imageViewAlbum.setImageResource(res);
            //blur
            //Picasso.get().load(res).transform(new BlurTransformation(context, 2)).into(imageViewBackgrounf);

            Picasso.get()
                    .load(res).placeholder(R.drawable.images_album_albumart_01)
                    .transform(new BlurTransformation(context, 2)).into(imageViewBackgrounf);
        } else {
            Picasso.get().load(formattedUrl).placeholder(R.drawable.images_album_albumart_01).into(imageViewAlbum);
            Picasso.get()
                    .load(formattedUrl).placeholder(R.drawable.images_album_albumart_01)
                    .transform(new BlurTransformation(context, 2)).into(imageViewBackgrounf);
        }
    }

    public void loadLocalFileIntoImage(String path, ImageView imageView) {
        Uri uri = Uri.fromFile(new File(path));
        Picasso.get().load(uri).into(imageView);
    }
}
