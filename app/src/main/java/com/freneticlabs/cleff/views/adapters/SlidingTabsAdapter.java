package com.freneticlabs.cleff.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.freneticlabs.cleff.fragments.AlbumsFragment;
import com.freneticlabs.cleff.fragments.ArtistsFragment;
import com.freneticlabs.cleff.fragments.GenresFragment;
import com.freneticlabs.cleff.fragments.SongsFragment;

/**
 * Created by jcmanzo on 12/15/14.
 */
public class SlidingTabsAdapter extends FragmentPagerAdapter {

    public SlidingTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    private final String[] TITLES = { "Songs", "Albums", "Artists",
            "Genres" };

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = null;

        if(index == 0) {
            fragment = new SongsFragment();
        } else if(index == 1) {
            fragment = new AlbumsFragment();

        } else if(index == 2) {
            fragment = new ArtistsFragment();

        } else if(index == 3) {
            fragment = new GenresFragment();

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}