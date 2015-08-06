package com.freneticlabs.cleff.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.AlbumDetailFragment;
import com.freneticlabs.cleff.fragments.BuildLibraryTaskFragment;
import com.freneticlabs.cleff.fragments.LibraryPagerFragment;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.events.AlbumInfoSelectedEvent;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements
        BuildLibraryTaskFragment.BuildLibraryTaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "build_library_task_fragment";
    private static final String TAG_LIBRARY_FRAGMENT = "show_library_fragment";
    private CleffApp mCleffApp;
    private BuildLibraryTaskFragment mBuildLibraryTaskFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Injected Views
     */
    // @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mCleffApp = (CleffApp) getApplication();
        mTitle = getTitle();

        setUpMainView();
        setUpNavigationView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CleffApp.getEventBus().unregister(this);
        CleffApp.activityPaused();
        MusicLibrary.get(this).saveLibrary();
    }

    @Override
    public void onResume() {
        super.onResume();
        CleffApp.getEventBus().register(this);
        mCleffApp.getPlaybackManager().initPlayback();
        CleffApp.activityResumed();
        MusicLibrary.get(this).loadLibrary();
    }

    public void setUpMainView() {
        if (mCleffApp.isFirstRun()) {
            startLibraryScan();
        } else {
            showLibraryFragment();
        }
    }

    public void setUpNavigationView() {
        // Set up the drawer.
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_drawer_library:
                        break;
                    case R.id.nav_drawer_favorites:
                        mTitle = getString(R.string.nav_drawer_favorite_title);
                        setTitle(mTitle);
                        Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_drawer_queue:
                        break;
                    case R.id.nav_drawer_recently_added:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Start up the fragment that builds the music library.
     */
    private void startLibraryScan() {
        mCleffApp.setAsFirstInstall();

        mBuildLibraryTaskFragment = (BuildLibraryTaskFragment) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);

        if (mBuildLibraryTaskFragment == null) {
            mBuildLibraryTaskFragment = new BuildLibraryTaskFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, mBuildLibraryTaskFragment, TAG_TASK_FRAGMENT)
                    .commit();
            Timber.d("Building library.");

        }


    }

    /**
     * Displays the main library view fragment
     */
    private void showLibraryFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, new LibraryPagerFragment())
                .commit();
        Timber.d("Showing mainview");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // if (!mDrawerLayout.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);
        // restoreActionBar();
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
                startLibraryScan();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {
        mCleffApp.setAsRunned();
        showLibraryFragment();
    }

    @Subscribe
    public void onAlbumInfoSelected(AlbumInfoSelectedEvent album) {
        Timber.d("Album Clicked");
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailFragment.ALBUM_INFO_ID, album.album_id);
        intent.putExtra(AlbumDetailFragment.ALBUM_INFO_NAME, album.album_name);

        startActivity(intent);
    }
}
