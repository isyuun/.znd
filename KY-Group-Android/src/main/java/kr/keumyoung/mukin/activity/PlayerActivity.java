package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.PlayerLog;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;
import kr.keumyoung.mukin.data.bus.ModePopupAction;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.elements.ControlPanel;
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.EffectsPopup;
import kr.keumyoung.mukin.elements.EffectsPopupItem;
import kr.keumyoung.mukin.elements.ModePopup;
import kr.keumyoung.mukin.elements.ModePopupItem;
import kr.keumyoung.mukin.elements.OperationPopup;
import kr.keumyoung.mukin.elements.PitchPopup;
import kr.keumyoung.mukin.elements.TempoPopup;
import kr.keumyoung.mukin.elements.TwoLineLyricsView;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.AudioMixingHelper;
import kr.keumyoung.mukin.helper.DateHelper;
import kr.keumyoung.mukin.helper.DownloadHelper;
import kr.keumyoung.mukin.helper.ImageUtils;
import kr.keumyoung.mukin.helper.LyricsTimingHelper;
import kr.keumyoung.mukin.helper.MediaManager;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.AudioJNI;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PlayerJNI;
import kr.keumyoung.mukin.util.PreferenceKeys;

/**
 * on 13/01/18.
 */

public class PlayerActivity extends BaseActivity {

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 480;
    @Inject
    Bus bus;
    @Inject
    NavigationHelper navigationHelper;
    @Inject
    AnimationHelper animationHelper;
    @Inject
    ToastHelper toastHelper;
    @Inject
    MediaManager mediaManager;
    @Inject
    DateHelper dateHelper;
    @Inject
    RestApi restApi;
    @Inject
    DownloadHelper downloadHelper;
    @Inject
    LyricsTimingHelper lyricsTimingHelper;
    @Inject
    AudioMixingHelper audioMixingHelper;

    @BindView(R.id.close_icon)
    ImageView closeIcon;
    @BindView(R.id.header_song_name)
    TextView headerSongName;
    @BindView(R.id.header_song_sub_text)
    TextView headerSongSubText;
    @BindView(R.id.header_song_name_section)
    LinearLayout headerSongNameSection;
    @BindView(R.id.header_toolbar)
    LinearLayout headerToolbar;
    @BindView(R.id.time_progress_bar)
    ProgressBar timeProgressBar;
    @BindView(R.id.album_cover_image)
    RoundedImageView albumCoverImage;
    @BindView(R.id.song_name)
    TextView songName;
    @BindView(R.id.song_desc)
    TextView songDesc;
    @BindView(R.id.song_duration)
    TextView songDuration;
    @BindView(R.id.init_state_frame)
    RelativeLayout initStateFrame;
    @BindView(R.id.control_panel_component)
    ControlPanel controlPanelComponent;
    @BindView(R.id.controls_panel)
    RelativeLayout controlsPanel;
    @BindView(R.id.controls_panel_divider)
    View controlsPanelDivider;
    @BindView(R.id.controls_popup)
    RelativeLayout controlsPopup;
    @BindView(R.id.player_frame)
    RelativeLayout playerFrame;
    @BindView(R.id.status_text)
    TextView statusText;
    @BindView(R.id.close_ripple)
    RippleView closeRipple;
    @BindView(R.id.duration_text)
    TextView durationText;
    @BindView(R.id.status_bar)
    RelativeLayout statusBar;
    @BindView(R.id.init_frame_content)
    LinearLayout initFrameContent;
    @BindView(R.id.root_bg_image)
    ImageView rootBgImage;
    @BindView(R.id.lyrics_view)
    TwoLineLyricsView lyricsView;
    Song song;
    ImageUtils imageUtils;
    long mediaDuration, mediaCurrentPosition;
    EffectsPopup effectsPopup;
    ModePopup modePopup;
    TempoPopup tempoPopup;
    PitchPopup pitchPopup;
    OperationPopup operationPopup;
    ControlPanelItemAction lastAction;
    int setFileError;
    int initializeError;
    ArrayList<PlayerLog> playerLogs = new ArrayList<>();
    File songFile;
    long microTimePerClock = 0;
    boolean isPlayed = false, isPlaying = false;
    int bufferSize;
    int sampleRate;
    // progress listener
    ScheduledExecutorService service;
    long startTime;
    boolean closePlayer = false;
    boolean isFinished = false;
    @BindView(R.id.status_layout)
    LinearLayout statusLayout;
    private PlayerJNI playerJNI;
    private AudioJNI audioJNI;
    private String tempPath, destinationPath;

