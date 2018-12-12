package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.request.SongHitRequest;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity4 extends BaseActivity3 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private Song song;
    protected SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
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
                                navigationHelper.navigate(BaseActivity4.this, _PlayerActivity.class, false, bundle);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "updateSongHits:onResponse()" + "\n" + errorString);
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
}
