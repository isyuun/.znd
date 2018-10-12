package kr.keumyoung.mukin.activity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SongRepo {

    @SerializedName("resultCode")
    String resultCode;
    @SerializedName("msg")
    String msg;

    @SerializedName("totalCount")
    int totalCount = 0;

    public int getTotalCount() {
        return totalCount;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public ArrayList<data> data = new ArrayList<>();
    public ArrayList<data> getData(){ return data;}

    /*public data songInfo = new SongRepo().new data();
    public SongRepo.data getSongInfo(){ return songInfo;}*/

    public class data implements Serializable {
        public data(){}

        @SerializedName("songSeq")int songSeq;    // 곡번호
        @SerializedName("idx")int idx;               // idx
        @SerializedName("title")String title;        // 제목
        @SerializedName("sngr")String sngr;          // 가수
        @SerializedName("ky3Path")String ky3Path;    // ky3
        @SerializedName("compr")String compr;        // 작곡
        @SerializedName("writer")String writer;      // 작사
        @SerializedName("thumb")String thumb;        // 썸네일
        @SerializedName("keySex")String keySex;      // 기본키
        @SerializedName("manKey")String manKey;      // 남자키
        @SerializedName("womanKey")String womanKey;  // 여자키

        public String getKeySex() {
            return keySex;
        }

        public void setKeySex(String keySex) {
            this.keySex = keySex;
        }

        public String getManKey() {
            return manKey;
        }

        public void setManKey(String manKey) {
            this.manKey = manKey;
        }

        public String getWomanKey() {
            return womanKey;
        }

        public void setWomanKey(String womanKey) {
            this.womanKey = womanKey;
        }

        public int getSongSeq() {
            return songSeq;
        }

        public void setSongSeq(int songSeq) {
            this.songSeq = songSeq;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSngr() {
            return sngr;
        }

        public void setSngr(String sngr) {
            this.sngr = sngr;
        }

        public String getKy3Path() {
            return ky3Path;
        }

        public void setKy3Path(String ky3Path) {
            this.ky3Path = ky3Path;
        }

        public String getCompr() {
            return compr;
        }

        public void setCompr(String compr) {
            this.compr = compr;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String writer) {
            this.writer = writer;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
