package com.freneticlabs.cleff.models.events;

import com.freneticlabs.cleff.models.Song;

/**
 * Created by jcmanzo on 2/10/15.
 */
public class SongSelectedEvent {
    public final int songPosition;
    public final Song song;

    public SongSelectedEvent(Song song, int songPositionSelected) {
        songPosition = songPositionSelected;
        this.song = song;
    }
}
