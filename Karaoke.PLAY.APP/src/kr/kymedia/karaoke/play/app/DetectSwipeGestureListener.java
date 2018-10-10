package kr.kymedia.karaoke.play.app;

import android.view.GestureDetector;
import android.view.MotionEvent;

import kr.kymedia.karaoke.util.Log;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }

    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.
    private BaseFullActivity activity = null;

    public BaseFullActivity getActivity() {
        return activity;
    }

    public void setActivity(BaseFullActivity activity) {
        this.activity = activity;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
            if (deltaX > 0) {
                //this.activity.displayMessage("Swipe to left");
                Log.e(__CLASSNAME__, getMethodName() + "Swipe to left");
            } else {
                //this.activity.displayMessage("Swipe to right");
                Log.e(__CLASSNAME__, getMethodName() + "Swipe to right");
            }
        }

        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            if (deltaY > 0) {
                //this.activity.displayMessage("Swipe to up");
                Log.e(__CLASSNAME__, getMethodName() + "Swipe to up");
            } else {
                //this.activity.displayMessage("Swipe to down");
                Log.e(__CLASSNAME__, getMethodName() + "Swipe to down");
            }
        }


        return true;
    }

    // Invoked when single tap screen.
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.e(__CLASSNAME__, getMethodName() + e);
        //this.activity.displayMessage("Single tap occurred.");
        return true;
    }

    // Invoked when double tap screen.
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.e(__CLASSNAME__, getMethodName() + e);
        //this.activity.displayMessage("Double tap occurred.");
        return true;
    }
}
