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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.listeners.RecyclerItemClickListener;
import com.freneticlabs.cleff.models.ActivityResultBus;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.ActivityResultEvent;
import com.freneticlabs.cleff.models.events.MusicDataChangedEvent;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.views.DividerItemDecoration;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class SongsFragment extends Fragment {

    @Bind(R.id.recycler_view_songs) RecyclerView mRecyclerView;

    private SharedPreferences mSettings;
    private RecyclerView.LayoutManager mLayoutManager;

    private final String LIST_STATE_KEY = "list_state";

    private Parcelable mListState;
    private CleffApp mCleffApp;
    private Context mContext;
    private SongsAdapter mSongsAdapter;
    private ArrayList<Song> mSongs;
    private int mCurrentSongPosition = 0;


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
    }


    @Override
    public void onResume() {
        super.onResume();
        CleffApp.getEventBus().register(this);
        ActivityResultBus.getActivityResultBus().register(mActivityResultSubscriber);
        mSongs = MusicLibrary.getInstance(mContext).getAllSongs();
        if(mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CleffApp.getEventBus().unregister(this);
        ActivityResultBus.getActivityResultBus().unregister(mActivityResultSubscriber);

        mListState = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CleffApp.ACTIVITY_RESULT_CODE_SONGS_FRAGMENT) {
           // MusicLibrary.getInstance(mContext).printLibrary();
            mSongs = new ArrayList<>(MusicLibrary.getInstance(mContext).getAllSongs());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        ButterKnife.bind(this, rootView);
        mSongs = new ArrayList<>(MusicLibrary.getInstance(mContext).getAllSongs());
        setUpRecyclerView();
        setUpListeners();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_album:
                mSongsAdapter.sortLibrary(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getAlbum().compareTo(song2.getAlbum());
                    }
                });
                break;
            case R.id.action_sort_by_artist:
                mSongsAdapter.sortLibrary(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getArtist().compareTo(song2.getArtist());
                    }
                });
                break;
            case R.id.action_sort_by_genre:
                mSongsAdapter.sortLibrary(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getGenre().compareTo(song2.getAlbum());
                    }
                });
                break;
            case R.id.action_sort_by_name:
                mSongsAdapter.sortLibrary(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getTitle().compareTo(song2.getTitle());

                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
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

    private void setUpRecyclerView() {
        // Set a linear layout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLayoutManager = mRecyclerView.getLayoutManager();

        // Create and set the adapter
        mSongsAdapter = new SongsAdapter(mContext, mRecyclerView, new ArrayList<Song>(mSongs));
        mRecyclerView.setAdapter(mSongsAdapter);

        // Set the divider between song items
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));


    }

    /**
     * Listens to when the songs data set is changed
      * @param event
     */
    @Subscribe
    public void onMusicDataSetChanged(MusicDataChangedEvent event) {
        Timber.d("Music Dataset changed in SongsFragment");

       mSongs = event.songs;
       mSongsAdapter.notifyDataChange();
    }

    /**
     * Sets up listeners
     */
    private void setUpListeners() {
        // Listener for when a recyclerview item is clicked
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                mCurrentSongPosition = position;
                Song song = mSongs.get(mCurrentSongPosition);
                CleffApp.getEventBus().post(new MusicDataChangedEvent(MusicLibrary.getInstance(getActivity()).getAllSongs()));
                CleffApp.getEventBus().post(new SongSelectedEvent(song, position));

                // Pass all songs to the player viewpager activity
                playerIntent.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getId());
                playerIntent.putParcelableArrayListExtra(PlayerActivity.EXTRA_SONG_DATA, mSongs);
                getActivity().startActivityForResult(playerIntent, CleffApp.ACTIVITY_RESULT_CODE_SONGS_FRAGMENT);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();

            onActivityResult(requestCode, resultCode, data);
        }
    };
}