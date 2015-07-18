package com.freneticlabs.cleff.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.listeners.RecyclerItemClickListener;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.views.DividerItemDecoration;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class SongsFragment extends Fragment {

    @InjectView(R.id.recycler_view_songs)
    RecyclerView mRecyclerView;

   /* @InjectView(R.id.action_play)
    FloatingActionButton mFloatingPlayButton;

    @InjectView(R.id.action_skip_next)
    FloatingActionButton mFloatingNextButton;

    @InjectView(R.id.action_skip_previous)
    FloatingActionButton mFloatingPreviousButton;

    */

    private SharedPreferences mSettings;
    private RecyclerView.LayoutManager mLayoutManager;

    private final String LIST_STATE_KEY = "list_state";

    private Parcelable mListState;
    private CleffApp mCleffApp;
    private Context mContext;
    private SongsAdapter mSongsAdapter;
    private ArrayList<Song> mSongs;
    private int mLastPosition = 0;
    private int mPositionOffset = 0;
    private int mCurrentSongPosition = 0;

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
        mSettings = mCleffApp.getAppPreferences();
        mSongs = MusicLibrary.get(mContext).getSongs();
    }


    @Override
    public void onResume() {
        super.onResume();
        CleffApp.getEventBus().register(this);

        if(mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CleffApp.getEventBus().unregister(this);

        mListState = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);
        setUpRecyclerView();
        setUpListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFloatingUi();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    /**
     * Called when the player global state has changed.
     * @param event is the state of the MediaPlayer
     */
    @Subscribe
    public void onMusicStateChange(MusicStateChangeEvent event) {
        mPlayerState = event.musicState;
        Timber.d(mPlayerState);
    }

    private void updateFloatingUi() {
            Timber.d(mPlayerState);
/*
            if(mPlayerState.equals(CleffApp.MUSIC_IDLE)) {

        } else if(mPlayerState.equals(CleffApp.MUSIC_PLAYING)) {

              mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else if(mPlayerState.equals(CleffApp.MUSIC_PAUSED)) {

             mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);

        }*/
    }

    private void setUpRecyclerView() {
        // Set a linear layout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLayoutManager = mRecyclerView.getLayoutManager();

        // Create and set the adapter
        mSongsAdapter = new SongsAdapter(mContext, mSongs);
        mRecyclerView.setAdapter(mSongsAdapter);

        // Set the divider between song items
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

    }

    /**
     * Sets up listerners
     */
    private void setUpListeners() {
        // Listener for when a recyclerview item is clicked
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                mCurrentSongPosition = position;
                Song song = mSongs.get(mCurrentSongPosition);

                CleffApp.getEventBus().post(new SongSelectedEvent(position));

                playerIntent.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getId());
                startActivityForResult(playerIntent, 0);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

       /* // Set up the floating player action listeners
         mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
             @Override
             public void onMenuExpanded() {
                 Timber.d("EXPANDED");
             }

             @Override
             public void onMenuCollapsed() {
                 Timber.d("COLLAPSED");

             }
         });*/

        /*mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }



}