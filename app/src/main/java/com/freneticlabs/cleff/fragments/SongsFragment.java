package com.freneticlabs.cleff.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.SongListDivider;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class SongsFragment extends Fragment implements
    SongsAdapter.SongsListHeaderListener{

    @InjectView(R.id.recycler_view_songs) TwoWayView mRecyclerView;
    @InjectView(R.id.action_play) FloatingActionButton mFloatingPlayButton;
    @InjectView(R.id.action_skip_next) FloatingActionButton mFloatingNextButton;
    @InjectView(R.id.action_skip_previous) FloatingActionButton mFloatingPreviousButton;

    private CleffApp mCleffApp;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setHasOptionsMenu(true);

        mCleffApp = (CleffApp)getActivity().getApplication();

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


        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SongListDivider(getActivity()));

        SongsAdapter songAdapter = new SongsAdapter(getActivity(), this);
        mRecyclerView.setAdapter(songAdapter);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                Timber.d("Item clicked: " + position);
                if(mCleffApp.isServiceRunning() == false) {
                    Timber.d("NOT RUNNING YO");
                }

                if(position > 0) {
                    mCleffApp.getPlaybackManager().initPlayback();
                    mCleffApp.getService().setSong((position - 1));
                    mCleffApp.getService().playSong();
                }
            }
        });

        // Set up the floating player action listeners
        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu)rootView.findViewById(R.id.floating_player_actions);
        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Timber.d("EXPANDED");
            }

            @Override
            public void onMenuCollapsed() {
                Timber.d("COLLAPSED");

            }
        });

        initListeners();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initListeners() {
        mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("CLICKED PLAY");
                mCleffApp.getService().playSong();
            }
        });

        mFloatingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED NEXT");
                mCleffApp.getService().playNext();
            }
        });

        mFloatingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED PREVIOUS");
                mCleffApp.getService().playPrevious();
            }
        });
    }

    @Override
    public void repeatClicked() {
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text("Repeat on"));
    }

    @Override
    public void shuffleClicked() {
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text("Shuffle on"));
    }
}