package kr.keumyoung.mukin;

import android.util.Log;

import java.util.ArrayList;

import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;

public class MainApplication2 extends MainApplication {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    private Songs reserves = new Songs();

    private int index(String songid) {
        int ret = -1;
        for (Song song : reserves) {
            if (song.getSongId().equalsIgnoreCase(songid)) {
                ret = reserves.indexOf(song);
                break;
            }
        }
        return ret;
    }

    public boolean isReserves(String songid) {
        return !(index(songid) == -1);
    }

    private void delReserve(String songid) {
        int idx;
        while ((idx = index(songid)) > -1) {
            reserves.remove(idx);
        }
    }

    public void delReserve() {
        if (reserves.size() > 0) {
            reserves.remove(0);
        }
    }

    public Songs getReserves() {
        return reserves;
    }

    public Song getReserve() {
        if (reserves.size() > 0) {
            return (reserves.get(0));
        }
        return null;
    }

    public void onReserveSelected(Song song) {
        if (index(song.getSongId()) == -1) {
            reserves.add(song);
        } else {
            delReserve(song.getSongId());
        }
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + getReserves());
    }
}
