package kr.keumyoung.mukin.data.request;

/**
 *  on 25/01/18.
 */

public class UpdateUserCustomRequest {
    private String userid,dfid, nickname, name, profileimage, email, sociallogin = "", socialid = "";

    public UpdateUserCustomRequest(String userid, String dfid, String name, String profileimage, String email, String sociallogin, String socialid) {
        this.userid = userid;
        this.dfid = dfid;
        this.nickname = name;
        this.name = name;
        this.profileimage = profileimage;
        this.email = email;
        this.sociallogin = sociallogin;
        this.socialid = socialid;
    }

    public UpdateUserCustomRequest(String userid, String dfid, String name, String profileimage, String email) {
        this.userid = userid;
        this.dfid = dfid;
        this.nickname = name;
        this.name = name;
        this.profileimage = profileimage;
        this.email = email;
    }
}
