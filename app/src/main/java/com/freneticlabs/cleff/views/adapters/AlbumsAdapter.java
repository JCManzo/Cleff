package com.freneticlabs.cleff.views.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by jcmanzo on 12/28/14.
 */
public class AlbumsAdapter extends ArrayAdapter<Album> {
    private ArrayList<Album> mAlbumList;
    private final Context mContext;
    private LayoutInflater mAlbumsInflater;
    private String mSongCount;
    private CleffApp mCleffApp;
    // Remember the last item shown on screen
    private static final String ART_WORK_PATH = "content://media/external/audio/albumart";


    public AlbumsAdapter(Context context, ArrayList<Album> albums) {
        super(context, 0, albums);
        mContext = context;
        mAlbumList = albums;
        mCleffApp = (CleffApp) mContext.getApplicationContext();
        mAlbumsInflater = LayoutInflater.from(context);
        mSongCount = mCleffApp.getResources().getString(R.string.album_song_count);
    }

    static class ViewHolder {
        @InjectView(R.id.grid_album_title)
        TextView title;
        @InjectView(R.id.grid_album_num_songs)
        TextView songCount;
        @InjectView(R.id.grid_album_image)
        ImageView artwork;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public int getCount() {
        return mAlbumList.size();
    }

    @Override
    public Album getItem(int position) {
        return mAlbumList.get(position);
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
            view = mAlbumsInflater.inflate(R.layout.albums_grid_item, parent, false);

            // Set up up the ViewHolder
            holder = new ViewHolder(view);
            holder.title = (TextView)view.findViewById(R.id.grid_album_title);

            view.setTag(holder);

        } else {
            // View already exists
            holder = (ViewHolder)view.getTag();
        }

        //get song using position
        final Album album = mAlbumList.get(position);
        //get title and artist strings
        holder.title.setText(album.getAlbumName());
        holder.songCount.setText(mSongCount + Integer.toString(album.getNumOfSongs()));

        Uri uri = ContentUris.withAppendedId(Uri.parse(ART_WORK_PATH), album.getAlbumId());
        Picasso.with(mContext)
                .load(uri)
                .resize(300,300)
                .placeholder(R.drawable.adele)
                .into(holder.artwork);

        return view;
    }


}
