package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.structure.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;


public class AddItemAddUserDialogFragment extends DialogFragment {
    private ArrayList<User> mSelectedItems = new ArrayList<>();
    private OnResultListener mOnResultListener;
    private User[] list;

    public interface OnResultListener {

        void onResult(ArrayList<User> selected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_item_add_requested_for_pick_users));

        list = new User[2];

        list[0] = new User("1", "Arne");
        list[1] = new User("2", "Chris");

        String[] users = new String[list.length];
        boolean[] selected = new boolean[list.length];

        for (int i = 0; i < list.length; i++) {
            users[i] = list[i].getDisplayName();

            if (mSelectedItems.contains(list[i])) {
                selected[i] = true;

            } else {
                selected[i] = false;
            }
        }


        builder.setMultiChoiceItems(users, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    mSelectedItems.add(list[i]);

                } else if (mSelectedItems.contains(list[i])) {
                    mSelectedItems.remove(list[i]);
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
