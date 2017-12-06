package de.ameyering.wgplaner.test;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import de.ameyering.wgplaner.test.mockclasses.TestServerCalls;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class RegistrationActivitySteps {
    private ActivityTestRule<RegistrationActivity> mActivityRule = new ActivityTestRule<>
    (RegistrationActivity.class);

    public RegistrationActivitySteps() {
    }

    private void initActivity(Activity activity) {
        Context appContext = activity.getBaseContext();
        Money.initialize(Locale.getDefault());
        ImageStore.initialize(appContext);
        Configuration.initConfig(appContext);
        DataProvider.getInstance().setServerCallsInstance(TestServerCalls.getInstance());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (mActivityRule != null && mActivityRule.getActivity() != null) {
            mActivityRule.getActivity().finish();
        }

        ActivityFinisher.finishOpenActivities();
    }

    @Given("^The app is opened$")
    public void i_open_splash_screen() {
        Activity activity = mActivityRule.launchActivity(null);
        initActivity(activity);
    }

    /*
    @Then("^Toast with text \"([^\"]*)\" is displayed")
    public void toast_is_displayed(String text) throws Exception {
        onView(withText(text)).inRoot(withDecorView(not(is(mActivityRule.getActivity().
                        getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    */

}
