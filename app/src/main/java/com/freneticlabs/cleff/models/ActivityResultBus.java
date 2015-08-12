package com.freneticlabs.cleff.models;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by jcmanzo on 8/10/15.
 */
public class ActivityResultBus extends Bus{

    private static ActivityResultBus sActivityResultBus;

    public static ActivityResultBus getActivityResultBus() {
        if (sActivityResultBus == null) {
            sActivityResultBus = new ActivityResultBus();
        }

        return sActivityResultBus;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void postQueue(final Object o) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ActivityResultBus.getActivityResultBus().post(o);
            }
        });
    }
}
