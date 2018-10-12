package kr.keumyoung.mukin.dagger;

import android.content.Context;

import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.api.KakaoApi;
import kr.keumyoung.mukin.api.NetworkServiceGenerator;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.bus.MainThreadBus;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.AudioMixingHelper;
import kr.keumyoung.mukin.helper.BlurHelper;
import kr.keumyoung.mukin.helper.DateHelper;
import kr.keumyoung.mukin.helper.DownloadHelper;
import kr.keumyoung.mukin.helper.FileHelper;
import kr.keumyoung.mukin.helper.LyricsHelper;
import kr.keumyoung.mukin.helper.LyricsTimingHelper;
import kr.keumyoung.mukin.helper.MediaManager;
import kr.keumyoung.mukin.helper.PreferenceHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * on 18/05/17.
 */

@Module
public class MainModule {

    @Provides
    @Singleton
    Context provideContext() {
        return MainApplication.getInstance().getApplicationContext();
    }

    @Provides
    @Singleton
    RestApi provideRestApi() {
        return NetworkServiceGenerator.createService(RestApi.class, AppConstants.API_BASE_URL);
    }

    @Provides
    @Singleton
    KakaoApi providesKakaoApi() {
        return NetworkServiceGenerator.createService(KakaoApi.class, AppConstants.KAKAO_BASE_URL);
    }

    @Provides
    @Singleton
    PreferenceHelper providePreferenceManager() {
        return new PreferenceHelper();
    }

    @Provides
    @Singleton
    MediaManager providesMediaManager() {
        return new MediaManager();
    }

    @Provides
    @Singleton
    Bus providesBus() {
        return new MainThreadBus();
    }

    @Provides
    @Singleton
    ToastHelper providesToastHeler() {
        return new ToastHelper(provideContext());
    }

    @Provides
    @Singleton
    AnimationHelper providesAnimationHelper() {
        return new AnimationHelper();
    }

    @Provides
    @Singleton
    DateHelper providesDateHelper() {
        return new DateHelper();
    }

    @Provides
    @Singleton
    DownloadHelper providesDownloadHelper() {
        return new DownloadHelper();
    }

    @Provides
    @Singleton
    BlurHelper providesBlurHelper() {
        return new BlurHelper();
    }

    @Provides
    @Singleton
    FileHelper providesFileHelper() {
        return new FileHelper();
    }

    @Provides
    @Singleton
    LyricsHelper providesLyricsHelper() {
        return new LyricsHelper();
    }

    @Provides
    @Singleton
    AudioMixingHelper providesAudioMixingHelper() {
        return new AudioMixingHelper();
    }

    @Provides
    @Singleton
    LyricsTimingHelper providesLyricsTimingHelper() {
        return new LyricsTimingHelper();
    }
}
