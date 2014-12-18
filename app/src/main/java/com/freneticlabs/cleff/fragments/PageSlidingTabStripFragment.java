package com.freneticlabs.cleff.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.views.adapters.TabsPageAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageSlidingTabStripFragment extends Fragment{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;

    public static final String TAG = PageSlidingTabStripFragment.class
            .getSimpleName();

    public static PageSlidingTabStripFragment newInstance() {

        return new PageSlidingTabStripFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_sliding_tab_strip, container, false);
        ButterKnife.inject(this, view);

        // Set an adapter
        pager.setAdapter(new TabsPageAdapter(getFragmentManager()));

        // Bind the tabs to the ViewPager
        tabs.setViewPager(pager);

        return view;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}
