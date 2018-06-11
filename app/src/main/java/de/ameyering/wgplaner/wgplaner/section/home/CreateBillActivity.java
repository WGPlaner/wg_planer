package de.ameyering.wgplaner.wgplaner.section.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import io.swagger.client.ApiException;
import io.swagger.client.model.Bill;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.User;

public class CreateBillActivity extends AppCompatActivity {

    private static Calendar now = new GregorianCalendar();
    private static DataProviderInterface dataProvider;

    private Calendar dueDateCalendar = new GregorianCalendar();

    private List<String> itemUids;
    private List<ListItem> items = new ArrayList<>();
    private HashMap<User, Double> recipientPriceMapping = new HashMap<>();

    private TextInputLayout layoutDueDate;
    private EditText editDueDate;

    private LinearLayout itemsView;
    private LinearLayout recipientsView;

    private Date dueDate = null;

    static {
        now.add(Calendar.WEEK_OF_YEAR, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);

        WGPlanerApplication application = (WGPlanerApplication) getApplication();
        dataProvider = application.getDataProviderInterface();

        itemUids = getIntent().getStringArrayListExtra(Intent.EXTRA_UID);

        if (itemUids == null || itemUids.isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();

        } else {
            prepareData();
        }

        Toolbar toolbar = findViewById(R.id.create_bill_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.create_bill_title);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        toolbar.setNavigationOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateBillActivity.this);
            builder.setMessage(getString(R.string.dialog_discard_message));
            builder.setPositiveButton(R.string.dialog_discard_positive, (dialogInterface, i) -> {
                dialogInterface.cancel();
                setResult(RESULT_CANCELED);
                finish();
            });
            builder.setNegativeButton(R.string.dialog_discard_negative, (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        FloatingActionButton save = findViewById(R.id.create_bill_action_save);
        save.setOnClickListener(view -> {
            editDueDate.setEnabled(false);
            save.setClickable(false);

            if (layoutDueDate.getError() == null && dueDate != null) {
                Bill bill = new Bill();
                bill.setDueDate(new DateTime(dueDate.getTime()));
                bill.setBoughtItems(itemUids);
                List<String> sentTo = new ArrayList<>();
                Integer sum = 0;

                for (User user : recipientPriceMapping.keySet()) {
                    sentTo.add(user.getUid());
                }

                for (ListItem item : items) {
                    sum += item.getPrice();
                }

                bill.setSentTo(sentTo);
                bill.setCreatedBy(dataProvider.getCurrentUserUid());
                bill.setSum(sum);

                dataProvider.createBill(bill, new OnAsyncCallListener<Bill>() {
                    @Override
                    public void onFailure(ApiException e) {
                        CreateBillActivity.this.runOnUiThread(() -> {
                            Toast.makeText(CreateBillActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                            editDueDate.setEnabled(true);
                            save.setClickable(true);
                        });
                    }

                    @Override
                    public void onSuccess(Bill result) {
                        CreateBillActivity.this.runOnUiThread(() -> {
                            editDueDate.setEnabled(true);
                            save.setClickable(true);

                            setResult(RESULT_OK);
                            finish();
                        });
                    }
                });

            } else {
                editDueDate.setEnabled(true);
                save.setClickable(true);

                layoutDueDate.setError(getString(R.string.create_bill_due_date_error));
            }
        });

        layoutDueDate = findViewById(R.id.create_bill_container_master_input);
        editDueDate = findViewById(R.id.create_bill_due_date_edit);

        final DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, i, i1, i2) -> {
            dueDateCalendar.set(Calendar.YEAR, i);
            dueDateCalendar.set(Calendar.MONTH, i1);
            dueDateCalendar.set(Calendar.DAY_OF_MONTH, i2);

            DateFormat format = DateFormat.getDateInstance();
            Date date = dueDateCalendar.getTime();
            editDueDate.setText(format.format(date));

            if (dueDateCalendar.before(now)) {
                layoutDueDate.setError(getString(R.string.create_bill_due_date_error));

            } else {
                dueDate = date;
                layoutDueDate.setError(null);
            }
        };

        editDueDate.setOnClickListener(view -> {
            new DatePickerDialog(CreateBillActivity.this, onDateSetListener, dueDateCalendar
                .get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        itemsView = findViewById(R.id.create_bill_items_container);
        recipientsView = findViewById(R.id.create_bill_recipients_container);

        initializeItemsView();
        initializeRecipientsView();
    }

    private void prepareData() {
        for (String itemUid : itemUids) {
            ListItem item = dataProvider.getListItem(UUID.fromString(itemUid));

            if (item.getPrice() != null) {
                double price = ((double) item.getPrice()) / 100;
                double pricePerRecipient = price / item.getRequestedFor().size();

                for (String userUid : item.getRequestedFor()) {
                    User user = dataProvider.getUserByUid(userUid);

                    if (!recipientPriceMapping.containsKey(user)) {
                        recipientPriceMapping.put(user, pricePerRecipient);

                    } else {
                        double actual = recipientPriceMapping.get(user);
                        actual += pricePerRecipient;
                        recipientPriceMapping.put(user, actual);
                    }
                }

                items.add(item);
            }
        }
    }

    private void initializeItemsView() {
        TransitionManager.beginDelayedTransition(itemsView);
        itemsView.removeAllViews();

        for (ListItem item : items) {
            View view = getLayoutInflater().inflate(R.layout.bill_detail_items_layout, itemsView, false);

            TextView itemName = view.findViewById(R.id.bill_list_item_name);
            TextView itemPrice = view.findViewById(R.id.bill_list_item_price);

            itemName.setText(item.getTitle());

            double price = ((double) item.getPrice()) / 100;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(dataProvider.getCurrentGroupCurrency());
            itemPrice.setText(format.format(price));

            itemsView.addView(view);
        }
    }

    private void initializeRecipientsView() {
        TransitionManager.beginDelayedTransition(recipientsView);
        recipientsView.removeAllViews();

        for (User user : recipientPriceMapping.keySet()) {
            double price = recipientPriceMapping.get(user);

            View view = getLayoutInflater().inflate(R.layout.bill_recipients_list_layout, recipientsView,
                    false);

            CircularImageView recipientImage = view.findViewById(R.id.bill_list_recipient_picture);
            TextView recipientName = view.findViewById(R.id.bill_list_recipient_name);
            TextView recipientPrice = view.findViewById(R.id.bill_list_recipient_price);

            Bitmap pic = dataProvider.getGroupMemberPicture(user.getUid());

            recipientImage.setImageBitmap(pic);
            recipientName.setText(user.getDisplayName());

            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(dataProvider.getCurrentGroupCurrency());
            recipientPrice.setText(format.format(price));

            recipientsView.addView(view);
        }
    }
}
