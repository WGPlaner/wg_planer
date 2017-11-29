package de.ameyering.wgplaner.wgplaner.section.registration.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class PickDisplayNameFragment extends NavigationFragment {
    private EditText inputName;
    private UploadProfilePictureFragment uploadProfilePictureFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_display_name_registration, container, false);

        Button btnContinue = view.findViewById(R.id.btn_continue_pick_display_name);
        inputName = view.findViewById(R.id.input_username);

        String displayName = Configuration.singleton.getConfig(Configuration.Type.USER_DISPLAY_NAME);

        if (uploadProfilePictureFragment == null) {
            uploadProfilePictureFragment = new UploadProfilePictureFragment();
        }

        if (displayName != null) {
            inputName.setText(displayName);
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = inputName.getText().toString();

                if (!displayName.isEmpty()) {
                    DataProvider.getInstance().setCurrentUserDisplayName(displayName);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right,
                        R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left,
                        R.anim.anim_fragment_exit_to_right);
                    transaction.hide(PickDisplayNameFragment.this);
                    transaction.add(R.id.container_registration, uploadProfilePictureFragment);
                    transaction.addToBackStack("");
                    transaction.commit();

                } else {
                    Toast.makeText(getContext(), PickDisplayNameFragment.this.getActivity().getString(
                            R.string.warning_input_username_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
