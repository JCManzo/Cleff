package com.freneticlabs.cleff.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.freneticlabs.cleff.utils.CleffJSONSerializer;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by jcmanzo on 8/16/14.
 */
public class MusicLibrary {

    private ArrayList<Song> mSongs;
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
            Timber.d("Opening library");
        } catch (Exception e) {
            mSongs = new ArrayList<Song>();
            Log.e(TAG, "Error loading library: ", e);
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

    public Song getSong(long id) {
        for(Song song : mSongs) {
            if(song.getId() == id) {
                return song;
            }
        }
        return null;
    }

    public void updateRating(long songID, float rating) {

        for(Song song : mSongs) {
            if(song.getId() == songID) {
                int index = mSongs.indexOf(song);
                song.setSongRating(rating);

                mSongs.set(index, song);
            }
        }
    }

    public float getRating(long songId) {
        for(Song song : mSongs) {
            if(song.getId() == songId) {
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
            Log.i(TAG, "library saved");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving library: ", e);
            return false;
        }
    }
}
