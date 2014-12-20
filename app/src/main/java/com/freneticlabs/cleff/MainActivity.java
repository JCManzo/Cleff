package com.freneticlabs.cleff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.freneticlabs.cleff.fragments.BuildLibraryTaskFragment;
import com.freneticlabs.cleff.fragments.NavigationDrawerFragment;
import com.freneticlabs.cleff.fragments.PageSlidingTabStripFragment;
import com.freneticlabs.cleff.fragments.SongsFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        BuildLibraryTaskFragment.BuildLibraryTaskCallbacks,
        SongsFragment.OnListViewSongListener{

    private static final String TAG_TASK_FRAGMENT = "build_library_task_fragment";
    public static final String PREFS_NAME = "ListnPrefsFile";
    private MusicService mService;
    private CleffApp mCleffApp;
    private boolean mBound = false;
    private Intent playIntent;
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private BuildLibraryTaskFragment mBuildLibraryTaskFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Injected Views
     */
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.build_lib_progressbar) ProgressBar mProgressBar;
    @InjectView(R.id.build_lib_container) LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Timber.d("Activity Created");
        // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        mBuildLibraryTaskFragment = (BuildLibraryTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstRun = settings.getBoolean("firstRun", false);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
       if (mBuildLibraryTaskFragment == null && !firstRun) {
            mBuildLibraryTaskFragment = new BuildLibraryTaskFragment();
            fm.beginTransaction()
                    .add(mBuildLibraryTaskFragment, TAG_TASK_FRAGMENT)
                    .commit();
           Timber.d("running buildlibrary");
           mLinearLayout.setVisibility(View.VISIBLE);

        } else if (firstRun) {
            // Library has been built. Show main fragment.
            mLinearLayout.setVisibility(View.GONE);
            fm.beginTransaction()
                    .replace(R.id.container, new PageSlidingTabStripFragment())
                    .commit();
           Timber.d("Mainview");

       }

        mCleffApp = (CleffApp)getApplication();
       mCleffApp.getPlaybackManager().initPlayback();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        MusicLibrary.get(this).saveLibrary();

    }


    @Override
    protected void onDestroy() {
        // Cancel all scheduled croutons
        super.onDestroy();

    }


    @Override
    public void OnListViewSongSelected(Song song) {
        Log.i(TAG, song.getTitle());

        // Start the service if it has been stopped.
        if(!mCleffApp.isServiceRunning()) {
            mCleffApp.getPlaybackManager().initPlayback();
            Timber.i("SERVICE NOT RUNNING");

        } else {
            Timber.i( "SERVICE IS RUNNING");
        }
        mCleffApp.getService().setSong(song);
        mCleffApp.getService().playSong();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
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
    public void onPreExecute() {
        mLinearLayout.setVisibility(View.VISIBLE);
        Log.i("TASK", "Main onPreExecute");
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

        mLinearLayout.setVisibility(View.GONE);
        Log.i("TASK", "Main onPostExecute");
        // Library has been created. No need to run this fragment again.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstRun", true);

        // Commit the edits!
        editor.apply();

        // Display the library in a listview
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new PageSlidingTabStripFragment())
                .commit();
    }
}
