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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jcmanzo on 12/14/14.
 */
public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Song> mSongList;
    private ItemClickListener mItemClickListener;
    private final Context mContext;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public SongsAdapter(ArrayList<Song> songList, Context context, @NonNull ItemClickListener itemClickListener) {
        mSongList = songList;
        mContext = context;
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        public void itemClicked(Song song, int position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Return the correct instance of ViewHolder depending if the viewType is TYPE_ITEM or TYPE_HEADER
        Context context = viewGroup.getContext();

        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View parent = LayoutInflater.from(context).inflate(R.layout.song_list_row_item, viewGroup, false);
            return ViewHolderItem.newInstance(parent);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            View parent = LayoutInflater.from(context).inflate(R.layout.songs_list_header, viewGroup, false);
            return ViewHolderHeader.newInstance(parent);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof ViewHolderItem) {
            final Song song = getItem(position);

            ViewHolderItem viewHolderItem = (ViewHolderItem) viewHolder;
            viewHolderItem.setTitle(song.getTitle());
            viewHolderItem.setArtist(song.getArtist());
            // Populates the imageview with the corresponding drawable
            Picasso.with(mContext)
                    .load(R.raw.adele)
                    .placeholder(android.R.color.transparent)
                    .resize(150, 150)
                    .centerInside()
                    .into(viewHolderItem.albumImage);
            viewHolderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClicked(song, position);
                }
            });
        } else if(viewHolder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) viewHolder;

        }

    }

    @Override
    public int getItemCount() {
        // +1 to account for the extra header item in the list
        return (mSongList.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Song getItem(int position) {
        return mSongList.get(position - 1);
    }

    public static final class ViewHolderItem extends RecyclerView.ViewHolder {
        private final View parent;

        @InjectView(R.id.list_song_title) TextView title;
        @InjectView(R.id.list_song_artist) TextView artist;
        @InjectView(R.id.list_song_image) CircleImageView albumImage;

        public static ViewHolderItem newInstance(View view) {
            return new ViewHolderItem(view);
        }

        public ViewHolderItem(View view) {
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

        public void setAlbumImage(int image) {
            albumImage.setImageResource(image);
        }
        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }

    public static final class ViewHolderHeader extends RecyclerView.ViewHolder {
        public ViewHolderHeader(View itemView) {
            super(itemView);
        }

        public static ViewHolderHeader newInstance(View view) {
            return new ViewHolderHeader(view);
        }
    }
}
