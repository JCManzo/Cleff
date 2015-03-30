package com.freneticlabs.cleff.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class SongsFragment extends Fragment {

    @InjectView(R.id.list_view_songs)
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
    private ArrayList<Song> mSongs;
    private int mLastPosition = 0;
    private int mPositionOffset = 0;
    private int mCurrentSongPosition =0;
    private static String mPlayerState = CleffApp.MUSIC_IDLE;
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
        mSongs = MusicLibrary.get(mContext).getSongs();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        CleffApp.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CleffApp.getEventBus().unregister(this);
        saveListPosition();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);

        mSongsAdapter = new SongsAdapter(mContext, mSongs);
        mListView.setAdapter(mSongsAdapter);
        initListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFloatingUi();
        restoreListPosition();

    }

    /**
     * Called when the player global state has changed.
     * @param event is the state of the MediaPlayer
     */
    @Subscribe
    public void onMusicStateChange(MusicStateChangeEvent event) {
        mPlayerState = event.musicState;
        Timber.d(mPlayerState);
        updateFloatingUi();

    }

    private void updateFloatingUi() {
        Timber.d(mPlayerState);

        if(mPlayerState.equals(CleffApp.MUSIC_IDLE)) {

        } else if(mPlayerState.equals(CleffApp.MUSIC_PLAYING)) {

            mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else if(mPlayerState.equals(CleffApp.MUSIC_PAUSED)) {

            mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);

        }
    }

    /**
     * Sets up listerners
     */
    private void initListeners() {
        // Listener for when a list item is clicked

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
               mCurrentSongPosition = position;
               Song song = mSongs.get(mCurrentSongPosition);

               CleffApp.getEventBus().post(new SongSelectedEvent(position));

               playerIntent.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getId());
               startActivityForResult(playerIntent, 0);
           }
       });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

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
               mCleffApp.getService().togglePlayer();

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

    /**
     * Saves last position in the list to a preferences files.
     */
    private void saveListPosition() {
        Timber.d(Integer.toString(mLastPosition));

        SharedPreferences.Editor editor = mSettings.edit();

        editor.putInt(CleffApp.LAST_VIEWED_ITEM, mLastPosition);

        editor.putInt(CleffApp.LAST_VIEWED_OFFSET, mPositionOffset);

        editor.apply();
    }

    /**
     * Restores the previous position of the list
     */
    private void restoreListPosition() {
        int position = mSettings.getInt(CleffApp.LAST_VIEWED_ITEM, 0);
        int offset = mSettings.getInt(CleffApp.LAST_VIEWED_OFFSET, 0);

        mListView.setSelectionFromTop(position, offset);
    }

    public void saveLastPlayedSong(int position) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(CleffApp.LAST_SELECTED_ITEM, position);

        editor.apply();
    }

}