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

/**
 * A simple {@link Fragment} subclass.
 */
public class PageSlidingTabStripFragment extends Fragment{
   // @InjectView(R.id.pager) ViewPager pager;
    //@InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
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

        return inflater.inflate(R.layout.fragment_page_sliding_tab_strip, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        TabsPageAdapter adapter = new TabsPageAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}
