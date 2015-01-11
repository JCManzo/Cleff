package com.freneticlabs.cleff.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Album;
import com.freneticlabs.cleff.views.adapters.AlbumsAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment {
    @InjectView(R.id.albums_grid_view)
    GridView mGridView;
    private AlbumsAdapter mAlbumsAdapter;
    private ArrayList<Album> mAlbums;
    private SharedPreferences mSettings;

    private CleffApp mCleffApp;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCleffApp = (CleffApp)getActivity().getApplication();
        mSettings = mCleffApp.getSharedPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_albums_grid, container, false);
        ButterKnife.inject(this, rootView);


       // mAlbumsAdapter = new AlbumsAdapter(getActivity(), R.layout.albums_grid_item, mAlbums);
//        mGridView.setAdapter(mAlbumsAdapter);

        return rootView;
    }


}
