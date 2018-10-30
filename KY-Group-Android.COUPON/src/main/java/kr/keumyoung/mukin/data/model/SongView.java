package kr.keumyoung.mukin.data.model;

import android.view.View;

public class SongView {
    Song song;
    View view;

    public SongView(Song song, View view) {
        this.song = song;
        this.view = view;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
