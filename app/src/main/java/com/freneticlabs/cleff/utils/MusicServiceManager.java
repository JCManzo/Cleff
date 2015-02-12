package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.content.Intent;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class MusicServiceManager {
    private Context mContext;

    public MusicServiceManager(Context context) {
        mContext = context;
    }

    /**
     * Check to see if music service is already running.
     */
    public void initPlayback() {

        if(!MusicService.isRunning()) {
            // Service is not running, start it.
            Timber.d("Service is not running. Starting service...");
            CleffApp.getEventBus().register(this);
            startService();
        } else {
            Timber.d("Service already running.");

        }
    }

    /**
     * Starts the music service
     */
    private void startService() {
        Timber.d("Starting Service");
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        MusicService.setRunning(true);

    }
}
