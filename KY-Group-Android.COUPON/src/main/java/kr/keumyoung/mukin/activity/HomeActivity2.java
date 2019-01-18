package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.SongView;
import kr.keumyoung.mukin.fragment.HomeFragment;
import kr.keumyoung.mukin.fragment.ReservesFragment;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class HomeActivity2 extends HomeActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.play)
    FrameLayout play;

    @BindView(R.id.reserve_text)
    TextView reserveText;

    @BindView(R.id.reserve_label)
    View reserveLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //play.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return true;
        //    }
        //});
        play.setVisibility(View.INVISIBLE);
        play.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "play.onClick()");
            if (getApp().getReserves().size() > 0) {
                onSongSelected(getApp().getReserves().get(0));
            }
        });
        setReserveText();
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

    public void setTextViewMarquee(final TextView tv, boolean enable) {
        if (tv == null) {
            return;
        }
        // set the ellipsize mode to MARQUEE and make it scroll only once
        // tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        // tv.setMarqueeRepeatLimit(1);
        // in order to start strolling, it has to be focusable and focused
        // tv.setFocusable(true);
        // tv.setFocusableInTouchMode(true);
        // tv.requestFocus();
        if (enable) {
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        tv.setSingleLine(true);
        tv.setSelected(enable);
    }

    ReservesFragment reservesFragment;

    private void openReserves() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        post(openReserves);
    }

    private Runnable openReserves = new Runnable() {
        @Override
        public void run() {
            if (reservesFragment == null) reservesFragment = new ReservesFragment();
            if (getCurrentFragment() instanceof HomeFragment) ((HomeFragment) getCurrentFragment()).replaceListFragment(reservesFragment);
        }
    };

    public void setReserveText() {
        String text = getApp().getReserves().toString();
        setTextViewMarquee(reserveText, true);

        if (getApp().getReserves().size() > 0) {
            reserveLabel.setVisibility(View.VISIBLE);
            animationHelper.showHeaderText(reserveText);
            reserveText.setText(text);
            reserveText.setClickable(true);
            reserveText.setOnClickListener(v -> {
                openReserves();
            });
            reserveLabel.setClickable(true);
            reserveLabel.setOnClickListener(v -> {
                openReserves();
            });
            navIcon.setVisibility(View.GONE);
            if (headerImage.getVisibility() == View.VISIBLE) {
                animationHelper.hideWithFadeAnim(headerImage);
            }
            if (headerText.getVisibility() == View.VISIBLE) {
                animationHelper.hideWithFadeAnim(headerText);
            }
        } else {
            reserveLabel.setVisibility(View.INVISIBLE);
            reserveText.setVisibility(View.INVISIBLE);
            reserveText.setText("");
            reserveText.setClickable(false);
            reserveText.setOnClickListener(null);
            reserveLabel.setClickable(false);
            reserveLabel.setOnClickListener(null);
            if (this.headerId > 0) {
                navIcon.setVisibility(View.VISIBLE);
                showHeaderText(this.headerId);
            } else {
                navIcon.setVisibility(View.INVISIBLE);
                showHeaderImage();
            }
        }
    }

    @Override
    public void hideNavigationIcon() {
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.hideNavigationIcon();
    }

    @Override
    public void changeNavigationIcon(int icon) {
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.changeNavigationIcon(icon);
    }

    private int headerId = 0;

    @Override
    public void showHeaderText(int text) {
        this.headerId = text;
        if (getApp().getReserves().size() > 0) {
            return;
        }
        super.showHeaderText(text);
    }

    @Override
    public void showHeaderImage() {
        this.headerId = 0;
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
