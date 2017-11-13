package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.structure.User;

public class AddItemRequestedForAdapter extends
    RecyclerView.Adapter<AddItemRequestedForAdapter.ViewHolder> {

    private ArrayList<User> selected = new ArrayList<>();

    @Override
    public int getItemCount() {
        return selected.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item_requested_for_item,
                parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initialize(selected.get(position), position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView userPicture;
        TextView userName;
        ImageButton close;
        private int position;
        private User user;

        public ViewHolder(View view, int viewType) {
            super(view);
            userPicture = view.findViewById(R.id.add_item_requested_for_user_picture);
            userName = view.findViewById(R.id.add_item_requested_for_user_name);
            close = view.findViewById(R.id.add_item_requested_for_close);
        }

        public void initialize(User user, int position) {
            this.user = user;
            this.position = position;
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected.remove(ViewHolder.this.user);
                    notifyItemRemoved(ViewHolder.this.position);
                }
            });

            userName.setText(user.getDisplayName());
        }
    }

    public void updateSelection(ArrayList<User> selected) {
        if (selected != null) {
            this.selected.clear();
            this.selected.addAll(selected);
        }
    }

    public ArrayList<User> getSelection() {
        return selected;
    }
}
