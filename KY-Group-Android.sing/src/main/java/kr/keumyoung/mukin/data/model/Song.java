package kr.keumyoung.mukin.data.model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  on 12/01/18.
 */

public class Song extends SongModel implements Serializable {
    private String songId, identifier, artistId, songTitle, songSubTitle, albumImage, artistName, genreName, songFile, songFileName, lyricsFile, language, gender;
    private int hits, duration;
    private DateTime createdOn, updatedOn;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongSubTitle() {
        return songSubTitle;
    }

    public void setSongSubTitle(String songSubTitle) {
        this.songSubTitle = songSubTitle;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtistName(ArrayList<String> artistNames) {
        artistName = "";
        for (String name : artistNames) artistName += name + ", ";
        artistName = artistName.length() > 2 ? artistName.substring(0, artistName.length() - 2) : artistName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(ArrayList<String> genreNames) {
        genreName = "";
        for (String name : genreNames) genreName += name + ", ";
        genreName = genreName.length() > 2 ? genreName.substring(0, genreName.length() - 2) : genreName;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = Integer.parseInt(hits);
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = Integer.parseInt(duration);
    }

    public String getSongFile() {
        return songFile;
    }

    public void setSongFile(String songFile) {
        this.songFile = songFile;
    }

    public String getSongFileName() {
        return songFileName;
    }

    public void setSongFileName(String songFileName) {
        this.songFileName = songFileName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLyricsFile() {
        return lyricsFile;
    }

    public void setLyricsFile(String lyricsFile) {
        this.lyricsFile = lyricsFile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
