package de.ameyering.wgplaner.wgplaner.section.settings.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import io.swagger.client.model.User;


public class UserDetailDialogFragment extends DialogFragment {
    public static final String UID_BUNDLE_ARG = "USER_UID_ARG";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(savedInstanceState != null) {
            String uid = savedInstanceState.getString(UID_BUNDLE_ARG);

            if(uid != null) {
                User user = DataProvider.getInstance().getUserByUid(uid);

                LayoutInflater inflater = getActivity().getLayoutInflater();

                View view = inflater.inflate(R.layout.fragment_user_detail_dialog, null);
                TextView name = view.findViewById(R.id.fragment_user_detail_username);
                TextView email = view.findViewById(R.id.fragment_user_detail_email);
                TextView createdAt = view.findViewById(R.id.fragment_user_detail_created_at);
                ImageView close = view.findViewById(R.id.fragment_user_detail_close);

                CircularImageView image = view.findViewById(R.id.fragment_user_detail_image);

                Bitmap bitmap = ImageStore.getInstance().loadGroupMemberPicture(uid, getActivity());

                if(bitmap != null){
                    image.setImageBitmap(bitmap);
                }

                name.setText(user.getDisplayName());

                String emailVal = user.getEmail();

                if(emailVal != null && !emailVal.isEmpty()){
                    email.setText(emailVal);
                } else {
                    email.setText(getString(R.string.user_detail_email_default));
                    email.setTypeface(null, Typeface.ITALIC);
                }

                DateTime date = user.getCreatedAt();

                if(date != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    String dateString = sdf.format(date.toDate());

                    createdAt.setText(dateString);
                }

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserDetailDialogFragment.this.dismiss();
                    }
                });

                builder.setView(view);
                return builder.create();
            }
        }

        return null;
    }
}
