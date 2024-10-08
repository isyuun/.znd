package kr.keumyoung.karaoke.app;

import android.accounts.Account;
import android.app.Activity;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import androidx.annotation.IdRes;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import kr.kymedia.karaoke.util.EnvironmentUtils;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

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

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void showKeyboard(final boolean visible) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        if (imm != null) {
            if (visible) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}

