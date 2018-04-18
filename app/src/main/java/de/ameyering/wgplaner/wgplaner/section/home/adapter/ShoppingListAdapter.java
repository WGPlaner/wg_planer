package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.ShoppingListFragment;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;

public class ShoppingListAdapter extends
    RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewItem> {
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    private ArrayList<ListItem> items = new ArrayList<>();
    private ArrayList<Object> viewItems = new ArrayList<>();
    private static DataProvider dataProvider = DataProvider.getInstance();
    private int sortBy = 0;

    public abstract class ShoppingListViewItem extends RecyclerView.ViewHolder {

        private ShoppingListViewItem(View itemView) {
            super(itemView);
        }

        public abstract void setData(Object object, Bundle args);

        public void setData(Object object) {
            setData(object, null);
        }
    }

    public class ShoppingListViewItemHeader extends ShoppingListViewItem {

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

    public class ShoppingListViewItemContent extends ShoppingListViewItem {
        private TextView name = null;

        private CardView container = null;

        private LinearLayout attributeContainer = null;

        private LinearLayout containerNumber = null;
        private TextView displayNumber = null;

        private LinearLayout containerRequestedBy = null;
        private TextView displayRequestedBy = null;

        private LinearLayout containerRequestedFor = null;
        private LinearLayout displayRequestedFor = null;

        private CheckBox checkbox = null;

        private ListItem item = null;

        ShoppingListViewItemContent(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.shopping_list_item_product_name);

            container = itemView.findViewById(R.id.shopping_list_item_container);

            attributeContainer = itemView.findViewById(R.id.shopping_list_item_attribute_container);

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
            if (object instanceof ListItem) {
                item = (ListItem) object;
            }

            if (sortBy == ShoppingListFragment.SORT_REQUESTED_FOR) {
                TransitionManager.beginDelayedTransition(attributeContainer);
                containerRequestedFor.setVisibility(View.GONE);
                containerRequestedBy.setVisibility(View.VISIBLE);

            } else if (sortBy == ShoppingListFragment.SORT_REQUESTED_BY) {
                TransitionManager.beginDelayedTransition(attributeContainer);
                containerRequestedFor.setVisibility(View.VISIBLE);
                containerRequestedBy.setVisibility(View.GONE);

            } else if (sortBy == ShoppingListFragment.SORT_CATEGORY) {
                TransitionManager.beginDelayedTransition(attributeContainer);
                containerRequestedFor.setVisibility(View.VISIBLE);
                containerRequestedBy.setVisibility(View.VISIBLE);

            } else if (sortBy == ShoppingListFragment.SORT_NAME) {
                containerRequestedFor.setVisibility(View.VISIBLE);
                containerRequestedBy.setVisibility(View.VISIBLE);
            }

            if (item != null) {
                name.setText(item.getTitle());
                displayNumber.setText(item.getCount().toString());
                displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                createViews(LayoutInflater.from(displayRequestedFor.getContext()), displayRequestedFor,
                    item.getRequestedFor());

                checkbox.setChecked(dataProvider.isItemSelected(item));
            }
        }

        private void createViews(LayoutInflater inflater, ViewGroup layout, List<String> users) {
            TransitionManager.beginDelayedTransition(layout, new ChangeBounds());
            layout.removeAllViews();

            for (String user : users) {
                TextView requestedForUser = (TextView) inflater.inflate(
                        R.layout.section_shopping_list_item_request_for_layout, layout, false);
                requestedForUser.setText(dataProvider.getUserByUid(user).getDisplayName());
                layout.addView(requestedForUser);
            }
        }
    }

    public ShoppingListAdapter(ArrayList<ListItem> items) {
        this.items = items;
        viewItems = sortBy(items, sortBy);
    }

    @Override
    public void onBindViewHolder(ShoppingListViewItem holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            holder.setData(viewItems.get(position), null);

        } else {
            holder.setData(viewItems.get(position), (Bundle) payloads.get(0));
        }
    }

    @Override
    public ShoppingListViewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
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

        if (viewItem instanceof String) {
            return HEADER_VIEW_TYPE;

        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return viewItems.size();
    }

    public void onNewData(int sortBy) {
        this.sortBy = sortBy;
        onNewData(items);
    }

    public void onNewData(ArrayList<ListItem> items) {
        ArrayList<Object> viewItems = sortBy(items, sortBy);

        final DiffCallback callback = new DiffCallback(viewItems, this.viewItems);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.viewItems.clear();
        this.viewItems.addAll(viewItems);

        result.dispatchUpdatesTo(this);
    }

    private ArrayList<Object> sortBy(ArrayList<ListItem> items, int sorting) {
        switch (sorting) {
            case ShoppingListFragment.SORT_REQUESTED_FOR:
                return sortByRequestedFor(items);

            case ShoppingListFragment.SORT_REQUESTED_BY:
                return sortByRequestedBy(items);

            case ShoppingListFragment.SORT_CATEGORY:
                return sortByCategory(items);

            case ShoppingListFragment.SORT_NAME:
                return sortByName(items);

            default:
                return new ArrayList<>();
        }
    }

    private ArrayList<Object> sortByName(ArrayList<ListItem> items) {
        Collections.sort(items, (item, t1) -> item.getTitle().compareTo(t1.getTitle()));

        ArrayList<Object> viewItems = new ArrayList<>();
        viewItems.addAll(items);
        return viewItems;
    }

    private ArrayList<Object> sortByCategory(ArrayList<ListItem> items) {
        HashMap<String, ArrayList<ListItem>> requestedBy = new HashMap<>();

        for (ListItem item : items) {
            String category = item.getCategory();

            if (category == null) {
                category = "null";
            }

            if (requestedBy.containsKey(category)) {
                requestedBy.get(category).add(item);

            } else {
                ArrayList<ListItem> specificItems = new ArrayList<>();
                specificItems.add(item);
                requestedBy.put(category, specificItems);
            }
        }

        ArrayList<Object> objects = new ArrayList<>();

        for (String category : requestedBy.keySet()) {
            objects.add(category);
            objects.addAll(requestedBy.get(category));
        }

        return objects;
    }

    private ArrayList<Object> sortByRequestedBy(ArrayList<ListItem> items) {
        HashMap<String, ArrayList<ListItem>> requestedBy = new HashMap<>();

        for (ListItem item : items) {
            String user = dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName();

            if (requestedBy.containsKey(user)) {
                requestedBy.get(user).add(item);

            } else {
                ArrayList<ListItem> specificItems = new ArrayList<>();
                specificItems.add(item);
                requestedBy.put(user, specificItems);
            }
        }

        ArrayList<Object> objects = new ArrayList<>();

        for (String user : requestedBy.keySet()) {
            objects.add(user);
            objects.addAll(requestedBy.get(user));
        }

        return objects;
    }

    private ArrayList<Object> sortByRequestedFor(ArrayList<ListItem> items) {
        HashMap<List<String>, ArrayList<ListItem>> requestedFor = new HashMap<>();

        for (ListItem item : items) {
            List<String> users = item.getRequestedFor();
            Collections.sort(item.getRequestedFor());

            if (requestedFor.containsKey(users)) {
                requestedFor.get(users).add(item);

            } else {
                ArrayList<ListItem> specificItems = new ArrayList<>();
                specificItems.add(item);
                requestedFor.put(users, specificItems);
            }
        }

        ArrayList<Object> viewItems = new ArrayList<>();

        for (List<String> uids : requestedFor.keySet()) {
            String header = "";

            if (uids.size() == dataProvider.getCurrentGroupMembers().size()) {
                header = dataProvider.getCurrentGroupName();

            } else {
                StringBuffer buffer = new StringBuffer();

                for (int i = 0; i < uids.size(); i++) {
                    buffer.append(dataProvider.getUserByUid(uids.get(i)).getDisplayName());

                    if (i != uids.size() - 1) {
                        buffer.append(", ");
                    }
                }

                header = buffer.toString();
            }

            viewItems.add(header);
            viewItems.addAll(requestedFor.get(uids));
        }

        return viewItems;
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
            return false;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if (oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if (oldItem instanceof String) {
                return oldItem.equals(newItem);

            } else {
                return oldItem instanceof ListItem &&
                    ((ListItem) oldItem).getId().equals(((ListItem) newItem).getId());
            }
        }
    }
}
