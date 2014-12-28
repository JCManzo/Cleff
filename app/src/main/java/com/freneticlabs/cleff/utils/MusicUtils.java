package com.freneticlabs.cleff.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.freneticlabs.cleff.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by jcmanzo on 12/27/14.
 */
public class MusicUtils {
    private static final HashMap<Long, Drawable> sAlbumArtCache = new HashMap<Long, Drawable>();

    public static Bitmap getArtFromFile(Context context, Uri albumArtUri) {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
            Timber.d("FOUND");
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.adele);
        } catch (IOException e) {

        }
        return  bitmap;
    }

    public static Drawable getCachedArt(Context context, long albumId) {
        Drawable albumArt = null;

        synchronized (sAlbumArtCache) {
            albumArt = sAlbumArtCache.get(albumId);
        }

        if(albumArt == null) {
            // Albumr art does not exist in cache
        }
        return albumArt;
    }
}
