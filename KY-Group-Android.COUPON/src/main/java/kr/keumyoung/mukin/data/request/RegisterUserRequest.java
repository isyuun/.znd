package kr.keumyoung.mukin.data.request;

/**
 *  on 25/01/18.
 */

public class RegisterUserRequest {
    private String email, first_name, last_name, password;

    public RegisterUserRequest(String email, String first_name, String last_name, String password) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
    }
}

