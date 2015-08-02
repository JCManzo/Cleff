package com.freneticlabs.cleff.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.listeners.RecyclerItemClickListener;
import com.freneticlabs.cleff.models.Album;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.events.AlbumInfoSelectedEvent;
import com.freneticlabs.cleff.views.adapters.AlbumsAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment {

    @Bind(R.id.albums_grid_view) RecyclerView mRecyclerView;
    private ArrayList<Album> mAlbums;
    private SharedPreferences mSettings;
    private Context mContext;
    private CleffApp mCleffApp;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    public interface OnAlbumSelectedListener {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity().getApplicationContext();
        mCleffApp = (CleffApp)getActivity().getApplication();
        mSettings = mCleffApp.getAppPreferences();
        mAlbums = MusicLibrary.get(mContext).getAlbums();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_albums_grid, container, false);
        ButterKnife.bind(this, rootView);

        setUpRecyclerViewAndAdapter();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setUpRecyclerViewAndAdapter() {
        AlbumsAdapter albumsAdapter = new AlbumsAdapter(mContext, mRecyclerView, mAlbums);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(albumsAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Album album = mAlbums.get(position);

                CleffApp.getEventBus().post(new AlbumInfoSelectedEvent(album.getId()));


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }
}
