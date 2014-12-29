package com.freneticlabs.cleff.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.views.adapters.AlbumsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment {
    @InjectView(R.id.recycler_view_albums) RecyclerView mRecyclerView;
    public AlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_albums_grid, container, false);
        ButterKnife.inject(this, rootView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        AlbumsAdapter albumsAdapter = new AlbumsAdapter(getActivity());
        mRecyclerView.setAdapter(albumsAdapter);
        return rootView;
    }


}
