package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.AddItemActivity;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.ShoppingListAdapter;
import de.ameyering.wgplaner.wgplaner.structure.CategoryHolder;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;

public class ShoppingListFragment extends SectionFragment {
    private static final int REQ_CODE_ADD_ITEM = 0;

    private RecyclerView categories;
    private ShoppingListAdapter adapter;
    private DataProvider.OnDataChangeListener shoppingListListener = new
    DataProvider.OnDataChangeListener() {

        @Override
        public void onDataChanged(final DataProvider.DataType type) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type == DataProvider.DataType.SELECTED_ITEMS) {
                            if (floatingActionButton != null) {
                                changeFloatingActionButton();
                            }

                        } else if (type == DataProvider.DataType.SHOPPING_LIST) {
                            onNewData(dataProvider.getCurrentShoppingList());

                        } else if (type == DataProvider.DataType.CURRENT_GROUP_MEMBERS) {
                            onNewData(dataProvider.getCurrentShoppingList());
                        }
                    }
                });
            }
        }
    };
    private SwipeRefreshLayout swipeToRefresh;

    private DataProvider dataProvider = DataProvider.getInstance();

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
            changeFloatingActionButton();
        }

        if (categories == null) {
            categories = view.findViewById(R.id.section_shopping_list_recycler_view);
            categories.setLayoutManager(new LinearLayoutManager(getContext()));
            categories.setHasFixedSize(false);
        }

        if (adapter == null) {
            items.clear();
            items.addAll(dataProvider.getCurrentShoppingList());
            ArrayList<CategoryHolder> holders = CategoryHolder.orderByCategory(getContext(),
                    CategoryHolder.Category.REQUESTED_FOR, items);
            adapter = new ShoppingListAdapter(holders);

            categories.setAdapter(adapter);
        }

        swipeToRefresh = view.findViewById(R.id.shopping_list_swipe_to_refresh);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ApiResponse<ShoppingList> result = dataProvider.syncShoppingList();

                        if(result == null || result.getData() == null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeToRefresh.setRefreshing(false);
                                    Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeToRefresh.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        if (shoppingListListener == null) {
            dataProvider.addOnDataChangeListener(shoppingListListener);
        }

        return view;
    }

    @Override
    public void onResume() {
        onNewData(dataProvider.getCurrentShoppingList());
        dataProvider.addOnDataChangeListener(shoppingListListener);
        super.onResume();
    }

    @Override
    public void onStart() {
        dataProvider.syncShoppingList();
        super.onStart();
    }

    @Override
    public void onPause() {
        dataProvider.removeOnDataChangeListener(shoppingListListener);
        super.onPause();
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
                    onNewData(dataProvider.getCurrentShoppingList());
                }
            }
            break;
        }
    }

    private void changeFloatingActionButton() {
        if (!dataProvider.isSomethingSelected()) {
            floatingActionButton.setVisibility(View.VISIBLE);

            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(200);
            anim.setRepeatCount(1);
            anim.setRepeatMode(Animation.REVERSE);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_add_white));
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), AddItemActivity.class);
                            startActivityForResult(intent, REQ_CODE_ADD_ITEM);
                        }
                    });
                }
            });

            floatingActionButton.startAnimation(anim);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);

            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(200);
            anim.setRepeatCount(1);
            anim.setRepeatMode(Animation.REVERSE);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_check_white));
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dataProvider.buySelection();
                        }
                    });
                }
            });

            floatingActionButton.startAnimation(anim);
        }
    }
}
