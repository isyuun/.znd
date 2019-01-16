package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.SongView;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class HomeActivity2 extends HomeActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.play)
    FrameLayout play;

    @BindView(R.id.reserve_text)
    TextView reserveText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //play.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return true;
        //    }
        //});
        play.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "play.onClick()");
        });
    }

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

    protected void onSongClick(SongView songView) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + songView + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + songView.getSong().getSongId() + ":" + songView.getSong().getSongTitle() + ":" + songView.getClick());
        onSongSelected(songView.getSong());
    }

    protected void onFavoriteClick(SongView songView) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + songView + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + songView.getSong().getSongId() + ":" + songView.getSong().getSongTitle() + ":" + songView.getClick());
        onFavoriteSelected(songView.getSong());
    }

    protected void onReserveClick(SongView songView) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + songView + ":" + preferenceHelper.getString(PreferenceKeys.USER_ID) + ":" + songView.getSong().getSongId() + ":" + songView.getSong().getSongTitle() + ":" + songView.getClick());
        onReserveSelected(songView.getSong());
    }

    @Override
    public void onReserveSelected(Song song) {
        super.onReserveSelected(song);
        setReserveText();
        if (getApp().getReserves().size() > 0) {
            play.setVisibility(View.VISIBLE);
        } else {
            play.setVisibility(View.INVISIBLE);
        }
    }

    public void setReserveText() {
        String text = getApp().getReserves().toString();
        if (headerImage.getVisibility() == View.VISIBLE)
            animationHelper.hideWithFadeAnim(headerImage);

        if (headerText.getVisibility() == View.VISIBLE)
            animationHelper.hideWithFadeAnim(headerText);

        if (getApp().getReserves().size() > 0) {
            reserveText.setText(text);
            animationHelper.showHeaderText(reserveText);
        } else {
            reserveText.setText(R.string.reserve);
            animationHelper.hideWithFadeAnim(reserveText);
        }
    }

    @Override
    public void setHeaderText(int text) {
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.setHeaderText(text);
    }

    @Override
    public void instantHideHeaderImage() {
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.instantHideHeaderImage();
    }

    @Override
    public void showHeaderImage() {
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.showHeaderImage();
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + isShowingProgress());
        super.onBackPressed();
        hideProgress();
    }
}
