package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

public class _license extends html {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.loadUrl("https://www.keumyoung.kr:444/mukinapp/_LICENSE.html");
    }
}
