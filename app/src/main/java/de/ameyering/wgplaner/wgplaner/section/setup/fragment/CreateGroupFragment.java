package de.ameyering.wgplaner.wgplaner.section.setup.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.model.Group;
import io.swagger.client.model.SuccessResponse;

public class CreateGroupFragment extends Fragment {
    private static final int REQ_CODE_PICK_IMAGE = 0;
    private static int standard_width = 512;
    private static int standard_text_size = 300;

    private String groupName;
    private Bitmap bitmap = null;

    private TextInputLayout editGroupNameLayout;
    private TextInputLayout editGroupCountryLayout;
    private EditText editGroupName;
    private AutoCompleteTextView editGroupCountry;
    private CircularImageView groupPicture;

    private Button btnCreateGroup;

    private Locale[] locales = Locale.getAvailableLocales();
    private HashMap<String, String> currencyMapping = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_create_group, container, false);

        editGroupCountry = view.findViewById(R.id.fragment_setup_create_group_country);
        transformCurrencies(locales);
        String[] countries = new String[currencyMapping.keySet().size()];
        countries = currencyMapping.keySet().toArray(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
            countries);
        editGroupCountry.setAdapter(adapter);

        editGroupCountryLayout = view.findViewById(R.id.fragment_setup_create_group_country_input_layout);
        editGroupCountryLayout.setErrorEnabled(true);

        editGroupNameLayout = view.findViewById(R.id.fragment_setup_create_group_name_input_layout);
        editGroupNameLayout.setErrorEnabled(true);

        editGroupName = view.findViewById(R.id.fragment_setup_create_input_group_name);
        groupPicture = view.findViewById(R.id.fragment_setup_create_group_picture);

        groupPicture.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
        });

        btnCreateGroup = view.findViewById(R.id.fragment_setup_create_btn_create_group);
        btnCreateGroup.setOnClickListener(view12 -> {
            if (checkInputsAndReturn()) {
                createGroup();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();

                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        bitmap = scaleBitmap(bitmap);

                        groupPicture.setImageBitmap(bitmap);
                        groupPicture.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.anim_load_new_profile_picture));

                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to load picture", Toast.LENGTH_LONG).show();
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


    private void createGroup() {
        String code = currencyMapping.get(editGroupCountry.getText().toString());

        DataProvider.getInstance().createGroup(groupName, code, bitmap, getContext(),
        new ServerCallsInterface.OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), getString(R.string.server_connection_failed),
                        Toast.LENGTH_LONG).show();
                    btnCreateGroup.setEnabled(true);
                    editGroupName.setEnabled(true);
                    editGroupCountry.setEnabled(true);
                });
            }

            @Override
            public void onSuccess(Group result) {
                if(bitmap == null) {
                    bitmap = createStandardBitmap(result.getDisplayName());
                }

                DataProvider.getInstance().setCurrentGroupImage(bitmap, null);

                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btnCreateGroup.setEnabled(false);
        editGroupName.setEnabled(false);
        editGroupCountry.setEnabled(false);
    }


    private boolean checkInputsAndReturn() {
        groupName = editGroupName.getText().toString();
        String groupCountry = editGroupCountry.getText().toString();

        if (groupName.trim().isEmpty()) {
            editGroupNameLayout.setError(getString(R.string.error_group_name_invalid));
            return false;

        } else {
            editGroupNameLayout.setError(null);
        }

        if (groupCountry.isEmpty() || !currencyMapping.containsKey(groupCountry)) {
            editGroupCountryLayout.setError(getString(R.string.error_invalid_currency));
            return false;

        } else {
            editGroupNameLayout.setError(null);
        }

        return true;
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

        canvas.drawRect(0, 0, standard_width, standard_width,paint);


        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(standard_text_size);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        canvas.drawText(displayName.substring(0, 1), xPos, yPos, textPaint);

        return standard;
    }
}
