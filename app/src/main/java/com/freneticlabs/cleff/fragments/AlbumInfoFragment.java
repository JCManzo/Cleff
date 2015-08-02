package com.freneticlabs.cleff.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.DividerItemDecoration;
import com.freneticlabs.cleff.views.adapters.AlbumInfoAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumInfoFragment extends Fragment {
    private int mAlbumId;
    private Context mContext;
    private ArrayList<Song> mAlbumSongs;
    @Bind(R.id.recycler_view_album_info) RecyclerView mRecyclerView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    public AlbumInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_album_info_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        setUpToolbar();

        mAlbumId =getArguments().getInt(CleffApp.ALBUM_INFO_ID);
        Timber.d("album " + Integer.toString(mAlbumId) + " selected");
        mAlbumSongs = MusicLibrary.get(getActivity()).getAllAlbumSongs(mAlbumId);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new AlbumInfoAdapter(getActivity(), mAlbumSongs));
        return rootView;
    }


    public void setUpToolbar() {
        // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
