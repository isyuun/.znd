package kr.keumyoung.mukin.data.model;

import org.joda.time.DateTime;

import javax.inject.Inject;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.helper.DateHelper;

/**
 *  on 13/01/18.
 */

public class Artist {
    private String artistId, artistName, artistImage;
    private int songCount;
    private DateTime createdOn, updatedOn;

    @Inject
    DateHelper dateHelper;

    public Artist() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(String artistImage) {
        this.artistImage = artistImage;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(String songCount) {
        this.songCount = Integer.parseInt(songCount);
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = dateHelper.parseDate(createdOn);
    }

    public DateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = dateHelper.parseDate(updatedOn);
    }
}
