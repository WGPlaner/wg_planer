package de.ameyering.wgplaner.wgplaner.section.registration.fragment;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

public class UploadProfilePictureFragment extends NavigationFragment {
    public static final int REQ_CODE_PICK_IMAGE = 0;
    public static final int REQ_CODE_CROP_IMAGE = 1;
    private CircularImageView image;
    private Bitmap bitmap;
    private Uri selectedImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_profile_picture_registration, container,
                false);

        image = view.findViewById(R.id.registration_profile_picture);
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

        Button buttonContinue = view.findViewById(R.id.btn_continue_upload_prfoile_picture);
        Button buttonSkip = view.findViewById(R.id.btn_skip_upload_profile_picture);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNavigationEventListener != null) {
                    SaveBitmap saveTask = new SaveBitmap();
                    saveTask.execute(bitmap);

                    mNavigationEventListener.onForward();
                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNavigationEventListener != null) {
                    mNavigationEventListener.onForward();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {

                    selectedImage = data.getData();

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setType("image/*");

                    List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(intent, 0);

                    if (activities.size() == 0) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                            bitmap = scaleBitmap(bitmap);

                            image.setImageBitmap(bitmap);
                            image.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                    R.anim.anim_load_new_profile_picture));

                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Failed to load Picture", Toast.LENGTH_LONG).show();
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
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle extras = data.getExtras();

                    if (extras != null) {
                        bitmap = extras.getParcelable("data");
                        bitmap = scaleBitmap(bitmap);

                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.anim_load_new_profile_picture));
                    }

                } else {
                    if (selectedImage != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                            bitmap = scaleBitmap(bitmap);

                            image.setImageBitmap(bitmap);
                            image.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                    R.anim.anim_load_new_profile_picture));

                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Failed to load Picture", Toast.LENGTH_LONG).show();
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
            bitmap = Configuration.singleton.getProfilePicture(getContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UploadProfilePictureFragment.this.bitmap = bitmap;
            UploadProfilePictureFragment.this.image.setImageBitmap(bitmap);
        }
    }

    private class SaveBitmap extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps.length >= 0) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Configuration.singleton.setProfilePicture(stream.toByteArray());
            }

            return null;
        }
    }
}
