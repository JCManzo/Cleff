package com.freneticlabs.cleff.models;

import android.content.Context;

import com.freneticlabs.cleff.utils.CleffJSONSerializer;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by jcmanzo on 8/16/14.
 */
public class MusicLibrary {
    private static MusicLibrary sMusicLibrary;
    private Context mContext;

    private ArrayList<Song> mSongs;
    private ArrayList<Album> mAlbums;


    private CleffJSONSerializer mSongsJSONSerializer;
    private CleffJSONSerializer mAlbumsJSONSerializer;

    private static final String SONGS_FILE = "songs.json";
    private static final String ALBUMS_FILE = "albums.json";

    public MusicLibrary(Context context) {
        mContext = context;
        mSongs = new ArrayList<>();
        mAlbums = new ArrayList<>();
        mSongsJSONSerializer = new CleffJSONSerializer(mContext, SONGS_FILE);
        mAlbumsJSONSerializer = new CleffJSONSerializer(mContext, ALBUMS_FILE);
        try {
            mSongs = mSongsJSONSerializer.loadSongs();
            mAlbums = mAlbumsJSONSerializer.loadAlbums();
            Timber.d("Opening library");
        } catch (Exception e) {
            mSongs = new ArrayList<Song>();
            Timber.e("Error loading library: ", e);
        }
    }

    public void addSong(Song song) {
        mSongs.add(song);
    }
    public void addAlbum(Album album) {
        mAlbums.add(album);
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

    public ArrayList<Album> getAlbums() {
        return mAlbums;
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
            Timber.i(song.getTitle());
            Timber.i(String.valueOf(song.getSongRating()));
        }
    }

    public boolean saveLibrary() {
        try {
            mSongsJSONSerializer.saveSongs(mSongs);
            mAlbumsJSONSerializer.saveAlbums(mAlbums);
            Timber.i("library saved");
            return true;
        } catch (Exception e) {
            Timber.e("Error saving library: ", e);
            return false;
        }
    }
}
