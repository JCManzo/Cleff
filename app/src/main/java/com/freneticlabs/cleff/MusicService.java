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
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

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
    public static final String SERVICEMD = "com.freneticlabs.cleff.service.receiver";
    public static final String STOP_SERVICE = "com.freneticlabs.cleff.STOP_SERVICE";

    public static final int UPDATE_PLAYING = -1;
    private static int NOTIFICATION_ID = 579; // just a number

    private NotificationCompat.Builder mNotificationBuilder;
    public static final int mNotificationId = 1080;


    private ArrayList<Song> mSongs;
    private CleffApp mCleffApp;

    private boolean mIsPlaying = false;
    private boolean mIsPaused = false;
    private boolean mShuffle = false;
    private boolean mRepeat = false;
    private Random mRandom;

    // Keep track of the current song position in the Array
    private int mCurrentSongPosition;

    private int mPreviousSongPosition;

    //PrepareServiceListener instance.
    private PrepareServiceListener mPrepareServiceListener;

    /**
     * Starts playing the current song in the playing queue.
     */

    public  MusicService() {

    }

    /**
     * Public interface that provides access to
     * major events during the service startup
     * process.
     *
     */
    public interface PrepareServiceListener {

        /**
         * Called when the service is up and running.
         */
        public void onServiceRunning(MusicService service);

        /**
         * Called when the service failed to start.
         * Also returns the failure reason via the exception
         * parameter.
         */
        public void onServiceFailed(Exception exception);

    }

    /**
     * Returns an instance of the PrepareServiceListener.
     */
    public PrepareServiceListener getPrepareServiceListener() {
        return mPrepareServiceListener;
    }

    /**
     * Sets the mPrepareServiceListener object.
     */
    public void setPrepareServiceListener(PrepareServiceListener listener) {
        mPrepareServiceListener = listener;
    }

    @Override
    public void onCreate() {
        // Create the service
        super.onCreate();
        Timber.d("onCreate()");
        mRandom = new Random();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy()");
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Timber.d("onStartCommand()");

        mContext = getApplicationContext();
        mCurrentSongPosition = 0;
        mService = this;
        mSongs = MusicLibrary.get(getApplicationContext()).getSongs();


        initMediaPlayer();
        initService();

        // Get audiofocus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Timber.e("Could not get audio focus");
            mService.stopSelf();
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     *  Initialize the service listeners.
     */
    void initService() {
        mCleffApp = (CleffApp) getApplicationContext();
        mCleffApp.setService(this);

        //The service has been successfully started.
        setPrepareServiceListener(mCleffApp.getPlaybackManager());
        getPrepareServiceListener().onServiceRunning((MusicService) mService);
    }

    /**
     * Initialize the Mediaplayer
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
            Timber.e("Error setting wake mode in initMusicPLayer()");
            mMediaPlayer = new MediaPlayer();
        }
    }

    public void setSong(int songPosition){
        mCurrentSongPosition = songPosition;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public boolean isPaused() {
        return mIsPaused;
    }
    /**
     * Play song that was set by setSong()
     */
    public void playSong() {
        if (mMediaPlayer == null) initMediaPlayer();

        Song currentSong = mSongs.get(mCurrentSongPosition);
        Timber.d("Playing song: " + currentSong.getTitle());

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.
                EXTERNAL_CONTENT_URI, currentSong.getID());
        Timber.d(trackUri.toString());
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();
            mIsPlaying = true;
            mIsPaused = false;

        } catch (Exception e) {
            Timber.d("Error setting the data source", e);
        }

       // publishResults(playSong, UPDATE_PLAYING);
    }

    /**
     * Stop the current playing song and the service.
     *
     */
    public void stopPlayer() {
        if (mIsPlaying) {
            mIsPlaying = false;
            if (mMediaPlayer != null) {
                Timber.d("Stopping player.");
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            stopForeground(true);
        }
    }

    /**
     * Plays the next song in the MusicLibrary list.
     * If mShuffle is true, a random song is played.
     */
    public void playNext(){
        if(mShuffle) {
            int newSong = mCurrentSongPosition;
            while (newSong == mCurrentSongPosition) {
                newSong = mRandom.nextInt(mSongs.size());
            }
            mCurrentSongPosition = newSong;
        } else {
            mCurrentSongPosition++;
            if (mCurrentSongPosition >= mSongs.size()) {
                mCurrentSongPosition = 0;
            }
        }

        if(mIsPlaying) {
            playSong();
        }
    }

    /**
     * Plays the previous song in the MusicLibrary list.
     *
     */
    public void playPrevious() {
        mCurrentSongPosition--;
        if(mCurrentSongPosition < 0) {
            mCurrentSongPosition = mSongs.size() - 1;
        }

        if(mIsPlaying) {
            playSong();
        }
    }

    public void pausePlayer() {
        if(mIsPlaying) {
            mMediaPlayer.pause();
            mIsPaused = true;
            mIsPlaying = false;
        }
    }
    /**
     * Builds and updates the notification.
     * @return Notification
     */
    public Notification updateNotification() {
        mNotificationBuilder = new NotificationCompat.Builder(mContext);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher);

        Song currentSong = mSongs.get(mCurrentSongPosition);
        //Grab the notification layouts.
        RemoteViews notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_layout);

        // Set up the notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sendBroadcast(intent);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(MusicService.STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, stopServiceIntent, 0);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationView.setOnClickPendingIntent(R.id.notification_base, stopServicePendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(currentSong.getTitle())
                .setOngoing(true)
                .setContentTitle("Playing:")
                .setContent(notificationView)
                .setContentText(currentSong.getTitle())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = Notification.FLAG_FOREGROUND_SERVICE |
                Notification.FLAG_NO_CLEAR |
                Notification.FLAG_ONGOING_EVENT;

        return notification;
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
               /* if (mMediaPlayer.mmIsPlaying()) mMediaPlayer.stop();
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
        mediaPlayer.start();
        Timber.d("onPrepared()");
        Notification notification = updateNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Timber.e("ERROR in onError()" + Integer.toString(i) + " " + Integer.toString(i2));
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Timber.d("Song completed.");
        if(mediaPlayer.getCurrentPosition() > 0) {
            playNext();
        }
    }
}
