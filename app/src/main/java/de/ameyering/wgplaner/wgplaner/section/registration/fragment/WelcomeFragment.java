package de.ameyering.wgplaner.wgplaner.section.registration.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;


public class WelcomeFragment extends NavigationFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_registration, container, false);

        Button btnContinue = view.findViewById(R.id.btn_continue_welcome);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNavigationEventListener != null) {
                    mNavigationEventListener.onForward();
                }
            }
        });

        return view;
    }
}
