package kr.keumyoung.mukin.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import javax.inject.Inject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;
import kr.keumyoung.mukin.helper.AnimationHelper;

public class BaseListFragment extends _BaseFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Inject
    AnimationHelper animationHelper;

    public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onBackPressed();
        if (getActivity().findViewById(R.id.swipe_refresh) != null) {
            ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh)).setRefreshing(false);
        }
    }

    public void refresh() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.refresh();
        if (getActivity().findViewById(R.id.swipe_refresh) != null) {
            ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh)).setRefreshing(false);
        }
        populateSongs();
    }

    Songs songs = new Songs();
    SongAdapter songAdapter;

    public void populateSongs() {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        populateSongs(0);
    }

    protected void populateSongs(int offset) {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + offset);
    }

    protected void onPopulateSongs() {
        updateSongs();
    }


    public void updateSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName()/* + ":" + activity.getFavorites() + ":" + songs + ":" + songAdapter*/);
        for (Song song : songs) {
            song.setFavorite(activity.isFavorites(song.getSongId()));
            song.setReserve(activity.getApp().isReserves(song.getSongId()));
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        postDelayed(notifyDataSetChanged, 100);
    }

    private Runnable notifyDataSetChanged = () -> {
        if (songAdapter != null) {
            songAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onResume();
        updateSongs();
    }

    protected void updateEmptyVisibility() {
        try {
            RecyclerView songsRecycler = (RecyclerView) findViewById(R.id.recycler);
            SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
            LinearLayout emptyFrame = (LinearLayout) findViewById(R.id.empty_frame);
            if (swipeRefresh != null && swipeRefresh.isRefreshing())
                swipeRefresh.setRefreshing(false);
            if (songs.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(songsRecycler);
                animationHelper.showWithZoomAnim(emptyFrame);
            } else if (songsRecycler.getVisibility() != View.VISIBLE) {
                animationHelper.showWithZoomAnim(songsRecycler);
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            } else if (emptyFrame.getVisibility() == View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
