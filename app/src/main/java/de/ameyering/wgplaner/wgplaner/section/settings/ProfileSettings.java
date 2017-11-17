package de.ameyering.wgplaner.wgplaner.section.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.home.AddItemActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.PickDisplayNameFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.UploadProfilePictureFragment;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

public class ProfileSettings extends AppCompatActivity {

    private Button btLeaveGroup;
    private EditText inputName;
    private EditText inputEmail;

    public static final int REQ_CODE_PICK_IMAGE = 0;
    public static final int REQ_CODE_CROP_IMAGE = 1;
    private CircularImageView image;
    private Bitmap bitmap;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);
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

        //Choose image
        image = findViewById(R.id.profile_settings_profile_picture);
        image.addOnRotationListener(new CircularImageView.OnRotationListener() {
            @Override
            public void onRotateLeft(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onRotateRight(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }
        });
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

        btLeaveGroup = findViewById(R.id.bt_delete_group_profile_settings);

        inputName = findViewById(R.id.tfName_profile_settings);
        String displayName = Configuration.singleton.getConfig(Configuration.Type.USER_DISPLAY_NAME);
        if (displayName != null) {
            inputName.setText(displayName);
        }

        inputEmail = findViewById(R.id.tfEmail_profile_settings);
        String mEmail = Configuration.singleton.getConfig(Configuration.Type.USER_EMAIL_ADDRESS);
        if (mEmail != null) {
            inputEmail.setText(mEmail);
        }

        btLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Delete the connection between the User and the WG
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
        //TODO: Implement Intent result
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

    private class LoadBitmap extends AsyncTask<Void, Void, Void> {
        private Bitmap bitmap;

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = Configuration.singleton.getProfilePicture(ProfileSettings.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            image.setImageBitmap(bitmap);
            super.onPostExecute(aVoid);
        }
    }

    private class SaveBitmap extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            if(bitmaps.length > 0) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                Configuration.singleton.setProfilePicture(stream.toByteArray());
            }
            return null;
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
        if (displayName != null && !displayName.isEmpty()) {
            inputName.setText(displayName);
            Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);

            String emailAddress = inputEmail.getText().toString();
            if (emailAddress != null) {
                if (isValidEmail(emailAddress)) {
                    Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, emailAddress);

                    SaveBitmap saveTask = new SaveBitmap();
                    saveTask.execute(bitmap);

                    return true;
                } else {
                    //TODO send notification that the Email is wrong
                    return false;
                }
            } else {
                //TODO send notification that the Email is wrong
                return false;
            }
        } else {
            //TODO send notification that the name can not be empty
            return false;
        }
    }
}
