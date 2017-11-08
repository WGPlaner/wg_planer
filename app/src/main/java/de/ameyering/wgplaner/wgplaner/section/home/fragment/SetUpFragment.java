package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.SetUpGroupActivity;


public class SetUpFragment extends Fragment {
    private static final int REQ_CODE_CREATE_GROUP = 0;

    private EditText key;
    private ImageButton buttonKey;
    private Button buttonCreate;

    private OnReadyListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_up, container, false);

        key = view.findViewById(R.id.set_up_input_group_access_key);
        buttonKey = view.findViewById(R.id.set_up_btn_key_continue);
        buttonCreate = view.findViewById(R.id.set_up_btn_create_group);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        buttonKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SetUpGroupActivity.class);
                startActivityForResult(intent, REQ_CODE_CREATE_GROUP);
            }
        });

        return view;
    }

    public void setOnReadyListener(OnReadyListener listener){
        if(listener != null){
            this.listener = listener;
        }
    }

    public interface OnReadyListener {

        void onReady();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQ_CODE_CREATE_GROUP:{
                if(resultCode == Activity.RESULT_OK){
                    listener.onReady();
                }
            }
            break;
        }
    }
}
