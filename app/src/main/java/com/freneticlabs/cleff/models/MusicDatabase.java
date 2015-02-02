package com.freneticlabs.cleff.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by jcmanzo on 1/6/15.
 */
public class MusicDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "musiclibrary.db";
    private static final int DATABASE_VERSION = 1;

    //Music library table.
    public static final String GENRE_TABLE = "genre";
    public static final String MUSIC_TABLE = "musiclibrary";
    public static final String _ID = "_id";
    public static final String SONG_TITLE = "song_name";
    public static final String SONG_ARTIST = "song_artist";
    public static final String SONG_ALBUM = "song_album";
    public static final String SONG_ALBUM_ID = "song_album_id";
    public static final String SONG_ALBUM_ARTIST = "album_artist";
    public static final String SONG_DURATION = "song_duration";
    public static final String SONG_FILE_PATH = "song_file_path";
    public static final String SONG_TRACK_NUMBER = "song_track_number";
    public static final String SONG_GENRE = "song_genre";
    public static final String SONG_PLAY_COUNT = "song_play_count";
    public static final String SONG_YEAR = "song_year";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String SONG_SCANNED = "scanned";
    public static final String SONG_RATING = "rating";
    public static final String DATE_ADDED = "date_added";
    public static final String RATING = "rating";
    public static final String LAST_PLAYED = "last_played";
    public static final String SONG_SOURCE = "source";
    public static final String SONG_ALBUM_ART_PATH = "album_art_path";
    public static final String SONG_DELETED = "deleted";
    public static final String ARTIST_ART_LOCATION = "artist_art_location";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST_ID = "artist_id";
    public static final String GENRE_ID = "_id";
    public static final String GENRE_NAME = "genre_name";
    public static final String GENRE_SONG_COUNT = "genre_song_count";
    public static final String LOCAL_COPY_PATH = "local_copy_path";
    public static final String LIBRARIES = "libraries";
    public static final String SAVED_POSITION = "saved_position";
    public static final String ALBUMS_COUNT = "albums_count";
    public static final String SONGS_COUNT = "songs_count";
    public static final String GENRES_SONG_COUNT = "num_of_songs";

    public MusicDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public MusicDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, factory, version);
    }


    public Cursor getAllSongs() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] projection = {
                _ID,
                SONG_TITLE,
                SONG_ARTIST,
                SONG_FILE_PATH
        };
        String sqlTables = "musiclibrary";

        qb.setTables(sqlTables);
        Cursor songCursor = qb.query(db, null, null, null,
                null, null, SONG_TITLE + " ASC");

        songCursor.moveToFirst();

        return songCursor;
    }
}