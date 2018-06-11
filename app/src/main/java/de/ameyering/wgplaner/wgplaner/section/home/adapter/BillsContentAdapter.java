package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import io.swagger.client.model.Bill;

public class BillsContentAdapter extends RecyclerView.Adapter<BillsContentAdapter.ViewHolder> {
    private final DataProviderInterface dataProviderInterface;
    private List<Bill> bills = new ArrayList<>();
    private List<OnItemTouchListener> listeners = new ArrayList<>();
    private Context context;
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    public interface OnItemTouchListener {

        void onItemTouch(UUID uuid);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDateFormat format = new SimpleDateFormat("dd. MMMM yyyy",
            context.getResources().getConfiguration().locale);

        private Bill bill;

        private LinearLayout container;

        private CircularImageView profilePicture;

        private TextView header;
        private TextView subHeader;

        private ImageView icon;

        public ViewHolder(View view) {
            super(view);

            container = view.findViewById(R.id.bill_content_container);
            profilePicture = view.findViewById(R.id.received_bills_item_profile_picture);
            header = view.findViewById(R.id.received_bills_item_date);
            subHeader = view.findViewById(R.id.received_bills_item_price);
            icon = view.findViewById(R.id.received_bills_item_done);
        }

        public void setData(Bill bill) {
            if (bill != null) {
                this.bill = bill;

                DateFormat format = DateFormat.getDateInstance();
                header.setText(format.format(bill.getDueDate().toDate()));

                if (bill.getSum() != null) {
                    subHeader.setVisibility(View.VISIBLE);
                    double price = Double.valueOf(((double) bill.getSum()) / 100);
                    subHeader.setText(numberFormat.format(price));

                } else {
                    subHeader.setVisibility(View.GONE);
                }

                container.setOnClickListener(view -> callAllListeners(bill.getUid()));

                if (bill.getState().equals("paid")) {
                    icon.setImageResource(R.drawable.ic_done_black_);

                } else if (bill.getState().equals("confirmed paid")) {
                    icon.setImageResource(R.drawable.ic_done_all_black);

                } else {
                    icon.setImageResource(R.drawable.ic_access_time_black);
                }

                profilePicture.setImageBitmap(dataProviderInterface.getGroupMemberPicture(bill.getCreatedBy()));
            }
        }
    }

    public BillsContentAdapter(List<Bill> bills, Context context,
        DataProviderInterface dataProviderInterface) {
        this.bills.clear();
        this.bills.addAll(bills);
        this.context = context;
        this.dataProviderInterface = dataProviderInterface;
        this.numberFormat.setCurrency(dataProviderInterface.getCurrentGroupCurrency());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.section_bills_item, parent, false);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(bills.get(0));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        holder.setData(bills.get(position));
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public void onNewData(List<Bill> bills) {
        final DiffCallback callback = new DiffCallback(bills, this.bills);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.bills.clear();
        this.bills.addAll(bills);

        result.dispatchUpdatesTo(this);
    }

    private void callAllListeners(UUID uuid) {
        for (OnItemTouchListener listener : listeners) {
            listener.onItemTouch(uuid);
        }
    }

    public void addOnTouchListener(OnItemTouchListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnItemTouchListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private class DiffCallback extends DiffUtil.Callback {
        private List<Bill> newList = new ArrayList<>();
        private List<Bill> oldList = new ArrayList<>();

        public DiffCallback(List<Bill> newList, List<Bill> oldList) {
            this.newList.clear();
            this.newList.addAll(newList);
            this.oldList.clear();
            this.oldList.addAll(newList);
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
            Bill newBill = newList.get(newItemPosition);
            Bill oldBill = oldList.get(oldItemPosition);

            return newBill.getUid().equals(oldBill.getUid());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
}
