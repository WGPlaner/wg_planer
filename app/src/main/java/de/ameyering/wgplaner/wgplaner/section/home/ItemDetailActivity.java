package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.User;

public class ItemDetailActivity extends AppCompatActivity {
    private DataProvider dataProvider = DataProvider.getInstance();
    private ImageStore imageStore = ImageStore.getInstance();

    private TextView itemNameView;
    private TextView itemPriceView;
    private TextInputLayout itemPriceEditLayout;
    private EditText itemPriceEdit;
    private TextView itemNumberView;

    private FloatingActionButton addPriceActionView;

    private LinearLayout boughtByContainerView;
    private LinearLayout boughtOnContainerView;

    private TextView boughtByNameView;
    private CircularImageView boughtByPictureView;

    private TextView boughtOnDateView;

    private TextView requestedByNameView;
    private CircularImageView requestedByPictureView;

    private LinearLayout requestedForChildContainer;

    private ListItem item;

    boolean isInEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Toolbar activityToolbar = findViewById(R.id.item_detail_toolbar);
        setSupportActionBar(activityToolbar);
        activityToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        activityToolbar.setNavigationOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        String uuid = getIntent().getStringExtra(Intent.EXTRA_UID);
        item = dataProvider.getListItem(UUID.fromString(uuid));

        if (item == null) {
            setResult(RESULT_CANCELED);
            finish();
        }

        itemNameView = findViewById(R.id.item_detail_item_name);
        itemPriceView = findViewById(R.id.item_detail_item_price);
        itemNumberView = findViewById(R.id.item_detail_item_number);

        itemNameView.setText(item.getTitle());

        if (item.getPrice() != null) {
            itemPriceView.setVisibility(View.VISIBLE);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(dataProvider.getCurrentGroupCurrency());
            itemPriceView.setVisibility(View.VISIBLE);
            itemPriceView.setText(format.format(((double) item.getPrice()) / 100));

        } else {
            itemPriceView.setVisibility(View.GONE);
        }

        itemNumberView.setText(String.format(getString(R.string.item_detail_amount_format),
                item.getCount().toString()));

        addPriceActionView = findViewById(R.id.item_detail_action_add_price);

        boughtByContainerView = findViewById(R.id.item_detail_item_bought_by_container);
        boughtOnContainerView = findViewById(R.id.item_detail_item_bought_on_container);

        if (item.getBoughtBy() != null) {
            boughtByContainerView.setVisibility(View.VISIBLE);
            boughtOnContainerView.setVisibility(View.VISIBLE);

            boughtByNameView = findViewById(R.id.item_detail_item_bought_by_name);
            boughtByPictureView = findViewById(R.id.item_detail_item_bought_by_picture);

            boughtByNameView.setText(dataProvider.getUserByUid(item.getBoughtBy()).getDisplayName());
            Bitmap pic = imageStore.loadGroupMemberPicture(item.getBoughtBy(), this);
            boughtByPictureView.setImageBitmap(pic);

            boughtOnDateView = findViewById(R.id.item_detail_item_bought_on);
            SimpleDateFormat format = new SimpleDateFormat("dd. MMMM yyyy",
                getResources().getConfiguration().locale);
            boughtOnDateView.setText(format.format(item.getBoughtAt().toDate()));

        } else {
            boughtByContainerView.setVisibility(View.GONE);
            boughtOnContainerView.setVisibility(View.GONE);
        }

        requestedByNameView = findViewById(R.id.item_detail_item_requested_by_name);
        requestedByPictureView = findViewById(R.id.item_detail_item_requested_by_picture);

        requestedByNameView.setText(dataProvider.getUserByUid(item.getRequestedBy()).getDisplayName());
        Bitmap pic = imageStore.loadGroupMemberPicture(item.getRequestedBy(), this);
        requestedByPictureView.setImageBitmap(pic);

