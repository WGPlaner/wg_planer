package de.ameyering.wgplaner.wgplaner.section.registration.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.User;

public class PickDisplayNameFragment extends NavigationFragment {
    private EditText inputName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_display_name_registration, container, false);

        Button btnContinue = view.findViewById(R.id.btn_continue_pick_display_name);
        inputName = view.findViewById(R.id.input_username);

        String displayName = Configuration.singleton.getConfig(Configuration.Type.USER_DISPLAY_NAME);

        if (displayName != null) {
            inputName.setText(displayName);
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = inputName.getText().toString();

                if (!displayName.isEmpty()) {
                    User user = DataProvider.Users.getCurrentUser();
                    user.setDisplayName(displayName);
                    DataProvider.Users.setCurrentUser(user);

                    if (mNavigationEventListener != null) {
                        mNavigationEventListener.onForward();
                    }

                } else {
                    Toast.makeText(getContext(), PickDisplayNameFragment.this.getActivity().getString(
                            R.string.warning_input_username_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
