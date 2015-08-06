package com.freneticlabs.cleff.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.models.MusicLibrary;
import com.freneticlabs.cleff.models.Song;
import com.freneticlabs.cleff.utils.Utils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {
    public static final String EXTRA_SONG_ID = "com.freneticlabs.cleff.song_id";

    @Bind(R.id.player_song_art) ImageView mSongArt;

    private Song mSong;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(int songId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SONG_ID, songId);

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int songId = (int)getArguments().getSerializable(EXTRA_SONG_ID);
        mSong = MusicLibrary.get(getActivity()).getSong(songId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);
        Bitmap bitmap = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();

        try{
            File file = new File(mSong.getPath());
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            Artwork artwork = tag.getFirstArtwork();
            if(artwork == null) {
                Timber.d("Could not find embedded art.");
            } else {
                // TODO fetch cover art in asynctask
                byte[] art = artwork.getBinaryData();

               // Decode the covert art with 400x400 pixel resolution
                bitmap = Utils.decodeSampledBitmapFromResource(art, 500, 500);
                //bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                // Create a scaled up version of the bitmap
               /* int origImgWidth = bitmap.getWidth();
                int origImgHeight = bitmap.getHeight();

                int newWidth = getScreenWidth();
                float scaleFactor = (float) newWidth / (float) origImgWidth;
                int newHeight = (int) (origImgHeight * scaleFactor);

                Bitmap scaledPicture = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                if(!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                mSongArt.setImageBitmap(scaledPicture);
*/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;

    }

    @Override
    public void onDestroyView() {
        mSongArt.setImageDrawable(null);
        super.onDestroyView();
        ButterKnife.unbind(this);

    }


}