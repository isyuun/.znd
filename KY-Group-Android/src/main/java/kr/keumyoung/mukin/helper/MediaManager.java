package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

/**
 *  on 05/09/17.
 */

public class MediaManager {

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

        Picasso.get().load(formattedUrl).into(imageView);
    }

    public void loadLocalFileIntoImage(String path, ImageView imageView) {
        Uri uri = Uri.fromFile(new File(path));
        Picasso.get().load(uri).into(imageView);
    }
}
