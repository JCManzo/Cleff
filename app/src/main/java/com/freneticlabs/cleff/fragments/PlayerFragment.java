package com.freneticlabs.cleff.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {
    public static final String EXTRA_SONG_ID = "com.freneticlabs.cleff.song_id";
    private Song mSong;
    @InjectView(R.id.player_song_name) TextView mSongName;
    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(long songId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SONG_ID, songId);

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        long songId = (long)getArguments().getSerializable(EXTRA_SONG_ID);
        mSong = MusicLibrary.get(getActivity()).getSong(songId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        mSongName.setText(mSong.getTitle());
        return view;
    }


}