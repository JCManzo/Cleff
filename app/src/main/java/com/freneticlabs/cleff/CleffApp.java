package com.freneticlabs.cleff;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.utils.MusicServiceManager;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class CleffApp extends Application {

    private static final String TAG = CleffApp.class.getSimpleName();

    //Context.
    private Context mContext;

    private static CleffApp sCleffApp;
    private static MusicService mService;
    private static MusicServiceManager mPlaybackManager;
    private static SharedPreferences mSharedPreferences;

    private static final Bus mEventBus = new Bus();
    private boolean mIsServiceRunning = false;
    private static boolean mIsActivityVisible = false;

    private ArrayList<Song> mSongsList;

    //SharedPreferences keys.
    public static final String REPEAT_MODE = "RepeatMode";
    public static final String SERVICE_RUNNING = "ServiceRunning";
    public static final String SHUFFLE_ON = "ShuffleOn";
    public static final String FIRST_RUN = "FirstRun";
    public static final String LAST_PLAYED_SONG = "LastPlayedSong";

    // Keys used in SongsFragment
    public static final String PLAYER_ACTIONS_EXPANDED = "PlayerActionsExpanded";
    public static final String LAST_SELECTED_ITEM = "SongListSelectedItem";
    public static final String LAST_VIEWED_ITEM = "LastViewedItem";
    public static final String LAST_VIEWED_OFFSET = "LastViewedOffset";

    public CleffApp() {
        sCleffApp = this;
    }

    public static CleffApp getCleffApp() {
        return sCleffApp;
    }

    public void setService(MusicService service) {
        mService = service;
    }

    public MusicService getService() {
        return mService;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean running) {
        mIsServiceRunning = running;
    }

    public static boolean isActivityVisible() {
        return mIsActivityVisible;
    }

    public static void activityResumed() {
        mIsActivityVisible = true;
    }

    public static void activityPaused() {
        mIsActivityVisible = false;
    }

    public MusicServiceManager getPlaybackManager() {
        return mPlaybackManager;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }


    public static Bus getEventBus() {
        return mEventBus;
    }

    public ArrayList<Song> getSongList() {
        return mSongsList;
    }

    public void addSong(Song song) {
        mSongsList.add(song);
    }

    public Song getSong(String id) {
        for(Song song : getSongList()) {
            if(song.getId().equals(id)) {
                return song;
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = this.getSharedPreferences("com.freneticlabs.cleff", Context.MODE_PRIVATE);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }


        mSongsList = new ArrayList<>();
        mContext = getApplicationContext();
        mPlaybackManager = new MusicServiceManager(mContext);
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
