package de.ameyering.wgplaner.wgplaner.section.splashscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.home.JoinGroupActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ServerCalls;
import io.swagger.client.ApiException;
import io.swagger.client.model.User;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String FIREBASE_AUTH_TAG = "FIREBASE_AUTH";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean loadJoinGroup = false;
    private String joinGroupKey = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = findViewById(R.id.progressBar);
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

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            List<String> path = appLinkData.getPathSegments();

            if (path.size() != 0 && path.get(0).equals("groups") && path.get(1).equals("join")) {
                joinGroupKey = path.get(3);
                loadJoinGroup = true;
            }
        }
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
            String uid = user.getUid();
            DataProvider.initialize(uid, ServerCalls.getInstance());
            initializeUser(uid);
        }
    }

    private void initializeUser(String uid) {
        if (uid != null) {
            DataProvider.Users.initializeCurrentUser(new ServerCalls.OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    switch (e.getCode()) {
                        case 401:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashScreenActivity.this, getString(R.string.user_unauthorized),
                                        Toast.LENGTH_LONG).show();
                                }
                            });
                            break;

                        case 404:
                            loadRegistration();
                            break;

                        default:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashScreenActivity.this, getString(R.string.server_connection_failed),
                                        Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                    }
                }

                @Override
                public void onSuccess(User result) {
                    getData();
                    loadNext();
                }
            });
        }
    }

    private void initialize() {
        Configuration.initConfig(this);
        Money.initialize(Locale.getDefault());
    }

    private void loadHome() {
        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadRegistration() {
        Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadNext() {
        if (loadJoinGroup) {
            onLoadJoinGroup();

        } else {
            loadHome();
        }
    }

    private void onLoadJoinGroup() {
        Intent intent = new Intent(this, JoinGroupActivity.class);
        intent.putExtra("ACCESS_KEY", joinGroupKey);
        intent.setAction(Intent.ACTION_QUICK_VIEW);
        startActivity(intent);
        finish();
    }

    private void getData() {
        DataProvider.CurrentGroup.updateGroup();

        if (DataProvider.CurrentGroup.getGroup().getUid() != null) {
            DataProvider.ShoppingList.updateShoppingList();
        }
    }
}
