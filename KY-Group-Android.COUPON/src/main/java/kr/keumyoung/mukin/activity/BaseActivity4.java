package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.SongParser;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;
import kr.keumyoung.mukin.data.request.SongHitRequest;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity4 extends BaseActivity3 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    public void navigate(Class<? extends BaseActivity> to, boolean finish, Bundle data) {
        navigationHelper.navigate(this, to, finish, data);
    }

    private Song song;
    private SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId()  + ":" + song.getSongTitle() + ":");
            if (song != null) onSongSelected(song);
        }
    };

    public void onSongSelected(Song song) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId() + ":" + song.getSongTitle());
        this.song = song;
        CommonHelper.hideSoftKeyboard(this);

        showProgress();
        setProgressMessage();

        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(preferenceHelper.getString(PreferenceKeys.USER_ID), song.getSongId()));
        restApi.updateSongHits(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), model)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();
                            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[OK]" + "updateSongHits:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                            if (responseBody != null) {
                                hideProgress();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.SONG, song);
                                // navigate to player activity for playing the media and processing
                                navigate(PlayerActivity.class, false, bundle);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[NG]" + "updateSongHits:onResponse()" + "\n" + errorString);
                                JSONObject errorObject = new JSONObject(errorString);
                                if (!handleDFError(errorObject, sessionRefreshListener)) {
                                    hideProgress();
                                    toastHelper.showError(R.string.common_api_error);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgress();
                            toastHelper.showError(R.string.common_api_error);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideProgress();
                        toastHelper.showError(R.string.common_api_error);
                    }
                });
    }

    protected void addFavoriteSong(Song song) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId() + ":" + song.getSongTitle());
        song.setFavorite(true);
        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(preferenceHelper.getString(PreferenceKeys.USER_ID), song.getSongId()));
        restApi.addFavoriteSong(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "addFavoriteSong:onResponse()" + "\n" + response);
                getFavoriteSongs();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    protected void delFavoriteSong(Song song) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId() + ":" + song.getSongTitle());
        song.setFavorite(false);
        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(preferenceHelper.getString(PreferenceKeys.USER_ID), song.getSongId()));
        String userid = preferenceHelper.getString(PreferenceKeys.USER_ID);
        String songid = song.getSongId();
        String filter = "";
        filter += "(" + "songid=" + songid + ")";
        filter += " and ";
        filter += "(" + "userid=" + userid + ")";
        restApi.delFavoriteSong(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "delFavoriteSong:onResponse()" + "\n" + response);
                getFavoriteSongs();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    ArrayList<String> favorites = new ArrayList<>();

    public boolean isFavorites(String songid) {
        return (favorites.indexOf(songid) > -1);
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    protected void getFavoriteSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + favorites);
        String filter = "userid=" + preferenceHelper.getString(PreferenceKeys.USER_ID);
        restApi.tableGetRequestWithFilter(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.FAVORITE, filter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[OK]" + "getFavoriteSongs:onResponse()" + "\n" + responseString);
                        JSONObject responseObject = new JSONObject(responseString);

                        JSONArray songArray = responseObject.getJSONArray(Constants.RESOURCE);
                        int length = songArray.length();
                        favorites.clear();
                        for (int index = 0; index < length; index++) {
                            JSONObject songObject = songArray.getJSONObject(index);
                            Song song = SongParser.convertToSongFromJson(songObject);
                            favorites.add(song.getSongId());
                        }
                        onFavoriteSongs();
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[NG]" + "getFavoriteSongs:onResponse()" + "\n" + errorString);
                        JSONObject errorObject = new JSONObject(errorString);
                        if (!handleDFError(errorObject, sessionRefreshListener)) {
                            hideProgress();
                            toastHelper.showError(R.string.common_api_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[RT]" + "getFavoriteSongs:onResponse()" + ":" + favorites);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    protected void onFavoriteSongs() {
    }

    public void updateFavoriteSongs(Songs songs, SongAdapter adapter) {
        for (Song song : songs) {
            song.setFavorite(isFavorites(song.getSongId()));
        }
        adapter.notifyDataSetChanged();
    }

}
