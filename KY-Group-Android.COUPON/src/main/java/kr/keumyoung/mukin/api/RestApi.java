package kr.keumyoung.mukin.api;

import io.reactivex.Observable;
import kr.keumyoung.mukin.data.model.UserRoleModel;
import kr.keumyoung.mukin.data.request.LoginRequest;
import kr.keumyoung.mukin.data.request.RegisterUserCustomRequest;
import kr.keumyoung.mukin.data.request.RegisterUserRequest;
import kr.keumyoung.mukin.data.request.SongHitRequest;
import kr.keumyoung.mukin.data.request.UserSongRequest;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * on 5/17/17.
 */

public interface RestApi {

    @POST("user/register")
    Call<ResponseBody> registerUser(
            @Body RegisterUserRequest request
    );

    @POST("kymedia/_table/user")
    Call<ResponseBody> registerCustom(
            @Body RequestModel<RegisterUserCustomRequest> request,
            @Header("X-DreamFactory-Session-Token") String sessionToken
    );

    @POST("user/session")
    Call<ResponseBody> login(
            @Body LoginRequest request
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithFilter(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("filter") String filter
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequest(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithOrder(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("order") String order
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithRelatedAndFilter(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("related") String related,
            @Query("filter") String filter
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithFilterAndLimitOffset(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("filter") String filter,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithRelated(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("related") String related
    );

    @GET("kymedia/_table/songs?order=hits%20DESC&filter=hits%3E0")
    Call<ResponseBody> tableNameWithOrdering(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("kymedia/_table/{table_name}")
    Call<ResponseBody> tableGetRequestWithLimitOffset(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("table_name") String tableName,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @PUT("user/session")
    Call<ResponseBody> refreshSessionToken(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Query("session_token") String token
    );

    @POST("kymedia/_table/usersongs")
    Call<ResponseBody> saveUploadedSong(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Body RequestModel<UserSongRequest> request
    );

    @GET("kymedia/_table/songs")
    Call<ResponseBody> searchSongWithTableName(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Query("filter") String filter
    );

    @GET("kymediasearch")
    Call<ResponseBody> searchCustomScript(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Query("path") String filter,
            @Query("keyword") String keyWord
    );

    @POST("kymedia/_table/songhits")
    Call<ResponseBody> updateSongHits(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Body RequestModel<SongHitRequest> request
    );

    @POST("kymedia/_table/usersongs")
    Call<ResponseBody> addFavoriteSong(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Body RequestModel<UserSongRequest> request
    );

    @DELETE("kymedia/_table/usersongs")
    Call<ResponseBody> delFavoriteSong(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Body RequestModel<UserSongRequest> request
    );

    @PUT("system/user/{userid}")
    Call<ResponseBody> updateUserRole(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Path("userid") String userid,
            @Body UserRoleModel request
    );

    @DELETE("user/session")
    Call<ResponseBody> logout(
            @Header("X-DreamFactory-Session-Token") String sessionToken
    );

    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String url);

    @POST("file_save")
    @Multipart
    Call<ResponseBody> fileSave(
            @Header("X-DreamFactory-Session-Token") String sessionToken,
            @Part("song_identifier") String songIdentifier,
            @Part("player_log") String keyWord,
            @Part MultipartBody.Part multipartBody
    );
}

