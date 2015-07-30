package com.freneticlabs.cleff.models.events;

/**
 * Created by jcmanzo on 2/10/15.
 */
public class MusicStateChangeEvent {
    public final String musicState;
    public final int songPos;
    public MusicStateChangeEvent(String state, int pos) {
        musicState = state;
        songPos = pos;
    }
}
