package kr.keumyoung.mukin.data.request;

/**
 *  on 02/02/18.
 * Project: KyGroup
 */

public class SongHitRequest {
    private int songid, userid;

    public SongHitRequest(String userid, String songid) {
        this.songid = Integer.parseInt(songid);
        this.userid = Integer.parseInt(userid);
    }
}
