package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import io.swagger.client.model.Bill;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.User;

public class BillActivity extends AppCompatActivity {
    private String billUid;
    private Bill bill;
    private HashMap<String, Double> costs;
    private double amount;

    private TextView amountView;
    private TextView ownerView;
    private Button actionView;

    private LinearLayout costsContainer;
    private LinearLayout itemsContainer;

    private DataProvider dataProvider = DataProvider.getInstance();
    private ImageStore imageStore = ImageStore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        billUid = getIntent().getStringExtra(Intent.EXTRA_UID);
        bill = dataProvider.getBill(billUid);

        if(bill != null) {
            amountView = findViewById(R.id.bill_detail_amount);
            ownerView = findViewById(R.id.bill_detail_owner);
            actionView = findViewById(R.id.bill_detail_action);

            ownerView.setText(dataProvider.getUserByUid(bill.getCreatedBy()).getDisplayName());
            amountView.setText(String.format(Locale.getDefault(),"%1$,.2f", amount));

            costsContainer = findViewById(R.id.bill_cost_objects_container);
            itemsContainer = findViewById(R.id.bill_items_objects_container);
        }
    }

    private void addItems(List<ListItem> items) {
        for(ListItem item: items) {
            View view = getLayoutInflater().inflate(R.layout.bill_detail_items_children_layout, this.itemsContainer);

            //TODO: Implement if layout is complete
        }
    }

    private void addCosts(HashMap<String, Double> costs) {
        for(String uid: costs.keySet()) {
            Double price = costs.get(uid);
            User user = dataProvider.getUserByUid(uid);

            View view = getLayoutInflater().inflate(R.layout.bill_detail_costs_children_layout, this.costsContainer);

            CircularImageView imageView = view.findViewById(R.id.bill_costs_image);
            TextView nameView = view.findViewById(R.id.bill_costs_user_name);
            TextView priceView = view.findViewById(R.id.bill_costs_price);

            Bitmap bitmap = imageStore.loadGroupMemberPicture(uid, this);

            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }

            nameView.setText(user.getDisplayName());
            priceView.setText(String.format(Locale.getDefault(), price.toString()));

            TransitionManager.beginDelayedTransition(this.costsContainer);
            this.costsContainer.addView(view);
        }
    }

    private void computeCosts(List<ListItem> items) {
        HashMap<String, Double> costs = new HashMap<>();
        double amount = 0;

        for(ListItem item: items) {
            List<String> requestedFor = item.getRequestedFor();
            double price = item.getPrice() / 100;
            amount += price;
            double pricePerUser = price / requestedFor.size();

            for(String uid: requestedFor) {
                if(costs.containsKey(uid)) {
                    double cost = costs.get(uid);
                    costs.put(uid, cost + pricePerUser);
                }
            }
        }

        this.costs = costs;
        this.amount = amount;
    }
}
