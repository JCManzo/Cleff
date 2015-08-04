package com.freneticlabs.cleff.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jcmanzo on 8/7/14.
 */
public class Song extends Music implements Parcelable {

    //================================================================================
    // Properties
    //================================================================================

    private boolean mFavorited = false;

    private int mAlbumId;
    private int mSongId;

    private String mAlbum;
    private String mArtist;
    private String mGenre;
    private String mPath;
    private String mTitle;

    public Song() {
        // Required empty constructor
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
    public int hashCode() {
        int result = 17;
        result = 31 * result + mSongId;
        result = 31 * result + mAlbumId;
        return result;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    //================================================================================
    // Accessors
    //================================================================================

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

    public int getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    public boolean getFavorited() {
        return mFavorited;
    }

    public void setFavorited(boolean favorited) {
        mFavorited = favorited;
    }

    public int getSongId() {
        return mSongId;
    }

    public void setSongId(int songId) {
        mSongId = songId;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    protected Song(Parcel in) {
        mFavorited = in.readByte() != 0x00;
        mAlbumId = in.readInt();
        mSongId = in.readInt();
        mAlbum = in.readString();
        mArtist = in.readString();
        mGenre = in.readString();
        mPath = in.readString();
        mTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mFavorited ? 0x01 : 0x00));
        dest.writeInt(mAlbumId);
        dest.writeInt(mSongId);
        dest.writeString(mAlbum);
        dest.writeString(mArtist);
        dest.writeString(mGenre);
        dest.writeString(mPath);
        dest.writeString(mTitle);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}