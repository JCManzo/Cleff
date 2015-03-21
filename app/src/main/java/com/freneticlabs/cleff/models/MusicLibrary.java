package com.freneticlabs.cleff.models;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freneticlabs.cleff.utils.PojoMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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



    private static final String SONGS_FILE = "songs.json";

    private static final String ALBUMS_FILE = "albums.json";

    public MusicLibrary(Context context) {
        mContext = context;
        mSongs = new ArrayList<>();
        mAlbums = new ArrayList<>();
        try {
            loadAlbumsFromJSONFile();
            loadSongsFromJSONFile();
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
        }
    }

    public void printLib(ArrayList<Song> songs)  {
        for(Song song : songs) {
            Timber.i(song.getTitle());
        }
    }

    public boolean saveLibrary() {
        try {
           // mSongsJSONSerializer.saveSongs(mSongs);
            saveAlbumsToJSONFile();
            saveSongsToJSONFile();
            Timber.i("library saved");

            return true;
        } catch (Exception e) {
            Timber.e("Error saving library: ", e);
            return false;
        }
    }

    /**
     * Reads JSON file containing songs and creates an ArrayList
     * of Song POJO's
     *
     * @return ArrayList of songs
     */
    public void loadSongsFromJSONFile() throws IOException {
        try {
            //Open and read the file
            InputStream inputStream = mContext.openFileInput(SONGS_FILE);

            mSongs = PojoMapper.fromJson(inputStream, new TypeReference<ArrayList<Song>>() { });
            Timber.d("Songs loaded from file " + SONGS_FILE);
        } catch (FileNotFoundException ex) {
            // ignore. happens when app is first initialized
        }
    }

    /**
     * Reads JSON file containing songs and creates an ArrayList
     * of Album POJO's
     *
     * @return ArrayList of songs
     */
    public void loadAlbumsFromJSONFile() throws IOException {
        try {
            //Open and read the file
            InputStream inputStream = mContext.openFileInput(ALBUMS_FILE);

            mAlbums = PojoMapper.fromJson(inputStream, new TypeReference<ArrayList<Album>>() { });
            Timber.d("Albums loaded from file " + ALBUMS_FILE);
        } catch (FileNotFoundException ex) {
            // ignore. happens when app is first initialized
        }
    }

    /**
     * Saves the current songs to a JSON File
     */
    public void saveSongsToJSONFile() {
        Writer writer = null;
        OutputStream outputStream;

        try {
            // Create the file
            outputStream = mContext.openFileOutput(SONGS_FILE, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            Timber.d("Songs saved to file " + SONGS_FILE);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            // Write the json string to file.
            PojoMapper.toJson(mSongs, writer, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveAlbumsToJSONFile() {
        Writer writer = null;
        OutputStream outputStream;

        try {
            // Create the file
            outputStream = mContext.openFileOutput(ALBUMS_FILE, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            Timber.d("Albums saved to file " + ALBUMS_FILE);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            // Write the json string to file.
            PojoMapper.toJson(mAlbums, writer, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
