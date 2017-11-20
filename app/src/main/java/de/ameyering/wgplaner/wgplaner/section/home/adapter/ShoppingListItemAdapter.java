package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;
import io.swagger.client.model.ListItem;


public class ShoppingListItemAdapter extends
    RecyclerView.Adapter<ShoppingListItemAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        LinearLayout containerNumber;
        TextView displayNumber;

        LinearLayout containerRequestedBy;
        TextView displayRequestedBy;

        LinearLayout containerRequestedFor;
        TextView displayRequestedFor;

        CheckBox checkbox;

        ListItem item;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.shopping_list_item_product_name);

            containerNumber = itemView.findViewById(R.id.shopping_list_item_product_attribute_number);
            displayNumber = itemView.findViewById(R.id.shopping_list_item_product_number);

            containerRequestedBy = itemView.findViewById(
                    R.id.shopping_list_item_product_attribute_requested_by);
            displayRequestedBy = itemView.findViewById(R.id.shopping_list_item_product_requested_by);

            containerRequestedFor = itemView.findViewById(
                    R.id.shopping_list_item_product_attribute_requested_for);
            displayRequestedFor = itemView.findViewById(R.id.shopping_list_item_product_requested_for);

            checkbox = itemView.findViewById(R.id.shopping_list_item_checked);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkbox.isChecked()){
                        DataContainer.SelectedShoppingListItems.addSelectedShoppingListItem(item);
                    } else {
                        DataContainer.SelectedShoppingListItems.removeSelectedShoppingListItem(item);
                    }
                }
            });
        }

        public void initialize(ListItem item) {
            if(this.item == null){
                this.item = item;
            } else if(!this.item.equals(item)) {
                this.item = item;
                checkbox.setChecked(false);
            } else if (this.item.equals(item)){
                checkbox.setChecked(false);
                return;
            }

            String title = item.getTitle();

            if (title != null) {
                name.setText(title);
            }

            String requestedBy = item.getRequestedBy();

            if (requestedBy != null) {
                String displayName = DataContainer.Users.getDisplayNameByUid(requestedBy);

                if (displayName != null) {
                    displayRequestedBy.setText(displayName);
                }
            }

            displayNumber.setText(item.getCount());

            List<String> uids = item.getRequestedFor();
            String concatNames = "";

            for (String uid : uids) {
                concatNames = concatNames + DataContainer.Users.getDisplayNameByUid(uid) + ", ";
            }

            displayRequestedFor.setText(concatNames);
        }

        public void updateContent(ListItem item, Bundle args) {
            this.item = item;

            if (args == null) {
                return;
            }

            int title = args.getInt(ItemDiffCallback.NAME);
            int requestedFor = args.getInt(ItemDiffCallback.REQUESTED_FOR);
            int requestedBy = args.getInt(ItemDiffCallback.REQUESTED_BY);
            int number = args.getInt(ItemDiffCallback.NUMBER);

            if (title == ItemDiffCallback.TRUE) {
                name.setText(item.getTitle());
            }

            if (requestedBy == ItemDiffCallback.TRUE) {
                displayRequestedBy.setText(item.getRequestedBy());
            }

            if (requestedFor == ItemDiffCallback.TRUE) {
                List<String> uids = item.getRequestedFor();
                String concatNames = "";

                for (String uid : uids) {
                    concatNames = concatNames + DataContainer.Users.getDisplayNameByUid(uid) + ", ";
                }

                displayRequestedFor.setText(concatNames);
            }

            if(number == ItemDiffCallback.TRUE){
                displayNumber.setText(item.getCount());
            }
        }
    }

    private ArrayList<ListItem> items = new ArrayList<>();

    public ShoppingListItemAdapter(ArrayList<ListItem> items, Context context) {
        this.items.clear();
        this.items.addAll(items);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initialize(items.get(position));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);

        } else {
            holder.updateContent(items.get(position), (Bundle) payloads.get(0));
        }
    }

    public void onNewData(ArrayList<ListItem> items) {
        final ItemDiffCallback callback = new ItemDiffCallback(items, this.items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        this.items.clear();
        this.items.addAll(items);

        result.dispatchUpdatesTo(this);
    }

    private class ItemDiffCallback extends DiffUtil.Callback {
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String REQUESTED_BY = "requestedBy";
        public static final String REQUESTED_FOR = "requestedFor";

        public static final int FALSE = 0;
        public static final int TRUE = 1;

        private ArrayList<ListItem> newItems = new ArrayList<>();
        private ArrayList<ListItem> oldItems = new ArrayList<>();

        public ItemDiffCallback(ArrayList<ListItem> newItems, ArrayList<ListItem> oldItems) {
            this.newItems.clear();
            this.newItems.addAll(newItems);

            this.oldItems.clear();
            this.oldItems.addAll(oldItems);
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            ListItem newItem = newItems.get(newItemPosition);
            ListItem oldItem = oldItems.get(oldItemPosition);

            if (!isTitleEquals(newItem.getTitle(), oldItem.getTitle())) {
                return false;
            }

            if (!isRequestedForEquals(newItem.getRequestedFor(), oldItem.getRequestedFor())) {
                return false;
            }

            return true;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ListItem newItem = newItems.get(newItemPosition);
            ListItem oldItem = oldItems.get(oldItemPosition);

            if (!isTitleEquals(newItem.getTitle(), oldItem.getTitle())) {
                return false;
            }

            if (!isRequestedForEquals(newItem.getRequestedFor(), oldItem.getRequestedFor())) {
                return false;
            }

            if (!isRequestedByEquals(newItem.getRequestedBy(), oldItem.getRequestedBy())) {
                return false;
            }

            return false;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            ListItem newItem = newItems.get(newItemPosition);
            ListItem oldItem = oldItems.get(oldItemPosition);

            int title = TRUE;
            int requestedBy = TRUE;
            int requestedFor = TRUE;
            int number = TRUE;

            Bundle args = new Bundle();

            if (!isTitleEquals(newItem.getTitle(), oldItem.getTitle())) {
                args.putInt(NAME, TRUE);

            } else {
                title = FALSE;
                args.putInt(NAME, FALSE);
            }

            if (!isRequestedByEquals(newItem.getRequestedBy(), oldItem.getRequestedBy())) {
                args.putInt(REQUESTED_BY, TRUE);

            } else {
                requestedBy = FALSE;
                args.putInt(REQUESTED_BY, FALSE);
            }

            if (!isRequestedForEquals(newItem.getRequestedFor(), oldItem.getRequestedFor())) {
                args.putInt(REQUESTED_FOR, TRUE);

            } else {
                requestedFor = FALSE;
                args.putInt(REQUESTED_FOR, FALSE);
            }

            if(!isNumberEquals(newItem.getCount(), oldItem.getCount())){
                args.putInt(NUMBER, TRUE);
            } else {
                number = FALSE;
                args.putInt(NUMBER, FALSE);
            }

            if (title == FALSE && requestedBy == FALSE && requestedFor == FALSE && number == FALSE) {
                return null;
            }

            return args;
        }

        private boolean isTitleEquals(String newTitle, String oldTitle) {
            if (newTitle == null) {
                if (oldTitle != null) {
                    return false;
                }

            } else {
                if (oldTitle == null) {
                    return false;
                }
            }

            if (newTitle != null && !newTitle.equals(oldTitle)) {
                return false;
            }

            return true;
        }

        private boolean isRequestedForEquals(List<String> newRequestedFor, List<String> oldRequestedFor) {
            if (newRequestedFor == null) {
                if (oldRequestedFor != null) {
                    return false;
                }

            } else {
                if (oldRequestedFor == null) {
                    return false;
                }
            }

            if (newRequestedFor != null && !newRequestedFor.equals(oldRequestedFor)) {
                return false;
            }

            return true;
        }

        private boolean isRequestedByEquals(String newRequestedBy, String oldRequestedBy) {
            if (newRequestedBy == null) {
                if (oldRequestedBy != null) {
                    return false;
                }

            } else {
                if (oldRequestedBy == null) {
                    return false;
                }
            }

            if (newRequestedBy != null && !newRequestedBy.equals(oldRequestedBy)) {
                return false;
            }

            return true;
        }

        private boolean isNumberEquals(int newNumber, int oldNumber) {
            if (newNumber == oldNumber) {
                return true;
            }

            return false;
        }
    }
}
