package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Song> mSongs;
    private RecyclerView mRecyclerView;
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;


    // Remember the last item shown on screen
    private int lastPosition = -1;

    public SongsAdapter() {

    }

    public SongsAdapter(Context context, RecyclerView recyclerView, ArrayList<Song> songs) {
        mContext = context;
        mRecyclerView = recyclerView;
        mSongs = songs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.recyler_view_song_title) TextView songTitle;
        @Bind(R.id.recycler_view_song_artist) TextView songArtist;
        @Bind(R.id.song_layout_container ) RelativeLayout relativeContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder songViewHolder, final int position) {
        songViewHolder.songTitle.setText(mSongs.get(position).getTitle());
        songViewHolder.songArtist.setText(mSongs.get(position).getArtist());

        setAnimation(songViewHolder.relativeContainer, position);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    public void sort(Comparator<? super Song> comparator) {
        Timber.d("Sorting..");
        Collections.sort(mSongs, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }

    /*@Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if(mSongs.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }*/

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
