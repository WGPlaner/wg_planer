package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import io.swagger.client.model.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;


public class AddItemAddUserDialogFragment extends DialogFragment {
    private ArrayList<User> mSelectedItems = new ArrayList<>();
    private OnResultListener mOnResultListener;
    private User[] list;

    public interface OnResultListener {

        void onResult(ArrayList<User> selected);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_item_add_requested_for_pick_users));

        ArrayList<User> all = DataContainer.Users.getAll();
        list = new User[all.size()];

        for(int i = 0; i < all.size(); i++){
            list[i] = all.get(i);
        }

        String[] users = new String[list.length];
        boolean[] selected = new boolean[list.length];

        for (int i = 0; i < list.length; i++) {
            users[i] = list[i].getDisplayName();

            selected[i] = mSelectedItems.contains(list[i]);
        }


        builder.setMultiChoiceItems(users, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    mSelectedItems.add(list[position]);

                } else if (mSelectedItems.contains(list[position])) {
                    mSelectedItems.remove(list[position]);
                }
            }
        });

        builder.setPositiveButton(R.string.add_item_add_requested_for_positive,
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(mSelectedItems);
                }

                dialogInterface.cancel();
            }
        });

        builder.setNegativeButton(R.string.add_item_add_requested_for_negative,
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }

    public void setOnResultListener(OnResultListener mOnResultListener) {
        this.mOnResultListener = mOnResultListener;
    }

    public void setSelectedItems(ArrayList<User> users) {
        if (users != null) {
            mSelectedItems.clear();
            mSelectedItems.addAll(users);
        }
    }
}
