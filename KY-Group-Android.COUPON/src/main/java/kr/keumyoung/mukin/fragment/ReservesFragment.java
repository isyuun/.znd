package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PaginationScrollListener;

/**
 *  on 12/01/18.
 */

public class ReservesFragment extends _BaseFragment {
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

    @BindView(R.id.featured_recycler)
    RecyclerView featuredRecycler;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout featuredSwipeRefresh;
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
        View view = inflater.inflate(R.layout.fragment_featured, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        featuredSwipeRefresh.setOnRefreshListener(this::populateSongs);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.showHeaderText(R.string.reserve_song);
        activity.hideMenuIcon();

        parentFragment.hideIcons();

        //parentFragment.activateSearch();
        //initiateTextWatcher();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (songAdapter == null) songAdapter = new SongAdapter(activity.getApp().getReserves());
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        featuredRecycler.setLayoutManager(manager);
        featuredRecycler.setAdapter(songAdapter);

        featuredRecycler.addOnScrollListener(new PaginationScrollListener(manager) {
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

        updateSongs();
    }

    //protected void populateSongs(int offset) {
    //    activity.showProgress();
    //    CommonHelper.hideSoftKeyboard(activity);
    //
    //    this.offset = offset;
    //
    //    if (offset == 0) {
    //        activity.showProgress();
    //        featuredSwipeRefresh.setRefreshing(true);
    //    } else {
    //        songAdapter.setLoading(true);
    //        songAdapter.notifyDataSetChanged();
    //    }
    //
    //    return;
    //
    //    isLoading = true;
    //
    //    String filter = "userid=" + preferenceHelper.getString(PreferenceKeys.USER_ID);
    //    restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.FAVORITE, filter)
    //            .enqueue(new Callback<ResponseBody>() {
    //                @Override
    //                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //                    try {
    //                        ResponseBody responseBody = response.body();
    //                        ResponseBody errorBody = response.errorBody();
    //                        if (responseBody != null) {
    //                            String responseString = responseBody.string();
    //                            JSONObject responseObject = new JSONObject(responseString);
    //
    //                            JSONArray songArray = responseObject.getJSONArray(Constants.RESOURCE);
    //                            int length = songArray.length();
    //                            songs.clear();
    //                            for (int index = 0; index < length; index++) {
    //                                JSONObject songObject = songArray.getJSONObject(index);
    //                                Song song = SongParser.convertToSongFromJson(songObject);
    //                                song.setFavorite(activity.isFavorites(song.getSongId()));
    //                                songs.add(song);
    //                            }
    //                            if (activity.isShowingProgress()) activity.hideProgress();
    //
    //                            if (songAdapter.isLoading()) songAdapter.setLoading(false);
    //                            songAdapter.notifyDataSetChanged();
    //
    //                            updateEmptyVisibility();
    //                        } else if (errorBody != null) {
    //                            String errorString = errorBody.string();
    //                            JSONObject errorObject = new JSONObject(errorString);
    //                            if (activity.handleDFError(errorObject, sessionRefreshListener)) {
    //                                // df session error. handled from base activity
    //                            } else {
    //                                // TODO: 30/01/18 handle more error here
    //                            }
    //                        }
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                        if (activity.isShowingProgress()) activity.hideProgress();
    //                        if (songAdapter.isLoading()) songAdapter.setLoading(false);
    //                        songAdapter.notifyDataSetChanged();
    //                        updateEmptyVisibility();
    //                    }
    //                }
    //
    //                @Override
    //                public void onFailure(Call<ResponseBody> call, Throwable t) {
    //                    t.printStackTrace();
    //                    if (activity.isShowingProgress()) activity.hideProgress();
    //                    if (songAdapter.isLoading()) songAdapter.setLoading(false);
    //                    songAdapter.notifyDataSetChanged();
    //                    updateEmptyVisibility();
    //                }
    //            });
    //}

    //protected String getTableName() {
    //    return TableNames.FAVORITE;
    //}

    private void updateEmptyVisibility() {
        try {
            if (featuredSwipeRefresh != null && featuredSwipeRefresh.isRefreshing())
                featuredSwipeRefresh.setRefreshing(false);
            if (songs.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(featuredRecycler);
                animationHelper.showWithZoomAnim(emptyFrame);
            } else if (featuredRecycler.getVisibility() != View.VISIBLE) {
                animationHelper.showWithZoomAnim(featuredRecycler);
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            } else if (emptyFrame.getVisibility() == View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        for (String song: this.favorites) {
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
