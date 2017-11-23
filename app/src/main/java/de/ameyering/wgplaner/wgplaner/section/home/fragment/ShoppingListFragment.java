package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.AddItemActivity;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.ShoppingListCategoryAdapter;
import de.ameyering.wgplaner.wgplaner.structure.CategoryHolder;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;

public class ShoppingListFragment extends SectionFragment {
    private static final int REQ_CODE_ADD_ITEM = 0;

    private RecyclerView categories;
    private ShoppingListCategoryAdapter adapter;
    private DataProvider.OnUpdatedDataListener shoppingListListener = null;

    private ArrayList<ListItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_shopping_list, container, false);

        if (toolbar != null) {
            if (title != null) {
                toolbar.setSubtitle(title);

            } else {
                toolbar.setSubtitle(R.string.section_title_shopping_list);
            }
        }

        if (floatingActionButton != null) {
            if (DataProvider.ShoppingList.getSelectedShoppingListCount() == 0) {
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_add_white));
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AddItemActivity.class);
                        startActivityForResult(intent, REQ_CODE_ADD_ITEM);
                    }
                });

            } else {
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_check_white));
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataProvider.ShoppingList.buySelection();
                        onNewData(DataProvider.ShoppingList.getShoppingList());
                    }
                });
            }
        }

        if (categories == null) {
            categories = view.findViewById(R.id.section_shopping_list_recycler_view);
            categories.setLayoutManager(new LinearLayoutManager(getContext()));
            categories.setHasFixedSize(false);
        }

        if (adapter == null) {
            items.clear();
            items.addAll(DataProvider.ShoppingList.getShoppingList());
            ArrayList<CategoryHolder> holders = CategoryHolder.orderByCategory(getContext(),
                    CategoryHolder.Category.REQUESTED_FOR, items);
            adapter = new ShoppingListCategoryAdapter(holders, getContext());

            categories.setAdapter(adapter);
        }

        if (shoppingListListener == null) {
            shoppingListListener = new DataProvider.OnUpdatedDataListener() {
                @Override
                public void onUpdatedData() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNewData(DataProvider.ShoppingList.getShoppingList());

                            if (DataProvider.ShoppingList.getSelectedShoppingListCount() == 0) {
                                floatingActionButton.setVisibility(View.VISIBLE);
                                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                                        R.drawable.ic_add_white));
                                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), AddItemActivity.class);
                                        startActivityForResult(intent, REQ_CODE_ADD_ITEM);
                                    }
                                });

                            } else {
                                floatingActionButton.setVisibility(View.VISIBLE);
                                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                                        R.drawable.ic_check_white));
                                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DataProvider.ShoppingList.buySelection();
                                        onNewData(DataProvider.ShoppingList.getShoppingList());
                                    }
                                });
                            }
                        }
                    });
                }
            };

            DataProvider.ShoppingList.addOnUpdatedDataListener(shoppingListListener);
        }

        return view;
    }

    public void onNewData(ArrayList<ListItem> items) {
        this.items.clear();
        this.items.addAll(items);

        ArrayList<CategoryHolder> holders = CategoryHolder.orderByCategory(getContext(),
                CategoryHolder.Category.REQUESTED_FOR, this.items);

        if (adapter != null) {
            adapter.onNewData(holders);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_ADD_ITEM: {
                if (resultCode == Activity.RESULT_OK) {
                    onNewData(DataProvider.ShoppingList.getShoppingList());
                }
            }
            break;
        }
    }
}
