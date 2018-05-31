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
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.model.Group;

public class JoinGroupFragment extends Fragment {
    private EditText key;
    private Button btnJoinGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_join_group, container, false);

        key = view.findViewById(R.id.fragment_setup_join_group_input_access_key);

        btnJoinGroup = view.findViewById(R.id.fragment_setup_join_group_btn_join);
        btnJoinGroup.setOnClickListener(view1 -> {
            if (checkInputAndReturn(key.getText().toString())) {
                joinGroup(key.getText().toString());
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
        DataProvider.getInstance().joinCurrentGroup(key, getContext(),
        new ServerCallsInterface.OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                getActivity().runOnUiThread(() ->  {
                    Toast.makeText(getContext(), getString(R.string.server_connection_failed),
                        Toast.LENGTH_LONG).show();
                    btnJoinGroup.setEnabled(true);
                    JoinGroupFragment.this.key.setEnabled(true);
                });
            }

            @Override
            public void onSuccess(Group result) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btnJoinGroup.setEnabled(false);
        this.key.setEnabled(false);
    }
}
