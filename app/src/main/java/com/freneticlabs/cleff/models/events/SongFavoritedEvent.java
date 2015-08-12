package com.freneticlabs.cleff.models.events;

/**
 * Created by jcmanzo on 8/4/15.
 */
public class SongFavoritedEvent {
    public boolean isFavorited;

    public SongFavoritedEvent(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}
