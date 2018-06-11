package de.ameyering.wgplaner.wgplaner.section.home;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.OnAsyncCallListener;
import io.swagger.client.ApiException;
import io.swagger.client.model.User;

public class EditEmailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout editLayout;
    private EditText edit;
    private Button save;
    private Button cancel;

    private DataProviderInterface dataProviderInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        WGPlanerApplication application = (WGPlanerApplication) getApplication();
        dataProviderInterface = application.getDataProviderInterface();

        toolbar = findViewById(R.id.edit_email_toolbar);
        editLayout = findViewById(R.id.edit_email_layout);
        edit = findViewById(R.id.edit_email_view);
        save = findViewById(R.id.edit_email_save);
        cancel = findViewById(R.id.edit_email_cancel);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        save.setOnClickListener(view -> {
            String email = edit.getText().toString();
            String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if(matcher.matches()) {
                runOnUiThread(() -> {
                    editLayout.setError(null);

                    save.setClickable(false);
                    cancel.setClickable(false);
                    edit.setEnabled(false);
                });

                dataProviderInterface.setCurrentUserEmail(email, new OnAsyncCallListener<User>() {
                    @Override
                    public void onFailure(ApiException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(EditEmailActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();

                            save.setClickable(true);
                            cancel.setClickable(true);
                            edit.setEnabled(true);
                        });
                    }

                    @Override
                    public void onSuccess(User result) {
                        runOnUiThread(() -> {
                            setResult(Activity.RESULT_OK);
                            finish();
                        });
                    }
                });
            } else {
                runOnUiThread(() -> editLayout.setError(getString(R.string.invalid_email)));
            }
        });

        cancel.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
