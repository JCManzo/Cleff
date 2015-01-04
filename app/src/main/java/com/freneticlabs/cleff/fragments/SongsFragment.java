package com.freneticlabs.cleff.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.activities.PlayerActivity;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.adapters.SongsAdapter;
import com.freneticlabs.cleff.views.widgets.CheckableImageButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class SongsFragment extends Fragment{

    @InjectView(R.id.recycler_view_songs) ListView mListView;
    @InjectView(R.id.action_play) FloatingActionButton mFloatingPlayButton;
    @InjectView(R.id.action_skip_next) FloatingActionButton mFloatingNextButton;
    @InjectView(R.id.action_skip_previous) FloatingActionButton mFloatingPreviousButton;
    @InjectView(R.id.floating_player_actions) FloatingActionsMenu mFloatingActionsMenu;

    private SharedPreferences mSettings;
    private int mLastClickedItem = -1;
    private CleffApp mCleffApp;
    private Context mContext;
    private ArrayList<Song> mSongs;
    private int mLastPosition = 0;
    private int mPositionOffset = 0;
    private CheckableImageButton mCurrentButton;
    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity().getApplicationContext();
        mCleffApp = (CleffApp)getActivity().getApplication();
        mSettings = mCleffApp.getSharedPreferences();
        mSongs = MusicLibrary.get(mContext).getSongs();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveListPosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        ButterKnife.inject(this, rootView);

        final SongsAdapter songAdapter = new SongsAdapter(getActivity(), R.layout.song_list_row_item, mSongs);
        songAdapter.setSongsListListener(new SongsAdapter.SongsListListener() {
            @Override
            public void playBackClicked(int clickedPosition) {
                Timber.d(Integer.toString(clickedPosition));

                int wantedChild = getWantedChild(clickedPosition);
                View view = mListView.getChildAt(wantedChild);
                //view.setBackgroundColor(getResources().getColor(R.color.red));
                    if (view.isSelected()) {
                        Timber.d("IS SELECTED");
                        //view.setSelected(false);

                    } else {
                        Timber.d("NOT SELECTED");
                        view.setSelected(true);

                    }
                 updatePlayer(clickedPosition);
                    if(mLastClickedItem != -1 && mLastClickedItem != clickedPosition) {
                        int t = getWantedChild(mLastClickedItem);
                        View view2 = mListView.getChildAt(t);
                        if(view2 != null)
                        view2.setSelected(false);
                    }
                    mLastClickedItem = clickedPosition;

            }

            @Override
            public void itemClicked(int song) {
                 /*

        */
            }
        });
        mListView.setAdapter(songAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               view.setSelected(true);
                Song song = MusicLibrary.get(getActivity()).getSongs().get(position);
                Intent i = new Intent(getActivity(), PlayerActivity.class);
               // i.putExtra(PlayerFragment.EXTRA_SONG_ID, song.getID());
                //startActivity(i);

                    updatePlayer((position));
                   // saveLastPlayedSong((position));
                //Timber.d("CLICKED");
            }
        });
        initListeners();
        return rootView;
    }

    private int getWantedChild(int position) {
        int firstPosition = mListView.getFirstVisiblePosition() - mListView.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = position - firstPosition;

        if (wantedChild < 0 || wantedChild >= mListView.getChildCount()) {
            Timber.d("Unable to get view for desired position, because it's not being displayed on screen.");
            return -1;
        }

        return wantedChild;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //updateUi();
        updateState(SCROLL_STATE_IDLE);
        restoreListPosition();

    }


    private void initListeners() {

        mListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                updateState(scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                mLastPosition = mListView.getFirstVisiblePosition();
                View childView = mListView.getChildAt(0);
                mPositionOffset = (childView == null) ? 0 : childView.getTop();

               // Timber.d("First visible position: " + first + " with offset: " + topOffset);

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
            updatePlayer(mCleffApp.getService().getCurrentSongPosition());

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

    private void saveListPosition() {
        Timber.d(Integer.toString(mLastPosition));

        SharedPreferences.Editor editor = mSettings.edit();

        editor.putInt(CleffApp.LAST_VIEWED_ITEM, mLastPosition);

        editor.putInt(CleffApp.LAST_VIEWED_OFFSET, mPositionOffset);

        editor.apply();
    }

    private void restoreListPosition() {
        int position = mSettings.getInt(CleffApp.LAST_VIEWED_ITEM, 0);
        int offset = mSettings.getInt(CleffApp.LAST_VIEWED_OFFSET, 0);

        Timber.d("I: " + position + " " + "I2: " + " " + offset);

        mListView.setSelectionFromTop(position, offset);
    }

    public void updateUi() {
        if(mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PLAYING)) {
            Timber.d("PLAYER UI CHANGED TO PLAY");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else {
            Timber.d("PLAYER UI CHANGED TO PAUSED");
            mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);
        }
    }

    public void play(int songIndex) {
        mCleffApp.getPlaybackManager().initPlayback();
        mCleffApp.getService().setSong(songIndex);
        mCleffApp.getService().playSong();
    }

    public void updatePlayer (int position){
        Timber.d("Updating player..");
        int selectedSong = position;

        if(mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.IDLE)) {
            Timber.d("PLAYER IS NOW PLAYING");

            play(selectedSong);

        } else if(mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PLAYING)) {
            int lastPlayedSong = mSettings.getInt(CleffApp.LAST_PLAYED_SONG, selectedSong);
            if(lastPlayedSong == selectedSong) {
                mCleffApp.getService().pausePlayer();
                Timber.d("PLAYER IS NOW PAUSED");
            } else {
                Timber.d("PLAYING NEW SONG");
               play(selectedSong);
            }
        } else if(mCleffApp.getService().getPlayerSate().equals(MusicService.PlayerState.PAUSED)) {
            int lastPlayedSong = mSettings.getInt(CleffApp.LAST_PLAYED_SONG, selectedSong);
            Timber.d("Last played song: " + Integer.toString(lastPlayedSong));
            Timber.d("Current song: " + Integer.toString(selectedSong));

            if(lastPlayedSong == selectedSong) {
                mCleffApp.getService().resumePlayer();
                Timber.d("RESUMING PLAYER");

            } else {
                Timber.d("PLAYING NEW SONG");
                play(selectedSong);
            }
        } else {
            Timber.d("NONE");
            throw new RuntimeException("Not a valid state");
        }

        updateUi();
    }

    public void saveLastPlayedSong(int position) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(CleffApp.LAST_SELECTED_ITEM, position);

        editor.apply();
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