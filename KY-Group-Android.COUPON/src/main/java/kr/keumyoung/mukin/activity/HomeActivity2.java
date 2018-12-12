package kr.keumyoung.mukin.activity;

import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.SongView;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class HomeActivity2 extends HomeActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onMenuClick() {
        if (currentFragment != null && currentFragment.onMenuClick()) return;

        openPreference();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = "\n" + "[" + PreferenceKeys.LOGIN_EMAIL + "]" + preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL);
        text += "\n" + "[" + PreferenceKeys.LOGIN_PASSWORD + "]" + preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD);
        text += "\n" + "[" + PreferenceKeys.USER_ID + "]" + preferenceHelper.getString(PreferenceKeys.USER_ID);
        text += "\n" + "[" + PreferenceKeys.SESSION_TOKEN + "]" + preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN);
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + text);
        //로그인
        if (preferenceHelper.getString(PreferenceKeys.LOGIN_EMAIL).isEmpty() || preferenceHelper.getString(PreferenceKeys.LOGIN_PASSWORD).isEmpty() ||
                preferenceHelper.getString(PreferenceKeys.USER_ID).isEmpty() || preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN).isEmpty()) {
            // user is not logged in
            //openPreferenceLogin();
            openPreferenceLoginChoice();
        }

        getFavoriteSongs();
        getFreeSongs();
    }

    protected void onSongSelected(SongView song) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + song + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + song.getSong().getSongId() + ":" + song.getSong().getSongTitle() + ":");

        if (song.getView().getId() == R.id.favorite_button) {
            if (!isFavorites(song.getSong().getSongId())) {
                addFavoriteSong(song.getSong());
            } else {
                delFavoriteSong(song.getSong());
            }
        } else {
            onSongSelected(song.getSong());
        }
    }

    @Override
    public void onBackPressed() {
        Log.e(__CLASSNAME__, getMethodName() + isShowingProgress());
        super.onBackPressed();
        hideProgress();
    }
}
