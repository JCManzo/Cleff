package com.freneticlabs.cleff.models.events;

import com.freneticlabs.cleff.MusicService;

/**
 * Created by jcmanzo on 1/31/15.
 */
public class MusicServiceStartedEvent {

    public final MusicService mMusicService;

    public MusicServiceStartedEvent(MusicService musicService) {
        mMusicService = musicService;
    }
}
