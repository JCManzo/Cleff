package com.freneticlabs.cleff.fragments;

import android.app.Activity;
import android.content.ContentResolver;
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
import com.freneticlabs.cleff.models.Album;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import java.util.HashMap;

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
    private static final String ALBUM_ART_PATH = "content://media/external/audio/albumart";
    private static final Uri EXTERNAL_CONTENT = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private Context mContext;
    private CleffApp mCleffApp;
    private String mMediaStoreSelection = null;
    private HashMap<String, String> mGenresHashMap = new HashMap<String, String>();
    private HashMap<String, Integer> mGenresSongCountHashMap = new HashMap<String, Integer>();
    private ContentResolver mContentResolver;

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

    public interface BuildCursorListener {

        public void onCursorReady(Cursor cursor, int currentSongIndex, boolean playAll);
    }

    private BuildLibraryTaskCallbacks mBuildLibraryTaskCallbacks;
    private BuildCursorListener mBuildCursorListener;

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
        mContentResolver = mContext.getContentResolver();

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
            Cursor albumStoreCursor = getAlbumsFromMediaStore();

            if (mediaStoreCursor!=null) {
                buildSongList(mediaStoreCursor);
                mediaStoreCursor.close();
            }

            if(albumStoreCursor  != null) {
                buildAlbumList(albumStoreCursor);
                albumStoreCursor.close();
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

        public void buildSongList(Cursor mediaStoreCursor) {
            // Populates the ArrayList in MusicLibrary with Song objects.


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
                int songTrackColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

                // Queries for this Genre columns.
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Genres._ID
                };

                do {

                    int songId = mediaStoreCursor.getInt(idColumn);

                    int songAlbumId = mediaStoreCursor.getInt(albumIdColumn);
                    String songYear = mediaStoreCursor.getString(yearColumn);
                    String songTitle = mediaStoreCursor.getString(titleColumn);
                    String songArtist = mediaStoreCursor.getString(artistColumn);
                    String songAlbum = mediaStoreCursor.getString(albumColumn);
                    String songFilePath = mediaStoreCursor.getString(filePathColumn);
                    String songDuration = mediaStoreCursor.getString(durationColumn);
                    String songDateAdded = mediaStoreCursor.getString(dateAddedColumn);
                    String songDateModified = mediaStoreCursor.getString(dateModifiedColumn);
                    String songAlbumArtist = mediaStoreCursor.getString(albumArtistColumn);
                    String songTrackNumber = mediaStoreCursor.getString(songTrackColumn);
                    String songGenre= "";

                    /* TODO find an alternative to getContentUriForAudioID in order to use it in lower versions*/
                    Uri songGenreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songId);
                    Cursor genresCursor = mContentResolver.query(songGenreUri, genresProjection, null, null, null);
                    int genresIdColumn = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    if(genresCursor.moveToFirst()) {
                        do {
                            songGenre += genresCursor.getString(genresIdColumn) + " ";
                        } while (genresCursor.moveToNext());
                    }

                    Timber.d(songGenre);
                    genresCursor.close();
                    boolean unknownArtist = songArtist == null || songArtist.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownAlbum = songAlbum == null || songAlbum.equals(MediaStore.UNKNOWN_STRING);
                    boolean unknownTitle = songTitle == null || songTitle.equals(MediaStore.UNKNOWN_STRING);

                    if(unknownArtist) songArtist = mUnknownArtist;
                    if(unknownAlbum) songAlbum = mUnknownAlbum;
                    if(unknownTitle) songTitle = mUnknownTitle;


                    Song song = new Song();
                    song.setId(songId);
                    song.setYear(songYear);
                    song.setTitle(songTitle);
                    song.setArtist(songArtist);
                    song.setAlbum(songAlbum);
                    song.setPath(songFilePath);
                    song.setAlbumID(songAlbumId);
                    song.setGenre(songGenre);

                    MusicLibrary.get(mContext).addSong(song);
                }
                while (mediaStoreCursor.moveToNext());
            }
            if(mediaStoreCursor != null) mediaStoreCursor.close();
        }

        public void buildAlbumList(Cursor cursor) {
            String mUnknownAlbum = getActivity().getApplicationContext().getString(R.string.unknown_album_name);

            if (cursor != null && cursor.moveToFirst()) {
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                int albumNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                int albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                int albumSongCountColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

                do {
                    int albumId = cursor.getInt(albumIdColumn);
                    int albumSongCount = cursor.getInt(albumSongCountColumn);
                    String albumName = cursor.getString(albumNameColumn);
                    String albumArtist = cursor.getString(albumArtistColumn);

                    boolean isUnknownAlbum = albumName == null || albumName.equals(MediaStore.UNKNOWN_STRING);

                    if(isUnknownAlbum) albumName = mUnknownAlbum;

                    Album album = new Album();
                    album.setId(albumId);
                    album.setAlbumName(albumName);
                    album.setAlbumArtist(albumArtist);
                    album.setNumOfSongs(albumSongCount);
                    MusicLibrary.get(mContext).addAlbum(album);
                } while (cursor.moveToNext());
            }
            if(cursor != null) cursor.close();
            Timber.d(Integer.toString(MusicLibrary.get(mContext).getAlbums().size()));
        }

        /**
         * Retrives all songs from the MediaStore with no folder contraint
         */
        private Cursor getSongsFromMediaStore() {
            //Get a cursor of all active music folders.
            // Cursor musicFoldersCursor = mContext.getDBAccessHelper().getAllMusicFolderPaths();

            Cursor mediaStoreCursor = null;
            String projection[] = {
                    MediaStore.Audio.Media.TITLE,
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
                    MediaStore.Audio.Media._ID,
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
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + "=1";

            mediaStoreCursor = mContentResolver.query(uri, projection, selection, null, MediaStore.Audio.Media.TITLE);

            return mediaStoreCursor;
        }

        /**
         * Retrives all albums from the MediaStore with no folder contraint
         */
        private Cursor getAlbumsFromMediaStore() {
            Cursor cursor = null;
            String projection[] = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS
            };
            Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

            cursor = mContentResolver.query(uri, projection, null, null, MediaStore.Audio.Albums.ALBUM);

            return cursor;
        }

        /**
         * Builds a HashMap of all songs, their genres and the amount of songs
         * of each genre.
         */
        private String getGenreForSong() {
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

                    cursor.close();
                }

            }

            if (genresCursor!=null)
                genresCursor.close();

            return "";

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

}