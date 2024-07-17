package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.SongParser;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PaginationScrollListener;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 12/01/18.
 */

public class FavoritesFragment extends _BaseListFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    AnimationHelper animationHelper;

    HomeFragment parentFragment;

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            populateSongs();
        }
    };

    TextWatcher textWatcher;

    int offset = 0;
    boolean isLoading = false, isLastPage = false;

    @BindView(R.id.recycler)
    RecyclerView songsRecycler;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        parentFragment = (HomeFragment) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh.setOnRefreshListener(this::populateSongs);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.showHeaderText(R.string.favorites);
        activity.hideMenuIcon();

        parentFragment.hideIcons();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (songAdapter == null) songAdapter = new SongAdapter(songs);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        songsRecycler.setLayoutManager(manager);
        songsRecycler.setAdapter(songAdapter);

        songsRecycler.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public void loadMoreItems() {
                offset += Constants.LIMIT;
                populateSongs(offset);
            }
        });
    }

    protected void populateSongs(int offset) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + offset);

        activity.showProgress();
        CommonHelper.hideSoftKeyboard(activity);

        this.offset = offset;

        if (offset == 0) {
            activity.showProgress();
            swipeRefresh.setRefreshing(true);
        } else {
            songAdapter.setLoading(true);
            songAdapter.notifyDataSetChanged();
        }

        isLoading = true;

        String filter = "userid=" + preferenceHelper.getString(PreferenceKeys.USER_ID);
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.FAVORITE, filter)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject responseObject = new JSONObject(responseString);

                                JSONArray songArray = responseObject.getJSONArray(Constants.RESOURCE);
                                int length = songArray.length();
                                songs.clear();
                                for (int index = 0; index < length; index++) {
                                    JSONObject songObject = songArray.getJSONObject(index);
                                    Song song = SongParser.convertToSongFromJson(songObject);
                                    song.setFavorite(activity.isFavorites(song.getSongId()));
                                    songs.add(song);
                                }
                                if (activity.isShowingProgress()) activity.hideProgress();

                                if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                //songAdapter.notifyDataSetChanged();
                                updateSongs();

                                updateEmptyVisibility();
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                JSONObject errorObject = new JSONObject(errorString);
                                if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                                    // df session error. handled from base activity
                                } else {
                                    // TODO: 30/01/18 handle more error here
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (activity.isShowingProgress()) activity.hideProgress();
                            if (songAdapter.isLoading()) songAdapter.setLoading(false);
                            songAdapter.notifyDataSetChanged();
                            updateEmptyVisibility();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        if (activity.isShowingProgress()) activity.hideProgress();
                        if (songAdapter.isLoading()) songAdapter.setLoading(false);
                        songAdapter.notifyDataSetChanged();
                        updateEmptyVisibility();
                    }
                });
    }

    //private void updateEmptyVisibility() {
    //    try {
    //        if (swipeRefresh != null && swipeRefresh.isRefreshing())
    //            swipeRefresh.setRefreshing(false);
    //        if (songs.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
    //            animationHelper.hideViewWithZoomAnim(songsRecycler);
    //            animationHelper.showWithZoomAnim(emptyFrame);
    //        } else if (songsRecycler.getVisibility() != View.VISIBLE) {
    //            animationHelper.showWithZoomAnim(songsRecycler);
    //            animationHelper.hideViewWithZoomAnim(emptyFrame);
    //        } else if (emptyFrame.getVisibility() == View.VISIBLE) {
    //            animationHelper.hideViewWithZoomAnim(emptyFrame);
    //        }
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}

    @Override
    public void onStop() {
        super.onStop();

        parentFragment.showIcons();
        parentFragment.deactivateSearch();

        parentFragment.removeTextWatcher(textWatcher);
    }

    @Override
    public boolean onNavigationClick() {
        parentFragment.popFragment();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    ArrayList<String> favorites = new ArrayList<>();

    public int favorites() {
        int ret = 0;
        for (String song : this.favorites) {
            if (activity.isFavorites(song)) ret++;
        }
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.favorites.clear();
        for (Song song : songs) {
            this.favorites.add(song.getSongId());
        }

        if (songs.isEmpty() || favorites() > 0) populateSongs();
    }
}
