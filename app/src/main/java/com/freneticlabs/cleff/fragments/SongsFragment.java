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
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.views.DividerItemDecoration;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;

import java.util.ArrayList;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;


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
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        ButterKnife.bind(this, rootView);
        mSongs = MusicLibrary.get(mContext).getSongs();
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
                mSongsAdapter.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getAlbum().compareTo(song2.getAlbum());
                    }
                });
                break;
            case R.id.action_sort_by_artist:
                mSongsAdapter.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getArtist().compareTo(song2.getArtist());
                    }
                });
                break;
            case R.id.action_sort_by_genre:
                mSongsAdapter.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getGenre().compareTo(song2.getAlbum());
                    }
                });
                break;
            case R.id.action_sort_by_name:
                mSongsAdapter.sort(new Comparator<Song>() {
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
        mSongsAdapter = new SongsAdapter(mContext, mRecyclerView, mSongs);
        mRecyclerView.setAdapter(mSongsAdapter);

        // Set the divider between song items
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

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
    }

    /**
     * Sets up listerners
     */
    private void setUpListeners() {
        // Listener for when a recyclerview item is clicked

    }
}