package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.content.Intent;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class PlaybackManager implements MusicService.PrepareServiceListener {
    private static final String TAG = PlaybackManager.class.getSimpleName();
    private Context mContext;
    private CleffApp mCleffApp;

    public PlaybackManager(Context context) {
        mContext = context;
    }

    public void initPlayback() {
        mCleffApp = (CleffApp) mContext.getApplicationContext();

        // Start the service if it isn't already running
        if(!mCleffApp.isServiceRunning()) {
            Timber.d("Service is not running.");
           startService();
        } else {
            Timber.d("Service already running.");
            mCleffApp.getService()
                    .getPrepareServiceListener()
                    .onServiceRunning(mCleffApp.getService());
        }
    }

    public void stopPlayback() {
        mCleffApp.getService().stopPlayer();
    }
    private void startService() {
        Timber.d("Starting service.");
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);

    }

    @Override
    public void onServiceRunning(MusicService service) {
        mCleffApp = (CleffApp) mContext.getApplicationContext();
        mCleffApp.setIsServiceRunning(true);
        mCleffApp.setService(service);
        mCleffApp.getService().setPrepareServiceListener(this);

    }

    @Override
    public void onServiceFailed(Exception exception) {

    }
}
