package de.ameyering.wgplaner.wgplaner.structure;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;

public class CategoryHolder {
    private static DataProvider dataProvider = DataProvider.getInstance();

    private String header = "";
    private ArrayList<ListItem> items = new ArrayList<>();

    public enum Category {
        REQUESTED_BY, REQUESTED_FOR
    }

    private CategoryHolder(String header) {
        this.header = header;
    }

    private CategoryHolder(String header, ArrayList<ListItem> items) {
        this.header = header;
        this.items.clear();
        this.items.addAll(items);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ListItem> items) {
        this.items = items;
    }

    public static ArrayList<CategoryHolder> orderByCategory(Context context, Category category,
        List<ListItem> items) {
        ArrayList<CategoryHolder> categoryHolders = new ArrayList<>();

        switch (category) {
            case REQUESTED_FOR:
                categoryHolders.addAll(orderByRequestedFor(context, items));
                break;

            case REQUESTED_BY:
                categoryHolders.addAll(orderByRequestedBy(items));
                break;
        }

        return categoryHolders;
    }

    private static ArrayList<CategoryHolder> orderByRequestedBy(List<ListItem> items) {
        ArrayList<String> headers = new ArrayList<>();

        for (ListItem item : items) {
            if (item.getRequestedBy() == null) {
                if (!headers.contains("null")) {
                    headers.add("null");
                }

            } else {
                if (!headers.contains(item.getRequestedBy())) {
                    headers.add(item.getRequestedBy());
                }
            }
        }

        ArrayList<CategoryHolder> categoryHolders = new ArrayList<>();

        for (String header : headers) {
            categoryHolders.add(new CategoryHolder(header));
        }

        for (ListItem item : items) {
            int position = headers.indexOf(item.getRequestedBy());

            categoryHolders.get(position).items.add(item);
        }

        return categoryHolders;
    }

    private static ArrayList<CategoryHolder> orderByRequestedFor(Context context,
        List<ListItem> items) {
        ArrayList<String> headers = new ArrayList<>();

        for (ListItem item : items) {
            if (item.getRequestedFor() == null) {
                if (!headers.contains("null")) {
                    headers.add("null");
                }

            } else {
                List<String> requestedFor = item.getRequestedFor();
                StringBuilder header = new StringBuilder("");

                if (requestedFor.size() == dataProvider.getCurrentGroupMembers().size()) {
                    header.append(context.getString(R.string.group_label));

                } else {
                    for (int i = 0; i < requestedFor.size(); i++) {
                        if (i == 0) {
                            header.append(dataProvider.getUserByUid(requestedFor.get(i)).getDisplayName());

                        } else {
                            header.append(" || ");
                            header.append(dataProvider.getUserByUid(requestedFor.get(
                                        i)).getDisplayName());
                        }
                    }
                }

                if (!headers.contains(header.toString())) {
                    headers.add(header.toString());
                }
            }
        }

        ArrayList<CategoryHolder> categoryHolders = new ArrayList<>();

        for (String header : headers) {
            categoryHolders.add(new CategoryHolder(header));
        }

        for (ListItem item : items) {
            List<String> requestedFor = item.getRequestedFor();
            StringBuilder header = new StringBuilder("");

            if (requestedFor.size() == dataProvider.getCurrentGroupMembers().size()) {
                header.append(context.getString(R.string.group_label));

            } else {
                for (int i = 0; i < requestedFor.size(); i++) {
                    if (i == 0) {
                        header.append(dataProvider.getUserByUid(requestedFor.get(i)).getDisplayName());

                    } else {
                        header.append(" || ");
                        header.append(dataProvider.getUserByUid(requestedFor.get(
                                    i)).getDisplayName());
                    }
                }
            }

            int position = headers.indexOf(header.toString());

            categoryHolders.get(position).items.add(item);
        }

        return categoryHolders;
    }
}
