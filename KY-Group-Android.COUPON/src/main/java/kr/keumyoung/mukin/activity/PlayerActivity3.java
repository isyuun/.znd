package kr.keumyoung.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.elements.OperationPopup;
import kr.keumyoung.mukin.util.Constants;

import static kr.keumyoung.mukin.elements.OperationPopup.PlayerOperation.RESTART;

public class PlayerActivity3 extends PlayerActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @BindView(R.id.next)
    FrameLayout next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        next.setOnClickListener(v -> next());
    }

    private void next() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        onControlOperation(OperationPopup.PlayerOperation.RESTART);
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
            getApp().delReserve();
        }
        super.initiatePlayer();
        if (getApp().getReserves().size() > 0) {
            next.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void prepareMediaPlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.prepareMediaPlayer();
    }

    @Override
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[" + (playerOperation == RESTART) + "]" + playerOperation);
        super.onControlOperation(playerOperation);
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
    }
}
