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
import kr.keumyoung.mukin.adapter.GenreAdapter;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.model.Genre;
import kr.keumyoung.mukin.data.model.Genres;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PaginationScrollListener;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;

import org.json.JSONArray;
import org.json.JSONException;
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

public class GenreFragment extends BaseFragment {

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    RestApi restApi;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.genre_recycler)
    RecyclerView genreRecycler;
    @BindView(R.id.genre_swipe_refresh)
    SwipeRefreshLayout genreSwipeRefresh;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;

    HomeFragment parentFragment;

    GenreAdapter genreAdapter;

    Genres genres = new Genres();

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            populateGenres();
        }
    };

    Unbinder unbinder;

    int offset = 0;
    boolean isLoading = false, isLastPage = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        parentFragment = (HomeFragment) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        genreSwipeRefresh.setOnRefreshListener(this::populateGenres);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.instantHideHeaderImage();
        activity.setHeaderText(R.string.genre);
        activity.hideMenuIcon();

        parentFragment.hideIcons();
        parentFragment.hideSearch();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (genreAdapter == null) genreAdapter = new GenreAdapter(genres);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        genreRecycler.setLayoutManager(layoutManager);
        genreRecycler.setAdapter(genreAdapter);

        if (genres.isEmpty()) populateGenres();

        genreRecycler.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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
                populateGenres(offset);
            }
        });
    }

    private void populateGenres() {
        populateGenres(0);
    }

    private void populateGenres(int offset) {
        activity.showProgress();
        CommonHelper.hideSoftKeyboard(activity);

        this.offset = offset;

        if (offset == 0) {
            activity.showProgress();
        } else {
            genreAdapter.setLoading(true);
            genreAdapter.notifyDataSetChanged();
        }

        isLoading = true;
        restApi.tableGetRequest(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.GENRE).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();

                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray genreArray = responseObject.getJSONArray(Constants.RESOURCE);
                        int length = genreArray.length();
                        if (length > 0 && offset == 0) genres.clear();
                        isLastPage = length == 0;
                        for (int index = 0; index < length; index++) {
                            JSONObject genreObject = genreArray.getJSONObject(index);
                            Genre genre = convertGenreFromJson(genreObject);
                            genres.add(genre);
                        }
                        if (genreAdapter.isLoading()) genreAdapter.setLoading(false);
                        genreAdapter.notifyDataSetChanged();
                        activity.hideProgress();
                        updateEmptyVisibility();
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        // send an instance to get called on session refresh
                        if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                            if (genreAdapter.isLoading()) genreAdapter.setLoading(false);
                            genreAdapter.notifyDataSetChanged();
                            activity.hideProgress();
                            updateEmptyVisibility();
                        } else {
                            // TODO: 29/01/18 handle genre listing error
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (genreAdapter.isLoading()) genreAdapter.setLoading(false);
                    genreAdapter.notifyDataSetChanged();
                    activity.hideProgress();
                    updateEmptyVisibility();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (genreAdapter.isLoading()) genreAdapter.setLoading(false);
                genreAdapter.notifyDataSetChanged();
                activity.hideProgress();
                updateEmptyVisibility();
            }
        });
    }

    private Genre convertGenreFromJson(JSONObject genreObject) throws JSONException {
        Genre genre = new Genre();
        genre.setGenreId(genreObject.getString(Constants.GENRE_ID));
        genre.setName(genreObject.getString(Constants.GENRE_NAME));
        genre.setIcon(genreObject.getString(Constants.GENRE_IMAGE));
        genre.setCount(genreObject.getString(Constants.SONG_COUNT));
        genre.setCreatedOn(genreObject.getString(Constants.CREATED_ON));
        genre.setUpdatedOn(genreObject.getString(Constants.UPDATED_ON));
        return genre;
    }

    private void updateEmptyVisibility() {
        try {
            if (genreSwipeRefresh != null && genreSwipeRefresh.isRefreshing())
                genreSwipeRefresh.setRefreshing(false);
            if (genres.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
                animationHelper.hideViewWithZoomAnim(genreRecycler);
                animationHelper.showWithZoomAnim(emptyFrame);
            } else if (genreRecycler.getVisibility() != View.VISIBLE) {
                animationHelper.showWithZoomAnim(genreRecycler);
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

//        activity.changeNavigationIcon(R.drawable.menu_icon);
        activity.showMenuIcon();

        parentFragment.showIcons();
        parentFragment.showSearch();
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
}
