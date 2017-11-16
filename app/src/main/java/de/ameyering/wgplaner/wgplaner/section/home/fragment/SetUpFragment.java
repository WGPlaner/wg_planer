package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.SetUpGroupActivity;


public class SetUpFragment extends SectionFragment {
    private static final int REQ_CODE_CREATE_GROUP = 0;

    private Button buttonCreate;
    private Button buttonJoin;

    private OnReadyListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_up, container, false);

        if (toolbar != null && title != null) {
            toolbar.setSubtitle(title);
        }

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        buttonCreate = view.findViewById(R.id.set_up_group_btn_create_group);
        buttonJoin = view.findViewById(R.id.set_up_group_btn_join_group);

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement Join-Group flow
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

    public void setOnReadyListener(OnReadyListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public interface OnReadyListener {

        void onReady();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_CREATE_GROUP: {
                if (resultCode == Activity.RESULT_OK) {
                    listener.onReady();
                }
            }
            break;
        }
    }
}
