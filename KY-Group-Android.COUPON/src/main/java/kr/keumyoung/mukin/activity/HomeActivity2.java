package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Subscribe;

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

public class HomeActivity2 extends HomeActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onResume() {
        super.onResume();
        String text = "\n" + "[" + PreferenceKeys.LOGIN_EMAIL + "]" + preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        text += "\n" + "[" + PreferenceKeys.LOGIN_PASSWORD + "]" + preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD);
        text += "\n" + "[" + PreferenceKeys.USER_ID + "]" + preferenceHelper.getString(PreferenceKeys.USER_ID);
        text += "\n" + "[" + PreferenceKeys.SESSION_TOKEN + "]" + preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN);
        Log.e(__CLASSNAME__, getMethodName() + text);
        //로그인
        if (preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL).isEmpty() || preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD).isEmpty() ||
                preferenceHelper.getString(PreferenceKeys.USER_ID).isEmpty() || preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN).isEmpty()) {
            // user is not logged in
            navigationHelper.navigate(this, _LoginActivity.class, false, null);
        }
    }

    @Override
    protected void onMenuClick() {
        if (currentFragment != null && currentFragment.onMenuClick()) return;

        openPreference();
    }

    private Object busEventListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busEventListener = new Object() {
            @Subscribe
            public void onSongSelected(Song song) {
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId()  + ":" + song.getSongTitle() + ":");
                HomeActivity2.this.onSongSelected(song);
            }
        };

    }

    @Override
    protected void onStart() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStart();
        //bus.register(this);
        bus.register(busEventListener);
    }

    @Override
    protected void onStop() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStop();
        //bus.unregister(this);
        bus.unregister(busEventListener);
    }

    private Song song;
    private SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId()  + ":" + song.getSongTitle() + ":");
            if (song != null) onSongSelected(song);
        }
    };

    @Override
    public void onSongSelected(Song song) {
        //if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSongId()  + ":" + song.getSongTitle() + ":");
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
                            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[OK]" + "updateSongHits:updateSongHits:onResponse()" + "\n" + errorBody + "\n" + responseBody);
                            if (responseBody != null) {
                                hideProgress();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.SONG, song);
                                // navigate to player activity for playing the media and processing
                                navigationHelper.navigate(HomeActivity2.this, PlayerActivity.class, false, bundle);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "[NG]" + "updateSongHits:updateSongHits:onResponse()" + "\n" + errorString);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(__CLASSNAME__, getMethodName() + isShowingProgress());
        hideProgress();
    }
}
