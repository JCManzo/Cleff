package com.freneticlabs.cleff.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Artist;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.views.adapters.ArtistAdapter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {

  public ArtistsFragment() {
    // Required empty public constructor
  }

  @Bind(R.id.artists_view) RecyclerView mRecyclerView;
  private ArtistAdapter mArtistsAdapter;
  private ArrayList<Artist> mArtists;
  private Context mContext;
  private CleffApp mCleffApp;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    mContext = getActivity().getApplicationContext();
    mCleffApp = (CleffApp) getActivity().getApplication();
    mArtists = new ArrayList<Artist>(MusicLibrary.getInstance(mContext).getAllArtists());
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_artist_grid, container, false);
    ButterKnife.bind(this, rootView);

    setUpRecyclerViewAndAdapter();
    return rootView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  private void setUpRecyclerViewAndAdapter() {
    mArtistsAdapter = new ArtistAdapter(mContext, mRecyclerView, mArtists);
    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    mRecyclerView.setAdapter(mArtistsAdapter);
  }
}
