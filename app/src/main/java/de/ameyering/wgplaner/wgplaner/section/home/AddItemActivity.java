package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.AddItemRequestedForAdapter;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.AddItemAddUserDialogFragment;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import io.swagger.client.ApiException;
import io.swagger.client.model.User;
import io.swagger.client.model.ListItem;

public class AddItemActivity extends AppCompatActivity {
    private RecyclerView requestedFor;
    private AddItemRequestedForAdapter adapter;

    private EditText nameInput;
    private EditText numberInput;

    private ArrayList<User> selected = new ArrayList<>();
    private ListItem newItem = new ListItem();

    private DataProviderInterface dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        WGPlanerApplication application = (WGPlanerApplication) getApplication();
        dataProvider = application.getDataProviderInterface();

        Toolbar toolbar = findViewById(R.id.add_item_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        toolbar.setNavigationOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
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

        nameInput = findViewById(R.id.add_item_name_input);
        numberInput = findViewById(R.id.add_item_number_input) ;

        if (savedInstanceState != null) {
            ArrayList<String> selectedUids = savedInstanceState.getStringArrayList("UsersUids");

            if (selectedUids != null && !selectedUids.isEmpty()) {
                for (String uid : selectedUids) {
                    selected.add(dataProvider.getUserByUid(uid));
                }
            }

            String name = savedInstanceState.getString("Name");
            String number = savedInstanceState.getString("Number");

            if (name != null) {
                nameInput.setText(name);
            }

            if (number != null) {
                numberInput.setText(number);
            }
        }

        requestedFor = findViewById(R.id.add_item_requested_for_list);
        adapter = new AddItemRequestedForAdapter();
        requestedFor.setLayoutManager(new LinearLayoutManager(this));
        requestedFor.setHasFixedSize(false);
        requestedFor.setAdapter(adapter);
        adapter.updateSelection(selected);

        Button buttonAddUser = findViewById(R.id.add_item_add_user_btn);
        buttonAddUser.setOnClickListener(view -> {
            AddItemAddUserDialogFragment dialog = new AddItemAddUserDialogFragment();
            dialog.setSelectedItems(selected);
            dialog.setOnResultListener(result -> {
                AddItemActivity.this.selected.clear();
                AddItemActivity.this.selected.addAll(result);

                adapter.updateSelection(AddItemActivity.this.selected);
                adapter.notifyDataSetChanged();
            });
            dialog.show(getSupportFragmentManager(), "");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_full_screen_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_item_save && checkInputAndReturn()) {
            dataProvider.addShoppingListItem(newItem,
                new OnAsyncCallListener<ListItem>() {
                    @Override
                    public void onFailure(ApiException e) {
                        runOnUiThread(() -> Toast.makeText(AddItemActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onSuccess(ListItem result) {
                        runOnUiThread(() -> {
                            setResult(RESULT_OK, new Intent());
                            finish();
                        });
                    }
                });

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<String> selectedUids = new ArrayList<>();

        for (User user : selected) {
            selectedUids.add(user.getUid());
        }

        outState.putStringArrayList("UsersUids", selectedUids);
        outState.putString("Name", nameInput.getText().toString());
        outState.putString("Number", numberInput.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private boolean checkInputAndReturn() {
        String name = nameInput.getText().toString();
        String number = numberInput.getText().toString();

        if (!name.isEmpty() && !number.isEmpty() && !selected.isEmpty()) {
            int num;

            try {
                num = Integer.parseInt(number);

            } catch (Exception e) {
                return false;
            }

            newItem = new ListItem();
            newItem.setTitle(name);
            newItem.setCount(num);
            newItem.setCategory("");

            List<String> users = new ArrayList<>();

            for (User user : selected) {
                users.add(user.getUid());
            }

            newItem.setRequestedFor(users);

            return true;

        } else {
            runOnUiThread(() -> Toast.makeText(AddItemActivity.this, getString(R.string.fill_out_all_fields),
                Toast.LENGTH_LONG).show());
            return false;
        }
    }
}
