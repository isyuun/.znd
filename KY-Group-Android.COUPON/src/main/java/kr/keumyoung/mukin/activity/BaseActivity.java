package kr.keumyoung.mukin.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.util.CommonHelper;

/**
 * on 11/01/18.
 */

// DI ==> Dependency Injection via Dagger. All the components can be found in MainModule.java under
//        dagger package and all the component can be found in MainComponent.java
// DF ==> Dream Factory

// class is not registered under manifest as it is never directly used
public class BaseActivity extends AppCompatActivity {

    public static final int SYSTEM_UI_HIDE_DELAY = 2000;
    // necessary constants for system ui updates
    Handler handler = new Handler();
    // runnable to update the system UI
    private final Runnable checkSystemUiRunnable = this::checkHideSystemUI;
    // base view holder for holding the ui components added in base activity
    ViewHolder viewHolder;
    // instance of main thread bus injected via DI
    @Inject
    Bus bus;
    // instance of animation helper. injected via DI
    @Inject
    AnimationHelper animationHelper;
    // instance of retrofit api instance for DF. injected via DI
    @Inject
    RestApi restApi;
    // instance of shared preference helper. injected via DI
    @Inject
    PreferenceHelper preferenceHelper;

    boolean isShowingProgress;
    private boolean keyboardListenersAttached = false;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = viewHolder.rootLayout.getRootView().getHeight() - viewHolder.rootLayout.getHeight();
            if (heightDiff >= 400) onShowKeyboard();
            else onHideKeyboard();
        }
    };

    public PreferenceHelper getPreferenceHelper() {
        return preferenceHelper;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void attachKeyboardListeners() {
        if (keyboardListenersAttached) return;
        viewHolder.rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }

    public void detachKeyboardListener() {
        if (keyboardListenersAttached) {
            viewHolder.rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
            keyboardListenersAttached = false;
        }
    }

    protected void onHideKeyboard() {
        // nothing to be handled from here
    }

    protected void onShowKeyboard() {
        // nothing to be handled from here
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DI injection
        MainApplication.getInstance().getMainComponent().inject(this);

        // explicitly inflating view for butter knife injection under ViewHolder class
        View view = getLayoutInflater().inflate(R.layout.activity_base, null, false);
        setContentView(view);

        // register the bus for catching all the events posted in the bus
        bus.register(this);

        viewHolder = new ViewHolder(view, this);

        // for manipulating the system UI
        setFlags();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the bus for discarding all the events further
        bus.unregister(this);
    }

    // method for adding views to the main container of the base activity
    public void inflateContainerView(View view) {
        viewHolder.containerFrame.removeAllViews();
        viewHolder.containerFrame.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFlags();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
    }

    private void checkHideSystemUI() {
        setFlags();
        handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
    }

    protected void setFlags() {
        // update the system UI.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (!withStatusBar()) flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    // to indicate if the status bar is needed in the activity or not.
    // -- shown by default
    // -- if not needed override this method in the respective activity to pass false
    protected boolean withStatusBar() {
        // if status bar is needed
        return true;
    }

    // to show the progress bar added in the base activity. it can be called from a background thread also
    // keep the things under runOnUiThread
    public void showProgress() {
        if (isShowingProgress) return;
        runOnUiThread(() -> {
            try {
                isShowingProgress = true;
                //animationHelper.showWithFadeAnim(viewHolder.progressBar);
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.containerFrame.setEnabled(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    // to hide the progress bar added in the base activity.
    public void hideProgress() {
        if (!isShowingProgress) return;
        runOnUiThread(() -> {
            try {
                isShowingProgress = false;
                //animationHelper.hideWithFadeAnim(viewHolder.progressBar);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.containerFrame.setEnabled(true);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isShowingProgress() {
        return isShowingProgress;
    }

    public void setProgressMessage(int message) {
        runOnUiThread(() -> {
            try {
                viewHolder.progressMessage.setText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setProgressMessage() {
        setProgressMessage(R.string.loading);
    }

    // returns the blank frame for popups to be added for the parent layout during inflating popup layouts
    public ViewGroup getPopupView() {
        return viewHolder.popupFrameLayout;
    }

    // adds the popup views to the popup frame
    public void addPopupView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        viewHolder.popupFrameLayout.removeAllViews();
        viewHolder.popupFrameLayout.addView(view);

        viewHolder.popupFrameLayout.setVisibility(View.INVISIBLE);
        animationHelper.showWithZoomAnim(viewHolder.popupFrameLayout);
    }

    // remove popup view from the frame and hide
    public void removePopupView() {
        viewHolder.popupFrameLayout.removeAllViews();
        animationHelper.hideViewWithZoomAnim(viewHolder.popupFrameLayout);
    }

    //// common instance for checking session expiration error for all DF API calls.
    //// if the session is expired, try to refresh the session and continue with session refresh listener
    //// else logout the internal session and land the user to the login screen
    //public boolean handleDFError(JSONObject errorObject, SessionRefreshListener listener) throws JSONException {
    //    String errorCode = errorObject.getJSONObject("error").getString("code");
    //    if (errorCode.equalsIgnoreCase("401")) { // session has expired. need to refresh the session_token
    //        restApi.refreshSessionToken(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN),
    //                preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
    //            @Override
    //            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //                try {
    //                    ResponseBody responseBody = response.body();
    //                    ResponseBody errorBody = response.errorBody();
    //                    if (responseBody != null) {
    //                        String responseString = responseBody.string();
    //                        JSONObject responseObject = new JSONObject(responseString);
    //                        String sessionToken = responseObject.getString("session_token");
    //                        preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, sessionToken);
    //                        listener.onSessionRefresh();
    //                    } else if (errorBody != null) {
    //                        listener.logout(BaseActivity.this);
    //                    }
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                    listener.logout(BaseActivity.this);
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<ResponseBody> call, Throwable t) {
    //                t.printStackTrace();
    //                listener.logout(BaseActivity.this);
    //            }
    //        });
    //        return true;
    //    }
    //    return false;
    //}


    /*static */class ViewHolder implements View.OnTouchListener, View.OnClickListener {
        @BindView(R.id.container_frame)
        FrameLayout containerFrame;
        @BindView(R.id.progress_bar)
        RelativeLayout progressBar;
        //@BindView(R.id.spin_kit)
        //SpinKitView spinKitView;
        @BindView(R.id.progress_message)
        TextView progressMessage;
        @BindView(R.id.popup_view)
        FrameLayout popupFrameLayout;
        @BindView(R.id.root_layout)
        RelativeLayout rootLayout;
        @BindView(R.id.app_version)
        TextView appVersion;

        BaseActivity baseActivity;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(View view, BaseActivity baseActivity) {
            this.baseActivity = baseActivity;
            ButterKnife.bind(this, view);

            // once the progress bar is shown, we don't want any touch event on the screen
            progressBar.setOnTouchListener(this);

            // on outside click of the popup, hide the popup
            popupFrameLayout.setOnClickListener(this);

            // on click to the layout hide the keyboard
            rootLayout.setOnClickListener(this);

            appVersion.setText(String.format("Ver: 1.%s", BuildConfig.VERSION_CODE));

            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onClick(View view) {
            if (view == popupFrameLayout) baseActivity.removePopupView();
            else if (view == rootLayout) CommonHelper.hideSoftKeyboard(baseActivity);
        }
    }
}
