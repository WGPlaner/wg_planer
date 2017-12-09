package de.ameyering.wgplaner.wgplaner.section.splashscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.SetUpActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String FIREBASE_AUTH_TAG = "FIREBASE_AUTH";
    private static final String PATH_PATTERN =
        "^(http|https)://api.wgplaner.ameyering.de/groups/join/[A-Z0-9]{12}";
    private DataProvider dataProvider;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LinearLayout errorContainer;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeToRefresh;

    private Intent joinGroupIntent = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            Pattern pattern = Pattern.compile(PATH_PATTERN);
            Matcher matcher = pattern.matcher(data.toString());

            if (matcher.matches()) {
                this.joinGroupIntent = intent;
            }
        }


        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        errorContainer = findViewById(R.id.splash_screen_error_container);
        errorMessage = findViewById(R.id.splash_screen_error_message);

        swipeToRefresh = findViewById(R.id.splash_screen_swipe_to_refresh);
        swipeToRefresh.setEnabled(false);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retryAnim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initializeUser(dataProvider.getCurrentUserUid());
                    }
                }).start();
            }
        });


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
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = mAuth.getCurrentUser();
                onUser(currentUser);
            }
        }).start();
    }

    private void onUser(FirebaseUser user) {
        initialize();

        if (user != null) {
            String uid = user.getUid();
            dataProvider = DataProvider.getInstance();
            initializeUser(uid);
        }
    }

    private void initializeUser(String uid) {
        DataProviderInterface.SetUpState state = dataProvider.initialize(uid, this);

        switch (state) {
            case GET_USER_FAILED: {
                failStateAnim();
            }
            break;

            case UNREGISTERED: {
                loadRegistration();
            }
            break;

            case REGISTERED: {
                loadSetUp();
            }
            break;

            case SETUP_COMPLETED: {
                loadHome();
            }
            break;

            case GET_GROUP_FAILED: {
                //TODO: Implement get group failed
                loadHome();
            }
            break;

            case CONNECTION_FAILED: {
                failStateAnim();
            }
        }
    }

    private void initialize() {
        ImageStore.initialize(this);
        Configuration.initConfig(this);
        Money.initialize(Locale.getDefault());
    }

    private void loadHome() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadRegistration() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);

                if (joinGroupIntent != null) {
                    intent.setData(intent.getData());
                }

                startActivity(intent);
                finish();
            }
        });
    }

    private void loadSetUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, SetUpActivity.class);

                if (joinGroupIntent != null) {
                    intent.setData(joinGroupIntent.getData());
                }

                startActivity(intent);
                finish();
            }
        });
    }

    private void failStateAnim() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.animate().alpha(0f);
                progressBar.setVisibility(View.INVISIBLE);
                swipeToRefresh.setRefreshing(false);
                swipeToRefresh.setEnabled(true);
                errorContainer.setAlpha(0f);
                errorContainer.setVisibility(View.VISIBLE);
                errorContainer.animate().alpha(1f);
                errorMessage.setText(getString(R.string.splash_screen_connection_timeout) + "\n" + getString(
                        R.string.splash_screen_swipe_to_refresh));
            }
        });
    }

    private void retryAnim() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.animate().alpha(1f);
                swipeToRefresh.setEnabled(false);
                errorContainer.animate().alpha(0f);
                errorContainer.setVisibility(View.INVISIBLE);
            }
        });
    }
}
