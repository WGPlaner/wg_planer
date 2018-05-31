package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.PinboardPagerAdapter;

public class PinboardFragment extends SectionFragment {
    private TabLayout tabLayout;
    private ViewPager pager;
    private PinboardPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_pinboard, container, false);

        if (actionBar != null) {
            if (title != null) {
                actionBar.setSubtitle(title);

            } else {
                actionBar.setSubtitle(getString(R.string.section_title_pinboard));
            }
        }

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        if (tabLayout == null) {
            tabLayout = view.findViewById(R.id.pinboard_tab_layout);
        }

        if (pager == null) {
            pager = view.findViewById(R.id.pinboard_view_pager);
        }

        if (adapter == null) {
            adapter = new PinboardPagerAdapter(getChildFragmentManager(), tabLayout, pager);
            initializeChildFragments();
            adapter.initialize();
        }

        return view;
    }

    private void initializeChildFragments() {
        PinboardChatFragment chatFragment = new PinboardChatFragment();
        chatFragment.setTitle("CHAT");
        adapter.addFragment(chatFragment);

        PinboardIssuesFragment issuesFragment = new PinboardIssuesFragment();
        issuesFragment.setTitle("ISSUES");
        adapter.addFragment(issuesFragment);
    }
}
