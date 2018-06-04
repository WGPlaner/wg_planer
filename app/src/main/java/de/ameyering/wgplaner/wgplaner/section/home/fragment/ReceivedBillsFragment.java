package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.BillsContentAdapter;
import io.swagger.client.model.Bill;


public class ReceivedBillsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BillsContentAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_bills_content, container, false);

        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.section_bills_content_recycler);
        }

        if(adapter == null) {
            adapter = new BillsContentAdapter(new ArrayList<>(), getContext());
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        return view;
    }

    private void onNewData(List<Bill> bills) {
        if(bills != null) {
            getActivity().runOnUiThread(() -> adapter.onNewData(bills));
        }
    }

    private void sortBy() {

    }
}
