package com.freneticlabs.cleff.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicDatabase;
import com.freneticlabs.cleff.models.MusicLibrary;

import java.util.HashMap;


/**
 * This Fragment manages a single background task to rebuild the
 * music library. The fragment is retained across configuration changes.
 * Activities that contain this fragment must implement the
 * {@link BuildLibraryTaskFragment.BuildLibraryTaskCallbacks} interface
 * to handle interaction events.
 */
public class BuildLibraryTaskFragment extends Fragment {
    private static final String TAG = BuildLibraryTaskFragment.class.getSimpleName();
    private static final String ALBUM_ART_PATH = "content://media/external/audio/albumart";
    private static final Uri EXTERNAL_CONTENT = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private Context mContext;
    private CleffApp mCleffApp;
    private String mMediaStoreSelection = null;
    private HashMap<String, String> mGenresHashMap = new HashMap<String, String>();
    private HashMap<String, Integer> mGenresSongCountHashMap = new HashMap<String, Integer>();

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
        mContext = getActivity();
        mCleffApp = (CleffApp)mContext.getApplicationContext();
        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        mTask = new BuildLibraryTask(mContext);
        mTask.execute();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mBuildLibraryTaskCallbacks = null;
    }

    private class BuildLibraryTask extends AsyncTask <Void, Void, Void>{

        public BuildLibraryTask(Context context) {
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
            Cursor mediaStoreCursor = getSongsFromMediaStore();
            if (mediaStoreCursor!=null) {
                saveMediaStoreDataToDB(mediaStoreCursor);
                mediaStoreCursor.close();
            }
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
        }

        public void saveMediaStoreDataToDB(Cursor mediaStoreCursor) {
            // Populates the ArrayList in MusicLibrary with Song objects.
            MusicLibrary.get(getActivity()).clearLibrary();

            buildGenresTable();

            String mUnknownAlbum = getActivity().getApplicationContext().getString(R.string.unknown_album_name);
            String mUnknownArtist = getActivity().getApplicationContext().getString(R.string.unknown_artist_name);
            String mUnknownTitle = getActivity().getApplicationContext().getString(R.string.unknown_title_name);


            if(mediaStoreCursor!=null && mediaStoreCursor.moveToFirst()){

                /** These are the columns in the music cursor that we are interested in. */
                int idColumn = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int albumArtistColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                int albumIdColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID);
                int dateAddedColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
                int dateModifiedColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED);
                int durationColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int titleColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int yearColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
                int filePathColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                do {
                    // long thisSongId = musicCursor.getLong(idColumn);
                    int thisSongId = Integer.parseInt(mediaStoreCursor.getString(idColumn));

                    String songAlbumId = mediaStoreCursor.getString(albumIdColumn);
                    String songId = mediaStoreCursor.getString(idColumn);
                    String songYear = mediaStoreCursor.getString(yearColumn);
                    String songTitle = mediaStoreCursor.getString(titleColumn);
                    String songArtist = mediaStoreCursor.getString(artistColumn);
                    String songAlbum = mediaStoreCursor.getString(albumColumn);
                    String songFilePath = mediaStoreCursor.getString(filePathColumn);
                    String songDuration = mediaStoreCursor.getString(durationColumn);
                    String songDateAdded = mediaStoreCursor.getString(dateAddedColumn);
                    String songDateModified = mediaStoreCursor.getString(dateModifiedColumn);
                    String songAlbumArtist = mediaStoreCursor.getString(albumArtistColumn);

                    boolean unknownArtist = songArtist == null || songArtist.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownAlbum = songAlbum == null || songAlbum.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownTitle = songTitle == null || songTitle.equals(MediaStore.UNKNOWN_STRING);

                    if(unknownArtist) songArtist = mUnknownArtist;
                    if(unknownAlbum) songAlbum = mUnknownAlbum;
                    if(unknownTitle) songTitle = mUnknownTitle;

                    ContentValues values = new ContentValues();
                    values.put(MusicDatabase.SONG_TITLE, songTitle);
                    values.put(MusicDatabase.SONG_FILE_PATH, songFilePath);
                    values.put(MusicDatabase.SONG_ARTIST, songArtist);
                    values.put(MusicDatabase.SONG_GENRE, getSongGenre(songFilePath));
                    mCleffApp.getMusicDatabase()
                            .getWritableDatabase()
                            .insert(MusicDatabase.SONG_TABLE, null, values);

                }
                while (mediaStoreCursor.moveToNext());
            }
            if(mediaStoreCursor != null) mediaStoreCursor.close();
        }

        private String getSongGenre(String filePath) {
            if (mGenresHashMap!=null)
                return mGenresHashMap.get(filePath);
            else
                return mContext.getResources().getString(R.string.unknown_genre_name);
        }

        private String getSongGenreCount(String filePath) {
            if (mGenresSongCountHashMap!=null)
                return Integer.toString(mGenresSongCountHashMap.get(filePath));
            else
                return mContext.getResources().getString(R.string.unknown_genre_name);
        }
        /**
         * Retrives all songs from the MediaStore with no folder contraint
         */
        private Cursor getSongsFromMediaStore() {
            //Get a cursor of all active music folders.
           // Cursor musicFoldersCursor = mContext.getDBAccessHelper().getAllMusicFolderPaths();

            Cursor mediaStoreCursor = null;
            String sortOrder = null;
            String projection[] = { MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.TRACK,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.DATE_MODIFIED,
                    MediaStore.Audio.Media.TRACK,
                    MediaStore.Audio.Media._ID
            };

            //Grab the cursor of MediaStore entries.
          //  if (musicFoldersCursor==null || musicFoldersCursor.getCount() < 1) {
                //No folders were selected by the user. Grab all songs in MediaStore.
               // mediaStoreCursor = MediaStoreAccessHelper.getAllSongs(mContext, projection, sortOrder);
          //  } else {
                //Build a selection statement for querying MediaStore.
               // mMediaStoreSelection = buildMusicFoldersSelection(musicFoldersCursor);
                /*mediaStoreCursor = MediaStoreAccessHelper.getAllSongsWithSelection(mContext,
                        mMediaStoreSelection,
                        projection,
                        sortOrder);

                //Close the music folders cursor.
                musicFoldersCursor.close();*/
           // }
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + "=1";

            mediaStoreCursor = contentResolver.query(uri, null, selection, null, sortOrder);

            return mediaStoreCursor;
        }

        /**
         * Builds a HashMap of all songs and their genres.
         */
        private void buildGenresTable() {
            // Queries for this Genre columns.
            String[] genresProjection = {
                    MediaStore.Audio.Genres.NAME,
                    MediaStore.Audio.Genres._ID
            };

            //Get a cursor of all genres in MediaStore.
            Cursor genresCursor = mContext.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    genresProjection,
                    null,
                    null,
                    null);

            //Iterate through all genres in MediaStore.
            for (genresCursor.moveToFirst(); !genresCursor.isAfterLast(); genresCursor.moveToNext()) {
                int genreIdColumn = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                String genreId = genresCursor.getString(genreIdColumn);

                int genreNameColumn = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                String genreName = genresCursor.getString(genreNameColumn);

                if (genreName==null || genreName.isEmpty() ||
                        genreName.equals(" ") || genreName.equals("   ") ||
                        genreName.equals("    ")) {
                    genreName = mContext.getResources().getString(R.string.unknown_genre_name);
                }


            /* Grab a cursor of songs in the each genre id. Limit the songs to
             * the user defined folders using mMediaStoreSelection.
            */
                Cursor cursor = mContext.getContentResolver().query(makeGenreUri(genreId),
                        new String[] { MediaStore.Audio.Media.DATA },
                        null,
                        null,
                        null);

                //Add the songs' file paths and their genre names to the hash.
                if (cursor!=null) {
                    for (int i=0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        mGenresHashMap.put(cursor.getString(0), genreName);
                        mGenresSongCountHashMap.put(genreName, cursor.getCount());
                    }
                    ContentValues values = new ContentValues();
                    values.put(MusicDatabase.GENRE_ID, genreId);
                    values.put(MusicDatabase.GENRE_NAME, genreName);
                    values.put(MusicDatabase.GENRES_SONG_COUNT, getSongGenreCount(genreName));
                    mCleffApp.getMusicDatabase()
                            .getWritableDatabase()
                            .insert(MusicDatabase.GENRE_TABLE, null, values);

                    cursor.close();
                }

            }

            if (genresCursor!=null)
                genresCursor.close();

        }

        /**
         * Returns a Uri of a specific genre in MediaStore.
         * The genre is specified using the genreId parameter.
         */
        private Uri makeGenreUri(String genreId) {
            String CONTENTDIR = MediaStore.Audio.Genres.Members.CONTENT_DIRECTORY;
            return Uri.parse(new StringBuilder().append(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString())
                    .append("/")
                    .append(genreId)
                    .append("/")
                    .append(CONTENTDIR)
                    .toString());
        }


    }

}