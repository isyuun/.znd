package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.animation.ResizeAnimation;

import javax.inject.Inject;

/**
 *  on 16/01/18.
 */

public class AnimationHelper {

    @Inject
    Context context;

    @Inject
    public AnimationHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public void rotateInfinite(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        view.startAnimation(animation);
    }

    public void hideViewWithSlideUpAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        ResizeAnimation resizeAnimation = new ResizeAnimation(view, 0, view.getHeight(), true);
        resizeAnimation.setDuration(500);
        resizeAnimation.setInterpolator(new DecelerateInterpolator());
        resizeAnimation.start();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showViewWithSlideUpAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        view.startAnimation(animation);

        ResizeAnimation resizeAnimation = new ResizeAnimation(view, 0, view.getHeight(), true);
        resizeAnimation.setDuration(500);
        resizeAnimation.setInterpolator(new DecelerateInterpolator());
        resizeAnimation.start();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideWithFadeAnim(View view) {
        hideWithFadeAnim(view, false);
    }

    public void hideWithFadeAnim(View view, boolean keepLayout) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(keepLayout ? View.INVISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showWithFadeAnim(View view) {
        showWithFadeAnim(view, false, 500);
    }

    public void showWithFadeAnim(View view, boolean withDelay, int duration) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setDuration(duration);
        if (withDelay) animation.setStartOffset(300);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showHeaderText(View view) {
        showHeaderText(view, true);
    }

    public void showHeaderText(View view, boolean withDelay) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_text_left_in);
        if (withDelay) animation.setStartOffset(300);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideHeaderText(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_text_left_out);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void updateImageResource(ImageView imageView, int newResource) {
        hideWithFadeAnim(imageView, true);
        imageView.setImageResource(newResource);
        showWithFadeAnim(imageView);
    }

    public void showWithZoomAnim(View view) {
        view.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideViewWithZoomAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void highlightView(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.highlight);
        animation.setStartOffset(200);
        view.startAnimation(animation);
    }
}
