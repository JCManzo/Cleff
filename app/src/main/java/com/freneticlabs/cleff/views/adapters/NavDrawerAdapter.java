package com.freneticlabs.cleff.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freneticlabs.cleff.R;

/**
 * Created by jcmanzo on 2/16/15.
 */
public class NavDrawerAdapter extends ArrayAdapter {
    private final Context mContext;
    private final String[] mNavDrawerValues;
    private int[] mNavDrawerImages = {
            R.drawable.ic_nav_drawer_music_library,
            R.drawable.ic_nav_drawer_favorites,
            R.drawable.ic_nav_drawer_music_queue,
            R.drawable.ic_nav_drawer_recently_added
    };

    public NavDrawerAdapter(Context context, String[] menuValues) {
        super(context, R.layout.nav_drawer_item, menuValues);
        mContext = context;
        mNavDrawerValues = menuValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.nav_drawer_item, parent, false);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.drawer_item_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_item_icon);

        textView.setText(mNavDrawerValues[position]);
        imageView.setImageResource(mNavDrawerImages[position]);

        return rowView;
    }
}
