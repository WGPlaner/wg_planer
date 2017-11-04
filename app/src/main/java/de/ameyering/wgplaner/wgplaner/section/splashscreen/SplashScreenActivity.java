package de.ameyering.wgplaner.wgplaner.section.splashscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String FIREBASE_AUTH_TAG = "FIREBASE_AUTH";
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
                    if(currentUser == null) {
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
        if (user != null) {
            uid = user.getUid();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    initialize();
                    getData(uid);

                    Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).start();

        } else {
            //TODO: Implement Auth failed
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
