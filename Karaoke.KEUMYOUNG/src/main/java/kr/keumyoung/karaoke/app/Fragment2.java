package kr.keumyoung.karaoke.app;

import android.accounts.Account;
import android.os.Build;
import android.support.annotation.IdRes;
import android.view.View;

import kr.keumyoung.karaoke.BuildConfig;
import kr.kymedia.karaoke.util.EnvironmentUtils;

public class Fragment2 extends Fragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    protected String getGoogleAccount() {
        String email = null;
        Account accounts[] = EnvironmentUtils.getGoogleAccount(getActivity());
        if (accounts.length > 0) {
            email = accounts[0].name;
        }
        return email;
    }

    @SuppressWarnings("TypeParameterUnusedInFormals")
    public <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    protected int checkSelfPermission(String readContacts) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getActivity().checkSelfPermission(readContacts);
        }
        return 0;
    }

    public void finish() {
        getActivity().finish();
    }
}

