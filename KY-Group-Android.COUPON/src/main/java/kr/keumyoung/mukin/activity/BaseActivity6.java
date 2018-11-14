package kr.keumyoung.mukin.activity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.SongParser;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.TableNames;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity6 extends BaseActivity5 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    ArrayList<String> frees = new ArrayList<>();

    public boolean isFrees(String songid) {
        return (frees.indexOf(songid) > -1);
    }

    public ArrayList<String> getFrees() {
        return frees;
    }

    protected void getFreeSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + frees);
        restApi.tableGetRequest(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), TableNames.FREES).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[OK]" + "getFreeSongs:onResponse()" + "\n" + responseString);
                        JSONObject responseObject = new JSONObject(responseString);

                        JSONArray songArray = responseObject.getJSONArray(Constants.RESOURCE);
                        int length = songArray.length();
                        frees.clear();
                        for (int index = 0; index < length; index++) {
                            JSONObject songObject = songArray.getJSONObject(index);
                            Song song = SongParser.convertToSongFromJson(songObject);
                            frees.add(song.getSongId());
                        }
                        onFreeSongs();
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[NG]" + "getFreeSongs:onResponse()" + "\n" + errorString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[BF]" + "getFreeSongs:onResponse()" + ":" + frees + ":" + SongParser.FREE_SONG);
                SongParser.FREE_SONG = frees;
                if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "[AF]" + "getFreeSongs:onResponse()" + ":" + frees + ":" + SongParser.FREE_SONG);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                toastHelper.showError(R.string.common_api_error);
            }
        });
    }

    protected void onFreeSongs() {
    }
}
