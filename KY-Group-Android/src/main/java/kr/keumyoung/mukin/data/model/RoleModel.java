package kr.keumyoung.mukin.data.model;

/**
 *  on 23/04/18.
 * Project: KyGroup
 */

class RoleModel {
    private String app_id, role_id, user_id;

    RoleModel(String user_id) {
        this.user_id = user_id;
        app_id = "4"; // hard coded as DB values
        role_id = "2"; // hard coded as DB values
    }
}
