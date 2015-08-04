package com.freneticlabs.cleff.models;

/**
 * Created by jcmanzo on 7/11/15.
 */


public class Artist extends Music{
    private int mArtistId;
    private int mNumOfSongs;

    private String mArtistName;

    public Artist() {

    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;

        if(o != null && o instanceof Artist ) {
            isEqual = (mArtistId) == ((Artist) o).mArtistId;
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + mArtistId;
        result = 31 * result + mNumOfSongs;
        return result;
    }

    @Override
    public int getId() {
        super.getId();
        return mArtistId;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        mArtistId = id;
    }

    public void setNumOfSongs(int numOfSongs) {
        mNumOfSongs = numOfSongs;
    }

    public void setArtistName(String artist) {
        mArtistName = artist;
    }



    @Override
    public String toString() {
        return getArtistName();
    }


    public String getArtistName() {
        return mArtistName;
    }

    public int getNumOfSongs() {
        return mNumOfSongs;
    }



}
