package kr.keumyoung.mukin.data.request;

/**
 *  on 25/01/18.
 */

public class RegisterUserCustomRequest {
    private String dfid, nickname, name, profileimage, email, sociallogin = "", socialid = "";

    public RegisterUserCustomRequest(String dfid, String nickname, String profileimage, String email, String sociallogin, String socialid) {
        this.dfid = dfid;
        this.nickname = nickname;
        this.name = nickname;
        this.profileimage = profileimage;
        this.email = email;
        this.sociallogin = sociallogin;
        this.socialid = socialid;
    }

    public RegisterUserCustomRequest(String dfid, String nickname, String profileimage, String email) {
        this.dfid = dfid;
        this.nickname = nickname;
        this.name = nickname;
        this.profileimage = profileimage;
        this.email = email;
    }
}
