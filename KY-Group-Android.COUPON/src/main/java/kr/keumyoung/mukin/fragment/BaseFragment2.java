package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;

public class BaseFragment2 extends BaseFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    Handler handler = new Handler();

    public final boolean post(Runnable r) {
        handler.removeCallbacks(r);
        return handler.post(r);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        handler.removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }

    _BaseFragment currentChildFragment;

    public _BaseFragment getChildCurrentFragment() {
        return currentChildFragment;
    }

    public void populateSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        populateSongs(0);
    }

    protected void populateSongs(int offset) {
    }

    public void onBackPressed() {
        if (getActivity().findViewById(R.id.swipe_refresh) != null) {
            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refresh)).setRefreshing(false);
        }
    }

    public void refresh() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        if (getActivity().findViewById(R.id.swipe_refresh) != null) {
            ((SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refresh)).setRefreshing(false);
        }
        populateSongs();
    }

    protected void onPopulateSongs() {
        updateFavoriteSongs();
    }


    Songs songs = new Songs();
    SongAdapter songAdapter;

    public void updateFavoriteSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + activity.getFavorites() + ":" + songs + ":" + songAdapter);
        for (Song song : songs) {
            song.setFavorite(activity.isFavorites(song.getSongId()));
        }
        if (songAdapter != null) {
            songAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onResume();
        updateFavoriteSongs();
    }
}
