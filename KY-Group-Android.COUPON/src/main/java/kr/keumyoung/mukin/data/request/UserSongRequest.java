package kr.keumyoung.mukin.data.request;

/**
 *  on 31/01/18.
 * Project: KyGroup
 */

public class UserSongRequest {
    private int songid, userid;
    private String filename, sharelink;

    public UserSongRequest(String songid, String userid, String filename, String sharelink) {
        this.songid = Integer.parseInt(songid);
        this.userid = Integer.parseInt(userid);
        this.filename = filename;
        this.sharelink = sharelink;
    }
}
