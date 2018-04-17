package de.ameyering.wgplaner.wgplaner.section.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.BoughtItemsAdapter;
import io.swagger.client.model.ListItem;

public class BoughtItemsFragment extends SectionFragment {
    private RecyclerView recycler = null;
    private BoughtItemsAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section_bought_items, container, false);

        if (toolbar != null) {
            if (title != null) {
                toolbar.setSubtitle(title);

            } else {
                toolbar.setSubtitle(R.string.section_title_shopping_list);
            }
        }

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

        if(recycler == null) {
            recycler = view.findViewById(R.id.section_bought_items_list);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setHasFixedSize(false);
        }

        if(adapter == null) {
            adapter = new BoughtItemsAdapter(new ArrayList<ListItem>(), getContext());
            recycler.setAdapter(adapter);
        }

        return view;
    }
}
