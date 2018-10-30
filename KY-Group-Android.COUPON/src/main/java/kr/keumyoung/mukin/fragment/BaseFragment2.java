package kr.keumyoung.mukin.fragment;

import android.os.Handler;

public class BaseFragment2 extends BaseFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    Handler handler = new Handler();

    public final boolean post(Runnable r) {
        handler.removeCallbacks(r);
        return handler.post(r);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        handler.removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }
}
