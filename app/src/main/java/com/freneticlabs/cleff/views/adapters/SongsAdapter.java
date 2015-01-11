package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicDatabase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends CursorAdapter {
    private final Context mContext;

    // Remember the last item shown on screen
    private int lastPosition = -1;


    public SongsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("TAG", "HEREEE");
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        Timber.d(cursor.getString(cursor.getColumnIndex(MusicDatabase.SONG_TITLE)));
        holder.title.setText(cursor.getString(cursor.getColumnIndex(MusicDatabase.SONG_TITLE)));
        holder.artist.setText(cursor.getString(cursor.getColumnIndex(MusicDatabase.SONG_ARTIST)));

        setAnimation(view, cursor.getPosition());

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.song_list_row_item, parent, false);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.list_song_title)
        TextView title;
        @InjectView(R.id.list_song_artist)
        TextView artist;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
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
