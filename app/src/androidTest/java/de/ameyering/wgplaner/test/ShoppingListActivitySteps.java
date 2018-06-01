package de.ameyering.wgplaner.test;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import java.util.Locale;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import de.ameyering.wgplaner.test.mockclasses.TestServerCalls;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;

public class ShoppingListActivitySteps {
    private ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    public ShoppingListActivitySteps() {
    }

    private void initActivity(Activity activity) {
        Context appContext = activity.getBaseContext();
        ImageStore.initialize(appContext);
        Configuration.initConfig(appContext);
        DataProvider.getInstance().setServerCallsInstance(TestServerCalls.getInstance());
    }

    @Before
    public void setUp() throws Exception {
        // Nothing to do.
    }

    @After
    public void tearDown() throws Exception {
        if (mActivityRule != null && mActivityRule.getActivity() != null) {
            mActivityRule.getActivity().finish();
        }

        ActivityFinisher.finishOpenActivities();
    }

    @Given("^The shopping list view is open$")
    public void iOpenShoppingListView() {
        Activity activity = mActivityRule.launchActivity(null);
        initActivity(activity);
    }

}
