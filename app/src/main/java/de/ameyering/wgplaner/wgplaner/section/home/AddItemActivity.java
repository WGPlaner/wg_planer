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

import java.util.ArrayList;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.AddItemRequestedForAdapter;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.AddItemAddUserDialogFragment;
import de.ameyering.wgplaner.wgplaner.structure.User;

public class AddItemActivity extends AppCompatActivity {
    public RecyclerView requestedFor;
    public AddItemRequestedForAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_item_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
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
        requestedFor = (RecyclerView) findViewById(R.id.add_item_requested_for_list);
        adapter = new AddItemRequestedForAdapter();
        requestedFor.setLayoutManager(new LinearLayoutManager(this));
        requestedFor.setHasFixedSize(false);
        requestedFor.setAdapter(adapter);

        Button buttonAddUser = (Button) findViewById(R.id.add_item_add_user_btn);
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemAddUserDialogFragment dialog = new AddItemAddUserDialogFragment();
                dialog.setOnResultListener(new AddItemAddUserDialogFragment.OnResultListener() {
                    @Override
                    public void onResult(ArrayList<User> selected) {
                        adapter.updateSelection(selected);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_full_screen_actvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item_save: {
                if(checkInpustAndReturn()){
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkInpustAndReturn() {
        return false;
    }
}
