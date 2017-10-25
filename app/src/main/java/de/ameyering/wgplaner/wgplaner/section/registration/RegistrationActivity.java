package de.ameyering.wgplaner.wgplaner.section.registration;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Stack;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.NavigationFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.PickDisplayNameFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.StateEMailFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.UploadProfilePictureFragment;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.WelcomeFragment;

public class RegistrationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Stack<NavigationFragment> backStack = new Stack<>();
    private Stack<NavigationFragment> registrationFlow = new Stack<>();
    private NavigationFragment actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackNavigation();
            }
        });

        initializeRegistrationFlow();
    }

    private void initializeRegistrationFlow() {
        NavigationFragment.OnNavigationEventListener navigationEventListener = new
        NavigationFragment.OnNavigationEventListener() {
            @Override
            public void onForward() {
                if (registrationFlow.size() > 0) {
                    backStack.push(actualFragment);
                    actualFragment = registrationFlow.pop();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                    transaction.replace(R.id.container_registration, actualFragment);
                    transaction.commit();

                    if (backStack.size() > 0) {
                        toolbar.setVisibility(View.VISIBLE);
                    }

                } else {
                    //TODO: handle Registration end
                }
            }

            @Override
            public void onBack() {
                handleBackNavigation();
            }
        };

        StateEMailFragment eMailFragment = new StateEMailFragment();
        eMailFragment.setNavigationEventListener(navigationEventListener);

        registrationFlow.push(eMailFragment);

        UploadProfilePictureFragment profilePictureFragment = new UploadProfilePictureFragment();
        profilePictureFragment.setNavigationEventListener(navigationEventListener);

        registrationFlow.push(profilePictureFragment);

        PickDisplayNameFragment displayNameFragment = new PickDisplayNameFragment();
        displayNameFragment.setNavigationEventListener(navigationEventListener);

        registrationFlow.push(displayNameFragment);

        WelcomeFragment welcomeFragment = new WelcomeFragment();
        welcomeFragment.setNavigationEventListener(navigationEventListener);

        actualFragment = welcomeFragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container_registration, actualFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }

    private void handleBackNavigation() {
        if (backStack.size() == 0) {
            finish();

        } else if (backStack.size() <= 1) {
            toolbar.setVisibility(View.INVISIBLE);
        }

        if (backStack.size() > 0) {
            registrationFlow.push(actualFragment);
            actualFragment = backStack.pop();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            transaction.replace(R.id.container_registration, actualFragment);
            transaction.commit();

        } else {
            //TODO: handle empty BackStack
        }
    }
}
