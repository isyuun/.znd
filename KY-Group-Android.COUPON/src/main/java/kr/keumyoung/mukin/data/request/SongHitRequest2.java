package kr.keumyoung.mukin.data.request;

/**
 *  on 02/02/18.
 * Project: KyGroup
 */

public class SongHitRequest2 {
    private int songid, userid, playtime;

    public SongHitRequest2(String userid, String songid, int playtime) {
        this.songid = Integer.parseInt(songid);
        this.userid = Integer.parseInt(userid);
        this.playtime = playtime;
    }
}
