package de.ameyering.wgplaner.test;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;

public class ShoppingListActivitySteps {
    private ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    public ShoppingListActivitySteps() {
    }

    private void initActivity(Activity activity) {
        Context appContext = activity.getBaseContext();
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
