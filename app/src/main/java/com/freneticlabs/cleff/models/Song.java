package com.freneticlabs.cleff.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jcmanzo on 8/7/14.
 */
public class Song implements Parcelable{
    private static final String JSON_ID         = "id";
    private static final String JSON_TITLE      = "title";
    private static final String JSON_ARTIST     = "artist";
    private static final String JSON_ALBUM      = "album";
    private static final String JSON_ALBUM_ID   = "album_id";
    private static final String JSON_GENRE      = "genre";
    private static final String JSON_RATING     = "rating";
    private static final String JSON_PLAYS      = "plays";
    private static final String JSON_YEAR       = "year";

    private Long mSongId;
    private String mAlbumID;

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private String mPath;
    private String mYear;
    private float mSongRating;



    public Song() {

    }
    public Song(Long songID, String songTitle, String songArtist,
                String album, String albumID, String genre) {
        mSongId = songID;
        mTitle = songTitle;
        mArtist = songArtist;
        mAlbum = album;
        mSongRating = 0;
        mAlbumID = albumID;
        mGenre = genre;
    }

    public Song(JSONObject jsonObject) throws  JSONException {
        mSongId = jsonObject.getLong(JSON_ID);
        mTitle = jsonObject.getString(JSON_TITLE);
        mArtist = jsonObject.getString(JSON_ARTIST);
        mAlbum = jsonObject.getString(JSON_ALBUM);
        mAlbumID = jsonObject.getString(JSON_ALBUM_ID);
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
        jsonObject.put(JSON_ID, String.valueOf(mSongId));
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

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public void setSongRating(float songRating) {
        mSongRating = songRating;
    }

    public Long getId() {
        return mSongId;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }
    public String getAlbumID() {
        return mAlbumID;
    }

    public void setID(Long songId) {
        mSongId = songId;
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

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setAlbumID(String albumID) {
        mAlbumID = albumID;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    @Override
    public String toString() {
     return getTitle();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mSongId);
        dest.writeString(this.mAlbumID);
        dest.writeString(this.mTitle);
        dest.writeString(this.mArtist);
        dest.writeString(this.mAlbum);
        dest.writeString(this.mGenre);
        dest.writeFloat(this.mSongRating);
    }

    private Song(Parcel in) {
        this.mSongId = in.readLong();
        this.mAlbumID = in.readString();
        this.mTitle = in.readString();
        this.mArtist = in.readString();
        this.mAlbum = in.readString();
        this.mGenre = in.readString();
        this.mSongRating = in.readFloat();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}