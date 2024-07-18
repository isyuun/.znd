package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.ArtistAdapter;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.model.Artist;
import kr.keumyoung.mukin.data.model.Artists;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 12/01/18.
 */

public class ArtistFragment extends _BaseListFragment {

    @Inject
    RestApi restApi;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.recycler)
    RecyclerView artistRecycler;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.empty_frame)
    LinearLayout emptyFrame;

    HomeFragment parentFragment;

    ArtistAdapter artistAdapter;

    Artists artists = new Artists();

    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            populateArtists();
        }
    };

    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        parentFragment = (HomeFragment) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh.setOnRefreshListener(this::populateArtists);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.changeNavigationIcon(R.drawable.back_icon);
        activity.showHeaderText(R.string.favorites);
        activity.hideMenuIcon();

        parentFragment.hideIcons();
//        parentFragment.activateSearch();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (artistAdapter == null) artistAdapter = new ArtistAdapter(artists);
        artistRecycler.setLayoutManager(new LinearLayoutManager(activity));
        artistRecycler.setAdapter(artistAdapter);

        if (artists.isEmpty()) populateArtists();
    }

    private void populateArtists() {
        swipeRefresh.setRefreshing(true);
        activity.showProgress();
        String order = Constants.ARTIST_NAME + " ASC";
        //restApi.tableGetRequestWithOrder(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.ARTIST, order).enqueue(new Callback<ResponseBody>() {
        restApi.tableGetRequestWithOrder(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.ARTISTS, order).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();

                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONArray artistArray = responseObject.getJSONArray(Constants.RESOURCE);
                        int length = artistArray.length();
                        if (length > 0) artists.clear();
                        for (int index = 0; index < length; index++) {
                            JSONObject artistObject = artistArray.getJSONObject(index);
                            Artist artist = convertArtistFromJson(artistObject);
                            artists.add(artist);
                        }
                        artistAdapter.notifyDataSetChanged();
                        activity.hideProgress();
                        updateEmptyVisibility();
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        // send an instance to get called on session refresh
                        if (activity.handleDFError(errorObject, sessionRefreshListener)) {
                            activity.hideProgress();
                            updateEmptyVisibility();
                        } else {
                            // TODO: 29/01/18 handle genre listing error
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideProgress();
                    updateEmptyVisibility();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                activity.hideProgress();
                updateEmptyVisibility();
            }
        });
    }

    private Artist convertArtistFromJson(JSONObject artistObject) throws JSONException {
        Artist artist = new Artist();
        artist.setArtistId(artistObject.getString(Constants.ARTIST_ID));
        artist.setArtistImage(artistObject.getString(Constants.ARTIST_IMAGE));
        artist.setArtistName(artistObject.getString(Constants.ARTIST_NAME));
        artist.setSongCount(artistObject.getString(Constants.SONG_COUNT));
        artist.setCreatedOn(artistObject.getString(Constants.CREATED_ON));
        artist.setUpdatedOn(artistObject.getString(Constants.UPDATED_ON));
        return artist;
    }

    //private void updateEmptyVisibility() {
    //    try {
    //        if (swipeRefresh != null && swipeRefresh.isRefreshing())
    //            swipeRefresh.setRefreshing(false);
    //        if (artists.isEmpty() && emptyFrame.getVisibility() != View.VISIBLE) {
    //            animationHelper.hideViewWithZoomAnim(artistRecycler);
    //            animationHelper.showWithZoomAnim(emptyFrame);
    //        } else if (artistRecycler.getVisibility() != View.VISIBLE) {
    //            animationHelper.showWithZoomAnim(artistRecycler);
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

        //activity.changeNavigationIcon(R.drawable.menu_icon);
        activity.showMenuIcon();

        parentFragment.showIcons();
        parentFragment.deactivateSearch();
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
