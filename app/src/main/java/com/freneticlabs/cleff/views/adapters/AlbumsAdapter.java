package com.freneticlabs.cleff.views.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by jcmanzo on 12/28/14.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private ArrayList<Album> mAlbumList;
    private final Context mContext;
    private RecyclerView mRecyclerView;

    public AlbumsAdapter(Context context, RecyclerView recyclerView, ArrayList<Album> albums) {
        mContext = context;
        mRecyclerView = recyclerView;
        mAlbumList = albums;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.grid_album_title) TextView album_title;
        @Bind(R.id.grid_album_artist) TextView album_artist;
        @Bind(R.id.grid_album_image) ImageView artwork;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //get song using position
        final Album album = mAlbumList.get(position);

        //get title and artist strings
        holder.album_title.setText(mAlbumList.get(position).getAlbumName());
        holder.album_artist.setText(mAlbumList.get(position).getAlbumArtist());

        Uri uri = ContentUris.withAppendedId(Uri.parse(CleffApp.ART_WORK_PATH), album.getId());
        Picasso.with(mContext)
                .load(uri)
                .resize(300,300)
                .placeholder(R.drawable.adele)
                .into(holder.artwork);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }
}
