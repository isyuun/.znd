package kr.keumyoung.mukin.activity;

import kr.keumyoung.mukin.activity.SongRepo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface SongApiService {

    /**
     * 노래 검색
     * @param version
     * @param row
     * @param searchTypeCd
     * @param lastIdx
     * @param searchWord
     * @param page
     * @return
     */
    @GET("/api/{version}/song/list")
    Call<SongRepo> getSongList(@Path("version") String version, @Query("row") int row
            , @Query("searchTypeCd") String searchTypeCd, @Query("lastIdx") int lastIdx
            , @Query("searchWord") String searchWord, @Query("page") int page);

    /**
     * 타입별 곡조회
     * @param version
     * @param songTypeCd
     * @param row
     * @param page
     * @return
     */
    @GET("/api/{version}/song/list/genre/{songTypeCd}")
    Call<SongRepo> getSongTypeList(@Path("version") String version, @Path("songTypeCd") String songTypeCd
            , @Query("row") int row, @Query("page") int page);

    /**
     * 오늘의 추천곡
     * @param version
     * @param regDt
     * @return
     */
    @GET("/api/{version}/song/today/new/list")
    Call<SongRepo> getSongTodayNewList(@Path("version") String version, @Query("regDt") String regDt);

    /**
     * 곡번호로 조회한다.
     * @param version
     * @param songSeq
     * @return
     */
    @GET("/api/{version}/song/info/seq")
    Call<SongRepo> selectSongInfoSeq(@Path("version") String version, @Query("songSeq") int songSeq);

    /**
     * 이달의 신곡을 조회 한다.
     * @param version
     * @param row
     * @param page
     * @param regDt
     * @return
     */
    @GET("/api/{version}/song/list/new")
    Call<SongRepo> selectSongListNew(@Path("version") String version, @Query("row") int row
            , @Query("page") int page, @Query("regDt") String regDt);

    /**
     * 사운드 lib를 다운받는다.
     * @return
     */

    @Streaming
    @GET
    Call<ResponseBody> downloadSoundLib(@Url String fileUrl);
}