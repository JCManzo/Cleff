package com.freneticlabs.cleff.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.BuildLibraryTaskFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.views.adapters.SlidingTabsAdapter;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity implements
        BuildLibraryTaskFragment.BuildLibraryTaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "build_library_task_fragment";

    private CleffApp mCleffApp;
    private SharedPreferences mSettings;
    private boolean mFirstRun;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private BuildLibraryTaskFragment mBuildLibraryTaskFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Injected Views
     */
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.navigation_view) NavigationView mNavigationView;
    @InjectView(R.id.tabs) TabLayout mTabLayout;
    @InjectView(R.id.main_pager) ViewPager mViewPager;
    @InjectView(R.id.floating_player_actions) FloatingActionsMenu mFloatingActionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setUpToolbar();
        setUpNavigationView();
        setUpPagerAndTabs();

        mTitle = getTitle();
        mCleffApp = (CleffApp)getApplication();
        mSettings = mCleffApp.getAppPreferences();

        FragmentManager fm = getSupportFragmentManager();
        mBuildLibraryTaskFragment = (BuildLibraryTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // Checks to see if the music library must be built if this is the
        // first time the user has run the app.
        SharedPreferences settings = mCleffApp.getAppPreferences();
        mFirstRun = settings.getBoolean(CleffApp.PREF_FIRST_RUN, true);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
       if (mBuildLibraryTaskFragment == null && mFirstRun) {
            mBuildLibraryTaskFragment = new BuildLibraryTaskFragment();

           fm.beginTransaction()
                    .add(mBuildLibraryTaskFragment, TAG_TASK_FRAGMENT)
                    .commit();

           Timber.d("Building library.");
          // mLinearLayout.setVisibility(View.VISIBLE);

           SharedPreferences.Editor editor = mSettings.edit();
           editor.putInt(CleffApp.LAST_SELECTED_ITEM, -1);

           // Commit the edits!
           editor.apply();

        } else if (!mFirstRun) {
            // Library has been built. Show main fragment.
           // mLinearLayout.setVisibility(View.GONE);
           /* fm.beginTransaction()
                    .replace(R.id.main_container, new SlidingTabsFragment())
                    .commit();*/
           Timber.d("Showing mainview");

       }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        CleffApp.activityPaused();
        MusicLibrary.get(this).saveLibrary();
        MusicLibrary.get(this).saveSongsToJSONFile();
    }

    @Override
    public void onResume() {
        super.onResume();

        mCleffApp.getPlaybackManager().initPlayback();
        CleffApp.activityResumed();

    }

    @Override
    protected void onDestroy() {
        // Cancel all scheduled croutons
        super.onDestroy();

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

    public void setUpToolbar() {
        // Set up the toolbar to act as ac action bar
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setUpNavigationView() {
        // Set up the drawer.
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void setUpPagerAndTabs() {
        SlidingTabsAdapter adapter = new SlidingTabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void startScanFragment() {
       /* FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.main_container, new BuildLibraryTaskFragment())
                .commit();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // if (!mDrawerLayout.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        //}
       // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.*
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_sort:
                break;
            case R.id.action_scan:
                startScanFragment();
                break;
            case R.id.action_sort_by_album:
                MusicLibrary.get(getApplicationContext()).sortSongsByAlbum();
                break;
            case R.id.action_sort_by_artist:
                MusicLibrary.get(getApplicationContext()).sortSongsByArtist();
                break;
            case R.id.action_sort_by_name:
                MusicLibrary.get(getApplicationContext()).sortSongsByTitle();
                break;
            case R.id.action_sort_by_genre:
                MusicLibrary.get(getApplicationContext()).sortSongsByGenre();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecute() {
       // mLinearLayout.setVisibility(View.VISIBLE);
        Log.i("TASK", "Main onPreExecute");
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

        //mLinearLayout.setVisibility(View.GONE);

        // Library has been created. No need to run this fragment again.
        mFirstRun = false;
        SharedPreferences settings = mCleffApp.getAppPreferences();
        settings.edit().putBoolean(CleffApp.PREF_FIRST_RUN, false).apply();

        // Display the library in a listview
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, new SlidingTabsFragment())
                .commit();*/

    }
}
