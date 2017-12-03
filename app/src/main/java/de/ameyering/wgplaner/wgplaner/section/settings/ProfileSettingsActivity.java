package de.ameyering.wgplaner.wgplaner.section.settings;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class ProfileSettingsActivity extends AppCompatActivity {
    public static final int REQ_CODE_PICK_IMAGE = 0;
    
    private DataProvider dataProvider = DataProvider.getInstance();

    private Button btLeaveGroup;
    private EditText inputName;
    private EditText inputEmail;
    private CircularImageView image;
    private Menu menu;

    private Bitmap bitmap;
    private Uri selectedImage;

    private boolean isInEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Toolbar toolbar = findViewById(R.id.profile_settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInEditMode) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingsActivity.this);
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

        //Choose image
        image = findViewById(R.id.profile_settings_profile_picture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
            }
        });
        image.setEnabled(false);

        image.setImageBitmap(dataProvider.getCurrentUserImage(this));

        btLeaveGroup = findViewById(R.id.bt_delete_group_profile_settings);

        inputName = findViewById(R.id.tfName_profile_settings);
        String displayName = dataProvider.getCurrentUserDisplayName();

        if (displayName != null) {
            inputName.setText(displayName);
        }

        inputEmail = findViewById(R.id.tfEmail_profile_settings);
        String email = dataProvider.getCurrentUserEmail();

        if (email != null) {
            inputEmail.setText(email);
        }

        btLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingsActivity.this);
                builder.setTitle(getString(R.string.dialog_leave_group_title));
                builder.setMessage(getString(R.string.dialog_leave_group_message));

                builder.setPositiveButton(R.string.dialog_leave_group_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dataProvider.leaveCurrentGroup()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ProfileSettingsActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.dialog_discard_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.create().show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_fullscreen_save: {
                if (checkInputAndReturn()) {
                    dataProvider.setCurrentUserDisplayName(inputName.getText().toString());
                    dataProvider.setCurrentUserEmail(inputEmail.getText().toString());
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
                Resources r = getResources();
                float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, r.getDisplayMetrics());
                ObjectAnimator.ofFloat(image, "elevation", elevation).setDuration(200).start();
                this.inputEmail.setEnabled(true);
                return true;
            }
        }

        return false;
    }

    private boolean checkInputAndReturn() {
        String displayName = inputName.getText().toString();

        if (displayName.isEmpty()) {
            Toast.makeText(this, getString(R.string.delete_display_name_error), Toast.LENGTH_LONG).show();
            return false;
        }

        String email = inputEmail.getText().toString();

        dataProvider.setCurrentUserImage(bitmap);

        return true;
    }
}
