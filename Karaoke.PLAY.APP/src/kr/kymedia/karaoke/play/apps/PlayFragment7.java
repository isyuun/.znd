package kr.kymedia.karaoke.play.apps;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import kr.kymedia.karaoke.play.app.BaseFullActivity;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.util._Log;

public class PlayFragment7 extends PlayFragment6 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.full_screen) {
            if (getActivity() instanceof BaseFullActivity) {
                ((BaseFullActivity) getActivity()).toggleFullScreen();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private GestureDetectorCompat mDetector = null;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _Log.e(__CLASSNAME__, getMethodName());
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (getActivity() instanceof BaseFullActivity) {
            //
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((BaseFullActivity) getActivity()).getGestureDetector().onTouchEvent(event);
                    return true;
                }
            });
            //
            //for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            //    ((ViewGroup) v).getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
            //        @Override
            //        public boolean onTouch(View v, MotionEvent event) {
            //            ((BaseFullActivity) getActivity()).getGestureDetector().onTouchEvent(event);
            //            return true;
            //        }
            //    });
            //}
        }

        return v;
    }
}
