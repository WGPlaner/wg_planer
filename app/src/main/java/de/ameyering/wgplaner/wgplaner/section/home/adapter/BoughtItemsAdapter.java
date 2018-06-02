package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.content.Context;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import io.swagger.client.model.ListItem;


public class BoughtItemsAdapter extends
    RecyclerView.Adapter<BoughtItemsAdapter.BoughtItemsViewItem> {
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int CONTENT_VIEW_TYPE = 1;

    private ArrayList<Object> items = new ArrayList<>();
    private SimpleDateFormat format = null;

    private boolean isActionModeActive = false;
    private ArrayList<ListItem> selectedItems = new ArrayList<>();

    private DataProviderInterface dataProvider;

    private ArrayList<OnAdapterChangeListener> listeners = new ArrayList<>();

    public interface OnAdapterChangeListener {

        void onItemSelected(int selectedCount);

        void onItemUnselected(int selectedCount);

        void onItemClicked(UUID itemUid);
    }

    public abstract class BoughtItemsViewItem extends RecyclerView.ViewHolder {

        public BoughtItemsViewItem(View itemView) {
            super(itemView);
        }

        public void setData(Object object) {
            setData(object, null);
        }

        public abstract void setData(Object object, Bundle args);
    }

    public class BoughtItemsHeaderItem extends BoughtItemsViewItem {

        private TextView header = null;

        public BoughtItemsHeaderItem(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.bought_items_section_header);
        }

        @Override
        public void setData(Object object, Bundle args) {
            header.setText((String) object);
        }
    }

    public class BoughtItemsContentItem extends BoughtItemsViewItem {
        private TextView name = null;

        private LinearLayout rootContainer = null;

        private CardView container = null;
        private LinearLayout contentContainer = null;

        private LinearLayout containerNumber = null;
        private TextView displayNumber = null;

        private LinearLayout containerRequestedBy = null;
        private TextView displayRequestedBy = null;

        private LinearLayout containerRequestedFor = null;
        private LinearLayout displayRequestedFor = null;

        private LinearLayout containerPrice = null;
        private TextView price = null;

        private LinearLayout checkboxContainer = null;
        private CheckBox checkBox = null;

        private ListItem item = null;

        public BoughtItemsContentItem(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.bought_items_item_product_name);

            rootContainer = itemView.findViewById(R.id.bought_items_container);

            container = itemView.findViewById(R.id.bought_items_item_container);
            contentContainer = itemView.findViewById(R.id.bought_items_content_container);

            container.setOnClickListener(view -> {
                if (item != null && isActionModeActive && checkBox != null && item.getPrice() != null) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item);
                        checkBox.setChecked(false);
                        callAllListenersUnselected();

                    } else {
                        selectedItems.add(item);
                        checkBox.setChecked(true);
                        callAllListenersSelected();
                    }

                } else if (item != null) {
                    callAllListenersClicked(item.getId());
                }
            });

            container.setOnLongClickListener(view -> {
                if (item != null && item.getPrice() != null) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item);
                        checkBox.setChecked(false);
                        callAllListenersUnselected();

                    } else {
                        selectedItems.add(item);
                        checkBox.setChecked(true);
                        callAllListenersSelected();
                    }
                }

                return false;
            });


            containerNumber = itemView.findViewById(R.id.bought_items_item_product_attribute_number);
            displayNumber = itemView.findViewById(R.id.bought_items_item_product_number);

            containerRequestedBy = itemView.findViewById(
                    R.id.bought_items_item_product_attribute_requested_by);
            displayRequestedBy = itemView.findViewById(R.id.bought_items_item_product_requested_by);

            containerRequestedFor = itemView.findViewById(
                    R.id.bought_items_item_product_attribute_requested_for);
            displayRequestedFor = itemView.findViewById(R.id.bought_items_item_product_requested_for);

            containerPrice = itemView.findViewById(R.id.bought_items_item_product_attribute_price);
            price = itemView.findViewById(R.id.bought_items_item_product_price);

            checkboxContainer = itemView.findViewById(R.id.bought_items_checkbox_container);
            checkBox = itemView.findViewById(R.id.bought_items_checkbox);

            checkBox.setOnClickListener(view -> {
                if (isActionModeActive && item.getPrice() != null) {
                    if (item != null && !selectedItems.contains(item)) {
                        selectedItems.add(item);
                        callAllListenersSelected();

                    } else if (item != null) {
                        selectedItems.remove(item);
                        callAllListenersUnselected();
                    }
                }
            });

            container = itemView.findViewById(R.id.bought_items_item_container);
        }

        @Override
        public void setData(Object object, Bundle args) {
            if (object != null) {
                item = (ListItem) object;
            }

            if (item != null) {
                name.setText(item.getTitle());
                displayNumber.setText("" + item.getCount());
                displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                createViews(LayoutInflater.from(displayRequestedFor.getContext()), displayRequestedFor,
                    item.getRequestedFor());

                boolean checked = selectedItems.contains(item);

                checkBox.setChecked(checked);

                Integer price = item.getPrice();

                if (price != null) {
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    format.setCurrency(dataProvider.getCurrentGroupCurrency());
                    this.price.setText(format.format(((double) item.getPrice()) / 100));

                } else {
                    this.price.setText("");
                }

                if (isActionModeActive && price != null) {
                    TransitionManager.beginDelayedTransition(rootContainer);
                    checkboxContainer.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(rootContainer);
                    checkboxContainer.setVisibility(View.GONE);
                }
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

    public BoughtItemsAdapter(ArrayList<ListItem> items, Context context, DataProviderInterface dataProvider) {
        this.dataProvider = dataProvider;

        Collections.sort(items, (item, t1) -> item.getBoughtAt().compareTo(t1.getBoughtAt()) * -1);

        format = new SimpleDateFormat("dd. MMMM yyyy", context.getResources().getConfiguration().locale);
        this.items.clear();
        this.items.addAll(transformItems(items));
    }

    @Override
    public void onBindViewHolder(BoughtItemsViewItem holder, int position, List<Object> payloads) {
        if (payloads != null && payloads.size() > position) {
            holder.setData(items.get(position), (Bundle) payloads.get(0));

        } else {
            holder.setData(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return HEADER_VIEW_TYPE;

        } else {
            return CONTENT_VIEW_TYPE;
        }
    }

    @Override
    public BoughtItemsViewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.section_bought_items_header_layout, parent, false);

            return new BoughtItemsHeaderItem(item);

        } else if (viewType == CONTENT_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.section_bought_items_item_layout, parent, false);

            return new BoughtItemsContentItem(item);

        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(BoughtItemsViewItem holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private ArrayList<Object> transformItems(ArrayList<ListItem> items) {
        HashMap<String, ArrayList<ListItem>> map = new HashMap<>();

        for (ListItem item : items) {
            String date = format.format(item.getBoughtAt().toDate());

            if (map.containsKey(date)) {
                map.get(date).add(item);

            } else {
                ArrayList<ListItem> list = new ArrayList<>();
                list.add(item);
                map.put(date, list);
            }
        }

        ArrayList<Object> viewItems = new ArrayList<>();

        for (String time : map.keySet()) {
            viewItems.add(time);
            viewItems.addAll(map.get(time));
        }

        return viewItems;
    }

    public void onNewData(ArrayList<ListItem> items) {
        ArrayList<Object> viewItems = transformItems(items);

        final DiffCallback callback = new DiffCallback(viewItems, this.items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.items.clear();
        this.items.addAll(viewItems);

        result.dispatchUpdatesTo(this);
    }

    public void exitActionMode() {
        if (!this.isActionModeActive) {
            return;
        }

        isActionModeActive = false;

        if (!selectedItems.isEmpty()) {
            selectedItems.clear();
        }

        updateActionMode();
    }

    private void updateActionMode() {

        final DiffCallback callback = new DiffCallback(this.items, this.items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        result.dispatchUpdatesTo(this);
    }

    public ArrayList<ListItem> getSelectedItems() {
        return selectedItems;
    }

    public void addListener(OnAdapterChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnAdapterChangeListener listener) {
        listeners.remove(listener);
    }

    private void callAllListenersSelected() {
        if (selectedItems.size() == 1) {
            isActionModeActive = true;

            updateActionMode();
        }

        for (OnAdapterChangeListener listener : listeners) {
            listener.onItemSelected(selectedItems.size());
        }
    }

    private void callAllListenersUnselected() {
        if (selectedItems.size() == 0) {
            isActionModeActive = false;

            updateActionMode();
        }

        for (OnAdapterChangeListener listener : listeners) {
            listener.onItemUnselected(selectedItems.size());
        }
    }

    private void callAllListenersClicked(UUID itemUid) {
        for (OnAdapterChangeListener listener : listeners) {
            listener.onItemClicked(itemUid);
        }
    }

    private class DiffCallback extends DiffUtil.Callback {
        public static final String DISPLAY_CHECKBOX = "DisplayCheckbox";
        private ArrayList<Object> newList = new ArrayList<>();
        private ArrayList<Object> oldList = new ArrayList<>();

        public DiffCallback(ArrayList<Object> newList, ArrayList<Object> oldList) {
            this.newList.clear();
            this.newList.addAll(newList);
            this.oldList.clear();
            this.oldList.addAll(oldList);
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            return oldItem.getClass() == newItem.getClass() &&
                !(oldItem instanceof ListItem &&
                    !((ListItem) oldItem).getId().equals(((ListItem) oldItem).getId()));

        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
}
