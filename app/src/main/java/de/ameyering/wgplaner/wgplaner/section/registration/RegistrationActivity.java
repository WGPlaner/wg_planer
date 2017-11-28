package de.ameyering.wgplaner.wgplaner.section.registration;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.registration.fragment.WelcomeFragment;

public class RegistrationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WelcomeFragment welcomeFragment;

    private boolean toastWasShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBackStack();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
        });

        if(savedInstanceState != null){
            welcomeFragment = (WelcomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "welcomeFragment");
        } else {
            loadWelcomeFragment();
            toolbar.setVisibility(View.GONE);
        }
    }

    private void loadWelcomeFragment() {
        welcomeFragment = new WelcomeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right);
        transaction.add(R.id.container_registration, welcomeFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        popBackStack();
    }

    private void popBackStack(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            if(!toastWasShown) {
                Toast toast = Toast.makeText(this, getString(R.string.back_press_leave_notification), Toast.LENGTH_LONG);
                toast.show();
                toastWasShown = true;
            } else {
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

        getSupportFragmentManager().putFragment(outState, "welcomeFragment", welcomeFragment);
    }
}
