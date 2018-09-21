package kr.keumyoung.mukin.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 *  on 19/01/18.
 */
// unused
public class ResizeAnimation extends Animation {
    final int targetHeight;
    View view;
    int startHeight;

    boolean hiding;

    public ResizeAnimation(View view, int targetHeight, int startHeight, boolean hiding) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.startHeight = startHeight;
        this.hiding = hiding;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (hiding) {
            newHeight = (int) (startHeight - startHeight * interpolatedTime);
        } else {
            newHeight = (int) (startHeight + startHeight * interpolatedTime);
        }

        view.getLayoutParams().height = newHeight;
        view.getParent().requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}