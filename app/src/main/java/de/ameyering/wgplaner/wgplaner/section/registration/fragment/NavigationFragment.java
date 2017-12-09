package de.ameyering.wgplaner.wgplaner.section.registration.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationFragment extends Fragment {
    public OnNavigationEventListener mNavigationEventListener;

    public interface OnNavigationEventListener {
        void onForward();
        void onBack();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setNavigationEventListener(OnNavigationEventListener mNavigationEventListener) {
        this.mNavigationEventListener = mNavigationEventListener;
    }
}
