package com.freneticlabs.cleff.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.MusicService;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class StopServiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(MusicService.STOP_SERVICE)) {
            CleffApp app = (CleffApp) context.getApplicationContext();
            //app.getService().stopSelf();
            MusicService.setRunning(false);
            //Stop the service.
            app.getService().stopSelf();

            // Ready up the service if the main activity is still visible.
            if(CleffApp.isActivityVisible()) {
                app.getPlaybackManager().initPlayback();
            }
            Timber.d("Service was stopped.");
        }
    }
}
