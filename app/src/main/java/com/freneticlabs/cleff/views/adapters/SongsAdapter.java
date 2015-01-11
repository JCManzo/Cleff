package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.views.widgets.CheckableImageButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends ArrayAdapter<Song> {
    private ArrayList<Song> mSongList;
    private final Context mContext;

    private CleffApp mCleffApp;
    private CheckableImageButton mCurrentButton = null;
    // Remember the last item shown on screen
    private int lastPosition = -1;


    public SongsAdapter(Context context, int resource, ArrayList<Song> songs) {
        super(context, resource, songs);
        mContext = context;
        mSongList = songs;
        mCleffApp = (CleffApp) mContext.getApplicationContext();
    }




    static class ViewHolderItem {
        @InjectView(R.id.list_song_title)
        TextView title;
        @InjectView(R.id.list_song_artist)
        TextView artist;


        public ViewHolderItem(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolderItem holder;
        final Song song = getItem(position);

        // reuse views
        if (view != null) {
            holder = (ViewHolderItem) view.getTag();

        } else {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.song_list_row_item, null);
            holder = new ViewHolderItem(view);
            view.setTag(holder);
        }

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());

        setAnimation(view, position);
        return view;
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
