package com.freneticlabs.cleff.fragments;


import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.listeners.RecyclerItemClickListener;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.views.DividerItemDecoration;
import com.freneticlabs.cleff.views.adapters.AlbumInfoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment {
    private String mAlbumName;
    private int mAlbumId;

    public static final String ALBUM_INFO_ID = "AlbumInfoId";
    public static final String ALBUM_INFO_NAME = "AlbumInfoName";
    private ArrayList<Song> mAlbumSongs;

    @Bind(R.id.recycler_view_album_info) RecyclerView mRecyclerView;
    @Bind(R.id.album_detail_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.album_detail_toolbar_image) ImageView mToolbarImage;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    public AlbumDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_album_detail, container, false);
        ButterKnife.bind(this, rootView);

        mAlbumId = getArguments().getInt(ALBUM_INFO_ID);
        mAlbumName = getArguments().getString(ALBUM_INFO_NAME);
        mAlbumSongs = MusicLibrary.get(getActivity()).getAllAlbumSongs(mAlbumId);
        setUpToolbar();
        setUpRecyclerView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mToolbarImage.setImageDrawable(null);
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    private void setUpToolbar() {
        // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            mCollapsingToolbarLayout.setTitle(mAlbumName);
            Uri uri = ContentUris.withAppendedId(Uri.parse(CleffApp.ART_WORK_PATH), mAlbumId);
            Picasso.with(getActivity())
                    .load(uri)
                    .placeholder(R.drawable.no_album_art)
                    .resize(500, 500)
                    .into(mToolbarImage);
        }
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setUpRecyclerView() {
        Timber.d("album " + Integer.toString(mAlbumId) + " selected");

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new AlbumInfoAdapter(getActivity(), mAlbumSongs));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                Song song = mAlbumSongs.get(position);

                CleffApp.getEventBus().post(new SongSelectedEvent(song, position));

                playerIntent.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getId());
                playerIntent.putParcelableArrayListExtra(PlayerActivity.EXTRA_SONG_DATA, mAlbumSongs);
                startActivityForResult(playerIntent, 0);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }
}
