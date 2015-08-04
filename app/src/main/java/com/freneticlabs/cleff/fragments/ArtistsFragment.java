package com.freneticlabs.cleff.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Artist;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.views.adapters.ArtistAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {


    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.artists_grid_view)
    GridView mGridView;
    private ArtistAdapter mArtistsAdapter;
    private ArrayList<Artist> mArtists;
    private SharedPreferences mSettings;
    private Context mContext;
    private CleffApp mCleffApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity().getApplicationContext();
        mCleffApp = (CleffApp)getActivity().getApplication();
        mSettings = mCleffApp.getAppPreferences();
        mArtists = new ArrayList<Artist>(MusicLibrary.get(mContext).getArtists());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);
        ButterKnife.bind(this, rootView);

        mArtistsAdapter = new ArtistAdapter(mContext, mArtists);

        mGridView.setAdapter(mArtistsAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
