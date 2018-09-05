package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import kr.keumyoung.karaoke.mukin.coupon.Fragment2;
import kr.keumyoung.karaoke.mukin.coupon.R;


public class html extends Fragment2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_html, container, false);
    }

    WebView myWebView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myWebView = (WebView) findViewById(R.id.webview);
    }
}
