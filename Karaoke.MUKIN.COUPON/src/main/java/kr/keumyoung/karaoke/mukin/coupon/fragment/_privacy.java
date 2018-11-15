package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class _privacy extends html {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.loadUrl("https://www.keumyoung.kr:444/mukinapp/_PRIVACY.html");
    }
}
