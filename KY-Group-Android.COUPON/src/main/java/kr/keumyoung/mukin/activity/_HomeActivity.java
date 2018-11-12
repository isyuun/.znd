package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;

import kr.keumyoung.mukin.data.model.SongView;

public final class _HomeActivity extends HomeActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private Object busEventListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busEventListener = new Object() {
            @Subscribe
            public void post(SongView songView) {
                onSongSelected(songView);
            }
        };

    }

    @Override
    protected void onStart() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStart();
        //bus.register(this);
        bus.register(busEventListener);
    }

    @Override
    protected void onStop() {
        Log.e(__CLASSNAME__, getMethodName() + ":" + bus);
        super.onStop();
        //bus.unregister(this);
        bus.unregister(busEventListener);
    }

}
