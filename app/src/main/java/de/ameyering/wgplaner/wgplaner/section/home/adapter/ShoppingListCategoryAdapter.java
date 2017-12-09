package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.structure.CategoryHolder;
import io.swagger.client.model.ListItem;


public class ShoppingListCategoryAdapter extends
    RecyclerView.Adapter<ShoppingListCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryHolder> items = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView itemRecycler;
        public TextView categoryName;
        public CardView category;

        ShoppingListItemAdapter adapter;

        CategoryHolder holder;

        public ViewHolder(View item) {
            super(item);

            itemRecycler = item.findViewById(R.id.shopping_list_category_recycler);
            categoryName = item.findViewById(R.id.shopping_list_category_header);
            category = item.findViewById(R.id.shopping_list_category_container);
        }

        public void initialize(CategoryHolder categoryHolder) {
            this.holder = categoryHolder;

            categoryName.setText(categoryHolder.getHeader());

            itemRecycler.setLayoutManager(new LinearLayoutManager(context));
            itemRecycler.setHasFixedSize(false);
            itemRecycler.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            adapter = new ShoppingListItemAdapter(categoryHolder.getItems(), context);

            itemRecycler.setAdapter(adapter);
        }

        public void updateContent(CategoryHolder categoryHolder, Bundle args) {
            if (args == null) {
                return;
            }

            int header = args.getInt(CategoryDiffCallback.HEADER);
            int items = args.getInt(CategoryDiffCallback.ITEMS);

            if (header == CategoryDiffCallback.TRUE_VALUE) {
                categoryName.setText(categoryHolder.getHeader());
            }

            if (items == CategoryDiffCallback.TRUE_VALUE) {
                adapter.onNewData(categoryHolder.getItems());
            }

            this.holder = categoryHolder;
        }
    }

    public ShoppingListCategoryAdapter(ArrayList<CategoryHolder> items, Context context) {
        if (items != null) {
            this.items = items;
        }

        if (context != null) {
            this.context = context;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_category_layout, parent, false);

        return new ViewHolder(item);
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
            Bundle args = (Bundle) payloads.get(0);
            holder.updateContent(items.get(position), args);
        }
    }

    public void onNewData(ArrayList<CategoryHolder> items) {
        final CategoryDiffCallback callback = new CategoryDiffCallback(items, this.items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.items.clear();
        this.items.addAll(items);

        result.dispatchUpdatesTo(this);
    }

    private class CategoryDiffCallback extends DiffUtil.Callback {
        public static final String HEADER = "header";
        public static final String ITEMS = "items";
        public static final int FALSE_VALUE = 0;
        public static final int TRUE_VALUE = 1;

        private ArrayList<CategoryHolder> newList = new ArrayList<>();
        private ArrayList<CategoryHolder> oldList = new ArrayList<>();

        public CategoryDiffCallback(ArrayList<CategoryHolder> newList, ArrayList<CategoryHolder> oldList) {
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
            ArrayList<ListItem> oldItems = oldList.get(oldItemPosition).getItems();
            ArrayList<ListItem> newItems = newList.get(newItemPosition).getItems();

            if (oldItems.size() != newItems.size()) {
                return false;
            }

            for (int i = 0; i < oldItems.size(); i++) {
                ListItem oldItem = oldItems.get(i);
                ListItem newItem = newItems.get(i);

                if (!oldItem.equals(newItem)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            CategoryHolder oldHolder = oldList.get(oldItemPosition);
            CategoryHolder newHolder = newList.get(newItemPosition);

            return oldHolder.getHeader().equals(newHolder.getHeader());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            CategoryHolder oldHolder = oldList.get(oldItemPosition);
            CategoryHolder newHolder = newList.get(newItemPosition);

            int header = TRUE_VALUE;
            int items = TRUE_VALUE;

            Bundle diff = new Bundle();

            if (!newHolder.getHeader().equals(oldHolder.getHeader())) {
                diff.putInt(HEADER, TRUE_VALUE);

            } else {
                header = FALSE_VALUE;
                diff.putInt(HEADER, FALSE_VALUE);
            }

            ArrayList<ListItem> oldItems = oldHolder.getItems();
            ArrayList<ListItem> newItems = newHolder.getItems();

            if (oldItems.size() == newItems.size()) {
                boolean hasToChange = false;

                for (int i = 0; i < oldItems.size(); i++) {
                    ListItem oldItem = oldItems.get(i);
                    ListItem newItem = newItems.get(i);

                    if (!oldItem.equals(newItem)) {
                        hasToChange = true;
                        break;
                    }
                }

                if (hasToChange) {
                    diff.putInt(ITEMS, TRUE_VALUE);

                } else {
                    items = FALSE_VALUE;
                    diff.putInt(ITEMS, FALSE_VALUE);
                }

            } else {
                diff.putInt(ITEMS, TRUE_VALUE);
            }

            if (header == FALSE_VALUE && items == FALSE_VALUE) {
                return null;
            }

            return diff;
        }
    }
}
