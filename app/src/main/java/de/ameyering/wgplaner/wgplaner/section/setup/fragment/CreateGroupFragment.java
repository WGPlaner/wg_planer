package de.ameyering.wgplaner.wgplaner.section.setup.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.adapter.LocaleSpinnerAdapter;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class CreateGroupFragment extends Fragment {
    private static final int REQ_CODE_PICK_IMAGE = 0;

    private String groupName;
    private Bitmap bitmap = null;
    private Currency currency;

    private EditText editGroupName;
    private CircularImageView groupPicture;

    private Locale[] locales = Locale.getAvailableLocales();
    private ArrayList<Currency> currencies = new ArrayList<>();
    private LocaleSpinnerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_create_group, container, false);

        currencies = transformLocale(locales);

        Spinner currencySpinner = view.findViewById(R.id.fragment_setup_create_spinner_currency);
        adapter = new LocaleSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, currencies);
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

        editGroupName = view.findViewById(R.id.fragment_setup_create_input_group_name);
        groupPicture = view.findViewById(R.id.fragment_setup_create_group_picture);

        groupPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
            }
        });

        Button btnCreateGroup = view.findViewById(R.id.fragment_setup_create_btn_create_group);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputsAndReturn()) {
                    createGroup();
                }
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE: {
                if (resultCode == getActivity().RESULT_OK) {
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


    private void createGroup() {
        if(DataProvider.getInstance().createGroup(groupName, currency, null)){
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), getString(R.string.server_connection_failed),
                        Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private boolean checkInputsAndReturn() {
        groupName = editGroupName.getText().toString();

        if (groupName.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.set_up_group_name_error),
                Toast.LENGTH_LONG).show();
            return false;
        }

        if (currency == null) {
            Toast.makeText(getContext(), getString(R.string.set_up_group_currency_error),
                Toast.LENGTH_LONG).show();
        }

        return true;
    }
}
