package com.freneticlabs.cleff.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.views.adapters.SlidingTabsAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {


    private CleffApp mCleffApp;
    private SharedPreferences mSettings;
    private static String mPlayerState = CleffApp.MUSIC_IDLE;

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
    @InjectView(R.id.action_play) FloatingActionButton mFloatingPlayButton;
    @InjectView(R.id.action_skip_next) FloatingActionButton mFloatingNextButton;
    @InjectView(R.id.action_skip_previous) FloatingActionButton mFloatingPreviousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mCleffApp = (CleffApp)getApplication();
        mSettings = mCleffApp.getAppPreferences();

        if (mCleffApp.isFirstRun()) {
            startLibraryScan();
        }
        setUpToolbar();
        setUpNavigationView();
        setUpPagerAndTabs();
        setUpListeners();
        updateFloatingUi();

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
        setTitle(getString(R.string.nav_drawer_library_title));
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

    private void startLibraryScan() {
        Intent intent = new Intent(this, BuildingLibraryProgressActivity.class);
        startActivity(intent);
    }

    private void setUpListeners() {
        // Set up the floating player action listeners
         mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
             @Override
             public void onMenuExpanded() {
                 Timber.d("EXPANDED");
             }

             @Override
             public void onMenuCollapsed() {
                 Timber.d("COLLAPSED");

             }
         });

        mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCleffApp.getService().togglePlayer();

            }
        });

        mFloatingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED NEXT");
                mCleffApp.getService().playNext();
            }
        });

        mFloatingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("CLICKED PREVIOUS");
                mCleffApp.getService().playPrevious();
            }
        });
    }

    private void updateFloatingUi() {
        if(mPlayerState.equals(CleffApp.MUSIC_IDLE)) {

        } else if(mPlayerState.equals(CleffApp.MUSIC_PLAYING)) {

              mFloatingPlayButton.setIcon(R.drawable.ic_orange_pause);

        } else if(mPlayerState.equals(CleffApp.MUSIC_PAUSED)) {

             mFloatingPlayButton.setIcon(R.drawable.ic_orange_play_arrow);

        }
    }

    /**
     * Called when the player global state has changed.
     * @param event is the state of the MediaPlayer
     */
    @Subscribe
    public void onMusicStateChange(MusicStateChangeEvent event) {
        mPlayerState = event.musicState;
        updateFloatingUi();
        Timber.d(mPlayerState);
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
                startLibraryScan();
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



}
