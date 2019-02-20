package kr.keumyoung.mukin.data.request;

/**
 *  on 25/01/18.
 */

public class PasswordChangeRequest {
    private String email, new_password, code;

    public PasswordChangeRequest(String email, String new_password, String code) {
        this.email = email;
        this.new_password = new_password;
        this.code = code;
    }
}

