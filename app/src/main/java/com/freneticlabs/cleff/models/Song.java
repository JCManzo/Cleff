package com.freneticlabs.cleff.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jcmanzo on 8/7/14.
 */
public class Song {
    private static final String JSON_ID         = "id";
    private static final String JSON_TITLE      = "title";
    private static final String JSON_ARTIST     = "artist";
    private static final String JSON_ALBUM      = "album";
    private static final String JSON_ALBUM_ID   = "album_id";
    private static final String JSON_GENRE      = "genre";
    private static final String JSON_RATING     = "rating";
    private static final String JSON_PLAYS      = "plays";
    private static final String JSON_YEAR       = "year";

    private long mID;
    private long mAlbumID;

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mGenre;


    private float mSongRating;

    public Song(long songID, String songTitle, String songArtist,
                String album, String genre) {
        mID = songID;
        mTitle = songTitle;
        mArtist = songArtist;
        mAlbum = album;
        mSongRating = 0;
        mGenre = genre;
    }

    public Song(JSONObject jsonObject) throws  JSONException {
        mID = jsonObject.getLong(JSON_ID);
        mTitle = jsonObject.getString(JSON_TITLE);
        mArtist = jsonObject.getString(JSON_ARTIST);
        mAlbum = jsonObject.getString(JSON_ALBUM);
        mAlbumID = jsonObject.getLong(JSON_ALBUM_ID);
        mSongRating = Float.parseFloat(jsonObject.getString(JSON_RATING));
        mGenre = jsonObject.getString(JSON_GENRE);
    }

    /**
     * Creates a JSON representation of a Song object
     * @return JSONG Song object
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, String.valueOf(mID));
        jsonObject.put(JSON_TITLE, mTitle);
        jsonObject.put(JSON_ARTIST, mArtist);
        jsonObject.put(JSON_ALBUM, mAlbum);
        jsonObject.put(JSON_ALBUM_ID, String.valueOf(mAlbumID));
        jsonObject.put(JSON_RATING, String.valueOf(mSongRating));
        jsonObject.put(JSON_GENRE, mGenre);

        return jsonObject;
    }



    public float getSongRating() {
        return mSongRating;
    }

    public void setSongRating(float songRating) {
        mSongRating = songRating;
    }

    public long getID() {
        return mID;
    }

    public String getAlbum() {
        return mAlbum;
    }
    public long getAlbumID() {
        return mAlbumID;
    }

    public void setID(long ID) {
        mID = ID;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return this.mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    @Override
    public String toString() {
     return getTitle();
    }
}