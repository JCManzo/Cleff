package com.freneticlabs.cleff.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.adapters.SongAdapter;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    private ArrayList<Song> mSongList;
    private ListView mSongView;
    OnListViewSongListener mCallback;

    private static final String TAG = "SongListFragment";

    public SongsFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface OnListViewSongListener {
        public void OnListViewSongSelected(Song song);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSongList = MusicLibrary.get(getActivity()).getSongs();
       // setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        mSongList = MusicLibrary.get(getActivity()).getSongs();

        Timber.d("Created");

        mSongView = (ListView)view.findViewById(R.id.list_view_songs);


        mSongView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Song song = mSongList.get(position);

                mCallback.OnListViewSongSelected(song);
            }
        });


        SongAdapter songAdt = new SongAdapter(getActivity());

        mSongView.setAdapter(songAdt);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
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
