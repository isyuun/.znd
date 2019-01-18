package kr.keumyoung.mukin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Artist;
import kr.keumyoung.mukin.data.model.Genre;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.util.Constants;

/**
 * on 12/01/18.
 */

public class HomeFragment extends _BaseFragment {

    @Inject
    Bus bus;

    @Inject
    AnimationHelper animationHelper;

    @BindView(R.id.search_edit_frame)
    CardView searchEditFrame;
    @BindView(R.id.newsongs_item)
    LinearLayout featuredItem;
    @BindView(R.id.tophits_item)
    LinearLayout tophitsItem;
    @BindView(R.id.genres_item)
    LinearLayout genresItem;
    @BindView(R.id.favorites_item)
    LinearLayout artistsItem;
    @BindView(R.id.list_section)
    LinearLayout listSection;
    @BindView(R.id.search_et)
    EditText searchEt;
    @BindView(R.id.search_btn)
    ImageButton searchBTN;
    @BindView(R.id.search_close)
    ImageView searchClose;
    @BindView(R.id.newsongs_ripple)
    RippleView featuredRipple;
    @BindView(R.id.child_fragment_container)
    FrameLayout childFragmentContainer;
    @BindView(R.id.search_edit_ripple)
    RippleView searchEditRipple;
    @BindView(R.id.top_hit_ripple)
    RippleView topHitRipple;
    @BindView(R.id.genre_ripple)
    RippleView genreRipple;
    @BindView(R.id.favorite_ripple)
    RippleView favoriteRipple;

    RecommendedFragment recommendedFragment;
    FeaturedFragment featuredFragment;
    TopHitsFragment topHitsFragment;
    GenreFragment genreFragment;
    SearchFragment searchFragment;
    SongsFragment songsFragment;
    ArtistFragment artistFragment;
    FavoritesFragment favoritesFragment;

    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (currentListFragment == null) loadRecommendedFragment();
    }

    private void loadRecommendedFragment() {
        if (recommendedFragment == null) recommendedFragment = new RecommendedFragment();
        replaceListFragment(recommendedFragment);
    }

    public void replaceListFragment(_BaseListFragment fragment) {
        if (currentListFragment != null &&
                fragment.getClass().getSimpleName().equalsIgnoreCase(
                        currentListFragment.getClass().getSimpleName()))
            return;
        currentListFragment = fragment;
        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentManager manager = getChildFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(fragmentTag, 0);

        if (!fragmentPopped) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.child_fragment_container, fragment);
            transaction.addToBackStack(fragmentTag);
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //CommonHelper.hideSoftKeyboard(activity);
    }

    @Subscribe
    public void onArtistSelected(Artist artist) {
        songsFragment = new SongsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.SELECTION, Constants.ARTIST);
        arguments.putString(Constants.FILTER, "artistid=" + artist.getArtistId());
        arguments.putString(Constants.ID, artist.getArtistId());

        songsFragment.setArguments(arguments);
        replaceListFragment(songsFragment);
    }

    @Subscribe
    public void onGenreSelected(Genre genre) {
        songsFragment = new SongsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.SELECTION, Constants.GENRE);
        arguments.putString(Constants.FILTER, "genreid=" + genre.getGenreId());
        arguments.putString(Constants.ID, genre.getGenreId());

        songsFragment.setArguments(arguments);
        replaceListFragment(songsFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.search_edit_ripple, R.id.newsongs_item, R.id.tophits_item, R.id.genres_item, R.id.favorites_item, R.id.search_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_edit_ripple:
                //dsjung 장르, 노래 프레그먼트 검색창 클릭 안되는 문제로
                //Song, Genre 프레그먼트 추가
                if (currentListFragment instanceof RecommendedFragment
                        || currentListFragment instanceof FeaturedFragment
                        || currentListFragment instanceof TopHitsFragment
                        || currentListFragment instanceof GenreFragment //?
                        || currentListFragment instanceof SongsFragment //?
                        || currentListFragment instanceof ArtistFragment) {
                    searchEditRipple.setOnRippleCompleteListener(rippleView -> {
                        if (searchFragment == null) searchFragment = new SearchFragment();
                        replaceListFragment(searchFragment);
                    });
                }
                break;
            case R.id.newsongs_item:
                featuredRipple.setOnRippleCompleteListener(rippleView -> {
                    if (featuredFragment == null) featuredFragment = new FeaturedFragment();
                    replaceListFragment(featuredFragment);
                });
                break;
            case R.id.tophits_item:
                topHitRipple.setOnRippleCompleteListener(rippleView -> {
                    if (topHitsFragment == null) topHitsFragment = new TopHitsFragment();
                    replaceListFragment(topHitsFragment);
                });
                break;
            case R.id.genres_item:
                genreRipple.setOnRippleCompleteListener(rippleView -> {
                    if (genreFragment == null) genreFragment = new GenreFragment();
                    replaceListFragment(genreFragment);
                });
                break;
            case R.id.favorites_item:
                favoriteRipple.setOnRippleCompleteListener(rippleView -> {
                    //if (artistFragment == null) artistFragment = new ArtistFragment();
                    //replaceListFragment(artistFragment);
                    if (favoritesFragment == null) favoritesFragment = new FavoritesFragment();
                    replaceListFragment(favoritesFragment);
                });
                break;
            case R.id.search_close:
                searchEt.getText().clear();
                break;
        }
    }

    @Override
    public boolean onNavigationClick() {
        if (currentListFragment instanceof RecommendedFragment) return false;
        if (currentListFragment != null) return currentListFragment.onNavigationClick();
        return super.onNavigationClick();
    }

    @Override
    public boolean onMenuClick() {
        return super.onMenuClick();
    }

    public void popFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
            currentListFragment = (_BaseListFragment) fragmentManager.findFragmentById(R.id.child_fragment_container);
        }
    }

    public void hideIcons() {
//        animationHelper.hideViewWithSlideUpAnim(listSection);
        listSection.setVisibility(View.GONE);
    }

    public void showIcons() {
//        animationHelper.showViewWithSlideUpAnim(listSection);
        listSection.setVisibility(View.VISIBLE);
    }

    public void showSearch() {
        searchEditFrame.setVisibility(View.VISIBLE);
        searchEditRipple.setVisibility(View.VISIBLE);
    }

    public void hideSearch() {
        searchEditFrame.setVisibility(View.GONE);
        searchEditRipple.setVisibility(View.GONE);
    }

    //public void activateSearch() {
    //    activateSearch(true);
    //}

    public void activateSearch(boolean clear) {
        searchEt.setEnabled(true);
        searchEt.setFocusableInTouchMode(true);
        searchClose.setVisibility(View.GONE);
        if (clear) searchEt.getText().clear();
        //dsjung
        searchEt.requestFocus();
    }

    public void popupKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(searchEt.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void deactivateSearch() {
        //searchEt.setFocusableInTouchMode(false);
        //searchEt.setEnabled(false);
        //searchClose.setVisibility(View.GONE);
        //
        //CommonHelper.hideSoftKeyboard(activity);
    }

    public void addTextWatcher(TextWatcher watcher) {
        searchEt.addTextChangedListener(watcher);
    }

    public void removeTextWatcher(TextWatcher watcher) {
        searchEt.removeTextChangedListener(watcher);
    }

    public void clearSearch() {
        searchEt.getText().clear();
    }
}
