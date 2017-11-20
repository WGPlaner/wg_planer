package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.AddItemRequestedForAdapter;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.AddItemAddUserDialogFragment;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.ShoppinglistApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.User;
import de.ameyering.wgplaner.wgplaner.utils.DataContainer;
import io.swagger.client.model.ListItem;

public class AddItemActivity extends AppCompatActivity {
    public RecyclerView requestedFor;
    public AddItemRequestedForAdapter adapter;

    public EditText nameInput;
    public EditText numberInput;

    private ArrayList<User> selected = new ArrayList<>();
    private ListItem newItem = new ListItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = findViewById(R.id.add_item_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                builder.setMessage(getString(R.string.dialog_discard_message));
                builder.setPositiveButton(R.string.dialog_discard_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.dialog_discard_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        nameInput = findViewById(R.id.add_item_name_input);
        numberInput = findViewById(R.id.add_item_number_input) ;

        requestedFor = findViewById(R.id.add_item_requested_for_list);
        adapter = new AddItemRequestedForAdapter();
        requestedFor.setLayoutManager(new LinearLayoutManager(this));
        requestedFor.setHasFixedSize(false);
        requestedFor.setAdapter(adapter);

        Button buttonAddUser = findViewById(R.id.add_item_add_user_btn);
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemAddUserDialogFragment dialog = new AddItemAddUserDialogFragment();
                dialog.setSelectedItems(adapter.getSelection());
                dialog.setOnResultListener(new AddItemAddUserDialogFragment.OnResultListener() {
                    @Override
                    public void onResult(ArrayList<User> selected) {
                        AddItemActivity.this.selected.clear();
                        AddItemActivity.this.selected.addAll(selected);

                        adapter.updateSelection(AddItemActivity.this.selected);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_full_screen_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item_save: {
                if (checkInputAndReturn()) {
                    DataContainer.ShoppingListItems.createShoppingListItem(DataContainer.CallBehavior.PARALLEL, newItem);
                    return true;
                }
            }
        }

        return false;
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

            List<String> users = new ArrayList<>();

            for(User user: selected){
                users.add(user.getUid());
            }

            newItem.setRequestedFor(users);

            return true;
        }

        return false;
    }
}
