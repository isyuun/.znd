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
import kr.keumyoung.mukin.elements.ControlPanelPlay;
import kr.keumyoung.mukin.elements.OperationPopup;
import kr.keumyoung.mukin.util.Constants;

import static kr.keumyoung.mukin.elements.OperationPopup.PlayerOperation.NEXT;

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
        if (getApp().getReserves().size() > 1) {
            next.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + "[" + (playerOperation == NEXT) + "]" + playerOperation);
        if (playerOperation == NEXT) getApp().delReserve();
        super.onControlOperation(playerOperation);
    }

    @Override
    protected void onPause() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());
        super.onPause();
        getApp().delReserve();
    }
}
