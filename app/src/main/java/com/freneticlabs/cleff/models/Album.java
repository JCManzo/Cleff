package com.freneticlabs.cleff.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jcmanzo on 12/28/14.
 */
public class Album {
    private static final String JSON_ID         = "id";
    private static final String JSON_ALBUM      = "album";
    private static final String JSON_ALBUM_ARTIST     = "artist";
    private static final String JSON_NUM_OF_SONGS     = "songs";

    private long mAlbumId;

    private int mNumOfSongs;

    private String mAlbum;
    private String mAlbumArtist;

    public Album(long albumId, int numOfSongs, String album, String albumArtist) {
        mAlbumId = albumId;
        mNumOfSongs = numOfSongs;
        mAlbum = album;
        mAlbumArtist = albumArtist;
    }

    public Album(JSONObject jsonObject) throws JSONException {
        mAlbumId = jsonObject.getLong(JSON_ID);
        mNumOfSongs = jsonObject.getInt(JSON_NUM_OF_SONGS);
        mAlbum = jsonObject.getString(JSON_ALBUM);
        mAlbumArtist = jsonObject.getString(JSON_ALBUM_ARTIST);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, mAlbumId);
        jsonObject.put(JSON_NUM_OF_SONGS, mNumOfSongs);
        jsonObject.put(JSON_ALBUM, mAlbum);
        jsonObject.put(JSON_ALBUM_ARTIST, mAlbumArtist);

        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;

        if(o != null && o instanceof Album ) {
            isEqual = (mAlbumId) == ((Album) o).mAlbumId;
        }
        return isEqual;
    }

    @Override
    public String toString() {
        return getAlbum();
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public String getAlbumArtist() {
        return mAlbumArtist;
    }


    public int getNumOfSongs() {
        return mNumOfSongs;
    }


    public String getAlbum() {
        return mAlbum;
    }

}
