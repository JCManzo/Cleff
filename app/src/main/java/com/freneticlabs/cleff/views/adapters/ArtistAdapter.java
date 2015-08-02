package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Artist;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jcmanzo on 7/11/15.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {
    private ArrayList<Artist> mArtistList;
    private final Context mContext;
    private LayoutInflater mArtistsInflater;
    private String mSongCount;
    private CleffApp mCleffApp;

    public ArtistAdapter(Context context, ArrayList<Artist> artists) {
        super(context, 0, artists);
        mContext = context;
        mArtistList = artists;
        mCleffApp = (CleffApp) mContext.getApplicationContext();
        mArtistsInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        @Bind(R.id.grid_artist_title)
        TextView title;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return mArtistList.size();
    }

    @Override
    public Artist getItem(int position) {
        return mArtistList.get(position);
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
            view = mArtistsInflater.inflate(R.layout.item_artists_grid, parent, false);

            // Set up up the ViewHolder
            holder = new ViewHolder(view);
            holder.title = (TextView)view.findViewById(R.id.grid_artist_title);

            view.setTag(holder);

        } else {
            // View already exists
            holder = (ViewHolder)view.getTag();
        }

        //get song using position
        final Artist Artist = mArtistList.get(position);
        //get title and artist strings
        holder.title.setText(Artist.getArtistName());


        return view;
    }
}
