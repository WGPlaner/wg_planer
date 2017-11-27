package de.ameyering.wgplaner.wgplaner.section.setup.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class DescriptionFragment extends Fragment {
    private static final String JOIN_GROUP_FRAGMENT_TAG = "JoinGOrupFrgment";
    private static final String CREATE_GROUP_FRAGMENT_TAG = "CreateGroupFragment";

    public CreateGroupFragment createGroupFragment = new CreateGroupFragment();
    public JoinGroupFragment joinGroupFragment = new JoinGroupFragment();

    private boolean isInCreateGroup = false;
    private boolean isInJoinGroup = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_description, container, false);

        TextView header = view.findViewById(R.id.fragment_setup_description_header);
        header.setText(String.format(getString(R.string.set_up_group_header), DataProvider.getInstance().getCurrentUserDisplayName()));

        Button btnCreateGroup = view.findViewById(R.id.fragment_setup_description_btn_create);
        Button btnJoinGroup = view.findViewById(R.id.fragment_setup_description_btn_join);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCreateGroup();
            }
        });

        btnJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadJoinGroup();
            }
        });

        if(isInCreateGroup){
            loadCreateGroup();
        } else if (isInJoinGroup) {
            loadJoinGroup();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isInCreateGroup = false;
        isInJoinGroup = false;
    }

    private void loadCreateGroup(){
        isInCreateGroup = true;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right);
        transaction.hide(this);
        transaction.add(R.id.container_set_up, createGroupFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

    private void loadJoinGroup(){
        isInJoinGroup = true;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right);
        transaction.hide(this);
        transaction.add(R.id.container_set_up, joinGroupFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }
}
