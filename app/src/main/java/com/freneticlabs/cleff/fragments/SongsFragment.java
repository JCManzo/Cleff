package com.freneticlabs.cleff.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.models.MusicDatabase;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.events.QueryAllSongsEvent;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class SongsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.recycler_view_songs)
    ListView mListView;
    @InjectView(R.id.action_play)
    FloatingActionButton mFloatingPlayButton;
    @InjectView(R.id.action_skip_next)
    FloatingActionButton mFloatingNextButton;
    @InjectView(R.id.action_skip_previous)
    FloatingActionButton mFloatingPreviousButton;
    @InjectView(R.id.floating_player_actions)
    FloatingActionsMenu mFloatingActionsMenu;
    private static final int URL_LOADER = 0;

    private SharedPreferences mSettings;
    private int mLastClickedItem = -1;
    private CleffApp mCleffApp;
    private Context mContext;
    private SongsAdapter mSongsAdapter;
    private int mLastPosition = 0;
    private int mPositionOffset = 0;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity().getApplicationContext();
        mCleffApp = (CleffApp) getActivity().getApplication();
        mSettings = mCleffApp.getSharedPreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveListPosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);

        mSongsAdapter = new SongsAdapter(getActivity(), null, 0);
        mListView.setAdapter(mSongsAdapter);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        initListeners();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //updateUi();
        updateState(SCROLL_STATE_IDLE);
        restoreListPosition();
    }

    private void initListeners() {
        // Listener for when a list item is clicked

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), PlayerActivity.class);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MusicDatabase.SONG_FILE_PATH));
                Timber.d(path);

                // i.putExtra(PlayerFragment.EXTRA_SONG_ID, 3);
               // startActivity(i);
               //play((position));
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                updateState(scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastPosition = mListView.getFirstVisiblePosition();
                View childView = mListView.getChildAt(0);
                mPositionOffset = (childView == null) ? 0 : childView.getTop();

            }
        });


        // Set up the floating player action listeners
        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Timber.d("EXPANDED");
            }

            @Override
            public void onMenuCollapsed() {
                Timber.d("COLLAPSED");

            }
        });

        mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlayer(mCleffApp.getService().getCurrentSongPosition());

            }
        });

        mFloatingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED NEXT");
                mCleffApp.getService().playNext();
            }
        });

        mFloatingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED PREVIOUS");
                mCleffApp.getService().playPrevious();
            }
        });
    }

    private void saveListPosition() {
        Timber.d(Integer.toString(mLastPosition));

        SharedPreferences.Editor editor = mSettings.edit();

        editor.putInt(CleffApp.LAST_VIEWED_ITEM, mLastPosition);

        editor.putInt(CleffApp.LAST_VIEWED_OFFSET, mPositionOffset);

        editor.apply();
    }

    private void restoreListPosition() {
        int position = mSettings.getInt(CleffApp.LAST_VIEWED_ITEM, 0);
        int offset = mSettings.getInt(CleffApp.LAST_VIEWED_OFFSET, 0);

        Timber.d("I: " + position + " " + "I2: " + " " + offset);

        mListView.setSelectionFromTop(position, offset);
    }

    public void updateUi() {
        if (mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PLAYING)) {
            Timber.d("PLAYER UI CHANGED TO PLAY");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else {
            Timber.d("PLAYER UI CHANGED TO PAUSED");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);
        }
    }

    public void play(int file) {
        mCleffApp.getPlaybackManager().initPlayback();
        mCleffApp.getService().setSong(file);
        mCleffApp.getService().playSong();
    }

    public void updatePlayer(int selectedSong) {
        Timber.d("Updating player..");

        if (mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.IDLE)) {
            Timber.d("PLAYER IS NOW PLAYING");

            play(selectedSong);

        } else if (mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PLAYING)) {
            int lastPlayedSong = mSettings.getInt(CleffApp.LAST_PLAYED_SONG, selectedSong);
            if (lastPlayedSong == selectedSong) {
                mCleffApp.getService().pausePlayer();
                Timber.d("PLAYER IS NOW PAUSED");
            } else {
                Timber.d("PLAYING NEW SONG");
                play(selectedSong);
            }
        } else if (mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PAUSED)) {
            int lastPlayedSong = mSettings.getInt(CleffApp.LAST_PLAYED_SONG, selectedSong);
            Timber.d("Last played song: " + Integer.toString(lastPlayedSong));
            Timber.d("Current song: " + Integer.toString(selectedSong));

            if (lastPlayedSong == selectedSong) {
                mCleffApp.getService().resumePlayer();
                Timber.d("RESUMING PLAYER");

            } else {
                Timber.d("PLAYING NEW SONG");
                play(selectedSong);
            }
        } else {
            Timber.d("NONE");
            throw new RuntimeException("Not a valid state");
        }

        updateUi();
    }

    public void saveLastPlayedSong(int position) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(CleffApp.LAST_SELECTED_ITEM, position);

        editor.apply();
    }

    private void updateState(int scrollState) {
        String stateName = "Undefined";
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                stateName = "Idle";
                break;

            case SCROLL_STATE_DRAGGING:
                stateName = "Dragging";
                break;

            case SCROLL_STATE_SETTLING:
                stateName = "Flinging";
                break;
        }

    }


    /**
     * Queries the Cleff database for all songs
     * @param loaderId
     * @param args
     * @return a cursor containing all the songs in the library
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        final String[] projection = {
                MusicDatabase._ID,
                MusicDatabase.SONG_ARTIST,
                MusicDatabase.SONG_TITLE,
                MusicDatabase.SONG_FILE_PATH
        };

        Uri uri = MusicLibrary.CONTENT_URI;

        // Returns a new CursorLoader
        return new CursorLoader(
                getActivity(),   // Parent activity context
                uri,        // Table to query
                projection,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null           // Default sort order
        );
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSongsAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        // Post the event to any subscribers
        CleffApp.getEventBus().post(new QueryAllSongsEvent((cursor)));
        mSongsAdapter.swapCursor(cursor);

    }
}