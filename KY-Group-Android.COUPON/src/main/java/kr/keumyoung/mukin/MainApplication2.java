package kr.keumyoung.mukin;

import android.util.Log;

import java.util.ArrayList;

import kr.keumyoung.mukin.data.model.Song;

public class MainApplication2 extends MainApplication {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    private ArrayList<Song> reserves = new ArrayList<>();

    protected boolean isReserves(Song song) {
        return (reserves.indexOf(song) > -1);
    }

    public boolean isReserves(String songid) {
        boolean ret = false;
        for (Song song : reserves) {
            if (song.getSongId() == songid) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    protected void addReserve(Song song) {
        song.setReserve(true);
        reserves.add(song);
    }

    protected void delReserve(Song song) {
        song.setReserve(false);
        reserves.remove(song);
    }

    public void delReserve() {
        if (reserves.size() > 0) {
            delReserve(reserves.get(reserves.size() - 1));
        }
    }

    public void clearReserve() {
        for (Song song : reserves) {
            song.setReserve(false);
        }
        reserves.clear();
    }

    public ArrayList<Song> getReserves() {
        return reserves;
    }

    public void onReserveSelected(Song song) {
        if (!isReserves(song)) {
            addReserve(song);
        } else {
            delReserve(song);
        }
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + getReserves());
    }
}
