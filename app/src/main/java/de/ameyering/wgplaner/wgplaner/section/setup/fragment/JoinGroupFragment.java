package de.ameyering.wgplaner.wgplaner.section.setup.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class JoinGroupFragment extends Fragment {
    EditText key;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_join_group, container, false);

        key = view.findViewById(R.id.fragment_setup_join_group_input_access_key);

        Button btnJoinGroup = view.findViewById(R.id.fragment_setup_join_group_btn_join);
        btnJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputAndReturn(key.getText().toString())) {
                    joinGroup(key.getText().toString());
                }
            }
        });

        return view;
    }

    private boolean checkInputAndReturn(String key) {
        if (key != null && !key.isEmpty()) {
            Pattern pattern = Pattern.compile("^[A-Z0-9]{12}$");
            Matcher matcher = pattern.matcher(key);

            return matcher.matches();
        }

        return false;
    }

    private void joinGroup(String key) {
        if (DataProvider.getInstance().joinCurrentGroup(key, getContext())) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();

        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), getString(R.string.server_connection_failed),
                        Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
