package com.freneticlabs.cleff.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.fragments.BuildLibraryTaskFragment;
import com.freneticlabs.cleff.models.MusicLibrary;

import timber.log.Timber;

/**
 * Created by jcmanzo on 7/18/15.
 */
public class BuildingLibraryProgressActivity extends AppCompatActivity implements
        BuildLibraryTaskFragment.BuildLibraryTaskCallbacks {
    private static final String TAG_TASK_FRAGMENT = "build_library_task_fragment";

    private CleffApp mCleffApp;
    private SharedPreferences mPreferences;

    /**
     * Fragment managing the initial scan and build of the music library.
     */
    private BuildLibraryTaskFragment mBuildLibraryTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCleffApp = (CleffApp)getApplication();
        mPreferences = mCleffApp.getAppPreferences();

        FragmentManager fm = getSupportFragmentManager();
        mBuildLibraryTaskFragment = (BuildLibraryTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // Checks to see if the music library must be built if this is the
        // first time the user has run the app.
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mBuildLibraryTaskFragment == null && mCleffApp.isFirstRun()) {
            mBuildLibraryTaskFragment = new BuildLibraryTaskFragment();
            Timber.d("Building library.");

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mBuildLibraryTaskFragment)
                    .commit();

        }

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute() {
        /**
         * Set the app as runned and save the music library to disk
         */
        mCleffApp.setAsRunned();
        MusicLibrary.get(getApplicationContext()).saveLibrary();

        // Exit the activity
        finish();

    }

    @Override
    public void onCancelled() {

    }
}
