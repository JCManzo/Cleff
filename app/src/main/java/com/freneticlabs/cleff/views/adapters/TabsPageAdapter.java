package com.freneticlabs.cleff.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.freneticlabs.cleff.fragments.SongListFragment;

/**
 * Created by jcmanzo on 12/15/14.
 */
public class TabsPageAdapter extends FragmentPagerAdapter {

    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    private final String[] TITLES = { "Songs", "Albums", "Artists",
            "Genres" };
    @Override
    public Fragment getItem(int index) {
        switch (index + 1) {
            case 1:
                return new SongListFragment();
            /*case 2:
                return new AlbumsFragment();
            case 3:
                return new ArtistsFragment();
            case 4:
                return new GenresFragment();*/
            default:
                return new SongListFragment();
        }
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