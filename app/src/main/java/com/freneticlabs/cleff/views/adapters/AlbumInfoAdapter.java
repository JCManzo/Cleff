package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jcmanzo on 8/1/15.
 */
public class AlbumInfoAdapter extends RecyclerView.Adapter<AlbumInfoAdapter.AlbumInfoVH> {
    private ArrayList<Song> mAlbumSongs;
    private Context mContext;

    public AlbumInfoAdapter() {
    }

    public AlbumInfoAdapter(Context context, ArrayList<Song> albumSongs) {
        mContext = context;
        mAlbumSongs = albumSongs;
    }

    public static class AlbumInfoVH extends RecyclerView.ViewHolder {
        @Bind(R.id.album_info_song_title) TextView songTitle;
        @Bind(R.id.album_info_song_number) TextView songNumber;

        public AlbumInfoVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    @Override
    public void onBindViewHolder(AlbumInfoVH holder, int position) {
        holder.songTitle.setText(mAlbumSongs.get(position).getTitle());
        holder.songNumber.setText(Integer.toString(position+1));

    }

    @Override
    public AlbumInfoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_info, parent, false);
        return new AlbumInfoVH(view);
    }

    @Override
    public int getItemCount() {
        return mAlbumSongs.size();
    }
}
