package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 12/01/18.
 */

public class SearchFragment extends BaseFragment {

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
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_message)
    TextView emptyMessage;

    Unbinder unbinder;

    HomeFragment parentFragment;
    Songs songs = new Songs();
    SongAdapter songAdapter;
    TextWatcher textWatcher;

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            // nothing to do
        }
    };


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
        activity.setHeaderText(R.string.search);

        updateEmptyVisibility();

        parentFragment.hideIcons();
        parentFragment.activateSearch(songs.isEmpty());
        new Handler().postDelayed(() -> parentFragment.popupKeyboard(), 100);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
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
                if (queryText.length() >= 3) performSearch(queryText);
                else {
                    songs.clear();
                    songAdapter.notifyDataSetChanged();
                    updateEmptyVisibility();
                }
            }
        };

        parentFragment.addTextWatcher(textWatcher);
    }

    private void performSearch(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
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
                                for (int index = 0; index < length; index++) {
                                    JSONObject songObject = resultArray.getJSONObject(index);
                                    Song song = SongParser.convertToSongFromJson(songObject);
                                    songs.add(song);
                                }
                                countText.setText(String.format("%s %s", Integer.toString(length),
                                        length > 1 ? getResources().getString(R.string.songs_found) : getResources().getString(R.string.song_found)));
                                songAdapter.notifyDataSetChanged();
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

    private void updateEmptyVisibility() {
        if (songs.isEmpty() || parentFragment.searchEt.getText().toString().isEmpty()) {
            countText.setText("");
            animationHelper.hideViewWithZoomAnim(searchRecycler);
            animationHelper.showWithZoomAnim(emptyFrame);
            if (parentFragment.searchEt.getText().toString().isEmpty())
                emptyMessage.setText(R.string.type_your_desired_search);
            else emptyMessage.setText(R.string.no_songs_found);
        } else if (searchRecycler.getVisibility() != View.VISIBLE) {
            animationHelper.showWithZoomAnim(searchRecycler);
            animationHelper.hideViewWithZoomAnim(emptyFrame);
        } else if (emptyFrame.getVisibility() == View.VISIBLE) {
            animationHelper.hideViewWithZoomAnim(emptyFrame);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

        activity.instantHideHeaderImage();

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
}
