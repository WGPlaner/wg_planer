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

public class StateEMailFragment extends NavigationFragment {
    private EditText inputEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state_email_address, container, false);

        inputEmail = view.findViewById(R.id.input_email);

        String mEmail = Configuration.singleton.getConfig(Configuration.Type.EMAIL);

        if(mEmail != null){
            inputEmail.setText(mEmail);
        }

        Button btnContinue = view.findViewById(R.id.btn_continue_state_email_address);
        Button btnSkip = view.findViewById(R.id.btn_continue_state_email_address);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAdress = inputEmail.getText().toString();

                if(emailAdress != null){
                    //TODO: Check if email is valid
                    Configuration.singleton.addConfig(Configuration.Type.EMAIL, emailAdress);

                    if(mNavigationEventListener != null){
                        mNavigationEventListener.onForward();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Email is invalid", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNavigationEventListener != null){
                    mNavigationEventListener.onForward();
                }
            }
        });


        return view;
    }
}
