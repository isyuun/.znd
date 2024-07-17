package kr.kymedia.karaoke.play.app;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import kr.kymedia.karaoke.app.FileOpenActivity;
import kr.kymedia.karaoke.util._Log;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class BaseFullActivity extends FileOpenActivity implements View.OnTouchListener, GestureDetector.OnDoubleTapListener {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    /**
     * Detects and toggles immersive mode.
     */
    public void toggleHideyBar() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            _Log.e(__CLASSNAME__, getMethodName() + "Turning immersive mode mode off. " + isImmersiveModeEnabled);
        } else {
            _Log.e(__CLASSNAME__, getMethodName() + "Turning immersive mode mode on." + isImmersiveModeEnabled);
        }

        // Immersive mode: Backward compatible to KitKat (API 19).
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // This sample uses the "sticky" form of immersive mode, which will let the user swipe
        // the bars back in again, but will automatically make them disappear a few seconds later.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
        //
        post(showActionBar);
    }

    public void toggleFullScreen() {
        toggleHideyBar();
        post(hideActionBar);
    }


    private Runnable showActionBar = new Runnable() {
        @Override
        public void run() {
            getSupportActionBar().show();
        }
    };

    private Runnable hideActionBar = new Runnable() {
        @Override
        public void run() {
            getSupportActionBar().hide();
        }
    };

    // This is the gesture detector compat instance.
    private GestureDetectorCompat mDetector = null;

    public GestureDetectorCompat getGestureDetector() {
        return this.mDetector;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _Log.e(__CLASSNAME__, getMethodName());

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        //_Log.e(__CLASSNAME__, "onSystemUiVisibilityChange()" + visibility + ":" + (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN));
                        //// Note that system bars will only be "visible" if none of the
                        //// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        //if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        //    // TODO: The system bars are visible. Make any desired
                        //    // adjustments to your UI, such as showing the action bar or
                        //    // other navigational controls.
                        //} else {
                        //    // TODO: The system bars are NOT visible. Make any desired
                        //    // adjustments to your UI, such as hiding the action bar or
                        //    // other navigational controls.
                        //}
                    }
                });

        //// Create a common gesture listener object.
        //DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        //
        //// Set activity in the listener.
        //gestureListener.setActivity(this);

        // Create the gesture detector with the gesture listener.
        mDetector = new GestureDetectorCompat(this,
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    _Log.e(__CLASSNAME__, "onDoubleTap()");
                    toggleHideyBar();
                    return super.onDoubleTap(e);
                }

                //@Override
                //public boolean onDown(MotionEvent e) {
                //    return true;
                //}
                //
                //@Override
                //public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //    //_Log.e(__CLASSNAME__, "onFling()");
                //    //toggleHideyBar();
                //    return super.onFling(e1, e2, velocityX, velocityY);
                //}
            });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //_Log.i(__CLASSNAME__, getMethodName() + " event:" + event);
        // Pass activity on touch event to the gesture detector.
        mDetector.onTouchEvent(event);
        // Return true to tell android OS that event has been consumed, do not pass it to other event listeners.
        return true;
    }

    public int getStatusBarHeight() {
        //int result = 0;
        //int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        //if (resourceId > 0) {
        //    result = getResources().getDimensionPixelSize(resourceId);
        //}
        //return result;
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        _Log.i("*** Elenasys :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);
        return statusBarHeight;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        _Log.i(__CLASSNAME__, getMethodName() + " hasCapture:" + hasCapture);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        _Log.i(__CLASSNAME__, getMethodName() + " view:" + view + " motionEven:" + motionEvent);
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        _Log.i(__CLASSNAME__, getMethodName() + " motionEven:" + motionEvent);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        _Log.i(__CLASSNAME__, getMethodName() + " motionEven:" + motionEvent);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        _Log.i(__CLASSNAME__, getMethodName() + " motionEven:" + motionEvent);
        return false;
    }

}
