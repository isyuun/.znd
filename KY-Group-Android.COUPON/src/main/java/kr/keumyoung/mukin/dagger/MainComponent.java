package kr.keumyoung.mukin.dagger;

import kr.keumyoung.mukin.activity.BaseActivity;
import kr.keumyoung.mukin.activity.HomeActivity;
import kr.keumyoung.mukin.activity.LoginActivity;
//import kr.keumyoung.mukin.activity.LoginChoiceActivity;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.activity.PreviewActivity;
import kr.keumyoung.mukin.activity.RegisterActivity;
import kr.keumyoung.mukin.activity.ShareActivity;
import kr.keumyoung.mukin.activity.SplashScreenActivity;
import kr.keumyoung.mukin.adapter.ArtistAdapter;
import kr.keumyoung.mukin.adapter.GenreAdapter;
import kr.keumyoung.mukin.adapter.ShareAdapter;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Artist;
import kr.keumyoung.mukin.data.model.Genre;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.SongModel;
import kr.keumyoung.mukin.elements.ControlPanel;
import kr.keumyoung.mukin.elements.ControlPanelItem;
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.ControlsPopup;
import kr.keumyoung.mukin.elements.EffectsPopup;
import kr.keumyoung.mukin.elements.EffectsPopupItem;
import kr.keumyoung.mukin.elements.ModePopup;
import kr.keumyoung.mukin.elements.ModePopupItem;
import kr.keumyoung.mukin.elements.OperationPopupItem;
import kr.keumyoung.mukin.elements.TempoPopup;
import kr.keumyoung.mukin.fragment.ArtistFragment;
import kr.keumyoung.mukin.fragment.FavoritesFragment;
import kr.keumyoung.mukin.fragment.FeaturedFragment;
import kr.keumyoung.mukin.fragment.GenreFragment;
import kr.keumyoung.mukin.fragment.HomeFragment;
import kr.keumyoung.mukin.fragment.RecommendedFragment;
import kr.keumyoung.mukin.fragment.SearchFragment;
import kr.keumyoung.mukin.fragment.SongsFragment;
import kr.keumyoung.mukin.fragment.TopHitsFragment;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.AudioMixingHelper;
import kr.keumyoung.mukin.helper.BlurHelper;
import kr.keumyoung.mukin.helper.DownloadHelper;
import kr.keumyoung.mukin.helper.FileHelper;
import kr.keumyoung.mukin.helper.LyricsHelper;
import kr.keumyoung.mukin.helper.LyricsTimingHelper;
import kr.keumyoung.mukin.helper.MediaManager;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.popup.ImageOptionChooserPopup;
import kr.keumyoung.mukin.viewholder.ArtistViewHolder;
import kr.keumyoung.mukin.viewholder.GenreViewHolder;
import kr.keumyoung.mukin.viewholder.ShareViewHolder;
import kr.keumyoung.mukin.viewholder.SongViewHolder;

import javax.inject.Singleton;

import dagger.Component;

/**
 *  on 18/05/17.
 */

@Component(modules = {MainModule.class})
@Singleton
public interface MainComponent {

    void inject(MediaManager mediaManager);

    void inject(PreferenceHelper preferenceHelper);

    void inject(BaseActivity baseActivity);

    void inject(PlayerActivity baseActivity);

    void inject(ShareActivity baseActivity);

    void inject(PreviewActivity baseActivity);

    void inject(RegisterActivity baseActivity);

    void inject(HomeActivity baseActivity);

    void inject(LoginActivity baseActivity);

    //void inject(LoginChoiceActivity baseActivity);

    void inject(SplashScreenActivity baseActivity);

    void inject(HomeFragment homeFragment);

    void inject(SongAdapter songAdapter);

    void inject(RecommendedFragment fragment);

    void inject(FeaturedFragment featuredFragment);

    void inject(GenreFragment genreFragment);

    void inject(GenreAdapter genreAdapter);

    void inject(ArtistFragment artistFragment);

    void inject(ArtistAdapter artistAdapter);

    void inject(SongsFragment songsFragment);

    void inject(ArtistViewHolder artistViewHolder);

    void inject(GenreViewHolder genreViewHolder);

    void inject(SongViewHolder songViewHolder);

    void inject(SearchFragment searchFragment);

    void inject(ControlPanelItem controlPanelItem);

    void inject(ControlPanelPlay controlPanelPlay);

    void inject(AnimationHelper animationHelper);

    void inject(ControlPanel controlPanel);

    void inject(EffectsPopup effectsPopup);

    void inject(EffectsPopupItem effectsPopupItem);

    void inject(ModePopupItem modePopupItem);

    void inject(OperationPopupItem operationPopupItem);

    void inject(ControlsPopup controlsPopup);

    void inject(TempoPopup tempoPopup);

    void inject(ModePopup controlsPopup);

    void inject(ShareViewHolder shareViewHolder);

    void inject(ShareAdapter shareAdapter);

    void inject(ImageOptionChooserPopup imageOptionChooserPopup);

    void inject(Genre genre);

    void inject(SessionRefreshListener listener);

    void inject(Artist artist);

    void inject(Song song);

    void inject(SongModel songModel);

    void inject(DownloadHelper downloadHelper);

    void inject(BlurHelper blurHelper);

    void inject(FileHelper fileHelper);

    void inject(LyricsHelper lyricsHelper);

    void inject(AudioMixingHelper audioMixingHelper);

    void inject(LyricsTimingHelper lyricsTimingHelper);

    void inject(TopHitsFragment topHitsFragment);

    void inject(FavoritesFragment favoritesFragment);
}
