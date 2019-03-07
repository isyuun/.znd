package kr.keumyoung.mukin.activity;

import android.util.Log;
import android.view.KeyEvent;

import kr.keumyoung.mukin.BuildConfig;

public class HomeActivity4 extends HomeActivity3 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean ret = false;

        if (action == KeyEvent.ACTION_DOWN) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + keyCode + ":" + event);
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    play();
                    ret = true;
                    break;
                default:
                    break;
            }
        }

        if (ret) {
            return ret;
        }

        return super.dispatchKeyEvent(event);
    }

}
