package com.freneticlabs.cleff.models.events;

import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

/**
 * Created by jcmanzo on 8/4/15.
 */
public class MusicDataChangedEvent {
    public ArrayList<Song> songs;

    public MusicDataChangedEvent(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
