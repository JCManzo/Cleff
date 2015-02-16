package com.freneticlabs.cleff.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.PlayerFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerActivity extends ActionBarActivity {
    private ArrayList<Song> mSongs;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.player_activity_pager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.inject(this);

        // Set up the toolbar to act as an action bar
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        int songId = (int)getIntent().getSerializableExtra(PlayerFragment.EXTRA_SONG_ID);
        for (int i = 0; i < mSongs.size(); i++) {
            if(mSongs.get(i).getId() == songId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                Song song = mSongs.get(position);
                if(song.getTitle() != null) {
                    setTitle(song.getTitle());

                }
                CleffApp.getEventBus().post(new SongSelectedEvent(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



}