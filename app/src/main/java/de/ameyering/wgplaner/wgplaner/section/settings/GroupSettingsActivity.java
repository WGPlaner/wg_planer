package de.ameyering.wgplaner.wgplaner.section.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import de.ameyering.wgplaner.wgplaner.utils.OnDataChangeListener;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.model.Group;
import io.swagger.client.model.User;

public class GroupSettingsActivity extends AppCompatActivity {
    public static final int REQ_CODE_PICK_IMAGE = 0;
    private static final String CLIPBOARD_LABEL = "AccessKey";
    private static int standard_width = 512;
    private static int standard_text_size = 300;

    private DataProviderInterface dataProvider;
    private ImageStore imageStore = ImageStore.getInstance();

    private Bitmap bitmap;
    private Uri selectedImage;

    private Locale[] locales = Locale.getAvailableLocales();
    private HashMap<String, String> currencyMapping = new HashMap<>();

    private boolean isInEditMode = false;

    private CircularImageView groupImage;

    private LinearLayout displayContainer;
    private TextView displayGroupName;
    private TextView displayGroupCurrency;

    private LinearLayout editContainer;
    private TextInputLayout editGroupNameLayout;
    private TextInputLayout editGroupCurrencyLayout;
    private EditText editGroupName;
    private AutoCompleteTextView editGroupCurrency;

    private FloatingActionButton editGroupToggle;

    private LinearLayout actionContainer;
    private LinearLayout membersView;

    private AlertDialog alertDialog = null;

