package com.freneticlabs.cleff.models.events;

import com.freneticlabs.cleff.models.Album;

/**
 * Created by jcmanzo on 8/1/15.
 */
public class AlbumInfoSelectedEvent {
    public final int album_id;
    public final String album_name;

    public AlbumInfoSelectedEvent(Album album) {
        this.album_id = album.getId();
        this.album_name = album.getAlbumName();
    }
}
