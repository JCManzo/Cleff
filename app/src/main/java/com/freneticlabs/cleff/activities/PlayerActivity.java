package com.freneticlabs.cleff.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.PlayerFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.freneticlabs.cleff.utils.MusicUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class PlayerActivity extends ActionBarActivity {
    private static final String BY = "by ";
    private ArrayList<Song> mSongs;
    private Song mSong;
    private Handler mSeekBarHandler = new Handler();
    private int mSeekBarProgress = 0;

    private CleffApp mCleffApp;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.player_activity_pager) ViewPager mViewPager;
    @InjectView(R.id.seekBar) DiscreteSeekBar mDiscreteSeekBar;
    @InjectView(R.id.player_song_album) TextView mSongAlbum;
    @InjectView(R.id.player_song_title) TextView mSongTitle;
    @InjectView(R.id.player_song_artist) TextView mSongArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.inject(this);
        mCleffApp = (CleffApp)getApplication();
        // Set up the toolbar to act as an action bar
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.getBackground().setAlpha(0);
        }
        mSongs = MusicLibrary.get(this).getSongs();

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
        mSongAlbum.setText(song.getAlbum());
        mSongArtist.setText(BY + song.getArtist());
        mSongTitle.setText(song.getTitle());
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
                int currentDuration = mCleffApp.getService().getCurrentPosition();
                // Displaying Total Duration time
                //mSongTotalDuration.setText("" + MusicUtils.milliSecondsToTimer(totalDuration));
                //int max_time = MusicUtils.millisecondsToTime(totalDuration);
                int cur_time = MusicUtils.millisecondsToTime(currentDuration);

                //Timber.d(MusicUtils.milliSecondsToTimer(totalDuration));
               // Timber.d(MusicUtils.milliSecondsToTimer(currentDuration));
                Timber.d(Integer.toString(cur_time));
                // Displaying time completed playing
               // mSongCurrentDuration.setText("" + MusicUtils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                mSeekBarProgress = (int) (MusicUtils.getProgressPercentage(currentDuration, totalDuration));
                mDiscreteSeekBar.setProgress(mSeekBarProgress);
                //mDiscreteSeekBar.setIndicatorFormatter("%04d");
               // mDiscreteSeekBar.setMax(max_time);
                // Running this thread after 100 milliseconds
                mSeekBarHandler.postDelayed(this, 100);
            }
        }
    };

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
                int currentPosition = MusicUtils.progressToTimer(seekBar.getProgress(), totalDuration);

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

    }
}