    public PlayerJNI getPlayerJNI() {
        return playerJNI;
    }

    public AudioJNI getAudioJNI() {
        return audioJNI;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_player, null, false);
        inflateContainerView(view);
        ButterKnife.bind(this, view);

        imageUtils = new ImageUtils(this);

        updateViewWithState(ControlPanelPlay.PlayButtonState.INIT);
        initiatePlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!closePlayer) return;

        if (effectsPopup != null) effectsPopup.onDestroy();
        if (modePopup != null) modePopup.onDestroy();

        lyricsTimingHelper.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (playerJNI != null) playerJNI.FinalizeAudio();
//        if (audioJNI != null) audioJNI.FinalizeAudio();
//        playerJNI = null;
//        audioJNI = null;
    }

    // initiate player. take song from the intent. download bitmap, song midi and song json
    private void initiatePlayer() {
        showProgress();
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.DATA)) {
            Bundle bundle = intent.getBundleExtra(Constants.DATA);
            if (bundle != null && bundle.containsKey(Constants.SONG))
                song = (Song) bundle.getSerializable(Constants.SONG);
        }

        if (song != null) {
            headerSongName.setText(song.getSongTitle());
            headerSongSubText.setText(song.getSongSubTitle());
            mediaManager.loadImageIntoView(song.getAlbumImage(), albumCoverImage);

            songName.setText(song.getSongTitle());
            songDesc.setText(song.getSongSubTitle());
            songDuration.setText(dateHelper.getDuration(song.getDuration()));
            durationText.setText(dateHelper.getDuration(song.getDuration()));

            String destinationPath = String.format("%s%s", ImageUtils.BASE_PATH, song.getSongFileName());
            String url = String.format("%s%s", Constants.FILE_API, song.getSongFile());
            System.out.println("destinationPath: " + destinationPath);
            System.out.println("url: " + url);

            showProgress();
            setProgressMessage(R.string.fetching_file);
            downloadHelper.download(url, song.getSongFileName())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        songFile = file;
                        downloadSongJson();
                    }, throwable -> {
                        throwable.printStackTrace();
                        toastHelper.showError(R.string.file_not_found);
                        preferenceHelper.clearSavedSettings();
                        navigationHelper.finish(this);
                    });

            downloadHelper.downloadBitmap(Constants.FILE_API + song.getAlbumImage())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        rootBgImage.setVisibility(View.INVISIBLE);
                        rootBgImage.setImageBitmap(bitmap);
                        animationHelper.showWithFadeAnim(rootBgImage);
                    }, Throwable::printStackTrace, () -> System.out.println("blurring completed"));
        } else {
            toastHelper.showError(R.string.song_not_playable);
            preferenceHelper.clearSavedSettings();
            navigationHelper.finish(this);
        }
    }

    private void downloadSongJson() {
        setProgressMessage(R.string.fetching_lyrics);
        String timingFileName = String.format("%s.json", song.getIdentifier());
        String url = String.format("%ssongs/json/%s", Constants.FILE_API, timingFileName);

        downloadHelper.download(url, timingFileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> lyricsTimingHelper.initiateWithLyricsView(this, lyricsView, file)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setProgressMessage, throwable -> {
                            throwable.printStackTrace();
                            hideProgress();
                        }, this::prepareMediaPlayer));
    }

    private void prepareMediaPlayer() {
        try {
            if (playerJNI == null) playerJNI = new PlayerJNI();

            initializeError =
                    playerJNI.Initialize(preferenceHelper.getString(PreferenceKeys.LIBRARY_PATH));
            String filePath = songFile.getAbsolutePath();
            if (playerJNI != null) playerJNI.SetPortSelectionMethod(5); // Type K
            setFileError = playerJNI.SetFile(filePath);
            microTimePerClock = 4000; //GetTimePerClock();
            setupProgressListener();

            // update pitch based on song gender
            if (modePopup == null) modePopup = new ModePopup(this);
            ModePopupAction modePopupActionSettings = new ModePopupAction(
                    song.getGender().equalsIgnoreCase(Constants.MALE) ?
                            ModePopup.ModeOptions.MALE : ModePopup.ModeOptions.FEMALE,
                    ModePopupItem.SelectionState.SELECTED
            );
            modePopup.onSelectionEffectItem(modePopupActionSettings);
            onSelectionModeItem(modePopupActionSettings);

            // update preset values
            int tempoValue = preferenceHelper.getInt(PreferenceKeys.TEMPO_VALUE, -1);
            if (tempoPopup == null) tempoPopup = new TempoPopup(this);
            if (tempoValue != -1) {
                if (playerJNI != null) playerJNI.SetSpeedControl(tempoValue);
                tempoPopup.updatePresetValue(tempoValue);
            }

            int pitchValue = preferenceHelper.getInt(PreferenceKeys.PITCH_VALUE, -1);
            if (pitchPopup == null) pitchPopup = new PitchPopup(this);
            if (pitchValue != -1) {
                if (playerJNI != null) playerJNI.SetKeyControl(pitchValue);
                pitchPopup.updatePresetValue(pitchValue);
            }

            int presetSongGender = preferenceHelper.getInt(PreferenceKeys.SONG_GENDER, -1);
            if (presetSongGender != -1) {
                ModePopupAction modePopupAction = new ModePopupAction(
                        presetSongGender == 0 ?
                                ModePopup.ModeOptions.MALE : ModePopup.ModeOptions.FEMALE,
                        ModePopupItem.SelectionState.SELECTED
                );
                modePopup.onSelectionEffectItem(modePopupAction);
                onSelectionModeItem(modePopupAction);
            }


            setupRecorder();
        } catch (Exception e) {
            e.printStackTrace();
            hideProgress();
            toastHelper.showError(R.string.file_not_found);
        }
    }

    private void setupRecorder() {
        try {
            if (audioJNI == null) audioJNI = new AudioJNI();

            String sampleRateString = null, bufferSizeString = null;
            if (Build.VERSION.SDK_INT >= 17) {
                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    sampleRateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
                    bufferSizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
                }
            }

            if (sampleRateString == null) {
                sampleRate = SAMPLE_RATE;
                bufferSize = BUFFER_SIZE;
            } else {
                sampleRate = Integer.parseInt(sampleRateString);
                bufferSize = Integer.parseInt(bufferSizeString);
            }

            tempPath = getCacheDir().getAbsolutePath() + "/temp.wav";
            destinationPath = ImageUtils.BASE_PATH + song.getIdentifier();

            audioJNI.setReverb(preferenceHelper.getInt(PreferenceKeys.IS_REVERB));
            audioJNI.setDelay(preferenceHelper.getInt(PreferenceKeys.IS_DELAY));
            audioJNI.setEcho(preferenceHelper.getInt(PreferenceKeys.IS_ECHO));

            hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupProgressListener() {
        service = Executors.newScheduledThreadPool(1);
        service.scheduleWithFixedDelay(getRunnable(), 0, microTimePerClock, TimeUnit.MICROSECONDS);
    }

    private Runnable getRunnable() {
        return () -> runOnUiThread(() -> {
            if (isPlaying && playerJNI != null) {
                mediaDuration =
                        playerJNI.GetTotalClocks() * microTimePerClock;
                mediaCurrentPosition =
                        playerJNI.GetCurrentClocks() * microTimePerClock;

                int percent = Math.round(((float) mediaCurrentPosition / (float) mediaDuration) * 100);
                timeProgressBar.setProgress(percent);

                long remainingTime = mediaDuration - mediaCurrentPosition;
                durationText.setText(dateHelper.getDuration(Math.round(remainingTime / 1000000)));

                if (remainingTime == 0) {
                    onControlOperation(OperationPopup.PlayerOperation.FINISH);
                }
            }
        });
    }

    @Subscribe
    public void updateViewWithState(ControlPanelPlay.PlayButtonState buttonState) {
        switch (buttonState) {
            case INIT:
                initStateFrame.setVisibility(View.VISIBLE);
                animationHelper.showWithFadeAnim(initFrameContent, false, 1500);
                playerFrame.setVisibility(View.GONE);
                break;
            case PAUSE:
                onClickPauseButton();

                statusText.setText(R.string.paused);
                animationHelper.showHeaderText(statusText, false);
                showOperationPopup();
                break;
            case PLAY:
                onClickPlayButton();

                if (initStateFrame.getVisibility() == View.VISIBLE)
                    animationHelper.hideWithFadeAnim(initStateFrame);
                if (playerFrame.getVisibility() != View.VISIBLE)
                    animationHelper.showWithFadeAnim(playerFrame, false, 1500);
                if (timeProgressBar.getVisibility() != View.VISIBLE)
                    animationHelper.showWithFadeAnim(timeProgressBar, false, 1500);
                if (headerSongNameSection.getVisibility() != View.VISIBLE)
                    animationHelper.showHeaderText(headerSongNameSection, false);
                statusText.setText(R.string.recording);
                animationHelper.showHeaderText(statusText, false);
                hideAndRestoreOperationPopup();
                break;
            case FINISHED:
                initStateFrame.setVisibility(View.GONE);
                playerFrame.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onClickPauseButton() {
        isPlaying = false;

        lyricsTimingHelper.pause();

        if (playerJNI != null) playerJNI.Stop();
        if (audioJNI != null) audioJNI.StopAudio();
    }

    private void onClickPlayButton() {
        isPlaying = true;

        if (isPlayed) {
            lyricsTimingHelper.resume();
            if (audioJNI != null) audioJNI.restart();
        } else {
            isPlayed = true;
            lyricsTimingHelper.start();
            if (audioJNI != null)
                audioJNI.StartAudio(sampleRate, bufferSize, tempPath, destinationPath);
        }

        if (playerJNI != null) playerJNI.Start();
    }

    private void hideAndRestoreOperationPopup() {
        if (lastAction != null) updateViewWithPanelOptions(lastAction);
        else controlsPopup.setVisibility(View.GONE);
    }

    private void showOperationPopup() {
        if (operationPopup == null) operationPopup = new OperationPopup(this);
        controlsPopup.setVisibility(View.VISIBLE);
        controlsPopup.removeAllViews();
        controlsPopup.addView(operationPopup.getView());
    }

    @Subscribe
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        switch (playerOperation) {
            case RESTART:
                if (audioJNI != null) audioJNI.FinalizeAudio();
                if (playerJNI != null) playerJNI.Stop();
                playerJNI = null;
                audioJNI = null;
                isPlayed = false;
                lyricsTimingHelper.remove();
                if (service != null) service.shutdown();
                service = null;
                timeProgressBar.setProgress(0);
                statusLayout.setVisibility(View.INVISIBLE);
                onPopupClose();
                initiatePlayer();
                break;
            case RESUME:
                controlPanelComponent.updatePlayButtonWithState(ControlPanelPlay.PlayButtonState.PLAY);
                updateViewWithState(ControlPanelPlay.PlayButtonState.PLAY);
                break;
            case FINISH:
                if (!isFinished)
                    uploadFile();
                break;
        }
    }

    private void uploadFile() {
        isFinished = true;
        startTime = Calendar.getInstance().getTimeInMillis();

        isPlaying = false;
        lyricsTimingHelper.remove();
        if (service != null) service.shutdown();

        uploadSong();
    }

    private void uploadSong() {
        showProgress();
        setProgressMessage(R.string.saving_song);

        if (playerJNI != null) playerJNI.FinalizePlayer();
        playerJNI = null;
        if (audioJNI != null) audioJNI.FinalizeAudio();
        audioJNI = null;

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SONG, song);
        bundle.putSerializable(Constants.PLAYER_LOG, playerLogs);
        navigationHelper.navigate(this, PreviewActivity.class, true, bundle);
    }

    @Subscribe
    public void updateViewWithPanelOptions(ControlPanelItemAction action) {
        if (controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.INIT
                || controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.PAUSE)
            return;

        lastAction = action;

        switch (action.getPanelOption()) {
            case EFFECT:
                if (effectsPopup == null) effectsPopup = new EffectsPopup(this);
                controlsPopup.setVisibility(View.VISIBLE);
                controlsPopup.removeAllViews();
                controlsPopup.addView(effectsPopup.getView());
                break;

            case MODE:
                if (modePopup == null) modePopup = new ModePopup(this);
                controlsPopup.setVisibility(View.VISIBLE);
                controlsPopup.removeAllViews();
                controlsPopup.addView(modePopup.getView());
                break;

            case PITCH:
                if (pitchPopup == null) pitchPopup = new PitchPopup(this);
                controlsPopup.setVisibility(View.VISIBLE);
                controlsPopup.removeAllViews();
                controlsPopup.addView(pitchPopup.getView());
                break;

            case TEMPO:
                if (tempoPopup == null) tempoPopup = new TempoPopup(this);
                controlsPopup.setVisibility(View.VISIBLE);
                controlsPopup.removeAllViews();
                controlsPopup.addView(tempoPopup.getView());
                break;
        }
    }

    @Override
    protected boolean withStatusBar() {
        return false;
    }

    @OnClick({R.id.close_ripple, R.id.header_song_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close_ripple:
                closeRipple.setOnRippleCompleteListener(rippleView -> cancelRecording());
                break;
            case R.id.header_song_name:
                break;
        }
    }

    @Subscribe
    public void onSelectionModeItem(ModePopupAction action) {
        if (action.getSelectionState() == ModePopupItem.SelectionState.SELECTED) {
            switch (action.getModeOptions()) {
                case MALE:
                    if (playerJNI != null) playerJNI.SetKeyControl(-7);
                    preferenceHelper.saveInt(PreferenceKeys.SONG_GENDER, 0);
                    break;
                case FEMALE:
                    if (playerJNI != null) playerJNI.SetKeyControl(7);
                    preferenceHelper.saveInt(PreferenceKeys.SONG_GENDER, 1);
                    break;
            }
        }
    }

    @Subscribe
    public void onSelectionEffectItem(EffectPopupAction action) {
        int value = action.getSelectionState() == EffectsPopupItem.SelectionState.SELECTED ? 1 : 0;
        switch (action.getEffectOptions()) {
            case DELAY:
                if (audioJNI != null) audioJNI.setDelay(value);
                preferenceHelper.saveInt(PreferenceKeys.IS_DELAY, value);
                break;
            case REVERV:
                if (audioJNI != null) audioJNI.setReverb(value);
                preferenceHelper.saveInt(PreferenceKeys.IS_REVERB, value);
                break;
            case ECHO:
                if (audioJNI != null) audioJNI.setEcho(value);
                preferenceHelper.saveInt(PreferenceKeys.IS_ECHO, value);
                break;
            default:
            case NONE:
                if (audioJNI != null) audioJNI.setEcho(0);
                preferenceHelper.saveInt(PreferenceKeys.IS_ECHO, 0);
                if (audioJNI != null) audioJNI.setDelay(0);
                preferenceHelper.saveInt(PreferenceKeys.IS_DELAY, 0);
                if (audioJNI != null) audioJNI.setReverb(0);
                preferenceHelper.saveInt(PreferenceKeys.IS_REVERB, 0);
                break;
        }
    }

    private void cancelRecording() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_stop_recording));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    if (service != null) service.shutdown();
                    closePlayer = true;
                    preferenceHelper.clearSavedSettings();

                    if (playerJNI != null) {
                        playerJNI.FinalizePlayer();
                        playerJNI = null;
                    }

                    if (audioJNI != null) {
                        audioJNI.FinalizeAudio();
                        audioJNI = null;
                    }

                    lyricsTimingHelper.stop();

                    navigationHelper.finish(this);
                }
        );

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onPopupClose() {
        controlPanelComponent.deselectAllPanels();
        controlsPopup.setVisibility(View.GONE);
        lastAction = null;
    }

    @Override
    public void onBackPressed() {
        cancelRecording();
    }

    @Subscribe
    public void onPlayerActivity(PlayerLog playerLog) {
        if (playerJNI != null) playerLog.setTick(playerJNI.GetCurrentClocks());
        playerLogs.add(playerLog);
    }
}
