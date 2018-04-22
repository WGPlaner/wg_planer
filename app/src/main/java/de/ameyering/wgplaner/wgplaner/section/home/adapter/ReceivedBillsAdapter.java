package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import io.swagger.client.model.Bill;

public class ReceivedBillsAdapter extends RecyclerView.Adapter<ReceivedBillsAdapter.ViewHolder>{
    private ArrayList<Bill> bills = new ArrayList<>();
    private Context context = null;

    private static ImageStore imageStore = ImageStore.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDateFormat format = new SimpleDateFormat("dd. MMMM yyyy", context.getResources().getConfiguration().locale);

        private Bill bill = null;

        private CircularImageView profilePicture = null;

        private TextView header = null;
        private TextView subHeader = null;

        private ImageView icon = null;

        public ViewHolder(View view) {
            super(view);

            profilePicture = view.findViewById(R.id.received_bills_item_profile_picture);
            header = view.findViewById(R.id.received_bills_item_date);
            subHeader = view.findViewById(R.id.received_bills_item_price);
            icon = view.findViewById(R.id.received_bills_item_done);
        }

        public void setData(Bill bill) {
            if(bill != null) {
                this.bill = bill;
            }

            header.setText(format.format(bill.getCreatedAt().toDate()));
            subHeader.setText((bill.getSum() / 100));

            switch (bill.getState()) {
                case "paid":
                    icon.setImageResource(R.drawable.ic_done_black_);
                    break;
                case "confimred paid":
                    icon.setImageResource(R.drawable.ic_done_all_black);
                    break;
                default:
                    icon.setVisibility(View.INVISIBLE);
            }

            profilePicture.setImageBitmap(imageStore.loadGroupMemberPicture(bill.getCreatedBy(), context));
        }
    }

    public ReceivedBillsAdapter(ArrayList<Bill> bills, Context context) {
        this.bills.clear();
        this.bills.addAll(bills);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.section_received_bills_item, parent, false);

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

    public void onNewData(ArrayList<Bill> bills) {
        final DiffCallback callback = new DiffCallback(bills, this.bills);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);

        this.bills.clear();
        this.bills.addAll(bills);

        result.dispatchUpdatesTo(this);
    }

    private class DiffCallback extends DiffUtil.Callback {
        private ArrayList<Bill> newList = new ArrayList<>();
        private ArrayList<Bill> oldList = new ArrayList<>();

        public DiffCallback(ArrayList<Bill> newList, ArrayList<Bill> oldList) {
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
            Bill newBill = newList.get(newItemPosition);
            Bill oldBill = oldList.get(oldItemPosition);

            return newBill.equals(oldBill);
        }
    }
}
