package de.ameyering.wgplaner.test;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

import de.ameyering.wgplaner.test.mockclasses.TestServerCalls;
import de.ameyering.wgplaner.wgplaner.section.registration.RegistrationActivity;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;

public class RegistrationActivitySteps {
    private ActivityTestRule<RegistrationActivity> mActivityRule = new ActivityTestRule<>
    (RegistrationActivity.class);

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

    @Given("^The app is opened$")
    public void iOpenSplashScreen() {
        Activity activity = mActivityRule.launchActivity(null);
        initActivity(activity);
    }
}
