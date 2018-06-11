package de.ameyering.wgplaner.wgplaner.section.home.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.section.home.CreateBillActivity;
import de.ameyering.wgplaner.wgplaner.section.home.ItemDetailActivity;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.BoughtItemsAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnDataChangeListener;
import io.swagger.client.model.ListItem;

public class BoughtItemsFragment extends SectionFragment implements ActionMode.Callback {
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recycler = null;
    private BoughtItemsAdapter adapter = null;
    private TextView isEmptyView = null;

    private DataProviderInterface dataProvider;

    private ActionMode actionMode = null;

    private BoughtItemsAdapter.OnAdapterChangeListener onAdapterChangeListener = new
    BoughtItemsAdapter.OnAdapterChangeListener() {
        @Override
        public void onItemSelected(int selectedCount) {
            if (actionMode == null && selectedCount == 1) {
                actionMode = ((AppCompatActivity) BoughtItemsFragment.this.getContext()).startActionMode(
                        BoughtItemsFragment.this);
            }

            String title = selectedCount + " " + getActivity().getString(
                    R.string.bought_items_action_mode_selected_label);
            actionMode.setTitle(title);
        }

        @Override
        public void onItemUnselected(int selectedCount) {
            if (actionMode != null && selectedCount == 0) {
                actionMode.finish();
                actionMode = null;

            } else {
                String title = selectedCount + " " + getActivity().getString(
                        R.string.bought_items_action_mode_selected_label);
                actionMode.setTitle(title);
            }
        }

        @Override
        public void onItemClicked(UUID itemUid) {
            Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
            intent.putExtra(Intent.EXTRA_UID, itemUid.toString());
            getActivity().startActivityForResult(intent, 0);
        }
    };

    private OnDataChangeListener onDataChangeListener = type -> {
        if (type == DataProviderInterface.DataType.BOUGHT_ITEMS && adapter != null) {
            onNewData(dataProvider.getBoughtItems());
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_bought_items, container, false);

        WGPlanerApplication application = (WGPlanerApplication) getActivity().getApplication();
        dataProvider = application.getDataProviderInterface();

        if (actionBar != null) {
            actionBar.setTitle(R.string.section_title_bought_items);
        }

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        if (isEmptyView == null) {
            isEmptyView = view.findViewById(R.id.section_bought_items_empty);
        }

        swipeRefreshLayout = view.findViewById(R.id.bought_items_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(() -> {
            dataProvider.syncBoughtItems();
            BoughtItemsFragment.this.getActivity().runOnUiThread(() -> {
                if (actionMode != null) {
                    actionMode.finish();
                }
                onNewData(dataProvider.getBoughtItems());
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start());

        recycler = view.findViewById(R.id.section_bought_items_list);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(false);

        if (adapter == null) {
            adapter = new BoughtItemsAdapter(dataProvider.getBoughtItems(), getContext(), dataProvider);
        }

        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        new Thread(() -> dataProvider.syncBoughtItems()).start();
        super.onStart();
    }

    @Override
    public void onResume() {
        adapter.addListener(onAdapterChangeListener);
        dataProvider.addOnDataChangeListener(onDataChangeListener);

        super.onResume();
    }

    @Override
    public void onPause() {
        adapter.removeListener(onAdapterChangeListener);
        dataProvider.removeOnDataChangeListener(onDataChangeListener);
        super.onPause();
    }

    public void onNewData(ArrayList<ListItem> items) {
        if (adapter != null) {
            getActivity().runOnUiThread(() -> {
                adapter.onNewData(items);

                if (items.size() == 0) {
                    isEmptyView.setVisibility(View.VISIBLE);

                    if (actionMode != null) {
                        actionMode.finish();
                        actionMode = null;
                    }

                } else {
                    isEmptyView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.menu_action_mode_bought_items, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bought_items_action_mode_menu_bill:
                List<ListItem> items = adapter.getSelectedItems();
                ArrayList<String> itemUids = new ArrayList<>();

                for (ListItem item : items) {
                    itemUids.add(item.getId().toString());
                }

                Intent intent = new Intent(getActivity(), CreateBillActivity.class);
                intent.putStringArrayListExtra(Intent.EXTRA_UID, itemUids);

                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        adapter.exitActionMode();
    }
}
