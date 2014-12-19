package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private ArrayList<Song> mSongList;
    private ItemClickListener mItemClickListener;


    public SongAdapter(ArrayList<Song> songList, @NonNull ItemClickListener itemClickListener) {
        mSongList = songList;

        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        public void itemClicked(Song song);
    }

    public ArrayList<Song> getValues() {
        return mSongList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View parent = LayoutInflater.from(context).inflate(R.layout.row_list_song, viewGroup, false);

        return ViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Song song = mSongList.get(position);
        viewHolder.setTitle(song.getTitle());
        viewHolder.setArtist(song.getArtist());
        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;

        @InjectView(R.id.list_song_title) TextView title;
        @InjectView(R.id.list_song_artist) TextView artist;

        public static ViewHolder newInstance(View view) {
            return new ViewHolder(view);
        }

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            this.parent = view;
        }

        public void setTitle(CharSequence text) {
            title.setText(text);
        }

        public void setArtist(CharSequence text) {
            artist.setText(text);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }
}
