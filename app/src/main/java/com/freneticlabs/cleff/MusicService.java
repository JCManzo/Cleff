package com.freneticlabs.cleff;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

/**
 * Created by jcmanzo on 12/17/14.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private Context mContext;
    private Service mService;
    public static String RESULT = "result";
    public static final String SERVICEMD = "com.jcmanzo.cleff.service.receiver";
    public static final int UPDATE_PLAYING = -1;
    private static int NOTIFICATION_ID = 579; // just a number

    //Handler object.
    private Handler mHandler;

    private static final String TAG = MusicService.class.getSimpleName();
    private static final String ACTION_PLAY = "com.cleff.action.PLAY";

    // Keep track of the current song position in the Array
    private Song mCurrentSong;
    private ArrayList<Song> mSongs;
    private boolean      isPlaying = false;
    private final IBinder mBinder = new LocalBinder();

    /**
     * Starts playing the current song in the playing queue.
     */

    public  MusicService() {

    }

    @Override
    public void onCreate() {
        // Create the service
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }



    public class LocalBinder extends Binder {
        MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     *  Unbinds the service from the client.
     *
     * @param intent
     * @return true
     */
    @Override
    public boolean onUnbind(Intent intent){
        if(mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        mContext = getApplicationContext();
        mService = this;
        mHandler = new Handler();
        mSongs = MusicLibrary.get(getApplicationContext()).getSongs();

        initMediaPlayer();

        // Get audiofocus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "Could not get audio focus");
            mService.stopSelf();
        }


        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     * Initializes the mediaplayer
     */
    void initMediaPlayer() {
        if (mMediaPlayer  != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        try {
            mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);

        } catch (Exception e) {
            Log.e(TAG, "Error setting wake mode in initMusicPLayer()");
            mMediaPlayer = new MediaPlayer();
        }
    }

    public void setSong(Song song){
        mCurrentSong = song;
    }


    /**
     * Plays the song that was set by setSong()
     */
    public void playSong() {
        // Set the URI
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.
                EXTERNAL_CONTENT_URI, mCurrentSong.getID());

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();
            isPlaying = true;

        } catch (Exception e) {
            Log.e(TAG, "Error setting the data source", e);
        }

       // publishResults(playSong, UPDATE_PLAYING);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
               /* if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;*/
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mMediaPlayer.start();

        // Set up the notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sendBroadcast(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(mCurrentSong.getTitle())
                .setOngoing(true)
                .setContentTitle("Playing:")
                .setContentText(mCurrentSong.getTitle())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Song completed.");
    }


}
