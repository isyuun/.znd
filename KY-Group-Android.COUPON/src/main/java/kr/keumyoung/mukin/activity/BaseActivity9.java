package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.data.model.Song;

public class BaseActivity9 extends BaseActivity8 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onReserveSelected(Song song) {
        getApp().onReserveSelected(song);
        updateReserveSongs();
    }

    public boolean isReserves(String songid) {
        return getApp().isReserves(songid);
    }

    private void updateReserveSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + getCurrentFragment() + ":" + getChildCurrentFragment());
        if (getChildCurrentFragment() != null) {
            getChildCurrentFragment().updateSongs();
        }
    }
}
