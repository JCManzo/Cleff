package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;

import java.util.ArrayList;

import timber.log.Timber;


/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongAdapter extends BaseAdapter implements Filterable{
    private ArrayList<Song> mSongs;
    private ArrayList<Song> mOrigin;
    private LayoutInflater mSongInflater;
    private Context mContext;
    private static final String TAG = "SongAdapter";

    public SongAdapter(Context context){
        mContext = context;
        mSongs = MusicLibrary.get(context).getSongs();
        if(mSongs == null || mSongs.isEmpty()) {
            Timber.d("EMPTY SONGS" );
        } else {
            Timber.d("Songs NOT EMPTY"+ Integer.toString(mSongs.size()));
        }
        mSongInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSongs.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            //map to song layout
            convertView = mSongInflater.inflate(R.layout.row_list_song, parent, false);

            // Set up up the ViewHolder
            holder = new ViewHolder();
            holder.songName = (TextView)convertView.findViewById(R.id.list_song_title);
            holder.artistName = (TextView)convertView.findViewById(R.id.list_song_artist);

            convertView.setTag(holder);

        } else {
            // View already exists
            holder = (ViewHolder)convertView.getTag();
        }

        //get song using position
        final Song song = mSongs.get(position);

        //get title and artist strings
        holder.songName.setText(song.getTitle());
        holder.artistName.setText(song.getArtist());

        return convertView;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults onReturn = new FilterResults();
                ArrayList<Song> results = new ArrayList<Song>();
                if(mOrigin == null) {
                    Log.i(TAG, "mOrigin is null");
                    mOrigin = mSongs;
                }
                if(constraint != null) {
                    if(mOrigin != null && mOrigin.size() > 0) {
                        for (Song song : mOrigin) {
                            if(song.getTitle().toLowerCase().contains(constraint.toString())) {
                                results.add(song);
                            }
                        }
                    }
                    onReturn.values = results;
                }
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mSongs = (ArrayList<Song>)filterResults.values;

                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    static class ViewHolder {
        private TextView artistName;
        private TextView songName;


    }
}
