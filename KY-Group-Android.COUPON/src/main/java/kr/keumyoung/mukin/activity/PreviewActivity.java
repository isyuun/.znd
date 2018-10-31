package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.keumyoung.mukin.AppConstants;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.api.RestApi;
import kr.keumyoung.mukin.data.PlayerLog;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.request.UserSongRequest;
import kr.keumyoung.mukin.elements.TwoLineLyricsView;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.DateHelper;
import kr.keumyoung.mukin.helper.DownloadHelper;
import kr.keumyoung.mukin.helper.ImageUtils;
import kr.keumyoung.mukin.helper.LyricsTimingHelper;
import kr.keumyoung.mukin.helper.MediaManager;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.interfaces.SessionRefreshListener;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.PlayerJNI;
import kr.keumyoung.mukin.util.PreferenceKeys;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import kr.keumyoung.mukin.util.PlayerKyUnpackJNI;

import kr.keumyoung.mukin.activity.SongSearchApi;

/**
 * on 15/01/18.
 * 0.0(5) isyuun:녹음기능제거
 */
@Deprecated
public class PreviewActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static final int SAMPLE_RATE = 44100;
    @Inject
    NavigationHelper navigationHelper;
    @Inject
    AnimationHelper animationHelper;
    @Inject
    MediaManager mediaManager;
    @Inject
    DateHelper dateHelper;
    @Inject
    ToastHelper toastHelper;
    @Inject
    RestApi restApi;
    @Inject
    DownloadHelper downloadHelper;
    @Inject
    LyricsTimingHelper lyricsTimingHelper;

    ArrayList<PlayerLog> playerLogs = new ArrayList<>();
    @BindView(R.id.header_text)
    TextView headerText;
    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.album_image)
    RoundedImageView albumImage;
    @BindView(R.id.album_info_panel)
    RelativeLayout albumInfoPanel;
    @BindView(R.id.play_duration)
    TextView playDuration;
    @BindView(R.id.seekbar_panel)
    RelativeLayout seekbarPanel;
    @BindView(R.id.song_lyrics)
    TextView songLyrics;
    @BindView(R.id.cancel_anchor)
    LinearLayout cancelAnchor;
    @BindView(R.id.play_anchor)
    ImageView playAnchor;
    @BindView(R.id.re_record_anchor)
    LinearLayout reRecordAnchor;
    @BindView(R.id.save_button)
    TextView saveButton;
    @BindView(R.id.bottom_panel)
    RelativeLayout bottomPanel;
    @BindView(R.id.close_button)
    ImageView closeButton;
    @BindView(R.id.close_button_ripple)
    RippleView closeButtonRipple;
    @BindView(R.id.cancel_anchor_ripple)
    RippleView cancelAnchorRipple;
    @BindView(R.id.play_anchor_ripple)
    RippleView playAnchorRipple;
    @BindView(R.id.re_record_anchor_ripple)
    RippleView reRecordAnchorRipple;
    @BindView(R.id.save_button_ripple)
    RippleView saveButtonRipple;
    @BindView(R.id.album_info_section)
    LinearLayout albumInfoSection;
    @BindView(R.id.song_name)
    TextView songName;
    @BindView(R.id.song_subtitle)
    TextView songSubtitle;
    @BindView(R.id.song_progress)
    SeekBar songProgress;
    @BindView(R.id.root_bg_image)
    ImageView rootBgImage;
    MediaPlayer mediaPlayer;
    Song song;
    boolean isPlaying = false;
    String fileName;
    PlayerJNI playerJNI;
	PlayerKyUnpackJNI playerKyUnpackJNI;
	 
    SessionRefreshListener sessionRefreshListener = new SessionRefreshListener() {
        @Override
        public void onSessionRefresh() {
            navigationHelper.finish(PreviewActivity.this);
        }
    };
    boolean isPlaying_ = false;
    ScheduledExecutorService service;
    @BindView(R.id.lyrics_view)
    TwoLineLyricsView lyricsView;
    boolean isPlayed = false;
    File audioFile = null;
    private int effectIndex = 0;
    private int pauseTick = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_preview, null, false);
        inflateContainerView(view);

        ButterKnife.bind(this, view);

        //dsjung 절전모드 화면꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        playerJNI = new PlayerJNI();
		playerKyUnpackJNI = new PlayerKyUnpackJNI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        songProgress.setEnabled(false);

        animationHelper.showWithFadeAnim(albumImage);
        animationHelper.showHeaderText(headerText, false);
        animationHelper.showHeaderText(albumInfoSection, false);
        animationHelper.showWithFadeAnim(lyricsView);

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.DATA)) {
            Bundle bundle = intent.getBundleExtra(Constants.DATA);
            if (bundle.containsKey(Constants.SONG))
                song = (Song) bundle.getSerializable(Constants.SONG);

            if (bundle.containsKey(Constants.PLAYER_LOG))
                playerLogs = (ArrayList<PlayerLog>) bundle.getSerializable(Constants.PLAYER_LOG);
        }

        if (song != null) {
            mediaManager.loadImageIntoView(song.getAlbumImage(), albumImage);
            songName.setText(song.getSongTitle());
            songSubtitle.setText(song.getSongSubTitle());
//            playDuration.setText(dateHelper.getDuration(song.getDuration()));

            downloadHelper.downloadBitmap(song.getAlbumImage())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        rootBgImage.setVisibility(View.INVISIBLE);
                        rootBgImage.setImageBitmap(bitmap);
                        animationHelper.showWithFadeAnim(rootBgImage);
                    }, Throwable::printStackTrace, () -> System.out.println("blurring completed"));

		/*
            File file = new File(ImageUtils.getBaseFolder(), song.getIdentifier() + ".json");
            lyricsTimingHelper.initiateWithLyricsView(this, lyricsView, file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setProgressMessage, throwable -> {
                        throwable.printStackTrace();
                        hideProgress();
                    }, this::initiatePlayer);
		*/

			String filename = String.format("%05d", Integer.parseInt(song.getIdentifier()))+".KY3";
			File ky3Path = new File(ImageUtils.BASE_PATH + filename);
			System.out.println("#### SongDownload ky3Path : " + ky3Path + " filename " + filename);
		//	if(!ky3Path.isFile())
			{
				int temp = (int)Math.floor(Integer.parseInt(song.getIdentifier()) /(double)100);
				String dir = String.format("%03d", temp);
				String downUrl = Constants.BASE_HOST_URL+"/ky3/"+dir+File.separator+filename;
				SongSearchApi.downloadFile(downUrl, ImageUtils.BASE_PATH, song.getIdentifier() +".KY3", mHandler, Constants.API_KY3_DOWNLOAD);
			}
        }
    }

    private void initiatePlayer() {
        showProgress();
        new Handler().postDelayed(() -> {
            isPlayed = false;
            if (playerJNI == null) playerJNI = new PlayerJNI();

            playerJNI.Initialize(preferenceHelper.getString(PreferenceKeys.LIBRARY_PATH));
         //   String filePath = ImageUtils.BASE_PATH + song.getIdentifier() + ".mid";
			String midPath = ImageUtils.BASE_PATH + Integer.parseInt(song.getIdentifier()) +".mid";
            if (playerJNI != null) playerJNI.SetPortSelectionMethod(5); // Type K
			if (playerJNI != null) playerJNI.SetSpeedControl(0);
            if (playerJNI != null) 
			{
		//		System.out.println("#### initiatePlayer midPath : " + midPath);
				playerJNI.SetFile(midPath);
				File midiFile = new File(midPath);
				midiFile.delete();
            }
			if (playerJNI != null) playerJNI.Seek(0);

            try {
                String pathAudio = ImageUtils.BASE_PATH + song.getIdentifier() + ".wav";
                audioFile = new File(pathAudio);
                Uri uri = Uri.fromFile(audioFile);
				
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(PreviewActivity.this, uri);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(PreviewActivity.this::onPreparedMediaPlayer);
                hideProgress();
            } catch (Exception e) {
                e.printStackTrace();
                hideProgress();
            }
        }, 500);

    }

    private void setupProgressListener() {
        if (service == null) service = Executors.newScheduledThreadPool(1);
        service.scheduleWithFixedDelay(getRunnable(), 0, 100, TimeUnit.MILLISECONDS);
    }

    private Runnable getRunnable() {
        return () -> runOnUiThread(() -> {
        //    if (playerJNI != null) System.out.println("TICK: " + playerJNI.GetCurrentClocks());

            if (isPlaying && playerJNI != null && mediaPlayer != null) {
                songProgress.setProgress(mediaPlayer.getCurrentPosition());
                applyEffect();

                if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
                    onCompletion(mediaPlayer);
                }
            }
        });
    }

    private void onPreparedMediaPlayer(MediaPlayer mediaPlayer) {
        setupProgressListener();
        songProgress.setMax(mediaPlayer.getDuration());
        songProgress.setProgress(0);
        playDuration.setText(dateHelper.getDuration(mediaPlayer.getDuration() / 1000));
    }

    private void applyEffect() {
        if (effectIndex >= playerLogs.size()) return;
        if (playerJNI != null) {
            int currentTick = playerJNI.GetCurrentClocks();
            for (int index = effectIndex; index < playerLogs.size(); index++) {
                PlayerLog currentLog = playerLogs.get(index);
                if (currentTick < currentLog.getTick()) {
                    break;
                } else if (currentLog.getTick() == currentTick) {
                    switch (currentLog.getLogType()) {
                        case PITCH:
                            if (playerJNI != null) playerJNI.SetKeyControl(currentLog.getValue());
                            break;
                        case TEMPO:
                            if (playerJNI != null) playerJNI.SetSpeedControl(currentLog.getValue());
                            break;
                    }
                    effectIndex = index + 1;
                }
            }
        }
    }

    //alert message box / only ok button
    private void alertMessageBox(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.confirm),
                (arg0, arg1) -> {
                }
        );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void stopSong() {
        try {
            if (playerJNI != null) {
                playerJNI.FinalizePlayer();
                playerJNI = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            isPlaying = false;
            isPlaying_ = false;
            isPlayed = false;
            updatePlayIcon();
            lyricsTimingHelper.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (playerJNI != null) {
//            playerJNI.FinalizeAudio();
//            playerJNI = null;
//        }
//
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//
//        lyricsTimingHelper.stop();
    }

    @OnClick({R.id.cancel_anchor_ripple, R.id.play_anchor_ripple, R.id.re_record_anchor_ripple, R.id.save_button_ripple, R.id.close_button_ripple})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_anchor_ripple:
                cancelAnchorRipple.setOnRippleCompleteListener(rippleView -> cancelRecording(getResources().getString(R.string.do_you_want_to_discard_recording)));
            case R.id.close_button_ripple:
                cancelRecording(getResources().getString(R.string.do_you_want_to_discard_recording));
                break;
            case R.id.play_anchor_ripple:
                playAnchorRipple.setOnRippleCompleteListener(rippleView -> {
                    if (!isPlaying_) playSong();
                    else pauseSong();
                    updatePlayIcon();
                });
                break;
            case R.id.re_record_anchor_ripple:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SONG, song);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (playerJNI != null) {
                    playerJNI.Stop();
                    playerJNI.FinalizePlayer();
                    playerJNI = null;
                }

                reRecordAnchorRipple.setOnRippleCompleteListener(rippleView -> navigationHelper.navigateWithReverseAnim(this, PlayerActivity.class, true, bundle));
                break;
            case R.id.save_button_ripple:
                alertMessageBox(getResources().getString(R.string.function_not_support));
                //saveButtonRipple.setOnRippleCompleteListener(rippleView -> saveUploadedSong());
                break;
        }
    }

    private void pauseSong() {
        try {
            isPlaying_ = false;
            isPlaying = false;
            if (playerJNI != null) {
                pauseTick = playerJNI.GetCurrentClocks();
                playerJNI.Stop();
            }

            new Handler().postDelayed(() -> {
                if (mediaPlayer != null) mediaPlayer.pause();
            }, 100);

            lyricsTimingHelper.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSong() {
        try {
            if (isPlayed) {
                lyricsTimingHelper.resume();
            } else {
                lyricsTimingHelper.start();
                isPlayed = true;
            }
            isPlaying_ = true;
            isPlaying = true;
            if (playerJNI != null) {
//                playerJNI.Seek(pauseTick);
                playerJNI.Start();
            }
            if (mediaPlayer != null) mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelRecording(String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> navigationHelper.finish(this)
        );

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveUploadedSong() {
        lyricsTimingHelper.remove();
        if (mediaPlayer != null) mediaPlayer.stop();
        if (playerJNI != null) playerJNI.Stop();

        if (audioFile != null) {
            showProgress();
            setProgressMessage(R.string.sharing_song);
            RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), audioFile);
            MultipartBody.Part audioPart = MultipartBody.Part.createFormData("file", audioFile.getName(), reqBody);
            String playerLogJson = "[]";
            try {
                playerLogJson = getPlayerLogJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            restApi.fileSave(
                    preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN),
                    song.getIdentifier(),
                    playerLogJson,
                    audioPart
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString = response.body().string();
                        System.out.println("Response String" + responseString);

                        JSONObject jsonObject = new JSONObject(responseString);

                        JSONArray responseArray = jsonObject.getJSONArray(Constants.RESOURCE);

                        JSONObject responseObject = responseArray.getJSONObject(0);

                        String songFileName = responseObject.getString(Constants.PATH);

                        preferenceHelper.saveInt(PreferenceKeys.IS_REVERB, 0);
                        preferenceHelper.saveInt(PreferenceKeys.IS_ECHO, 0);
                        preferenceHelper.saveInt(PreferenceKeys.IS_DELAY, 0);
                        preferenceHelper.saveInt(PreferenceKeys.TEMPO_VALUE, 0);
                        preferenceHelper.saveInt(PreferenceKeys.PITCH_VALUE, 0);
                        preferenceHelper.saveInt(PreferenceKeys.SONG_GENDER, -1);

                        saveShareInfoIntoDF(String.format("%s%s", AppConstants.API_BASE_URL, songFileName));
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgress();
                        toastHelper.showError(R.string.song_file_could_not_shared);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    toastHelper.showError(R.string.song_file_could_not_shared);
                }
            });
        }
    }

    private String getPlayerLogJson() throws JSONException {
        if (!playerLogs.isEmpty()) {

            JSONArray array = new JSONArray();
            for (PlayerLog playerLog : playerLogs) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("logType", playerLog.getLogType() == PlayerLog.LogType.PITCH ? "pitch" : "tempo");
                jsonObject.put("value", playerLog.getValue());
                jsonObject.put("tick", playerLog.getTick());
                array.put(jsonObject);
            }

            return array.toString();
        }
        return "[]";
    }

    private void saveShareInfoIntoDF(String shareLink) {
        RequestModel<UserSongRequest> model = new RequestModel<>(new UserSongRequest(song.getSongId(), preferenceHelper.getString(PreferenceKeys.USER_ID), fileName, shareLink));
        restApi.saveUploadedSong(preferenceHelper.getString(PreferenceKeys.SESSION_TOKEN), model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    ResponseBody errorBody = response.errorBody();

                    if (responseBody != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.SONG, song);
                        bundle.putString(Constants.SHARE_LINK, shareLink);

                        navigationHelper.navigate(PreviewActivity.this, ShareActivity.class, true, bundle);
                        hideProgress();
                    } else if (errorBody != null) {
                        String errorString = errorBody.string();
                        JSONObject errorObject = new JSONObject(errorString);
                        if (handleDFError(errorObject, sessionRefreshListener)) {
                            // session error has been handled using base activity code
                        } else {
                            // TODO: 31/01/18 handle more error related to share
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgress();
            }
        });
    }

    private void updatePlayIcon() {
        runOnUiThread(() -> {
            if (isPlaying_)
                animationHelper.updateImageResource(playAnchor, R.drawable.pause_02_icon);
            else animationHelper.updateImageResource(playAnchor, R.drawable.play_02_icon);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerJNI != null) playerJNI.FinalizePlayer();
        playerJNI = null;

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlaying_ = false;
        isPlaying = false;

        if (playerJNI != null) {
            playerJNI.Stop();
            playerJNI.FinalizePlayer();
            playerJNI = null;
        }
        pauseTick = 0;

        this.mediaPlayer.release();
        this.mediaPlayer = null;

        lyricsTimingHelper.remove();

        updatePlayIcon();

        service.shutdownNow();
        service = null;

        initiatePlayer();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (fromUser) {
//            int songProgress = Math.round((float) mediaDuration * (progress / 100));
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.seekTo(songProgress);
//        }
    }

    @Override
    public void onBackPressed() {
        cancelRecording(getResources().getString(R.string.do_you_want_to_discard_recording));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public PlayerJNI getPlayerJNI() {
        return playerJNI;
    }

	public Handler mHandler = new Handler() {
		@Override
		 public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case Constants.API_KY3_DOWNLOAD:
					{
						System.out.println("#### API_KY3_DOWNLOAD!!!");
						String ky3Path = ImageUtils.BASE_PATH + song.getIdentifier() +".KY3";
						
						playerKyUnpackJNI.Init(ky3Path, ImageUtils.BASE_PATH, Integer.parseInt(song.getIdentifier()));
						initiatePlayer();

						String SokPath = ImageUtils.BASE_PATH + Integer.parseInt(song.getIdentifier()) +".sok";
						System.out.println("#### ky3Path : " + ky3Path + " || SokPath : " + SokPath);
						lyricsTimingHelper.initiateWithLyrics(PreviewActivity.this, lyricsView, SokPath);
				//		lyricsTimingHelper.parseSokLineArray(SokPath);
						playerKyUnpackJNI.LyricSokParse(lyricsTimingHelper.GetLyricsLineString());

						File ky3File = new File(ky3Path);
						ky3File.delete();

						File sokFile = new File(SokPath);
						sokFile.delete();
					}
					break;
				case Constants.API_ERROR_CODE:
					toastHelper.showError(R.string.file_not_found);
                    preferenceHelper.clearSavedSettings();
                    navigationHelper.finish(PreviewActivity.this);
					break;
			}
		 }
	};
}
