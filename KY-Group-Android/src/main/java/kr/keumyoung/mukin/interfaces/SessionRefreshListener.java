package kr.keumyoung.mukin.interfaces;

import android.content.Intent;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.BaseActivity;
import kr.keumyoung.mukin.activity.LoginChoiceActivity;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.PreferenceKeys;

import javax.inject.Inject;

/**
 *  on 29/01/18.
 * Project: KyGroup
 */

public abstract class SessionRefreshListener {
    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    ToastHelper toastHelper;

    public SessionRefreshListener() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public abstract void onSessionRefresh();

    public void logout(BaseActivity activity) {
        // clear the values in shared preference and redirect the user to the login choice activity clearing all previous activities
        preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, "");
        preferenceHelper.saveString(PreferenceKeys.USER_ID, "");
        preferenceHelper.saveString(PreferenceKeys.DF_ID, "");
        preferenceHelper.saveString(PreferenceKeys.NICK_NAME, "");
        preferenceHelper.saveString(PreferenceKeys.PROFILE_IMAGE, "");

        toastHelper.showError(R.string.session_expired_error);

        Intent i = new Intent(activity, LoginChoiceActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
