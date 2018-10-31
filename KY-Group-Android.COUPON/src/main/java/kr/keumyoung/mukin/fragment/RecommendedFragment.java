package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.SongParser;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PaginationScrollListener;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  on 12/01/18.
 */

public class RecommendedFragment extends _BaseFragment {

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.recommended_recycler)
    RecyclerView recommendedRecycler;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;
    @BindView(R.id.recommended_swipe_refresh)
    SwipeRefreshLayout recommendedSwipeRefresh;

    SongAdapter songAdapter;

    Songs songs = new Songs();

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            populateSongs(offset);
        }
    };

    Unbinder unbinder;

    int offset = 0;
    boolean isLoading = false, isLastPage = false;

    LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recommendedSwipeRefresh.setOnRefreshListener(() -> populateSongs(0));
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showHeaderImage();
        activity.hideNavigationIcon();
        activity.showMenuIcon();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(songs);

        layoutManager = new LinearLayoutManager(activity);

        recommendedRecycler.setLayoutManager(layoutManager);
        recommendedRecycler.setAdapter(songAdapter);

        if (songs.isEmpty())
            populateSongs(0);

        recommendedRecycler.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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

    private void populateSongs(int offset) {
        this.offset = offset;

        if (offset == 0) {
            activity.showProgress();
            recommendedSwipeRefresh.setRefreshing(true);
        } else {
            songAdapter.setLoading(true);
            //songAdapter.notifyDataSetChanged();
        }

        isLoading = true;

        restApi.tableGetRequestWithLimitOffset(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.RECOMMENDED, Constants.LIMIT, offset)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        isLoading = false;
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject responseObject = new JSONObject(responseString);

                                JSONArray songArray = responseObject.getJSONArray(Constants.RESOURCE);
                                int length = songArray.length();
                                if (length > 0 && offset == 0) songs.clear();
                                isLastPage = length == 0;
                                for (int index = 0; index < length; index++) {
                                    JSONObject songObject = songArray.getJSONObject(index);
                                    Song song = SongParser.convertToSongFromJson(songObject);
                                    song.setFavorite(activity.isFavorites(song.getSongId()));
                                    songs.add(song);
                                }
                                if (activity.isShowingProgress()) activity.hideProgress();

                                if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                songAdapter.notifyDataSetChanged();

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
                        isLoading = false;
                        if (songAdapter.isLoading()) songAdapter.setLoading(false);
                        songAdapter.notifyDataSetChanged();
                        updateEmptyVisibility();
                    }
                });
    }

    private void updateEmptyVisibility() {
        try {
            if (recommendedSwipeRefresh != null && recommendedSwipeRefresh.isRefreshing())
                recommendedSwipeRefresh.setRefreshing(false);
            if (songs.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(recommendedRecycler);
                animationHelper.showWithZoomAnim(emptyFrame);
            } else if (recommendedRecycler != null && recommendedRecycler.getVisibility() != View.VISIBLE) {
                animationHelper.showWithZoomAnim(recommendedRecycler);
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            } else if (emptyFrame != null && emptyFrame.getVisibility() == View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(emptyFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        //CommonHelper.hideSoftKeyboard(activity);
        activity.hideNavigationIcon();
        activity.updateFavoriteSongs(songs, songAdapter);
    }
}
