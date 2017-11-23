package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.LocaleSpinnerAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCalls;
import io.swagger.client.ApiException;
import io.swagger.client.model.Group;

public class SetUpGroupActivity extends AppCompatActivity {
    private static final int REQ_CODE_PICK_IMAGE = 0;

    private String groupName;
    private Bitmap bitmap = null;
    private Currency currency;

    private EditText editGroupName;
    private CircularImageView groupPicture;

    private Locale[] locales = Locale.getAvailableLocales();
    private ArrayList<Currency> currencies = new ArrayList<>();
    private LocaleSpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_group);

        currencies = transformLocale(locales);

        Toolbar toolbar = findViewById(R.id.set_up_group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SetUpGroupActivity.this);
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
        Spinner currencySpinner = findViewById(R.id.set_up_spinner_currency);
        adapter = new LocaleSpinnerAdapter(this, android.R.layout.simple_spinner_item, currencies);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                currency = (Currency) adapter.getItem(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Currency currency = Currency.getInstance(Locale.getDefault());
        int pos = currencies.indexOf(currency);

        if (pos != -1) {
            currencySpinner.setSelection(pos);
        }

        editGroupName = findViewById(R.id.set_up_input_group_name);
        groupPicture = findViewById(R.id.set_up_group_picture);

        groupPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
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
        int id = item.getItemId();

        switch (id) {
            case R.id.add_item_save:
                if (checkInputsAndReturn()) {
                    createGroup();
                }

                return true;
        }

        return false;
    }

    private void createGroup() {
        Group group = new Group();
        group.setDisplayName(groupName);
        group.setCurrency(currency.getCurrencyCode());
        ArrayList<String> members = new ArrayList<>();
        members.add(DataProvider.Users.getCurrentUsersUid());
        group.setMembers(members);
        group.setAdmins(members);

        DataProvider.CurrentGroup.createGroup(group, new ServerCalls.OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetUpGroupActivity.this, getString(R.string.server_connection_failed),
                                    Toast.LENGTH_LONG).show();
                            }
                        });
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

    private boolean checkInputsAndReturn() {
        groupName = editGroupName.getText().toString();

        if (groupName.isEmpty()) {
            Toast.makeText(SetUpGroupActivity.this, getString(R.string.set_up_group_name_error),
                Toast.LENGTH_LONG).show();
            return false;
        }

        if (currency == null) {
            Toast.makeText(SetUpGroupActivity.this, getString(R.string.set_up_group_currency_error),
                Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE: {
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        bitmap = scaleBitmap(bitmap);

                        groupPicture.setImageBitmap(bitmap);
                        groupPicture.startAnimation(AnimationUtils.loadAnimation(this,
                                R.anim.anim_load_new_profile_picture));

                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load picture", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int maxLength = Math.max(bitmap.getHeight(), bitmap.getWidth());
        float scale = (float) 800 / (float) maxLength;

        int newWidth = Math.round(bitmap.getWidth() * scale);
        int newHeight = Math.round(bitmap.getHeight() * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private ArrayList<Currency> transformLocale(Locale[] locales) {
        ArrayList<Currency> currencies = new ArrayList<>();

        for (Locale locale : locales) {
            try {
                Currency currency = Currency.getInstance(locale);

                if (!currencies.contains(currency)) {
                    currencies.add(currency);
                }

            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        return currencies;
    }
}
