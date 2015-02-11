package com.freneticlabs.cleff.models.events;

/**
 * Created by jcmanzo on 2/10/15.
 */
public class SongSelectedEvent {
    public final int songPosition;

    public SongSelectedEvent(int songPositionSelected) {
        songPosition = songPositionSelected;
    }
}
