package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;

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
            Log.i(TAG, "Starting Service");
           startService();
        } else {
            Log.i(TAG, "Service Already Exists");
            mCleffApp.getService()
                    .getPrepareServiceListener()
                    .onServiceRunning(mCleffApp.getService());
        }
    }

    private void startService() {
        Log.i(TAG, "Service Started");
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
