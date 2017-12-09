package de.ameyering.wgplaner.wgplaner.section.settings.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import io.swagger.client.model.User;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
    ArrayList<User> users = new ArrayList<>();
    Context context;

    public GroupMemberAdapter(ArrayList<User> users, Context context) {
        this.users.clear();
        this.users.addAll(users);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userPicture;
        TextView userName;
        ImageButton close;
        CardView userContainer;
        User user;

        public ViewHolder(View view) {
            super(view);
            userPicture = view.findViewById(R.id.add_item_requested_for_user_picture);
            userName = view.findViewById(R.id.add_item_requested_for_user_name);
            close = view.findViewById(R.id.add_item_requested_for_close);
            userContainer = view.findViewById(R.id.add_item_requested_for_user_container);
        }

        public void initialize(User user) {
            this.user = user;
            close.setVisibility(View.INVISIBLE);
            userName.setText(user.getDisplayName());

            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.fragment_user_detail_dialog);


                    TextView name = dialog.findViewById(R.id.fragment_user_detail_username);
                    TextView email = dialog.findViewById(R.id.fragment_user_detail_email);
                    ImageView image = dialog.findViewById(R.id.fragment_user_detail_image);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    Bitmap bitmap = ImageStore.getInstance().loadGroupMemberPicture(ViewHolder.this.user.getUid(),
                            context);

                    if (bitmap != null) {
                        image.setImageBitmap(bitmap);
                    }

                    if (ViewHolder.this.user.getEmail() != null && !ViewHolder.this.user.getEmail().isEmpty()) {
                        email.setText(ViewHolder.this.user.getEmail());

                    } else {
                        email.setVisibility(View.GONE);
                    }

                    name.setText(ViewHolder.this.user.getDisplayName());
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initialize(users.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item_requested_for_item,
                parent, false);

        return new ViewHolder(view);
    }

    public void onNewData(ArrayList<User> users) {
        UserCallback callback = new UserCallback(users, this.users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.users.clear();
        this.users.addAll(users);

        result.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private static class UserCallback extends DiffUtil.Callback {
        private ArrayList<User> newUsers;
        private ArrayList<User> oldUsers;

        public UserCallback(ArrayList<User> newUsers, ArrayList<User> oldUsers) {
            this.newUsers = newUsers;
            this.oldUsers = oldUsers;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            User newUser = newUsers.get(newItemPosition);
            User oldUser = oldUsers.get(oldItemPosition);

            return newUser.getUid().equals(oldUser.getUid());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            User newUser = newUsers.get(newItemPosition);
            User oldUser = oldUsers.get(oldItemPosition);

            return newUser.getDisplayName().equals(oldUser.getDisplayName());
        }
    }
}
