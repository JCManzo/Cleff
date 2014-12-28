package com.freneticlabs.cleff.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import timber.log.Timber;


/**
 * This Fragment manages a single background task to rebuild the
 * music library. The fragment is retained across configuration changes.
 * Activities that contain this fragment must implement the
 * {@link BuildLibraryTaskFragment.BuildLibraryTaskCallbacks} interface
 * to handle interaction events.
 */
public class BuildLibraryTaskFragment extends Fragment {
    private static final String TAG = BuildLibraryTaskFragment.class.getSimpleName();

    public BuildLibraryTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public static interface BuildLibraryTaskCallbacks {
        void onPreExecute();
        void onCancelled();
        void onPostExecute();
    }

    private BuildLibraryTaskCallbacks mBuildLibraryTaskCallbacks;
    private BuildLibraryTask mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mBuildLibraryTaskCallbacks = (BuildLibraryTaskCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement BuildLibraryTaskCallbacks.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        mTask = new BuildLibraryTask();
        mTask.execute();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mBuildLibraryTaskCallbacks = null;
    }

    private class BuildLibraryTask extends AsyncTask <Void, Void, Void>{

        public BuildLibraryTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mBuildLibraryTaskCallbacks != null) {
                mBuildLibraryTaskCallbacks.onPreExecute();
            }
            Log.i("TASK", "Frag onPreExecute");

        }

        @Override
        protected Void doInBackground(Void... voids) {
            createSongList();
            return null;
        }

        @Override
        protected void onCancelled() {
            if (mBuildLibraryTaskCallbacks != null) {
                mBuildLibraryTaskCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mBuildLibraryTaskCallbacks != null) {
                mBuildLibraryTaskCallbacks.onPostExecute();
             }
            Log.i(TAG, "Frag onPostExecute");
        }

        public void createSongList() {
            // Populates the ArrayList in MusicLibrary with Song objects.
            MusicLibrary.get(getActivity()).clearLibrary();

            String mUnknownAlbum = getActivity().getApplicationContext().getString(R.string.unknown_album_name);
            String mUnknownArtist = getActivity().getApplicationContext().getString(R.string.unknown_artist_name);
            String mUnknownTitle = getActivity().getApplicationContext().getString(R.string.unknown_title_name);

            ContentResolver musicResolver = getActivity().getContentResolver();
            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String where = MediaStore.Audio.Media.IS_MUSIC + "=1";

            Cursor musicCursor = musicResolver.query(musicUri, null, where, null, null);
            if(musicCursor!=null && musicCursor.moveToFirst()){

                /** These are the columns in the music cursor that we are interested in. */
                int idColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.ARTIST);
                int albumColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.ALBUM);

                int albumIdColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Albums.ALBUM_ID);
                int dateAddedColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.DATE_ADDED);
                int durationColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.DURATION);
                int titleColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.TITLE);
                int yearColumn = musicCursor.getColumnIndexOrThrow
                        (MediaStore.Audio.Media.YEAR);


                // Queries for this Genre columns.
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Genres._ID
                };

                // Add songs to list
                do {
                   // long thisSongId = musicCursor.getLong(idColumn);
                    int thisSongId = Integer.parseInt(musicCursor.getString(idColumn));

                    long thisAlbumId = musicCursor.getLong(albumIdColumn);
                    int thisYear = musicCursor.getInt(yearColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    String thisGenre = "";

                    Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, thisAlbumId);

                    // Get album art
                    Timber.d(albumArtUri.toString());

                    /* TODO find an alternative to getContentUriForAudioID in order to use it in lower versions*/
                    Uri songGenreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", thisSongId);
                    Cursor genresCursor = musicResolver.query(songGenreUri, genresProjection, null, null, null);
                    int genresIdColumn = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    if(genresCursor.moveToFirst()) {
                        do {
                            thisGenre += genresCursor.getString(genresIdColumn) + " ";
                        } while (genresCursor.moveToNext());
                    }

                    if(genresCursor != null) genresCursor.close();

                   // Log.i(TAG, thisTitle + " " + thisGenre);

                    boolean unknownArtist = thisArtist == null || thisArtist.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownAlbum = thisAlbum == null || thisAlbum.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownTitle = thisTitle == null || thisTitle.equals(MediaStore.UNKNOWN_STRING);
                    Timber.d(Long.toString(thisAlbumId));

                    if(unknownArtist) thisArtist = mUnknownArtist;
                    if(unknownAlbum) thisAlbum = mUnknownAlbum;
                    if(unknownTitle) thisTitle = mUnknownTitle;

                    Song song = new Song(thisSongId, thisTitle, thisArtist,
                                         thisAlbum, thisAlbumId, thisGenre);

                    MusicLibrary.get(getActivity()).addSong(song);
                    MusicLibrary.get(getActivity()).addAlumbArtPair(thisAlbumId, albumArtUri);
                }
                while (musicCursor.moveToNext());
            }
            if(musicCursor != null) musicCursor.close();
            MusicLibrary.get(getActivity()).sortLibrary();
        }
    }

}
