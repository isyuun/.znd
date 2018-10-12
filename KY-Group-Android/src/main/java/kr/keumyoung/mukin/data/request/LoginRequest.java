package kr.keumyoung.mukin.data.request;

/**
 *  on 25/01/18.
 */

public class LoginRequest {
    private String email, password;
    private int duration = 0;

    public LoginRequest(String email, String password, int duration) {
        this.email = email;
        this.password = password;
        this.duration = duration;
    }
}
