package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.elements.OperationPopup;

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
    }

    @Override
    protected void initiatePlayer() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":mediaManager:" + mediaManager);
        super.initiatePlayer();
        if (getApp().getReserves().size() > 0) {
            next.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onControlOperation(OperationPopup.PlayerOperation playerOperation) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":playerOperation:" + playerOperation);
        super.onControlOperation(playerOperation);
    }
}
