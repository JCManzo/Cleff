package com.freneticlabs.cleff.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.PlayerFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.utils.Utils;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity {
    private ArrayList<Song> mSongs;
    private Song mSong;
    private Handler mSeekBarHandler = new Handler();
    private int mSeekBarProgress = 0;
    private static String mPlayerState = CleffApp.MUSIC_IDLE;

    private CleffApp mCleffApp;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.player_activity_pager) ViewPager mViewPager;
    @Bind(R.id.seekBar) DiscreteSeekBar mDiscreteSeekBar;
    @Bind(R.id.player_song_title) TextView mSongTitle;
    @Bind(R.id.player_song_artist) TextView mSongArtist;

    @Bind(R.id.player_toggle_button) ImageView mToggleButton;
    @Bind(R.id.player_skip_prev_button) ImageView mSkipPrevButton;
    @Bind(R.id.player_skip_next_button) ImageView mSkipNextButton;
    @Bind(R.id.player_fav_fab) FloatingActionButton mFloatingActionFavButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);
        mCleffApp = (CleffApp)getApplication();
        mSongs = MusicLibrary.get(this).getSongs();

        setUpToolbar();

        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Song song = mSongs.get(position);

                return PlayerFragment.newInstance(song.getId());
            }

            @Override
            public int getCount() {
                return mSongs.size();
            }
        });
        updateProgressBar();

        int songId = (int)getIntent().getSerializableExtra(PlayerFragment.EXTRA_SONG_ID);

        for (int i = 0; i < mSongs.size(); i++) {
            mSong = mSongs.get(i);
            if(mSong.getId() == songId) {
                mViewPager.setCurrentItem(i);
                updateSongDisplayInfo(mSong);
                break;
            }
        }

        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        CleffApp.getEventBus().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        CleffApp.getEventBus().unregister(this);
        super.onPause();
    }

    private void updateSongDisplayInfo(Song song) {
        mSongArtist.setText(song.getArtist());
        mSongTitle.setText(song.getTitle());
    }

    public void setUpToolbar() {
        // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    /**
     * Updates the seekbar every 100 milliseconds
     */
    public void updateProgressBar() {
        mSeekBarHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Runnable that converts duration of song to mm:ss format.
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if(mCleffApp.getService() != null && mCleffApp.getService().isPlaying()) {
                int totalDuration = mCleffApp.getService().getDuration();
                int currentDuration = mCleffApp.getService().getMPCurrentPosition();
                // Displaying Total Duration time
                //mSongTotalDuration.setText("" + MusicUtils.milliSecondsToTimer(totalDuration));
                //int max_time = MusicUtils.millisecondsToTime(totalDuration);
                int cur_time = Utils.millisecondsToTime(currentDuration);

                //Timber.d(MusicUtils.milliSecondsToTimer(totalDuration));
               // Timber.d(MusicUtils.milliSecondsToTimer(currentDuration));
               // Timber.d(Integer.toString(cur_time));
                // Displaying time completed playing
               // mSongCurrentDuration.setText("" + MusicUtils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                mSeekBarProgress = (int) (Utils.getProgressPercentage(currentDuration, totalDuration));
                mDiscreteSeekBar.setProgress(mSeekBarProgress);
                //mDiscreteSeekBar.setIndicatorFormatter("%04d");
               // mDiscreteSeekBar.setMax(max_time);
                // Running this thread after 100 milliseconds
                mSeekBarHandler.postDelayed(this, 100);
            }
        }
    };

    @OnClick(R.id.player_toggle_button)
        public void playerToggle() {
            mCleffApp.getService().togglePlayer();
        }

    @Subscribe
    public void onMusicStateChange(MusicStateChangeEvent event) {
        mPlayerState = event.musicState;
        updateUi();
    }

    private void updateUi() {
        if(mPlayerState.equals(CleffApp.MUSIC_IDLE)) {

        } else if(mPlayerState.equals(CleffApp.MUSIC_PLAYING)) {

            mToggleButton.setImageResource(R.drawable.ic_player_pause_circle_outline);

        } else if(mPlayerState.equals(CleffApp.MUSIC_PAUSED)) {

            mToggleButton.setImageResource(R.drawable.ic_player_orange_play_circle_outline);

        }
    }

    private void initListeners() {

        // Seekbar
        mDiscreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                    // remove message Handler from updating progress bar
                    mSeekBarHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                mSeekBarHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mCleffApp.getService().getDuration();
                int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mCleffApp.getService().seek(currentPosition);
                Timber.d(Integer.toString(currentPosition));
                // update timer progress again
                updateProgressBar();
            }
        });

        // ViewPager
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Song song = mSongs.get(position);
                updateSongDisplayInfo(song);
                if (song.getTitle() != null) {
                    // setTitle(song.getTitle());

                }
                CleffApp.getEventBus().post(new SongSelectedEvent(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCleffApp.getService().togglePlayer();
                updateUi();
            }
        });

        mSkipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Timber.d("BEFORE SONG POST: " + Integer.toString(mCleffApp.getService().getCurrentSongPosition()));
                mCleffApp.getService().playNext();
                //Timber.d("AFTER SONG POST: " + Integer.toString(mCleffApp.getService().getCurrentSongPosition()));
                mViewPager.setCurrentItem(mCleffApp.getService().getCurrentSongPosition());
                updateUi();
            }
        });

        mSkipPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCleffApp.getService().playPrevious();
                mViewPager.setCurrentItem(mCleffApp.getService().getCurrentSongPosition());
                updateUi();
            }
        });

        mFloatingActionFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar
                        .make(view, R.string.player_added_to_faves, Snackbar.LENGTH_LONG)
                        .show();
                mFloatingActionFavButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                mFloatingActionFavButton.setImageResource(R.drawable.ic_player_favorite_full);
            }
        });
    }
}