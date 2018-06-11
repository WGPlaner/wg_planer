package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.ameyering.wgplaner.wgplaner.section.home.fragment.ReceivedBillsFragment;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.SentBillsFragment;

public class BillsPagerAdapter extends FragmentPagerAdapter {
    private ReceivedBillsFragment receivedBillsFragment = new ReceivedBillsFragment();
    private SentBillsFragment sentBillsFragment = new SentBillsFragment();

    public BillsPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return receivedBillsFragment;

        } else if (position == 1) {
            return sentBillsFragment;

        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
