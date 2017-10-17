package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.section.home.fragment.PinboardChildFragment;

/**
 * Created by D067867 on 17.10.2017.
 */

public class PinboardPagerAdapter extends FragmentPagerAdapter {
    private TabLayout tabLayout;
    private ViewPager pager;
    private ArrayList<PinboardChildFragment> childFragments = new ArrayList<>();


    public PinboardPagerAdapter(FragmentManager manager){
        super(manager);
    }

    public PinboardPagerAdapter(FragmentManager manager, TabLayout tabLayout, ViewPager pager){
        this(manager);
        this.tabLayout = tabLayout;
        this.pager = pager;
    }

    @Override
    public Fragment getItem(int position) {
        return childFragments.get(position);
    }

    @Override
    public int getCount() {
        return childFragments.size();
    }

    public void setViewPager(ViewPager pager){
        this.pager = pager;
    }

    public void setTabLayout(TabLayout tabLayout){
        this.tabLayout = tabLayout;
    }

    public void addFragment(PinboardChildFragment fragment){
        childFragments.add(fragment);
    }

    public void initialize(){

        //Adds tab for each Fragment to TabLayout
        for(PinboardChildFragment fragment: childFragments){
            tabLayout.addTab(tabLayout.newTab().setText(fragment.getTitle()));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager.setAdapter(this);
        pager.setSaveEnabled(false);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
