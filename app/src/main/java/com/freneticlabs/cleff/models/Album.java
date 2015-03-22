package com.freneticlabs.cleff.models;

/**
 * Created by jcmanzo on 12/28/14.
 */
public class Album extends Music{
    private int mAlbumId;
    private int mNumOfSongs;

    private String mAlbum;
    private String mAlbumArtist;

    public Album() {

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
    public int getId() {
        super.getId();
        return mAlbumId;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        mAlbumId = id;
    }

    public void setNumOfSongs(int numOfSongs) {
        mNumOfSongs = numOfSongs;
    }

    public void setAlbumName(String album) {
        mAlbum = album;
    }

    public void setAlbumArtist(String albumArtist) {
        mAlbumArtist = albumArtist;
    }

    @Override
    public String toString() {
        return getAlbumName();
    }


    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    public int getNumOfSongs() {
        return mNumOfSongs;
    }

    public String getAlbumName() {
        return mAlbum;
    }

}
