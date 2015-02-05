package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends ArrayAdapter<Song> {
    private final Context mContext;
    private ArrayList<Song> mSongs;
    private LayoutInflater mSongInflater;

    // Remember the last item shown on screen
    private int lastPosition = -1;


    public SongsAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
        mContext = context;
        mSongs = songs;
        mSongInflater = LayoutInflater.from(context);

    }

    @Override
    public Song getItem(int position) {
        return mSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            //map to song layout
            view = mSongInflater.inflate(R.layout.song_list_row_item, parent, false);

            // Set up up the ViewHolder
            holder = new ViewHolder();
            holder.title = (TextView)view.findViewById(R.id.list_song_title);
            holder.artist = (TextView)view.findViewById(R.id.list_song_artist);

            view.setTag(holder);

        } else {
            // View already exists
            holder = (ViewHolder)view.getTag();
        }

        //get song using position
        final Song song = mSongs.get(position);
        //get title and artist strings
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());

        return view;
    }

    /**
     * ViewHolder pattern allows ListView to scroll more smoothly
     */
    private static class ViewHolder {
        TextView title;
        TextView artist;
    }

    /**
     * Only ViewHolderItems that have not previously appeared
     * on the screen are animated.
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
