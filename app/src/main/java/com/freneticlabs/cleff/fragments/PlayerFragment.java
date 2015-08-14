package com.freneticlabs.cleff.fragments;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {
  public static final String EXTRA_SONG_ID = "com.freneticlabs.cleff.song_id";

  @Bind(R.id.player_song_art) ImageView mSongArt;

  private Song mSong;

  public PlayerFragment() {
    // Required empty public constructor
  }

  public static PlayerFragment newInstance(int songId) {
    Bundle args = new Bundle();
    args.putSerializable(EXTRA_SONG_ID, songId);

    PlayerFragment fragment = new PlayerFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    int songId = (int) getArguments().getSerializable(EXTRA_SONG_ID);
    mSong = MusicLibrary.getInstance(getActivity()).getSong(songId);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_player, container, false);
    ButterKnife.bind(this, view);

    // Load the song's album art
    Uri uri = ContentUris.withAppendedId(Uri.parse(CleffApp.ART_WORK_PATH), mSong.getAlbumId());
    Picasso.with(getActivity())
        .load(uri)
        .resize(400, 400)
        .placeholder(R.drawable.no_album_art)
        .error(R.drawable.adele)
        .into(mSongArt);

    return view;
  }

  @Override public void onDestroyView() {
    mSongArt.setImageDrawable(null);
    super.onDestroyView();
    ButterKnife.unbind(this);
  }
}