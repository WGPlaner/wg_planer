package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.structure.CategoryHolder;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.ListItem;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewItem> {

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    public static abstract class ShoppingListViewItem extends RecyclerView.ViewHolder {

        private ShoppingListViewItem(View itemView) {
            super(itemView);
        }

        public abstract void setData(Object object, Bundle args);

        public void setData(Object object) {
            setData(object, null);
        }
    }

    public static class ShoppingListViewItemHeader extends ShoppingListViewItem {

        private TextView header = null;

        public ShoppingListViewItemHeader (View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.shopping_list_section_header);
        }

        @Override
        public void setData(final Object object, Bundle args) {
            if(args != null) {
                if (object instanceof String) {
                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(200);
                    anim.setRepeatCount(1);
                    anim.setRepeatMode(Animation.REVERSE);

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //Nothing to do here
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // nothing to do here
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            header.setText((String) object);
                        }
                    });

                    header.startAnimation(anim);
                }
            } else {
                header.setText((String) object);
            }
        }
    }

    public static class ShoppingListViewItemContent extends ShoppingListViewItem {
        private TextView name = null;

        private LinearLayout containerNumber = null;
        private TextView displayNumber = null;

        private LinearLayout containerRequestedBy = null;
        private TextView displayRequestedBy = null;

        private LinearLayout containerRequestedFor = null;
        private TextView displayRequestedFor = null;

        private CheckBox checkbox = null;

        private ListItem item = null;

        public ShoppingListViewItemContent(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.shopping_list_item_product_name);

            containerNumber = itemView.findViewById(R.id.shopping_list_item_product_attribute_number);
            displayNumber = itemView.findViewById(R.id.shopping_list_item_product_number);

            containerRequestedBy = itemView.findViewById(
                R.id.shopping_list_item_product_attribute_requested_by);
            displayRequestedBy = itemView.findViewById(R.id.shopping_list_item_product_requested_by);

            containerRequestedFor = itemView.findViewById(
                R.id.shopping_list_item_product_attribute_requested_for);
            displayRequestedFor = itemView.findViewById(R.id.shopping_list_item_product_requested_for);

            checkbox = itemView.findViewById(R.id.shopping_list_item_checked);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkbox.isChecked()) {
                        dataProvider.selectShoppingListItem(item);

                    } else {
                        dataProvider.unselectShoppingListItem(item);
                    }
                }
            });
        }

        @Override
        public void setData(Object object, Bundle args) {
            if(object instanceof ListItem) {
                item = (ListItem) object;
            }

            if(item != null) {
                if (args != null) {

                    int title = args.getInt(DiffCallback.NAME);
                    int requestedFor = args.getInt(DiffCallback.REQUESTED_FOR);
                    int requestedBy = args.getInt(DiffCallback.REQUESTED_BY);
                    int number = args.getInt(DiffCallback.NUMBER);

                    if (title == DiffCallback.TRUE_VALUE) {
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

                    if (requestedBy == DiffCallback.TRUE_VALUE) {
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

                    if(number == DiffCallback.TRUE_VALUE) {
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
                                displayNumber.setText(item.getRequestedBy());
                            }
                        });
                        displayNumber.startAnimation(anim);
                    }
                } else {
                    name.setText(item.getTitle());
                    displayNumber.setText(item.getCount().toString());
                    displayRequestedBy.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
                }
            }
        }
    }

    public ArrayList<Object> viewItems = new ArrayList<>();
    private static DataProvider dataProvider = DataProvider.getInstance();

    public ShoppingListAdapter(ArrayList<CategoryHolder> sections) {
        viewItems = transformSections(sections);
    }

    private ArrayList<Object> transformSections(ArrayList<CategoryHolder> sections) {
        ArrayList<Object> viewItems = new ArrayList<>();

        for(CategoryHolder section: sections) {
            viewItems.addAll(section.getViewItems());
        }

        return viewItems;
    }

    @Override
    public void onBindViewHolder(ShoppingListViewItem holder, int position, List<Object> payloads) {
        if(payloads == null || payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.setData(viewItems.get(position), (Bundle) payloads.get(0));
        }
    }

    @Override
    public ShoppingListViewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_VIEW_TYPE) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_header_layout, parent, false);

            return new ShoppingListViewItemHeader(item);
        } else {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_shopping_list_item_layout2, parent, false);

            return new ShoppingListViewItemContent(item);
        }
    }

    @Override
    public void onBindViewHolder(ShoppingListViewItem holder, int position) {
        holder.setData(viewItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Object viewItem = viewItems.get(position);

        if(viewItem instanceof String) {
            return HEADER_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return viewItems.size();
    }

    public void onNewData(ArrayList<CategoryHolder> items) {
        ArrayList<Object> viewItems = transformSections(items);

        final DiffCallback callback = new DiffCallback(viewItems, this.viewItems);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.viewItems.clear();
        this.viewItems.addAll(viewItems);

        result.dispatchUpdatesTo(this);
    }

    private class DiffCallback extends DiffUtil.Callback {
        public static final java.lang.String NAME = "NAME";
        public static final java.lang.String REQUESTED_FOR = "REQUESTED_FOR";
        public static final java.lang.String REQUESTED_BY = "REQUESTED_BY";
        public static final java.lang.String NUMBER = "NUMBER";
        public static final int TRUE_VALUE = 1;
        public static final int FALSE_VALUE = 0;
        private ArrayList<Object> newList = new ArrayList<>();
        private ArrayList<Object> oldList = new ArrayList<>();

        public DiffCallback(ArrayList<Object> newList, ArrayList<Object> oldList) {
            this.newList.clear();
            this.newList.addAll(newList);

            this.oldList.clear();
            this.oldList.addAll(oldList);
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if(oldItem instanceof String) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof ListItem) {
                return oldItem.equals(newItem);
            } else {
                return false;
            }
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if(oldItem instanceof String) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof ListItem) {
                return ((ListItem) oldItem).getId().equals(((ListItem) newItem).getId());
            } else {
                return false;
            }
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Object newItem = newList.get(newItemPosition);
            Object oldItem = oldList.get(oldItemPosition);

            int title = TRUE_VALUE;
            int requestedBy = TRUE_VALUE;
            int requestedFor = TRUE_VALUE;
            int number = TRUE_VALUE;

            if(oldItem.getClass() != newItem.getClass()) {
                return false;
            }

            if(oldItem instanceof String) {
                return null;
            } else if (oldItem instanceof ListItem) {
                ListItem newListItem = (ListItem) newItem;
                ListItem oldListItem = (ListItem) oldItem;

                Bundle args = new Bundle();

                if (!isTitleEquals(newListItem.getTitle(), oldListItem.getTitle())) {
                    args.putInt(NAME, TRUE_VALUE);

                } else {
                    title = FALSE_VALUE;
                    args.putInt(NAME, FALSE_VALUE);
                }

                if (!isRequestedByEquals(newListItem.getRequestedBy(), oldListItem.getRequestedBy())) {
                    args.putInt(REQUESTED_BY, TRUE_VALUE);

                } else {
                    requestedBy = FALSE_VALUE;
                    args.putInt(REQUESTED_BY, FALSE_VALUE);
                }

                if (!isRequestedForEquals(newListItem.getRequestedFor(), oldListItem.getRequestedFor())) {
                    args.putInt(REQUESTED_FOR, TRUE_VALUE);

                } else {
                    requestedFor = FALSE_VALUE;
                    args.putInt(REQUESTED_FOR, FALSE_VALUE);
                }

                if (!isNumberEquals(newListItem.getCount(), oldListItem.getCount())) {
                    args.putInt(NUMBER, TRUE_VALUE);

                } else {
                    number = FALSE_VALUE;
                    args.putInt(NUMBER, FALSE_VALUE);
                }

                if (title == FALSE_VALUE && requestedBy == FALSE_VALUE && requestedFor == FALSE_VALUE &&
                    number == FALSE_VALUE) {
                    return null;
                }

                return args;
            } else {
                return null;
            }
        }

        private boolean isTitleEquals(String newTitle, String oldTitle) {
            if (newTitle == null) {
                if (oldTitle != null) {
                    return false;
                }

            } else {
                if (oldTitle == null) {
                    return false;
                }
            }

            return !(newTitle != null && oldTitle != null && !newTitle.equals(oldTitle));
        }

        private boolean isRequestedForEquals(List<String> newRequestedFor, List<String> oldRequestedFor) {
            if (newRequestedFor == null) {
                if (oldRequestedFor != null) {
                    return false;
                }

            } else {
                if (oldRequestedFor == null) {
                    return false;
                }
            }

            return !(newRequestedFor != null && oldRequestedFor != null &&
                !newRequestedFor.equals(oldRequestedFor));
        }

        private boolean isRequestedByEquals(String newRequestedBy, String oldRequestedBy) {
            if(newRequestedBy == null && oldRequestedBy == null) {
                return true;
            } else if (newRequestedBy != null && oldRequestedBy != null) {
                if(newRequestedBy.equals(oldRequestedBy)){
                    return true;
                }

                return false;
            }

            return false;
        }

        private boolean isNumberEquals(int newNumber, int oldNumber) {
            return newNumber == oldNumber;

        }
    }
}
