package com.freneticlabs.cleff.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jcmanzo on 8/7/14.
 */
public class Song extends Music implements Parcelable {
    private int mSongId;
    private int mAlbumID;

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private String mPath;
    private String mYear;
    private float mSongRating;

    public Song() {
        // Required empty constructor
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


    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }
    public int getAlbumID() {
        return mAlbumID;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;

        if(o != null && o instanceof Song ) {
            isEqual = (mSongId) == ((Song) o).mSongId;
        }
        return isEqual;
    }

    @Override
    public int getId() {
        super.getId();
        return mSongId;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        mSongId = id;
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

    public void setAlbumID(int albumID) {
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
        dest.writeInt(this.mAlbumID);
        dest.writeString(this.mTitle);
        dest.writeString(this.mArtist);
        dest.writeString(this.mAlbum);
        dest.writeString(this.mGenre);
        dest.writeFloat(this.mSongRating);
    }

    private Song(Parcel in) {
        this.mSongId = in.readInt();
        this.mAlbumID = in.readInt();
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