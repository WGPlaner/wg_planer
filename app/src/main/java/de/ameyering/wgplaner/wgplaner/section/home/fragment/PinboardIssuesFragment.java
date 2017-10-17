package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ameyering.wgplaner.wgplaner.R;

/**
 * Created by D067867 on 17.10.2017.
 */

public class PinboardIssuesFragment extends PinboardChildFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.section_pinboard_issues, container, false);
    }
}
