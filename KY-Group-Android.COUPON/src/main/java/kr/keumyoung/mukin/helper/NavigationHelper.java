package kr.keumyoung.mukin.helper;

import android.content.Intent;
import android.os.Bundle;

import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.BaseActivity;
import kr.keumyoung.mukin.util.Constants;

import javax.inject.Inject;

/**
 *  on 11/01/18.
 */

public class NavigationHelper {

    @Inject
    NavigationHelper() {
        // nothing to initialize
    }

    //public void navigate(BaseActivity from, Class<? extends BaseActivity> to) {
    //    navigate(from, to, true, null);
    //}

    public void navigate(BaseActivity from, Class<? extends BaseActivity> to, boolean finish, Bundle data) {
        Intent intent = new Intent(from, to);
        if (data != null) intent.putExtra(Constants.DATA, data);
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        if (finish) from.finish();
    }

    //public void navigateWithReverseAnim(BaseActivity from, Class<? extends BaseActivity> to) {
    //    navigateWithReverseAnim(from, to, true, null);
    //}

    public void navigateWithReverseAnim(BaseActivity from, Class<? extends BaseActivity> to, boolean finish, Bundle data) {
        Intent intent = new Intent(from, to);
        if (data != null) intent.putExtra(Constants.DATA, data);
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        if (finish) from.finish();
    }

    public void finish(BaseActivity from) {
        from.finish();
        from.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    //public void navigateWithClearTask(BaseActivity from, Class<? extends BaseActivity> to) {
    //    Intent intent = new Intent(from, to);
    //    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //    from.startActivity(intent);
    //    from.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    //    from.finish();
    //}
}
