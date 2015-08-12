package com.freneticlabs.cleff.models;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.models.events.MusicDataChangedEvent;
import com.freneticlabs.cleff.utils.PojoMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

/**
 * Created by jcmanzo on 8/16/14.
 */
public class MusicLibrary {
    private static MusicLibrary sMusicLibrary;
    private Context mContext;
    private CleffApp mCleffApp;
    private ArrayList<Song> mSongs;
    private ArrayList<Album> mAlbums;
    private ArrayList<Artist> mArtists;


    private static final String SONGS_FILE = "songs.json";
    private static final String ALBUMS_FILE = "albums.json";
    private static final String ARTISTS_FILE = "artists.json";

    public MusicLibrary(Context context) {
        Timber.d("Constructor called");
        mContext = context;
        mSongs = new ArrayList<Song>();
        mAlbums = new ArrayList<Album>();
        mArtists = new ArrayList<Artist>();

        mCleffApp = (CleffApp.getCleffApp());

    }

    /**
     * Returns a single instance of this class
     * @param context
     * @return
     */
    public static MusicLibrary getInstance(Context context) {
        if(sMusicLibrary == null) {
            sMusicLibrary = new MusicLibrary(context.getApplicationContext());

        }
        return sMusicLibrary;
    }

    /**
     * Adds {@param song} to the songs dataset
     *
     * @param song is the song to be added
     */
    public void addSong(Song song) {
            mSongs.add(song);
    }

    /**
     * Adds {@album album} to the album dataset
     *
     * @param album is the album to be added
     */
    public void addAlbum(Album album) {
            mAlbums.add(album);
    }

    /**
     * Adds {@artist artist} to the artist dataset
     *
     * @param artist is the artist to be added
     */
    public void addArtist(Artist artist) {
            mArtists.add(artist);
    }

    public ArrayList<Song> getAllSongs() {
        return mSongs;
    }

    public ArrayList<Album> getAllAlbums() {
        return mAlbums;
    }

    public ArrayList<Artist> getAllArtists() {
        return mArtists;
    }

    public ArrayList<Song> sortSongsBy(Comparator<? super Song> comparator) {
        Timber.d("Sorting..");
        Collections.sort(mSongs, comparator);

        CleffApp.getEventBus().post(new MusicDataChangedEvent(mSongs));
        return mSongs;
    }

    public ArrayList<Song> getAllAlbumSongs(int albumId) {
        ArrayList<Song> albumSongs = new ArrayList<>();
        for (Song song : mSongs) {
            if(song.getAlbumId() == albumId) {
                albumSongs.add(song);
            }
        }

        return albumSongs;
    }

    public Song getSong(int id) {
        for(Song song : mSongs) {
            if(song.getId() == id) {
                return song;
            }
        }
        return null;
    }

    public void toggleFavorite(int songId) {
        for(Song song : mSongs) {
            if (song.getId() == songId) {
                if (song.getFavorited()) {
                    song.setFavorited(false);
                } else {
                    song.setFavorited(true);
                }
                int index = mSongs.indexOf(song);
                mSongs.set(index, song);
                CleffApp.getEventBus().post(new MusicDataChangedEvent(mSongs));
                printLibrary();
                Timber.d("Updating song with: " + song.getFavorited());
            }
        }
    }

    public boolean isSongFavorited(int songId) {
        for (Song song : mSongs) {
            if (song.getId() == songId) {
                return song.getFavorited();
            }
        }
        return false;
    }

    public void printLibrary() {
        for (Song song : mSongs) {
            Timber.d(song.toString());
        }
    }

    /**
     * Saves all music data to JSON files.
     *
     * @return true if succesfull, false otherwise.
     */
    public boolean saveLibrary() {
        try {
            Timber.i("Saving Library..");
            saveSongsToJSONFile();
            saveAlbumsToJSONFile();
            saveArtistsToJSONFile();

            return true;
        } catch (Exception e) {
            Timber.e("Error saving library: ", e);
            return false;
        }
    }

    public boolean loadLibrary() {
        try {
            loadSongsFromJSONFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            loadAlbumsFromJSONFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }

        try {
            loadArtistsFromJSONFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }

        return true;
    }
  /**
     * Reads JSON file containing songs and creates an ArrayList
     * of Song POJO's
   * */
    private void loadSongsFromJSONFile() throws IOException {
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
     */
    private void loadAlbumsFromJSONFile() throws IOException {
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
     * Reads JSON file containing artists and creates an ArrayList
     * of Artist POJO's
     *
     */
    private void loadArtistsFromJSONFile() throws IOException {
        try {
            //Open and read the file
            InputStream inputStream = mContext.openFileInput(ARTISTS_FILE);

            mArtists = PojoMapper.fromJson(inputStream, new TypeReference<ArrayList<Artist>>() { });
            Timber.d("Artists loaded from file " + ARTISTS_FILE);
        } catch (FileNotFoundException ex) {
            // ignore. happens when app is first initialized
        }
    }


    /**
     * Saves the current songs to a JSON File
     */
    private void saveSongsToJSONFile() {
        Writer writer = null;
        OutputStream outputStream = null;

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

    /**
     * Saves the current albums to a JSON File
     */
    private void saveAlbumsToJSONFile() {
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

    /**
     * Saves the current artists to a JSON File
     */
    private void saveArtistsToJSONFile() {
        Writer writer = null;
        OutputStream outputStream;

        try {
            // Create the file
            outputStream = mContext.openFileOutput(ARTISTS_FILE, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            Timber.d("Artists saved to file " + ARTISTS_FILE);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            // Write the json string to file.
            PojoMapper.toJson(mArtists, writer, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
