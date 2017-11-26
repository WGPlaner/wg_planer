package de.ameyering.wgplaner.wgplaner.section.setup;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.setup.fragment.DescriptionFragment;

public class SetUpActivity extends AppCompatActivity {
    private static final String DESCRIPTION_FRAGMENT_TAG = "DescriptionFragment";

    private Toolbar toolbar;

    private DescriptionFragment descriptionFragment = new DescriptionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        toolbar = findViewById(R.id.set_up_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().popBackStack();
            }
        });

        if(savedInstanceState != null){
            descriptionFragment = (DescriptionFragment) getSupportFragmentManager().getFragment(savedInstanceState, DESCRIPTION_FRAGMENT_TAG);
        } else {
            loadDescriptionFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,DESCRIPTION_FRAGMENT_TAG, descriptionFragment);
    }

    private void loadDescriptionFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right);
        transaction.add(R.id.container_set_up, descriptionFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
    }
}
