package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 12/01/18.
 */

public class SearchFragment extends _BaseListFragment {

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    RestApi restApi;

    @Inject
    AnimationHelper animationHelper;

    @Inject
    ToastHelper toastHelper;

    @BindView(R.id.count_text)
    TextView countText;
    @BindView(R.id.recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;

    Unbinder unbinder;

    HomeFragment parentFragment;
    TextWatcher textWatcher;

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            // nothing to do
        }
    };
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        parentFragment = (HomeFragment) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.showHeaderText(R.string.search);

        updateEmptyVisibility();

        parentFragment.hideIcons();
        parentFragment.activateSearch(songs.isEmpty());
        postDelayed(() -> parentFragment.popupKeyboard(), 100);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
        swipeRefresh.setOnRefreshListener(this::searchSong);
    }

    private void setupRecyclerView() {
        if (songAdapter == null) songAdapter = new SongAdapter(songs);
        searchRecycler.setLayoutManager(new LinearLayoutManager(activity));
        searchRecycler.setAdapter(songAdapter);

        prepareTextWatcher();
    }

    private void prepareTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String queryText = editable.toString().trim();
                if (queryText.length() >= 1) {
                    parentFragment.searchClose.setVisibility(View.VISIBLE);
                } else {
                    parentFragment.searchClose.setVisibility(View.GONE);
                }
            }
        };

        parentFragment.addTextWatcher(textWatcher);

        parentFragment.searchEt.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                searchSong();
                return true;
            }
            return false;
        });

        parentFragment.searchBTN.setOnClickListener(view -> {
            searchSong();
        });

        parentFragment.searchClose.setOnClickListener(view -> {
            parentFragment.searchEt.getText().clear();
        });
    }

    Editable editable;

    void searchSong() {
        this.editable = parentFragment.searchEt.getText();
        post(searchSong);
        CommonHelper.hideSoftKeyboard(activity);
    }

    Runnable searchSong = () -> {
        if (editable == null) {
            editable = parentFragment.searchEt.getText();
        }
        String queryText = editable.toString().trim();
        //if (queryText.length() >= 3) performSearch(queryText);
        //dsjung 3글자 미만 제목의 곡이 검색 안되서 아래와 같이 수정함
        if (queryText.length() >= 1) {
            performSearch(queryText);
        } else {
            clearSongList();
        }
    };

    private void clearSongList() {
        songs.clear();
        songAdapter.notifyDataSetChanged();
        updateEmptyVisibility();
    }

    private void performSearch(String keyword) {
        if (BuildConfig.DEBUG) Log.e("[SearchFragment]", "performSearch(...)" + keyword);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        restApi.searchCustomScript(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), "global", keyword)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();

                            if (responseBody != null) {
                                String responseString = responseBody.string();
                                JSONObject responseObject = new JSONObject(responseString);

                                JSONArray resultArray = responseObject.getJSONArray(Constants.RESOURCE);
                                int length = resultArray.length();
                                songs.clear();
                                songAdapter.notifyDataSetChanged();
                                for (int index = 0; index < length; index++) {
                                    JSONObject songObject = resultArray.getJSONObject(index);
                                    Song song = SongParser.convertToSongFromJson(songObject);
                                    song.setFavorite(activity.isFavorites(song.getSongId()));
                                    songs.add(song);
                                }
                                countText.setText(String.format("%s %s", Integer.toString(length),
                                        length > 1 ? getResources().getString(R.string.songs_found) : getResources().getString(R.string.song_found)));
                                //songAdapter.notifyDataSetChanged();
                                updateSongs();
                                updateEmptyVisibility();
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                JSONObject errorObject = new JSONObject(errorString);
                                if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                                    // error is handled in base activity. nothing to do here
                                } else {
                                    // TODO: 02/02/18 handle more errors here related to search
                                }
                                updateEmptyVisibility();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            updateEmptyVisibility();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                        throwable.printStackTrace();
                        updateEmptyVisibility();
                    }
                });
    }

//    private void performSearch(String queryText) {
//        String filter = "(songtitle like %" + queryText + "%) or (songsubtitle like %" + queryText + "%) or (identifier=" + queryText + ")";
//
//        restApi.searchSongWithTableName(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), filter)
//                .enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            ResponseBody responseBody = response.body();
//                            ResponseBody errorBody = response.errorBody();
//
//                            if (responseBody != null) {
//                                String responseString = responseBody.string();
//                                JSONObject responseObject = new JSONObject(responseString);
//                                JSONArray songsArray = responseObject.getJSONArray(Constants.RESOURCE);
//                                int length = songsArray.length();
//                                songs.clear();
//                                for (int index = 0; index < length; index++) {
//                                    JSONObject songObject = songsArray.getJSONObject(index);
//                                    Song song = SongParser.convertToSongFromJson(songObject);
//                                    songs.add(song);
//                                }
//                                countText.setText(String.format("%s %s", Integer.toString(length),
//                                        length > 1 ? getResources().getString(R.string.songs_found) : getResources().getString(R.string.song_found)));
//                                songAdapter.notifyDataSetChanged();
//                                updateEmptyVisibility();
//                            } else if (errorBody != null) {
//                                String errorString = errorBody.string();
//                                JSONObject errorObject = new JSONObject(errorString);
//                                if (activity.handleDFError(errorObject, sessionRefreshListener)) {
//                                    activity.hideProgress();
//                                    updateEmptyVisibility();
//                                } else {
//                                    // TODO: 29/01/18 handle more errors related to the songs listing
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            activity.hideProgress();
//                            updateEmptyVisibility();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.printStackTrace();
//                        activity.hideProgress();
//                        updateEmptyVisibility();
//                    }
//                });
//    }

    protected void updateEmptyVisibility() {
        try {
            if (songs.isEmpty() || parentFragment.searchEt.getText().toString().isEmpty()) {
                if (countText != null)
                    countText.setText("");
                if (animationHelper != null) {
                    animationHelper.hideViewWithZoomAnim(searchRecycler);
                    animationHelper.showWithZoomAnim(emptyFrame);
                }
            } else if (searchRecycler != null && searchRecycler.getVisibility() != View.VISIBLE) {
                if (animationHelper != null) {
                    animationHelper.showWithZoomAnim(searchRecycler);
                    animationHelper.hideViewWithZoomAnim(emptyFrame);
                }
            } else if (emptyFrame != null && emptyFrame.getVisibility() == View.VISIBLE) {
                if (animationHelper != null)
                    animationHelper.hideViewWithZoomAnim(emptyFrame);
            }
            if (progressBar != null)
                progressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        CommonHelper.hideSoftKeyboard(activity);

        activity.instantHideHeaderImage();
//        activity.changeNavigationIcon(R.drawable.menu_icon);

        parentFragment.showIcons();
        parentFragment.deactivateSearch();
        parentFragment.removeTextWatcher(textWatcher);
    }

    @Override
    public boolean onNavigationClick() {
        songs.clear();
        parentFragment.clearSearch();
        parentFragment.popFragment();
        return true;
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
    }
}
