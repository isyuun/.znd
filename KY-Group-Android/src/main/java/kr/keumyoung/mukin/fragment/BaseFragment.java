package kr.keumyoung.mukin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import kr.keumyoung.mukin.activity.HomeActivity;

/**
 *  on 12/01/18.
 */

public class BaseFragment extends Fragment {

    public HomeActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (HomeActivity) getActivity();
    }

    public boolean onNavigationClick() {
        return false;
    }

    public boolean onMenuClick() {
        return false;
    }
}
