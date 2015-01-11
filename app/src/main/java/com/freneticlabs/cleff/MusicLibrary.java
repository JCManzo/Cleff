package com.freneticlabs.cleff;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.freneticlabs.cleff.models.MusicDatabase;

import timber.log.Timber;

/**
 * Created by jcmanzo on 1/10/15.
 */
public class MusicLibrary extends ContentProvider {
    private MusicDatabase mMusicLibrary;
    private static final String AUTHORITY = "com.freneticlabs.cleff";
    private static final String BASE_PATH = "musiclibrary";
    /** A uri to do operations on cust_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    /** Constants to identify the requested operation */
    private static final int SONGS = 10;
    private static final int SONG_ID = 20;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY,  BASE_PATH, SONGS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SONG_ID);
    }

    /** A callback method which is invoked when the content provider is starting up */
    public boolean onCreate() {
        mMusicLibrary = new MusicDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /** A callback method which is by the default content uri */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SONGS:
                Timber.d("URITYPE SONGS");
                return mMusicLibrary.getSongs();
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
}
