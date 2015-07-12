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

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {


    public ArtistsFragment() {
        // Required empty public constructor
    }

    @InjectView(R.id.artists_grid_view)
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
        mArtists = MusicLibrary.get(mContext).getArtists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);
        ButterKnife.inject(this, rootView);


        mArtistsAdapter = new ArtistAdapter(mContext, mArtists);
        if(mGridView == null) {
            Timber.d("GRIVIEW IS NULL");
        } else {
            Timber.d("GRIDVIEW IS NOT NULL");
        }

        if(mArtistsAdapter == null) {
            Timber.d("ARTIST ADAPTER IS NULL");
        } else {
            Timber.d("ARTIST ADAPTER IS NOT NULL");
        }
        mGridView.setAdapter(mArtistsAdapter);
        return rootView;
    }

}
