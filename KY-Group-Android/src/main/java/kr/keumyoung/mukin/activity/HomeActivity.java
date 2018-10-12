package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.request.SongHitRequest;
import kr.keumyoung.mukin.fragment.BaseFragment;
import kr.keumyoung.mukin.fragment.HomeFragment;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.CommonHelper;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.MicChecker;
import kr.keumyoung.mukin.util.PreferenceKeys;
import kr.keumyoung.mukin.util.RandromAlbumImage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * on 11/01/18.
 */

// DI ==> Dependency Injection via Dagger. All the components can be found in MainModule.java under
//        dagger package and all the component can be found in MainComponent.java
// DF ==> Dream Factory

public class HomeActivity extends BaseActivity {

    // view injections by butterknife
    @BindView(R.id.nav_icon)
    ImageView navIcon;
    @BindView(R.id.header_image)
    ImageView headerImage;
    @BindView(R.id.menu_icon)
    ImageView menuIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.side_menu_logo)
    ImageView sideMenuLogo;
    @BindView(R.id.side_menu_recycler)
    RecyclerView sideMenuRecycler;
    @BindView(R.id.menu_container)
    FrameLayout menuContainer;
    @BindView(R.id.main_drawer)
    DrawerLayout mainDrawer;
    @BindView(R.id.header_text)
    TextView headerText;

    // injections through DI
    @Inject
    Bus bus;

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    AnimationHelper animationHelper;

    @Inject
    RestApi restApi;

    @Inject
    ToastHelper toastHelper;

    // current fragment always holds the currently added fragment to the container
    BaseFragment currentFragment;

    HomeFragment homeFragment;
    boolean backPressed = false;
    private Song song;
    private SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            if (song != null) onSongSelected(song);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_home, null, false);
        inflateContainerView(view);
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this, view);

        closeDrawer();

        initiateSideMenu();

        openHome();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // go to home fragment from anywhere
    private void openHome() {
        if (homeFragment == null) homeFragment = new HomeFragment();
        replaceFragment(homeFragment);
    }

    private void initiateSideMenu() {
        // nothing to do with the side menu now
    }

    public void replaceFragment(BaseFragment fragment) {
        replaceFragment(fragment, true, R.id.fragment_container);
    }

    // method made specifically for side menu. now unused
    public void replaceFragment(BaseFragment fragment, int containerId) {
        replaceFragment(fragment, true, containerId);
    }

    public void replaceFragment(BaseFragment fragment, boolean addToStack, int containerId) {
        currentFragment = fragment;
        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentManager manager = getSupportFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(fragmentTag, 0);

        if (!fragmentPopped) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (addToStack) transaction.addToBackStack(fragmentTag);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(containerId, fragment);
            transaction.commit();
        }
    }

    public boolean popFragment() {
        boolean result = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            result = getSupportFragmentManager().popBackStackImmediate();
            currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    // on song selection, open the player activity after registering the user hit
    @Subscribe
    public void onSongSelected(Song song) {
        this.song = song;
        CommonHelper.hideSoftKeyboard(this);

        showProgress();
        setProgressMessage();
        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(preferenceHelper.getString(PreferenceKeys.USER_ID), song.getSongId()));
        restApi.updateSongHits(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), model)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            ResponseBody responseBody = response.body();
                            ResponseBody errorBody = response.errorBody();

                            if (responseBody != null) {
                                hideProgress();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.SONG, song);
                                // navigate to player activity for playing the media and processing
                                navigationHelper.navigate(HomeActivity.this, PlayerActivity.class, false, bundle);
                            } else if (errorBody != null) {
                                String errorString = errorBody.string();
                                JSONObject errorObject = new JSONObject(errorString);
                                if (!handleDFError(errorObject, sessionRefreshListener)) {
                                    hideProgress();
                                    toastHelper.showError(R.string.common_api_error);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgress();
                            toastHelper.showError(R.string.common_api_error);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideProgress();
                        toastHelper.showError(R.string.common_api_error);
                    }
                });
    }

    protected void openDrawer() {
        mainDrawer.openDrawer(menuContainer, true);
    }

    protected void closeDrawer() {
        mainDrawer.closeDrawer(menuContainer, false);
        mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @OnClick({R.id.nav_icon, R.id.menu_icon, R.id.side_menu_logo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nav_icon:
                onNavigationClick();
                break;
            case R.id.menu_icon:
                onMenuClick();
                break;
            case R.id.side_menu_logo:
                // now invalid
                logout();
                break;
        }
    }

    private void logout() {
//        preferenceHelper.saveString(PreferenceKeys.SESSION_TOKEN, "");
//        preferenceHelper.saveString(PreferenceKeys.USER_ID, "");
        preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
        preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
        navigationHelper.navigateWithReverseAnim(this, LoginChoiceActivity.class);
    }

    private void onMenuClick() {
        if (currentFragment != null && currentFragment.onMenuClick()) return;

        callLogout();
    }

    private void callLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_logout));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    showProgress();
                    restApi.logout(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            hideProgress();
                            preferenceHelper.saveString(PreferenceKeys.LOGIN_PASSWORD, "");
                            preferenceHelper.saveString(PreferenceKeys.LOGIN_EMAIL, "");
                            navigationHelper.navigateWithClearTask(HomeActivity.this, LoginChoiceActivity.class);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hideProgress();
                        }
                    });
                }
        );

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void onNavigationClick() {
        // first check for the navigation click listener in fragment. if handled, do nothing else toggle navigation drawer
        if (currentFragment != null && currentFragment.onNavigationClick()) return;
        if (mainDrawer.isDrawerOpen(Gravity.START)) closeDrawer();
        else openDrawer();
    }

    public void changeNavigationIcon(int icon) {
        animationHelper.hideWithFadeAnim(navIcon, true);
        navIcon.setImageResource(icon);
        animationHelper.showWithFadeAnim(navIcon);
    }

    public void setHeaderText(int text) {
        if (headerImage.getVisibility() == View.VISIBLE)
            animationHelper.hideWithFadeAnim(headerImage);

        headerText.setText(text);
        animationHelper.showHeaderText(headerText);
    }

    public void showHeaderImage() {
        if (headerImage.getVisibility() != View.VISIBLE)
            animationHelper.showWithFadeAnim(headerImage, true, 500);
        if (headerText.getVisibility() == View.VISIBLE)
            animationHelper.hideHeaderText(headerText);
    }

    public void hideMenuIcon() {
        animationHelper.hideWithFadeAnim(menuIcon, true);
    }

    public void showMenuIcon() {
        animationHelper.showWithFadeAnim(menuIcon);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == null || !currentFragment.onNavigationClick()) {
            if (backPressed) {
                navigationHelper.finish(this);
            } else {
                backPressed = true;
                toastHelper.showGenericToast(R.string.tap_once_for_exit);
                new Handler().postDelayed(() -> backPressed = false, 2000);
            }
        }
    }

    public void instantHideHeaderImage() {
        headerImage.setVisibility(View.GONE);
    }

    public void hideNavigationIcon() {
        navIcon.setVisibility(View.INVISIBLE);
    }
}
