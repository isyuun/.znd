package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.PlayerLog;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import kr.keumyoung.mukin.data.bus.EffectPopupAction;
import kr.keumyoung.mukin.data.bus.ModePopupAction;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.elements.ControlPanel;
import kr.keumyoung.mukin.elements.ControlPanelItem;
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
import kr.keumyoung.mukin.util.MicChecker;
import kr.keumyoung.mukin.util.PlayerJNI;
import kr.keumyoung.mukin.util.PlayerKyUnpackJNI;
import kr.keumyoung.mukin.util.PreferenceKeys;

/**
 * on 13/01/18.
 */

public class PlayerActivity extends _BaseActivity {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 480;
    private static final String TAG = PlayerActivity.class.getName();
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
    @BindView(R.id.bg_image)
    ImageView BgImage;
    @BindView(R.id.lyrics_view)
    TwoLineLyricsView lyricsView;
    @BindView(R.id.recording_circle)
    ImageView recording_circle;
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
    private PlayerKyUnpackJNI playerKyUnpackJNI;
    private MicChecker.MIC_CONNECTION_STATES preStatas;
    private boolean songFinishWithMic = false;  //녹음전까지 녹음가능 상태로 있었는가?
    //중간에 유선 마이크를 뺐는가?
    //AudioJNI를 중단했는가?

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

        //dsjung 절전모드 화면꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContext = this;

        if (operationPopup == null) operationPopup = new OperationPopup(this);

        MicChecker.getInstance().setOnStateChangedEvent(micCheckEventListener);
        preStatas = MicChecker.getInstance().getStates();

        imageUtils = new ImageUtils(this);
        playerKyUnpackJNI = new PlayerKyUnpackJNI();
        initiatePlayer();

        //dsjung 초기화면 뜨도록 수정함.
        //왜 시작시 초기화면 안뜨게 했는지..?
        controlPanelComponent.updatePlayButtonWithState(ControlPanelPlay.PlayButtonState.INIT);
        updateViewWithState(ControlPanelPlay.PlayButtonState.INIT);
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

    public Context mContext;

    private MicChecker.MicCheckEventListener micCheckEventListener = new MicChecker.MicCheckEventListener() {
        @Override
        public void onStateChangedEvent(MicChecker.MIC_CONNECTION_STATES states) {
            Log.d(TAG, "onStateChangedEvent " + states);
            //if (preStatas == MicChecker.MIC_CONNECTION_STATES.HEADSET
            //        && states != MicChecker.MIC_CONNECTION_STATES.HEADSET) {
            //    if (audioJNI != null && isPlayed) {
            //        songFinishWithMic = false;
            //        audioJNI.StopAudio();
            //        Log.d(TAG, "StopAudio");
            //        alertMessageBox(getResources().getString(R.string.error_headset_eject));
            //        enableRecordUI(false);
            //    }
            //}
            //
            //if (states == MicChecker.MIC_CONNECTION_STATES.HEADSET)
            //    enableMicEffectUI(true);
            //else
            //    enableMicEffectUI(false);
            //preStatas = states;
            enableRecordUI(false);
            enableMicEffectUI(true);
        }
    };

