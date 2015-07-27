package com.freneticlabs.cleff.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.views.adapters.SlidingTabsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibrarySlidingTabsPagerFragment extends Fragment {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.floating_player_actions) FloatingActionsMenu mFloatingActionsMenu;
    @Bind(R.id.action_play) FloatingActionButton mFloatingPlayButton;
    @Bind(R.id.action_skip_next) FloatingActionButton mFloatingNextButton;
    @Bind(R.id.action_skip_previous) FloatingActionButton mFloatingPreviousButton;

    private CleffApp mCleffApp;
    private static String mPlayerState = CleffApp.MUSIC_IDLE;

    public LibrarySlidingTabsPagerFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library_sliding_tabs_pager, container, false);
        ButterKnife.bind(this, view);
        mCleffApp = (CleffApp)getActivity().getApplication();

        setUpToolbar();

        ViewPager mViewPager = (ViewPager)view.findViewById(R.id.main_pager);
        mViewPager.setAdapter(new SlidingTabsAdapter(getChildFragmentManager(), getActivity().getApplication().getApplicationContext()));

        TabLayout mTabs = (TabLayout)view.findViewById(R.id.main_tabs);

        mTabs.setupWithViewPager(mViewPager);

        setUpListeners();
        updateFloatingUi();
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void setUpToolbar() {
       // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpListeners() {
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
                mCleffApp.getService().togglePlayer();

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

    private void updateFloatingUi() {
        if(mPlayerState.equals(CleffApp.MUSIC_IDLE)) {

        } else if(mPlayerState.equals(CleffApp.MUSIC_PLAYING)) {

            mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else if(mPlayerState.equals(CleffApp.MUSIC_PAUSED)) {

            mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);

        }
    }

    /**
     * Called when the player global state has changed.
     * @param event is the state of the MediaPlayer
     */
    @Subscribe
    public void onMusicStateChange(MusicStateChangeEvent event) {
        mPlayerState = event.musicState;
        updateFloatingUi();
        Timber.d(mPlayerState);
    }
}
