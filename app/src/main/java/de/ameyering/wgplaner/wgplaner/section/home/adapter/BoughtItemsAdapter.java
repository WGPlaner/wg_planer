package de.ameyering.wgplaner.wgplaner.section.home.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;


public class BoughtItemsAdapter extends RecyclerView.Adapter<BoughtItemsAdapter.BoughtItemsViewItem> {
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int CONTENT_VIEW_TYPE = 1;

    public static abstract class BoughtItemsViewItem extends RecyclerView.ViewHolder {

        public BoughtItemsViewItem(View itemView) {
            super(itemView);
        }

        public void setData(Object object) {
            setData(object, null);
        }

        public abstract void setData(Object object, Bundle args);
    }

    public static class BoughtItemsHeaderItem extends BoughtItemsViewItem {

        private TextView header = null;

        public BoughtItemsHeaderItem(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.bought_items_section_header);
        }

        @Override
        public void setData(Object object, Bundle args) {
            header.setText((String) object);
        }
    }

    public static class BoughtItemsContentItem extends BoughtItemsViewItem {
        private TextView name = null;

        private CardView container = null;

        private LinearLayout containerNumber = null;
        private TextView displayNumber = null;

        private LinearLayout containerRequestedBy = null;
        private TextView displayRequestedBy = null;

        private LinearLayout containerRequestedFor = null;
        private TextView displayRequestedFor = null;

        private LinearLayout containerPrice = null;
        private TextView price = null;

        private ListItem item = null;

        public BoughtItemsContentItem(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.bought_items_item_product_name);

            container = itemView.findViewById(R.id.bought_items_item_container);

            containerNumber = itemView.findViewById(R.id.bought_items_item_product_attribute_number);
            displayNumber = itemView.findViewById(R.id.bought_items_item_product_number);

            containerRequestedBy = itemView.findViewById(
                R.id.bought_items_item_product_attribute_requested_by);
            displayRequestedBy = itemView.findViewById(R.id.bought_items_item_product_requested_by);

            containerRequestedFor = itemView.findViewById(
                R.id.bought_items_item_product_attribute_requested_for);
            displayRequestedFor = itemView.findViewById(R.id.bought_items_item_product_requested_for);

            containerPrice = itemView.findViewById(R.id.bought_items_item_product_attribute_price);
            price = itemView.findViewById(R.id.bought_items_item_product_price);

            container = itemView.findViewById(R.id.bought_items_item_container);
        }

        @Override
        public void setData(Object object, Bundle args) {
            if (object != null) {
                item = (ListItem) object;
            }

            if(item != null) {
                if (args == null) {
                    name.setText(item.getTitle());
                    displayNumber.setText(item.getCount());
                    displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                    displayRequestedFor.setText(buildRequestedFor(item.getRequestedFor()));
                    price.setText(item.getPrice());
                } else {
                    if (args.getBoolean(DiffCallback.NAME)) {
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
                                name.setText(item.getTitle());
                            }
                        });
                        name.startAnimation(anim);
                    }

                    if (args.getBoolean(DiffCallback.REQUESTED_BY)) {
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
                                displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                            }
                        });
                        displayRequestedBy.startAnimation(anim);
                    } else {
                        displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                    }

                    if (args.getBoolean(DiffCallback.NUMBER)) {
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
                                displayNumber.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                            }
                        });
                        displayNumber.startAnimation(anim);
                    }

                    if(args.getBoolean(DiffCallback.REQUESTED_FOR)) {
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
                                displayRequestedFor.setText(buildRequestedFor(item.getRequestedFor()));
                            }
                        });
                        displayRequestedFor.startAnimation(anim);
                    }

                    if(args.getBoolean(DiffCallback.PRICE)) {
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
                                price.setText(item.getPrice());
                            }
                        });
                        price.startAnimation(anim);
                    }
                }
            }
        }

        private String buildRequestedFor(List<String> uids){
            StringBuilder requestedFor = new StringBuilder();

            if(uids.size() == dataProvider.getCurrentGroupMembers().size()) {
                requestedFor.append("Group");
            } else {
                for(int i = 0; i < uids.size(); i++) {
                    requestedFor.append(uids.get(i));

                    if(i != uids.size() - 1){
                        requestedFor.append(", ");
                    }
                }
            }

            return requestedFor.toString();
        }
    }

    private ArrayList<Object> items = new ArrayList<>();
    private Context context = null;
    private SimpleDateFormat format = null;

    private static DataProvider dataProvider = DataProvider.getInstance();

    public BoughtItemsAdapter(ArrayList<ListItem> items, Context context) {
        Collections.sort(items, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem item, ListItem t1) {
                return item.getBoughtAt().compareTo(t1.getBoughtAt()) * -1;
            }
        });

        this.context = context;
        format = new SimpleDateFormat("dd. MMMM yyyy", context.getResources().getConfiguration().locale);
        this.items.clear();
        this.items.addAll(transformItems(items));
    }

    @Override
    public void onBindViewHolder(BoughtItemsViewItem holder, int position, List<Object> payloads) {
        if(payloads != null) {
            holder.setData(items.get(position), (Bundle) payloads.get(0));
        } else {
            holder.setData(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) instanceof DateTime) {
            return HEADER_VIEW_TYPE;
        } else {
            return CONTENT_VIEW_TYPE;
        }
    }

    @Override
    public BoughtItemsViewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_bought_items_header_layout, parent, false);

            return new BoughtItemsHeaderItem(item);
        } else if (viewType == CONTENT_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_bought_items_item_layout, parent, false);

            return new BoughtItemsContentItem(item);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(BoughtItemsViewItem holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private ArrayList<Object> transformItems(ArrayList<ListItem> items) {
        HashMap<String, ArrayList<ListItem>> map = new HashMap<>();

        for(ListItem item: items) {
            String date = format.format(item.getBoughtAt().toDate());

            if(map.containsKey(date)) {
                map.get(date).add(item);
            } else {
                ArrayList<ListItem> list = new ArrayList<>();
                list.add(item);
                map.put(date, list);
            }
        }

        ArrayList<Object> viewItems = new ArrayList<>();

        for(String time: map.keySet()) {
            viewItems.add(time);
            viewItems.addAll(map.get(time));
        }

        return viewItems;
    }

    public void onNewData(ArrayList<ListItem> items) {
        ArrayList<Object> viewItems = transformItems(items);

        final DiffCallback callback = new DiffCallback(viewItems, this.items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.items.clear();
        this.items.addAll(viewItems);

        result.dispatchUpdatesTo(this);
    }

    private static class DiffCallback extends DiffUtil.Callback {
        public static final java.lang.String NAME = "NAME";
        public static final java.lang.String REQUESTED_FOR = "REQUESTED_FOR";
        public static final java.lang.String REQUESTED_BY = "REQUESTED_BY";
        public static final java.lang.String NUMBER = "NUMBER";
        public static final java.lang.String PRICE = "PRICE";
        private ArrayList<Object> newList = new ArrayList<>();
        private ArrayList<Object> oldList = new ArrayList<>();

        public DiffCallback(ArrayList<Object> newList, ArrayList<Object> oldList) {
            this.newList.clear();
            this.newList.addAll(newList);
            this.oldList.clear();
            this.oldList.addAll(oldList);
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            return oldItem.getClass() == newItem.getClass() &&
                    !(oldItem instanceof ListItem &&
                    !((ListItem) oldItem).getId().equals(((ListItem) oldItem).getId()));

        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if (oldItem instanceof ListItem) {
                return ((ListItem) oldItem).equals((ListItem) newItem);
            } else {
                return oldItem instanceof String && ((String) oldItem).equals((String) newItem);
            }
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass() || oldItem instanceof String) {
                return null;
            }

            ListItem newListItem = (ListItem) newItem;
            ListItem oldListItem = (ListItem) oldItem;

            Bundle changes = new Bundle();

            changes.putBoolean(
                REQUESTED_BY,
                !oldListItem.getRequestedBy().equals(newListItem.getRequestedBy())
            );

            changes.putBoolean(
                NAME,
                !oldListItem.getTitle().equals(newListItem.getTitle())
            );

            changes.putBoolean(
                REQUESTED_FOR,
                !oldListItem.getRequestedFor().equals(newListItem.getRequestedFor())
            );

            changes.putBoolean(
                NUMBER,
                oldListItem.getCount() != newListItem.getCount()
            );

            changes.putBoolean(
                PRICE,
                oldListItem.getPrice() != newListItem.getPrice()
            );

            return changes;
        }
    }
}
