package de.ameyering.wgplaner.wgplaner.section.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.home.adapter.LocaleSpinnerAdapter;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

public class GroupSettings extends AppCompatActivity {


    private ArrayList<Currency> currencies = new ArrayList<>();
    private Locale[] locales = Locale.getAvailableLocales();
    private LocaleSpinnerAdapter adapter;
    private Currency currency;

    public static final int REQ_CODE_PICK_IMAGE = 0;
    public static final int REQ_CODE_CROP_IMAGE = 1;
    private CircularImageView image;
    private Bitmap bitmap;
    private Uri selectedImage;

    private EditText inputName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        currencies = transformLocale(locales);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettings.this);
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

        image = findViewById(R.id.group_settings_profile_picture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
            }
        });
        LoadBitmap loadTask = new LoadBitmap();
        loadTask.execute();

        inputName = findViewById(R.id.tfName_profile_settings);
        String displayName = Configuration.singleton.getConfig(Configuration.Type.USER_DISPLAY_NAME);
        if (displayName != null) {
            inputName.setText(displayName);
        }

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

    private class LoadBitmap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = Configuration.singleton.getProfilePicture(GroupSettings.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            image.setImageBitmap(bitmap);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setType("image/*");

                    List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);

                    if (activities.isEmpty()) {
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

                    } else {
                        intent.setData(selectedImage);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 200);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("scale", true);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
                    }
                }

                break;

            case REQ_CODE_CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();

                    if (extras != null) {
                        bitmap = extras.getParcelable("data");
                        bitmap = scaleBitmap(bitmap);

                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(this,
                            R.anim.anim_load_new_profile_picture));
                    }

                } else {
                    if (selectedImage != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            bitmap = scaleBitmap(bitmap);

                            image.setImageBitmap(bitmap);
                            image.startAnimation(AnimationUtils.loadAnimation(this,
                                R.anim.anim_load_new_profile_picture));

                        } catch (IOException e) {
                            Toast.makeText(this, "Failed to load Picture", Toast.LENGTH_LONG).show();
                        }

                        return;
                    }
                }
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
        getMenuInflater().inflate(R.menu.menu_add_full_screen_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item_save: {
                if (checkInputAndReturn()) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkInputAndReturn() {
        String displayName = inputName.getText().toString();

        if (displayName == null || displayName.isEmpty()) {
            Toast.makeText(this, getString(R.string.delete_display_name_error), Toast.LENGTH_LONG).show();
            return false;
        }

        Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);

        //TODO: Send updateUser

        SaveBitmap task = new SaveBitmap();
        task.execute(bitmap);

        return true;
    }

    private class SaveBitmap extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps.length > 0) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                Configuration.singleton.setProfilePicture(stream.toByteArray());
            }

            return null;
        }
    }
}
