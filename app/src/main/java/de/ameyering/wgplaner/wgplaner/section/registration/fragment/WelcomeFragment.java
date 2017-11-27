package de.ameyering.wgplaner.wgplaner.section.registration.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.ameyering.wgplaner.wgplaner.R;


public class WelcomeFragment extends NavigationFragment {
    private PickDisplayNameFragment pickDisplayNameFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_registration, container, false);

        if(pickDisplayNameFragment == null){
            pickDisplayNameFragment = new PickDisplayNameFragment();
        }

        Button btnContinue = view.findViewById(R.id.btn_continue_welcome);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right);
                transaction.hide(WelcomeFragment.this);
                transaction.add(R.id.container_registration, pickDisplayNameFragment);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        return view;
    }
}
