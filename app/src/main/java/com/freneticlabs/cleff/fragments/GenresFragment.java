package com.freneticlabs.cleff.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freneticlabs.cleff.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment {


    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.empty_data_set);
        return textView;
    }


}
