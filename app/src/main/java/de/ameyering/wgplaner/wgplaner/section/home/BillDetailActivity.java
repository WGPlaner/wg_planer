package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import de.ameyering.wgplaner.wgplaner.utils.OnDataChangeListener;
import io.swagger.client.model.Bill;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.User;

public class BillDetailActivity extends AppCompatActivity {
    private String billUid;
    private Bill bill;
    private Double amount;

    private Toolbar toolbar;
    private FloatingActionButton actionPay;

    private TextView dueDateView;
    private TextView sumView;
    private TextView ownerNameView;
    private CircularImageView ownerImageView;

    private LinearLayout recipientsContainer;
    private LinearLayout itemsContainer;

    private DataProviderInterface dataProvider;

    private HashMap<String, Double> recipientCosts = new HashMap<>();

    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    private OnDataChangeListener billListener = type -> {
      if(type == DataProviderInterface.DataType.BILLS) {
          this.bill = dataProvider.getBill(this.billUid);

          if(this.bill == null) {
              runOnUiThread(() -> {
                  setResult(RESULT_CANCELED);
                  finish();
              });
          } else {
              runOnUiThread(this::setUpViews);
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        WGPlanerApplication application = (WGPlanerApplication) getApplication();
        dataProvider = application.getDataProviderInterface();

        numberFormat.setCurrency(dataProvider.getCurrentGroupCurrency());

        billUid = getIntent().getStringExtra(Intent.EXTRA_UID);
        bill = dataProvider.getBill(billUid);

        if(bill != null) {
            toolbar = findViewById(R.id.bill_detail_toolbar);
            actionPay = findViewById(R.id.bill_detail_action_pay);

            dueDateView = findViewById(R.id.bill_detail_due_date);
            sumView = findViewById(R.id.bill_detail_cost_sum);
            ownerNameView = findViewById(R.id.bill_detail_owner_name);
            ownerImageView = findViewById(R.id.bill_detail_owner_image);

            recipientsContainer = findViewById(R.id.bill_detail_recipients_container);
            itemsContainer = findViewById(R.id.bill_detail_items_container);

            setUpViews();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onResume() {
        dataProvider.addOnDataChangeListener(billListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataProvider.removeOnDataChangeListener(billListener);
        super.onPause();
    }

    private void initData() {
        HashMap<String, Double> recipientsCosts = new HashMap<>();

        Double amount = 0d;

        for(ListItem item: bill.getBoughtListItems()) {
            Double price = ((double) item.getPrice()) / 100;
            amount += price;

            int requestedForSize = item.getRequestedFor().size();
            for(String userUid: item.getRequestedFor()) {
                Double pricePerUser = 0d;

                if(recipientsCosts.containsKey(userUid)) {
                    pricePerUser = recipientsCosts.get(userUid);
                }

                pricePerUser += price / requestedForSize;
                recipientsCosts.put(userUid, pricePerUser);
            }
        }

        this.amount = amount;
        this.recipientCosts =  recipientsCosts;
    }

    private void setUpViews() {
        initData();
        setUpHeader();
        addItems();
        addRecipients();
    }

    private void setUpHeader() {
        User owner = dataProvider.getUserByUid(bill.getCreatedBy());
        ownerNameView.setText(owner.getDisplayName());

        Bitmap ownerImage = dataProvider.getGroupMemberPicture(bill.getCreatedBy());
        ownerImageView.setImageBitmap(ownerImage);

        DateFormat format = DateFormat.getDateInstance();
        dueDateView.setText(format.format(bill.getDueDate().toDate()));

        sumView.setText(numberFormat.format(amount));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        if(bill.getCreatedBy().equals(dataProvider.getCurrentUserUid())) {
            actionPay.setVisibility(View.GONE);
        } else {
            actionPay.setVisibility(View.VISIBLE);
            actionPay.setOnClickListener(view -> {
                //TODO: Implement if ready
            });
        }
    }

    private void addItems() {
        TransitionManager.beginDelayedTransition(itemsContainer);
        itemsContainer.removeAllViews();

        for(ListItem item: bill.getBoughtListItems()) {
            if(item != null) {
                View view = getLayoutInflater().inflate(R.layout.bill_detail_items_layout, itemsContainer, false);

                TextView itemName = view.findViewById(R.id.bill_list_item_name);
                TextView itemPrice = view.findViewById(R.id.bill_list_item_price);

                itemName.setText(item.getTitle());
                itemPrice.setText(numberFormat.format(((double) item.getPrice()) / 100));

                itemsContainer.addView(view);
            }
        }
    }

    private void addRecipients() {
        TransitionManager.beginDelayedTransition(recipientsContainer);
        recipientsContainer.removeAllViews();

        for(String uid: recipientCosts.keySet()) {
            User user = dataProvider.getUserByUid(uid);

            if(user != null) {
                Double pricePerUser = recipientCosts.get(uid);

                View view = getLayoutInflater().inflate(R.layout.bill_detail_cost_recipient_layout, recipientsContainer, false);

                CircularImageView recipientImage = view.findViewById(R.id.cost_recipient_image);
                TextView recipientName = view.findViewById(R.id.cost_recipient_name);
                TextView recipientSum = view.findViewById(R.id.cost_recipient_sum);
                ImageView paymentStatus = view.findViewById(R.id.cost_recipient_status);

                recipientName.setText(user.getDisplayName());

                recipientSum.setText(numberFormat.format(pricePerUser));

                Bitmap bitmap = dataProvider.getGroupMemberPicture(uid);

                if (bitmap != null) {
                    recipientImage.setImageBitmap(bitmap);
                }

                if (bill.getPayedBy() != null && bill.getPayedBy().contains(uid)) {
                    paymentStatus.setImageResource(R.drawable.ic_check_black);
                } else {
                    paymentStatus.setImageResource(R.drawable.ic_access_time_black);
                }

                recipientsContainer.addView(view);
            }
        }
    }
}
