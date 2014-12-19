package com.freneticlabs.cleff.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.freneticlabs.cleff.CleffApplication;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class StopServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Stop the service.
        CleffApplication app = (CleffApplication) context.getApplicationContext();
        app.getService().stopSelf();
        app.setIsServiceRunning(false);

    }
}
