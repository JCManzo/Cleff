package com.freneticlabs.cleff.models;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freneticlabs.cleff.CleffApp;
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
        mContext = context;
        mSongs = new ArrayList<>();
        mAlbums = new ArrayList<>();
        mArtists = new ArrayList<>();

        mCleffApp = (CleffApp.getCleffApp());
        try {
            loadAlbumsFromJSONFile();
            loadSongsFromJSONFile();
            loadArtistsFromJSONFile();
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

    public void addArtist(Artist artist) {
        mArtists.add(artist);
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

    public ArrayList<Artist> getArtists() {
        return mArtists;
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

    public void printLibSongs() {
        for(Song song : mSongs) {
            Timber.i(song.getTitle());
        }
    }

    public void printLibAlbums()  {
        for(Album album : mAlbums) {
            Timber.i(album.getAlbumName());
        }
    }

    /**
     * Saves all music data to JSON files.
     *
     * @return true if succesfull, false otherwise.
     */
    public boolean saveLibrary() {
        try {
           // mSongsJSONSerializer.saveSongs(mSongs);
            saveAlbumsToJSONFile();
            saveSongsToJSONFile();
            saveArtistsToJSONFile();
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
     * Reads JSON file containing artists and creates an ArrayList
     * of Artist POJO's
     *
     */
    public void loadArtistsFromJSONFile() throws IOException {
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
    public void saveSongsToJSONFile() {
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

    /**
     * Saves the current artists to a JSON File
     */
    public void saveArtistsToJSONFile() {
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


    public void sortSongsByTitle() {
        Collections.sort(mSongs, new Comparator<Song>() {
            public int compare(Song song1, Song song2) {
                return song1.getTitle().compareTo(song2.getTitle());
            }
        });
    }
    public void sortSongsByAlbum() {
        Collections.sort(mSongs, new Comparator<Song>() {
            public int compare(Song song1, Song song2) {
                return song1.getAlbum().compareTo(song2.getAlbum());
            }
        });
    }
    public void sortSongsByArtist() {
        Collections.sort(mSongs, new Comparator<Song>() {
            public int compare(Song song1, Song song2) {
                return song1.getArtist().compareTo(song2.getArtist());
            }
        });
    }
    public void sortSongsByGenre() {
        Collections.sort(mSongs, new Comparator<Song>() {
            public int compare(Song song1, Song song2) {
                return song1.getGenre().compareTo(song2.getGenre());
            }
        });
    }
}
