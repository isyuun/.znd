package kr.keumyoung.mukin.activity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.fragment.HomeFragment;
import kr.keumyoung.mukin.fragment.ReservesFragment;
import kr.keumyoung.mukin.fragment._BaseListFragment;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class BaseActivity9 extends BaseActivity8 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onReserveSelected(Song song) {
        getApp().onReserveSelected(song);
        updateReserveSongs();
    }

    private void updateReserveSongs() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + getCurrentFragment() + ":" + getChildCurrentFragment());
        if (getChildCurrentFragment() != null && getChildCurrentFragment() instanceof _BaseListFragment) {
            ((_BaseListFragment) getChildCurrentFragment()).updateSongs();
        }
    }

    ReservesFragment reservesFragment;

    protected void openReserves() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        post(openReserves);
    }

    private Runnable openReserves = new Runnable() {
        @Override
        public void run() {
            if (reservesFragment == null) reservesFragment = new ReservesFragment();
            if (getCurrentFragment() instanceof HomeFragment) {
                ((HomeFragment) getCurrentFragment()).replaceListFragment(reservesFragment);
            }
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

    public void onPopupClose() {}

    public int tempo() {
        return preferenceHelper.getInt(PreferenceKeys.TEMPO_VALUE);
    }

    public void tempo(int tempo) {
        preferenceHelper.saveInt(PreferenceKeys.TEMPO_VALUE, tempo);
    }

    public int pitch() {
        return preferenceHelper.getInt(PreferenceKeys.PITCH_VALUE);
    }

    public void pitch(int pitch) {
        preferenceHelper.saveInt(PreferenceKeys.PITCH_VALUE, pitch);
    }
}
