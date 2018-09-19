package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 13/01/18.
 */

public class SongsFragment extends BaseFragment {

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    RestApi restApi;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.songs_recycler)
    RecyclerView songsRecycler;
    @BindView(R.id.songs_swipe_refresh)
    SwipeRefreshLayout songsSwipeRefresh;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;

    HomeFragment parentFragment;

    SongAdapter songAdapter;

    String filter, id;

    Songs songs = new Songs();

    TextWatcher textWatcher;

    Unbinder unbinder;

    String selection;

    int offset = 0;
    boolean isLoading = false, isLastPage = false;

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            populateSongs();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        parentFragment = (HomeFragment) getParentFragment();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(Constants.FILTER)) filter = bundle.getString(Constants.FILTER);
            if (bundle.containsKey(Constants.ID)) id = bundle.getString(Constants.ID);
            if (bundle.containsKey(Constants.SELECTION))
                selection = bundle.getString(Constants.SELECTION);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.instantHideHeaderImage();
        activity.setHeaderText(R.string.songs);
        activity.hideMenuIcon();

        parentFragment.hideIcons();
//        parentFragment.activateSearch();
//        initiateTextWatcher();
    }

    private void initiateTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString().trim();
                if (keyword.isEmpty()) populateSongs();
                else processSearch(keyword);
            }
        };
        parentFragment.addTextWatcher(textWatcher);
    }

    private void processSearch(String keyword) {
        String filter = String.format("((%s) and (songtitle like %%%s%%) or (songsubtitle like %%%s%%))", this.filter, keyword, keyword);
        populateSongs(filter, false, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (songAdapter == null) songAdapter = new SongAdapter(songs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        songsRecycler.setLayoutManager(layoutManager);
        songsRecycler.setAdapter(songAdapter);

        populateSongs();

        songsRecycler.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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
                songAdapter.setLoading(true);
                songAdapter.notifyDataSetChanged();
                offset += Constants.LIMIT;
                populateSongs(filter, false, offset);
            }
        });
    }

    private void populateSongs() {
        CommonHelper.hideSoftKeyboard(activity);
        populateSongs(filter, true, 0);
    }

    private void populateSongs(String filter, boolean showProgress, int offset) {
        try {
            if (showProgress) activity.showProgress();
            System.out.println("FILTER: " + filter);

            if (selection.equalsIgnoreCase(Constants.ARTIST) || selection.equalsIgnoreCase(Constants.GENRE)) {
                restApi.searchCustomScript(
                        preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN),
                        selection,
                        id
                )
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                try {
                                    ResponseBody responseBody = response.body();
                                    ResponseBody errorBody = response.errorBody();

                                    if (responseBody != null) {
                                        String responseString = responseBody.string();
                                        JSONObject responseObject = new JSONObject(responseString);
                                        JSONArray songsArray = responseObject.getJSONArray(Constants.RESOURCE);
                                        int length = songsArray.length();
                                        songs.clear();
                                        for (int index = 0; index < length; index++) {
                                            JSONObject songObject = songsArray.getJSONObject(index);
                                            Song song = SongParser.convertToSongFromJson(songObject);
                                            songs.add(song);
                                        }
                                        if (showProgress) activity.hideProgress();
                                        if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                        songAdapter.notifyDataSetChanged();
                                        updateEmptyVisibility();
                                    } else if (errorBody != null) {
                                        String errorString = errorBody.string();
                                        JSONObject errorObject = new JSONObject(errorString);
                                        if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                                            if (showProgress) activity.hideProgress();
                                            updateEmptyVisibility();
                                        } else {
                                            // TODO: 29/01/18 handle more errors related to the songs listing
                                            songs.clear();
                                            songAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    songs.clear();
                                    if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                    songAdapter.notifyDataSetChanged();
                                    if (showProgress) activity.hideProgress();
                                    updateEmptyVisibility();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                                songs.clear();
                                if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                songAdapter.notifyDataSetChanged();
                                activity.hideProgress();
                                updateEmptyVisibility();
                            }
                        });

            } /*else if (selection.equalsIgnoreCase(Constants.GENRE)) {
                isLoading = true;
                restApi.tableGetRequestWithFilterAndLimitOffset(
                        preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN),
                        TableNames.SONGS, filter, Constants.LIMIT, offset).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();

                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject responseObject = new JSONObject(responseString);
                                JSONArray songsArray = responseObject.getJSONArray(Constants.RESOURCE);
                                int length = songsArray.length();
                                isLastPage = length == 0;
                                if (length > 0 && offset == 0) songs.clear();
                                for (int index = 0; index < length; index++) {
                                    JSONObject songObject = songsArray.getJSONObject(index);
                                    Song song = SongParser.convertToSongFromJson(songObject);
                                    songs.add(song);
                                }
                                isLoading = false;
                                if (showProgress) activity.hideProgress();
                                if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                songAdapter.notifyDataSetChanged();
                                updateEmptyVisibility();
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                JSONObject errorObject = new JSONObject(errorString);
                                if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                                    if (showProgress) activity.hideProgress();
                                    updateEmptyVisibility();
                                } else {
                                    // TODO: 29/01/18 handle more errors related to the songs listing
                                    songs.clear();
                                    if (songAdapter.isLoading()) songAdapter.setLoading(false);
                                    songAdapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            songs.clear();
                            if (songAdapter.isLoading()) songAdapter.setLoading(false);
                            songAdapter.notifyDataSetChanged();
                            if (showProgress) activity.hideProgress();
                            updateEmptyVisibility();
                            isLoading = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        songs.clear();
                        if (songAdapter.isLoading()) songAdapter.setLoading(false);
                        songAdapter.notifyDataSetChanged();
                        activity.hideProgress();
                        updateEmptyVisibility();
                        isLoading = false;
                    }
                });
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
        }
    }

    private void updateEmptyVisibility() {
        try {
            if (songsSwipeRefresh != null && songsSwipeRefresh.isRefreshing())
                songsSwipeRefresh.setRefreshing(false);
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

    @Override
    public void onStop() {
        super.onStop();

//        activity.changeNavigationIcon(R.drawable.menu_icon);
        activity.showMenuIcon();

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
}
