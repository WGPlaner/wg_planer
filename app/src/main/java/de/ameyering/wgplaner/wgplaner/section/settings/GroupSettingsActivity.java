package de.ameyering.wgplaner.wgplaner.section.settings;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.settings.adapter.GroupMemberAdapter;
import de.ameyering.wgplaner.wgplaner.section.setup.adapter.LocaleSpinnerAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import io.swagger.client.model.User;

public class GroupSettingsActivity extends AppCompatActivity {
    public static final int REQ_CODE_PICK_IMAGE = 0;
    private static final String CLIPBOARD_LABEL = "AccessKey";

    private DataProvider dataProvider = DataProvider.getInstance();

    public RecyclerView members;
    public GroupMemberAdapter adapterMembers;
    private ArrayList<Currency> currencies = new ArrayList<>();
    private Locale[] locales = Locale.getAvailableLocales();
    private LocaleSpinnerAdapter adapter;
    private Currency currency;
    private Spinner currencySpinner;
    private CircularImageView image;
    private FloatingActionButton fab;
    private Bitmap bitmap;
    private Uri selectedImage;
    private EditText inputName;
    private boolean isInEditMode = false;
    private Menu menu;

    private AlertDialog alertDialog = null;

    private DataProvider.OnDataChangeListener listener = new DataProvider.OnDataChangeListener() {
        @Override
        public void onDataChanged(DataProvider.DataType type) {
            if (type == DataProvider.DataType.CURRENT_GROUP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currency = dataProvider.getCurrentGroupCurrency();
                        int pos = currencies.indexOf(currency);

                        if (pos != -1) {
                            currencySpinner.setSelection(pos);
                        }

                        inputName.setText(dataProvider.getCurrentGroupName());
                    }
                });

            } else if (type == DataProvider.DataType.CURRENT_GROUP_MEMBERS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<User> users = dataProvider.getCurrentGroupMembers();
                        adapterMembers.onNewData(users);

                        image.setImageBitmap(dataProvider.getCurrentGroupImage(GroupSettingsActivity.this));
                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        currencies = transformLocale(locales);

        Toolbar toolbar = findViewById(R.id.group_settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInEditMode) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingsActivity.this);
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

                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        image = findViewById(R.id.group_settings_profile_picture);
        image.setEnabled(false);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
            }
        });

        inputName = findViewById(R.id.tfName_group_settings);
        String displayName = dataProvider.getCurrentGroupName();

        if (displayName != null) {
            inputName.setText(displayName);
        }

        members = findViewById(R.id.rvMembers_group_settings);
        adapterMembers = new GroupMemberAdapter(dataProvider.getCurrentGroupMembers(), this);
        members.setLayoutManager(new LinearLayoutManager(this));
        members.setHasFixedSize(false);
        members.setAdapter(adapterMembers);

        image.setImageBitmap(dataProvider.getCurrentGroupImage(this));

        currencySpinner = findViewById(R.id.spCurrency_group_settings);
        currencySpinner.setEnabled(false);
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

        fab = findViewById(R.id.group_settings_fab_invite_friends);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingsActivity.this);

                String accessKey = dataProvider.createGroupAccessKey();
                URL url = null;

                try {
                    url = new URL("https://api.wgplaner.ameyering.de/groups/join/" + accessKey);

                } catch (MalformedURLException e) {
                    Log.e("URL", ":" + e);
                }

                final String urlString = url.toString();

                builder.setTitle(getString(R.string.dialog_invite_friends_title));
                builder.setPositiveButton(R.string.dialog_invite_friends_button_share,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, urlString);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)));
                    }
                });
                builder.setNeutralButton(R.string.dialog_invite_friends_button_copy,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, urlString);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(GroupSettingsActivity.this, getString(R.string.access_key_copied),
                            Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(R.string.dialog_discard_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                if (accessKey != null && url != null) {
                    builder.setMessage(accessKey);
                    alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(GroupSettingsActivity.this, getString(R.string.generate_access_key_error),
                        Toast.LENGTH_LONG).show();
                }

            }
        });

        if (!dataProvider.isAdmin(dataProvider.getCurrentUserUid())) {
            fab.setVisibility(View.GONE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        bitmap = scaleBitmap(bitmap);

                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(this,
                                R.anim.anim_load_new_profile_picture));

                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load Picture", Toast.LENGTH_LONG).show();
                    }

                    return;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        if (dataProvider.isAdmin(dataProvider.getCurrentUserUid())) {
            getMenuInflater().inflate(R.menu.menu_edit_full_screen_actvity, menu);
            MenuItem save = menu.findItem(R.id.edit_fullscreen_save);
            MenuItem edit = menu.findItem(R.id.edit_fullscreen_edit);

            if (isInEditMode) {
                save.setVisible(true);
                edit.setVisible(false);

            } else {
                edit.setVisible(true);
                save.setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_fullscreen_save: {
                if (checkInputAndReturn()) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
            }

            case R.id.edit_fullscreen_edit: {
                isInEditMode = true;
                item.setVisible(false);
                MenuItem save = menu.findItem(R.id.edit_fullscreen_save);
                save.setVisible(true);
                this.inputName.setEnabled(true);
                this.image.setEnabled(true);
                currencySpinner.setEnabled(true);
                Resources r = getResources();
                float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, r.getDisplayMetrics());
                ObjectAnimator.ofFloat(image, "elevation", elevation).setDuration(200).start();
                return true;
            }
        }

        return false;
    }

    private boolean checkInputAndReturn() {
        String displayName = inputName.getText().toString();

        if (displayName == null || displayName.isEmpty()) {
            Toast.makeText(this, getString(R.string.delete_group_name_error), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!displayName.equals(dataProvider.getCurrentGroupName())) {
            dataProvider.setCurrentGroupName(displayName);
        }

        if (currency == null || currencies.isEmpty()) {
            Toast.makeText(this, getString(R.string.delete_currency_error), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!currency.equals(dataProvider.getCurrentGroupCurrency())) {
            dataProvider.setCurrentGroupCurrency(currency);
        }

        dataProvider.setCurrentGroupImage(bitmap);

        return true;
    }

    @Override
    protected void onPause() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        dataProvider.removeOnDataChangeListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        dataProvider.addOnDataChangeListener(listener);
        super.onResume();
    }
}
