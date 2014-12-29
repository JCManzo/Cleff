package com.freneticlabs.cleff;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.freneticlabs.cleff.utils.PlaybackManager;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class CleffApp extends Application {

    private static final String TAG = CleffApp.class.getSimpleName();
    private static CleffApp sCleffApp;

    //Context.
    private Context mContext;

    //Service reference and flags.
    private MusicService mService;
    private PlaybackManager mPlaybackManager;
    private Picasso mPicasso;
    private boolean mIsServiceRunning = false;
    private static boolean mIsActivityVisible = false;
    private static SharedPreferences mSharedPreferences;

    //SharedPreferences keys.
    public static final String REPEAT_MODE = "RepeatMode";
    public static final String SERVICE_RUNNING = "ServiceRunning";
    public static final String SHUFFLE_ON = "ShuffleOn";
    public static final String PLAYER_ACTIONS_EXPANDED = "PlayerActionsExpanded";
    public static final String FIRST_RUN = "FirstRun";


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

    public PlaybackManager getPlaybackManager() {
        return mPlaybackManager;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public Picasso getPicasso() {
        return mPicasso;
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



        mContext = getApplicationContext();
        mPicasso = new Picasso.Builder(mContext).build();
        mPlaybackManager = new PlaybackManager(this.getApplicationContext());
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
