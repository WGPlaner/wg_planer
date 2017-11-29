package de.ameyering.wgplaner.test;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;

public class TestCucumberSteps extends ActivityInstrumentationTestCase2<HomeActivity> {
    private Activity activity;
    private Context instrumentationContext;
    private Context appContext;

    public TestCucumberSteps() {
        super(HomeActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Context instrumentationContext = getInstrumentation().getContext();
        Context appContext = getInstrumentation().getTargetContext();
        Activity activity = getActivity();
        assertNotNull(activity);

        // This is normally done by the SplashScreenActivity.
        // We have to manually initialize singletons.
        Money.initialize(Locale.getDefault());
        ImageStore.initialize(appContext);
        Configuration.initConfig(appContext);
    }

    @After
    public void after() {}

    @Given("^I do nothing$")
    public void i_press_buttonText() {
    }

    @Then("^Success$")
    public void success() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        ActivityFinisher.finishOpenActivities();
        getActivity().finish();
        super.tearDown();
    }
}
