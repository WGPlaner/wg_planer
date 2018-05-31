package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.AddItemActivity;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.ShoppingListAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;

public class ShoppingListFragment extends SectionFragment {
    private static final int REQ_CODE_ADD_ITEM = 0;
    public static final int SORT_REQUESTED_FOR = 0;
    public static final int SORT_REQUESTED_BY = 1;
    public static final int SORT_CATEGORY = 2;
    public static final int SORT_NAME = 3;

    private RecyclerView categories;
    private ShoppingListAdapter adapter;
    private SwipeRefreshLayout swipeToRefresh;
    private TextView no_items;

    private DataProvider dataProvider = DataProvider.getInstance();

    private DataProvider.OnDataChangeListener shoppingListListener = type -> {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (type == DataProvider.DataType.SELECTED_ITEMS) {
                    if (floatingActionButton != null) {
                        changeFloatingActionButton();
                    }

                } else if (type == DataProvider.DataType.SHOPPING_LIST) {
                    onNewData(dataProvider.getCurrentShoppingList());

                } else if (type == DataProvider.DataType.CURRENT_GROUP_MEMBERS) {
                    onNewData(dataProvider.getCurrentShoppingList());
                }
            });
        }
    };

    private ArrayList<ListItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_shopping_list, container, false);

        if (actionBar != null) {
            actionBar.setTitle(R.string.section_title_shopping_list);
        }

        if (floatingActionButton != null) {
            changeFloatingActionButton();
        }

        categories = view.findViewById(R.id.section_shopping_list_recycler_view);
        categories.setLayoutManager(new LinearLayoutManager(getContext()));
        categories.setHasFixedSize(false);

        no_items = view.findViewById(R.id.section_shopping_list_no_items);

        if (adapter == null) {
            items.clear();
            items.addAll(dataProvider.getCurrentShoppingList());

            if (items.size() == 0) {
                no_items.setVisibility(View.VISIBLE);

            } else {
                no_items.setVisibility(View.GONE);
            }

            adapter = new ShoppingListAdapter(items);
        }

        categories.setAdapter(adapter);

        swipeToRefresh = view.findViewById(R.id.shopping_list_swipe_to_refresh);
        swipeToRefresh.setOnRefreshListener(() -> new Thread(() -> {
            ApiResponse<ShoppingList> result = dataProvider.syncShoppingList();

            if (result == null || result.getData() == null) {
                getActivity()
                .runOnUiThread(() -> {
                    swipeToRefresh.setRefreshing(false);
                    Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_LONG).show();
                });

            } else {
                getActivity().runOnUiThread(() -> swipeToRefresh.setRefreshing(false));
            }
        }).start());

        if (shoppingListListener == null) {
            dataProvider.addOnDataChangeListener(shoppingListListener);
        }

        setHasOptionsMenu(true);

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

        getActivity().runOnUiThread(() -> {
            if (items.size() == 0) {
                no_items.setVisibility(View.VISIBLE);

            } else {
                no_items.setVisibility(View.GONE);
            }
        });

        if (adapter != null) {
            adapter.onNewData(this.items);
        }
    }

    public void onNewData(int sorting) {
        if (adapter != null) {
            adapter.onNewData(sorting);
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
                /**
                 * No implementation needed
                 * @param animation
                 */
                @Override
                public void onAnimationStart(Animation animation) {

                }

                /**
                 * No implementation needed
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_add_white));
                    floatingActionButton.setOnClickListener(view -> {
                        Intent intent = new Intent(getActivity(), AddItemActivity.class);
                        startActivityForResult(intent, REQ_CODE_ADD_ITEM);
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
                /**
                 * No implementation needed
                 * @param animation
                 */
                @Override
                public void onAnimationStart(Animation animation) {

                }

                /**
                 * No implementation needed
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_check_white));
                    floatingActionButton.setOnClickListener(view -> {
                        dataProvider.buySelection(new ServerCallsInterface.OnAsyncCallListener<SuccessResponse>() {
                            @Override
                            public void onFailure(ApiException e) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), getString(R.string.server_connection_failed), Toast.LENGTH_LONG)
                                    .show();
                                });
                            }

                            @Override
                            public void onSuccess(SuccessResponse result) {

                            }
                        });
                    });
                }
            });

            floatingActionButton.startAnimation(anim);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shopping_list_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.action_sort));
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_shopping_list_sort, popup.getMenu());

            popup.setOnMenuItemClickListener(item1 -> {
                if (item1.getItemId() == R.id.action_sort_by_requested_for) {
                    ShoppingListFragment.this.onNewData(SORT_REQUESTED_FOR);

                } else if (item1.getItemId() == R.id.action_sort_by_requested_by) {
                    ShoppingListFragment.this.onNewData(SORT_REQUESTED_BY);

                } else if (item1.getItemId() == R.id.action_sort_by_category) {
                    ShoppingListFragment.this.onNewData(SORT_CATEGORY);

                } else if (item1.getItemId() == R.id.action_sort_name) {
                    ShoppingListFragment.this.onNewData(SORT_NAME);

                } else {
                    return false;
                }

                return true;
            });

            popup.show();
            return true;
        }

        return false;
    }
}
