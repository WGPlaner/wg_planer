package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.structure.Item;


public class ShoppingListCategoryAdapter extends
    RecyclerView.Adapter<ShoppingListCategoryAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView itemRecycler;
        public TextView categoryName;
        public CardView category;

        public ViewHolder(View item) {
            super(item);

            itemRecycler = item.findViewById(R.id.section_shopping_list_recycler_view);
            categoryName = item.findViewById(R.id.shopping_list_category_header);
            category = item.findViewById(R.id.shopping_list_category_container);
        }

        public void initialize(ArrayList<Item> items, String header) {

        }
    }

    private Context context;

    private HashMap<String, ArrayList<Item>> items = new HashMap<>();

    public ShoppingListCategoryAdapter(HashMap<String, ArrayList<Item>> items, Context context) {
        if (items != null) {
            this.items = items;
        }

        if (context != null) {
            this.context = context;
        }
    }

    @Override
    public int getItemCount() {
        return items.keySet().size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_category_layout, null, false);
        ViewHolder holder = new ViewHolder(item);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TODO: Implement type ShoppingList
    }

    public void updateItems(ArrayList<Item> items) {
        //TODO: Implement DiffCallback
    }
}
