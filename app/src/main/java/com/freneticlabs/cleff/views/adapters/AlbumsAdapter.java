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
import com.freneticlabs.cleff.models.MusicLibrary;

import java.util.ArrayList;

import timber.log.Timber;


/**
 * Created by jcmanzo on 12/28/14.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolderItem> {
    private Context mContext;
    private CleffApp mCleffApp;
    private ArrayList<Album> mAlbumsList;

    public AlbumsAdapter(Context context) {
        mContext = context;
        mAlbumsList = MusicLibrary.get(mContext).getAlbums();
        mCleffApp = (CleffApp) mContext.getApplicationContext();

        Timber.d(Integer.toString(mAlbumsList.size()));
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Return the correct instance of ViewHolder depending if the viewType is TYPE_ITEM or TYPE_HEADER
        Context context = viewGroup.getContext();
        View parent = LayoutInflater.from(context).inflate(R.layout.albums_grid_item, viewGroup, false);
        return ViewHolderItem.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolderItem viewHolder, int position) {
       final Album album = getItem(position);

       viewHolder.setAlbum(album.getAlbum());
       Uri coverPath = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), album.getAlbumId());
       mCleffApp.getPicasso()
               .load(coverPath)
               .placeholder(R.drawable.adele)
               .resize(200,200)
               .into(viewHolder.mImage);
    }

    @Override
    public int getItemCount() {
        return mAlbumsList.size();
    }

    private Album getItem(int position) {
        return mAlbumsList.get(position);
    }

    public static final class ViewHolderItem extends RecyclerView.ViewHolder {
       // @InjectView(R.id.grid_album_title) TextView album;
        //@InjectView(R.id.grid_album_image) ImageView albumImage;
        private final View mView;
        private final TextView mName;
        private final ImageView mImage;
        public static ViewHolderItem newInstance(View view) {
            TextView name = (TextView)view.findViewById(R.id.grid_album_title);
            ImageView image = (ImageView)view.findViewById(R.id.grid_album_image);
            return new ViewHolderItem(view, name, image);
        }

        public ViewHolderItem(View view, TextView name, ImageView image) {
            super(view);
            mView = view;
            mName = name;
            mImage = image;
            //ButterKnife.inject(this, view);
        }

        public void setAlbum(CharSequence text) {
            mName.setText(text);
        }

        //public void seImage() {
          // albumImage.setImageResource(R.drawable.adele);
        //}

      /*  public ImageView getAlbumImage() {
            return albumImage;
        }*/
    }
}
