package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.section.home.BillDetailActivity;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.BillsContentAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnDataChangeListener;
import io.swagger.client.model.Bill;


public class ReceivedBillsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BillsContentAdapter adapter;
    private TextView isEmptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DataProviderInterface dataProvider;

    private OnDataChangeListener billsListener = type -> {
        if (type == DataProviderInterface.DataType.BILLS) {
            onNewData(dataProvider.getReceivedBills());
        }
    };

    private BillsContentAdapter.OnItemTouchListener listener = uuid -> {
        Intent intent = new Intent(getActivity(), BillDetailActivity.class);
        intent.putExtra(Intent.EXTRA_UID, uuid.toString());

        startActivityForResult(intent, 0);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        WGPlanerApplication application = (WGPlanerApplication) getActivity().getApplication();
        dataProvider = application.getDataProviderInterface();

        dataProvider.syncBillList();

        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_bills_content, container, false);

        if (recyclerView == null) {
            recyclerView = view.findViewById(R.id.section_bills_content_recycler);
        }

        if (swipeRefreshLayout == null) {
            swipeRefreshLayout = view.findViewById(R.id.bills_content_swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(() -> new Thread(() -> {
                dataProvider.syncBillList();
                ReceivedBillsFragment.this.getActivity().runOnUiThread(() -> {
                    onNewData(dataProvider.getReceivedBills());
                    swipeRefreshLayout.setRefreshing(false);
                });
            }).start());
        }

        if (isEmptyView == null) {
            isEmptyView = view.findViewById(R.id.bills_content_empty);
        }

        if (adapter == null) {
            adapter = new BillsContentAdapter(new ArrayList<>(), getContext(), dataProvider);
            onNewData(dataProvider.getReceivedBills());
            adapter.addOnTouchListener(listener);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        return view;
    }

    @Override
    public void onResume() {
        dataProvider.addOnDataChangeListener(billsListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        dataProvider.removeOnDataChangeListener(billsListener);
        super.onPause();
    }

    private void onNewData(List<Bill> bills) {
        if (bills != null) {
            getActivity().runOnUiThread(() -> {
                adapter.onNewData(sort(bills));

                if (bills.size() == 0) {
                    isEmptyView.setVisibility(View.VISIBLE);

                } else {
                    isEmptyView.setVisibility(View.GONE);
                }
            });
        }
    }

    private List<Bill> sort(List<Bill> bills) {
        Collections.sort(bills, (bill, t1) ->  {
            if (bill.getDueDate().compareTo(t1.getDueDate()) == 0) {
                if (bill.getState().equals(t1.getState())) {
                    return 0;

                } else if (bill.getState().equals("confirmed paid")) {
                    return 1;

                } else if (bill.getState().equals("paid")) {
                    if (t1.getState().equals("confirmed paid")) {
                        return -1;

                    } else {
                        return 1;
                    }

                } else {
                    return -1;
                }
            }

            return bill.getDueDate().compareTo(t1.getDueDate());
        });

        return bills;
    }
}
