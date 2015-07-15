package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
    private Context mContext;
    private ArrayList<Song> mSongs;

    public SongsAdapter() {

    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView songArtist;

        SongViewHolder(View view) {
            super(view);
            songTitle = (TextView)view.findViewById(R.id.recyler_view_song_title);
            songArtist = (TextView)view.findViewById(R.id.recycler_view_song_artist);
        }
    }
    // Remember the last item shown on screen
    private int lastPosition = -1;


    public SongsAdapter(Context context, ArrayList<Song> songs) {
        mContext = context;
        mSongs = songs;

    }

    @Override
    public void onBindViewHolder(SongViewHolder songViewHolder, int position) {
        songViewHolder.songTitle.setText(mSongs.get(position).getTitle());
        songViewHolder.songArtist.setText(mSongs.get(position).getArtist());

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_list_row_item, viewGroup, false);
        SongViewHolder songViewHolder = new SongViewHolder(view);

        setAnimation(view, position);

        return songViewHolder;
    }
/*
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/

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
