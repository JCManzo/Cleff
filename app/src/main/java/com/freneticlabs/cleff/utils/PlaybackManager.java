package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.content.Intent;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;
import com.freneticlabs.cleff.models.events.MusicServiceStartedEvent;
import com.squareup.otto.Subscribe;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class PlaybackManager {
    private Context mContext;
    private CleffApp mCleffApp;

    public PlaybackManager(Context context) {
        mContext = context;
    }

    public void initPlayback() {
        mCleffApp = (CleffApp) mContext.getApplicationContext();
        // Start the service if it isn't already running

        if(!MusicService.isRunning()) {
            Timber.d("Service is not running. Starting service...");
            CleffApp.getEventBus().register(this);
            MusicService.setRunning(true);
            startService();

        } else {
            Timber.d("Service already running.");

        }
    }


    private void startService() {
        Timber.d("Starting service.");
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        CleffApp.getEventBus().post(new MusicServiceStartedEvent(mCleffApp.getService()));

    }

    @Subscribe
    public void onServiceRunningEvent(MusicServiceStartedEvent event) {
        Timber.d("Event Called");
        mCleffApp = (CleffApp) mContext.getApplicationContext();
        mCleffApp.setIsServiceRunning(true);
        mCleffApp.setService(event.mMusicService);

    }


}
