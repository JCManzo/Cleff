package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.freneticlabs.cleff.fragments.AlbumsFragment;
import com.freneticlabs.cleff.fragments.ArtistsFragment;
import com.freneticlabs.cleff.fragments.GenresFragment;
import com.freneticlabs.cleff.fragments.SongsFragment;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/15/14.
 */
public class LibraryTabs extends FragmentStatePagerAdapter {

    private Context mContext;
    final int PAGE_COUNT = 4;
    private String[] TITLES = new String[] {
            "Songs",
            "Albums",
            "Artists",
            "Genres"
    };

    public LibraryTabs(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new SongsFragment();
            case 1:
                return new AlbumsFragment();
            case 2:
                return new ArtistsFragment();
            case 3:
                return new GenresFragment();
            default:
                return new SongsFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}