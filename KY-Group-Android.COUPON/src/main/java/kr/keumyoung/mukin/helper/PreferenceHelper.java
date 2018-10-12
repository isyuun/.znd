package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.content.SharedPreferences;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.util.PreferenceKeys;

import javax.inject.Inject;

/**
 *  on 02/09/17.
 */

public class PreferenceHelper {

    @Inject
    Context context;

    private static SharedPreferences preferences;

    //private static final String MY_PREFS = "KY_GROUP";

    @Inject
    public PreferenceHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultVal) {
        return preferences.getString(key, defaultVal);
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void clearSavedSettings() {
        saveInt(PreferenceKeys.IS_DELAY, 0);
        saveInt(PreferenceKeys.IS_ECHO, 0);
        saveInt(PreferenceKeys.IS_REVERB, 0);
        saveInt(PreferenceKeys.TEMPO_VALUE, 0);
        saveInt(PreferenceKeys.PITCH_VALUE, 0);
        saveInt(PreferenceKeys.SONG_GENDER, 0);
    }
}
