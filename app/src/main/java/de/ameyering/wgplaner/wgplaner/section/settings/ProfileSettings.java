package de.ameyering.wgplaner.wgplaner.section.settings;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.AddItemActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.PickDisplayNameFragment;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

public class ProfileSettings extends AppCompatActivity {

    private Button btLeaveGroup;
    private Button btSaveChanges;
    private EditText inputName;
    private EditText inputEmail;

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

        btLeaveGroup = findViewById(R.id.bt_delete_group_profile_settings);
        btSaveChanges = findViewById(R.id.bt_save_changes_profile_settings);

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

        btSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save the new Name
                String displayName = inputName.getText().toString();
                if (displayName != null && !displayName.isEmpty()) {
                    inputName.setText(displayName);
                    Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);
                }

                //Save the new Email-address
                String emailAddress = inputEmail.getText().toString();
                if (emailAddress != null) {
                    if (isValidEmail(emailAddress)) {
                        Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, emailAddress);
                    }
                    else{
                        //TODO send notification that the Email is wrong
                    }
                }
                else{
                    //TODO send notification that the name can not be empty
                }
            }
        });

    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
