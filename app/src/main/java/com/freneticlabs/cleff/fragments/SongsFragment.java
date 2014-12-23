package com.freneticlabs.cleff.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.SongListDivider;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SongsFragment extends Fragment implements
        SongsAdapter.ItemClickListener {

    @InjectView(R.id.recycler_view_songs) RecyclerView mRecyclerView;

    private static final String TAG = SongsFragment.class.getSimpleName();
    OnListViewSongListener mCallback;



    public SongsFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface OnListViewSongListener {
        public void OnListViewSongSelected(Song song, int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Items will be shown in a vertical linear layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SongListDivider(getActivity()));

        SongsAdapter songAdapter = new SongsAdapter(MusicLibrary.get(getActivity()).getSongs(), getActivity(), this);
        mRecyclerView.setAdapter(songAdapter);


        return rootView;
    }

    @Override
    public void itemClicked(Song song, int position) {
        mCallback.OnListViewSongSelected(song, position);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnListViewSongListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}