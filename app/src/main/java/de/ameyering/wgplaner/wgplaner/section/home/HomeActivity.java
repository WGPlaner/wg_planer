package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.PinboardFragment;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.ShoppingListFragment;
import de.ameyering.wgplaner.wgplaner.section.settings.GroupSettingsActivity;
import de.ameyering.wgplaner.wgplaner.section.settings.ProfileSettingsActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.SetUpActivity;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class HomeActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQ_CODE_PROFILE_SETTINGS = 0;
    private static final int REQ_CODE_GROUP_SETTINGS = 1;

    private DataProvider dataProvider;

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private ShoppingListFragment shoppingListFragment = new ShoppingListFragment();

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (dataProvider == null) {
            dataProvider = DataProvider.getInstance();
        }

        fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dataProvider.addOnDataChangeListener(new DataProvider.OnDataChangeListener() {
            @Override
            public void onDataChanged(DataProvider.DataType type) {
                if (type == DataProvider.DataType.CURRENT_GROUP) {
                    if (dataProvider.getCurrentGroupUID() == null) {
                        Intent intent = new Intent(HomeActivity.this, SetUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        loadShoppingList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return id == R.id.action_settings;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // TODO
        } else if (id == R.id.nav_shopping_list) {
            // TODO
        } else if (id == R.id.nav_accounting) {
            // TODO
        } else if (id == R.id.nav_rosters) {
            // TODO
        } else if (id == R.id.nav_calendar) {
            // TODO
        } else if (id == R.id.nav_pinboard) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            PinboardFragment pinboard = new PinboardFragment();
            pinboard.setToolbar(toolbar);
            pinboard.setTitle(getString(R.string.section_title_pinboard));
            pinboard.setFloatingActionButton(fab);

            transaction.replace(R.id.container, pinboard);
            transaction.commit();
        } else if (id == R.id.nav_general_settings) {

        } else if (id == R.id.nav_profile_settings) {
            startActivityForResult(new Intent(this, ProfileSettingsActivity.class), REQ_CODE_PROFILE_SETTINGS);

        } else if (id == R.id.nav_group_settings) {
            startActivityForResult(new Intent(this, GroupSettingsActivity.class), REQ_CODE_GROUP_SETTINGS);

        } else {
            return false;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadShoppingList() {
        navigationView.setCheckedItem(R.id.nav_shopping_list);

        shoppingListFragment.setToolbar(toolbar);
        shoppingListFragment.setTitle(getString(R.string.section_title_shopping_list));
        shoppingListFragment.setFloatingActionButton(fab);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction.replace(R.id.container, shoppingListFragment);
        transaction.commit();
    }
}
