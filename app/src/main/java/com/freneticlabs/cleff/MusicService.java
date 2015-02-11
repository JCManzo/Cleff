package com.freneticlabs.cleff;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.freneticlabs.cleff.activities.MainActivity;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.models.events.MusicStateChangeEvent;
import com.freneticlabs.cleff.models.events.SongSelectedEvent;
import com.squareup.otto.Subscribe;

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
    private SharedPreferences mSettings;

    public static final String STOP_SERVICE = "com.freneticlabs.cleff.STOP_SERVICE";

    private static int NOTIFICATION_ID = 579; // just a number

    private NotificationCompat.Builder mNotificationBuilder;

    public enum PlayerState {
        IDLE, PAUSED, PLAYING
    }

    PlayerState mPlayerSate = PlayerState.IDLE;

    private boolean mShuffle = false;
    private boolean mRepeat = false;
    private static boolean mRunning = false;

    private Random mRandom;
    private ArrayList<Song> mSongs;
    private CleffApp mCleffApp;

    // Keep track of the current song position in the Array
    private int mCurrentSongPosition = 0;
    private int mLastSongPosition = -1;
    private int mPreviousSongPosition;

    /**
     * Starts playing the current song in the playing queue.
     */
    public  MusicService() {

    }


    @Override
    public void onCreate() {
        // Create the service
        super.onCreate();
        mSongs = MusicLibrary.get(mContext).getSongs();

        CleffApp.getEventBus().register(this);
        Timber.d("onCreate()");
        mRandom = new Random();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CleffApp.getEventBus().unregister(this);

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
        Log.d("CHAIN", "onStartCommand()");

        mContext = getApplicationContext();
        mCleffApp = (CleffApp) getApplicationContext();
        mSettings = mCleffApp.getSharedPreferences();

        mCleffApp.setService(this);
        initMediaPlayer();

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

    public static boolean isRunning() {
        return mRunning;
    }

    public static void setRunning(boolean running) {
        mRunning = running;
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

    public int getCurrentSongPosition(){
        return mCurrentSongPosition;
    }

    public void setShuffle() {
        if(mShuffle) mShuffle=false;
        else mShuffle=true;
    }

    public boolean isShuffle() {
        return mShuffle;
    }

    public void setRepeat() {
        if(mRepeat) mRepeat=false;
        else mRepeat=true;
    }

    public boolean isRepeat() {
        return mRepeat;
    }

    public PlayerState getPlayerSate() {
        return mPlayerSate;
    }

    public PlayerState isPaused() {
        return mPlayerSate;
    }


    @Subscribe
    public void onSongSelected(SongSelectedEvent event) {
        mCurrentSongPosition = event.songPosition;
        int lastPlayedSong = mSettings.getInt(CleffApp.LAST_PLAYED_SONG, mLastSongPosition);

        if (getPlayerSate().equals(PlayerState.IDLE)) {
            play();
        } else if (getPlayerSate().equals(PlayerState.PLAYING)) {
            if(lastPlayedSong == mCurrentSongPosition) {
                pause();
            } else {
                play();
            }
        } else if (getPlayerSate().equals(PlayerState.PAUSED)) {
            if(lastPlayedSong == mCurrentSongPosition) {
                resume();
            } else {
                play();
            }
        }

    }

    /**
     * Plays the current song in mCurrentSongPosition
     **/
    public void play() {
        Timber.d("Play Called");
        if (mMediaPlayer == null) initMediaPlayer();

        Song currentSong = mSongs.get(mCurrentSongPosition);

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.getId());
        Timber.d(trackUri.toString());
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();
            mPlayerSate = PlayerState.PLAYING;

            CleffApp.getEventBus().post(new MusicStateChangeEvent(CleffApp.MUSIC_PLAYING));

            // Saves the position of the previously played song to a file.
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(CleffApp.LAST_PLAYED_SONG, mLastSongPosition);

            editor.apply();

        } catch (Exception e) {
            Timber.d("Error setting the data source", e);
        }

       // publishResults(playSong, UPDATE_PLAYING);
    }

    /**
     * Pauses the player if it is in a playing state.
     */
    public void pause() {
        Timber.d("Paused Called");
        if(mPlayerSate.equals(PlayerState.PLAYING)) {
            mMediaPlayer.pause();
            mPlayerSate = PlayerState.PAUSED;
            CleffApp.getEventBus().post(new MusicStateChangeEvent(CleffApp.MUSIC_PAUSED));

        }
    }
    /**
     * Stop the current playing song and the service.
     *
     */
    public void stop() {
        Timber.d("Stop Called");

        if (mPlayerSate.equals(PlayerState.PLAYING) || mPlayerSate.equals(PlayerState.PAUSED)) {
            //mIsPlaying = false;
            mPlayerSate = PlayerState.IDLE;
            CleffApp.getEventBus().post(new MusicStateChangeEvent(CleffApp.MUSIC_IDLE));

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

        play();
    }

    /**
     * Plays the previous song in the MusicLibrary list.
     */
    public void playPrevious() {
        mCurrentSongPosition--;
        if(mCurrentSongPosition < 0) {
            mCurrentSongPosition = mSongs.size() - 1;
        }

        play();
    }

    /**
     * Play and pause button is one entity. Toggle back and forth
     * depending on the player state.
     */
    public void togglePlayer() {
        if(mPlayerSate.equals(PlayerState.PLAYING)) {
            pause();
        } else if(mPlayerSate.equals(PlayerState.PAUSED)) {
            resume();
        } else if(mPlayerSate.equals(PlayerState.IDLE)) {
            mMediaPlayer.reset();
            mPlayerSate = PlayerState.PLAYING;
            play();

        }
    }

    /**
     * Resumes the player if it was previously paused.
     */
    public void resume() {
        Timber.d("Resume Called");
        mMediaPlayer.start();
        mPlayerSate = PlayerState.PLAYING;
        CleffApp.getEventBus().post(new MusicStateChangeEvent(CleffApp.MUSIC_PLAYING));

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
        /*switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
               *//* if (mMediaPlayer.mmIsPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;*//*
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
        }*/
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Notification notification = updateNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Timber.e("Error in onError()" + Integer.toString(i) + " " + Integer.toString(i2));
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Timber.d("Song completed.");
        if(mediaPlayer.getCurrentPosition() > 0) {
            if(mRepeat) {
            // play();
            } else {
                playNext();
            }
        }
    }
}
