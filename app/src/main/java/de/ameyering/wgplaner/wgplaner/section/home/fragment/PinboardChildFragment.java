package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by D067867 on 17.10.2017.
 */

public abstract class PinboardChildFragment extends Fragment {
    private String title;

    public PinboardChildFragment(){
        this.title = "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setTitle(@NonNull String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
