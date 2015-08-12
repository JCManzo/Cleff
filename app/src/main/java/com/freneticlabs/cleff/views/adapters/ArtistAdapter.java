package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.freneticlabs.cleff.CleffApp;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.Artist;
import java.util.ArrayList;

/**
 * Created by jcmanzo on 7/11/15.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
  private ArrayList<Artist> mArtistList;
  private final Context mContext;
  private LayoutInflater mArtistsInflater;
  private String mSongCount;
  private CleffApp mCleffApp;

  public ArtistAdapter(Context context, RecyclerView recyclerView, ArrayList<Artist> artists) {
    mContext = context;
    mArtistList = artists;
    mCleffApp = (CleffApp) mContext.getApplicationContext();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.grid_artist_title) TextView artistName;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists_grid, parent, false);
    return new ViewHolder(view);
  }

  @Override public int getItemCount() {
    return mArtistList.size();
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.artistName.setText(mArtistList.get(position).getArtistName());

  }
}
