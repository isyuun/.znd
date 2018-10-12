package kr.keumyoung.mukin.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  on 23/04/18.
 * Project: KyGroup
 */

public class UserRoleModel {
    private List<RoleModel> user_to_app_to_role_by_user_id = new ArrayList<>();

    public UserRoleModel(String userId) {
        user_to_app_to_role_by_user_id.add(new RoleModel(userId));
    }
}
