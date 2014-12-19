package com.freneticlabs.cleff.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.adapters.SongAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment implements
            SongAdapter.ItemClickListener {

    @InjectView(R.id.recycler_view_songs) RecyclerView recyclerView;

    private static final String TAG = SongListFragment.class.getSimpleName();
    private SongAdapter mSongAdapter;
    OnListViewSongListener mCallback;


    public SongListFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface OnListViewSongListener {
        public void OnListViewSongSelected(Song song);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        setHasOptionsMenu(true);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*if (savedInstanceState != null) {
            ArrayList<Song> values = savedInstanceState.getParcelableArrayList("songList");
            if (values != null) {
                mSongAdapter = new SongAdapter(values, this);
            }
        }*/

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        mSongAdapter = new SongAdapter(MusicLibrary.get(getActivity()).getSongs(), this);

        recyclerView.setAdapter(mSongAdapter);
        return rootView;
    }

    @Override
    public void itemClicked(Song song) {
        mCallback.OnListViewSongSelected(song);

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
