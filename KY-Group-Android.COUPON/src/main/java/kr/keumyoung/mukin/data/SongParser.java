package kr.keumyoung.mukin.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.util.Constants;

public class SongParser {

    public static ArrayList<String> FREE_SONG = new ArrayList<String>() {{
    }};

    public static Song convertToSongFromJson(JSONObject object) throws JSONException {
        Song song = new Song();
        JSONObject songObject = object.getJSONObject(Constants.SONG);
        JSONArray artistArray = object.getJSONArray(Constants.ARTISTS);
        JSONArray genreArray = object.getJSONArray(Constants.GENRES);

        song.setSongId(songObject.getString(Constants.SONG_ID));
        song.setIdentifier(songObject.getString(Constants.IDENTIFIER));
        song.setAlbumImage(songObject.getString(Constants.ALBUM_IMAGE));
        song.setSongTitle(songObject.getString(Constants.SONG_TITLE));
        song.setSongSubTitle(songObject.getString(Constants.SONG_SUB_TITLE));
        song.setHits(songObject.getString(Constants.HITS));
        song.setDuration(songObject.getString(Constants.DURATION));
        song.setSongFile(songObject.getString(Constants.SONG_FILE));
        song.setSongFileName(songObject.getString(Constants.FILE_NAME));
        song.setLyricsFile(songObject.getString(Constants.LYRICS_FILE));
        song.setLanguage(songObject.getString(Constants.LANGUAGE));
        song.setCreatedOn(songObject.getString(Constants.CREATED_ON));
        song.setUpdatedOn(songObject.getString(Constants.UPDATED_ON));

        song.setGender(songObject.getString(Constants.SONG_GENDER));

        ArrayList<String> artistNames = new ArrayList<>();
        for (int index = 0; index < artistArray.length(); index++) {
            JSONObject artistObject = artistArray.getJSONObject(index);
            artistNames.add(artistObject.getString(Constants.ARTIST_NAME));
        }
        song.setArtistName(artistNames);

        ArrayList<String> genreNames = new ArrayList<>();
        for (int index = 0; index < genreArray.length(); index++) {
            JSONObject genreObject = genreArray.getJSONObject(index);
            genreNames.add(genreObject.getString(Constants.GENRE_NAME));
        }
        song.setGenreName(genreNames);

        song.setFree(FREE_SONG.contains(song.getSongId()));

        return song;
    }
}
