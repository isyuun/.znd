package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class _notice extends html {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myWebView.loadUrl("https://www.keumyoung.kr:444/mukinapp/_notice.asp");
    }
}
