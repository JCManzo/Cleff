package com.freneticlabs.cleff;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class CleffApplication extends Application {
    
    private static final String TAG = CleffApplication.class.getSimpleName();
    private static CleffApplication sCleffApplication;
    //Context.
    private Context mContext;

    //Service reference and flags.
    private MusicService mService;
    private PlaybackManager mPlaybackManager;

    private boolean mIsServiceRunning = false;

    public CleffApplication() {
        sCleffApplication = this;
    }

    public static CleffApplication getCleffApplication() {
        return sCleffApplication;
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

    public PlaybackManager getPlaybackManager() {
        return mPlaybackManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Application Created");
        mContext = getApplicationContext();
        mPlaybackManager = new PlaybackManager(this.getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "Application Terminated");

    }
}
