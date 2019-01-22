package kr.keumyoung.mukin.activity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
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

    @BindView(R.id.reserve_anchor)
    View reserveAnchor;

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
        });
        setReserves();
    }

    protected void play() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        if (getApp().getReserves().size() > 0) {
            onSongSelected(getApp().getReserves().get(0));
        }
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
        setReserves();
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
        setReserves();
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

    protected void forceRippleAnimation(View view) {
        Drawable background = view.getBackground();

        if (Build.VERSION.SDK_INT >= 21 && background instanceof RippleDrawable) {
            final RippleDrawable rippleDrawable = (RippleDrawable) background;

            rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    rippleDrawable.setState(new int[]{});
                }
            }, 200);
        }
    }

    public void setReserves() {
        String text = getApp().getReserves().toString();
        setTextViewMarquee(reserveText, true);

        if (getApp().getReserves().size() > 0) forceRippleAnimation(reserveAnchor);

        if (getApp().getReserves().size() > 0) {
            reserveLabel.setVisibility(View.VISIBLE);
            animationHelper.showHeaderText(reserveText);
            reserveText.setText(text);
            reserveAnchor.setClickable(true);
            reserveAnchor.setOnClickListener(v -> openReserves());
            navIcon.setVisibility(View.GONE);
            hideHeaders();
        } else {
            reserveLabel.setVisibility(View.INVISIBLE);
            reserveText.setVisibility(View.INVISIBLE);
            reserveText.setText("");
            reserveAnchor.setClickable(false);
            reserveAnchor.setOnClickListener(null);
            showHeaders();
        }
        //if (getApp().getReserves().size() > 0) {
        //    play.setVisibility(View.VISIBLE);
        //} else {
        //    play.setVisibility(View.INVISIBLE);
        //}
    }

    private void showHeaders() {
        if (this.headerId > 0) {
            navIcon.setVisibility(View.VISIBLE);
            showHeaderText(this.headerId);
        } else {
            navIcon.setVisibility(View.INVISIBLE);
            showHeaderImage();
        }
    }

    private void hideHeaders() {
        if (headerImage.getVisibility() == View.VISIBLE) {
            animationHelper.hideWithFadeAnim(headerImage, true);
        }
        if (headerText.getVisibility() == View.VISIBLE) {
            animationHelper.hideWithFadeAnim(headerText, true);
        }
    }

    @Override
    public void showMenuIcon() {
        //isyyun:가뫈좀놔둔다...
        //super.showMenuIcon();
    }

    @Override
    public void hideMenuIcon() {
        //isyyun:가뫈좀놔둔다...
        //super.hideMenuIcon();
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
