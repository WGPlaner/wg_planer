package de.ameyering.wgplaner.wgplaner.section.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.BoughtItemsFragment;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.PinboardFragment;
import de.ameyering.wgplaner.wgplaner.section.home.fragment.ShoppingListFragment;
import de.ameyering.wgplaner.wgplaner.section.settings.GroupSettingsActivity;
import de.ameyering.wgplaner.wgplaner.section.settings.ProfileSettingsActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.SetUpActivity;
import de.ameyering.wgplaner.wgplaner.section.splashscreen.SplashScreenActivity;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;

public class HomeActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQ_CODE_PROFILE_SETTINGS = 0;
    private static final int REQ_CODE_GROUP_SETTINGS = 1;
    private static final String ACTIVE_ARGS = "ACTIVE_FRAGMENT";
    private static final int SHOPPING_LIST = 0;
    private static final int BOUGHT_ITEMS = 1;

    private DataProviderInterface dataProvider;

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private ShoppingListFragment shoppingListFragment;
    private BoughtItemsFragment boughtItemsFragment;

    private Fragment activeFragment = null;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (toolbar == null) {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        WGPlanerApplication application = (WGPlanerApplication) getApplicationContext();
        dataProvider = application.getDataProviderInterface();

        if (fab == null) {
            fab = findViewById(R.id.fab);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dataProvider.addOnDataChangeListener(type -> {
            if (type == DataProviderInterface.DataType.CURRENT_GROUP) {
                if (dataProvider.getCurrentGroupUID() == null) {
                    Intent intent = new Intent(HomeActivity.this, SetUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        if (boughtItemsFragment == null) {
            boughtItemsFragment = new BoughtItemsFragment();
        }

        boughtItemsFragment.setActionBar(getSupportActionBar());
        boughtItemsFragment.setTitle(getString(R.string.section_title_bought_items));
        boughtItemsFragment.setFloatingActionButton(fab);

        if (shoppingListFragment == null) {
            shoppingListFragment = new ShoppingListFragment();
        }

        shoppingListFragment.setActionBar(getSupportActionBar());
        shoppingListFragment.setTitle(getString(R.string.section_title_shopping_list));
        shoppingListFragment.setFloatingActionButton(fab);

        if (savedInstanceState == null) {
            loadActiveFragment();

        } else {
            int fragment = savedInstanceState.getInt(ACTIVE_ARGS);
            activeFragment = getSupportFragmentManager().findFragmentById(R.id.container);

            switch (fragment) {
                case SHOPPING_LIST:
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_shopping_list));
                    break;

                case BOUGHT_ITEMS:
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_bought_items));
                    break;

                default:
                    break;
            }
        }
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
            if (!activeFragment.equals(shoppingListFragment)) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                transaction.remove(activeFragment);
                transaction.add(R.id.container, shoppingListFragment);
                activeFragment = shoppingListFragment;
                transaction.commit();
            }

        } else if (id == R.id.nav_bought_items) {
            if (!activeFragment.equals(boughtItemsFragment)) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                transaction.remove(activeFragment);
                transaction.add(R.id.container, boughtItemsFragment);
                activeFragment = boughtItemsFragment;
                transaction.commit();
            }

        } else if (id == R.id.nav_rosters) {
            // TODO
        } else if (id == R.id.nav_calendar) {
            // TODO
        } else if (id == R.id.nav_pinboard) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            PinboardFragment pinboard = new PinboardFragment();
            pinboard.setActionBar(getSupportActionBar());
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

    private void loadActiveFragment() {
        if (activeFragment == null) {
            activeFragment = shoppingListFragment;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            Fragment actual = getSupportFragmentManager().findFragmentById(R.id.container);

            if (actual != null) {
                transaction.remove(actual);
            }

            transaction.add(R.id.container, activeFragment);
            transaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (activeFragment instanceof ShoppingListFragment) {
            outState.putInt(ACTIVE_ARGS, SHOPPING_LIST);

        } else if (activeFragment instanceof BoughtItemsFragment) {
            outState.putInt(ACTIVE_ARGS, BOUGHT_ITEMS);
        }
    }
}
