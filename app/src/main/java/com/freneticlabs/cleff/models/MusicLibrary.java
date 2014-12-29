package com.freneticlabs.cleff.models;

import android.content.Context;
import android.util.Log;

import com.freneticlabs.cleff.utils.CleffJSONSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by jcmanzo on 8/16/14.
 */
public class MusicLibrary {
    private ArrayList<Song> mSongsList;
    private ArrayList<Album> mAlbumsList;
    private HashMap<String, String> mGenresHashMap = new HashMap<String, String>();
    private HashMap<String, Integer> mGenresSongCountHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> mAlbumsCountMap = new HashMap<String, Integer>();

    private static MusicLibrary sMusicLibrary;

    private CleffJSONSerializer mCleffJSONSerializer;
    private Context mContext;


    private static final String TAG = MusicLibrary.class.getSimpleName();
    private static final String FILENAME = "songs.json";

    public MusicLibrary(Context context) {

        mContext = context;
        mSongsList = new ArrayList<>();
        mAlbumsList = new ArrayList<>();
        mCleffJSONSerializer = new CleffJSONSerializer(mContext, FILENAME);

        try {
            mSongsList = mCleffJSONSerializer.loadLibrary();
        } catch (Exception e) {
            mSongsList = new ArrayList<>();
            Timber.e("Error loading library.");
        }
    }

    public static MusicLibrary get(Context context) {
        if(sMusicLibrary == null) {
            sMusicLibrary = new MusicLibrary(context.getApplicationContext());
        }
        return sMusicLibrary;
    }

    public void addAlbum (Album album) {
        if(!mAlbumsList.contains(album)) {
            Timber.d("ALBUM ADDED");
            mAlbumsList.add(album);
        }
    }

    public ArrayList<Album> getAlbums() {
        return mAlbumsList;
    }
    public void addSong(Song song) {
        mSongsList.add(song);
    }

    public ArrayList<Song> getSongs() {
        return mSongsList;
    }

    public Song getSong(long id) {
        for(Song song : mSongsList) {
            if(song.getID() == id) {
                return song;
            }
        }
        return null;
    }

    /**
     * Sorts the library
     */
    public void sortLibrary() {
        //Sorting
        Collections.sort(mSongsList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {

                return song1.getTitle().compareTo(song2.getTitle());
            }
        });
    }

    public void updateRating(long songID, float rating) {

        for(Song song : mSongsList) {
            if(song.getID() == songID) {
                int index = mSongsList.indexOf(song);
                song.setSongRating(rating);

                mSongsList.set(index, song);
            }
        }
    }

    public float getRating(long songId) {
        for(Song song : mSongsList) {
            if(song.getID() == songId) {
                return song.getSongRating();
            }
        }
        return 0;
    }

    public void printLib() {
        for(Song song : mSongsList) {
            Log.i(TAG, song.getTitle());
            Log.i(TAG, String.valueOf(song.getSongRating()));
        }
    }

    public boolean saveLibrary() {
        try {
            mCleffJSONSerializer.saveLibrary(mSongsList);
            Log.d(TAG, "Library saved.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving library: ", e);
            return false;
        }
    }

    public void clearLibrary() {
        mSongsList.clear();
    }
    public int getCount() {
        return  mSongsList.size();
    }
}
