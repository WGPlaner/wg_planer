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
    private ArrayList<User> mSelectedItems;
    private OnResultListener mOnResultListener;

    public interface OnResultListener{

        void onResult(ArrayList<User> selected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_item_add_requested_for_pick_users));

        String[] users = new String[DataContainer.Users.getAll().size()];

        for(int i = 0; i < DataContainer.Users.getAll().size(); i++){
            users[i] = DataContainer.Users.getUser(i).getDisplayName();
        }

        builder.setMultiChoiceItems(users, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    mSelectedItems.add(DataContainer.Users.getUser(i));
                } else if (mSelectedItems.contains(DataContainer.Users.getUser(i))){
                    mSelectedItems.remove(DataContainer.Users.getUser(i));
                }
            }
        });

        builder.setPositiveButton(R.string.add_item_add_requested_for_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mOnResultListener != null){
                    mOnResultListener.onResult(mSelectedItems);
                }
                dialogInterface.cancel();
            }
        });

        builder.setNegativeButton(R.string.add_item_add_requested_for_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }

    public void setOnResultListener(OnResultListener mOnResultListener){
        this.mOnResultListener = mOnResultListener;
    }
}
