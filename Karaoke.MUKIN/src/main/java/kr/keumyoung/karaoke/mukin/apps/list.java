package kr.keumyoung.karaoke.mukin.apps;

import android.os.Bundle;
import androidx.annotation.Nullable;

import kr.keumyoung.karaoke.mukin.BuildConfig;
import kr.keumyoung.karaoke.mukin.R;
import kr.keumyoung.karaoke.mukin.adpts.data.song;

public class list extends __base implements kr.keumyoung.karaoke.mukin.frags.song.OnListFragmentInteractionListener {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentViewID(R.layout.activity_list);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onListFragmentInteraction(song.songItem item) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
