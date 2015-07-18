package com.freneticlabs.cleff;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.freneticlabs.cleff.utils.MusicServiceManager;
import com.squareup.otto.Bus;

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
    private static MusicServiceManager mMusicServiceManager;
    private static SharedPreferences mPreferences;

    private static final Bus mEventBus = new Bus();
    private boolean mIsServiceRunning = false;
    private static boolean mIsActivityVisible = false;

    public static final String ART_WORK_PATH = "content://media/external/audio/albumart";

    //SharedPreferences keys.
    public static final String PREF_FIRST_RUN = "cleff_first_run";
    public static final String PREFS_CLEF_FILE = "clef_app_prefs";

    public static final String REPEAT_MODE = "RepeatMode";
    public static final String SERVICE_RUNNING = "ServiceRunning";
    public static final String SHUFFLE_ON = "ShuffleOn";
    public static final String MUSIC_PLAYING = "MusicPlaying";
    public static final String MUSIC_PAUSED = "MusicPaused";
    public static final String MUSIC_IDLE = "MusicIdle";

    public static final String LAST_PLAYED_SONG = "LastPlayedSong";

    // Keys used in SongsFragment
    public static final String PLAYER_ACTIONS_EXPANDED = "PlayerActionsExpanded";
    public static final String LAST_SELECTED_ITEM = "SongListSelectedItem";
    public static final String LAST_VIEWED_ITEM = "LastViewedItem";
    public static final String LAST_VIEWED_OFFSET = "LastViewedOffset";

    private boolean mFirstRun = true;

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

    public boolean isFirstRun() {
        return mPreferences.getBoolean(PREF_FIRST_RUN, true);
    }

    public void setAsRunned() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(PREF_FIRST_RUN, false);
        editor.commit();
    }

    public MusicServiceManager getPlaybackManager() {
        return mMusicServiceManager;
    }

    public SharedPreferences getAppPreferences() {
        return mPreferences;
    }


    public static Bus getEventBus() {
        return mEventBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }


        mContext = getApplicationContext();
        mPreferences = mContext.getSharedPreferences(PREFS_CLEF_FILE, 0);
        mMusicServiceManager = new MusicServiceManager(mContext);

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
