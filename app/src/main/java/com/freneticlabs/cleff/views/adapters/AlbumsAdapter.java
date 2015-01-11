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
import com.freneticlabs.cleff.views.widgets.CheckableImageButton;
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

    private CleffApp mCleffApp;
    private CheckableImageButton mCurrentButton = null;
    // Remember the last item shown on screen
    private int lastPosition = -1;
    private static final String ART_WORK_PATH = "content://media/external/audio/albumart";


    public AlbumsAdapter(Context context, int resource, ArrayList<Album> albums) {
        super(context, resource, albums);
        mContext = context;
        mAlbumList = albums;
        mCleffApp = (CleffApp) mContext.getApplicationContext();
    }

    static class ViewHolderItem {
        @InjectView(R.id.grid_album_title)
        TextView title;
        @InjectView(R.id.grid_album_num_songs)
        TextView songNumber;
        @InjectView(R.id.grid_album_image)
        ImageView artwork;


        public ViewHolderItem(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolderItem holder;
        final Album album = getItem(position);

        // reuse views
        if (view != null) {
            holder = (ViewHolderItem) view.getTag();

        } else {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.albums_grid_item, null);
            holder = new ViewHolderItem(view);
            view.setTag(holder);
        }

        holder.title.setText(album.getAlbum());
        Uri uri = ContentUris.withAppendedId(Uri.parse(ART_WORK_PATH), album.getAlbumId());
        Picasso.with(mContext)
                .load(uri)
                .resize(300,300)
                .placeholder(R.drawable.adele)
                .into(holder.artwork);
        return view;
    }


}
