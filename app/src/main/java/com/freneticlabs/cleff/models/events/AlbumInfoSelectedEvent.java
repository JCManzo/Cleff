package com.freneticlabs.cleff.models.events;

/**
 * Created by jcmanzo on 8/1/15.
 */
public class AlbumInfoSelectedEvent {
    public final int album_id;

    public AlbumInfoSelectedEvent(int album_id) {
        this.album_id = album_id;
    }
}
