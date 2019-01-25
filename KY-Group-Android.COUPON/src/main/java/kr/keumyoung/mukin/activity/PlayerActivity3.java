package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.bus.ControlPanelItemAction;
import kr.keumyoung.mukin.data.bus.ModePopupAction;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.OperationPopup;
import kr.keumyoung.mukin.util.Constants;

import static kr.keumyoung.mukin.elements.OperationPopup.PlayerOperation.NEXT;


public class PlayerActivity3 extends PlayerActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.play_hit_anchor)
    View playHitAnchor;

    @BindView(R.id.next)
    @Deprecated
    FrameLayout next;

    @BindView(R.id.reserve_anchor)
    View reserveAnchor;

    @BindView(R.id.reserve_text)
    TextView reserveText;

    @BindView(R.id.reserve_label)
    View reserveLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(v -> next());
        setReserves();
    }

    public void setReserves() {
        String text = getApp().getReserves().toString();
        setTextViewMarquee(reserveText, true);

        if (getApp().getReserves().size() > 1) forceRippleAnimation(reserveAnchor);

        if (getApp().getReserves().size() > 1) {
            reserveLabel.setVisibility(View.VISIBLE);
            animationHelper.showHeaderText(reserveText);
            reserveText.setText(text);
            //reserveAnchor.setClickable(true);
            //reserveAnchor.setOnClickListener(v -> openReserves());
            hideHeaders();
        } else {
            reserveLabel.setVisibility(View.INVISIBLE);
            reserveText.setVisibility(View.INVISIBLE);
            reserveText.setText("");
            //reserveAnchor.setClickable(false);
            //reserveAnchor.setOnClickListener(null);
            showHeaders();
        }
    }

    private void showHeaders() {
        if (headerSongNameSection.getVisibility() != View.VISIBLE) {
            animationHelper.showHeaderText(headerSongNameSection, false);
        }
    }

    private void hideHeaders() {
        //if (headerSongNameSection.getVisibility() == View.VISIBLE) {
        //    animationHelper.hideWithFadeAnim(headerSongNameSection, true);
        //}
        headerSongNameSection.setVisibility(View.INVISIBLE);
    }

    private void showReserves() {
        reserveAnchor.setVisibility(View.VISIBLE);
    }

    private void hideReserves() {
        reserveAnchor.setVisibility(View.INVISIBLE);
    }

    private void next() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        onControlOperation(NEXT);
    }

    @Override
    protected void initiatePlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        isPlaying = false;
        isPlayed = false;
        Song song = getApp().getReserve();
        if (song != null) {
            Intent intent = getIntent();
            if (intent.hasExtra(Constants.DATA)) {
                Bundle bundle = intent.getBundleExtra(Constants.DATA);
                if (bundle != null) bundle.putSerializable(Constants.SONG, song);
            }
        }
        super.initiatePlayer();
    }

    @Override
    protected void prepareMediaPlayer() {
        tempo(0);
        pitch(0);
        super.prepareMediaPlayer();
    }

    @Override
    public void tempo(int tempo) {
        super.tempo(tempo);
        if (playerJNI != null) playerJNI.SetSpeedControl(tempo * 2);
    }

    @Override
    public void pitch(int pitch) {
        super.pitch(pitch);
        if (playerJNI != null) playerJNI.SetKeyControl(pitch);
    }

    @Override
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + playerOperation);
        switch (playerOperation) {
            case RESUME:
                break;
            case FINISH:
            case RESTART:
            case NEXT:
                onPlayStop();
                if (playerOperation == NEXT) getApp().delReserve();
                break;
        }
        super.onControlOperation(playerOperation);
    }

    @Override
    protected void onPause() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onPause();
        if (isPlaying) getApp().delReserve();
    }

    @Override
    public void updatePlayerState(ControlPanelPlay.PlayButtonState buttonState) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + buttonState);
        super.updatePlayerState(buttonState);
        setReserves();
        switch (buttonState) {
            case INIT:
            case PAUSE:
            case FINISHED:
                showReserves();
                hideHeaders();
                playHitAnchor.setVisibility(View.VISIBLE);
                break;
            case PLAY:
            case RESUME:
                hideReserves();
                showHeaders();
                playHitAnchor.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onSelectionModeItem(ModePopupAction action) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + action.getModeOptions());
        super.onSelectionModeItem(action);
    }

    @Override
    public void updateViewWithPanelOptions(ControlPanelItemAction action) {
        super.updateViewWithPanelOptions(action);
        switch (action.getPanelOption()) {
            case FAVORITE:
                break;
            case TEMPO:
                tempoPopup.updatePresetValue(tempo());
                break;
            case PITCH:
                pitchPopup.updatePresetValue(pitch());
                break;
            case MODE:
                break;
        }
    }


    private int playtime() {
        return (int) (((playerJNI != null ? playerJNI.GetCurrentClocks() : 0) * microTimePerClock) / 1000000);
    }


    @Override
    protected void onPlayStart() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + playtime());
        super.onPlayStart();
    }

    @Override
    protected void onPlayStop() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + playtime());
        updateSongHits(song, playtime());
        super.onPlayStop();
    }

    @Override
    protected void release() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + playtime());
        if (isPlaying) updateSongHits(song, playtime());
        super.release();
    }
}