        itemPriceEditLayout = findViewById(R.id.item_detail_add_price_layout);
        itemPriceEdit = findViewById(R.id.item_detail_add_price);

        addPriceActionView = findViewById(R.id.item_detail_action_add_price);
        addPriceActionView.setOnClickListener(view -> {
            if (!isInEditMode) {
                addPriceActionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_white));
                itemPriceView.setVisibility(View.GONE);
                itemPriceEditLayout.setVisibility(View.VISIBLE);
                isInEditMode = true;

            } else {
                addPriceActionView.setEnabled(false);
                itemPriceEdit.setEnabled(false);
                dataProvider.addPriceToListItem(item, itemPriceEdit.getText().toString(),
                new ServerCallsInterface.OnAsyncCallListener<ListItem>() {
                    @Override
                    public void onFailure(ApiException e) {
                        if (e == null) {
                            runOnUiThread(() -> {
                                itemPriceEditLayout.setError(getString(R.string.dialog_add_price_error));
                                itemPriceEdit.setEnabled(true);
                                addPriceActionView.setEnabled(true);
                            });

                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(ItemDetailActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                                itemPriceEdit.setEnabled(true);
                                addPriceActionView.setEnabled(true);
                            });
                        }
                    }

                    @Override
                    public void onSuccess(ListItem result) {
                        runOnUiThread(() -> {
                            itemPriceEdit.setEnabled(true);
                            addPriceActionView.setEnabled(true);
                            addPriceActionView.setImageDrawable(ContextCompat.getDrawable(ItemDetailActivity.this, R.drawable.ic_attach_money_white));
                            itemPriceView.setVisibility(View.VISIBLE);
                            itemPriceEditLayout.setVisibility(View.GONE);
                            itemPriceEditLayout.setError(null);
                            NumberFormat format = NumberFormat.getCurrencyInstance();
                            format.setCurrency(dataProvider.getCurrentGroupCurrency());
                            itemPriceView.setText(format.format(((double) result.getPrice()) / 100));
                            isInEditMode = false;
                        });
                    }
                });
            }
        });

        requestedForChildContainer = findViewById(R.id.item_detail_requested_for);

        initRequestedFor(transformUidsToUsers(item.getRequestedFor()));
    }

    private void initRequestedFor(List<User> users) {
        TransitionManager.beginDelayedTransition(requestedForChildContainer);
        requestedForChildContainer.removeAllViews();

        int rows = users.size() / 3 + 1;

        if (users.size() == 0) {
            rows = 0;
        }

        LayoutInflater inflater = getLayoutInflater();

        for (int row = 0; row < rows; row++) {
            View rowLayout = inflater.inflate(R.layout.activity_item_detail_requested_for_row,
                    requestedForChildContainer, false);
            LinearLayout layout = rowLayout.findViewById(R.id.item_detail_requested_for_row);

            for (int col = 0; col < 3; col++) {
                int index = (row * 3) + col;

                if (index < users.size()) {
                    User user = users.get(index);

                    View requestedForChild = inflater.inflate(R.layout.activity_item_detail_requested_for_child, layout,
                            false);
                    TextView requestedForChildName = requestedForChild.findViewById(
                            R.id.item_detail_requested_for_name);
                    CircularImageView requestedForChildPicture = requestedForChild.findViewById(
                            R.id.item_detail_requested_for_picture);

                    requestedForChildName.setText(user.getDisplayName());
                    Bitmap pic = imageStore.loadGroupMemberPicture(user.getUid(), this);

                    requestedForChildPicture.setImageBitmap(pic);
                    layout.addView(requestedForChild);

                } else {
                    break;
                }
            }

            requestedForChildContainer.addView(rowLayout);
        }
    }

    private List<User> transformUidsToUsers(List<String> uids) {
        ArrayList<User> users = new ArrayList<>();

        for (String uid : uids) {
            users.add(dataProvider.getUserByUid(uid));
        }

        return users;
    }
}
