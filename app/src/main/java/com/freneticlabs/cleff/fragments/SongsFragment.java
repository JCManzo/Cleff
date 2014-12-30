package com.freneticlabs.cleff.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class SongsFragment extends Fragment implements
    SongsAdapter.SongsListHeaderListener{

    @InjectView(R.id.recycler_view_songs) TwoWayView mRecyclerView;
    @InjectView(R.id.action_play) FloatingActionButton mFloatingPlayButton;
    @InjectView(R.id.action_skip_next) FloatingActionButton mFloatingNextButton;
    @InjectView(R.id.action_skip_previous) FloatingActionButton mFloatingPreviousButton;
    @InjectView(R.id.floating_player_actions) FloatingActionsMenu mFloatingActionsMenu;

    private SharedPreferences mSettings;

    private CleffApp mCleffApp;

    public SongsFragment() {
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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final Drawable divider = getResources().getDrawable(R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        SongsAdapter songAdapter = new SongsAdapter(getActivity(), this);
        mRecyclerView.setAdapter(songAdapter);

        initListeners();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updatePlayerActionsUi();
        updateState(SCROLL_STATE_IDLE);

    }

    private void initListeners() {
        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                Timber.d("Item clicked: " + position);

                Song song = MusicLibrary.get(getActivity()).getSongs().get(position - 1);
                Intent i = new Intent(getActivity(), PlayerActivity.class);
                i.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getID());
                startActivity(i);

                if (!mCleffApp.isServiceRunning()) {
                    Timber.e("SERVICE NOT RUNNING YO");
                }

                if (position > 0) {
                    mCleffApp.getPlaybackManager().initPlayback();
                    mCleffApp.getService().setSong((position - 1));
                    mCleffApp.getService().playSong();
                    updatePlayerActionsUi();
                }

            }
        });

        itemClick.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
               // mToast.setText("Item long pressed: " + position);
                //mToast.show();
                return true;
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
               // mPositionText.setText("First: " + mRecyclerView.getFirstVisiblePosition());
               // mCountText.setText("Count: " + mRecyclerView.getChildCount());
            }
        });

        // Set up the floating player action listeners
        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Timber.d("EXPANDED");
            }

            @Override
            public void onMenuCollapsed() {
                Timber.d("COLLAPSED");

            }
        });

        mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            updatePlayer();

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

    public void updatePlayerActionsUi() {
        if(mCleffApp.getService().isPlaying().equals(MusicService.PlayerState.PLAYING)) {
            Timber.d("PLAYER UI CHANGED TO PLAY");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);
        } else {
            Timber.d("PLAYER UI CHANGED TO PAUSED");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);
        }
    }

    public void updatePlayer (){
        if(mCleffApp.getService().isPlaying().equals(MusicService.PlayerState.PLAYING)) {
            mCleffApp.getService().pausePlayer();
            Timber.d("PLAYER IS NOW PAUSED");
        } else {
            if(mCleffApp.getService().isPaused().equals(MusicService.PlayerState.PAUSED)) {
                mCleffApp.getService().resumePlayer();
                Timber.d("RESUMING PLAYER");
            } else {
                mCleffApp.getPlaybackManager().initPlayback();
                Timber.d("PLAYER IS NOW PLAYING");
                mCleffApp.getService().playSong();
            }
        }

        updatePlayerActionsUi();
    }

    @Override
    public void repeatClicked() {
        if(mCleffApp.getService().isRepeat()) {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Repeat off")
                            .attachToRecyclerView(mRecyclerView));
        } else {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Repeat on")
                            .attachToRecyclerView(mRecyclerView));
        }

        mCleffApp.getService().setRepeat();
    }

    @Override
    public void shuffleClicked() {
        if(mCleffApp.getService().isShuffle()) {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Shuffle off")
                            .attachToRecyclerView(mRecyclerView));
        } else {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Shuffle on")
                            .attachToRecyclerView(mRecyclerView));
        }

        mCleffApp.getService().setShuffle();
    }

    private void updateState(int scrollState) {
        String stateName = "Undefined";
        switch(scrollState) {
            case SCROLL_STATE_IDLE:
                stateName = "Idle";
                break;

            case SCROLL_STATE_DRAGGING:
                stateName = "Dragging";
                break;

            case SCROLL_STATE_SETTLING:
                stateName = "Flinging";
                break;
        }

    }
}