package kr.keumyoung.mukin.data.model;

import android.view.View;

public class SongView {
    Song song;
    View view;
    int click = 0;

    public SongView(Song song, View view, int click) {
        this.song = song;
        this.view = view;
        this.click = click;
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

    public int getClick() {
        return click;
    }

    //public void setClick(int click) {
    //    this.click = click;
    //}
}
