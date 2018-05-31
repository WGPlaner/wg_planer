package de.ameyering.wgplaner.wgplaner.section.registration.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.SetUpActivity;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.model.User;

public class StateEMailFragment extends NavigationFragment {
    private EditText inputEmail;
    private DataProvider dataProvider = DataProvider.getInstance();

    private Button btnContinue;
    private Button btnSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state_email_address, container, false);

        inputEmail = view.findViewById(R.id.input_email);

        String mEmail = Configuration.singleton.getConfig(Configuration.Type.USER_EMAIL_ADDRESS);

        if (mEmail != null) {
            inputEmail.setText(mEmail);
        }

        btnContinue = view.findViewById(R.id.btn_continue_state_email_address);
        btnSkip = view.findViewById(R.id.btn_skip_state_email_address);

        btnContinue.setOnClickListener(view1 -> {
            String emailAddress = inputEmail.getText().toString();

            if (isValidEmail(emailAddress)) {
                dataProvider.setCurrentUserEmail(emailAddress, null);

                finish();

            } else {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Email is invalid",
                        Toast.LENGTH_LONG).show());
            }
        });

        btnSkip.setOnClickListener(view12 -> finish());


        return view;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private void finish() {
        dataProvider.registerUser(new ServerCallsInterface.OnAsyncCallListener<User>() {
            @Override
            public void onFailure(ApiException e) {
                FragmentActivity activity = getActivity();

                if (activity instanceof RegistrationActivity) {
                    activity.runOnUiThread(((RegistrationActivity) activity)::stopProgress);
                }

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), getString(R.string.server_connection_failed),
                        Toast.LENGTH_LONG).show();
                    btnContinue.setEnabled(true);
                    btnSkip.setEnabled(true);
                });
            }

            @Override
            public void onSuccess(User result) {
                Intent intent = new Intent(getContext(), SetUpActivity.class);

                if (RegistrationActivity.joinGroupIntent != null) {
                    intent.setData(RegistrationActivity.joinGroupIntent.getData());
                }

                FragmentActivity activity = getActivity();

                if (activity instanceof RegistrationActivity) {
                    activity.runOnUiThread(((RegistrationActivity) activity)::stopProgress);
                }

                activity.runOnUiThread(() -> {
                    btnContinue.setEnabled(true);
                    btnSkip.setEnabled(true);

                    startActivity(intent);
                    getActivity().finish();
                });
            }
        });

        FragmentActivity activity = getActivity();

        if (activity instanceof RegistrationActivity) {
            activity.runOnUiThread(((RegistrationActivity) activity)::startProgress);
        }

        btnContinue.setEnabled(false);
        btnSkip.setEnabled(false);
    }
}
