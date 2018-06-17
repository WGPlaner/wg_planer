package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import io.swagger.client.ApiException;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

public class UserDetail extends AppCompatActivity {
    private static final int CODE_DISPLAY_NAME = 0;
    private static final int CODE_EMAIL = 1;
    private static final int CODE_PICK_IMAGE = 2;
    private static int standard_width = 512;
    private static int standard_text_size = 300;

    private DataProviderInterface dataProvider;

    private String userUid;
    private User user;
    private DateFormat format = DateFormat.getDateInstance();
    private Bitmap bitmap;
    private Uri selectedImage;

    private Toolbar toolbar;
    private ImageView toolbarImage;
    private FloatingActionButton pickImage;

    private TextView userAttributeNameView;
    private TextView userAttributeEmailView;
    private TextView userAttributeCreatedAtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        WGPlanerApplication application = (WGPlanerApplication) getApplication();
        dataProvider = application.getDataProviderInterface();

        Intent intent = getIntent();
        userUid = intent.getStringExtra(Intent.EXTRA_UID);

        if (userUid != null) {
            user = dataProvider.getUserByUid(userUid);

            if (user != null) {
                toolbar = findViewById(R.id.toolbar);
                toolbarImage = findViewById(R.id.app_bar_image);
                pickImage = findViewById(R.id.user_detail_edit);

                userAttributeNameView = findViewById(R.id.user_attribute_name_view);
                userAttributeEmailView = findViewById(R.id.user_attribute_email_view);
                userAttributeCreatedAtView = findViewById(R.id.user_attribute_created_at_view);

                initViews();

                return;
            }
        }

        setResult(RESULT_CANCELED);
        finish();
    }

    private void initViews() {
        boolean isEditable = user.getUid().equals(dataProvider.getCurrentUserUid());
        setUpHeader(isEditable);
        setUpContent(isEditable);
    }

    private void setUpContent(boolean editable) {

        userAttributeCreatedAtView.setText(format.format(user.getCreatedAt().toDate()));

        userAttributeNameView.setText(user.getDisplayName());

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            userAttributeEmailView.setText(user.getEmail());

        } else {
            userAttributeEmailView.setText(R.string.user_detail_email_default);
        }

        if (editable) {
            userAttributeNameView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_black), null);
            userAttributeNameView.setOnClickListener(view -> startActivityForResult(new Intent(UserDetail.this,
                        EditDisplayNameActivity.class), CODE_DISPLAY_NAME));
            userAttributeEmailView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_black), null);
            userAttributeEmailView.setOnClickListener(view -> startActivityForResult(new Intent(UserDetail.this,
                        EditEmailActivity.class), CODE_EMAIL));

        } else {
            userAttributeNameView.setCompoundDrawables(null, null, null, null);
            userAttributeEmailView.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setUpHeader(boolean editable) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        getSupportActionBar().setTitle("");

        bitmap = dataProvider.getGroupMemberPicture(user.getUid());

        toolbarImage.setImageBitmap(bitmap);

        if (editable) {
            pickImage.setVisibility(View.VISIBLE);
            pickImage.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                CharSequence[] options = new CharSequence[2];
                options[0] = getString(R.string.pick_image);
                options[1] = getString(R.string.generate_image);
                builder.setItems(options, (dialogInterface, i) -> {
                    if (i == 0) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CODE_PICK_IMAGE);

                    } else if (i == 1) {
                        bitmap = createStandardBitmap(dataProvider.getCurrentUserDisplayName());

                        dataProvider.setCurrentUserImage(bitmap, new OnAsyncCallListener<SuccessResponse>() {
                            @Override
                            public void onFailure(ApiException e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(UserDetail.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                                });
                            }

                            @Override
                            public void onSuccess(SuccessResponse result) {
                                runOnUiThread(() -> toolbarImage.setImageBitmap(bitmap));
                            }
                        });
                    }
                });

                builder.show();
            });

        } else {
            pickImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_DISPLAY_NAME && resultCode == RESULT_OK) {
            user.setDisplayName(dataProvider.getCurrentUserDisplayName());
            initViews();
        }

        if (requestCode == CODE_EMAIL && resultCode == RESULT_OK) {
            user.setEmail(dataProvider.getCurrentUserEmail());
            initViews();
        }

        if (requestCode == CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            selectedImage = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                bitmap = scaleBitmap(bitmap);

                dataProvider.setCurrentUserImage(bitmap, new OnAsyncCallListener<SuccessResponse>() {
                    @Override
                    public void onFailure(ApiException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(UserDetail.this, getString(R.string.load_image_failed), Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onSuccess(SuccessResponse result) {
                        runOnUiThread(() -> toolbarImage.setImageBitmap(bitmap));
                    }
                });

            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.load_image_failed), Toast.LENGTH_LONG).show();
            }
        }

    }

    private Bitmap createStandardBitmap(String displayName) {
        Bitmap standard = Bitmap.createBitmap(standard_width, standard_width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(standard);
        Paint paint = new Paint();

        Random random = new Random();
        int randomRed = random.nextInt(230);
        int randomGreen = random.nextInt(230);
        int randomBlue = random.nextInt(230);

        int color = Color.argb(255, randomRed, randomGreen, randomBlue);

        paint.setColor(color);

        canvas.drawRect(0, 0, standard_width, standard_width, paint);


        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(standard_text_size);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int)((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        canvas.drawText(displayName.substring(0, 1), xPos, yPos, textPaint);

        return standard;
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int maxLength = Math.max(bitmap.getHeight(), bitmap.getWidth());
        float scale = (float) 800 / (float) maxLength;

        int newWidth = Math.round(bitmap.getWidth() * scale);
        int newHeight = Math.round(bitmap.getHeight() * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
