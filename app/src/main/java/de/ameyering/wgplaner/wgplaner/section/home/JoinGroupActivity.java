package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCalls;
import io.swagger.client.ApiException;
import io.swagger.client.model.Group;

public class JoinGroupActivity extends AppCompatActivity {
    EditText key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Toolbar toolbar = findViewById(R.id.join_group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinGroupActivity.this);
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

        key = findViewById(R.id.join_group_input_access_key);

        Intent sentIntent = getIntent();

        if (sentIntent.getAction() != null && sentIntent.getAction().equals(Intent.ACTION_QUICK_VIEW)) {
            String key = sentIntent.getStringExtra("ACCESS_KEY");

            if (checkInputAndReturn(key)) {
                this.key.setText(key);
                joinGroup(key);

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
                if (checkInputAndReturn(this.key.getText().toString())) {
                    joinGroup(key.getText().toString());
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkInputAndReturn(String key) {
        if (key != null && !key.isEmpty()) {
            Pattern pattern = Pattern.compile("^[A-Z0-9]{12}$");
            Matcher matcher = pattern.matcher(key);

            return matcher.matches();
        }

        return false;
    }

    private void joinGroup(String key) {
        DataProvider.CurrentGroup.joinGroup(key, new ServerCalls.OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JoinGroupActivity.this, getString(R.string.server_connection_failed),
                            Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onSuccess(Group result) {
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
