package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;

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
            Log.d("CHAIN","Service is not running. Starting service...");
            CleffApp.getEventBus().register(this);
            startService();
        } else {
            Log.d("CHAIN","Service already running.");

        }
    }

    /**
     * Starts the music service
     */
    private void startService() {
        Log.d("CHAIN", "Starting Service");
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        Log.d("CHAIN", "Service set to true");
        MusicService.setRunning(true);

    }
}
