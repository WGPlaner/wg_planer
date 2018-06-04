package de.ameyering.wgplaner.wgplaner.section.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.BillsPagerAdapter;

public class BillsFragment extends SectionFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BillsPagerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_bills, container, false);

        if (actionBar != null) {
            actionBar.setTitle(R.string.section_title_bills);
        }

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        if(tabLayout == null) {
            tabLayout = view.findViewById(R.id.section_bills_tab_layout);
        }

        if(viewPager == null) {
            viewPager = view.findViewById(R.id.section_bills_view_pager);
        }

        if(adapter == null) {
            adapter = new BillsPagerAdapter(getChildFragmentManager());
        }

        viewPager.setAdapter(adapter);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }
}
