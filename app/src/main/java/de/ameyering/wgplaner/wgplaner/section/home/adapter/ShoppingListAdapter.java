package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.structure.CategoryHolder;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewItem> {

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    private ArrayList<Object> viewItems = new ArrayList<>();
    private static DataProvider dataProvider = DataProvider.getInstance();

    public static abstract class ShoppingListViewItem extends RecyclerView.ViewHolder {

        private ShoppingListViewItem(View itemView) {
            super(itemView);
        }

        public abstract void setData(Object object, Bundle args);

        public void setData(Object object) {
            setData(object, null);
        }
    }

    public static class ShoppingListViewItemHeader extends ShoppingListViewItem {

        private TextView header = null;

        ShoppingListViewItemHeader(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.shopping_list_section_header);
        }

        @Override
        public void setData(final Object object, Bundle args) {
            header.setText((String) object);
        }
    }

    public static class ShoppingListViewItemContent extends ShoppingListViewItem {
        private TextView name = null;

        private CardView container = null;

        private LinearLayout containerNumber = null;
        private TextView displayNumber = null;

        private LinearLayout containerRequestedBy = null;
        private TextView displayRequestedBy = null;

        private LinearLayout containerRequestedFor = null;
        private TextView displayRequestedFor = null;

        private CheckBox checkbox = null;

        private ListItem item = null;

        ShoppingListViewItemContent(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.shopping_list_item_product_name);

            container = itemView.findViewById(R.id.shopping_list_item_container);

            containerNumber = itemView.findViewById(R.id.shopping_list_item_product_attribute_number);
            displayNumber = itemView.findViewById(R.id.shopping_list_item_product_number);

            containerRequestedBy = itemView.findViewById(
                R.id.shopping_list_item_product_attribute_requested_by);
            displayRequestedBy = itemView.findViewById(R.id.shopping_list_item_product_requested_by);

            containerRequestedFor = itemView.findViewById(
                R.id.shopping_list_item_product_attribute_requested_for);
            displayRequestedFor = itemView.findViewById(R.id.shopping_list_item_product_requested_for);

            checkbox = itemView.findViewById(R.id.shopping_list_item_checked);

            checkbox.setOnClickListener(view -> {
                if (checkbox.isChecked()) {
                    dataProvider.selectShoppingListItem(item);

                } else {
                    dataProvider.unselectShoppingListItem(item);
                }
            });

            container.setOnClickListener(view -> checkbox.performClick());
        }

        @Override
        public void setData(Object object, Bundle args) {
            if(object instanceof ListItem) {
                item = (ListItem) object;
            }

            if(item != null) {
                name.setText(item.getTitle());
                displayNumber.setText(item.getCount());
                displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
            }
        }
    }

    public ShoppingListAdapter(ArrayList<CategoryHolder> sections) {
        viewItems = transformSections(sections);
    }

    private ArrayList<Object> transformSections(ArrayList<CategoryHolder> sections) {
        ArrayList<Object> viewItems = new ArrayList<>();

        for(CategoryHolder section: sections) {
            viewItems.addAll(section.getViewItems());
        }

        return viewItems;
    }

    @Override
    public void onBindViewHolder(ShoppingListViewItem holder, int position, List<Object> payloads) {
        if(payloads == null || payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.setData(viewItems.get(position), (Bundle) payloads.get(0));
        }
    }

    @Override
    public ShoppingListViewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_header_layout, parent, false);

            return new ShoppingListViewItemHeader(item);
        } else {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_item_layout, parent, false);

            return new ShoppingListViewItemContent(item);
        }
    }

    @Override
    public void onBindViewHolder(ShoppingListViewItem holder, int position) {
        holder.setData(viewItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Object viewItem = viewItems.get(position);

        if(viewItem instanceof String) {
            return HEADER_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return viewItems.size();
    }

    public void onNewData(ArrayList<CategoryHolder> items) {
        ArrayList<Object> viewItems = transformSections(items);

        final DiffCallback callback = new DiffCallback(viewItems, this.viewItems);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.viewItems.clear();
        this.viewItems.addAll(viewItems);

        result.dispatchUpdatesTo(this);
    }

    private class DiffCallback extends DiffUtil.Callback {
        private ArrayList<Object> newList = new ArrayList<>();
        private ArrayList<Object> oldList = new ArrayList<>();

        DiffCallback(ArrayList<Object> newList, ArrayList<Object> oldList) {
            this.newList.clear();
            this.newList.addAll(newList);

            this.oldList.clear();
            this.oldList.addAll(oldList);
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if (oldItem instanceof String) {
                return oldItem.equals(newItem);
            } else
                return oldItem instanceof ListItem && oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if (oldItem instanceof String) {
                return oldItem.equals(newItem);
            } else
                return oldItem instanceof ListItem && ((ListItem) oldItem).getId().equals(((ListItem) newItem).getId());
        }
    }
}
