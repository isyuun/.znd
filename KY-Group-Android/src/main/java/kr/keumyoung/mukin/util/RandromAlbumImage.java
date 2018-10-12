package kr.keumyoung.mukin.util;

import android.content.Context;
import android.util.Log;

import kr.keumyoung.mukin.R;

public class RandromAlbumImage {
    private static RandromAlbumImage instance;
    private Context mContext;
    private int start_idx = 1; //1 ~ 21 random image
    public static synchronized RandromAlbumImage getInstance() {
        return instance;
    }
    public static synchronized RandromAlbumImage createInstance(Context context) {
        if (instance == null)
            instance = new RandromAlbumImage(context);
        return instance;
    }
    public RandromAlbumImage(Context context) {
        mContext = context;
    }

    public int getAlbumResourceID()
    {
        if(start_idx >= 22)
            start_idx = 1;
        return mContext.getResources().getIdentifier( String.format("images_album_albumart_%02d", start_idx++), "drawable" ,  mContext.getPackageName());
    }




}
