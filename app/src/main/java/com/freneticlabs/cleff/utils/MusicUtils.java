package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jcmanzo on 12/27/14.
 */
public class MusicUtils {
    public ArrayList<Song> getAllSongsFromAlbum(Context context, long albumId) {
        ArrayList<Song> albumSongs = new ArrayList<>();
        ArrayList<Song> songs = MusicLibrary.get(context).getSongs();

        for (Song song : songs) {
            if(song.getAlbumID() == albumId) {
                albumSongs.add(song);
            }
        }

        return songs;
    }
}
