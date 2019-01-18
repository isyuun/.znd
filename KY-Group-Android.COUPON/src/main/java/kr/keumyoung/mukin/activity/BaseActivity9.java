package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.fragment._BaseListFragment;

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

    private void updateReserveSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + getCurrentFragment() + ":" + getChildCurrentFragment());
        if (getChildCurrentFragment() != null && getChildCurrentFragment() instanceof _BaseListFragment) {
            ((_BaseListFragment) getChildCurrentFragment()).updateSongs();
        }
    }
}