    private OnDataChangeListener groupListener = type -> {
        if (type == DataProviderInterface.DataType.CURRENT_GROUP || type == DataProviderInterface.DataType.CURRENT_GROUP_MEMBERS) {
            runOnUiThread(this::setEditModeDisabled);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        WGPlanerApplication application = (WGPlanerApplication) getApplicationContext();
        dataProvider = application.getDataProviderInterface();

        Toolbar toolbar = findViewById(R.id.group_settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(view -> {
            if (isInEditMode) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingsActivity.this);
                builder.setMessage(getString(R.string.dialog_discard_message));
                builder.setPositiveButton(R.string.dialog_discard_positive, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    setResult(RESULT_CANCELED);
                    finish();
                });
                builder.setNegativeButton(R.string.dialog_discard_negative, (dialogInterface,
                        i) -> dialogInterface.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        groupImage = findViewById(R.id.group_settings_header_image);

        displayContainer = findViewById(R.id.group_settings_header_view);
        displayGroupName = findViewById(R.id.group_settings_name_view);
        displayGroupCurrency = findViewById(R.id.group_settings_currency_view);

        editContainer = findViewById(R.id.group_settings_header_edit);
        editGroupNameLayout = findViewById(R.id.group_settings_name_edit_layout);
        editGroupName = findViewById(R.id.group_setting_name_edit);
        editGroupCurrencyLayout = findViewById(R.id.group_settings_currency_edit_layout);
        editGroupCurrency = findViewById(R.id.group_settings_currency_edit);

        editGroupToggle = findViewById(R.id.group_settings_edit_group);

        actionContainer = findViewById(R.id.group_settings_content_action_container);
        Button actionAddMember = findViewById(R.id.group_settings_action_add_member);

        membersView = findViewById(R.id.group_settings_members_container);

        actionAddMember.setOnClickListener(view -> {
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
            (dialogInterface, i) -> {
                ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(getString(R.string.share_join_intent_chooser))
                .setText(String.format(getString(R.string.share_join_intent_text), urlString))
                .setSubject(getString(R.string.share_join_intent_subject))
                .setType("text/plain")
                .startChooser();
            });
            builder.setNeutralButton(R.string.dialog_invite_friends_button_copy,
            (dialogInterface, i) -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, urlString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(GroupSettingsActivity.this, getString(R.string.access_key_copied),
                    Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton(R.string.dialog_discard_negative, (dialogInterface, i) -> dialogInterface.cancel());

            if (accessKey != null && url != null) {
                builder.setMessage(accessKey);
                alertDialog = builder.create();
                alertDialog.show();

            } else {
                Toast.makeText(GroupSettingsActivity.this, getString(R.string.generate_access_key_error),
                    Toast.LENGTH_LONG).show();
            }
        });

        transformCurrencies(locales);
        String[] countries = new String[currencyMapping.keySet().size()];
        countries = currencyMapping.keySet().toArray(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
            countries);
        editGroupCurrency.setAdapter(adapter);


        initViews();
    }

    private void transformCurrencies(Locale[] locales) {
        HashMap<String, String> mapping = new HashMap<>();

        for (Locale locale : locales) {
            try {
                Currency currency = Currency.getInstance(locale);
                String displayCountry = currency.getDisplayName();

                if (!mapping.containsKey(displayCountry)) {
                    mapping.put(displayCountry, currency.getCurrencyCode());
                }

            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        this.currencyMapping = mapping;
    }

    private void initViews() {
        isInEditMode = false;

        setEditModeDisabled();
    }

    private void initMemberViews() {
        TransitionManager.beginDelayedTransition(membersView);
        membersView.removeAllViews();

        for (User user : dataProvider.getCurrentGroupMembers()) {
            View view = getLayoutInflater().inflate(R.layout.group_settings_member_layout, membersView, false);

            CircularImageView image = view.findViewById(R.id.picture);
            TextView name = view.findViewById(R.id.name);
            TextView email = view.findViewById(R.id.email);

            if (user.getUid().equals(dataProvider.getCurrentUserUid())) {
                image.setImageBitmap(imageStore.getProfileBitmap(this));

            } else {
                image.setImageBitmap(imageStore.loadGroupMemberPicture(user.getUid(), this));
            }

            name.setText(user.getDisplayName());

            if (user.getEmail() != null) {
                email.setVisibility(View.VISIBLE);
                email.setText(user.getEmail());

            } else {
                email.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                String uid = user.getUid();

                @Override
                public void onClick(View view) {
                    //Implement later
                }
            });

            membersView.addView(view);
        }
    }

    private void setEditModeDisabled() {
        displayContainer.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);

        groupImage.setClickable(false);

        if (dataProvider.isAdmin(dataProvider.getCurrentUserUid())) {
            editGroupToggle.setVisibility(View.VISIBLE);
            actionContainer.setVisibility(View.VISIBLE);
            editGroupToggle.setImageResource(R.drawable.ic_mode_edit_white);

            editGroupToggle.setOnClickListener(view -> {
                runOnUiThread(this::setEditModeEnabled);
            });

        } else {
            editGroupToggle.setVisibility(View.GONE);
            actionContainer.setVisibility(View.GONE);
        }

        groupImage.setImageBitmap(dataProvider.getCurrentGroupImage(this));

        displayGroupName.setText(dataProvider.getCurrentGroupName());

        Currency currency = dataProvider.getCurrentGroupCurrency();
        displayGroupCurrency.setText(currency.getDisplayName());

        initMemberViews();
    }

    private void setEditModeEnabled() {
        editContainer.setVisibility(View.VISIBLE);
        displayContainer.setVisibility(View.GONE);

        groupImage.setClickable(true);
        groupImage.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            CharSequence[] options = new CharSequence[2];
            options[0] = getString(R.string.pick_image);
            options[1] = getString(R.string.generate_image);
            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);

                } else if (i == 1) {
                    bitmap = createStandardBitmap(dataProvider.getCurrentGroupName());

                    runOnUiThread(() -> {
                        groupImage.setImageBitmap(bitmap);
                        groupImage.startAnimation(AnimationUtils.loadAnimation(this,
                                R.anim.anim_load_new_profile_picture));
                    });
                }
            });

            builder.show();
        });

        if (dataProvider.isAdmin(dataProvider.getCurrentUserUid())) {
            editGroupToggle.setVisibility(View.VISIBLE);
            actionContainer.setVisibility(View.VISIBLE);
            editGroupToggle.setImageResource(R.drawable.ic_save_white);

            editGroupToggle.setOnClickListener(view -> {
                runOnUiThread(() -> {
                    Group group = checkInputs();

                    if (group != null) {
                        runOnUiThread(() -> {
                            editGroupNameLayout.setEnabled(false);
                            editGroupCurrencyLayout.setEnabled(false);
                            editGroupToggle.setClickable(false);
                        });

                        dataProvider.updateGroup(group, new OnAsyncCallListener<Group>() {
                            @Override
                            public void onFailure(ApiException e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(GroupSettingsActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();

                                    editGroupNameLayout.setEnabled(true);
                                    editGroupCurrencyLayout.setEnabled(true);
                                    editGroupToggle.setClickable(true);
                                });
                            }

                            @Override
                            public void onSuccess(Group result) {
                                dataProvider.setCurrentGroupImage(bitmap, null);

                                runOnUiThread(() -> {
                                    editGroupNameLayout.setEnabled(true);
                                    editGroupCurrencyLayout.setEnabled(true);
                                    editGroupToggle.setClickable(true);

                                    setEditModeDisabled();
                                });
                            }
                        });
                    }
                });
            });

        } else {
            editGroupToggle.setVisibility(View.GONE);
            actionContainer.setVisibility(View.GONE);
        }

        editGroupName.setText(dataProvider.getCurrentGroupName());

        editGroupCurrency.setText(dataProvider.getCurrentGroupCurrency().getDisplayName());

        initMemberViews();
    }

    private Group checkInputs() {
        String name = editGroupName.getText().toString();

        if (name.isEmpty()) {
            editGroupNameLayout.setError(getString(R.string.group_settings_name_error));
            return null;
        }

        editGroupNameLayout.setError(null);

        String currency = editGroupCurrency.getText().toString();

        if (currency.isEmpty() || !currencyMapping.containsKey(currency)) {
            editGroupCurrencyLayout.setError(getString(R.string.group_settings_currency_error));

            return null;
        }

        editGroupCurrencyLayout.setError(null);

        Group group = new Group();

        group.setDisplayName(name);
        group.setCurrency(currencyMapping.get(currency));

        return group;
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

                        groupImage.setImageBitmap(bitmap);
                        groupImage.startAnimation(AnimationUtils.loadAnimation(this,
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
    protected void onPause() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        dataProvider.removeOnDataChangeListener(groupListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        dataProvider.addOnDataChangeListener(groupListener);
        super.onResume();
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
}
