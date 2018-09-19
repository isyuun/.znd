package kr.keumyoung.mukin.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 *  on 06/02/18.
 * Project: KyGroup
 */

public interface KakaoApi {
    @GET("user/me")
    Call<ResponseBody> userMeRequest(@Header("Authorization") String accessToken);
}