    //alert message box / only ok button
    private void alertMessageBox(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.confirm),
                (arg0, arg1) -> {
                }
        );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void enableMicEffectUI(boolean bEnable) {
        if (bEnable)
            controlPanelComponent.updateEffectButtonWithState(ControlPanelItem.SelectionState.NOT_SELECTED);
        else
            controlPanelComponent.updateEffectButtonWithState(ControlPanelItem.SelectionState.DISABLE);
    }

    private void enableRecordUI(boolean bEnable) {
        if (bEnable) {
            recording_circle.setColorFilter(getResources().getColor(R.color.recording_red), PorterDuff.Mode.SRC_ATOP);
            if (isPlaying) statusText.setText(R.string.recording);

            operationPopup.enableElementForPopup(OperationPopup.PlayerOperation.FINISH, true);

        } else {
            recording_circle.setColorFilter(getResources().getColor(R.color.recording_blue), PorterDuff.Mode.SRC_ATOP);
            if (isPlaying) statusText.setText(R.string.playing);

            operationPopup.enableElementForPopup(OperationPopup.PlayerOperation.FINISH, false);

            //장비 상태가 변경되었을 때 mic effect 등의 띄워져있는 팝업은 닫아준다.
            controlsPopup.setVisibility(View.GONE);
            controlsPopup.removeAllViews();
        }
    }

    //녹음 가능한 장치가 붙어있는가?
    private boolean isPossibleRecord() {
        return MicChecker.getInstance().getStates() == MicChecker.MIC_CONNECTION_STATES.HEADSET ? true : false;
    }

    // initiate player. take song from the intent. download bitmap, song midi and song json
    protected void initiatePlayer() {
        showProgress();
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.DATA)) {
            Bundle bundle = intent.getBundleExtra(Constants.DATA);
            if (bundle != null && bundle.containsKey(Constants.SONG))
                song = (Song) bundle.getSerializable(Constants.SONG);
        }

        if (song != null) {
            headerSongName.setText(song.getSongTitle());
            headerSongSubText.setText(song.getArtistName());

            //mediaManager.loadImageIntoView(song.getAlbumImage(), albumCoverImage);
            //mediaManager.loadImageIntoViewBlur(song.getAlbumImage(), BgImage);
            mediaManager.setPlayerImages(song.getAlbumImage(), albumCoverImage, BgImage);
            BgImage.setColorFilter(Color.parseColor("#5D5D5D"), PorterDuff.Mode.MULTIPLY);

            songName.setText(song.getSongTitle());
            songDesc.setText(song.getArtistName());
            songDuration.setText(dateHelper.getDuration(song.getDuration()));
            durationText.setText(dateHelper.getDuration(song.getDuration()));

         /*   String destinationPath = String.format("%s%s", ImageUtils.BASE_PATH, song.getSongFileName());
            String url = String.format("%s%s", Constants.FILE_API, song.getSongFile());
            System.out.println("destinationPath: " + destinationPath);
            System.out.println("url: " + url);*/

            showProgress();
            //  setProgressMessage(R.string.fetching_file);
			
        /*    downloadHelper.download(url, song.getSongFileName())
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
                });*/

            song.setSongFile(ImageUtils.BASE_PATH + song.getIdentifier() + ".KY3");

            String filename = String.format("%05d", Integer.parseInt(song.getIdentifier())) + ".KY3";
            File ky3Path = new File(ImageUtils.BASE_PATH + filename); // ky3 폴더
            System.out.println("#### SongDownload ky3Path : " + ky3Path + " filename " + filename);
            //	if(!ky3Path.isFile())
            {
                int temp = (int) Math.floor(Integer.parseInt(song.getIdentifier()) / (double) 100);
                String dir = String.format("%03d", temp);
                String downUrl = Constants.BASE_HOST_URL + "/ky3/" + dir + File.separator + filename;
                SongSearchApi.downloadFile(downUrl, ImageUtils.BASE_PATH, song.getIdentifier() + ".KY3", mHandler, Constants.API_KY3_DOWNLOAD);
            }

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
        //dsjung
        //setProgressMessage(R.string.fetching_lyrics);
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

    protected void prepareMediaPlayer() {
        try {
            if (playerJNI == null) playerJNI = new PlayerJNI();

            initializeError =
                    playerJNI.Initialize(preferenceHelper.getString(PreferenceKeys.LIBRARY_PATH));
            //    if (playerJNI != null)
            //        playerJNI.SetPortSelectionMethod(5); // Type K

            String midPath = ImageUtils.BASE_PATH + Integer.parseInt(song.getIdentifier()) + ".mid";
            setFileError = playerJNI.SetFile(midPath);
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
            int tempoValue = preferenceHelper.getInt(PreferenceKeys.TEMPO_VALUE);
            if (playerJNI != null) playerJNI.SetSpeedControl(tempoValue);
            if (tempoPopup == null) tempoPopup = new TempoPopup(this);
            tempoPopup.updatePresetValue(tempoValue);

            int pitchValue = preferenceHelper.getInt(PreferenceKeys.PITCH_VALUE);
            if (playerJNI != null) playerJNI.SetKeyControl(pitchValue);
            if (pitchPopup == null) pitchPopup = new PitchPopup(this);
            pitchPopup.updatePresetValue(pitchValue);

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

            if (playerJNI != null) playerJNI.SetKeyControl(0);
            if (playerJNI != null) playerJNI.SetSpeedControl(0);
            //setupRecorder();

            //dsjung 초기화면에 song total time 쓰레기 값으로 나오던 문제 수정
            songDuration.setText(dateHelper.getDuration(Math.round(playerJNI.GetTotalClocks() * microTimePerClock / 1000000)));
        } catch (Exception e) {
            e.printStackTrace();
            hideProgress();
            toastHelper.showError(R.string.file_not_found);
        }
    }

    @Deprecated
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

                    //dsjung 종료시 헤드셋이 아니거나 헤드셋을 해제한 경우가 있는경우에는 FINISH 안띄움
                    if (songFinishWithMic)
                        onControlOperation(OperationPopup.PlayerOperation.FINISH);
                    else
                        onControlOperation(OperationPopup.PlayerOperation.RESTART);
                }
            }

        });
    }

    //    boolean bDelay;
    @Subscribe
    public void updateViewWithState(ControlPanelPlay.PlayButtonState buttonState) {

        Log.d(TAG, "updateViewWithState = " + buttonState);

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
                if (playerFrame.getVisibility() != View.VISIBLE) {
                    animationHelper.showWithFadeAnim(playerFrame, false, 1500);
                }
                if (timeProgressBar.getVisibility() != View.VISIBLE)
                    animationHelper.showWithFadeAnim(timeProgressBar, false, 1500);
                if (headerSongNameSection.getVisibility() != View.VISIBLE)
                    animationHelper.showHeaderText(headerSongNameSection, false);
                if (songFinishWithMic)
                    statusText.setText(R.string.recording);
                else
                    statusText.setText(R.string.playing);
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

        /*dsjung
         *pause인데 stop을 시켜버려서 정지 -> 다시시작시 앱 오류나서 pause로 변경.
         *종료시 audioJNI 진행중이면 STOP후 Finalize 하도록 수정
         *if (audioJNI != null) audioJNI.StopAudio();
         */

        if (audioJNI != null) audioJNI.pause();
    }

    private void onClickPlayButton() {
        isPlaying = true;

        if (isPlayed) {
            lyricsTimingHelper.resume();
            //dsjung 마이크 가능 상태일때만 restart
            if (audioJNI != null && isPossibleRecord()) audioJNI.restart();
        } else {

            isPlayed = true;

            lyricsTimingHelper.start();

            //dsjung 헤드셋일 때만 녹음 모드 지원하도록
            //if (isPossibleRecord()) {
            //    if (audioJNI != null) {
            //        audioJNI.StartAudio(sampleRate, bufferSize, tempPath, destinationPath);
            //        Log.d(TAG, "StartAudio " + MicChecker.getInstance().getStates());
            //        enableRecordUI(true);
            //        enableMicEffectUI(true);
            //    }
            //    songFinishWithMic = true;
            //} else {
            //    toastHelper.showError(R.string.error_record_support);
            //    enableRecordUI(false);
            //    enableMicEffectUI(false);
            //    //alertRecordCancel();
            //    songFinishWithMic = false;
            //}
            enableRecordUI(false);
            enableMicEffectUI(true);
        }

        if (playerJNI != null) playerJNI.Start();
    }

    private void hideAndRestoreOperationPopup() {
        //dsjung
        //마이크 이펙트가 팝업된 상황 + 재생중 마이크를 뺀 상황에서는
        //라스트 엑션인 마이크 이펙트를 활성화 시키면 안됨.
        if (lastAction != null
                && lastAction.getPanelOption() == ControlPanel.ControlPanelOption.FAVORITE
                && isPossibleRecord() == false) {
            Log.d(TAG, "hideAndRestoreOperationPopup last action = FAVORITE and can't record now, lastaction will be null ");
            lastAction = null;
        }

        if (lastAction != null)
            updateViewWithPanelOptions(lastAction);
        else controlsPopup.setVisibility(View.GONE);
    }

    private void showOperationPopup() {
        //if (operationPopup == null) operationPopup = new OperationPopup(this);
        controlsPopup.setVisibility(View.VISIBLE);
        controlsPopup.removeAllViews();
        controlsPopup.addView(operationPopup.getView());
    }

    @Subscribe
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {

        Log.d(TAG, "onControlOperation = " + playerOperation);

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
                //statusLayout.setVisibility(View.INVISIBLE); dsjung INVISIBLE 하면 안됨
                onPopupClose();
                initiatePlayer();

                //dsjung 재시작시 버튼, 화면 초기화 하도록 추가함
                controlPanelComponent.updatePlayButtonWithState(ControlPanelPlay.PlayButtonState.INIT);
                updateViewWithState(ControlPanelPlay.PlayButtonState.INIT);
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
        //if (controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.INIT
        //        || controlPanelComponent.getPlayState() == ControlPanelPlay.PlayButtonState.PAUSE)
        //    return;

        boolean hide = (lastAction != null && lastAction.getPanelOption() == action.getPanelOption() && controlsPopup.getVisibility() == View.VISIBLE);
        lastAction = action;

        if (BuildConfig.DEBUG) Log.e(TAG, "updateViewWithPanelOptions(...)" + ":" + hide + ":" + controlsPopup.getVisibility());
        if (hide) {
            controlsPopup.setVisibility(View.GONE);
            return;
        }

        switch (action.getPanelOption()) {
            case FAVORITE:
                //마이크
                //toastHelper.showError(R.string.mic_not_working);
                //if (isPossibleRecord() == false) {
                //    toastHelper.showError(R.string.error_miceffect_support);
                //    break;
                //}
                //if (effectsPopup == null) effectsPopup = new EffectsPopup(this);
                //controlsPopup.removeAllViews();
                //controlsPopup.addView(effectsPopup.getView());
                //controlsPopup.setVisibility(visibility);
                /**
                 * 애창곡
                 */
                if (!isFavorites(song.getSongId())) {
                    addFavoriteSong(song);
                } else {
                    delFavoriteSong(song);
                }
                break;

            case MODE:
                if (modePopup == null) modePopup = new ModePopup(this);
                controlsPopup.removeAllViews();
                controlsPopup.addView(modePopup.getView());
                controlsPopup.setVisibility(View.VISIBLE);
                break;

            case PITCH:
                if (pitchPopup == null) pitchPopup = new PitchPopup(this);
                controlsPopup.removeAllViews();
                controlsPopup.addView(pitchPopup.getView());
                controlsPopup.setVisibility(View.VISIBLE);
                break;

            case TEMPO:
                if (tempoPopup == null) tempoPopup = new TempoPopup(this);
                controlsPopup.removeAllViews();
                controlsPopup.addView(tempoPopup.getView());
                controlsPopup.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavoriteSongs();
    }

    @Override
    protected void onFavoriteSongs() {
        super.onFavoriteSongs();
        enableFavoriteSong(isFavorites(song.getSongId()));
    }

    private void enableFavoriteSong(boolean bEnable) {
        //if (bEnable)
        //    controlPanelComponent.updateFavoriteButtonWithState(ControlPanelItem.SelectionState.NOT_SELECTED);
        //else
        //    controlPanelComponent.updateFavoriteButtonWithState(ControlPanelItem.SelectionState.DISABLE);
        if (bEnable) {
            controlPanelComponent.updateFavoriteButtonWithIcon(R.drawable.favorite_on_icon);
            controlPanelComponent.updateFavoriteButtonWithState(ControlPanelItem.SelectionState.SELECTED);
        } else {
            controlPanelComponent.updateFavoriteButtonWithIcon(R.drawable.favorite_off_icon);
            controlPanelComponent.updateFavoriteButtonWithState(ControlPanelItem.SelectionState.NOT_SELECTED);
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

        if (songFinishWithMic == true)
            alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_stop_recording));
        else
            alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_stop_playing));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    try {
                        if (service != null) service.shutdown();
                        closePlayer = true;
                        preferenceHelper.clearSavedSettings();

                        if (playerJNI != null) {
                            playerJNI.FinalizePlayer();
                            playerJNI = null;
                        }

                        if (audioJNI != null) {
                            //dsjung 종료시 플레이중에 Finalize 하면 오류
                            if (isPlayed) audioJNI.StopAudio();
                            audioJNI.FinalizeAudio();
                            audioJNI = null;
                        }

                        lyricsTimingHelper.stop();

                        navigationHelper.finish(this);
                        //navigationHelper.navigate(PlayerActivity.this, HomeActivity.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    //dsjung add
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.API_KY3_DOWNLOAD: {
                    System.out.println("#### API_KY3_DOWNLOAD!!!");
                    String ky3Path = ImageUtils.BASE_PATH + song.getIdentifier() + ".KY3";

                    playerKyUnpackJNI.Init(ky3Path, ImageUtils.BASE_PATH, Integer.parseInt(song.getIdentifier()));
                    prepareMediaPlayer();

                    String SokPath = ImageUtils.BASE_PATH + Integer.parseInt(song.getIdentifier()) + ".sok";
                    System.out.println("#### ky3Path : " + ky3Path + " || SokPath : " + SokPath);
                    lyricsTimingHelper.initiateWithLyrics(PlayerActivity.this, lyricsView, SokPath);
                    //		lyricsTimingHelper.parseSokLineArray(SokPath);
                    playerKyUnpackJNI.LyricSokParse(lyricsTimingHelper.GetLyricsLineString());

                    try {
                        File ky3File = new File(ky3Path);
                        ky3File.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        String midPath = ImageUtils.BASE_PATH + Integer.parseInt(song.getIdentifier()) + ".mid";
                        File midiFile = new File(midPath);
                        midiFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        File sokFile = new File(SokPath);
                        sokFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case Constants.API_ERROR_CODE:
                    toastHelper.showError(R.string.file_not_found);
                    preferenceHelper.clearSavedSettings();
                    navigationHelper.finish(PlayerActivity.this);
                    break;
            }
        }
    };
}
