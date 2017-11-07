package de.ameyering.wgplaner.wgplaner.section.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.User;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String FIREBASE_AUTH_TAG = "FIREBASE_AUTH";
    private FirebaseAuth mAuth;
    private String uid = null;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (currentUser == null) {
                        Log.d(FIREBASE_AUTH_TAG, "logInAnonymously:success");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        onUser(currentUser);
                    }
                } else {
                    Log.d(FIREBASE_AUTH_TAG, "logInAnonymously:failure");
                    Toast.makeText(SplashScreenActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    onUser(null);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        onUser(currentUser);
    }

    private void onUser(FirebaseUser user) {
        initialize();
        if (user != null) {
            uid = user.getUid();

            if (Configuration.singleton.getConfig(Configuration.Type.USER_UID) != uid) {
                Configuration.singleton.addConfig(Configuration.Type.USER_UID, uid);
            }

            initializeUser(uid);
        }
    }

    private void initializeUser(String uid) {
        if (uid != null) {

            UserApi api = new UserApi();

            api.getUser(uid, new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    if (response != null) {
                        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    switch (error.networkResponse.statusCode) {
                        case 401:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashScreenActivity.this, getString(R.string.user_unauthorized), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case 404:
                            Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashScreenActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                                    //finish();
                                }
                            });
                            break;
                    }
                }
            });
        }
    }

    private void initialize() {
        Configuration.initConfig(this);
        Money.initialize(Locale.getDefault());
    }

    private void getData(String uid) {
        //TODO: Get Data from AppServer
    }
}
