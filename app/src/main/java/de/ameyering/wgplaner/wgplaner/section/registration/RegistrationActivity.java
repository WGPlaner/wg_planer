package de.ameyering.wgplaner.wgplaner.section.registration;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.WelcomeFragment;

public class RegistrationActivity extends AppCompatActivity {
    private static final String WELCOME_FRAGMENT_ARGS = "WelcomeFragment";
    private static final String PATH_PATTERN =
        "^(http|https)://api.wgplaner.ameyering.de/groups/join/[A-Z0-9]{12}";

    private AppBarLayout appbar;
    private Toolbar toolbar;
    private WelcomeFragment welcomeFragment;
    private ProgressBar progressBar;

    private boolean toastWasShown = false;
    public static Intent joinGroupIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));

        toolbar.setNavigationOnClickListener(view -> popBackStack());

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                getSupportActionBar()
                .hide();

            } else {
                getSupportActionBar().show();
            }
        });

        if (savedInstanceState != null) {
            welcomeFragment = (WelcomeFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                    WELCOME_FRAGMENT_ARGS);

        } else {
            if (getIntent().getData() != null) {
                Uri data = getIntent().getData();
                Pattern pattern = Pattern.compile(PATH_PATTERN);
                Matcher matcher = pattern.matcher(data.toString());

                if (matcher.matches()) {
                    joinGroupIntent = getIntent();
                }
            }

            loadWelcomeFragment();
            toolbar.setVisibility(View.GONE);
        }
    }

    private void loadWelcomeFragment() {
        welcomeFragment = new WelcomeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right,
            R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left,
            R.anim.anim_fragment_exit_to_right);
        transaction.add(R.id.container_registration, welcomeFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        popBackStack();
    }

    private void popBackStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (!toastWasShown) {
                Toast toast = Toast.makeText(this, getString(R.string.back_press_leave_notification),
                        Toast.LENGTH_LONG);
                toast.show();
                toastWasShown = true;

            } else {
                toastWasShown = false;
                finish();
            }

        } else {
            getSupportFragmentManager().popBackStack();
            toastWasShown = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, WELCOME_FRAGMENT_ARGS, welcomeFragment);
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
