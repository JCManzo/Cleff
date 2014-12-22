package com.freneticlabs.cleff.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

    private ArrayList<Song> mSongs;
    private HashMap<Long, String> mAlbumArt;

    private static MusicLibrary sMusicLibrary;

    private CleffJSONSerializer mCleffJSONSerializer;
    private Context mContext;


    private static final String TAG = MusicLibrary.class.getSimpleName();
    private HashMap<Integer, Drawable> mAlbumArtCache;

    private static final String FILENAME = "songs.json";

    public MusicLibrary(Context context) {
        mContext = context;
        mSongs = new ArrayList<Song>();
        mCleffJSONSerializer = new CleffJSONSerializer(mContext, FILENAME);

        try {
            mSongs = mCleffJSONSerializer.loadLibrary();
        } catch (Exception e) {
            mSongs = new ArrayList<Song>();
            Timber.e("Error loading library.");
        }
    }

    public void addSong(Song song) {
        mSongs.add(song);
    }

    public static MusicLibrary get(Context context) {
        if(sMusicLibrary == null) {
            sMusicLibrary = new MusicLibrary(context.getApplicationContext());
        }
        return sMusicLibrary;
    }

    public ArrayList<Song> getSongs() {
        return mSongs;
    }

    public HashMap<Long, String> getAlbumArt() {
        return mAlbumArt;
    }

    public Song getSong(long id) {
        for(Song song : mSongs) {
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
        Collections.sort(mSongs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {

                return song1.getTitle().compareTo(song2.getTitle());
            }
        });
    }

    public void updateRating(long songID, float rating) {

        for(Song song : mSongs) {
            if(song.getID() == songID) {
                int index = mSongs.indexOf(song);
                song.setSongRating(rating);

                mSongs.set(index, song);
            }
        }
    }

    public float getRating(long songId) {
        for(Song song : mSongs) {
            if(song.getID() == songId) {
                return song.getSongRating();
            }
        }
        return 0;
    }

    public void printLib() {
        for(Song song : mSongs) {
            Log.i(TAG, song.getTitle());
            Log.i(TAG, String.valueOf(song.getSongRating()));
        }
    }

    public boolean saveLibrary() {
        try {
            mCleffJSONSerializer.saveLibrary(mSongs);
            Log.d(TAG, "Library saved.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving library: ", e);
            return false;
        }
    }

    public void clearLibrary() {
        mSongs.clear();
    }
    public int getCount() {
        return  mSongs.size();
    }
}
