package com.freneticlabs.cleff;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jcmanzo on 12/18/14.
 */
public class PlaybackManager implements MusicService.PrepareServiceListener{
    private static final String TAG = PlaybackManager.class.getSimpleName();
    private Context mContext;
    private CleffApplication mCleffApplication;

    public PlaybackManager(Context context) {
        mContext = context;
    }

    public void initPlayback() {
        mCleffApplication = (CleffApplication) mContext.getApplicationContext();
        // Start the service if it isn't already running
        if(!mCleffApplication.isServiceRunning()) {
           startService();
        } else {
            mCleffApplication.getService()
                    .getPrepareServiceListener()
                    .onServiceRunning(mCleffApplication.getService());
        }
    }

    private void startService() {
        Log.i(TAG, "Service Started");
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);

    }

    @Override
    public void onServiceRunning(MusicService service) {
        mCleffApplication = (CleffApplication) mContext.getApplicationContext();
        mCleffApplication.setIsServiceRunning(true);
        mCleffApplication.setService(service);
        mCleffApplication.getService().setPrepareServiceListener(this);

        if(mCleffApplication.getService() == null) {
            Log.i(TAG, "SERVICE IS NULL");
        } else {
            Log.i(TAG, "SERVICE ISNOT  NULL");
        }
    }

    @Override
    public void onServiceFailed(Exception exception) {

    }
}
