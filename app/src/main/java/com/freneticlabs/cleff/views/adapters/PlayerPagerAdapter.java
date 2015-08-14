package com.freneticlabs.cleff.views.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Song;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by jcmanzo on 8/12/15.
 */
public class PlayerPagerAdapter extends PagerAdapter {

  private Context mContext;
  private ArrayList<Song> mSongs;

  public PlayerPagerAdapter(Context context, ArrayList<Song> songs) {
    mContext = context;
    mSongs = songs;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View layout = inflater.inflate(R.layout.fragment_player, container, false);
    ImageView imageView = (ImageView)layout.findViewById(R.id.player_song_art);

    Uri uri = ContentUris.withAppendedId(Uri.parse(CleffApp.ART_WORK_PATH), mSongs.get(position).getId());
    Picasso.with(mContext)
        .load(uri)
        .fit()
        .placeholder(R.drawable.no_album_art)
        .into(imageView);
    return layout;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override public int getCount() {
    return mSongs.size();
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public CharSequence getPageTitle(int position) {

    return mSongs.get(position).getTitle();
  }
}
